package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Parcelable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

class KaraokeHook extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    KaraokeHook(ClassLoader mClassLoader) {
        super(mClassLoader);
    }

    void init() {
        Class playInfoClazz = XposedHelpers.findClass("com.tencent.karaoke.common.media.player.PlaySongInfo",classLoader);
        XposedHelpers.findAndHookMethod("com.tencent.karaoke.common.media.t", classLoader, "a", Context.class, playInfoClazz, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                context = (Context) param.args[0];
                Parcelable playSongInfo = (Parcelable) param.args[1];
                oldNotification = (Notification) param.getResult();
                titleID = 0x7f0f12a8;
                textID = 0x7f0f12a9;
                bitmapID = 0x7f0f12a7;
                iconID = 0x7f0209e3;
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
