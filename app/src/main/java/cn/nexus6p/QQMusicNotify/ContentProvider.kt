package cn.nexus6p.QQMusicNotify

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.StringDef
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import cn.nexus6p.QQMusicNotify.Utils.LogUtils
import cn.nexus6p.QQMusicNotify.Utils.MediaSessionTokenHashMap.mediaSessionTokenMap
import org.json.JSONObject

@Keep
class ContentProvider : ContentProvider() {

    companion object {
        const val CONTENT_PROVIDER_JSON: String = "content_provider_json"
        const val CONTENT_PROVIDER_PREFERENCE: String = "content_provider_preference"
        const val CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE: String = "content_provider_device_protected_preference"
        const val CONTENT_PROVIDER_COMMIT: String = "content_provider_commit"
        const val PUSH_MEDIA_SESSION_TOKEN: String = "push_mediasession_token"
        const val PULL_MEDIA_SESSION_TOKEN: String = "pull_mediasession_token"
        const val GET_ALL_SESSION_TOKEN: String = "get_all_session_token"

        const val BUNDLE_KEY_JSON_STRING: String = "bundle_key_json_string"
        const val BUNDLE_KEY_MEDIA_SESSION_TOKEN: String = "bundle_key_mediasession_token"

        /*@JvmStatic
        val mediaSessionTokenMap: HashMap<String,MediaSession.Token> = HashMap()*/
    }

    override fun call(@ContentProviderParams method: String, arg: String?, extras: Bundle?): Bundle? {
        //Log.d("XposedMusicNotify", "ContentProvider is called")
        val bundle = Bundle()
        if (method == CONTENT_PROVIDER_COMMIT) {
            LogUtils.addLog(arg!!, context!!)
            return Bundle().apply {
                putBoolean("success", true)
            }
        }
        if (method == PUSH_MEDIA_SESSION_TOKEN) {
            if (extras==null) return Bundle().apply {
                putBoolean("success", false)
            }
            val token = extras.get(BUNDLE_KEY_MEDIA_SESSION_TOKEN) as MediaSession.Token
            Log.d("XposedMusicNotify","get $arg's token:$token")
            Log.d("XposedMusicNotify","process: "+Process.myPid())
            mediaSessionTokenMap[arg!!] = token
            Log.d("XposedMusicNotify", mediaSessionTokenMap.toString())
            return Bundle().apply {
                putBoolean("success", true)
            }
        }
        if (method == PULL_MEDIA_SESSION_TOKEN) {
            val token = mediaSessionTokenMap[arg]
            return Bundle().apply {
                putParcelable(BUNDLE_KEY_MEDIA_SESSION_TOKEN,token)
            }
        }
        if (method == GET_ALL_SESSION_TOKEN) {
            return Bundle().apply {
                Log.d("XposedMusicNotify", "send hashMap $mediaSessionTokenMap")
                Log.d("XposedMusicNotify","process: "+Process.myPid())
                putSerializable(BUNDLE_KEY_MEDIA_SESSION_TOKEN, mediaSessionTokenMap)
            }
        }
        if (method == CONTENT_PROVIDER_JSON) {
            //Log.d("XposedMusicNotify","getJsonFile: "+GeneralUtils.getAssetsString("$arg.json",context))
            bundle.putString(BUNDLE_KEY_JSON_STRING, GeneralUtils.getAssetsString("$arg.json", context))
        } else {
            val mSharedPreferences: SharedPreferences = when (method) {
                CONTENT_PROVIDER_PREFERENCE -> GeneralUtils.getSharedPreferenceOnUI(context)
                CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context!!.createDeviceProtectedStorageContext().getSharedPreferences("deviceProtected", Context.MODE_PRIVATE)
                } else {
                    context!!.getSharedPreferences("deviceProtected", Context.MODE_PRIVATE)
                }
                else -> throw IllegalArgumentException()
            }

            bundle.putString(BUNDLE_KEY_JSON_STRING, JSONObject(mSharedPreferences.all).toString())

        }
        return bundle
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE, CONTENT_PROVIDER_JSON, CONTENT_PROVIDER_PREFERENCE, CONTENT_PROVIDER_COMMIT, PULL_MEDIA_SESSION_TOKEN, PUSH_MEDIA_SESSION_TOKEN)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class ContentProviderParams

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