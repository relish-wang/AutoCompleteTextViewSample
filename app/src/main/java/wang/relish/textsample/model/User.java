package wang.relish.textsample.model;

import java.io.Serializable;

/**
 * 用户实体类
 * 保存用户账号、姓名、手机号等信息。
 *
 * @author Relish Wang
 * @since 2019/03/05
 */
public class User implements Serializable {

    private String phone;
    private String password;
    private String name;

    private String error;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(String phone, String password, String name) {
        this.phone = phone;
        this.password = password;
        this.name = name;
    }

    public User(String error) {
        this.error = error;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
