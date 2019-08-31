package com.yyy.fengyupda.scan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yyy.fengyupda.R;
import com.yyy.fengyupda.dialog.JudgeDialog;
import com.yyy.fengyupda.dialog.LoadingDialog;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.interfaces.ResponseListener;
import com.yyy.fengyupda.model.storage.StorageScan;
import com.yyy.fengyupda.model.storage.StorageScanBean;
import com.yyy.fengyupda.model.storage.StorageStockPosBean;
import com.yyy.fengyupda.model.storage.StorageStockPosition;
import com.yyy.fengyupda.pick.builder.OptionsPickerBuilder;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectChangeListener;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectListener;
import com.yyy.fengyupda.pick.view.OptionsPickerView;
import com.yyy.fengyupda.util.NetConfig;
import com.yyy.fengyupda.util.NetParams;
import com.yyy.fengyupda.util.NetUtil;
import com.yyy.fengyupda.util.SharedPreferencesHelper;
import com.yyy.fengyupda.util.StringUtil;
import com.yyy.fengyupda.util.Toasts;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanManualActivity extends AppCompatActivity {
    private static final String TAG = "ScanManualActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.rv_scan)
    RecyclerView rvScan;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_stock_pos)
    TextView tvStovkPos;
    @BindView(R.id.fl_stock_pos)
    FrameLayout flStockPos;
    @BindView(R.id.fl_stock)
    FrameLayout flStock;
    @BindView(R.id.et_stock_pos)
    EditText etStockPos;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.rv_total)
    RecyclerView rvTotal;

    OptionsPickerView pvStockPos;

    String url;
    int mainID;
    int stockID;
    int stockSelectedPos = 0;//已选中仓位位置
    String tableName = "";
    int stockPosID = 0;

    boolean isShow = false;
    boolean isDown = false;
    boolean isLoad = false;
    boolean isTotal = false;

    List<StorageScanBean> products;
    List<StorageStockPosBean> stockPosBeans;
    List<StorageScanBean> totals;
    List<StorageScanBean> showList;

    Set<String> codes = new HashSet<>();


    ScanNewAdapter mAdapter;
    ScanTotalAdapter totalAdapter;
    private JudgeDialog deleteDialog;
    SharedPreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_mamual);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() throws Exception {
        url = NetConfig.url + NetConfig.Pda_Method;

        products = new ArrayList<>();
        stockPosBeans = new ArrayList<>();
        totals = new ArrayList<>();
        showList = new ArrayList<>();
//        url = NetConfig.url + NetConfig.Pda_Method;
        tvTitle.setText("扫描条码");
        ivRight.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
        tvDelete.setText("汇总");
        tvDelete.setBackgroundResource(R.drawable.bottom_save);
//        tvSave.setText("新增");
//        tvDelete.setVisibility(View.INVISIBLE);
        tvSave.setText("清空");
        tvSave.setBackgroundResource(R.drawable.bottom_delete);

        tvSubmit.setText("保存");
//        tvTotal.setText("数量：0");
        Intent intent = getIntent();
        mainID = intent.getIntExtra("mainID", 0);
        stockID = intent.getIntExtra("stockid", 0);
        tableName = intent.getStringExtra("tableName");


        setEdit();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvScan.setLayoutManager(layoutManager);
        rvTotal.setLayoutManager(new LinearLayoutManager(this));
        getProduct();
    }

    private void setEdit() {

        etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!isShow && !hasFocus) {
                    hideKeyBoard(ScanManualActivity.this, etCode);
                }
            }
        });
        etCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!isShow && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    etCode.clearFocus();
                    code = etCode.getText().toString();
                    if (StringUtil.isNotEmpty(code) && !isLoad) {
                        Log.e(TAG, codes.contains(code) + "" + codes.size() + code);
                        if (codes.contains(code) == false) {
                            codes.add(code);
                            getScamData(code);
                        } else {
                            Toasts.showShort(ScanManualActivity.this, "条码已存在");
                        }
                    }
                }
                return false;
            }
        });
        etStockPos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isShow && !hasFocus) {
                    hideKeyBoard(ScanManualActivity.this, etStockPos);
                }
            }
        });
        etStockPos.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isShow && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    etStockPos.clearFocus();
                    String pos = etStockPos.getText().toString();
                    boolean isNone = true;
                    for (int i = 0; i < stockPosBeans.size(); i++) {
//                        stockPosBeans.get(i).getSBerChID();
                        if (pos.equals(stockPosBeans.get(i).getSBerChID())) {
                            isNone = false;
                            stockSelectedPos = i;
                            tvStovkPos.setText(stockPosBeans.get(i).getSBerChID());
                            stockPosID = stockPosBeans.get(i).getIRecNo();
                            showORhide();
                            break;
                        }
                    }
                    if (isNone) {
                        Toasts.showShort(ScanManualActivity.this, "该仓位不存在");
                    }
                }

                return false;
            }
        });
    }

    /**
     * 获取已有条码
     */
    private void getProduct() throws Exception {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getProductParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                Log.e(TAG, string);
                StorageScan storageScan = new Gson().fromJson(string, StorageScan.class);
                if (storageScan.isSuccess()) {
                    List<StorageScanBean> list = storageScan.getDataset().getSBarCode();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.cancelDialogForLoading();
                            if (list != null && list.size() > 0) {
                                products.addAll(list);
                                showList.addAll(list);
//                                //以后条码放入codes，以便去重
                                for (int i = 0; i < list.size(); i++) {
                                    codes.add(list.get(i).getsBarCode());
                                }
                                refreshList();
                            }
                        }
                    });
                } else {
                    loadFail(storageScan.getMessage());
                }
            }

            @Override
            public void onFail(IOException e) {
                loadFail("加载失败");
            }
        });
    }

    private List<NetParams> getProductParams() throws Exception {
        List<NetParams> list = new ArrayList<>();
        list.add(new NetParams("otype", "GetProductD"));
        list.add(new NetParams("sTableName", tableName));
        list.add(new NetParams("iMainRecNo", mainID + ""));
        list.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        return list;
    }

    /**
     * Exceptiom
     *
     * @param message
     */
    private void loadFail(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.cancelDialogForLoading();
                Toasts.showShort(ScanManualActivity.this, message);
            }
        });
    }

    /**
     * 刷新列表
     */
    private void refreshList() {
        Log.d(TAG, products.size() + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, TAG);
                if (mAdapter == null) {
                    mAdapter = new ScanNewAdapter(ScanManualActivity.this, showList);
                    rvScan.setAdapter(mAdapter);
                    tvTitle.setText("扫描条码" + "(" + products.size() + ")");
                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            isDelete(position);
                        }
                    });
                } else {
                    mAdapter.notifyDataSetChanged();
                    tvTitle.setText("扫描条码" + "(" + products.size() + ")");
                }
                isLoad = false;
            }
        });
    }

    private void refreshTotal() {
        if (totalAdapter == null) {
            totalAdapter = new ScanTotalAdapter(this, totals);
            rvTotal.setAdapter(totalAdapter);
        } else {
            totalAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 判断是否删除
     *
     * @param position
     */
    private void isDelete(int position) {
        if (deleteDialog == null)
            deleteDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否删除？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm) {
                        codes.remove(products.get(position).getsBarCode());
                        products.remove(position);
                        showList.remove(position);
                        Log.e(TAG, codes.size() + "");
                        refreshList();
                    }

                }
            });
        deleteDialog.show();
    }

    @OnClick({R.id.iv_back, R.id.fl_stock, R.id.tv_total, R.id.tv_delete, R.id.tv_save, R.id.tv_submit, R.id.fl_stock_pos, R.id.iv_stock_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_stock:
                if (stockPosBeans.size() > 0) {
                    if (pvStockPos != null) {
                        pvStockPos.show();
                        pvStockPos.setSelectOptions(stockSelectedPos);
                    } else {
                        setPick();
                    }
                } else {
                    try {
                        getData(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.iv_stock_scan:
                if (stockPosBeans.size() > 0) {
                    showORhide();
                } else {
                    try {
                        getData(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_delete:
                try {
                    if (!isTotal) {
                        totals.clear();
                        getTotalList();
                        isTotal = true;
                        tvDelete.setText("明细");
                        rvScan.setVisibility(View.GONE);
                        rvTotal.setVisibility(View.VISIBLE);
                        flStock.setVisibility(View.GONE);
                        etCode.setVisibility(View.GONE);
                        etCode.setText("");
                        refreshTotal();
                    } else {
                        isTotal = false;
                        tvDelete.setText("汇总");
                        rvTotal.setVisibility(View.GONE);
                        rvScan.setVisibility(View.VISIBLE);
                        flStock.setVisibility(View.VISIBLE);
                        etCode.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                codes.clear();
//                products.clear();
//                refreshList();
                break;
            case R.id.tv_save:
                codes.clear();
                products.clear();
                totals.clear();
                showList.clear();
                refreshList();
//                showORhide();
                break;
            case R.id.tv_submit:
                saveCode();
                break;
            case R.id.fl_stock_pos:
//                Log.e(TAG, "clidk");
                showORhide();
                break;

            default:
                break;
        }
    }

    /**
     * 获取汇总列表
     */
    private void getTotalList() throws Exception {
//        Log.e("productSize", products.size() + "");
        for (int i = 0; i < products.size(); i++) {
            StorageScanBean storageScanBean = new StorageScanBean();
            storageScanBean.setsStyleNo(products.get(i).getsStyleNo());
            storageScanBean.setsColorName(products.get(i).getsColorName());
            storageScanBean.setType(2);

            if (totals.size() == 0) {
                totals.add(storageScanBean);
            } else {
                boolean isNew = true;
                for (int j = 0; j < totals.size(); j++) {
                    StorageScanBean totalItem = totals.get(j);
                    //根据款号和颜色进行汇总
                    if (storageScanBean.getsStyleNo().equals(totalItem.getsStyleNo()) && storageScanBean.getsColorName().equals(totalItem.getsColorName())) {
                        totals.get(j).setCount(totalItem.getCount() + 1);
                        isNew = false;
                        break;
                    }
                }
                if (isNew)
                    totals.add(0, storageScanBean);
            }
        }
//        Log.e(TAG, new Gson().toJson(totals));
    }

    /**
     * 输入框隐藏和显示
     */
    private void showORhide() {
        if (!isShow) {
            etStockPos.requestFocus();
            flStockPos.setVisibility(View.VISIBLE);
            isShow = true;
        } else {
            flStockPos.setVisibility(View.GONE);
            etCode.setText("");
            etCode.requestFocus();
            isShow = false;
        }
    }


    /**
     * 选择库位弹窗
     */
    private void setPick() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initPick();
                pvStockPos.show();
            }
        });
    }

    /**
     * 初始化弹窗
     */
    private void initPick() {

        pvStockPos = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText()
//                        + options2Items.get(options1).get(options2)
                /* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*/
                stockPosID = stockPosBeans.get(options1).getIRecNo();
                if (stockPosID == 0)
                    tvStovkPos.setText("");
                else
                    tvStovkPos.setText(stockPosBeans.get(options1).getPickerViewText());
            }
        })
                .setTitleText("仓位选择")
                .setContentTextSize(18)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(stockSelectedPos)//默认选中项
                .isDialog(true)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
//                        Toast.makeText(StorageActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvStockPos.setPicker(stockPosBeans);//一级选择器
//        pvCustom.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
        Dialog mDialog = pvStockPos.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvStockPos.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
                //当显示只有一列是需要设置window宽度，防止两边有空隙；
                WindowManager.LayoutParams winParams;
                winParams = dialogWindow.getAttributes();
                winParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialogWindow.setAttributes(winParams);
            }
        }
    }

    /**
     * 获取库位数据
     */
    private void getData(int type) throws Exception {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.cancelDialogForLoading();
                        StorageStockPosition storageStockPosition = new Gson().fromJson(string, StorageStockPosition.class);
                        if (storageStockPosition.isSuccess()) {
                            List<StorageStockPosBean> list = storageStockPosition.getDataset().getBscDataStockD();
                            if (list != null && list.size() > 0) {
                                stockPosBeans.addAll(list);
                                stockPosBeans.add(0, new StorageStockPosBean(0, "无"));
                                if (type == 0)
                                    setPick();
                                else
                                    showORhide();
                            } else
                                loadFail("仓位数据为空");
                        } else {

                        }
                    }
                });
            }

            @Override
            public void onFail(IOException e) {
                loadFail("加载失败");
            }
        });
    }

    /**
     * 获取库位参数
     *
     * @return
     */
    private List<NetParams> getParams() throws Exception {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "GetBscDataStockD"));
        params.add(new NetParams("iMainRecNo", stockID + ""));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));
        return params;
    }

    /**
     * 保存数据
     */
    private void saveCode() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getSaveCodeParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    boolean isSuccess = jsonObject.optBoolean("success");
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String data = "";
                                ArrayList<StorageScanBean> three = new ArrayList<>();
                                if (products.size() == 0) {
                                } else if (products.size() > 2) {
                                    for (int i = 0; i < 3; i++) {
                                        three.add(products.get(i));
                                        data = new Gson().toJson(three);
                                    }
                                } else {
                                    for (int j = 0; j > products.size(); j++) {
                                        three.add(products.get(j));
                                        data = new Gson().toJson(three);
                                    }
                                }
                                LoadingDialog.cancelDialogForLoading();
                                Intent intent = new Intent();
                                intent.putExtra("total", products.size());
                                intent.putExtra("list", data);
                                setResult(1, intent);
                                finish();
                            }
                        });
                    } else {
//                        Log.e(TAG, string);
//                        Log.e(TAG, jsonObject.getString("message"));
                        loadFail(jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loadFail("保存失败");
                }

            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                loadFail("加载失败");
            }
        });
    }

    /**
     * 保存参数
     *
     * @return
     */
    private List<NetParams> getSaveCodeParams() {
        String codes = "";
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "MMProductDsave"));
        params.add(new NetParams("sTableName", tableName));
        params.add(new NetParams("iRecNo", mainID + ""));
        for (int i = 0; i < products.size(); i++) {
            if (i != products.size() - 1)
                codes = codes + products.get(i).getsBarCode() + ","
                        + products.get(i).getiBscDataStockDRecNo() + "," + "1" + ";";
            else {
                codes = codes + products.get(i).getsBarCode() + ","
                        + products.get(i).getiBscDataStockDRecNo() + "," + "1";
            }
        }
        params.add(new NetParams("data", codes));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        Log.e(TAG, "iRecNo:" + mainID + ";" + "data:" + codes);
        return params;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, event.getScanCode() + "down");
        if (!isShow) {
            if (!isDown && event.getScanCode() == 261) {
                etCode.requestFocus();
                etCode.setText("");
                isDown = true;
            }
        } else {
            if (!isDown && event.getScanCode() == 261) {
                etStockPos.requestFocus();
                etStockPos.setText("");
                isDown = true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    String code = "";

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        isDown = false;
//        Log.e(TAG, event.getScanCode() + "up");
        if (!isShow) {
            if (event.getScanCode() == 261) {
                etCode.clearFocus();
                code = etCode.getText().toString();
                if (StringUtil.isNotEmpty(code) && !isLoad) {
//                Log.e(TAG, codes.contains(code) + "" + codes.size() + code);
                    if (codes.contains(code) == false) {
                        codes.add(code);
                        getScamData(code);
                    } else {
                        Toasts.showShort(ScanManualActivity.this, "条码已存在");
                    }

                }

            }
        } else {
            if (event.getScanCode() == 261) {
                etStockPos.clearFocus();
                String pos = etStockPos.getText().toString();
                boolean isNone = true;
                for (int i = 0; i < stockPosBeans.size(); i++) {
//                        stockPosBeans.get(i).getSBerChID();
                    if (pos.equals(stockPosBeans.get(i).getSBerChID())) {
                        isNone = false;
                        stockPosID = stockPosBeans.get(i).getIRecNo();
                        if (stockPosID == 0)
                            tvStovkPos.setText("");
                        else
                            tvStovkPos.setText(stockPosBeans.get(i).getSBerChID());
                        showORhide();
                        break;
                    }
                }
                if (isNone) {
                    Toasts.showShort(ScanManualActivity.this, "该仓位不存在");
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        try {
            return super.dispatchKeyEvent(e);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 获取条码数据
     */
    private void getScamData(String code) {
        isLoad = true;
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getScanParams(code), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                Log.e(TAG, string);
                StorageScan storageScan = new Gson().fromJson(string, StorageScan.class);
                if (storageScan.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.cancelDialogForLoading();
                            List<StorageScanBean> list = storageScan.getDataset().getSBarCode();
                            if (list != null && list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    StorageScanBean storageScanBean = list.get(i);
                                    storageScanBean.setiBscDataStockDRecNo(stockPosID);
                                    storageScanBean.setsBerChID(tvStovkPos.getText().toString());

                                    products.add(0, storageScanBean);
                                    showList.add(0, storageScanBean);
                                }
//                                showORhide();
                                refreshList();

                            } else {
                                Toasts.showShort(ScanManualActivity.this, "条码错误");
                                isLoad = false;
                            }

                        }
                    });
                } else {
                    loadFail(storageScan.getMessage());
                    isLoad = false;
                    Log.e(TAG, storageScan.getMessage());
                }
            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                loadFail("加载失败");
                isLoad = false;
            }
        });
    }


    /**
     * 扫码参数
     *
     * @param codes
     */
    private List<NetParams> getScanParams(String codes) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "GetsBarCode"));
        params.add(new NetParams("sbarcode", codes));
        Log.e(TAG, codes);
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));
        return params;
    }

    /**
     * 隐藏系统键盘
     */
    public static void hideKeyBoard(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
