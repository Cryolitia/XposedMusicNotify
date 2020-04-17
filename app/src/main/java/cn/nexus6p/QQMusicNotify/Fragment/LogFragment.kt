package cn.nexus6p.QQMusicNotify.Fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import cn.nexus6p.QQMusicNotify.R
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils
import cn.nexus6p.QQMusicNotify.Utils.LogUtils.Companion.cleanLog
import com.azhon.suspensionfab.FabAttributes
import com.azhon.suspensionfab.SuspensionFab
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

class LogFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.log_fragment, container, false)

        val sharedPreferences = GeneralUtils.getSharedPreferenceOnUI(context)

        val logFile = File(requireContext().filesDir.absolutePath + File.separator + "log.txt")
        if (!logFile.exists()) logFile.createNewFile()

        val textView = view.findViewById<TextView>(R.id.textView)
        textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.textViewBackground))

        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val colorPrimaryVariant = ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant)

        val suspensionFab = view.findViewById<SuspensionFab>(R.id.fab_top)
        val defaultFab = suspensionFab.findViewWithTag<FloatingActionButton>(0)
        defaultFab.backgroundTintList = ColorStateList.valueOf(colorPrimary)

        val deleteFAB = FabAttributes.Builder()
                .setBackgroundTint(colorPrimary)
                .setSrc(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_black_24dp))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(1)
                .build()
        val refreshFAB = FabAttributes.Builder()
                .setBackgroundTint(colorPrimary)
                .setSrc(ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh_black_24dp))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(2)
                .build()
        val settingFAB = FabAttributes.Builder()
                .setBackgroundTint(colorPrimary)
                .setSrc(ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings_black_24dp))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(3)
                .build()
        suspensionFab.addFab(settingFAB, deleteFAB, refreshFAB)
        suspensionFab.setFabClickListener { _, tag ->
            when (tag) {
                1 -> {
                    val snackBar = Snackbar.make(view.findViewById(R.id.log_content), "清除所有日志", Snackbar.LENGTH_LONG)
                            .setAction("确定", View.OnClickListener {
                                logFile.writeText("")
                                textView.text = ""
                            })
                    val snackBarView = snackBar.view
                    snackBarView.fitsSystemWindows = false
                    ViewCompat.setOnApplyWindowInsetsListener(snackBarView, null)
                    snackBar.show()

                    /*MaterialAlertDialogBuilder(activity)
                            .setMessage("清除所有日志")
                            .setPositiveButton("确定") { _, _ ->
                                logFile.delete()
                                textView.text = ""
                            }
                            .setNegativeButton("取消", null)
                            .create()
                            .show()*/
                }
                2 -> {
                    textView.text = logFile.readText()
                }
                3 -> {
                    val editText = EditText(context)
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.setText(sharedPreferences.getInt("logMaxLine", 1000).toString())
                    MaterialAlertDialogBuilder(requireContext())
                            .setTitle("最大保留条数")
                            .setView(editText)
                            .setPositiveButton("确定") { _, _ ->
                                val maxLine: Int = Integer.parseInt(editText.text.toString())
                                sharedPreferences.edit().putInt("logMaxLine", maxLine).apply()
                                cleanLog(maxLine, requireContext())
                                textView.text = logFile.readText()
                            }
                            .setNegativeButton("取消", null)
                            .create()
                            .show()
                }
            }
        }
        for (i in 0..3) {
            suspensionFab.findViewWithTag<FloatingActionButton>(i).apply {
                setColorFilter(Color.WHITE)
                rippleColor = colorPrimaryVariant
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val logFile = File(requireContext().filesDir.absolutePath + File.separator + "log.txt")
        val textView = requireView().findViewById<TextView>(R.id.textView)
        textView.text = logFile.readText()
    }

}
