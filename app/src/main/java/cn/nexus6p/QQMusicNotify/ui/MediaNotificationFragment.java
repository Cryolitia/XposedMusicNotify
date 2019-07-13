package cn.nexus6p.QQMusicNotify.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.bindEditTextSummary;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.bindListSummary;

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
    }
}
