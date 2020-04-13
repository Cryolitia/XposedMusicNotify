package cn.nexus6p.QQMusicNotify;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.nexus6p.QQMusicNotify.Fragment.AppsFragment;
import cn.nexus6p.QQMusicNotify.Fragment.SettingFragment;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;

import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getJsonFromInternet;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getSharedPreferenceOnUI;

@Keep
public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    Toolbar mToolbar;
    boolean shouldCheckUpdate = true;
    public boolean isInNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        findViewById(R.id.content).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content).getRootView(), (v, insets) -> {
            v.setPadding(0, 0, 0, insets.getTappableElementInsets().bottom);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar_preference), (v, insets) -> {
            v.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
            return insets;
        });

        //if (savedInstanceState!=null) shouldCheckUpdate = savedInstanceState.getBoolean("shouldCheckUpdate",true);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        /*switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }*/

        boolean isNightMode = getSharedPreferenceOnUI(this).getBoolean("forceNight", false);
        int nightMode = isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        @ColorInt int colorInt = Color.parseColor(currentNightMode == Configuration.UI_MODE_NIGHT_YES ? "#212121" : isNightMode ? "#212121" : "#F5F5F5");
        isInNightMode = !(currentNightMode == Configuration.UI_MODE_NIGHT_YES || isNightMode);

        mToolbar = findViewById(R.id.toolbar_preference);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setBackgroundColor(colorInt);
        setSupportActionBar(mToolbar);
        getWindow().setStatusBarColor(colorInt);
        View docker = getWindow().getDecorView();
        int ui = docker.getSystemUiVisibility();
        if (!(currentNightMode == Configuration.UI_MODE_NIGHT_YES || isNightMode)) {
            ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ui |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        } else {
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
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
        Fragment settingFragment = fragmentManager.findFragmentByTag("settingFragment");
        if (settingFragment == null) settingFragment = new SettingFragment();
        if (settingFragment.isAdded()) fragmentManager.beginTransaction().show(settingFragment);
        else getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, settingFragment, "settingFragment")
                .commit();

        try {
            if (savedInstanceState != null) {
                if (savedInstanceState.getString("currentFragment").equals("AppsFragment")) {
                    getSupportFragmentManager().findFragmentByTag("appsFragment").onCreate(null);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, getSupportFragmentManager().findFragmentByTag("appsFragment"), "appsFragment")
                            .commit();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        File file = new File(getExternalFilesDir(null) + File.separator + "version.json");
        if (!file.exists()) {
            copyAssetsDir2Phone();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            try {
                if (jsonObject.optInt("code") < 25)
                    new File(getExternalFilesDir(null) + File.separator + "setting.json").delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((!getSharedPreferenceOnUI(this).getBoolean("debugMode", false)) && jsonObject.optInt("code") < BuildConfig.VERSION_CODE)
                copyAssetsDir2Phone();
            else checkUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            checkUpdate();
        }

    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            if (getSupportFragmentManager().findFragmentById(R.id.content_frame).getClass().equals(AppsFragment.class)) {
                bundle.putString("currentFragment", "AppsFragment");
            }
            while (!transaction.isEmpty()) {
                transaction.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame));
            }
            transaction.commit();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.onSaveInstanceState(bundle);
    }

    public void copyAssetsDir2Phone() {
        try {
            String[] fileList = getAssets().list("config");
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("配置文件解压中...");
            builder.setCancelable(false);
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setMax(fileList.length);
            builder.setView(progressBar);
            AlertDialog alertDialog = builder.create();
            new Thread(() -> {
                try {
                    for (String filePath : fileList) {
                        Log.d("copyAssets2Phone", filePath);
                        if (filePath.equals("device_features")) continue;
                        InputStream inputStream = getAssets().open("config/" + filePath);
                        File file = new File(getExternalFilesDir(null) + File.separator + filePath);
                        Log.i("copyAssets2Phone", "file:" + file);
                        if (file.exists()) file.delete();
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
                        runOnUiThread(() -> progressBar.incrementProgressBy(1));
                    }
                    runOnUiThread(() -> {
                        alertDialog.dismiss();
                        Toast.makeText(this, "资源文件解压完毕", Toast.LENGTH_SHORT).show();
                        checkUpdate();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(alertDialog::dismiss);
                }
            }).start();
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
        if ((!getSharedPreferenceOnUI(this).getBoolean("debugMode", false)) && shouldCheckUpdate && !PreferenceUtil.isGooglePlay) {
            shouldCheckUpdate = false;
            getJsonFromInternet(this, false);
        }
    }

    public void reload() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    /*@Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }*/
}