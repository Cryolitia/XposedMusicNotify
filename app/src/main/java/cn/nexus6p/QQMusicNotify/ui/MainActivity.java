package cn.nexus6p.QQMusicNotify.ui;

import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cn.nexus6p.QQMusicNotify.R;

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
    }

    @Override
    public void onBackPressed() {
        if( !getFragmentManager().popBackStackImmediate() ) super.onBackPressed();
    }

}