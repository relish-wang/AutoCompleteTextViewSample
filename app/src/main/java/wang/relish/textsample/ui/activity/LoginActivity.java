package wang.relish.textsample.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import wang.relish.textsample.listener.ClearTextWatcher;
import wang.relish.textsample.listener.OnItemClickListener;
import wang.relish.textsample.model.User;
import wang.relish.textsample.model.UserResponse;
import wang.relish.textsample.ui.widget.SCAutoCompleteTextView;
import wang.relish.textsample.util.AnimatorUtil;
import wang.relish.textsample.util.SPUtil;
import wang.relish.textsample.util.SingleInstanceUtils;
import wang.relish.textsample.util.UserUtil;
import wang.relish.textsample.util.keyboard.GlobalLayoutListener;
import wang.relish.textsample.util.keyboard.PixelUtil;
import widget.AutoCompleteHack;

public class LoginActivity extends BaseActivity implements OnItemClickListener, SCAutoCompleteTextView.OnShowWindowListener {

    public static final String KEY_HISTORY_ACCOUNTS = "__accounts__";
    //登录账号
    public static final String KEY_USER_INFO_PHONE = "phone";
    //登录密码
    public static final String KEY_USER_INFO_PASSWORD = "password";
    public static final int KEYBOARD_CHANGE = 0xebad;

    @BindView(R.id.ll_root)
    View rootView;
    @BindView(R.id.act_account)
    SCAutoCompleteTextView mPhoneView;
    @BindView(R.id.iv_text_clear)
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

    /**
     * 打开登录页
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mPhoneView.addTextChangedListener(new ClearTextWatcher(mPhoneView, mAccountClear));
        mPasswordView.addTextChangedListener(new ClearTextWatcher(mPasswordView, mPwdClear));
        mPhoneView.setOnShowWindowListener(this);
        mPhoneView.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mPhoneView.getText().toString())) {
                if (mAdapter != null && mAdapter.getCount() > 0) {
                    mPhoneView.showDropDown();
                }
            }
        });
        mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ivPwdVisibility.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_password_invisible));
        rootView.post(() -> rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new GlobalLayoutListener(rootView, (isShow, map) -> {
                    mHandler.removeMessages(KEYBOARD_CHANGE);
                    updateKeyboardHeight(isShow, map);
                    mIsShow = isShow;
                    mHandler.sendEmptyMessageDelayed(KEYBOARD_CHANGE, 100);

                })));
        addPhoneHistoryList();
    }

    private void addPhoneHistoryList() {
        Observable.create((ObservableOnSubscribe<List<User>>) e -> {
            List<User> users = new ArrayList<>();
            try {
                final String cacheDataJson = SPUtil.getString(KEY_HISTORY_ACCOUNTS, "[]");
                users = SingleInstanceUtils
                        .getGsonInstance()
                        .fromJson(cacheDataJson, new TypeToken<List<User>>() {
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
                            showToast("用户数据保存失败");
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
        showToast("TODO 忘记密码");
    }

    @OnClick(R.id.tv_sign_up)
    public void signUp(View v) {
        RegisterActivity.start(v.getContext());
    }

    private boolean isPwdVisible = false;

    @OnClick(R.id.iv_pwd_visibility)
    void switchPwdVisibility() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_password_invisible);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
        if (isPwdVisible) {
            mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            int color = Color.parseColor("#C8CBCF");
            DrawableCompat.setTint(wrappedDrawable, color);
        } else {
            mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            int color = ContextCompat.getColor(this, R.color.colorAccent);
            DrawableCompat.setTint(wrappedDrawable, color);
        }
        ivPwdVisibility.setImageDrawable(wrappedDrawable);
        isPwdVisible = !isPwdVisible;
    }


    @OnClick(R.id.act_account)
    public void showDropDown() {
        mPhoneView.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mPhoneView.getText().toString())) {
                if (mAdapter != null && mAdapter.getCount() > 0) {
                    mPhoneView.showDropDown();
                }
            }
        });
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

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mIsShow) {
                // 用100ms隐藏弹窗, 用300ms做下滑动画, 再过50ms显示弹窗
                if (mPhoneView.isPopupShowing()) {
                    mPhoneView.dismissDropDown();
                }
                mPhoneView.post(new Runnable() {
                    @Override
                    public void run() {
                        mPhoneView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animatorFromY2Y(mOldY, 0);// 回归初始状态
                                // 隐藏键盘的时候 不再展示window
                            }
                        }, 100);
                    }
                });
            } else {
                if (mPhoneView.isFocused() && mAdapter != null && mAdapter.getCount() > 0) {
                    mPhoneView.showDropDown();
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPhoneView.isPopupShowing()) {
            mPhoneView.dismissDropDown();
        }
    }

    float mKeyBoardHeight;
    float mScreenHeight;// 这个值已经减去了屏幕的高度

    private void updateKeyboardHeight(boolean isShow, Map<String, Map<String, Object>> map) {
        if (isShow) {
            if (map != null) {
                Map<String, Object> endCoordinates = map.get("endCoordinates");
                if (endCoordinates != null) {
                    Object height = endCoordinates.get("height");
                    if (height != null && height instanceof Number) {
                        mKeyBoardHeight = PixelUtil.toPixelFromDIP(((Number) height).floatValue());
                    }
                    Object screenY = endCoordinates.get("screenY");
                    if (screenY != null && screenY instanceof Number) {
                        mScreenHeight = PixelUtil.toPixelFromDIP(((Number) screenY).floatValue());
                    }
                }
            }
        } else {
            mKeyBoardHeight = 0;
        }
    }

    boolean mIsShow = false;

    float mHeightNeeded = -1;

    @Override
    public boolean beforeShow() {
        // 屏幕没空间了 或 键盘收起来了
        if (Math.abs(mScreenHeight) < 0.1 || Math.abs(mKeyBoardHeight) < 0.1) {
            return false; // 键盘收起且需要滑动的页面的的时候拒绝展示PopupWindow
        }
        // 没数据的时候 不显示
        if (mAdapter == null || mAdapter.getCount() == 0) return false;
        mHeightNeeded = AutoCompleteHack.setListItemMaximum(mPhoneView, 3);
        Rect rect = getLocation(mPhoneView);
        float freeHeightInFact = mScreenHeight/*这个屏幕高度已经减去mKeyBoardHeight了*/ - rect.bottom - PixelUtil.toPixelFromDIP(2)/*android:dropDownVerticalOffset="2dp"*/;
        if (freeHeightInFact >= mHeightNeeded - 15) return true;// 误差大概在10.75左右
        animatorFromY2Y(mOldY, -(mHeightNeeded - freeHeightInFact));
        return true;
    }

    // 获取View在屏幕上的位置
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


    float mOldY = 0;

    private void animatorFromY2Y(float oldY, float newY) {
        ObjectAnimator animator = AnimatorUtil.objectAnimator(
                rootView,
                "translationY",
                oldY,
                newY,
                300,
                null);
        animator.start();
        mOldY = newY;
    }

}
