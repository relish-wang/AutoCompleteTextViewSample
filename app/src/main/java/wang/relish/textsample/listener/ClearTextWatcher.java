package wang.relish.textsample.listener;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * 用于绑定输入框和清除按钮行为
 *
 * <ul>
 *     <li>1 输入框内容为空时, 不显示清除按钮;输入框存在内容时，显示清除按钮。</li>
 *     <li>2 当清除按钮显示时，点击清除按钮清除输入框的内容</li>
 * </ul>
 *
 * @author Relish Wang
 * @since 2018/12/12
 */
public class ClearTextWatcher implements TextWatcher, View.OnClickListener {

    private EditText editText;
    private View clear;

    public ClearTextWatcher(EditText editText, View clear) {
        this.editText = editText;
        this.clear = clear;
        clear.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        clear.setVisibility(TextUtils.isEmpty(editText.getText().toString()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        editText.setText("");
    }
}