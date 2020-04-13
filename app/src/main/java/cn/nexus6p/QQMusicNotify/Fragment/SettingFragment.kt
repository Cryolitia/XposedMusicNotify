package cn.nexus6p.QQMusicNotify.Fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import cn.nexus6p.QQMusicNotify.R
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import cn.nexus6p.QQMusicNotify.Utils.HookStatue
import cn.nexus6p.QQMusicNotify.Utils.LogUtils
import splitties.toast.toast

class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.setting_fragment, container, false)
        childFragmentManager.beginTransaction().replace(R.id.content_frame3, SettingsFragment()).addToBackStack(SettingsFragment::class.java.simpleName).commit()

        val cardView = view.findViewById<CardView>(R.id.cardview)
        val cardViewTitle = view.findViewById<TextView>(R.id.cardView_Title)
        val cardViewSummary = view.findViewById<TextView>(R.id.cardView_summary)
        val cardViewImage = view.findViewById<ImageView>(R.id.cardView_image)
        val statue = HookStatue.getStatue(activity)
        cardViewTitle.text = HookStatue.getStatueName(statue)
        if (HookStatue.isActive(statue)) cardViewImage.setImageResource(R.drawable.ic_check_circle) else cardViewImage.setImageResource(R.drawable.ic_cancel)
        if (statue.name.contains("taichi", true)) cardViewImage.apply {
            setOnClickListener {
                activity!!.toast("跳转到太极")
                val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE");
                t.data = Uri.parse("package:" + "cn.nexus6p.QQMusicNotify");
                t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(t);
                } catch (e: ActivityNotFoundException) {
                    //ignore
                }
            }
            isClickable = true
        }
        cardView.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.content_frame, LogFragment(), "logFragment").addToBackStack(LogFragment::class.java.simpleName).commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        LogUtils.cleanLog(GeneralUtils.getSharedPreferenceOnUI(context).getInt("logMaxLine", 1000), activity!!)
        val cardViewSummary = view!!.findViewById<TextView>(R.id.cardView_summary)
        cardViewSummary.text = "已应用" + LogUtils.getLineCount(activity!!) + "次"
    }

}