package cn.nexus6p.QQMusicNotify.Hook

import android.util.Log
import androidx.annotation.Keep
import cn.nexus6p.QQMusicNotify.Base.BasicInit
import cn.nexus6p.QQMusicNotify.BuildConfig
import cn.nexus6p.QQMusicNotify.ContentProvider
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import de.robv.android.xposed.SELinuxHelper
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.toast

@Keep
class mesingleneuronoriginalmusicnotification_debugtool : BasicInit() {

    override fun init() {
        XposedHelpers.findAndHookMethod("me.singleneuron.originalmusicnotification_debugtool.MainActivity", classLoader, "toHook", object : XC_MethodReplacement() {

            var mParam: MethodHookParam? = null
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                super.afterHookedMethod(param)
                mParam = param
                Log.d("nexus", "已进入调试器")
                basicParam.context!!.toast("Xposed已经注入")
                val selinux = if (SELinuxHelper.isSELinuxEnabled()) if (SELinuxHelper.isSELinuxEnforced()) "Enforcing" else "Permissive" else "Disabled"
                print("SeLinux: $selinux")
                print("原生音乐通知已找到：\n版本名: " + BuildConfig.VERSION_NAME + "\n版本号：" + BuildConfig.VERSION_CODE)
                print("Context: " + basicParam.context.toString())
                print("ModuleContext: " + GeneralUtils.getModuleContext(basicParam.context))
                print("ClassLoader: $classLoader")
                print("ModuleClassLoader: " + GeneralUtils.getModuleContext(basicParam.context).classLoader)
                val jsonString: String = ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_JSON, "me.singleneuron.originalmusicnotification_debugtool", basicParam.context!!).getJSONString()
                print("JSONString: $jsonString")
                val settingJsonString: String = ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_PREFERENCE, null, basicParam.context!!).getJSONString()
                print("ModuleSettings: $settingJsonString")
                val deveceProtectedPreference = ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE,null,basicParam.context!!).getJSONString()
                print("DeviceProtectedPreference: $deveceProtectedPreference")
                return null
            }

            fun print(string: String) {
                XposedHelpers.callMethod(mParam!!.thisObject, "writeToTextview", string)
            }

        })
    }

}