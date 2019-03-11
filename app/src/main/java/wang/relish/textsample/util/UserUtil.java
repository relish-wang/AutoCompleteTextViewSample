package wang.relish.textsample.util;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.util.Iterator;
import java.util.List;

import wang.relish.textsample.model.User;
import wang.relish.textsample.model.UserResponse;

import static wang.relish.textsample.ui.activity.LoginActivity.KEY_HISTORY_ACCOUNTS;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public final class UserUtil {

    private static final char[] NUMS = "零一二三四五六七八九".toCharArray();

    /**
     * [模拟网络请求]登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 模拟的网络接口返回结果
     */
    public static UserResponse login(String phone, String password) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String usersJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
        List<User> users = SingleInstanceUtils.getGsonInstance().fromJson(usersJson, new TypeToken<List<User>>() {
        }.getType());
        for (User user : users) {
            if (TextUtils.equals(phone, user.getPhone())) {
                if (TextUtils.equals(password, user.getPassword())) {
                    return new UserResponse(user);
                } else {
                    return new UserResponse("密码错误");
                }
            }
        }
        // 内置10个账号
        if (phone.matches("^1351111222\\d$") && password.matches("^123456$")) {
            return new UserResponse(new User(phone, password, "李" + NUMS[phone.charAt(phone.length() - 1) - '0']));
        }
        return new UserResponse("用户名不存在");
    }

    /**
     * [模拟网络请求]登录后保存信息
     *
     * @param user 当前登录的用户信息
     * @return 是否保存成功
     */
    public static boolean saveUserIntoHistory(User user) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String cacheDataJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
        List<User> users = SingleInstanceUtils.getGsonInstance().fromJson(cacheDataJson, new TypeToken<List<User>>() {
        }.getType());
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User next = iterator.next();
            if (TextUtils.equals(next.getPhone(), user.getPhone())) {
                iterator.remove();
            }
        }
        users.add(0, user);
        String newUserJson = SingleInstanceUtils.getGsonInstance().toJson(users);
        return SPUtil.putString(KEY_HISTORY_ACCOUNTS, newUserJson);
    }

    /**
     * [模拟网络请求]获取账号信息
     *
     * @return 已登录的账号信息
     */
    public static List<User> getAllAccounts() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String usersJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
        return SingleInstanceUtils.getGsonInstance().fromJson(usersJson, new TypeToken<List<User>>() {
        }.getType());
    }
}
