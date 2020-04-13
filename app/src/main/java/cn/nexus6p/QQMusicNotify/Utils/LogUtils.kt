package cn.nexus6p.QQMusicNotify.Utils

import android.content.Context
import android.util.Log
import cn.nexus6p.QQMusicNotify.ContentProvider
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference
import com.topjohnwu.superuser.Shell
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogUtils {

    companion object {
        fun cleanLog(max: Int, context: Context) {
            val logFile = File(context.filesDir.absolutePath + File.separator + "log.txt")
            if (!logFile.exists()) return
            val fileLineCount: Int = getLineCount(context)
            if (fileLineCount <= max) return
            val tmpFile = File(context.filesDir.absolutePath + File.separator + "log.tmp")
            if (tmpFile.exists()) tmpFile.writeText("")
            tmpFile.writeBytes(logFile.readBytes())
            val command = "head -" + max + " " + tmpFile.absolutePath + " > " + logFile.absolutePath
            Log.d("log clean", command)
            val result = Shell.sh(command).exec()
            if (!result.isSuccess) Log.w("clean log", result.err.toString())
            tmpFile.delete()
        }

        fun addLogByContentProvider(packageName: String, className: String, context: Context) {
            val string: String = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ", Locale.getDefault()).format(Date()) + className + " hooked " + packageName + "  \n"
            ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_COMMIT, string, context)
        }

        fun addLog(string: String, context: Context) {
            val logFile = File(context.filesDir.absolutePath + File.separator + "log.txt")
            if (!logFile.exists()) logFile.createNewFile()
            val tmpFile = File(context.filesDir.absolutePath + File.separator + "log.tmp")
            if (tmpFile.exists()) tmpFile.writeText("")
            tmpFile.writeBytes(logFile.readBytes())
            logFile.writeText(string)
            logFile.appendText(tmpFile.readText())
            tmpFile.delete()
        }

        fun getLineCount(context: Context): Int {
            val logFile = File(context.filesDir.absolutePath + File.separator + "log.txt")
            if (!logFile.exists()) return 0
            if (logFile.length() < 1) return 0
            val lineResult = Shell.sh("wc -l " + logFile.absolutePath).exec()
            if (!lineResult.isSuccess) {
                Log.w("clean log", lineResult.err.toString())
                return 0
            }
            Log.d("log clean", lineResult.out.toString())
            return Integer.parseInt(lineResult.out[0].replace(logFile.absolutePath, "").replace(" ", ""))
        }
    }

}