package wang.relish.textsample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView mEtAccount;

    EditText mEtPwd;

    Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtAccount = findViewById(R.id.act_account);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {

    }

    class LoginTask extends AsyncTask<String, Void, String> {

        private String phone;
        private String password;

        public LoginTask(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (TextUtils.isEmpty(phone)) {
                return "请输入手机号";
            }
            if (!password.matches("^1\\d{10}$")) {
                return "请输入正确格式的手机号";
            }
            if (TextUtils.isEmpty(password)) {
                return "请输入密码";
            }
            SystemClock.sleep(1000);
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (string != null) {
                Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }
}
