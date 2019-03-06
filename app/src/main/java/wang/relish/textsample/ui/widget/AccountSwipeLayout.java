package wang.relish.textsample.ui.widget;

import android.content.Context;
import android.util.AttributeSet;



/**
 * @author Relish Wang
 * @since 2018/11/29
 */

public class AccountSwipeLayout extends SwipeLinearLayout {
    public AccountSwipeLayout(Context context) {
        super(context);
    }

    public AccountSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountSwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private OnScrollListener mListener;

    public void setOnScrollListener(OnScrollListener mListener) {
        this.mListener = mListener;
    }

    private boolean isExpand = false;

    @Override
    public void scrollAuto(int direction) {
        super.scrollAuto(direction);
        if (direction == DIRECTION_EXPAND) {
            if (mListener != null) {
                mListener.onExpanded(this);
                isExpand = true;
            }
        } else {
            if (mListener != null) {
                mListener.onFolded(this);
                isExpand = false;
            }
        }
    }

    public boolean isExpand() {
        return isExpand;
    }

    public interface OnScrollListener {
        void onExpanded(AccountSwipeLayout asl);

        void onFolded(AccountSwipeLayout asl);
    }
}
