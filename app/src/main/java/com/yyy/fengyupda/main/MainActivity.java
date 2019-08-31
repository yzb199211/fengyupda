package com.yyy.fengyupda.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fengyupda.R;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.output.OutputListActivity;
import com.yyy.fengyupda.storage.StorageListActivity;
import com.yyy.fengyupda.total.TotalCheckListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    MenuUsualAdapter menuUsualAdapter;
//    SharedPreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
//        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
//        Log.e(TAG, (String) preferencesHelper.getSharedPreference("userid", ""));
        ivBack.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.app_name1));
        setMenus();
    }

    private void setMenus() {
        List<Menu> list = new ArrayList<>();
        list.add(new Menu(1, R.mipmap.icon_input, "入库单"));
        list.add(new Menu(2, R.mipmap.icon_output, "出库单"));
        list.add(new Menu(3, R.mipmap.icon_statistics, "盘点单"));
        menuUsualAdapter = new MenuUsualAdapter(list, this);
        rvMenu.setAdapter(menuUsualAdapter);
        NoScrollGvManager manager = new NoScrollGvManager(this, 4);
        manager.setAutoMeasureEnabled(true);
        rvMenu.setHasFixedSize(true);
        manager.setScrollEnabled(false);
        rvMenu.setLayoutManager(manager);
        menuUsualAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goNext(list.get(position).getId());
            }
        });
    }

    private void goNext(int id) {
        Intent intent = new Intent();
        switch (id) {
            case 1:
                intent.setClass(this, StorageListActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent.setClass(this, OutputListActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent.setClass(this, TotalCheckListActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

}
