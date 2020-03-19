package cn.nexus6p.QQMusicNotify.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import cn.nexus6p.QQMusicNotify.ContentProvider;
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference;

final public class PreferenceUtil {

    public static boolean isGooglePlay = false;

    public static SharedPreferences getPreference(Context context) {
        return new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_PREFERENCE, null, context);
    }

    public static SharedPreferences getJSONPreference(String packageName, Context context) {
        return new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_JSON, packageName, context);
    }

    public static JSONArray getSupportPackages(Context context) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(((ContentProviderPreference) PreferenceUtil.getJSONPreference("packages", context)).getOriginalJsonString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

}
