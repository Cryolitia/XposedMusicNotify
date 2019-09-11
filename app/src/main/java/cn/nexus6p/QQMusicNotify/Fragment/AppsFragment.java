package cn.nexus6p.QQMusicNotify.Fragment;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import org.json.JSONArray;

import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.setWorldReadable;


public class AppsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.apps);

        boolean PMEnabled = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("pm",true);
        try {
            JSONArray jsonArray = GeneralUtils.getSupportPackages();
            for (int i=0;i<jsonArray.length();i++) {
                String packageName = jsonArray.getJSONObject(i).getString("app");
                Preference switchPreference = new Preference(getActivity(),null);
                if (PMEnabled) {
                    PackageInfo packageInfo;
                    try {
                        packageInfo = getActivity().getPackageManager().getPackageInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                    }
                    if(packageInfo == null) continue;
                    else switchPreference.setIcon(getActivity().getPackageManager().getApplicationIcon(packageName));
                }
                //switchPreference.setChecked(true);
                switchPreference.setTitle(jsonArray.getJSONObject(i).getString("name"));
                switchPreference.setSummary(packageName);
                switchPreference.setKey(packageName+".enabled");
                ((PreferenceScreen) findPreference("app")).addPreference(switchPreference);
            }
            boolean showNetease = false;
            try {
                showNetease = !PMEnabled || getActivity().getPackageManager().getPackageInfo("com.netease.cloudmusic", 0)!=null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (showNetease) {
                Preference Preference = new Preference(getActivity(),null);
                Preference.setTitle("网易云音乐");
                try {
                    Preference.setIcon(getActivity().getPackageManager().getApplicationIcon("com.netease.cloudmusic"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Preference.setSummary("com.netease.cloudmusic");
                Preference.setOnPreferenceClickListener(preference1 -> {
                    Shell.Result result = Shell.su("am start -n com.netease.cloudmusic/com.netease.cloudmusic.activity.SettingActivity").exec();
                    if (result.isSuccess()) Toast.makeText(getContext(),"请在设置中通知栏样式设置为系统样式",Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getContext(),"请检查root权限："+result.getErr().toString(),Toast.LENGTH_LONG).show();
                    }
                    return true;
                });
                ((PreferenceScreen) findPreference("app")).addPreference(Preference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setWorldReadable(getActivity());

    }

}
