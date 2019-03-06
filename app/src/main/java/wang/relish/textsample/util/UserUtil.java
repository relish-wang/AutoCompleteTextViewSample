package wang.relish.textsample.util;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import wang.relish.textsample.model.User;
import wang.relish.textsample.model.UserResponse;
import wang.relish.textsample.ui.activity.LoginActivity;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public final class UserUtil {

    /**
     * 模拟登录的网络请求(同步)
     *
     * @param phone    手机号
     * @param password 密码
     * @return 模拟的网络接口返回结果
     */
    public static UserResponse login(String phone, String password) {
        String usersJson = SPUtil.getString(LoginActivity.KEY_HISTORY_ACCOUNTS, "[]");
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
        return new UserResponse("用户名不存在");
    }
}
