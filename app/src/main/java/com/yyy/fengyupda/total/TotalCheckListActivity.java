package com.yyy.fengyupda.total;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yyy.fengyupda.R;
import com.yyy.fengyupda.dialog.LoadingDialog;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.interfaces.ResponseListener;
import com.yyy.fengyupda.model.storage.StorageList;
import com.yyy.fengyupda.model.storage.StorageListOrder;
import com.yyy.fengyupda.util.CodeConfig;
import com.yyy.fengyupda.util.NetConfig;
import com.yyy.fengyupda.util.NetParams;
import com.yyy.fengyupda.util.NetUtil;
import com.yyy.fengyupda.util.SharedPreferencesHelper;
import com.yyy.fengyupda.util.StringUtil;
import com.yyy.fengyupda.util.Toasts;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TotalCheckListActivity extends Activity {
    private final static String TAG = "StorageListActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rv_storage)
    RecyclerView rvStorage;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    SharedPreferencesHelper preferencesHelper;
    String userID;
    String url;

    TotalCheckAdapter storageAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<StorageListOrder> list;
    int listPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_list);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        userID = (String) preferencesHelper.getSharedPreference("userid", "");
        init();
    }

    private void init() {
//                String head = (String) preferencesHelper.getSharedPreference("url", "");
//        if (StringUtil.isNotEmpty(head)) {
//            url = head + "/" + NetConfig.Pda_Method;
//        }
        url = NetConfig.url + NetConfig.Pda_Method;

        list = new ArrayList<>();
        tvTitle.setText("盘点单");
//        url = NetConfig.url + NetConfig.Pda_Method;
        ivRight.setImageResource(R.mipmap.icon_add);
        layoutManager = new LinearLayoutManager(this);
        rvStorage.setLayoutManager(layoutManager);
        getData();
    }

    /**
     * 获取列表数据
     */
    private void getData() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {

                    StorageList storageList = new Gson().fromJson(string, StorageList.class);
                    if (storageList.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.cancelDialogForLoading();
                                setSuccess();
                                list.clear();
                                list.addAll(storageList.getDataset().getListProductM());
                                setData();
                            }
                        });
                    } else {
                        failLoad(storageList.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failLoad("加载失败");
                }
            }

            @Override
            public void onFail(IOException e) {
                failLoad("加载失败");
            }
        });

    }

    /**
     * 加载列表数据
     */
    private void setData() {
        if (storageAdapter != null) {
            storageAdapter.notifyDataSetChanged();
            Log.e(TAG, list.size() + "");
        } else {
            storageAdapter = new TotalCheckAdapter(this, list);
            storageAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    listPosition = position;
                    String data = new Gson().toJson(list.get(position));
                    Intent intent = new Intent();
                    intent.putExtra("data", data);
                    intent.setClass(TotalCheckListActivity.this, TotalCheckDetailActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            rvStorage.setAdapter(storageAdapter);
        }
    }


    /**
     * 获取列表数据参数
     *
     * @return
     */
    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("sTableName", "MMProductCheckM"));
        params.add(new NetParams("otype", "GetListProductM"));
        params.add(new NetParams("sUserID", userID));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        return params;
    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                listPosition = -1;
                Intent intent = new Intent();
                intent.setClass(TotalCheckListActivity.this, TotalCheckDetailActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    /**
     * 加载失败
     *
     * @param message
     */
    private void loadFail(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.cancelDialogForLoading();
                Toasts.showShort(TotalCheckListActivity.this, message);
            }
        });
    }

    /**
     * 加载失败
     *
     * @param message
     */
    private void failLoad(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.cancelDialogForLoading();
                Toasts.showShort(TotalCheckListActivity.this, message);
                setEmpty();
            }
        });
    }

    /**
     * 设置加载失败布局
     */
    private void setEmpty() {
        flEmpty.setVisibility(View.VISIBLE);
        rvStorage.setVisibility(View.GONE);
        tvEmpty.setText(getString(R.string.refresh));
    }

    /**
     * 设置加载成功布局
     */
    private void setSuccess() {
        flEmpty.setVisibility(View.GONE);
        rvStorage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, resultCode + "");
        if (resultCode == CodeConfig.DELETECODE) {
            if (listPosition != -1) {
                try {
                    list.remove(listPosition);
                    storageAdapter.notifyItemRemoved(listPosition);
                    storageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    getData();
                }
            }
        } else if (resultCode == CodeConfig.REFRESHCODE) {
            getData();
        }
    }
}
