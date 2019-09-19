package cn.nexus6p.QQMusicNotify.Fragment;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.os.Environment;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import org.json.JSONArray;

import java.io.File;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.MainActivity;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.bindEditTextSummary;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.downloadFileFromInternet;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.editFile;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getAssetsString;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getSharedPreferenceOnUI;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.setWorldReadable;


public class AppsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.apps);
        setWorldReadable(getActivity());
        bindEditTextSummary(findPreference("onlineGit"));
        boolean PMEnabled = getSharedPreferenceOnUI(getActivity()).getBoolean("pm",true);
        try {
            JSONArray jsonArray = GeneralUtils.getSupportPackages();
            for (int i=0;i<jsonArray.length();i++) {
                String packageName = jsonArray.getJSONObject(i).getString("app");
                Preference preference = new Preference(getActivity(),null);
                if (PMEnabled) {
                    PackageInfo packageInfo;
                    try {
                        packageInfo = getActivity().getPackageManager().getPackageInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                    }
                    if(packageInfo == null) continue;
                    else preference.setIcon(getActivity().getPackageManager().getApplicationIcon(packageName));
                }
                //switchPreference.setChecked(true);
                preference.setTitle(jsonArray.getJSONObject(i).getString("name"));
                preference.setSummary(packageName);
                //preference.setKey(packageName+".enabled");
                preference.setOnPreferenceClickListener(preference1 -> {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, DetailFragment.Companion.newInstance(packageName)).addToBackStack(DetailFragment.class.getSimpleName() ).commit();
                    return true;
                });
                ((PreferenceCategory) findPreference("app")).addPreference(preference);
            }
            boolean showNetease = false;
            try {
                showNetease = getActivity().getPackageManager().getPackageInfo("com.netease.cloudmusic", 0)!=null;
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
                ((PreferenceCategory) findPreference("app")).addItemFromInflater(Preference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = getActivity().getExternalFilesDir(null)+File.separator+"packages.json";
            findPreference("editJSON").setOnPreferenceClickListener(preference -> {
                editFile(new File(path),getActivity());
                return true;
            });
            findPreference("editJSON").setSummary(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findPreference("refreshPackage").setOnPreferenceClickListener(preference -> {
            if (!getSharedPreferenceOnUI(getActivity()).getBoolean("network",true)) {
                Toast.makeText(getActivity(),"联网已禁用",Toast.LENGTH_SHORT).show();
                return true;
            }
            downloadFileFromInternet("packages.json",(MainActivity) getActivity());
            return true;
        });

        setWorldReadable(getActivity());

    }

}
