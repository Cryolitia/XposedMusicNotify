package cn.nexus6p.QQMusicNotify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.settings);
        findPreference("version").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                try {
                    //intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse("coolmarket://u/603406"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"未安装酷安",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return true;
            }
        });
        findPreference("github").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://github.com/singleNeuron/QQMusicNotify"));
            startActivity(intent);
            return true;
        });
        Preference preference = findPreference("statue");
        if (HookStatue.isEnabled()) preference.setSummary("模块已激活");
        else if (HookStatue.isExpModuleActive(this)) preference.setSummary("太极已激活");
        else {
            preference.setSummary("模块未激活");
            preference.setOnPreferenceClickListener(preference1 -> {
                Intent t = new Intent("me.weishu.exp.ACTION_MODULE_MANAGE");
                t.setData(Uri.parse("package:" + "Your package name"));
                t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(t);
                } catch (ActivityNotFoundException e) {
                    // TaiChi not installed.
                    return false;
                }
                return true;
            });
        }
    }

}
