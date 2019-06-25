package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import org.json.JSONArray;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.qiwu.MusicNotification.NotificationHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class initHook implements IXposedHookLoadPackage {

    private XSharedPreferences xSharedPreferences;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
            findAndHookMethod("cn.nexus6p.QQMusicNotify.HookStatue", lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
            return;
        }
        xSharedPreferences = new XSharedPreferences("cn.nexus6p.QQMusicNotify");

        XposedHelpers.findAndHookMethod(Application.class.getName(), lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                if (isHookEnabled(lpparam.packageName,context)) {
                    final ClassLoader classLoader = (context.getClassLoader());
                    if (classLoader == null) {
                        Log.e(lpparam.packageName + "Hook", "Can't get ClassLoader!");
                        return;
                    }
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).init();
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
        JSONArray jsonArray = GeneralTools.getSupportPackages(GeneralTools.getMoudleContext(context));
        return (GeneralTools.isStringInJSONArray(packageName, jsonArray) && xSharedPreferences.getBoolean(packageName + ".enabled", true));
    }

}
