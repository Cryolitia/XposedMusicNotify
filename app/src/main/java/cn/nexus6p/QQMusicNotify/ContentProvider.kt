package cn.nexus6p.QQMusicNotify

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Keep
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import org.json.JSONObject

@Keep
class ContentProvider : ContentProvider() {

    companion object {
        const val CONTENT_PROVIDER_JSON: String = "content_provider_json"
        const val CONTENT_PROVIDER_PREFERENCE: String = "content_provider_preference"
        const val CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE: String = "content_provider_device_protected_preference"
        const val BUNDLE_KEY_JSON_STRING: String = "bundle_key_json_string"
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        //Log.d("XposedMusicNotify", "ContentProvider is called")
        val bundle = Bundle()
        if (method == CONTENT_PROVIDER_JSON) {
            //Log.d("XposedMusicNotify","getJsonFile: "+GeneralUtils.getAssetsString("$arg.json",context))
            bundle.putString(BUNDLE_KEY_JSON_STRING, GeneralUtils.getAssetsString("$arg.json", context))
        } else {
            val mSharedPreferences: SharedPreferences = when (method) {
                CONTENT_PROVIDER_PREFERENCE -> GeneralUtils.getSharedPreferenceOnUI(context)
                CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE -> context!!.createDeviceProtectedStorageContext().getSharedPreferences("deviceProtected", Context.MODE_PRIVATE);
                else -> throw IllegalArgumentException()
            }

            bundle.putString(BUNDLE_KEY_JSON_STRING, JSONObject(mSharedPreferences.all).toString())

        }
        return bundle
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        throw NoSuchMethodException()
    }

    override fun getType(uri: Uri): String? {
        throw NoSuchMethodException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw NoSuchMethodException()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NoSuchMethodException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NoSuchMethodException()
    }

}