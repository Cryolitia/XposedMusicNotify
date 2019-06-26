package cn.nexus6p.QQMusicNotify;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import org.json.JSONArray;
import org.json.JSONObject;
;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuLesserGeneralPublicLicense3;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

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
            JSONArray jsonArray = GeneralTools.getSupportPackages(getContext());
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
            boolean showNetease = false;
            try {
                showNetease = !PMEnabled || getActivity().getPackageManager().getPackageInfo("com.netease.cloudmusic", 0)!=null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (showNetease) {
                Preference Preference = new Preference(getActivity(),null);
                Preference.setTitle("网易云音乐");
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

        findPreference("version").setOnPreferenceClickListener(preference1 -> {
            getJsonFromInternet();
            return true;
        });

        findPreference("sdcard").setOnPreferenceChangeListener((preference1, o) -> {
            findPreference("locate").setEnabled((boolean)o);
            return true;
        });

        findPreference("locate").setEnabled(((SwitchPreference)findPreference("sdcard")).isChecked());

        findPreference("styleModify").setOnPreferenceChangeListener((preference1, o) -> {
            findPreference("always_show").setEnabled((boolean)o);
            return true;
        });

        findPreference("always_show").setEnabled(((SwitchPreference)findPreference("styleModify")).isChecked());

        findPreference("openSource").setOnPreferenceClickListener(preference1 -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("给播放器原生的音乐通知","https://github.com/singleNeuron/XposedMusicNotify","Copyright 2019 神经元",new GnuLesserGeneralPublicLicense3()));
            notices.addNotice(new Notice("AOSP","https://source.android.com/license","AOSP",new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("XposedBridge","https://github.com/rovo89/XposedBridge","Copyright 2013 rovo89, Tungstwenty",new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("MusicNotification", "https://github.com/Qiwu2542284182/MusicNotification", "祈无", new License() {
                @Override
                public String getName() {
                    return "PY License";
                }
                @Override
                public String readSummaryTextFromResources(Context context) {
                    return "PY License\n已和原作者py并获得使用授权";
                }
                @Override
                public String readFullTextFromResources(Context context) {
                    return "null";
                }
                @Override
                public String getVersion() {
                    return null;
                }
                @Override
                public String getUrl() {
                    return null;
                }
            }));
            notices.addNotice(new Notice("MediaNotification","https://github.com/Soptq/MediaNotification/tree/Coolapk","Soptq",new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("QQ净化","https://github.com/zpp0196/QQPurify","zpp0196",new ApacheSoftwareLicense20()));
            new LicensesDialog.Builder(Objects.requireNonNull(getContext()))
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();
            return true;
        });

        getJsonFromInternet();
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

    //抄的https://www.jianshu.com/p/4e12da9866a0
    private void getJsonFromInternet () {
        final String url = "https://raw.githubusercontent.com/singleNeuron/XposedMusicNotify/master/app/src/main/assets/version.json";
        if (!((SwitchPreference)findPreference("network")).isChecked()) {
            Toast.makeText(getContext(),"联网已禁用，无法检查新版本",Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode()==200) {
                    InputStream inputStream=conn.getInputStream();
                    byte[]jsonBytes=convertIsToByteArray(inputStream);
                    String json=new String(jsonBytes);
                    if (json.length()>0) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int versionCode = jsonObject.optInt("code");
                                int nowCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                                if (versionCode>nowCode) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("发现新版本")
                                            .setMessage(jsonObject.optString("name"))
                                            .setNegativeButton("取消",null)
                                            .setPositiveButton("下载", (dialogInterface, i) -> {
                                                Intent localIntent = new Intent("android.intent.action.VIEW");
                                                localIntent.setData(Uri.parse("https://github.com/singleNeuron/XposedMusicNotify/releases"));
                                                startActivity(localIntent);
                                            })
                                            .create()
                                            .show();
                                } else getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"检查更新成功，当前已是最新版本",Toast.LENGTH_SHORT).show());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                }
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"检查更新时出错",Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"检查更新时出错："+e.getMessage(),Toast.LENGTH_LONG).show());
            }
        }).start();

    }

    private byte[] convertIsToByteArray (InputStream inputStream) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length=inputStream.read(buffer))!=-1) {
                baos.write(buffer, 0, length);
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

}
