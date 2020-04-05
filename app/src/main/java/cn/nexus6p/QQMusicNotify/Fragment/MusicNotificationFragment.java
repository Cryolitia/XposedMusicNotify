package cn.nexus6p.QQMusicNotify.Fragment;

import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.MainActivity;
import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.HookStatue;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.deviceContextPreferenceChangeListener;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.jumpToAlipay;

@Keep
public class MusicNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.music_notification);
        //GeneralUtils.bindPreference(this,"styleModify","always_show");
        GeneralUtils.jumpToLink(this, "github2", "https://github.com/Qiwu2542284182/MusicNotification", false);
        if (HookStatue.isExpModuleActive(getActivity()) > 0) {
            findPreference("miuiModify").setSummary("仅太极·阳有效，请将SystemUI加入太极");
        }

        findPreference("styleModify").setOnPreferenceChangeListener((preference, newValue) -> {
            ((MainActivity) getActivity()).reload();
            return true;
        });
        /*findPreference("always_show").setOnPreferenceChangeListener((preference, newValue) -> {
            preferenceChangeListener(preference,newValue);
            return true;
        });*/
        findPreference("miuiModify").setOnPreferenceChangeListener((preference, newValue) -> {
            deviceContextPreferenceChangeListener(preference, newValue, getActivity());
            return true;
        });
        findPreference("miuiForceExpand").setOnPreferenceChangeListener((preference, newValue) -> {
            deviceContextPreferenceChangeListener(preference, newValue, getActivity());
            return true;
        });

        jumpToAlipay(this, "alipay", "fkx00337aktbgg6hgq64ae2?t=1542355035868");

        if (PreferenceUtil.isGooglePlay) findPreference("alipay").setVisible(false);

    }
}
