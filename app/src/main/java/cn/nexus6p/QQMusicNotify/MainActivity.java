package cn.nexus6p.QQMusicNotify;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.SwitchPreferenceCompat;

import org.jetbrains.annotations.NotNull;
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
import cn.nexus6p.QQMusicNotify.Fragment.SettingsFragment;
import cn.nexus6p.QQMusicNotify.R;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getJsonFromInternet;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.setWorldReadable;

@Keep
public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    boolean shouldCheckUpdate = true;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        setWorldReadable(this);

        //if (savedInstanceState!=null) shouldCheckUpdate = savedInstanceState.getBoolean("shouldCheckUpdate",true);

        boolean isNightMode = getSharedPreferences(BuildConfig.APPLICATION_ID+"_preferences", Context.MODE_PRIVATE).getBoolean("forceNight",false);
        int nightMode = isNightMode ? AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        @ColorInt int colorInt = Color.parseColor(isNightMode?"#212121":"#F5F5F5");

        mToolbar = findViewById(R.id.toolbar_preference);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setBackgroundColor(colorInt);
        setSupportActionBar(mToolbar);
        getWindow().setStatusBarColor(Color.parseColor(isNightMode?"#212121":"#FAFAFA"));
        View docker = getWindow().getDecorView();
        int ui = docker.getSystemUiVisibility();
        if (!isNightMode) {
            ui |=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        docker.setSystemUiVisibility(ui);
        //https://blog.csdn.net/polo2044/article/details/81708196

        boolean needRecreat = getDelegate().getLocalNightMode() != nightMode;
        if (needRecreat) {
            getDelegate().setLocalNightMode(nightMode);
            recreate();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment settingsFragment = fragmentManager.findFragmentByTag("settingsFragment");
        if (settingsFragment==null) settingsFragment = new SettingsFragment();
        if (settingsFragment.isAdded()) fragmentManager.beginTransaction().show(settingsFragment);
        else getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, settingsFragment,"settingsFragment")
                .commit();

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
            else checkUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            checkUpdate();
        }

        setWorldReadable(this);

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
            checkUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            checkUpdate();
        }
    }
    /*
    作者：HiWorldNice
    来源：CSDN
    原文：https://blog.csdn.net/pbm863521/article/details/78811250
    版权声明：本文为博主原创文章，转载请附上博文链接！*/

    private void checkUpdate() {
        if (shouldCheckUpdate) {
            shouldCheckUpdate = false;
            getJsonFromInternet(this,false);
        }
    }

}