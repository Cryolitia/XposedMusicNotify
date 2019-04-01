package cn.nexus6p.QQMusicNotify;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("模块状态")
                .setPositiveButton("确定", (dialogInterface, i) -> MainActivity.this.finish());
        if (HookStatue.isEnabled()) builder.setMessage("模块已激活");
        else if (HookStatue.isExpModuleActive(this)) builder.setMessage("太极已激活");
        builder.show();
    }
}
