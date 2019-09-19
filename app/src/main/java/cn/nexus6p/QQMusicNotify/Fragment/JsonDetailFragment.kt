package cn.nexus6p.QQMusicNotify.Fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import cn.nexus6p.QQMusicNotify.R
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException

class JsonDetailFragment private constructor() : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(jsonString:String) : JsonDetailFragment {
            val bundle = Bundle()
            bundle.putString("jsonString",jsonString)
            val jsonDetailFragment = JsonDetailFragment()
            jsonDetailFragment.arguments=bundle
            return jsonDetailFragment
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.json_detail)
        if (arguments==null) throw IllegalAccessException("Arguments should not be null,please use newInstance to get a DetailFragment object and set the param as the packageName")
        try {
            val jsonString = arguments!!.getString("jsonString")
            val jsonObject = JSONObject(jsonString!!)
            val detailPreferenceCategory : PreferenceCategory = findPreference("JSONDetail")!!
            for (key in jsonObject.keys()) {
                val preference = Preference(activity!!)
                preference.title = key
                preference.summary = jsonObject.optString(key)
                detailPreferenceCategory.addItemFromInflater(preference)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

}