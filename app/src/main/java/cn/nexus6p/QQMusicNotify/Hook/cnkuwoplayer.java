package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicNotification;
import cn.nexus6p.QQMusicNotify.GeneralUtils;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static android.content.Context.NOTIFICATION_SERVICE;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

@Keep
public class cnkuwoplayer extends BasicNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        final Class notifyClazz = XposedHelpers.findClass("cn.kuwo.mod.notification.manager.KwNotificationManager",classLoader);
        findAndHookMethod(notifyClazz, "getPlayNotification", Bitmap.class, String.class,String.class,String.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    channelID = "music";
                    NotificationChannel channel = new NotificationChannel(channelID, "音乐通知",NotificationManager.IMPORTANCE_HIGH);
                    ((NotificationManager) GeneralUtils.getContext().getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                }
                basicParam.setIconID(0x7f020dfd);
                basicParam.setContext((Context) getObjectField(param.thisObject,"mContext"));
                basicParam.setBitmap((Bitmap) param.args[0]);
                if (mTOKEN==null) mTOKEN = new MediaSession(basicParam.getContext(),"MediaSessionHelper").getSessionToken();
                basicParam.setToken(mTOKEN);
                basicParam.setTitleString((CharSequence) param.args[1]);
                basicParam.setTextString((CharSequence) param.args[2]);
                preSongIntent = new Intent("kuwo.play.pre");
                playIntent = new Intent("kuwo.play.playing");
                nextSongIntent = new Intent("kuwo.play.next");
                contentIntent = new Intent(basicParam.getContext(),XposedHelpers.findClass("cn.kuwo.player.activities.EntryActivity",classLoader));
                contentIntent.setAction("android.intent.action.MAIN")
                    .addCategory("android.intent.category.LAUNCHER");
                intentRequestID = 1;
                hasExtraAction = false;
                extraActionIcon = 0x7f02040a;
                extraActionIntent = new Intent("kuwo.desklrc.enable");
                Object object = XposedHelpers.callStaticMethod(XposedHelpers.findClass("cn.kuwo.a.b.b",classLoader),"r");
                Object object2 = XposedHelpers.callMethod(object,"getStatus");
                Object object3 = XposedHelpers.getStaticObjectField(XposedHelpers.findClass("cn.kuwo.service.PlayProxy$Status",classLoader),"PLAYING");
                basicParam.setStatue(object2==object3);
                return build();
            }
        });
	}

}
