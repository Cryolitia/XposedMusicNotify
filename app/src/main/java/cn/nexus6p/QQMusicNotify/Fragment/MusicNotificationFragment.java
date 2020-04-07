package cn.nexus6p.QQMusicNotify.Fragment;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import cn.nexus6p.QQMusicNotify.MainActivity;
import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.HookStatue;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.deviceContextPreferenceChangeListener;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.jumpToAlipay;

@Keep
public class MusicNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.music_notification);
        //GeneralUtils.bindPreference(this,"styleModify","always_show");
        GeneralUtils.jumpToLink(this, "github2", "https://github.com/Qiwu2542284182/MusicNotification", false);
        if (HookStatue.isExpModuleActive(getActivity()) > 0) {
            findPreference("miuiModify").setSummary("仅太极·阳有效，请将SystemUI加入太极");
        }

        findPreference("styleModify").setOnPreferenceChangeListener((preference, newValue) -> {
            ((MainActivity) getActivity()).reload();
            return true;
        });
        /*findPreference("always_show").setOnPreferenceChangeListener((preference, newValue) -> {
            preferenceChangeListener(preference,newValue);
            return true;
        });*/
        findPreference("miuiModify").setOnPreferenceChangeListener((preference, newValue) -> {
            deviceContextPreferenceChangeListener(preference, newValue, getActivity());
            return true;
        });
        findPreference("miuiForceExpand").setOnPreferenceChangeListener((preference, newValue) -> {
            deviceContextPreferenceChangeListener(preference, newValue, getActivity());
            return true;
        });

        jumpToAlipay(this, "alipay", "fkx00337aktbgg6hgq64ae2?t=1542355035868");

        if (PreferenceUtil.isGooglePlay) findPreference("alipay").setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("关于去白边及强制展开")
                .setMessage("此功能是否生效取决于您的手机系统及框架，且不属于本模块的常规功能范畴，本人不为其效果提供任何保证及咨询服务，本功能在您的手机上失效亦不会属于未来任何一个版本的修复内容。关于部分ROM上的通知如何去除白边或强制展开，请自行去相关论坛或手机系统交流群咨询。\n\n下面列出一个可能会有用但没用也不关我事的网址：\nhttps://github.com/singleNeuron/XposedRemoveNotificationWhiteFrame/releases")
                .setCancelable(false)
                .setPositiveButton("我已阅读并知悉 (5)", (dialog1, which) -> {
                    new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("我已阅读并知悉")
                            .setMessage("点击我已阅读并知悉代表您已同意由个人自行承担在任何场所向开发者提问关于此功能失效问题的一切后果。")
                            .setPositiveButton("同意", null)
                            .setNegativeButton("取消", (dialog2, which1) -> System.exit(0))
                            .create()
                            .show();
                })
                .create();
        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);
        new Thread(() -> {
            for (int i = 5; i > 0; i--) {
                int finalI = i;
                getActivity().runOnUiThread(() -> {
                    button.setText("我已阅读并知悉 (" + finalI + ")");
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getActivity().runOnUiThread(() -> {
                button.setText("我已阅读并知悉");
            });
            getActivity().runOnUiThread(() -> {
                button.setEnabled(true);
            });
        }).start();
    }

}
