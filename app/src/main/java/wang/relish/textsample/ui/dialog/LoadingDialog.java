package wang.relish.textsample.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import wang.relish.textsample.R;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public class LoadingDialog extends Dialog {


    public LoadingDialog(@NonNull Context context) {
        super(context,R.style.Dialog_Loading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading); Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);

        }
        setCanceledOnTouchOutside(false);
    }

    public synchronized void show() {
        try {
            super.show();
        } catch (Exception ignore) {
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception ignore) {
        }
    }
}
