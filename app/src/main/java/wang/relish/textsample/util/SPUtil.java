package wang.relish.textsample.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference工具
 *
 * @author Relish Wang
 * @since 2019/03/06
 */
public final class SPUtil {

    private SPUtil() {
        throw new UnsupportedOperationException("SPUtil can not be instantiated.");
    }

    private static Context context;

    public static void init(Context context) {
        SPUtil.context = context;
    }

    private static volatile SharedPreferences mInstance;

    private static synchronized SharedPreferences getInstance() {
        synchronized (SharedPreferences.class) {
            if (mInstance == null) {
                synchronized (SharedPreferences.class) {
                    if (mInstance == null) {
                        mInstance = context.getSharedPreferences("sp_user", Context.MODE_PRIVATE);
                    }
                }
            }
        }
        return mInstance;
    }


    public static boolean putString(String key, String value) {
        final SharedPreferences sp = getInstance();
        final SharedPreferences.Editor editor = sp.edit().putString(key, value);
        return editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        final SharedPreferences sp = getInstance();
        return sp.getString(key, defaultValue);
    }

}
