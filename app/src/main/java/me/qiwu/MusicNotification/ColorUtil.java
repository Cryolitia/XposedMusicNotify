package me.qiwu.MusicNotification;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.palette.graphics.Palette;

/**
 * Created by Deng on 2019/2/20.
 */

public class ColorUtil {
    public static int[] getColor(Bitmap bitmap) {
        int backgroundColor = Color.BLACK;
        int textColor = Color.WHITE;
        if (bitmap != null) {
            Palette palette = Palette.from(bitmap).generate();
            Palette.Swatch swatch = palette.getDominantSwatch();
            if (swatch != null) {
                backgroundColor = swatch.getRgb();
                textColor = getTextColor(palette, backgroundColor);
            }
        }
        return new int[]{backgroundColor, textColor};
    }

    private static int getTextColor(Palette palette, int color) {
        boolean isLightColor = isColorLight(color);
        int textColor = isLightColor ? palette.getDarkMutedColor(Color.BLACK) : palette.getLightMutedColor(palette.getLightVibrantColor(Color.WHITE));
        if (getDifference(color, textColor) < 100) {
            textColor = getReadableColor(color, textColor);
        }
        return textColor;
    }

    private static int getReadableColor(int backgroundColor, int textColor) {
        boolean isLight = isColorLight(backgroundColor);
        for (int i = 0; getDifference(textColor, backgroundColor) < 100 && i < 100; i++) {
            textColor = getMixedColor(textColor, isLight ? Color.BLACK : Color.WHITE);
        }
        if (getDifference(textColor, backgroundColor) < 100) {
            return isLight ? Color.BLACK : Color.WHITE;
        }
        return textColor;
    }

    //获取两种颜色的差异
    private static double getDifference(@ColorInt int color1, @ColorInt int color2) {
        double diff = Math.abs(0.299 * (Color.red(color1) - Color.red(color2)));
        diff += Math.abs(0.587 * (Color.green(color1) - Color.green(color2)));
        diff += Math.abs(0.114 * (Color.blue(color1) - Color.blue(color2)));
        return diff;
    }

    //获取两种颜色的混合颜色
    private static int getMixedColor(@ColorInt int color1, @ColorInt int color2) {
        return Color.rgb(
                (Color.red(color1) + Color.red(color2)) / 2,
                (Color.green(color1) + Color.green(color2)) / 2,
                (Color.blue(color1) + Color.blue(color2)) / 2
        );
    }

    private static boolean isColorLight(@ColorInt int color) {
        return getColorDarkness(color) < 0.5;
    }

    private static double getColorDarkness(@ColorInt int color) {
        if (color == Color.BLACK)
            return 1.0;
        else if (color == Color.WHITE || color == Color.TRANSPARENT)
            return 0.0;
        else
            return (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255);
    }

}
