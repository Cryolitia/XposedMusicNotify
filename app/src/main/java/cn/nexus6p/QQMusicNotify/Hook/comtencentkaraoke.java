package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.BasicViewNotification;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comtencentkaraoke extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        Class playInfoClazz = XposedHelpers.findClass("com.tencent.karaoke.common.media.player.PlaySongInfo",classLoader);
        XposedHelpers.findAndHookMethod("com.tencent.karaoke.common.media.n", classLoader, "a", Context.class, playInfoClazz, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                context = (Context) param.args[0];
                Parcelable playSongInfo = (Parcelable) param.args[1];
                oldNotification = (Notification) param.getResult();
                titleID = 0x7f0914e1;
                textID = 0x7f0914df;
                bitmapID = 0x7f0914dc;
                iconID = 0x7f080b4c;
                statue = ((int)param.args[2])==8;
                if (mTOKEN==null) mTOKEN = new MediaSession(context,"Karaoke media button").getSessionToken();
                token = mTOKEN;
                preSongIntent = new Intent("Notification_action_play_pre_song").putExtra("play_current_song",playSongInfo);
                playIntent = new Intent("Notification_action_play_pause").putExtra("play_current_song",playSongInfo);
                nextSongIntent = new Intent("Notification_action_play_next_song").putExtra("play_current_song",playSongInfo);
                contentIntent = new Intent("com.tencent.karaoke.action.PLAYER");
                contentIntent.setData(Uri.parse("qmkege://"))
                    .putExtra("action","notification_player")
                    .setClassName(context,XposedHelpers.findClass("com.tencent.karaoke.widget.intent.IntentHandleActivity",classLoader).getCanonicalName())
                    .addCategory("android.intent.category.DEFAULT");
                param.setResult(viewBuild());
            }
        });
    }

}
