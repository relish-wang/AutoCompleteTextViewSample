package wang.relish.textsample.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

/**
 * @author Relish Wang
 * @since 2018/12/13
 */

public class SCAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public SCAutoCompleteTextView(Context context) {
        super(context);
    }

    public SCAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void showDropDown() {
        if (mListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (mListener.beforeShow()) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //延时结束后显示前检查是否还显示
                              if (mListener.beforeShow()){
                                  SCAutoCompleteTextView.super.showDropDown();
                              }
                            }
                        }, 400);// 动画只要执行300ms就够了
                    }
                }
            });
        }
    }

    private OnShowWindowListener mListener;

    public void setOnShowWindowListener(OnShowWindowListener l) {
        mListener = l;
    }

    public interface OnShowWindowListener {
        boolean beforeShow();
    }


}
