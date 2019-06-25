package cn.nexus6p.QQMusicNotify.Hook;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.HookInterface;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comtencentqqmusic implements HookInterface {

    private ClassLoader mClassloader;

    @Override
    public void init() {
        XposedHelpers.findAndHookMethod("com.tencent.qqmusic.fragment.morefeatures.settings.providers.NotificationStyleSettingProvider", mClassloader, "shouldUseAndroidMediaNotificationStyle", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.qqmusic.fragment.morefeatures.settings.providers.NotificationStyleSettingProvider", mClassloader, "shouldUseQQMusicNotificationStyle", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });
    }

    @Override
    public HookInterface setClassLoader(ClassLoader classLoader) {
        mClassloader = classLoader;
        return this;
    }
}