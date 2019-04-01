package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

class KaraokeHook {

    private ClassLoader classLoader;

    KaraokeHook (ClassLoader mclassLoader) {
        classLoader = mclassLoader;
    }

    public void init () {
        Class playInfoClazz = XposedHelpers.findClass("com.tencent.karaoke.common.media.player.PlaySongInfo",classLoader);
        XposedHelpers.findAndHookMethod("com.tencent.karaoke.common.media.t", classLoader, "a", Context.class, playInfoClazz, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
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
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int icon = 0x7f0209e3;
                boolean statue = ((int)param.args[2])==8;
                MediaSession mediaSession = new MediaSession(context,"Karaoke media button");
                mediaSession.setMetadata(new MediaMetadata.Builder()
                            .putBitmap(MediaMetadata.METADATA_KEY_ART,bitmap)
                            .build());
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(textview1.getText())
                        .setContentText(textview2.getText())
                        .setSmallIcon(icon)
                        .setOngoing(statue)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .addAction(android.R.drawable.ic_media_previous,"后退", PendingIntent.getBroadcast(context, 0, new Intent("Notification_action_play_pre_song").putExtra("play_current_song",playSongInfo), PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(statue?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,statue?"暂停":"播放",PendingIntent.getBroadcast(context, 0, new Intent("Notification_action_play_pause").putExtra("play_current_song",playSongInfo), PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(android.R.drawable.ic_media_next, "前进",PendingIntent.getBroadcast(context, 0, new Intent("Notification_action_play_next_song").putExtra("play_current_song",playSongInfo), PendingIntent.FLAG_UPDATE_CURRENT))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken()))
                                .setCancelButtonIntent(PendingIntent.getBroadcast(context, 0, new Intent("Notification_action_close").putExtra("play_current_song",playSongInfo), PendingIntent.FLAG_UPDATE_CURRENT))
                                .setShowActionsInCompactView(0,1,2)
                        )
                        .setColorized(true)
                        .setLargeIcon(bitmap);
                Notification notification = builder.build();
                if (statue) notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
                Intent intent = new Intent("com.tencent.karaoke.action.PLAYER");
                intent.setData(Uri.parse("qmkege://"))
                    .putExtra("action","notification_player")
                    .setClassName(context,XposedHelpers.findClass("com.tencent.karaoke.widget.intent.IntentHandleActivity",classLoader).getCanonicalName())
                    .addCategory("android.intent.category.DEFAULT")
                    .setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                notification.contentIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                param.setResult(notification);
            }
        });
    }

}
