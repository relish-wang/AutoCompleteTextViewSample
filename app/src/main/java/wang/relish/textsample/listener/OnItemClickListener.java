package wang.relish.textsample.listener;

import android.view.View;


import wang.relish.textsample.model.User;

/**
 * @author Relish Wang
 * @since 2018/12/12
 */

public interface OnItemClickListener {
    void onItemClick(View v, User user);
}
