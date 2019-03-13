package wang.relish.textsample.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wang.relish.textsample.R;

/**
 * 注册成功
 *
 * @author Relish Wang
 * @since 2019/03/123
 */
public class RegisterSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_count_down)
    TextView mTvCountDown;

    CountDownTimer timer;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mTvCountDown.setText(getString(R.string.some_mills_later_go_login, "4"));
        timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvCountDown.setText(getString(R.string.some_mills_later_go_login, String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                LoginActivity.start(RegisterSuccessActivity.this);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvCountDown.postDelayed(() -> timer.start(),100);
    }
}
