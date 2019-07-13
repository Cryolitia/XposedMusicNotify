package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicInit;
import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
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
                XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.ExpandableNotificationRow", classLoader, "initDimens", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedHelpers.setIntField(param.thisObject,"mNotificationMinHeightLegacy",new XSharedPreferences("cn.nexus6p.QQMusicNotify").getInt("notification_min_height_legacy",133));
                    }
                });
                /*XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.ExpandableNotificationRow", classLoader, "updateLimits", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });*/
                final int minHeightID = context.getResources().getIdentifier("notification_min_height_legacy","dimen","com.android.systemui");
                final int marginEndID = context.getResources().getIdentifier("notification_custom_view_margin_end","dimen","com.android.systemui");
                final int marginStartID = context.getResources().getIdentifier("notification_custom_view_margin_start","dimen","com.android.systemui");
                final int extraPaddingID = context.getResources().getIdentifier("notification_row_extra_padding","dimen","com.android.systemui");
                final int cornerRadiusID = context.getResources().getIdentifier("notification_custom_corner_radius","dimen","com.android.systemui");
                XposedBridge.log("给播放器系统的音乐通知：获取到ID！"+ minHeightID);
                XposedHelpers.findAndHookMethod(Resources.class, "getDimensionPixelSize", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int resID = (int) param.args[0];
                        if (resID==minHeightID) {
                            param.setResult(new XSharedPreferences("cn.nexus6p.QQMusicNotify").getInt("notification_min_height_legacy",133));
                            XposedBridge.log("给播放器原生的音乐通知：设置成功");
                        } else if (resID==marginEndID||resID==marginStartID||resID==extraPaddingID||resID==cornerRadiusID) {
                            param.setResult(0);
                        }
                    }
                });
            }
        });
    }

}
