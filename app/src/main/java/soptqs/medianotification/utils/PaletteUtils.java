package soptqs.medianotification.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.palette.graphics.Palette;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PaletteUtils {

    public static Palette getPalette(Context context, Bitmap bitmap) {
        if (bitmap != null) return Palette.from(bitmap).generate();
        else return null;
    }

    public static Palette.Swatch getSwatch(Context context, Palette palette) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (palette == null)
            return new Palette.Swatch(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.WHITE), 1);

        Palette.Swatch swatch = null;
        switch (prefs.getInt(PreferenceUtils.PREF_COLOR_METHOD, PreferenceUtils.COLOR_METHOD_DOMINANT)) {
            case PreferenceUtils.COLOR_METHOD_DOMINANT:
                swatch = palette.getDominantSwatch();
                break;
            case PreferenceUtils.COLOR_METHOD_PRIMARY:
                swatch = getBestPaletteSwatchFrom(palette);
                break;
            case PreferenceUtils.COLOR_METHOD_VIBRANT:
                swatch = palette.getVibrantSwatch();
                break;
            case PreferenceUtils.COLOR_METHOD_MUTED:
                swatch = palette.getMutedSwatch();
                break;
            case PreferenceUtils.COLOR_METHOD_PHONOGRAPH:
                swatch = getHighestPopulationSwatch(palette.getSwatches());
                break;
        }

        if (swatch == null)
            swatch = new Palette.Swatch(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.WHITE), 1);

        return swatch;
    }



    @ColorInt
    public static int getTextColor(Context context, Palette palette, Palette.Swatch swatch) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean(PreferenceUtils.PREF_HIGH_CONTRAST_TEXT, false) || prefs.getBoolean(PreferenceUtils.PREF_NOTIFICATION_STYLE2, false)) {
            if (ColorUtils.isColorLight(swatch.getRgb()))
                return Color.BLACK;
            else return Color.WHITE;
        } else {
            int background = swatch.getRgb();
            if (prefs.getBoolean(PreferenceUtils.PREF_INVERSE_TEXT_COLORS, true)) {
                int inverse = -1;
                if (palette != null) {
                    switch (prefs.getInt(PreferenceUtils.PREF_COLOR_METHOD, PreferenceUtils.COLOR_METHOD_DOMINANT)) {
                        case PreferenceUtils.COLOR_METHOD_DOMINANT:
                            inverse = ColorUtils.isColorSaturated(background) ? palette.getMutedColor(-1) : palette.getVibrantColor(-1);
                            break;
                        case PreferenceUtils.COLOR_METHOD_VIBRANT:
                            inverse = palette.getMutedColor(-1);
                            break;
                        case PreferenceUtils.COLOR_METHOD_MUTED:
                            inverse = palette.getVibrantColor(-1);
                            break;
                    }

                    if (inverse != -1)
                        return ColorUtils.getReadableText(inverse, background, 150);
                }

                /*inverse = ColorUtils.getInverseColor(background);
                if (ColorUtils.getDifference(background, inverse) > 120 && ColorUtils.isColorSaturated(background))
                    return ColorUtils.getReadableText(inverse, background, 150);*/
            }
            return ColorUtils.getReadableText(background, background);
        }
    }

    private static Palette.Swatch getBestPaletteSwatchFrom(Palette palette) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null)
                return palette.getVibrantSwatch();
            else if (palette.getMutedSwatch() != null)
                return palette.getMutedSwatch();
            else if (palette.getDarkVibrantSwatch() != null)
                return palette.getDarkVibrantSwatch();
            else if (palette.getDarkMutedSwatch() != null)
                return palette.getDarkMutedSwatch();
            else if (palette.getLightVibrantSwatch() != null)
                return palette.getLightVibrantSwatch();
            else if (palette.getLightMutedSwatch() != null)
                return palette.getLightMutedSwatch();
            else if (!palette.getSwatches().isEmpty())
                return getBestPaletteSwatchFrom(palette.getSwatches());
        }
        return null;
    }

    private static Palette.Swatch getBestPaletteSwatchFrom(List<Palette.Swatch> swatches) {
        if (swatches == null) return null;
        return Collections.max(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                int a = opt1 == null ? 0 : opt1.getPopulation();
                int b = opt2 == null ? 0 : opt2.getPopulation();
                return a - b;
            }
        });
    }

    public static Palette.Swatch getHighestPopulationSwatch(List<Palette.Swatch> swatches) {
        Palette.Swatch highestSwatch = null;
        for (Palette.Swatch swatch : swatches) {
            if (swatch != null) {
                if (highestSwatch == null || swatch.getPopulation() > highestSwatch.getPopulation()) highestSwatch = swatch;
            }
        }
        return highestSwatch;
    }
}