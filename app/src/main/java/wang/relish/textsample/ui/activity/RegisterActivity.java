package wang.relish.textsample.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wang.relish.textsample.R;
import wang.relish.textsample.adapter.ObserverAdapter;
import wang.relish.textsample.listener.ClearTextWatcher;
import wang.relish.textsample.model.User;
import wang.relish.textsample.util.NameFactory;
import wang.relish.textsample.util.UserUtil;

/**
 * 注册
 *
 * @author Relish Wang
 * @since 2019/03/13
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.v_name)
    View mViewName;
    EditText mEtName;
    ImageView mIvNameClear;
    @BindView(R.id.iv_name_generator)
    ImageView mNameDice;
    @BindView(R.id.v_phone)
    View mViewPhone;
    EditText mEtPhone;
    ImageView mIvPhoneClear;

    @BindView(R.id.v_pwd)
    View mVPwd;
    EditText mEtPwd;
    ImageView mIvPwdClear;
    ImageView mIvPwdVisibility;
    @BindView(R.id.v_confirm_pwd)
    View mVConfirmPwd;
    EditText mEtConfirmPwd;
    ImageView mIvConfirmPwdClear;
    ImageView mIvConfirmPwdVisibility;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initViews();
    }

    private boolean isPwdVisible = false;
    private boolean isConfirmPwdVisible = false;

    private void initViews() {
        // 姓名
        mEtName = mViewName.findViewById(R.id.et_text);
        mEtName.setHint("请输入姓名");
        mIvNameClear = mViewName.findViewById(R.id.iv_text_clear);
        mEtName.addTextChangedListener(new ClearTextWatcher(mEtName, mIvNameClear));
        generateName(mNameDice);
        // 手机号
        mEtPhone = mViewPhone.findViewById(R.id.et_text);
        mIvPhoneClear = mViewPhone.findViewById(R.id.iv_text_clear);
        mEtPhone.addTextChangedListener(new ClearTextWatcher(mEtPhone, mIvPhoneClear));
        // 密码
        mEtPwd = mVPwd.findViewById(R.id.et_pwd);
        mIvPwdClear = mVPwd.findViewById(R.id.iv_pwd_clear);
        mIvPwdVisibility = mVPwd.findViewById(R.id.iv_pwd_visibility);
        mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mEtPwd.addTextChangedListener(new ClearTextWatcher(mEtPwd, mIvPwdClear));
        mIvPwdVisibility.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_invisible));
        mIvPwdVisibility.setOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_password_invisible);
            Drawable wrappedDrawable = DrawableCompat.wrap(Objects.requireNonNull(drawable)).mutate();
            if (isPwdVisible) {
                mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                int color = Color.parseColor("#C8CBCF");
                DrawableCompat.setTint(wrappedDrawable, color);
            } else {
                mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                int color = ContextCompat.getColor(v.getContext(), R.color.colorAccent);
                DrawableCompat.setTint(wrappedDrawable, color);
            }
            mIvPwdVisibility.setImageDrawable(wrappedDrawable);
            isPwdVisible = !isPwdVisible;
        });
        // 确认密码
        mEtConfirmPwd = mVConfirmPwd.findViewById(R.id.et_pwd);
        mIvConfirmPwdClear = mVConfirmPwd.findViewById(R.id.iv_pwd_clear);
        mIvConfirmPwdVisibility = mVConfirmPwd.findViewById(R.id.iv_pwd_visibility);
        mEtConfirmPwd.setHint("请确认密码");
        mEtConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mEtConfirmPwd.addTextChangedListener(new ClearTextWatcher(mEtConfirmPwd, mIvConfirmPwdClear));
        mIvConfirmPwdVisibility.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_invisible));
        mIvConfirmPwdVisibility.setOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_password_invisible);
            Drawable wrappedDrawable = DrawableCompat.wrap(Objects.requireNonNull(drawable)).mutate();
            if (isConfirmPwdVisible) {
                mEtConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                int color = Color.parseColor("#C8CBCF");
                DrawableCompat.setTint(wrappedDrawable, color);
            } else {
                mEtConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                int color = ContextCompat.getColor(v.getContext(), R.color.colorAccent);
                DrawableCompat.setTint(wrappedDrawable, color);
            }
            mIvConfirmPwdVisibility.setImageDrawable(wrappedDrawable);
            isConfirmPwdVisible = !isConfirmPwdVisible;
        });
    }

    @OnClick(R.id.iv_name_generator)
    public void generateName(View v) {
        final String name = NameFactory.produceName(this);
        mEtName.setText(name);
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.btn_register)
    public void register(View v) {
        final String name = mEtName.getText().toString();
        final String phone = mEtPhone.getText().toString();
        final String password = mEtPwd.getText().toString();
        final String confirmPassword = mEtConfirmPwd.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入姓名");
            return;
        }
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
        if (!TextUtils.equals(password, confirmPassword)) {
            showToast("两次输入密码不一致");
            return;
        }
        final User user = new User(phone, password, name);
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            Thread.sleep(1000);
            boolean b = UserUtil.saveUserIntoHistory(user);
            emitter.onNext(b);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnComplete(this::dismissLoading)
                .doOnError(e -> showToast(e.getMessage()))
                .subscribe(new ObserverAdapter<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        RegisterSuccessActivity.start(RegisterActivity.this);
                    }
                });
    }
}
