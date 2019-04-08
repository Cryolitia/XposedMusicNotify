package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

class QingtingHook extends BasicNotification {

    private static MediaSession.Token mTOKEN;

    QingtingHook(ClassLoader mClassLoader) {
        super(mClassLoader);
    }

    void init() {
        final Class notifyClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.business.k.s",classLoader);
        final Class infoClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.c.e",classLoader);
        final Class clazz3 = XposedHelpers.findClass("com.tencent.a.d.t",classLoader);
        final Class clazzO = XposedHelpers.findClass("com.tencent.qqmusicsdk.a.o",classLoader);
        final Class serviceHelperClazz = XposedHelpers.findClass("com.tencent.qqmusicsdk.service.l",classLoader);
        findAndHookMethod(notifyClazz, "b", Context.class, infoClazz, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                iconID = 0x7f020099;
                context = (Context) param.args[0];
                bitmap = (Bitmap) param.args[2];
                statue = (!(Boolean)XposedHelpers.callStaticMethod(clazzO,"a")&&((Boolean)XposedHelpers.callStaticMethod(clazzO,"e")||(Boolean)XposedHelpers.callStaticMethod(clazzO,"b")));
                if (mTOKEN==null) mTOKEN = new MediaSession(context,"mbr").getSessionToken();
                token = mTOKEN;
                titleString = (CharSequence) XposedHelpers.callMethod(param.args[1],"getName");
                textString = (CharSequence) XposedHelpers.callMethod(param.args[1],"getSinger");
                preSongIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_PREVIOUS_TASKBAR");
                playIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_TOGGLEPAUSE_TASKBAR");
                nextSongIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_NEXT_TASKBAR");
                contentIntent = new Intent("android.intent.action.MAIN");
                contentIntent.addCategory("android.intent.category.LAUNCHER").setClassName(context,(String)XposedHelpers.callStaticMethod(clazz3,"d",context));
                return build();
            }
        });
	}

}
