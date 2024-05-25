package com.jmormar.opentasker.util;

import android.graphics.Color;

public class ColorManager {

    public static int darkenColor(int color) {
        float factor = 0.55f;
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}
