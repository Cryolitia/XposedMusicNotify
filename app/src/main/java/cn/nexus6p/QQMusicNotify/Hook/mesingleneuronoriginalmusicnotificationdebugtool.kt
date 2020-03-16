package cn.nexus6p.QQMusicNotify.Hook

import android.content.Context
import android.util.Log
import cn.nexus6p.QQMusicNotify.Base.HookInterface
import cn.nexus6p.QQMusicNotify.BuildConfig
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class mesingleneuronoriginalmusicnotificationdebugtool : HookInterface {

    private lateinit var mClassLoader: ClassLoader

    override fun init() {
        XposedHelpers.findAndHookMethod("me.singleneuron.originalmusicnotification_debugtool.MainActivity", mClassLoader, "toHook", object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                super.afterHookedMethod(param)
                Log.d("nexus", "已进入调试器")
                XposedHelpers.getObjectField(param!!.thisObject, "textView")
                XposedHelpers.callMethod(param.thisObject, "writeToTextview", "原生音乐通知已找到：版本名" + BuildConfig.VERSION_NAME + " 版本号：" + BuildConfig.VERSION_CODE)
                return null
            }
        })
    }

    override fun setContext(context: Context?): HookInterface {
        return this
    }

    override fun setClassLoader(classLoader: ClassLoader?): HookInterface {
        if (classLoader != null) this.mClassLoader = classLoader
        return this
    }
}