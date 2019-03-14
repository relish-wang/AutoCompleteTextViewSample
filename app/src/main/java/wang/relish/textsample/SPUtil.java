package wang.relish.textsample;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.ArrayRes;

import static wang.relish.textsample.LoginActivity.KEY_HISTORY_ACCOUNTS;

/**
 * SharedPreference工具
 *
 * @author Relish Wang
 * @since 2019/03/06
 */
final class SPUtil {

    private SPUtil() {
        throw new UnsupportedOperationException("SPUtil can not be instantiated.");
    }

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
        SPUtil.putString(KEY_HISTORY_ACCOUNTS, newUserJson);
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
        String usersJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
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
