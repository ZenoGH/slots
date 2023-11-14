package com.template;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

interface PropertySetter {
    void setProperty(double value);
}
public class Utils {

    static int dpToPx(int valueInDp) {
        float valueInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueInDp, Resources.getSystem().getDisplayMetrics());
        return (int) valueInPx;
    }

    static void addButtonResponsiveness(View button) {
        PropertySetter setter = value -> {
            button.setScaleX((float) value);
            button.setScaleY((float) value);
        };
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Utils.tweenProperty(1, 0.9f, 0.01, setter);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Utils.tweenProperty(0.9f, 1, 0.01, setter);
                button.performClick();
                return true;
            }
            return false;
        });
    }

    private int playSound(Context context, int resource) {
        return playSoundCustomDuration(context, resource, -1);
    }

    private int playSoundCustomDuration(Context context, int resource, int milliseconds) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resource);
        int duration = mediaPlayer.getDuration();
        if (milliseconds > 0) {
            mediaPlayer.setLooping(milliseconds > duration);
            duration = milliseconds;
        }
        mediaPlayer.start();
        new Handler().postDelayed(() -> {
            mediaPlayer.reset();
            mediaPlayer.release();
        }, duration);
        return duration;
    }
    public static String intToStringWithLeadingZeros(int value, int length) {
        return String.format("%0" + length + "d", value);
    }
    static void tweenProperty(
            double current,
            double target,
            double step,
            PropertySetter propertySetter
    ) {
        int delay = 0;
        int sign = (int)Math.signum(target - current);
        for (double i = current; i * sign <= (target * sign) ; i += sign * step) {
            if (sign == 0) {
                break;
            }
            final double value = i;
            delay += 3;
            new Handler().postDelayed(() -> {
                propertySetter.setProperty(value);
            }, delay);
        }
    }
}
