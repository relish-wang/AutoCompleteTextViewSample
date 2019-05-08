package wang.relish.textsample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

/**
 * @author Relish Wang
 * @since 2019/04/09
 */
public class WXAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    public WXAutoCompleteTextView(Context context) {
        super(context);
    }

    public WXAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WXAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void showDropDown() {
        if (mListener != null) {
            if (mListener.beforeShow()) {
                Log.d(App.TAG, "显示候选列表!");
                WXAutoCompleteTextView.super.showDropDown();
            }
        }
    }


    private OnShowWindowListener mListener;

    public void setOnShowWindowListener(OnShowWindowListener l) {
        mListener = l;
    }

    public interface OnShowWindowListener {
        //        void afterShow();
        boolean beforeShow();
    }
}
