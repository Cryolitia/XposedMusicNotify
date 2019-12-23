package cn.nexus6p.QQMusicNotify.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

        String[] urls = {"https://xposedmusicnotify.singleneuron.me/config/","https://raw.githubusercontent.com/singleNeuron/XposedMusicNotify/gh-pages/config/","https://cn.xposedmusicnotify.singleneuron.me/config/"};
        SharedPreferences sharedPreferences = getSharedPreferenceOnUI(getActivity());
        ListPreference listPreference = findPreference("onlineGitIndex");
        listPreference.setSummary(sharedPreferences.getString("onlineGitIndex","0").equals("3")?sharedPreferences.getString("onlineGit","https://xposedmusicnotify.singleneuron.me/config/"):urls[Integer.valueOf(sharedPreferences.getString("onlineGitIndex","0"))]);
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.equals("3")) {
                final EditText editText = new EditText(getActivity());
                editText.setText(sharedPreferences.getString("onlineGitIndex","0").equals("3")?sharedPreferences.getString("onlineGit","https://xposedmusicnotify.singleneuron.me/config/"):urls[Integer.valueOf(sharedPreferences.getString("onlineGitIndex","0"))]);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("设置自定义仓库地址")
                        .setView(editText)
                        .setPositiveButton("确定", (dialog, which) -> {
                            String url = editText.getText().toString();
                            sharedPreferences.edit().putString("onlineGit",url).apply();
                            listPreference.setSummary(url);
                        })
                        .show();
            } else {
                String url = urls[Integer.valueOf((String)newValue)];
                sharedPreferences.edit().putString("onlineGit",url).apply();
                listPreference.setSummary(url);
            }
            return true;
        });
        setWorldReadable(getActivity());

    }

}
