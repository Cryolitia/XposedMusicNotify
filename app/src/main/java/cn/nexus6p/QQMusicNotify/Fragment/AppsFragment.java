package cn.nexus6p.QQMusicNotify.Fragment;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import org.json.JSONArray;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;


public class AppsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.apps);
        //boolean PMEnabled = getSharedPreferenceOnUI(getActivity()).getBoolean("pm", true);
        try {
            JSONArray jsonArray = GeneralUtils.getSupportPackages(this.getContext());
            for (int i = 0; i < jsonArray.length(); i++) {
                String packageName = jsonArray.getJSONObject(i).getString("app");
                if (!(BuildConfig.DEBUG) && packageName.contains("com.kugou.android") && Build.VERSION.SDK_INT > 28)
                    continue;
                Preference preference = new Preference(getActivity(), null);
                //if (PMEnabled) {
                PackageInfo packageInfo;
                try {
                    packageInfo = getActivity().getPackageManager().getPackageInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    packageInfo = null;
                }
                if (packageInfo == null) continue;
                else
                    preference.setIcon(getActivity().getPackageManager().getApplicationIcon(packageName));
                //}
                preference.setTitle(jsonArray.getJSONObject(i).getString("name"));
                preference.setSummary(packageName);
                preference.setOnPreferenceClickListener(preference1 -> {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, DetailFragment.Companion.newInstance(packageName)).addToBackStack(DetailFragment.class.getSimpleName()).commit();
                    return true;
                });
                ((PreferenceCategory) findPreference("app")).addPreference(preference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findPreference("thirdPartySource").setOnPreferenceClickListener(preference -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ThirdPartySourceFragment()).addToBackStack(ThirdPartySourceFragment.class.getSimpleName()).commit();
            return true;
        });

    }

}
