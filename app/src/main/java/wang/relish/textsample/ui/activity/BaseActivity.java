package wang.relish.textsample.ui.activity;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import wang.relish.textsample.ui.dialog.LoadingDialog;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public class BaseActivity extends AppCompatActivity {

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected LoadingDialog mLoadingDialog;


    protected void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    protected void dismissLoading() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }
}
