package wang.relish.textsample;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页
 *
 * @author Relish Wang
 * @since 2019/03/06
 */
public class LoginActivity extends AppCompatActivity {

    public static final String KEY_HISTORY_ACCOUNTS = "__accounts__";

    @BindView(R.id.act_account)
    WXAutoCompleteTextView mPhoneView;
    @BindView(R.id.et_pwd)
    EditText mPasswordView;
    AccountAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏文字颜色及图标为深色
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initData();
        initViews();
    }

    private void initViews() {
        mPhoneView.setOnShowWindowListener(() -> {
            if (mAdapter == null || mAdapter.getCount() == 0) return;
            boolean b = ACTVHeightUtil.setDropDownHeight(mPhoneView, 3);
            Log.d("setDropDownHeight", "setOnShowWindowListener#setDropDownHeight: " + b);
        });
    }

    private void initData() {
        final String cacheDataJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
        List<SPUtil.User> users = new Gson().fromJson(cacheDataJson, new TypeToken<List<SPUtil.User>>() {
        }.getType());
        setViewWithInfo(users == null ? new ArrayList<>() : users);
    }

    /**
     * 设置预填充数据
     *
     * @param users 账号信息
     */
    private void setViewWithInfo(List<SPUtil.User> users) {
        if (users == null) return;
        mAdapter = new AccountAdapter(users, user -> {
            // 点击了某条候选账号，自动填充手机号和密码
            final String phone = user.phone;
            final String password = user.password;
            mPhoneView.setText(phone);
            mPhoneView.setSelection(phone == null ? 0 : phone.length());
            mPasswordView.setText(password);
            mPasswordView.setSelection(password == null ? 0 : password.length());
            mPhoneView.dismissDropDown();
        });
        mPhoneView.setAdapter(mAdapter);
        final SPUtil.User user = users.get(users.size() - 1);
        if (user == null) return;
        final String loginName = user.phone;
//        mPhoneView.setText(loginName);
//        mPhoneView.setSelection(loginName == null ? 0 : loginName.length());
        final String password = user.password;
        mPasswordView.setText(password);
        mPasswordView.setSelection(password == null ? 0 : password.length());
    }

    /**
     * 监听物理返回键，优先关闭候选账号列表窗口
     */
    @Override
    public void onBackPressed() {
        if (mPhoneView != null && mPhoneView.isPopupShowing()) {
            mPhoneView.dismissDropDown();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.btn_login)
    public void test(){
        TestActivity.start(this);
    }
}
