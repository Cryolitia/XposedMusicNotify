package soptqs.medianotification.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PreferenceUtils {

    public static final String PREF_COLOR_METHOD = "colorMethod";
    public static final String PREF_CUSTOM_COLOR = "customColor";
    public static final String PREF_HIGH_CONTRAST_TEXT = "highContrastText";
    public static final String PREF_ALWAYS_DISMISSIBLE = "alwaysDismissible";
    public static final String PREF_INVERSE_TEXT_COLORS = "inverseTextColors";
    public static final int COLOR_METHOD_DOMINANT = 0;
    public static final int COLOR_METHOD_PRIMARY = 1;
    public static final int COLOR_METHOD_VIBRANT = 2;
    public static final int COLOR_METHOD_MUTED = 3;
    public static final int COLOR_METHOD_PHONOGRAPH = 4;
    public static final int COLOR_METHOD_DEFAULT = 5;
}
