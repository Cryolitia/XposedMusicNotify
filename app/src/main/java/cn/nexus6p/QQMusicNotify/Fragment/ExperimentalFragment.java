package cn.nexus6p.QQMusicNotify.Fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.R;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.setWorldReadable;

public class ExperimentalFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.experimental);
        setWorldReadable(getActivity());
    }
}
