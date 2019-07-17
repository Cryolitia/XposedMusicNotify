package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Keep;

import org.json.JSONArray;

import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import cn.nexus6p.QQMusicNotify.Hook.comandroidsystemui;
import me.qiwu.MusicNotification.NotificationHook;

import static cn.nexus6p.QQMusicNotify.PreferenceUtil.getXSharedPreference;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class initHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
            findAndHookMethod("cn.nexus6p.QQMusicNotify.HookStatue", lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
            return;
        }
        if (getXSharedPreference().getBoolean("forceO",false)) {
            XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader, "get", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param.method.getName().startsWith("get")) {
                        XposedHelpers.setStaticIntField(android.os.Build.VERSION.class, "SDK_INT",Build.VERSION_CODES.O);
                    }
                }
            });
        }
        XposedHelpers.findAndHookMethod(Application.class.getName(), lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                if (isHookEnabled(lpparam.packageName)) {
                    XposedBridge.log("给播放器系统的音乐通知：加载包"+lpparam.packageName);
                    final ClassLoader classLoader = (context.getClassLoader());
                    if (classLoader == null) {
                        Log.e(lpparam.packageName + "Hook", "Can't get ClassLoader!");
                        return;
                    }
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).setContext(context).init();
                }
            }
        });

        if (getXSharedPreference().getBoolean("styleModify", false)) {
            if (lpparam.packageName.equals("com.android.systemui")&& getXSharedPreference().getBoolean("miuiModify",true)) {
                XposedBridge.log("给播放器系统的音乐通知：加载包"+lpparam.packageName);
                try {
                    Class c = comandroidsystemui.class;
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(lpparam.classLoader).init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                new NotificationHook().init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isHookEnabled(String packageName) {
        JSONArray jsonArray = GeneralUtils.getSupportPackages();
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && getXSharedPreference().getBoolean(packageName + ".enabled", true));
    }

}
