package cn.nexus6p.QQMusicNotify;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

@Keep
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}