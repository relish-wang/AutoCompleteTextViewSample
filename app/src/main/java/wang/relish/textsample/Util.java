package wang.relish.textsample;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.ArrayRes;

import static wang.relish.textsample.LoginActivity.KEY_HISTORY_ACCOUNTS;

/**
 * @author Relish Wang
 * @since 2019/05/08
 */
public final class Util {


    private Util() {
        throw new UnsupportedOperationException("Util can not be instantiated.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                   View尺寸相关                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 获取View在屏幕上的位置
     *
     * @param v 当前View
     */
    public static Rect getLocation(View v) {
        Rect rect = new Rect();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        rect.left = location[0];
        rect.top = location[1];

        rect.right = rect.left + v.getMeasuredWidth();
        rect.bottom = rect.top + v.getMeasuredHeight();

        return rect;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     动画相关                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final TimeInterpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator();
    public static ObjectAnimator objectAnimator(View view, String propertyName, float from, float to,
                                                long duration, TimeInterpolator interpolator) {
        final ObjectAnimator objectAnimator = ObjectAnimator
                .ofFloat(view, propertyName, from, to)
                .setDuration(duration);
        objectAnimator.setInterpolator(interpolator == null ? DEFAULT_INTERPOLATOR : interpolator);
        return objectAnimator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                               SharedPreferences相关                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static volatile SharedPreferences sInstance;

    static void init(Context context) {
        sInstance = context.getSharedPreferences("sp_user", Context.MODE_PRIVATE);

        List<User> list = getAllAccounts();
        if (list != null && list.size() > 0) return;
        List<User> users = new ArrayList<>();
        // 默认添加10条数据
        for (int i = 0; i < 10; i++) {
            users.add(0, new User("1351111222" + i, "123456", NameFactory.produceName(context)));
        }
        String newUserJson = new Gson().toJson(users);
        Util.putString(KEY_HISTORY_ACCOUNTS, newUserJson);
    }


    static boolean putString(String key, String value) {
        final SharedPreferences.Editor editor = sInstance.edit().putString(key, value);
        return editor.commit();
    }

    static String getString(String key, String defaultValue) {
        return sInstance.getString(key, defaultValue);
    }

    /**
     * 名字生成器
     */
    static class NameFactory {

        static String produceName(Context context) {
            return familyName(context) + firstName(context);
        }

        private static String familyName(Context context) {
            String name = name(context, R.array.family_names);
            return name + (name.length() > 1 ? "·" : "");
        }

        private static String firstName(Context context) {
            return name(context, R.array.first_names);
        }

        private static String name(Context context, @ArrayRes int resId) {
            String[] name = context.getResources().getStringArray(resId);
            return name[new Random().nextInt(name.length)];
        }

    }

    /**
     * [模拟网络请求]获取账号信息
     *
     * @return 已登录的账号信息
     */
    private static List<User> getAllAccounts() {
        String usersJson = Util.getString(KEY_HISTORY_ACCOUNTS, "[]");
        return new Gson().fromJson(usersJson, new TypeToken<List<User>>() {
        }.getType());
    }

    /**
     * 用户实体类
     * 保存用户账号、姓名、手机号等信息。
     *
     * @author Relish Wang
     * @since 2019/03/05
     */
    static class User implements Serializable {

        String phone;
        String password;
        String name;

        User(String phone, String password, String name) {
            this.phone = phone;
            this.password = password;
            this.name = name;
        }

    }
}
