package wang.relish.textsample.util;

import android.content.Context;

import java.util.Random;

import androidx.annotation.ArrayRes;
import wang.relish.textsample.R;

/**
 * @author Relish Wang
 * @since 2019/03/13
 */
public class NameFactory {

    public static String produceName(Context context) {
        return familyName(context) + firstName(context);
    }

    private static String familyName(Context context) {
        return name(context, R.array.family_names);
    }

    private static String firstName(Context context) {
        return name(context, R.array.first_names);
    }

    private static String name(Context context, @ArrayRes int resId) {
        String[] name = context.getResources().getStringArray(resId);
        return name[new Random().nextInt(name.length)];
    }
}
