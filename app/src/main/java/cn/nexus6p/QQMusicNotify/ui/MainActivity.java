package cn.nexus6p.QQMusicNotify.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.R;

import static cn.nexus6p.QQMusicNotify.GeneralUtils.setWorldReadable;

@Keep
public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mToolbar = findViewById(R.id.toolbar_preference);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
        setWorldReadable(this);

        File file = new File(getExternalFilesDir(null) + File.separator +"version.json");
        if (!file.exists()) copyAssetsDir2Phone();

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            if (jsonObject.optInt("code")< BuildConfig.VERSION_CODE) copyAssetsDir2Phone();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setWorldReadable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setWorldReadable(this);
    }

    public void copyAssetsDir2Phone() {
        try {
            String[] fileList = getAssets().list("config");
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(fileList.length);
            progressDialog.setTitle("资源文件解压中");
            progressDialog.show();
            new Thread(() -> {
                try {
                    for (String filePath : fileList) {
                        Log.d("copyAssets2Phone",filePath);
                        if (filePath=="device_features") continue;
                        InputStream inputStream = getAssets().open("config/"+filePath);
                        File file = new File(getExternalFilesDir(null) + File.separator + filePath);
                        Log.i("copyAssets2Phone", "file:" + file);
                        if (!file.exists() || file.length() == 0) {
                            FileOutputStream fos = new FileOutputStream(file);
                            int len = -1;
                            byte[] buffer = new byte[1024];
                            while ((len = inputStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();
                            inputStream.close();
                            fos.close();
                            Log.d("MusicNotify", "文件复制完毕");
                        } else {
                            Log.d("MusicNotify", "文件已存在，无需复制");
                        }
                        runOnUiThread(() -> {
                            progressDialog.incrementProgressBy(1);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(progressDialog::dismiss);
                }
            }).start();
            progressDialog.dismiss();
            Toast.makeText(this,"资源文件解压完毕",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    作者：HiWorldNice
    来源：CSDN
    原文：https://blog.csdn.net/pbm863521/article/details/78811250
    版权声明：本文为博主原创文章，转载请附上博文链接！*/

}