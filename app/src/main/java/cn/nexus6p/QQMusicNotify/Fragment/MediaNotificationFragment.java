package cn.nexus6p.QQMusicNotify.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.R;

@Keep
public class MediaNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(cn.nexus6p.QQMusicNotify.R.xml.media_notification);
        String[] strings = getContext().getResources().getStringArray(R.array.pickColorMode);
        ListPreference preference = findPreference("colorMethod");
        preference.setSummary(preference.getEntry());
        preference.setOnPreferenceChangeListener((preference1, newValue) -> {
            preference.setSummary(strings[Integer.parseInt(newValue.toString())]);
            //preferenceChangeListener(preference1,newValue);
            return true;
        });

        /*findPreference("inverseTextColors").setOnPreferenceChangeListener((preference1, newValue) -> {
            preferenceChangeListener(preference1,newValue);
            return true;
        });

        findPreference("highContrastText").setOnPreferenceChangeListener((preference1, newValue) -> {
            preferenceChangeListener(preference1,newValue);
            return true;
        });*/

        EditTextPreference colorPreference = findPreference("customColor");
        colorPreference.setSummary(colorPreference.getText());
        colorPreference.setOnPreferenceChangeListener((preference1, newValue) -> {
            if (!newValue.toString().matches("\\#[0-9a-fA-F]{6}")) {
                Log.d("newValue", newValue.toString());
                Toast.makeText(getActivity(), "请输入16进制RGB颜色", Toast.LENGTH_LONG).show();
                return false;
            }
            colorPreference.setSummary((CharSequence) newValue);
            GradientDrawable drawable = (GradientDrawable) getContext().getDrawable(R.drawable.color_drawable);
            drawable.setColor(Color.parseColor((String) newValue));
            colorPreference.setIcon(drawable);
            //preferenceChangeListener(preference1,newValue);
            return true;
        });
        GradientDrawable drawable = (GradientDrawable) getContext().getDrawable(R.drawable.color_drawable);
        drawable.setColor(Color.parseColor(colorPreference.getText()));
        findPreference("customColor").setIcon(drawable);

    }
}
