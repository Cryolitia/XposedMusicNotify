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
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comtencentkaraoke extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        PreferenceUtil preferenceUtil = new PreferenceUtil("com.tencent.karaoke");
        className = preferenceUtil.getStringFromJson("class");
        methodName = preferenceUtil.getStringFromJson("method");
        titleID = preferenceUtil.getIntFromJson("titleID");
        textID = preferenceUtil.getIntFromJson("textID");
        bitmapID = preferenceUtil.getIntFromJson("bitmapID");
        basicParam.setIconID(preferenceUtil.getIntFromJson("iconID"));
        String playSongInfoClass = preferenceUtil.getStringFromJson("playSongInfoClass");
        String intentClass = preferenceUtil.getStringFromJson("intentClass");
        String preSongField = preferenceUtil.getStringFromJson("preSongField");
        String playSongField = preferenceUtil.getStringFromJson("playSongField");
        String nextSongField = preferenceUtil.getStringFromJson("nextSongField");
        String deleteField = preferenceUtil.getStringFromJson("deleteField");
        String IntentHandleActivity = preferenceUtil.getStringFromJson("IntentHandleActivity");

        Class playInfoClazz = XposedHelpers.findClass(playSongInfoClass, classLoader);
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, Context.class, playInfoClazz, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                basicParam.setContext((Context) param.args[0]);
                Parcelable playSongInfo = (Parcelable) param.args[1];
                oldNotification = (Notification) param.getResult();
                basicParam.setStatue(((int) param.args[2]) == 8);
                if (mTOKEN == null)
                    mTOKEN = new MediaSession(basicParam.getContext(), "Karaoke media button").getSessionToken();
                basicParam.setToken(mTOKEN);
                preSongIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass,classLoader),preSongField)).putExtra("play_current_song", playSongInfo);
                playIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass,classLoader),playSongField)).putExtra("play_current_song", playSongInfo);
                nextSongIntent = new Intent((String)XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass,classLoader),nextSongField)).putExtra("play_current_song", playSongInfo);
                contentIntent = new Intent("com.tencent.karaoke.action.PLAYER");
                contentIntent.setData(Uri.parse("qmkege://"))
                        .putExtra("action", "notification_player")
                        .putExtra("from","from_notification")
                        .setClassName(basicParam.getContext(), XposedHelpers.findClass(intentClass, classLoader).getCanonicalName())
                        .addCategory("android.intent.category.DEFAULT");
                XposedBridge.log("加载方法完毕");
                param.setResult(viewBuild());
            }
        });
    }

}
