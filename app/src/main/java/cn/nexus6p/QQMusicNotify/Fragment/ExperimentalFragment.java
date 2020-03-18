package cn.nexus6p.QQMusicNotify.Fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.MainActivity;
import cn.nexus6p.QQMusicNotify.R;

public class ExperimentalFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.experimental);
        findPreference("fakeTaichi").setOnPreferenceChangeListener((preference, newValue) -> {
            ((MainActivity) getActivity()).reload();
            return true;
        });
    }
}
