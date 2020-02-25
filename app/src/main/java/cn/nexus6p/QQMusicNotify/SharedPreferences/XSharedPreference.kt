package cn.nexus6p.QQMusicNotify.SharedPreferences

import android.content.SharedPreferences
import cn.nexus6p.QQMusicNotify.BuildConfig
import de.robv.android.xposed.XSharedPreferences
import java.lang.ref.WeakReference

@Deprecated(message = "Using Content Provider instead in 2.9")
class XSharedPreference {

    private var preferences : XSharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID)

    init {
        preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
        preferences.makeWorldReadable()
        preferences.reload()
    }

    companion object {
        private var xSharedPreference = null
        fun get(): SharedPreferences {
            return XSharedPreference().preferences
        }
    }

}