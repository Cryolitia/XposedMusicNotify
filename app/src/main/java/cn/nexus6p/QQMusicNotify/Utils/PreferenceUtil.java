package cn.nexus6p.QQMusicNotify.Utils;

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import de.robv.android.xposed.XSharedPreferences;

final public class PreferenceUtil {

    private static WeakReference<XSharedPreferences> xSharedPreferencesWeakReferences = new WeakReference<>(null);
    private JSONObject jsonObject;

    private PreferenceUtil(){}

    public PreferenceUtil(String packageName) {
        if (packageName==null||packageName.equals("")) throw new RuntimeException("Param Package Name should noe be NULL!");
        try {
            jsonObject = new JSONObject(GeneralUtils.getAssetsString(packageName+".json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStringFromJson(String key){
        return getStringFromJson(key,"");
    }

    public String getStringFromJson(String key,String defaultValue) {
        if (jsonObject==null) throw new RuntimeException("JsonObject should not be NULL!");
        String value;
        try {
            value = jsonObject.optString(key);
        } catch (Exception e) {
            e.printStackTrace();
            value = defaultValue;
        }
        return value;
    }

    public int getIntFromJson(String key) {
        return getIntFromJson(key,-1);
    }

    public int getIntFromJson(String key,int defaultValue) {
        if (jsonObject==null) throw new RuntimeException("JsonObject should not be NULL!");
        int value;
        try {
            value = Integer.parseInt(jsonObject.optString(key),16);
        } catch (Exception e) {
            e.printStackTrace();
            value = defaultValue;
        }
        return value;
    }

    public static XSharedPreferences getXSharedPreference () {
        XSharedPreferences preferences = xSharedPreferencesWeakReferences.get();
        if (preferences==null) {
            preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
            preferences.makeWorldReadable();
            preferences.reload();
            xSharedPreferencesWeakReferences = new WeakReference<>(preferences);
        } else  {
            preferences.reload();
        }
        return preferences;
    }



}
