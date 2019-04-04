package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

class KaraokeHook extends BasicNotification {

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
                Notification oldNotification = (Notification) param.getResult();
                RemoteViews remoteViews = oldNotification.bigContentView;
                View view = remoteViews.apply(context,null);
                int id1 = 0x7f0f12a8;
                TextView textview1 = view.findViewById(id1);
                int id2 = 0x7f0f12a9;
                TextView textview2 = view.findViewById(id2);
                int id3 = 0x7f0f12a7;
                ImageView imageView = view.findViewById(id3);
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                iconID = 0x7f0209e3;
                statue = ((int)param.args[2])==8;
                mediaSessionTag = "Karaoke media button";
                titleString = textview1.getText();
                textString = textview2.getText();
                preSongIntent = new Intent("Notification_action_play_pre_song").putExtra("play_current_song",playSongInfo);
                playIntent = new Intent("Notification_action_play_pause").putExtra("play_current_song",playSongInfo);
                nextSongIntent = new Intent("Notification_action_play_next_song").putExtra("play_current_song",playSongInfo);
                contentIntent = new Intent("com.tencent.karaoke.action.PLAYER");
                contentIntent.setData(Uri.parse("qmkege://"))
                    .putExtra("action","notification_player")
                    .setClassName(context,XposedHelpers.findClass("com.tencent.karaoke.widget.intent.IntentHandleActivity",classLoader).getCanonicalName())
                    .addCategory("android.intent.category.DEFAULT");
                param.setResult(build());
            }
        });
    }

}
