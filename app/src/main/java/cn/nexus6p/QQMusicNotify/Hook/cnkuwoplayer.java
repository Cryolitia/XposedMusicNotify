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
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
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
        PreferenceUtil preferenceUtil = new PreferenceUtil("cn.kuwo.player");
        String className = preferenceUtil.getStringFromJson("class");
        String methodName = preferenceUtil.getStringFromJson("method");
        int iconID = preferenceUtil.getIntFromJson("iconID");
        String contextField = preferenceUtil.getStringFromJson("contextField");
        String preSongIntentName = preferenceUtil.getStringFromJson("preSongIntent");
        String playSongIntentName = preferenceUtil.getStringFromJson("playSongIntent");
        String nextSongIntentName = preferenceUtil.getStringFromJson("nextSongIntent");
        String IntentHandleActivity = preferenceUtil.getStringFromJson("IntentHandleActivity");
        String getStatusClass = preferenceUtil.getStringFromJson("getStatusClass");
        String getStatusMethod = preferenceUtil.getStringFromJson("getStatusMethod");
        String getStatusMethod2 = preferenceUtil.getStringFromJson("getStatusMethod2");
        String playProxyStatusClass = preferenceUtil.getStringFromJson("playProxyStatusClass");
        String playProxyStatusField = preferenceUtil.getStringFromJson("playProxyStatusField");
        extraActionIcon = preferenceUtil.getIntFromJson("extraActionIcon");
        String extraActionIntentName = preferenceUtil.getStringFromJson("extraActionIntent");

        final Class notifyClazz = XposedHelpers.findClass(className,classLoader);
        findAndHookMethod(notifyClazz, methodName, Bitmap.class, String.class,String.class,String.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    channelID = "music";
                    NotificationChannel channel = new NotificationChannel(channelID, "音乐通知",NotificationManager.IMPORTANCE_HIGH);
                    ((NotificationManager) GeneralUtils.getContext().getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                }
                basicParam.setIconID(iconID);
                basicParam.setContext((Context) getObjectField(param.thisObject,contextField));
                basicParam.setBitmap((Bitmap) param.args[0]);
                if (mTOKEN==null) mTOKEN = new MediaSession(basicParam.getContext(),"MediaSessionHelper").getSessionToken();
                basicParam.setToken(mTOKEN);
                basicParam.setTitleString((CharSequence) param.args[1]);
                basicParam.setTextString((CharSequence) param.args[2]);
                preSongIntent = new Intent(preSongIntentName);
                playIntent = new Intent(playSongIntentName);
                nextSongIntent = new Intent(nextSongIntentName);
                contentIntent = new Intent(basicParam.getContext(),XposedHelpers.findClass(IntentHandleActivity,classLoader));
                contentIntent.setAction("android.intent.action.MAIN")
                    .addCategory("android.intent.category.LAUNCHER");
                intentRequestID = 1;
                hasExtraAction = true;
                extraActionIntent = new Intent(extraActionIntentName);
                Object object = XposedHelpers.callStaticMethod(XposedHelpers.findClass(getStatusClass,classLoader),getStatusMethod);
                Object object2 = XposedHelpers.callMethod(object,getStatusMethod2);
                Object object3 = XposedHelpers.getStaticObjectField(XposedHelpers.findClass(playProxyStatusClass,classLoader),playProxyStatusField);
                basicParam.setStatue(object2.equals(object3));
                return build();
            }
        });
	}

}
