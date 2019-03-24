package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.app.Notification.FLAG_ONLY_ALERT_ONCE;
import static android.app.Notification.PRIORITY_MAX;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class main implements IXposedHookLoadPackage {
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.tencent.qqmusiclocalplayer"))
			return;

        final Class clazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.business.k.s",lpparam.classLoader);
        final Class clazz2 = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.c.e",lpparam.classLoader);
        final Class clazz3 = XposedHelpers.findClass("com.tencent.a.d.t",lpparam.classLoader);
        findAndHookMethod(clazz, "b", Context.class, clazz2, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                int icon = 0x7f020099;
                Context context = (Context) param.args[0];
                Notification notification = new Notification.Builder(context)
                        .setContentTitle((CharSequence) XposedHelpers.callMethod(param.args[1],"getName"))
                        .setContentText((CharSequence) XposedHelpers.callMethod(param.args[1],"getSinger"))
                        .setLargeIcon((Bitmap)param.args[2])
                        .setSmallIcon(icon)
                        .setStyle(new Notification.MediaStyle()
                            //.setShowActionsInCompactView(0,1,2)
                        )
                        .build();
                //notification.flags |= FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR |  FLAG_ONLY_ALERT_ONCE | PRIORITY_MAX;
                //notification.flags &= Notification.PRIORITY_MIN;
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
