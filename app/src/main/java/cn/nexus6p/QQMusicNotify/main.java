package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.graphics.Palette;
import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class main implements IXposedHookLoadPackage {
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

	    findAndHookMethod(Notification.class, "hasMediaSession", new XC_MethodReplacement() {
            @Override
            protected Boolean replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("hook Notification successfully");
                return true;
            }
        });

		if (!lpparam.packageName.equals("com.tencent.qqmusiclocalplayer"))
			return;
        final Class notifyClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.business.k.s",lpparam.classLoader);
        final Class infoClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.c.e",lpparam.classLoader);
        final Class clazz3 = XposedHelpers.findClass("com.tencent.a.d.t",lpparam.classLoader);
        final Class clazzO = XposedHelpers.findClass("com.tencent.qqmusicsdk.a.o",lpparam.classLoader);
        findAndHookMethod(notifyClazz, "b", Context.class, infoClazz, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                int icon = 0x7f020099;
                Context context = (Context) param.args[0];
                Bitmap bitmap = (Bitmap) param.args[2];
                Boolean statue = (!(Boolean)XposedHelpers.callStaticMethod(clazzO,"a")&&((Boolean)XposedHelpers.callStaticMethod(clazzO,"e")||(Boolean)XposedHelpers.callStaticMethod(clazzO,"b")));
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle((CharSequence) XposedHelpers.callMethod(param.args[1],"getName"))
                        .setContentText((CharSequence) XposedHelpers.callMethod(param.args[1],"getSinger"))
                        .setSmallIcon(icon)
                        .setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .addAction(android.R.drawable.ic_media_previous,"后退",PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_PREVIOUS_TASKBAR"), 0))
                        .addAction(statue?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,"播放",PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_TOGGLEPAUSE_TASKBAR"), 0))
                        .addAction(android.R.drawable.ic_media_next, "前进",PendingIntent.getBroadcast(context, 0, new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_NEXT_TASKBAR"), 0))
                        .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setCancelButtonIntent(PendingIntent.getBroadcast(context,0,new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_CLOSE_TASKBAR"),0))
                                .setShowActionsInCompactView(0,1,2)
                                )
                        .setColorized(true);
                int color = Color.BLACK;
                if (bitmap!=null) {
                    builder.setLargeIcon(bitmap);
                    Palette palette = Palette.from(bitmap).generate();
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch!=null) {
                        color = swatch.getRgb();
                    }
                }
                builder.setColor(color);
                Notification notification = builder.build();
                notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setClassName(context,(String)XposedHelpers.callStaticMethod(clazz3,"d",context));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                notification.contentIntent = PendingIntent.getActivity(context,0,intent,0);
                return notification;
            }
        });

	}
}
