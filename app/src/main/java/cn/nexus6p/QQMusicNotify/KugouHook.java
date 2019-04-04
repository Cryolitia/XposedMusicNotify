package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

class KugouHook extends BasicNotification {

    KugouHook(ClassLoader mClassLoader) {
        super(mClassLoader);
    }

    void init() {
        final Class notifyClazz = XposedHelpers.findClass("cn.kuwo.mod.notification.manager.KwNotificationManager",classLoader);
        findAndHookMethod(notifyClazz, "getPlayNotification", Bitmap.class, String.class,String.class,String.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                iconID = 0x7f020dca;
                context = (Context) getObjectField(param.thisObject,"mContext");
                bitmap = (Bitmap) param.args[0];
                mediaSessionTag = "MediaSessionHelper";
                titleString = (CharSequence) param.args[1];
                textString = (CharSequence) param.args[2];
                preSongIntent = new Intent("kuwo.play.pre");
                playIntent = new Intent("kuwo.play.playing");
                nextSongIntent = new Intent("kuwo.play.next");
                contentIntent = new Intent(context,XposedHelpers.findClass("cn.kuwo.player.activities.EntryActivity",classLoader));
                contentIntent.setAction("android.intent.action.MAIN")
                    .addCategory("android.intent.category.LAUNCHER");
                intentRequestID = 1;
                hasExtraAction = true;
                extraActionIcon = 0x7f02040a;
                extraActionIntent = new Intent("kuwo.desklrc.enable");
                return build();
            }
        });
	}

}
