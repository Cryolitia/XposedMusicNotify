package cn.nexus6p.QQMusicNotify.SharedPreferences

import android.util.Log
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.Exception
import java.lang.IllegalArgumentException

@Deprecated(message = "Using Content Provider instead in 2.9")
class JSONPreference private constructor() : BasePreference() {

    lateinit var jsonObject : JSONObject

    companion object {
        fun get(packageName:String) : JSONPreference {
            require(packageName != "") { "PackageName should not be null" }
            val jsonPreference =  JSONPreference()
            try {
                jsonPreference.jsonObject = JSONObject(GeneralUtils.getAssetsString("$packageName.json"))
            } catch (e : Exception) {
            }
            return jsonPreference
        }
        fun setter() : JSONPreference {
            val jsonPreference = JSONPreference()
            if (File("/sdcard/Android/data/cn.nexus6p.QQMusicNotify/files/setting.json").exists())
                jsonPreference.jsonObject = JSONObject(GeneralUtils.getAssetsString("setting.json"))
            else jsonPreference.jsonObject = JSONObject()
            return jsonPreference
        }
    }

    override fun getBoolean(p0: String?, p1: Boolean): Boolean {
        return try {
            jsonObject.optBoolean(p0, p1)
        } catch (e:Exception) {
            p1
        }
    }

    override fun getInt(p0: String?, p1: Int): Int {
        return try {
            Integer.parseInt(jsonObject.optString(p0,p1.toString()),16)
        } catch (e:Exception) {
            p1
        }
    }

    override fun getString(p0: String?, p1: String?): String? {
        return try {
            jsonObject.optString(p0, p1 ?: "")
        } catch (e:Exception) {
            p1
        }
    }

    fun commit() {
        Log.d("JSONPreference",jsonObject.toString())
        val file = File("/sdcard/Android/data/cn.nexus6p.QQMusicNotify/files/setting.json")
        if (file.exists()) file.delete()
        try {
            val printStream = PrintStream(FileOutputStream(file))
            printStream.print(jsonObject.toString())
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

}