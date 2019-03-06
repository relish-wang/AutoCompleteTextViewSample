package wang.relish.textsample.model;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public class UserResponse {

    private boolean success;
    private String msg;
    private User user;

    public UserResponse(String msg) {
        this.success = false;
        this.msg = msg;
    }

    public UserResponse(User user) {
        this.success = true;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
