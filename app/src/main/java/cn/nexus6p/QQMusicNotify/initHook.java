package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import org.json.JSONArray;

import java.lang.ref.WeakReference;

import cn.nexus6p.QQMusicNotify.Base.HookInterface;
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

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Context context = (Context) param.args[0];
                if (context == null) {
                    Log.d("XposedMusicNotify", lpparam.packageName + ": Context is null!");
                    return;
                }
                ClassLoader classLoader = context.getClassLoader();
                if (classLoader == null) {
                    Log.d("XposedMusicNotify", lpparam.packageName + ": classloader is null!");
                    return;
                }

                if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
                    findAndHookMethod("cn.nexus6p.QQMusicNotify.Utils.HookStatue", lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
                    return;
                }
                if (lpparam.packageName.equals("me.singleneuron.originalmusicnotification_debugtool")) {
                    new cn.nexus6p.QQMusicNotify.Hook.mesingleneuronoriginalmusicnotificationdebugtool().setClassLoader(lpparam.classLoader).init();
                }

                if (lpparam.packageName.equals("com.android.systemui")) {
                    if (PreferenceUtil.getPreference().getBoolean("miuiModify", false)) {
                        try {
                            cn.nexus6p.removewhitenotificationforbugme.main.handleLoadPackage(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (PreferenceUtil.getPreference().getBoolean("miuiForceExpand", false)) {
                        try {
                            System.ExpandNotificationsHook(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                if (isHookEnabled(lpparam.packageName, context)) {
                    XposedBridge.log("原生音乐通知：加载包" + lpparam.packageName);
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).setContext(context).init();
                }

                if (PreferenceUtil.getPreference().getBoolean("styleModify", false)) {
                    XposedBridge.log("原生音乐通知：加载包" + lpparam.packageName);
                    try {
                        new NotificationHook().init(lpparam.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Deprecated
    private boolean isHookEnabled(String packageName, Context context) {
        return false;
        /*JSONArray jsonArray = jsonArrayWeakReference.get();
        if (jsonArray == null) {
            jsonArray= GeneralUtils.getSupportPackages(context);
            if (jsonArray != null) jsonArrayWeakReference = new WeakReference<>(jsonArray);
        }
        if (jsonArray == null) {
            Log.d("原生音乐通知", "加载配置文件失败：" + packageName);
            return false;
        }
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && (PreferenceUtil.getPreference().getBoolean(packageName + ".enabled", true)));
        */
    }

}
