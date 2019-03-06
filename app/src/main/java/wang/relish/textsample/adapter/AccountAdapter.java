package wang.relish.textsample.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import wang.relish.textsample.R;
import wang.relish.textsample.model.User;
import wang.relish.textsample.ui.activity.LoginActivity;
import wang.relish.textsample.ui.widget.AccountSwipeLayout;
import wang.relish.textsample.ui.widget.SwipeLinearLayout;
import wang.relish.textsample.util.SPUtil;
import wang.relish.textsample.util.SingleInstanceUtils;

/**
 * @author Relish Wang
 * @since 2018/11/27
 */

public class AccountAdapter extends BaseAdapter implements SwipeLinearLayout.OnSwipeListener, Filterable {

    private List<User> mAccounts = new ArrayList<>();
    private List<AccountSwipeLayout> mSwipeLinearLayouts = new ArrayList<>();

    private List<User> mOrigin = new ArrayList<>();

    private OnItemClickListener mListener;

    public AccountAdapter(List<User> account, OnItemClickListener l) {
        mOrigin = account;
        this.mAccounts = account;
        mListener = l;
    }

    @Override
    public int getCount() {
        return mAccounts == null ? 0 : mAccounts.size();
    }

    @Override
    public User getItem(int position) {
        return mAccounts == null ? null : mAccounts.size() <= position ? null : mAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        final User item = getItem(position);
        if (item == null) return 0;
        return item.getPhone().hashCode();
    }

    private ForegroundColorSpan foregroundColorSpan;
    private Animation appear, disappear;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (foregroundColorSpan == null) {
            foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context
                    , R.color.highlight_phone));
        }

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final User item = getItem(position);
        if (item == null) return convertView;
        holder.tvName.setText(item.getName());
        holder.tvPhone.setText(makeupHighlightText(item.getPhone()));
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 右侧侧滑出现红色删除按钮
                holder.sll.scrollAuto(SwipeLinearLayout.DIRECTION_EXPAND);
                onDirectionJudged(holder.sll, true);
            }
        });
        holder.sll.setDisable(true);
        holder.sll.scrollTo(0, 0);
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= mAccounts.size()) {
                    return;
                }
                final User user = mAccounts.remove(position);
                if (user == null) return;
                // 去重
                Iterator<User> iterator = mOrigin.iterator();
                List<User> users = new ArrayList<>();
                while (iterator.hasNext()) {
                    User next = iterator.next();
                    if (next == null) {
                        iterator.remove();
                        continue;
                    }
                    if (TextUtils.equals(user.getPhone(), next.getPhone())) {
                        iterator.remove();
                    } else {
                        users.add(next);
                    }
                }
                SPUtil.putString( LoginActivity.KEY_HISTORY_ACCOUNTS, SingleInstanceUtils.getGsonInstance().toJson(users));
                notifyDataSetChanged();
            }
        });
        View.OnClickListener choose = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.sll.isExpand()) {
                    holder.sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
                } else {
                    if (mListener != null) {
                        mListener.onItemClick(v, item);
                    }
                }
            }
        };
        holder.itemChoose.setOnClickListener(choose);
        holder.vItem.setOnClickListener(choose);
        holder.ivDelete.setVisibility(View.VISIBLE);
        holder.bottomLine.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public void onDirectionJudged(SwipeLinearLayout thisSll, boolean isHorizontal) {
        if (!isHorizontal) {
            for (SwipeLinearLayout sll : mSwipeLinearLayouts) {
                if (null == sll) {
                    continue;
                }
                sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
            }
        } else {
            for (SwipeLinearLayout sll : mSwipeLinearLayouts) {
                if (null == sll) {
                    continue;
                }
                if (!sll.equals(thisSll)) {
                    //划开一个sll， 其他收缩
                    sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
                }
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<User> newData = new ArrayList<>();
                if (!TextUtils.isEmpty(constraint)) {
                    for (User data : mOrigin) {
                        final String loginName = data.getPhone();
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
                mAccounts = (List<User>) results.values;
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
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.item_choose)
        View itemChoose;
        @BindView(R.id.sll)
        AccountSwipeLayout sll;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.rl_item)
        View vItem;

        public ViewHolder(View item) {
            this.item = item;
            ButterKnife.bind(this, item);

            sll.setOnSwipeListener(AccountAdapter.this);
            sll.setOnScrollListener(new AccountSwipeLayout.OnScrollListener() {
                @Override
                public void onExpanded(AccountSwipeLayout swipeLayout) {
                    if (ivDelete.getVisibility() == View.GONE) return;
                    if (disappear == null) {
                        disappear = AnimationUtils.loadAnimation(swipeLayout.getContext(), R.anim.account_del_disappear);
                    }
                    ivDelete.startAnimation(disappear);
                    ivDelete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivDelete.setVisibility(View.GONE);
                        }
                    }, 300);
                }

                @Override
                public void onFolded(AccountSwipeLayout swipeLayout) {
                    if (ivDelete.getVisibility() == View.VISIBLE) return;
                    if (appear == null) {
                        appear = AnimationUtils.loadAnimation(swipeLayout.getContext(), R.anim.account_del_appear);
                    }
                    ivDelete.startAnimation(appear);
                    ivDelete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivDelete.setVisibility(View.VISIBLE);
                        }
                    }, 300);
                }
            });
            mSwipeLinearLayouts.add(sll);
        }
    }
}
