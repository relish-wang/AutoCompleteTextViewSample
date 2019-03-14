package wang.relish.textsample;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 登录输入框的下拉候选账号列表
 *
 * @author Relish Wang
 * @since 2018/11/27
 */
class AccountAdapter extends BaseAdapter implements Filterable {

    private List<SPUtil.User> mAccounts;

    private List<SPUtil.User> mOrigin;

    private AccountAdapter.OnAccountChosenListener mListener;

    public AccountAdapter(List<SPUtil.User> account, OnAccountChosenListener l) {
        mOrigin = account;
        this.mAccounts = account;
        mListener = l;
    }

    @Override
    public int getCount() {
        return mAccounts == null ? 0 : mAccounts.size();
    }

    @Override
    public SPUtil.User getItem(int position) {
        return mAccounts == null ? null : mAccounts.size() <= position ? null : mAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        final SPUtil.User item = getItem(position);
        if (item == null) return 0;
        return item.phone.hashCode();
    }

    private ForegroundColorSpan foregroundColorSpan;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (foregroundColorSpan == null) {
            foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context
                    , R.color.colorAccent));
        }

        final AccountAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
            holder = new AccountAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (AccountAdapter.ViewHolder) convertView.getTag();
        }
        final SPUtil.User item = getItem(position);
        if (item == null) return convertView;
        holder.tvName.setText(item.name);
        holder.tvPhone.setText(makeupHighlightText(item.phone));
        View.OnClickListener choose = v -> {
            if (mListener != null) {
                mListener.onAccountChosen(item);
            }
        };
        holder.itemChoose.setOnClickListener(choose);
        holder.vItem.setOnClickListener(choose);
        holder.bottomLine.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<SPUtil.User> newData = new ArrayList<>();
                if (!TextUtils.isEmpty(constraint)) {
                    for (SPUtil.User data : mOrigin) {
                        final String loginName = data.phone;
                        if (!TextUtils.isEmpty(loginName) && loginName.contains(constraint)) {
                            newData.add(data);
                        }
                    }
                } else {
                    newData.addAll(mOrigin);
                }
                results.values = newData;
                results.count = newData.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                mAccounts = (List<SPUtil.User>) results.values;
                mKeyword = (String) constraint;
                notifyDataSetChanged();
            }
        };
    }

    private String mKeyword = "";

    private CharSequence makeupHighlightText(String phone) {
        if (!TextUtils.isEmpty(mKeyword) && !TextUtils.isEmpty(phone)) {
            int nameIndex = phone.indexOf(mKeyword);
            if (nameIndex != -1) {
                SpannableString ssText = new SpannableString(phone);
                ssText.setSpan(foregroundColorSpan, nameIndex, nameIndex + mKeyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return ssText;
            } else {
                return phone;
            }
        } else {
            return phone;
        }
    }

    class ViewHolder {
        View item;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.item_choose)
        View itemChoose;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.rl_item)
        View vItem;

        ViewHolder(View item) {
            this.item = item;
            ButterKnife.bind(this, item);
        }
    }

    /**
     * @author Relish Wang
     * @since 2018/12/12
     */

    public interface OnAccountChosenListener {
        void onAccountChosen(SPUtil.User user);
    }
}