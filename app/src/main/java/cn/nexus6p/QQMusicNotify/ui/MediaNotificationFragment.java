package cn.nexus6p.QQMusicNotify.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.bindListSummary;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.jumpToAlipay;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.jumpToLink;

public class MediaNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(cn.nexus6p.QQMusicNotify.R.xml.media_notification);

        bindListSummary((ListPreference) findPreference("pickColorMode"));
        bindListSummary((ListPreference) findPreference("getAlbum"));

        EditTextPreference colorPreference = (EditTextPreference) findPreference("defaultColor");
        colorPreference.setSummary(colorPreference.getText());
        colorPreference.setOnPreferenceChangeListener((preference1, newValue) -> {
            colorPreference.setSummary((CharSequence) newValue);
            GradientDrawable drawable = (GradientDrawable) getContext().getDrawable(R.drawable.color_drawable);
            drawable.setColor(Color.parseColor((String)newValue));
            colorPreference.setIcon(drawable);
            return true;
        });
        GradientDrawable drawable = (GradientDrawable) getContext().getDrawable(R.drawable.color_drawable);
        drawable.setColor(Color.parseColor(colorPreference.getText()));
        findPreference("defaultColor").setIcon(drawable);

        jumpToLink(this,"author3","https://github.com/Soptq",false);
        jumpToAlipay(this,"alipay2","FKX02896EL8F1WS3RV8183");

    }
}
