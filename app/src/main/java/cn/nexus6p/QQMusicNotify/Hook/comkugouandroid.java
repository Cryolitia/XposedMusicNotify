package cn.nexus6p.QQMusicNotify.Hook;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comkugouandroid extends BasicInit {

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT > 28) return;
        try {
            Class preferenceClass = XposedHelpers.findClass("com.kugou.framework.setting.preference.Preference", classLoader);
            XposedHelpers.findAndHookMethod("com.kugou.framework.setting.preference.PreferenceGroup", classLoader, "removePreference", preferenceClass, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Object preference = param.args[0];
                    String key = (String) XposedHelpers.callMethod(preference, "getKey");
                    XposedBridge.log("XposedMusicNotify: get key " + key);
                    Toast.makeText(basicParam.getContext(),"kugou try to remove "+key,Toast.LENGTH_SHORT).show();
                    if (key.contains("USE_KG_NOTIFICATION")) param.setResult(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            XposedHelpers.findAndHookMethod("com.kugou.common.q.c", classLoader, "bX", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            XposedHelpers.findAndHookMethod("com.kugou.common.q.c", classLoader, "aj", boolean.class, new XC_MethodReplacement() {
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