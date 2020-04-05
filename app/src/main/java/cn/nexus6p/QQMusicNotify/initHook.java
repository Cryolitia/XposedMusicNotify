package cn.nexus6p.QQMusicNotify;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import androidx.annotation.Keep;

import org.json.JSONArray;

import java.util.List;

import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference;
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

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        if (lpparam.packageName.equals("me.singleneuron.originalmusicnotification_debugtool")) {
            new cn.nexus6p.QQMusicNotify.Hook.mesingleneuronoriginalmusicnotification_debugtool(lpparam).init();
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Context context = (Context) param.args[0];
                if (context == null) {
                    XposedBridge.log(lpparam.packageName + ": Context is null!");
                    return;
                }
                ClassLoader classLoader = context.getClassLoader();
                if (classLoader == null) {
                    XposedBridge.log(lpparam.packageName + ": classloader is null!");
                    return;
                }

                if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    findAndHookMethod("cn.nexus6p.QQMusicNotify.Utils.HookStatue", lpparam.classLoader, "isEnabled", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            XposedBridge.log("模块已激活");
                            return true;
                        }
                    });
                    return;
                }

                if (lpparam.packageName.equals("com.android.systemui")) {
                    if (new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE, null, context).getBoolean("miuiModify", false)) {
                        try {
                            cn.nexus6p.removewhitenotificationforbugme.main.handleLoadPackage(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE, null, context).getBoolean("miuiForceExpand", false)) {
                        try {
                            System.ExpandNotificationsHook(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                if (PreferenceUtil.getPreference(context).getBoolean("onlyForeground", false) && (!RunningForeground(context, lpparam.packageName)))
                    return;

                if (isHookEnabled(lpparam.packageName, context)) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).setContext(context).init();
                }

                if (PreferenceUtil.getPreference(context).getBoolean("styleModify", false)) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    try {
                        new NotificationHook().init(lpparam.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isHookEnabled(String packageName, Context context) {
        JSONArray jsonArray = PreferenceUtil.getSupportPackages(context);
        if (jsonArray == null) {
            XposedBridge.log("加载配置文件失败：" + packageName);
            return false;
        }
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && (PreferenceUtil.getPreference(context).getBoolean(packageName + ".enabled", true)));
    }

    private static boolean RunningForeground(Context context, String packageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return true;
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if ((appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}
