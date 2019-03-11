package wang.relish.textsample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wang.relish.textsample.R;
import wang.relish.textsample.adapter.AccountAdapter;
import wang.relish.textsample.adapter.ObserverAdapter;
import wang.relish.textsample.adapter.OnItemClickListener;
import wang.relish.textsample.model.User;
import wang.relish.textsample.model.UserResponse;
import wang.relish.textsample.ui.widget.SCAutoCompleteTextView;
import wang.relish.textsample.util.SPUtil;
import wang.relish.textsample.util.SingleInstanceUtils;
import wang.relish.textsample.util.UserUtil;

public class LoginActivity extends BaseActivity implements OnItemClickListener {

    public static final String KEY_HISTORY_ACCOUNTS = "__accounts__";
    //登录账号
    public static final String KEY_USER_INFO_PHONE = "phone";
    //登录密码
    public static final String KEY_USER_INFO_PASSWORD = "password";

    @BindView(R.id.ll_root)
    View rootView;
    @BindView(R.id.act_account)
    SCAutoCompleteTextView mPhoneView;
    @BindView(R.id.iv_account_clear)
    ImageView mAccountClear;
    @BindView(R.id.et_pwd)
    EditText mPasswordView;
    @BindView(R.id.iv_pwd_clear)
    ImageView mPwdClear;
    @BindView(R.id.iv_pwd_visibility)
    ImageView ivPwdVisibility;
    @BindView(R.id.btn_login)
    TextView mBtnLogin;
    AccountAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        addPhoneHistoryList();
    }

    private void addPhoneHistoryList() {
        Observable.create((ObservableOnSubscribe<List<User>>) e -> {
            List<User> users = new ArrayList<>();
            try {
                final String cacheDataJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
                users = SingleInstanceUtils.getGsonInstance().fromJson(cacheDataJson, new TypeToken<List<User>>() {
                }.getType());
            } catch (Exception exception) {
                e.onError(exception);
            }
            e.onNext(users);
            e.onComplete();
        }).subscribe(new ObserverAdapter<List<User>>() {

            @Override
            public void onNext(List<User> userList) {
                final List<User> users = new ArrayList<>();
                if (userList != null) {
                    users.addAll(userList);
                }
                setViewWithInfo(users);
            }
        });
    }

    private void setViewWithInfo(List<User> phones) {
        if (phones == null || phones.isEmpty()) return;
        mAdapter = new AccountAdapter(phones, this);
        mPhoneView.setAdapter(mAdapter);
        Intent intent = getIntent();
        final String phoneFromIntent = intent.getStringExtra(KEY_USER_INFO_PHONE);
        final String passwordFromIntent = intent.getStringExtra(KEY_USER_INFO_PASSWORD);
        if (!(TextUtils.isEmpty(phoneFromIntent) && TextUtils.isEmpty(passwordFromIntent))) {
            mPhoneView.setText(phoneFromIntent);
            mPhoneView.setSelection(phoneFromIntent == null ? 0 : phoneFromIntent.length());
            mPasswordView.setText(passwordFromIntent);
            mPasswordView.setSelection(passwordFromIntent == null ? 0 : passwordFromIntent.length());
        } else {
            final User user = phones.get(phones.size() - 1);
            if (user == null) return;
            final String loginName = user.getPhone();
            mPhoneView.setText(loginName);
            mPhoneView.setSelection(loginName == null ? 0 : loginName.length());
            final String password = user.getPassword();
            mPasswordView.setText(password);
            mPasswordView.setSelection(password == null ? 0 : password.length());
        }
    }

    @OnClick(R.id.btn_login)
    public void login(View v) {
        final String phone = mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入手机号");
            return;
        }
        if (!phone.matches("^1\\d{10}$")) {// 简单判断手机号
            showToast("请输入正确格式的手机号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("请输入密码");
            return;
        }

        // 将已登录的账号存起来
        Observable.create((ObservableOnSubscribe<User>) emitter -> {
            //  查询数据库是否存在这样的数据
            final UserResponse login = UserUtil.login(phone, password);
            if (login.isSuccess()) {
                emitter.onNext(login.getUser());
            } else {
                emitter.onError(new Exception(login.getMsg()));
            }
            emitter.onComplete();
        }).flatMap((Function<User, ObservableSource<Boolean>>) user ->
                Observable.just(UserUtil.saveUserIntoHistory(user)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnComplete(this::dismissLoading)
                .doOnError(e -> showToast(e.getMessage()))
                .subscribe(new ObserverAdapter<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            Toast.makeText(LoginActivity.this, "用户数据保存失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @OnClick(R.id.tv_forget_pwd)
    public void forgetPwd(View v) {
        // TODO 忘记密码
    }

    @Override
    public void onItemClick(View v, User user) {
        final String phone = user.getPhone();
        final String password = user.getPassword();
        mPhoneView.setText(phone);
        mPhoneView.setSelection(phone == null ? 0 : phone.length());
        mPasswordView.setText(password);
        mPasswordView.setSelection(password == null ? 0 : password.length());
        mPhoneView.dismissDropDown();
    }
}
