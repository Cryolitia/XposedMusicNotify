package cn.nexus6p.QQMusicNotify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.Arrays;

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
    public static boolean nowNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        findViewById(R.id.content).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content).getRootView(), (v, insets) -> {
            v.setPadding(0, 0, 0, insets.getTappableElementInsets().bottom);
            return insets;
        });
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar_preference), (v, insets) -> {
            v.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
            return insets;
        });*/

        //if (savedInstanceState!=null) shouldCheckUpdate = savedInstanceState.getBoolean("shouldCheckUpdate",true);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = getSharedPreferenceOnUI(this).getBoolean("forceNight", false);
        int nightMode = isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        @ColorInt int colorInt = ContextCompat.getColor(this, R.color.toolbarBackground);

        //https://blog.csdn.net/maosidiaoxian/article/details/51734895
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(colorInt);

        mToolbar = findViewById(R.id.toolbar_preference);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        //mToolbar.setBackgroundColor(colorInt);
        setSupportActionBar(mToolbar);
        View docker = getWindow().getDecorView();
        int ui = docker.getSystemUiVisibility();
        nowNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES || isNightMode;
        if (!nowNightMode) {
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

        /*if (PreferenceUtil.isGooglePlay) {
            final ArrayList<Integer> noPlayArgs = new ArrayList<Integer>(Arrays.asList(ConnectionResult.SERVICE_DISABLED, ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_INVALID));
            GoogleApiAvailability googleApiAvailability = new GoogleApiAvailability();
            int googleApiStatue = googleApiAvailability.isGooglePlayServicesAvailable(this);
            if (noPlayArgs.contains(googleApiStatue)) PreferenceUtil.isGooglePlay=false;
        }*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment settingFragment = fragmentManager.findFragmentByTag("settingFragment");
        if (settingFragment == null) settingFragment = new SettingFragment();
        if (settingFragment.isAdded())
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out).show(settingFragment);
        else getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.content_frame, settingFragment, "settingFragment")
                .commit();

        try {
            if (savedInstanceState != null) {
                if (savedInstanceState.getString("currentFragment").equals("AppsFragment")) {
                    getSupportFragmentManager().findFragmentByTag("appsFragment").onCreate(null);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                            .replace(R.id.content_frame, getSupportFragmentManager().findFragmentByTag("appsFragment"), "appsFragment")
                            .commit();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (BuildConfig.VERSION_NAME.contains("canary") || BuildConfig.VERSION_NAME.contains("NIGHTLY") || BuildConfig.VERSION_NAME.contains("beta") || BuildConfig.VERSION_NAME.contains("alpha") || BuildConfig.VERSION_NAME.contains("α") || BuildConfig.VERSION_NAME.contains("β"))
            Toast.makeText(this, "您正在使用未经完全测试的版本，使用风险自负\n测试版本不代表最终品质", Toast.LENGTH_LONG).show();

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

        SharedPreferences sharedPreferences = getSharedPreferenceOnUI(this);
        if (sharedPreferences.getBoolean("firstRun",true)) {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("功能说明")
                    .setMessage("播放器\n此设置项将只显示所有允许自定义及更新配置文件的播放器，且其中设置均只用于将其音乐通知更改为系统样式。\n\n全局模式\n此设置项用于将所有系统样式的音乐通知强制更改为原生样式。\n\n以上两项功能相互独立，可只打开任意其中一项或同时打开，同时此两项功能内任何设置及显示的内容都与另一项功能无关。")
                    .setCancelable(false)
                    .setPositiveButton("我已阅读并知悉 (5)", null)
                    .create();
            dialog.show();
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setEnabled(false);
            new Thread(() -> {
                for (int i = 5; i > 0; i--) {
                    int finalI = i;
                    this.runOnUiThread(() -> {
                        button.setText("我已阅读并知悉 (" + finalI + ")");
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.runOnUiThread(() -> {
                    button.setText("我已阅读并知悉");
                });
                this.runOnUiThread(() -> {
                    button.setEnabled(true);
                });
            }).start();
            sharedPreferences.edit().putBoolean("firstRun",false).apply();
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
                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out).remove(getSupportFragmentManager().findFragmentById(R.id.content_frame));
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