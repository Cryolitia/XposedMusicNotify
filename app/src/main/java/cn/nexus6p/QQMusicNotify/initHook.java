package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.util.Arrays;
import java.util.List;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class initHook implements IXposedHookLoadPackage {

    private final static List<String> packageList = Arrays.asList("com.tencent.karaoke");
    private XSharedPreferences xSharedPreferences;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
            findAndHookMethod("cn.nexus6p.QQMusicNotify.HookStatue",lpparam.classLoader, "isEnabled", XC_MethodReplacement.returnConstant(true));
            return;
        }
        xSharedPreferences = new XSharedPreferences("cn.nexus6p.QQMusicNotify");
        boolean enableQT = xSharedPreferences.getBoolean("enableQT",true);
        boolean enableKW = xSharedPreferences.getBoolean("enableKW",true);

        if (enableQT&&lpparam.packageName.equals("com.tencent.qqmusiclocalplayer")) {
            new QingtingHook(lpparam.classLoader).init();
            return;
        }
        if (isHookEnabled(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class.getName(), lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();
                    if (classLoader == null) {
                        Log.e(lpparam.packageName+"Hook","Can't get ClassLoader!");
                        return;
                    }
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify."+lpparam.packageName.replace(".","")+"Hook");
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).init();
                }
            });
        }
        if (enableKW&&lpparam.packageName.equals("cn.kuwo.player")) {
            XposedHelpers.findAndHookMethod(Application.class.getName(), lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();
                    if (classLoader == null) {
                        Log.e("KuwoHook","Can't get ClassLoader!");
                        return;
                    }
                    new KuwoHook(classLoader).init();
                }
            });
        }
    }

    private boolean isHookEnabled (String packageName) {
        if (xSharedPreferences==null) {
            Log.e("QQMusicnotify","XSharedPreferences should not be null!");
        }
        return (packageList.contains(packageName) && xSharedPreferences.getBoolean(packageName+".enabled",true));
    }

}
