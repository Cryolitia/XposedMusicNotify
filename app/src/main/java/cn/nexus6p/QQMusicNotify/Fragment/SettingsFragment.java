package cn.nexus6p.QQMusicNotify.Fragment;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.MainActivity;
import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.HookStatue;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuGeneralPublicLicense30;
import de.psdev.licensesdialog.licenses.GnuLesserGeneralPublicLicense3;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getJsonFromInternet;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getSharedPreferenceOnUI;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.jumpToLink;

public class SettingsFragment extends PreferenceFragmentCompat {

    private int i = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootkey) {
        addPreferencesFromResource(R.xml.settings);
        jumpToLink(this, "author", "u/603406", true);
        jumpToLink(this, "github", "https://github.com/singleNeuron/XposedMusicNotify", false);
        jumpToLink(this, "telegram", "https://t.me/NeuronDevelopChannel", false);

        findPreference("thanks").setOnPreferenceClickListener(preference13 -> {
            if (i++ > 3) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ExperimentalFragment()).addToBackStack(ExperimentalFragment.class.getSimpleName()).commit();
                i = 0;
            }
            return true;
        });
        Preference preference = findPreference("statue");
        boolean fakeTaichi = GeneralUtils.getSharedPreferenceOnUI(getActivity()).getBoolean("fakeTaichi", false);
        boolean taichi_magisk = false;
        int ExpStatue = HookStatue.isExpModuleActive(getActivity());
        try {
            taichi_magisk = "1".equals(System.getProperty("taichi_magisk"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fakeTaichi || ExpStatue == 2) {
            preference.setSummary("太极·阴 已激活");
            Preference taichiProblemPreference = findPreference("taichiProblem");
            taichiProblemPreference.setVisible(true);
            taichiProblemPreference.setOnPreferenceClickListener(preference1 -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(R.drawable.taichi);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setAdjustViewBounds(true);
                builder.setView(imageView).setPositiveButton("确定", null).create().show();
                return true;
            });
            try {
                //Toast.makeText(getActivity(),System.getProperty("taichi_magisk"),Toast.LENGTH_LONG).show();
                if (taichi_magisk || HookStatue.isEnabled())
                    preference.setSummary("太极·阳 已激活");
            } catch (Exception e) {
                //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (HookStatue.isEnabled()) {
            preference.setSummary("Xposed 已激活");
        } else if (ExpStatue == 1) {
            preference.setSummary("太极·" + (taichi_magisk ? "阳" : "阴") + " 未激活");
            preference.setOnPreferenceClickListener(preference1 -> {
                Intent t = new Intent("me.weishu.exp.ACTION_MODULE_MANAGE");
                t.setData(Uri.parse("package:" + "cn.nexus6p.QQMusicNotify"));
                t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(t);
                } catch (ActivityNotFoundException e) {
                    preference.setSummary("模块未激活，您是否已在启用或升级模块后重启手机");
                }
                return true;
            });
        } else {
            preference.setSummary("模块未激活，您是否已在启用或升级模块后重启手机");
        }

        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
        if (BuildConfig.VERSION_NAME.contains("canary") || BuildConfig.VERSION_NAME.contains("NIGHTLY") || BuildConfig.VERSION_NAME.contains("beta") || BuildConfig.VERSION_NAME.contains("alpha") || BuildConfig.VERSION_NAME.contains("α") || BuildConfig.VERSION_NAME.contains("β"))
            Toast.makeText(getActivity(), "您正在使用未经完全测试的版本，使用风险自负\n测试版本不代表最终品质", Toast.LENGTH_LONG).show();
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
                assert cmb != null;
                cmb.setPrimaryClip(mClipData);
                Toast.makeText(getActivity(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        //从github.com/zpp0196/QQPurify里抄来的
        SwitchPreferenceCompat showIcon = findPreference("showIcon");
        showIcon.setChecked(getEnable());
        showIcon.setOnPreferenceChangeListener((iconPreference, newValue) -> {
            getActivity().getPackageManager().setComponentEnabledSetting(getAlias(), getEnable() ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            return true;
        });

        findPreference("connect").setOnPreferenceClickListener(preference1 -> {
            startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:liziyuan0720@gmail.com")), "发送邮件"));
            return true;
        });

        findPreference("version").setOnPreferenceClickListener(preference1 -> {
            getJsonFromInternet((MainActivity) getActivity(), true);
            return true;
        });

        //GeneralUtils.bindPreference(this,"sdcard","locate");

        findPreference("openSource").setOnPreferenceClickListener(preference1 -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("给播放器原生的音乐通知", "https://github.com/singleNeuron/XposedMusicNotify", "Copyright 2019 神经元", new GnuLesserGeneralPublicLicense3()));
            notices.addNotice(new Notice("Android", "https://source.android.com/license", "The Android Open Source Project", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("XposedBridge", "https://github.com/rovo89/XposedBridge", "Copyright 2013 rovo89, Tungstwenty", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("MusicNotification", "https://github.com/Qiwu2542284182/MusicNotification", "祈无", new License() {
                @Override
                public String getName() {
                    return "PY License";
                }

                @Override
                public String readSummaryTextFromResources(Context context) {
                    return "PY License\n已和原作者py";
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
            notices.addNotice(new Notice("MediaNotification", "https://github.com/Soptq/MediaNotification/tree/Coolapk", "Soptq", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("去除通知栏白色边框", "https://github.com/singleNeuron/XposedRemoveNotificationWhiteFrame", "Copyright 2019 神经元", new MITLicense()));
            notices.addNotice(new Notice("QQ净化", "https://github.com/zpp0196/QQPurify", "zpp0196", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("CustoMIUIzer", "https://code.highspec.ru/Mikanoshi/CustoMIUIzer", "Mikanoshi", new GnuGeneralPublicLicense30()));
            new LicensesDialog.Builder(Objects.requireNonNull(getContext()))
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();
            return true;
        });

        findPreference("music_notification").setOnPreferenceClickListener(preference1 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MusicNotificationFragment()).addToBackStack(MusicNotificationFragment.class.getSimpleName()).commit();
            return true;
        });

        findPreference("media_notification").setOnPreferenceClickListener(preference1 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MediaNotificationFragment()).addToBackStack(MusicNotificationFragment.class.getSimpleName()).commit();
            return true;
        });

        //findPreference("apps").setFragment(AppsFragment.class.getName());
        findPreference("apps").setOnPreferenceClickListener(preference1 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AppsFragment(), "appsFragment").addToBackStack(AppsFragment.class.getSimpleName()).commit();
            return true;
        });

        findPreference("reUnzip").setOnPreferenceClickListener(preference12 -> {
            try {
                for (File file : getActivity().getExternalFilesDir(null).listFiles()) file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((MainActivity) SettingsFragment.this.getActivity()).copyAssetsDir2Phone();
            return true;
        });

        /*findPreference("selinux").setOnPreferenceClickListener(preference1 -> {
            if (!Shell.rootAccess()) {
                Toast.makeText(getActivity(), "未检测到Root权限", Toast.LENGTH_SHORT).show();
                return true;
            }
            Shell.Result result = Shell.su("getenforce").exec();
            if (!result.isSuccess()) {
                Toast.makeText(getActivity(), result.getErr().toString(), Toast.LENGTH_LONG).show();
                return true;
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity()).setTitle("SeLinux").setMessage("当前状态： " + result.getOut().get(0));
            if ((!PreferenceUtil.isGooglePlay)&&result.getOut().get(0).contains("Permissive")) {
                builder.setPositiveButton("确定", null).setNegativeButton("取消", null).create().show();

            }
            else {
                builder.setPositiveButton("设为Permissive", (dialogInterface, i) -> {
                    Shell.Result result1 = Shell.su("setenforce 0").exec();
                    if (result1.isSuccess())
                        Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), result1.getErr().toString(), Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null).create().show();
            }
            return true;
        });*/

        /*try {
            Shell.Result result0 = Shell.su("getenforce").exec();
            if (result0.getOut().get(0).contains("Permissive")) findPreference("selinux").setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //SwitchPreferenceCompat pmPreference = (SwitchPreferenceCompat) findPreference("pm");
        //if (pmPreference.isChecked()) {
        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        boolean isChimiInstall = false;
        //boolean isRedirectStorageInstall = false;
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equalsIgnoreCase("chili.xposed.chimi")) {
                isChimiInstall = true;
                break;
            }
            //if (packageInfo.packageName.equalsIgnoreCase("moe.shizuku.redirectstorage"))
            //isRedirectStorageInstall = true;
        }
        if (!isChimiInstall) findPreference("chimi").setVisible(false);
        //if (!isRedirectStorageInstall) findPreference("redirectStorage").setVisible(false);
        //}

        findPreference("forceNight").setOnPreferenceChangeListener((preference1, newValue) -> {
            getActivity().recreate();
            return true;
        });

        findPreference("media_notification").setVisible(getSharedPreferenceOnUI(getActivity()).getBoolean("styleModify", true));

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
