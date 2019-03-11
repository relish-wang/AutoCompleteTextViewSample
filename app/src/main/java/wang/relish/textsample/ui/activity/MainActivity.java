package wang.relish.textsample.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wang.relish.textsample.R;
import wang.relish.textsample.adapter.ObserverAdapter;
import wang.relish.textsample.model.User;
import wang.relish.textsample.util.UserUtil;

/**
 * 主页
 *
 * @author Relish Wang
 * @since 2019/3/6
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_accounts)
    RecyclerView mRvAccounts;
    UserAdapter mAdapter;
    List<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new UserAdapter();
        mRvAccounts.setAdapter(mAdapter);
        mRvAccounts.setLayoutManager(new LinearLayoutManager(this));
        initData();
    }

    private void initData() {
        Observable.create((ObservableOnSubscribe<List<User>>) emitter -> {
            Thread.sleep(3000);
            List<User> allAccounts = UserUtil.getAllAccounts();
            emitter.onNext(allAccounts);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnComplete(this::dismissLoading)
                .doOnError(e -> showToast(e.getMessage()))
                .subscribe(new ObserverAdapter<List<User>>() {

                    @Override
                    public void onNext(List<User> users) {
                        mUsers = users;
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @OnClick(R.id.btn_logout)
    public void logout(View v){
        LoginActivity.logout(this);
    }

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_user, parent, false);
            return new ViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final User user = mUsers.get(position);
            if (user == null) return;
            holder.tvName.setText(user.getName());
            holder.tvPhone.setText(user.getPhone() + "/" + user.getPassword());
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_name)
            TextView tvName;
            @BindView(R.id.tv_phone)
            TextView tvPhone;


            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
