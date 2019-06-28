package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicInit;
import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comandroidsystemui extends BasicInit {

    @Override
    public void init() {
        XposedHelpers.findAndHookMethod(Application.class.getName(), classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                int minHeightID = context.getResources().getIdentifier("notification_min_height_legacy","dimen","com.android.systemui");
                XposedBridge.log("给播放器系统的音乐通知：获取到ID！"+ minHeightID);
                XposedHelpers.findAndHookMethod(Resources.class, "getDimensionPixelSize", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //XposedBridge.log("给播放器系统的音乐通知：加载成功");
                        super.afterHookedMethod(param);
                        if ((int)param.args[0]==minHeightID) {
                            XposedBridge.log("给播放器系统的音乐通知：设置成功");
                            param.setResult(new XSharedPreferences("cn.nexus6p.QQMusicNotify").getInt("notification_min_height_legacy",133));
                        }
                    }
                });

            }
        });
    }

}
