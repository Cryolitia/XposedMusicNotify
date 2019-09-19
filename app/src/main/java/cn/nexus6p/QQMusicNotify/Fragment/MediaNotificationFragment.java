package cn.nexus6p.QQMusicNotify.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;

import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.jumpToAlipay;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.jumpToLink;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.preferenceChangeListener;
import static cn.nexus6p.QQMusicNotify. Utils.GeneralUtils.setWorldReadable;

public class MediaNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(cn.nexus6p.QQMusicNotify.R.xml.media_notification);
        setWorldReadable(getActivity());
        String[] strings = getContext().getResources().getStringArray(R.array.pickColorMode);
        ListPreference preference = (ListPreference) findPreference("colorMethod");
        preference.setSummary(preference.getEntry());
        preference.setOnPreferenceChangeListener((preference1, newValue) -> {
            preference.setSummary(strings[Integer.parseInt(newValue.toString())]);
            preferenceChangeListener(preference1,newValue);
            return true;
        });

        findPreference("inverseTextColors").setOnPreferenceChangeListener((preference1, newValue) -> {
            preferenceChangeListener(preference1,newValue);
            return true;
        });

        findPreference("highContrastText").setOnPreferenceChangeListener((preference1, newValue) -> {
            preferenceChangeListener(preference1,newValue);
            return true;
        });

        EditTextPreference colorPreference = (EditTextPreference) findPreference("customColor");
        colorPreference.setSummary(colorPreference.getText());
        colorPreference.setOnPreferenceChangeListener((preference1, newValue) -> {
            colorPreference.setSummary((CharSequence) newValue);
            GradientDrawable drawable = (GradientDrawable) getContext().getDrawable(R.drawable.color_drawable);
            drawable.setColor(Color.parseColor((String)newValue));
            colorPreference.setIcon(drawable);
            preferenceChangeListener(preference1,newValue);
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
