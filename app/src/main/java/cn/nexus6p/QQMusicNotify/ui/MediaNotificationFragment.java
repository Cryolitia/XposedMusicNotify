package cn.nexus6p.QQMusicNotify.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;

import cn.nexus6p.QQMusicNotify.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.jumpToAlipay;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.jumpToLink;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.setWorldReadable;

public class MediaNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(cn.nexus6p.QQMusicNotify.R.xml.media_notification);

        String[] strings = getContext().getResources().getStringArray(R.array.pickColorMode);
        ListPreference preference = (ListPreference) findPreference("colorMethod");
        preference.setSummary(preference.getEntry());
        preference.setOnPreferenceChangeListener((preference1, newValue) -> {
            preference.setSummary(strings[Integer.parseInt(newValue.toString())]);
            return true;
        });

        EditTextPreference colorPreference = (EditTextPreference) findPreference("customColor");
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
        findPreference("customColor").setIcon(drawable);

        jumpToLink(this,"author3","https://github.com/Soptq",false);
        jumpToAlipay(this,"alipay2","FKX02896EL8F1WS3RV8183");

        setWorldReadable(getActivity());

    }
}
