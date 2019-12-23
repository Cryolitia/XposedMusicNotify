package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Keep;

import org.json.JSONArray;

import java.lang.ref.WeakReference;

import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.qiwu.MusicNotification.NotificationHook;
import name.mikanoshi.customiuizer.mods.System;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class initHook implements IXposedHookLoadPackage {

    private static WeakReference<JSONArray> jsonArrayWeakReference = new WeakReference<>(null);
    private boolean isSELinuxEnable=false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        //isSELinuxEnable = SELinuxHelper.isSELinuxEnforced();
        XposedBridge.log("SELinux状态："+isSELinuxEnable);
        if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
            findAndHookMethod("cn.nexus6p.QQMusicNotify.Utils.HookStatue", lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
            return;
        }
        if (lpparam.packageName.equals("com.android.systemui")) {
            if (PreferenceUtil.getPreference().getBoolean("miuiModify",false)) {
                try {
                    cn.nexus6p.removewhitenotificationforbugme.main.handleLoadPackage(lpparam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (PreferenceUtil.getPreference().getBoolean("miuiForceExpand",false)) {
                try {
                    System.ExpandNotificationsHook(lpparam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        /*if (getXSharedPreference().getBoolean("forceO",false)) {
            XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader, "get", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param.method.getName().startsWith("get")) {
                        XposedHelpers.setStaticIntField(android.os.Build.VERSION.class, "SDK_INT",Build.VERSION_CODES.O);
                    }
                }
            });
        }*/
        if (isHookEnabled(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class.getName(), lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context context = (Context) param.args[0];
                    if (isSELinuxEnable) Toast.makeText(context,"原生音乐通知：SELinux状态为Enforcing，所有模块功能将默认启动",Toast.LENGTH_SHORT).show();
                    XposedBridge.log("原生音乐通知：加载包" + lpparam.packageName);
                    final ClassLoader classLoader = (context.getClassLoader());
                    if (classLoader == null) {
                        Log.e(lpparam.packageName + "Hook", "Can't get ClassLoader!");
                        return;
                    }
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).setContext(context).init();
                }
            });
        }

        if (isSELinuxEnable||PreferenceUtil.getPreference().getBoolean("styleModify", false)) {
            XposedBridge.log("原生音乐通知：加载包"+lpparam.packageName);
            try {
                new NotificationHook().init(lpparam.packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isHookEnabled(String packageName) {
        JSONArray jsonArray = jsonArrayWeakReference.get();
        if (jsonArray==null) {
            jsonArray= GeneralUtils.getSupportPackages();
            if (jsonArray!=null) jsonArrayWeakReference = new WeakReference<>(jsonArray);
        }
        if (jsonArray==null) {
            Log.d("原生音乐通知","加载配置文件失败："+packageName);
            return false;
        }
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && (isSELinuxEnable || PreferenceUtil.getPreference().getBoolean(packageName + ".enabled", true)));
    }

}
