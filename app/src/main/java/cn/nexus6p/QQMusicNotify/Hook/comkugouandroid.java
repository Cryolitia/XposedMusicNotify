package cn.nexus6p.QQMusicNotify.Hook;

import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicInit;
import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comkugouandroid extends BasicInit {

    @Override
    public void init() {
        if ((!BuildConfig.DEBUG) && Build.VERSION.SDK_INT > 28) return;
        try {
            Class preferenceClass = XposedHelpers.findClass("com.kugou.framework.setting.preference.Preference", classLoader);
            XposedHelpers.findAndHookMethod("com.kugou.framework.setting.preference.PreferenceGroup", classLoader, "removePreference", preferenceClass, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Object preference = param.args[0];
                    String key = (String) XposedHelpers.callMethod(preference, "getKey");
                    XposedBridge.log("XposedMusicNotify: get key " + key);
                    if (key.contains("USE_KG_NOTIFICATION")) {
                        Toast.makeText(basicParam.getContext(), "stop kugou removing " + key, Toast.LENGTH_SHORT).show();
                        param.setResult(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences preference = PreferenceUtil.getJSONPreference("com.kugou.android", basicParam.getContext());
        String className = preference.getString("class", "");
        try {
            String method1 = preference.getString("method1", "");
            XposedHelpers.findAndHookMethod(className, classLoader, method1, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String method2 = preference.getString("method2", "");
            XposedHelpers.findAndHookMethod(className, classLoader, method2, boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}