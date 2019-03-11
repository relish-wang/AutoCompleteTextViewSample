package wang.relish.textsample.util;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * @author zhouqian
 * @since 20160121
 */
public final class AnimatorUtil {

    private static final TimeInterpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator();

    public static ObjectAnimator objectAnimator(View view, String propertyName, float from, float to,
                                                long duration, TimeInterpolator interpolator) {
        final ObjectAnimator objectAnimator = ObjectAnimator
                .ofFloat(view, propertyName, from, to)
                .setDuration(duration);
        objectAnimator.setInterpolator(interpolator == null ? DEFAULT_INTERPOLATOR : interpolator);
        return objectAnimator;
    }
}
