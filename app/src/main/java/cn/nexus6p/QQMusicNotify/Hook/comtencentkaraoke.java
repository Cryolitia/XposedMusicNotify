package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Keep;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.nexus6p.QQMusicNotify.Base.BasicViewNotification;
import cn.nexus6p.QQMusicNotify.GeneralUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comtencentkaraoke extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        initWithJSON("com.tencent.karaoke.json");
        if (className.equals("")) className = "com.tencent.karaoke.common.media.n";
        if (methodName.equals("")) methodName = "a";
        if (titleID==0) titleID = 0x7f0914e1;
        if (textID==0) textID = 0x7f0914df;
        if (bitmapID==0) bitmapID = 0x7f0914dc;
        if (iconID==0) iconID = 0x7f080b4c;
        Class playInfoClazz = XposedHelpers.findClass("com.tencent.karaoke.common.media.player.PlaySongInfo", classLoader);
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, Context.class, playInfoClazz, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                context = (Context) param.args[0];
                Parcelable playSongInfo = (Parcelable) param.args[1];
                oldNotification = (Notification) param.getResult();
                statue = ((int) param.args[2]) == 8;
                if (mTOKEN == null)
                    mTOKEN = new MediaSession(context, "Karaoke media button").getSessionToken();
                token = mTOKEN;
                preSongIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass("com.tencent.karaoke.common.f$a",classLoader),"d")).putExtra("play_current_song", playSongInfo);
                playIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass("com.tencent.karaoke.common.f$a",classLoader),"a")).putExtra("play_current_song", playSongInfo);
                nextSongIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass("com.tencent.karaoke.common.f$a",classLoader),"e")).putExtra("play_current_song", playSongInfo);
                contentIntent = new Intent("com.tencent.karaoke.action.PLAYER");
                contentIntent.setData(Uri.parse("qmkege://"))
                        .putExtra("action", "notification_player")
                        .putExtra("from","from_notification")
                        .setClassName(context, XposedHelpers.findClass("com.tencent.karaoke.widget.intent.IntentHandleActivity", classLoader).getCanonicalName())
                        .addCategory("android.intent.category.DEFAULT");
                XposedBridge.log("加载方法完毕");
                param.setResult(viewBuild());
            }
        });
    }

}
