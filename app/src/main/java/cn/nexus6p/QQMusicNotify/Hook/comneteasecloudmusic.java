package cn.nexus6p.QQMusicNotify.Hook;

import android.content.SharedPreferences;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comneteasecloudmusic extends BasicInit {

    @Override
    public void init() {
        XposedHelpers.findAndHookMethod(SharedPreferences.class, "getInt", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.args[0].equals("notificationBackground")) {
                    param.setResult(2);
                }
            }
        });
        XposedHelpers.findAndHookMethod(SharedPreferences.Editor.class, "putInt", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (param.args[0].equals("notificationBackground")) {
                    param.args[1] = 2;
                }
            }
        });
    }

}
