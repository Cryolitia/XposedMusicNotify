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
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import cn.nexus6p.QQMusicNotify.Hook.comandroidsystemui;
import me.qiwu.MusicNotification.NotificationHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class initHook implements IXposedHookLoadPackage {

    private XSharedPreferences xSharedPreferences;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
            findAndHookMethod("cn.nexus6p.QQMusicNotify.HookStatue", lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
            return;
        }
        xSharedPreferences = new XSharedPreferences("cn.nexus6p.QQMusicNotify");
        if (xSharedPreferences.getBoolean("forceO",false)) {
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
                if (isHookEnabled(lpparam.packageName,context)) {
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

        if (xSharedPreferences.getBoolean("styleModify", false)) {
            try {
                new NotificationHook().init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isHookEnabled(String packageName, Context context) {
        if (xSharedPreferences == null) {
            Log.e("QQMusicnotify", "XSharedPreferences should not be null!");
        }
        JSONArray jsonArray = GeneralUtils.getSupportPackages(GeneralUtils.getMoudleContext(context));
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && xSharedPreferences.getBoolean(packageName + ".enabled", true));
    }

}
