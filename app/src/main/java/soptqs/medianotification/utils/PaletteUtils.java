package soptqs.medianotification.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.palette.graphics.Palette;

import java.util.Collections;
import java.util.List;

import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;

public class PaletteUtils {

    public static Palette getPalette(Bitmap bitmap) {
        if (bitmap != null) return Palette.from(bitmap).generate();
        else return null;
    }

    public static Palette.Swatch getSwatch(Palette palette, Context context) {
        SharedPreferences prefs = PreferenceUtil.getPreference(context);

        if (palette == null)
            return new Palette.Swatch(Color.parseColor(prefs.getString(PreferenceUtils.PREF_CUSTOM_COLOR, "#FFFFFF")), 1);

        Palette.Swatch swatch = null;
        switch (Integer.parseInt(prefs.getString(PreferenceUtils.PREF_COLOR_METHOD, "0"))) {
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
            swatch = new Palette.Swatch(Color.parseColor(prefs.getString(PreferenceUtils.PREF_CUSTOM_COLOR, "#FFFFFF")), 1);

        return swatch;
    }


    @ColorInt
    public static int getTextColor(Palette palette, Palette.Swatch swatch, Context context) {
        SharedPreferences prefs = PreferenceUtil.getPreference(context);
        if (prefs.getBoolean(PreferenceUtils.PREF_HIGH_CONTRAST_TEXT, false)) {
            if (ColorUtils.isColorLight(swatch.getRgb()))
                return Color.BLACK;
            else return Color.WHITE;
        } else {
            int background = swatch.getRgb();
            if (prefs.getBoolean(PreferenceUtils.PREF_INVERSE_TEXT_COLORS, true)) {
                int inverse = -1;
                if (palette != null) {
                    switch (Integer.parseInt(prefs.getString(PreferenceUtils.PREF_COLOR_METHOD, "0"))) {
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
        return Collections.max(swatches, (opt1, opt2) -> {
            int a = opt1 == null ? 0 : opt1.getPopulation();
            int b = opt2 == null ? 0 : opt2.getPopulation();
            return a - b;
        });
    }

    public static Palette.Swatch getHighestPopulationSwatch(List<Palette.Swatch> swatches) {
        Palette.Swatch highestSwatch = null;
        for (Palette.Swatch swatch : swatches) {
            if (swatch != null) {
                if (highestSwatch == null || swatch.getPopulation() > highestSwatch.getPopulation())
                    highestSwatch = swatch;
            }
        }
        return highestSwatch;
    }
}