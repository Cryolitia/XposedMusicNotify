package soptqs.medianotification.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PreferenceUtils {

    public static final String PREF_COLOR_METHOD = "colorMethod";
    public static final String PREF_CUSTOM_COLOR = "customColor";
    public static final String PREF_HIGH_CONTRAST_TEXT = "highContrastText";
    public static final String PREF_USE_RECEIVER = "useReceiver";
    public static final String PREF_USE_LASTFM = "useLastFm";
    public static final String PREF_USE_TENCENTMUSIC = "useTencentmusic";
    public static final String PREF_MEDIA_CONTROLS_METHOD = "mediaControlsMethod";
    public static final String PREF_ALWAYS_DISMISSIBLE = "alwaysDismissible";
    public static final String PREF_FC_ON_DISMISS = "fcOnDismiss";
    public static final String PREF_DEFAULT_MUSIC_PLAYER = "defaultMusicPlayer";
    public static final String PREF_SHOW_ALBUM_ART = "showAlbumArt";
    public static final String PREF_INVERSE_TEXT_COLORS = "inverseTextColors";
    public static final String PREF_CONTRIBUTORS = "contributors%s%d";
    public static final String PREF_CONTRIBUTOR_LENGTH = "contributorLength";
    public static final String PREF_CONTRIBUTOR_NAME = "Name";
    public static final String PREF_CONTRIBUTOR_IMAGE = "Image";
    public static final String PREF_CONTRIBUTOR_URL = "Url";
    public static final String PREF_CONTRIBUTOR_VERSION = "version";
    public static final String PREF_CANCEL_ORIGINAL_NOTIFICATION = "cancelOriginalNotification";
    public static final String PREF_FORCE_MD_ICONS = "forceMdIcons";
    public static final String PREF_PLAYER_ENABLED = "playerEnabled%s";
    public static final String PREF_TUTORIAL = "tutorial";
    public static final String PREF_TUTORIAL_PLAYERS = "tutorialPlayers";
    public static final String PREF_ENABLE_BLUR = "enableBlur";
    public static final String PREF_ENABLE_RENDERSCRIPT = "enableRenderScript";


    public static final String PREF_NOTIFICATION_STYLE1 = "notificationstyle1";
    public static final String PREF_NOTIFICATION_STYLE2 = "notificationstyle2";

    public static final int COLOR_METHOD_DOMINANT = 0;
    public static final int COLOR_METHOD_PRIMARY = 1;
    public static final int COLOR_METHOD_VIBRANT = 2;
    public static final int COLOR_METHOD_MUTED = 3;
    public static final int COLOR_METHOD_PHONOGRAPH = 4;
    public static final int COLOR_METHOD_DEFAULT = 5;

    public static final int CONTROLS_METHOD_NONE = 0;
    public static final int CONTROLS_METHOD_AUDIO_MANAGER = 1;
    public static final int CONTROLS_METHOD_REFLECTION = 2;
    public static final int CONTROLS_METHOD_BROADCAST = 3;
    public static final int CONTROLS_METHOD_BROADCAST_STRING = 4;
    public static final int CONTROLS_METHOD_BROADCAST_PARCELABLE = 5;
    public static final int CONTROLS_METHOD_SHELL_ROOT = 6;

}
