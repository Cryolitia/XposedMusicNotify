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
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
        findPreference("qiwu").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            try {
                //intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("coolmarket://u/753785"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "未安装酷安", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return true;
        });
        Preference preference = findPreference("statue");
        if (HookStatue.isEnabled()) preference.setSummary("Xposed已激活");
        else if (HookStatue.isExpModuleActive(getActivity())) {
            try {
                if (Objects.requireNonNull(System.getProperties().get("taichi_magisk")).toString().equals("1"))
                    preference.setSummary("太极·阳 已激活");
                else {
                    preference.setSummary("太极·阴 已激活");
                    findPreference("styleModify").setSummary("警告：当前模式可能为太极·阴，此功能不可用");
                }
            } catch (Exception e) {
                e.printStackTrace();
                preference.setSummary("太极·阴 已激活");
                findPreference("styleModify").setSummary("警告：当前模式可能为太极·阴，此功能不可用");
            }
        } else {
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
        boolean PMEnabled =((SwitchPreference) findPreference("pm")).isChecked();
        if (PMEnabled) {
            PackageManager pm = getActivity().getPackageManager();
            try {
                PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
                findPreference("version").setSummary(packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            findPreference("version").setSummary("已禁止读取软件包列表，功能不可用");
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

        try {
            if (PMEnabled) {
                PackageManager pm = getActivity().getPackageManager();
            }
            JSONArray jsonArray = new JSONArray(getAssetsString("packages.json"));
            for (int i=0;i<jsonArray.length();i++) {
                String packageName = jsonArray.getJSONObject(i).getString("app");
                if (PMEnabled) {
                    PackageInfo packageInfo;
                    try {
                        packageInfo = getActivity().getPackageManager().getPackageInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                        e.printStackTrace();
                    }
                    if(packageInfo == null) continue;
                }
                SwitchPreference switchPreference = new SwitchPreference(getActivity(),null);
                switchPreference.setChecked(true);
                switchPreference.setTitle(jsonArray.getJSONObject(i).getString("name"));
                switchPreference.setSummary(packageName);
                switchPreference.setKey(packageName+".enabled");
                ((PreferenceScreen) findPreference("app")).addPreference(switchPreference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findPreference("connect").setOnPreferenceClickListener(preference1 -> {
            startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:liziyuan0720@gmail.com")),"发送邮件"));
            return true;
        });

        findPreference("github2").setOnPreferenceClickListener(preference1 -> {
            Intent localIntent = new Intent("android.intent.action.VIEW");
            localIntent.setData(Uri.parse("https://github.com/Qiwu2542284182/MusicNotification"));
            startActivity(localIntent);
            return true;
        });

        findPreference("alipay").setOnPreferenceClickListener(preference1 -> {
            Intent localIntent = new Intent();
            localIntent.setAction("android.intent.action.VIEW");
            localIntent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/fkx00337aktbgg6hgq64ae2?t=1542355035868"));
            if (localIntent.resolveActivity(getActivity().getPackageManager()) != null)
            {
                startActivity(localIntent);
                return true;
            }
            localIntent.setData(Uri.parse("https://qr.alipay.com/fkx00337aktbgg6hgq64ae2?t=1542355035868".toLowerCase()));
            startActivity(localIntent);
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

    private String getAssetsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    getActivity().getAssets().open(fileName), StandardCharsets.UTF_8) );
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
