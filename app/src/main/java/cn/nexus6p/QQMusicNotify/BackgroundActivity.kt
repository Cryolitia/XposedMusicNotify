package cn.nexus6p.QQMusicNotify

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class BackgroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moveTaskToBack(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        this.finish()
        return super.dispatchTouchEvent(ev)
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

}