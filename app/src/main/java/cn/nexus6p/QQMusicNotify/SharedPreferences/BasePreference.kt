package cn.nexus6p.QQMusicNotify.SharedPreferences

import android.content.SharedPreferences
import java.lang.RuntimeException

abstract class BasePreference : SharedPreferences {

    override fun contains(p0: String?): Boolean {
        throw NoSuchMethodException()
    }

    override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NoSuchMethodException()
    }

    override fun getAll(): MutableMap<String, *>? {
        throw NoSuchMethodException()
    }

    override fun edit(): SharedPreferences.Editor {
        throw NoSuchMethodException()
    }

    override fun getLong(p0: String?, p1: Long): Long {
        throw NoSuchMethodException()
    }

    override fun getFloat(p0: String?, p1: Float): Float {
        throw NoSuchMethodException()
    }

    override fun getStringSet(p0: String?, p1: MutableSet<String>?): MutableSet<String> {
        throw NoSuchMethodException()
    }

    override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NoSuchMethodException()
    }
}