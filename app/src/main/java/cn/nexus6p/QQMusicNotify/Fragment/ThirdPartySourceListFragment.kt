package cn.nexus6p.QQMusicNotify.Fragment

import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.Keep
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import cn.nexus6p.QQMusicNotify.MainActivity
import cn.nexus6p.QQMusicNotify.R
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL

@Keep
class ThirdPartySourceListFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.third_party_source)

        val thirdPartySourcePreferenceCategory = findPreference<PreferenceCategory>("thirdPartySourceList")!!

        val urls = arrayOf("https://xposedmusicnotify.singleneuron.me/config/", "https://raw.githubusercontent.com/singleNeuron/XposedMusicNotify/gh-pages/config/", "https://cn.xposedmusicnotify.singleneuron.me/config/")
        val sharedPreferences = GeneralUtils.getSharedPreferenceOnUI(activity)
        val listPreference = findPreference<ListPreference>("onlineGitIndex")
        listPreference!!.summary = if (sharedPreferences.getString("onlineGitIndex", "0") == "3") sharedPreferences.getString("onlineGit", "https://xposedmusicnotify.singleneuron.me/config/") else urls[sharedPreferences.getString("onlineGitIndex", "0")!!.toInt()]
        listPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            if (newValue == "3") {
                val editText = EditText(activity)
                editText.setText(if (sharedPreferences.getString("onlineGitIndex", "0") == "3") sharedPreferences.getString("onlineGit", "https://xposedmusicnotify.singleneuron.me/config/") else urls[sharedPreferences.getString("onlineGitIndex", "0")!!.toInt()])
                val builder = MaterialAlertDialogBuilder(activity)
                builder.setTitle("设置自定义仓库地址")
                        .setView(editText)
                        .setPositiveButton("确定") { _: DialogInterface?, _: Int ->
                            val url = editText.text.toString()
                            sharedPreferences.edit().putString("onlineGit", url).apply()
                            listPreference.summary = url
                        }
                        .show()
            } else {
                val url = urls[(newValue as String?)!!.toInt()]
                sharedPreferences.edit().putString("onlineGit", url).apply()
                listPreference.summary = url
            }
            true
        }

        val files = activity!!.getExternalFilesDir("ThirdPartySource")
        if (!files!!.exists()) files.mkdir()
        val fileTreeWalk = files.walk()
        fileTreeWalk.maxDepth(2)
                .filter { it.isFile }
                .filter { it.name == "description.json" }
                .forEach {
                    try {
                        val jsonObject = JSONObject(it.readText())
                        val id = jsonObject.optString("id")
                        val name = jsonObject.optString("name")
                        val url = jsonObject.optString("url")
                        val preference = Preference(activity).apply {
                            title = name
                            summary = url
                            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                                MaterialAlertDialogBuilder(activity).apply {
                                    this.setTitle("请选择操作")
                                    setItems(arrayOf("更新源", "查看源", "删除源"), DialogInterface.OnClickListener { _: DialogInterface, i: Int ->
                                        when (i) {
                                            0 -> {
                                                val builder = MaterialAlertDialogBuilder(context)
                                                builder.setTitle("下载中...packages.json")
                                                builder.setCancelable(false)
                                                val progressBar = ProgressBar(context)
                                                builder.setView(progressBar)
                                                val alertDialog = builder.create()
                                                alertDialog.show()
                                                Thread(Runnable {
                                                    try {
                                                        File(activity!!.getExternalFilesDir("ThirdPartySource").toString() + File.separator + id + File.separator + "packages.json").writeBytes(URL(url + (if (url.endsWith("/")) "" else "/") + "packages.json").readBytes())
                                                        activity!!.runOnUiThread {
                                                            activity!!.toast("成功")
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                        activity!!.toast(e.toString())
                                                    } finally {
                                                        alertDialog.dismiss()
                                                    }
                                                }).start()
                                            }
                                            1 -> {
                                                try {
                                                    val jsonArray = JSONArray(File(activity!!.getExternalFilesDir("ThirdPartySource").toString() + File.separator + id + File.separator + "packages.json").readText())
                                                    //Log.d("jsonArray", jsonArray.toString())
                                                    val packageNameArrayList = ArrayList<String>()
                                                    for (j in 0 until jsonArray.length()) {
                                                        val jsonObject2 = jsonArray.optJSONObject(j)
                                                        val packageName = jsonObject2.optString("app")
                                                        val supportedVersionJsonArray = jsonObject2.optJSONArray("supportedVersion")
                                                        var packageInfo: PackageInfo?
                                                        packageInfo = try {
                                                            activity!!.packageManager.getPackageInfo(packageName, 0)
                                                        } catch (e: PackageManager.NameNotFoundException) {
                                                            null
                                                        }
                                                        if (packageInfo == null) continue
                                                        //else Log.d("packageInfo",packageInfo.toString())
                                                        @Suppress("DEPRECATION") val longVersionCode: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
                                                        //Log.d("longVersionCode",longVersionCode.toString())
                                                        for (k in 0 until supportedVersionJsonArray!!.length()) {
                                                            if (longVersionCode.toString() == supportedVersionJsonArray.optJSONObject(k).optString("versionCode")) {
                                                                packageNameArrayList.add(packageName)
                                                                break
                                                            }
                                                        }
                                                    }
                                                    val appNameArray : Array<String> = Array(packageNameArrayList.size) { j->
                                                        val packageManager = activity!!.packageManager
                                                        val packageName = packageNameArrayList[j]
                                                        packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString() + " " + activity!!.packageManager.getPackageInfo(packageName, 0).versionName
                                                    }
                                                    MaterialAlertDialogBuilder(activity!!).apply {
                                                        this.setTitle("选择配置文件以应用")
                                                        setNegativeButton("取消",null)
                                                        setItems(appNameArray) { _, which ->
                                                            val packageName = packageNameArrayList[which]
                                                            @Suppress("DEPRECATION") val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) activity!!.packageManager.getPackageInfo(packageName, 0).longVersionCode else activity!!.packageManager.getPackageInfo(packageName, 0).versionCode
                                                            GeneralUtils.downloadFileFromInternet("$packageName/$versionCode/$packageName.json", url, activity as MainActivity, context.getExternalFilesDir(null))
                                                        }
                                                        show()
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                    activity!!.toast(e.toString())
                                                }
                                            }
                                            2 -> {
                                                try {
                                                    activity!!.toast(if (File(activity!!.getExternalFilesDir("ThirdPartySource").toString() + File.separator + id).deleteRecursively()) "删除成功" else "删除失败")
                                                    (activity as MainActivity).reload()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    })
                                    setNegativeButton("取消", null)
                                    show()
                                }
                                true
                            }
                        }
                        thirdPartySourcePreferenceCategory.addPreference(preference)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        findPreference<Preference>("refreshPackage")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (!GeneralUtils.getSharedPreferenceOnUI(activity).getBoolean("network", true)) {
                Toast.makeText(activity, "联网已禁用", Toast.LENGTH_SHORT).show()
            } else GeneralUtils.downloadFileFromInternet("packages.json", activity as MainActivity?)
            true
        }

        try {
            val path = activity!!.getExternalFilesDir(null).toString() + File.separator + "packages.json"
            findPreference<Preference>("editJSON")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                GeneralUtils.editFile(File(path), activity)
                true
            }
            findPreference<Preference>("editJSON")!!.summary = path
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}