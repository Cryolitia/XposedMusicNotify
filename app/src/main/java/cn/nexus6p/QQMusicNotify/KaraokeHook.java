package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
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
                Notification oldNotification = (Notification) param.getResult();
                RemoteViews remoteViews = oldNotification.bigContentView;
                View view = remoteViews.apply(context,null);
                int id1 = 0x7f0f12a8;
                TextView textview1 = view.findViewById(id1);
                int id2 = 0x7f0f12a9;
                TextView textview2 = view.findViewById(id2);
                int id3 = 0x7f0f12a7;
                ImageView imageView = view.findViewById(id3);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                int icon = 0x7f0209e3;
                //boolean statue = (!(Boolean)XposedHelpers.callStaticMethod(clazzO,"a")&&((Boolean)XposedHelpers.callStaticMethod(clazzO,"e")||(Boolean)XposedHelpers.callStaticMethod(clazzO,"b")));
                //Context serverContext = (Context) XposedHelpers.callStaticMethod(serviceHelperClazz,"a");
                MediaSession mediaSession = new MediaSession(context,"Karaoke media button");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(textview1.getText())
                        .setContentText(textview2.getText())
                        .setSmallIcon(icon)
                        //.setOngoing(statue)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        //.addAction(android.R.drawable.ic_media_previous,"后退", PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_PREVIOUS_TASKBAR"), 0))
                        //.addAction(statue?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,statue?"暂停":"播放",PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_TOGGLEPAUSE_TASKBAR"), 0))
                        //.addAction(android.R.drawable.ic_media_next, "前进",PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_NEXT_TASKBAR"), 0))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken()))
                                //.setCancelButtonIntent(PendingIntent.getBroadcast(context,0,new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_CLOSE_TASKBAR"),0))
                                //.setShowActionsInCompactView(0,1,2)
                        )
                        .setColorized(true)
                        .setLargeIcon(drawable.getBitmap());
                Notification notification = builder.build();
                //if (statue) notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
                //Intent intent = new Intent("android.intent.action.MAIN");
                //intent.addCategory("android.intent.category.LAUNCHER");
                //intent.setClassName(context,(String)XposedHelpers.callStaticMethod(clazz3,"d",context));
                //intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                //notification.contentIntent = PendingIntent.getActivity(context,0,intent,0);
                param.setResult(notification);
            }
        });
    }

}
