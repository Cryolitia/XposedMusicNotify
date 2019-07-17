package cn.nexus6p.QQMusicNotify;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XSharedPreferences;

final public class PreferenceUtil {

    private static WeakReference<XSharedPreferences> xSharedPreferencesWeakReferences = new WeakReference<>(null);

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
