package cn.nexus6p.QQMusicNotify.Utils;

import android.content.SharedPreferences;

import cn.nexus6p.QQMusicNotify.SharedPreferences.XSharedPreference;

final public class PreferenceUtil {

    public static boolean isGooglePlay = false;

    public static SharedPreferences getPreference() {
        try {
            if (GeneralUtils.getModuleContext().getApplicationInfo().targetSdkVersion < 24)
                return XSharedPreference.Companion.get();
            else return XSharedPreference.Companion.get();
        } catch (Exception e) {
            //return JSONPreference.Companion.get("setting");
            return XSharedPreference.Companion.get();
        }
    }

    @Deprecated
    public static SharedPreferences getJSONPreference(String packageName) {
        ///return JSONPreference.Companion.get(packageName);
        return null;
    }

}
