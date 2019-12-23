package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Keep;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.nexus6p.QQMusicNotify.Base.BasicViewNotification;
import cn.nexus6p.QQMusicNotify.SharedPreferences.JSONPreference;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comtencentkaraoke extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        SharedPreferences preference = PreferenceUtil.getJSONPreference("com.tencent.karaoke");
        className = preference.getString("class","");
        methodName = preference.getString("method","");
        titleID = preference.getInt("titleID",-1);
        textID = preference.getInt("textID",-1);
        bitmapID = preference.getInt("bitmapID",-1);
        basicParam.setIconID(preference.getInt("iconID",-1));
        String intentClass = preference.getString("intentClass","");
        String preSongField = preference.getString("preSongField","");
        String playSongField = preference.getString("playSongField","");
        String nextSongField = preference.getString("nextSongField","");
        String deleteField = preference.getString("deleteField","");
        String IntentHandleActivity = preference.getString("IntentHandleActivity","");

        JSONArray params = ((JSONPreference) preference).jsonObject.optJSONArray("params");
        Object[] objects = new Object[params.length()+1];
        try {
            for (int i = 0; i < params.length(); i++) {
                objects[i] = (params.get(i).toString().equals("int")) ? int.class : XposedHelpers.findClass(params.get(i).toString(), classLoader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objects[params.length()] = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                basicParam.setContext((Context) param.args[0]);
                Parcelable playSongInfo = (Parcelable) param.args[1];
                oldNotification = (Notification) param.getResult();
                basicParam.setStatue(((int) param.args[2]) == 8);
                if (mTOKEN == null)
                    mTOKEN = new MediaSession(basicParam.getContext(), "Karaoke media button").getSessionToken();
                basicParam.setToken(mTOKEN);
                preSongIntent = new Intent((String) XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass, classLoader), preSongField)).putExtra("play_current_song", playSongInfo);
                playIntent = new Intent((String) XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass, classLoader), playSongField)).putExtra("play_current_song", playSongInfo);
                nextSongIntent = new Intent((String) XposedHelpers.getStaticObjectField(XposedHelpers.findClass(intentClass, classLoader), nextSongField)).putExtra("play_current_song", playSongInfo);
                contentIntent = new Intent("com.tencent.karaoke.action.PLAYER");
                contentIntent.setData(Uri.parse("qmkege://"))
                        .putExtra("action", "notification_player")
                        .putExtra("from", "from_notification")
                        .setClassName(basicParam.getContext(), XposedHelpers.findClass(intentClass, classLoader).getCanonicalName())
                        .addCategory("android.intent.category.DEFAULT");
                XposedBridge.log("加载方法完毕");
                param.setResult(viewBuild());
            }
        };

        XposedHelpers.findAndHookMethod(className, classLoader, methodName, objects);

    }

}
