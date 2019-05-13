package cn.nexus6p.QQMusicNotify;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_WORLD_READABLE;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.settings);
        findPreference("author").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            try {
                //intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("coolmarket://u/603406"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "未安装酷安", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return true;
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
        else if (HookStatue.isExpModuleActive(getActivity())) preference.setSummary("太极已激活");
        else {
            preference.setSummary("模块未激活");
            preference.setOnPreferenceClickListener(preference1 -> {
                Intent t = new Intent("me.weishu.exp.ACTION_MODULE_MANAGE");
                t.setData(Uri.parse("package:" + "cn.nexus6p.QQMusicNotify"));
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
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            findPreference("version").setSummary(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findPreference("qqqun").setOnPreferenceClickListener(preference1 -> {
            /*
             *
             * 发起添加群流程。群号：某些不靠谱插件交流群(951343825) 的 key 为： AjOW9zYQyaV9LQhyqIQrjo21bXnu3JRC
             * 调用 joinQQGroup(AjOW9zYQyaV9LQhyqIQrjo21bXnu3JRC) 即可发起手Q客户端申请加群 某些不靠谱插件交流群(951343825)
             *
             * @param key 由官网生成的key
             * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
             ******************/
            final String key = "AjOW9zYQyaV9LQhyqIQrjo21bXnu3JRC";
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent);
            } catch (Exception e) {
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("QQ群", "951343825");
                cmb.setPrimaryClip(mClipData);
                Toast.makeText(getActivity(),"已复制到剪贴板",Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        //从github.com/zpp0196/QQPurify里抄来的
        SwitchPreference showIcon = (SwitchPreference) findPreference("showIcon");
        showIcon.setChecked(getEnable());
        showIcon.setOnPreferenceChangeListener((iconPreference, newValue) -> {
            getActivity().getPackageManager().setComponentEnabledSetting(getAlias(), getEnable() ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            return true;
        });
    }

    private boolean getEnable() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            int state = packageManager.getComponentEnabledSetting(getAlias());
            return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        } catch (Exception e) {
            return false;
        }
    }

    private ComponentName getAlias() {
        return new ComponentName(getActivity(), MainActivity.class.getName() + "Alias");
    }
}
