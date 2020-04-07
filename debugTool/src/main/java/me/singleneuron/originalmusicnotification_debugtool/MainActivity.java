package me.singleneuron.originalmusicnotification_debugtool;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.topjohnwu.superuser.Shell;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends Activity {

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        TextView textView = findViewById(R.id.textView2);
        findViewById(R.id.Button1).setOnClickListener(v -> {
            try {
                String string = getSupportPackages();
                JSONArray jsonArray = new JSONArray(string);
                textView.setText(jsonArray.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                textView.setText(stringWriter.toString());
            }
        });
        findViewById(R.id.Button2).setOnClickListener(v -> {
            callBackgroundActivity();
        });
        try {
            Shell.Result result = Shell.sh("su --version").exec();
            writeToTextview("su --version: " + (result.isSuccess() ? result.getOut().toString() : result.getErr().toString()));
            writeToTextview("taichi_magisk: " + System.getProperty("taichi_magisk"));
            Shell.Result result2 = Shell.su("getenforce").exec();
            writeToTextview("SeLinux (real statue go by root):" + (result2.isSuccess() ? result2.getOut().toString() : result2.getErr().toString()));
            writeToTextview("---------------");
            toHookOnInit();
            toHook();
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            writeToTextview(stringWriter.toString());
        }
    }

    public void toHook() {
        writeToTextview("原生音乐通知未找到");
        Log.d("XposedMusicNotify_DebugTool", "原生音乐通知未找到");
    }

    public void toHookOnInit() {
        writeToTextview("初始化时原生音乐通知未找到");
        Log.d("XposedMusicNotify_DebugTool", "原生音乐通知未附加到Application");
    }

    public void writeToTextview(String string) {
        textView.setText(textView.getText() + string + "\n\n");
    }

    private String getSupportPackages() {
        Bundle bundle = getBundle("content_provider_json", "packages");
        return bundle.getString("bundle_key_json_string");
    }

    private Bundle getBundle(String position, String key) {
        try {
            ContentResolver contentResolver = this.getContentResolver();
            Uri uri = Uri.parse("content://cn.nexus6p.QQMusicNotify.provider/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, position, key, null);
            } catch (RuntimeException e) {
                callBackgroundActivity();
            }
            if (result == null) {
                result = contentResolver.call(uri, position, key, null);
            }
            if (result == null) {
                return null;
            }
            return result;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private void callBackgroundActivity() {
        try {
            Intent intent = new Intent();
            intent.setClassName("cn.nexus6p.QQMusicNotify", "cn.nexus6p.QQMusicNotify.BackgroundActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }

}
