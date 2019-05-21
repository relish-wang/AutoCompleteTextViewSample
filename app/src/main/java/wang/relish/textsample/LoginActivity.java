package wang.relish.textsample;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import wang.relish.textsample.keyboard.GlobalLayoutListener;
import wang.relish.textsample.keyboard.PixelUtil;

/**
 * 登录页
 *
 * @author Relish Wang
 * @since 2019/03/06
 */
public class LoginActivity extends AppCompatActivity {

    public static final String KEY_HISTORY_ACCOUNTS = "__accounts__";

    @BindView(R.id.ll_root)
    View rootView;
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

    private AnimHandler mHandler;

    private void initViews() {
        mHandler = new AnimHandler(this);
        mPhoneView.setOnShowWindowListener(new WXAutoCompleteTextView.OnShowWindowListener() {

            @Override
            public boolean beforeShow() {
                // 屏幕没空间了 或 键盘收起来了
                if (Math.abs(mScreenHeight) < 0.1 || Math.abs(mKeyboardHeight) < 0.1) {
                    return false; // 键盘收起且需要滑动的页面的的时候拒绝展示PopupWindow
                }
                // 没数据的时候 不显示
                if (mAdapter == null || mAdapter.getCount() == 0) return false;
                mHeightNeeded = ACTVHeightUtil.setDropDownHeight(mPhoneView, 3);
                if (mHeightNeeded == -1) return true;

                Log.d(App.TAG, "mHeightNeeded = " + mHeightNeeded);
                Rect rect = Util.getLocation(mPhoneView);
                float freeHeightInFact = mScreenHeight/*这个屏幕高度已经减去mKeyBoardHeight了*/ - rect.bottom - PixelUtil.toPixelFromDIP(10)/*android:dropDownVerticalOffset="2dp"*/;
                Log.d(App.TAG, "freeHeightInFact = " + freeHeightInFact);
                if (freeHeightInFact >= mHeightNeeded - 15) return true;// 误差大概在10.75左右, 不需要执行动画
                animatorFromY2Y(-(mHeightNeeded - freeHeightInFact));// 300ms上移动画
                return true;
            }
        });
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener(rootView,
                (isShow, keyboardHeight, screenWidth, screenHeight) -> {
                    if (isShow) {
                        mKeyboardHeight = keyboardHeight;// 当前键盘高度
                        mScreenHeight = screenHeight;// 当前键盘可用高度
                    } else {
                        mScreenHeight = 0;
                    }
                    Log.d(App.TAG, "mScreenHeight = " + mScreenHeight);
                    mIsShow = isShow;

                    mHandler.removeMessages(AnimHandler.KEYBOARD_CHANGE);
                    mHandler.sendEmptyMessageDelayed(AnimHandler.KEYBOARD_CHANGE, 100);
                }));
    }


    private boolean mIsShow; // 键盘是否展示

    public void handleKeyboardEvent() {
        if (!mIsShow) {
            // 用100ms隐藏弹窗, 用300ms做下滑动画, 再过50ms显示弹窗
            if (mPhoneView.isPopupShowing()) {
                mPhoneView.dismissDropDown();
            }
            animatorFromY2Y(0);// 回归初始状态
        } else {
            if (mPhoneView.isFocused() && mAdapter != null && mAdapter.getCount() > 0) {
                mPhoneView.showDropDown();
            }
        }
    }

    private void initData() {
        final String cacheDataJson = Util.getString(KEY_HISTORY_ACCOUNTS, "[]");
        List<Util.User> users = new Gson().fromJson(cacheDataJson, new TypeToken<List<Util.User>>() {
        }.getType());
        setViewWithInfo(users == null ? new ArrayList<>() : users);
    }

    /**
     * 设置预填充数据
     *
     * @param users 账号信息
     */
    private void setViewWithInfo(List<Util.User> users) {
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
        final Util.User user = users.get(users.size() - 1);
        if (user == null) return;
        final String loginName = user.phone;
//        mPhoneView.setText(loginName);
//        mPhoneView.setSelection(loginName == null ? 0 : loginName.length());
        final String password = user.password;
//        mPasswordView.setText(password);
//        mPasswordView.setSelection(password == null ? 0 : password.length());
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

    private float mKeyboardHeight;
    private float mScreenHeight;// 屏幕可用高度
    private float mHeightNeeded = -1;
    /**
     * 记录动画移动到的位置
     */
    private float mOldY = 0;

    private void animatorFromY2Y(float newY) {
        ObjectAnimator animator = Util.objectAnimator(
                rootView,
                "translationY",
                mOldY,
                newY,
                0,
                null);
        animator.start();
        Log.d(App.TAG, "执行动画: " + mOldY + "->" + newY);
        mOldY = newY;
    }

}

final class AnimHandler extends Handler {

    static final int KEYBOARD_CHANGE = 0xebad;

    private final WeakReference<LoginActivity> aty;

    AnimHandler(LoginActivity aty) {
        this.aty = new WeakReference<LoginActivity>(aty);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case KEYBOARD_CHANGE: {
                LoginActivity activity = aty.get();
                if (activity != null) {
                    activity.handleKeyboardEvent();
                }
                break;
            }
        }
    }
}
