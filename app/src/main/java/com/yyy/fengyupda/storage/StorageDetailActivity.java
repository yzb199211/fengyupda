package com.yyy.fengyupda.storage;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.yyy.fengyupda.R;
import com.yyy.fengyupda.dialog.JudgeDialog;
import com.yyy.fengyupda.dialog.LoadingDialog;
import com.yyy.fengyupda.interfaces.ResponseListener;
import com.yyy.fengyupda.lookup.LookUpActivity;
import com.yyy.fengyupda.model.storage.Storage;
import com.yyy.fengyupda.model.storage.StorageCustomerBean;
import com.yyy.fengyupda.model.storage.StorageListOrder;
import com.yyy.fengyupda.model.storage.StorageMain;
import com.yyy.fengyupda.model.storage.StorageStockMBean;
import com.yyy.fengyupda.pick.builder.OptionsPickerBuilder;
import com.yyy.fengyupda.pick.builder.TimePickerBuilder;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectChangeListener;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectListener;
import com.yyy.fengyupda.pick.listener.OnTimeSelectChangeListener;
import com.yyy.fengyupda.pick.listener.OnTimeSelectListener;
import com.yyy.fengyupda.pick.view.OptionsPickerView;
import com.yyy.fengyupda.pick.view.TimePickerView;
import com.yyy.fengyupda.scan.ScanManualActivity;
import com.yyy.fengyupda.scan.ScanRFIDActivity;
import com.yyy.fengyupda.util.CodeConfig;
import com.yyy.fengyupda.util.NetConfig;
import com.yyy.fengyupda.util.NetParams;
import com.yyy.fengyupda.util.NetUtil;
import com.yyy.fengyupda.util.SharedPreferencesHelper;
import com.yyy.fengyupda.util.StringUtil;
import com.yyy.fengyupda.util.Toasts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StorageDetailActivity extends AppCompatActivity {
    private final static String TAG = "StorageDetailActivity";
    private final static int ADDCODE = 100;
    private final static int SUMITCODE = 101;
    private final static int SCANCODE = 102;
    private final static int CUSTOMERCODE = 103;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right2)
    ImageView ivRight2;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.tv_storage)
    TextView tvStorage;
    @BindView(R.id.switch_view)
    Switch switchView;
    @BindView(R.id.ll_detial)
    LinearLayout llDetial;
    @BindView(R.id.tv_supplier)
    TextView tvSupplier;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.scroll)
    ScrollView scroll;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.tv_num)
    TextView tvNum;
    String url;
    List<StorageCustomerBean> customers;
    List<StorageStockMBean> stocks;

    private TimePickerView pvTime;
    private OptionsPickerView pvCustom, pvStock;
    private JudgeDialog deleteDialog;
    private JudgeDialog submitDialog;
    private JudgeDialog saveDialog;
    private JudgeDialog cleanDialog;
    int isSelect = 0;//是否红冲
    int customerid, stockid;
    String selectDate;
    int codeNum = 0;
    int mainID = 0;
    SharedPreferencesHelper preferencesHelper;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        init();

    }

    private void init() {

        url = NetConfig.url + NetConfig.Pda_Method;
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if (StringUtil.isNotEmpty(data)) {
            initView(data);
        }
        tvTitle.setText("入库单");
        ivRight.setVisibility(View.GONE);
        customers = new ArrayList<>();
        stocks = new ArrayList<>();
        selectDate = StringUtil.getTime();
        tvDate.setText(StringUtil.getTime());
        setSwitch();
        getData();
    }

    /**
     * 初始化数据
     *
     * @param data
     */
    private void initView(String data) {
        StorageListOrder order = new Gson().fromJson(data, StorageListOrder.class);
        mainID = order.getIRecNo();
        selectDate = order.getDDate();
        customerid = order.getIBscDataCustomerRecNo();
        stockid = order.getIBscDataStockMRecNo();
        codeNum = order.getIQty();
        isSelect = order.getIRed();
        tvDate.setText(selectDate);
        tvSupplier.setText(order.getSCustShortName());
        tvStorage.setText(order.getSStockName());
        tvNum.setText("商品数量：" + codeNum);
        etRemark.setText(order.getSReMark());
        if (isSelect == 1)
            switchView.setChecked(true);
    }

    /**
     * 设置红冲监听
     */
    private void setSwitch() {
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isSelect = 1;
                else
                    isSelect = 0;
            }
        });
    }

    /**
     * 获取选择列表数据
     */
    private void getData() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    initData(string);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.cancelDialogForLoading();
                            Toasts.showShort(StorageDetailActivity.this, "加载失败");
                            setEmpty();
                        }
                    });
                }
                ;
            }

            @Override
            public void onFail(IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.cancelDialogForLoading();
                        Toasts.showShort(StorageDetailActivity.this, "加载失败");
                        setEmpty();
                    }
                });
            }
        });
    }

    /**
     * 初始化选择列表数据
     *
     * @param string
     * @throws Exception
     */
    private void initData(String string) throws Exception {
        Storage storage = new Gson().fromJson(string, Storage.class);
        if (storage.isSuccess()) {
            customers = storage.getDataset().getBscDataCustomer();
            customers.add(0, new StorageCustomerBean(0, "无"));
            stocks = storage.getDataset().getBscDataStockM();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelDialogForLoading();
                    setSuccess();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelDialogForLoading();
                    Toasts.showShort(StorageDetailActivity.this, storage.getMessage());
                    setEmpty();
                }
            });
        }
    }

    /**
     * 获取选择列表数据参数
     *
     * @return
     */
    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "GetBscData"));
        params.add(new NetParams("iType", "1"));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        return params;
    }

    /**
     * s设置加载失败布局
     */
    private void setEmpty() {
        flEmpty.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
        scroll.setVisibility(View.GONE);
        tvEmpty.setText(getString(R.string.refresh));
    }

    /**
     * 设置加载成功布局
     */
    private void setSuccess() {
        flEmpty.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.VISIBLE);
    }

    private void isClean() {
        if (cleanDialog == null) {
            cleanDialog = new JudgeDialog(this, R.style.JudgeDialog, "清空后将直接保存数据，确认是否清空？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        clearChild();
                }
            });
        } else cleanDialog.show();
    }

    @OnClick({R.id.tv_empty, R.id.iv_back, R.id.iv_right2, R.id.iv_right, R.id.tv_storage, R.id.iv_clear, R.id.iv_scan, R.id.iv_add_detail, R.id.tv_supplier, R.id.tv_date, R.id.tv_delete, R.id.tv_save, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mainID != 0)
                    back("", 0);
                else
                    finish();
                break;
            case R.id.iv_right2:
                break;
            case R.id.iv_right:
                break;
            case R.id.tv_storage:
                if (pvStock == null)
                    initPvStock();
                pvStock.show();
                break;
            case R.id.iv_clear:
                isClean();
                break;
            case R.id.iv_scan:
                if (stockid != 0) {
                    if (mainID == 0)
                        submit(SCANCODE, 0);
                    else {
                        goScan();
                    }
                } else
                    Toasts.showShort(StorageDetailActivity.this, "请选择仓库");
                break;
            case R.id.iv_add_detail:
                if (stockid != 0) {
                    if (mainID == 0)
                        submit(ADDCODE, 0);
                    else
                        goAdd();
                } else
                    Toasts.showShort(StorageDetailActivity.this, "请选择仓库");
                break;
            case R.id.tv_supplier:
//                if (pvCustom == null)
//                    initPvCustomer();
//                pvCustom.show();
                try {
                    Intent intent = new Intent();
                    intent.setClass(StorageDetailActivity.this, LookUpActivity.class);
                    intent.putExtra("data", new Gson().toJson(customers));
                    intent.putExtra("code", CUSTOMERCODE);
                    intent.putExtra("title", "客户选择");
                    startActivityForResult(intent, CUSTOMERCODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_date:
                if (pvTime == null)
                    initTimePicker();
                pvTime.show();
                break;
            case R.id.tv_delete:
                if (mainID != 0)
                    isDelete();
                else
                    finish();
                break;
            case R.id.tv_save:
                save();
                break;
            case R.id.tv_submit:
                submit();
                break;
            case R.id.tv_empty:
                getData();
                break;
            default:
        }
    }

    /**
     * 判断是否删除
     */
    private void isDelete() {
        if (deleteDialog == null)
            deleteDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否删除？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        deleteMain();
                }
            });
        deleteDialog.show();
    }

    /**
     * 保存主表信息
     */
    private void save() {
        if (stockid == 0) {
            Toasts.showShort(this, "请选择仓库");
            return;
        }
        if (TextUtils.isEmpty(selectDate)) {
            Toasts.showShort(this, "请选择入库日期");
            return;
        }
        isSave();

    }

    /**
     * 判断是否保存
     */
    private void isSave() {
        if (saveDialog == null)
            saveDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否保存？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        submit(SUMITCODE, 0);
                }
            });
        saveDialog.show();
    }

    /**
     * 保存主表信息
     */
    private void submit() {
        if (stockid == 0) {
            Toasts.showShort(this, "请选择仓库");
            return;
        }
        if (TextUtils.isEmpty(selectDate)) {
            Toasts.showShort(this, "请选择入库日期");
            return;
        }
        isSubmit();

    }

    /**
     * 判断是否提交
     */
    private void isSubmit() {
        if (submitDialog == null)
            submitDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否提交？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        submit(SUMITCODE, 1);
                }
            });
        submitDialog.show();
    }

    /**
     * 初始化日期
     */
    private void initTimePicker() {//Dialog 模式下，在底部弹出
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                selectDate = StringUtil.getTime(date);
                tvDate.setText(selectDate);
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                }).setContentTextSize(18).setBgColor(0xFFFFFFFF)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
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
     * 初始化供应商
     */
    private void initPvCustomer() {
        pvCustom = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText()
//                        + options2Items.get(options1).get(options2)
                /* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*/
                customerid = customers.get(options1).getIRecNo();
                if (customerid == 0)
                    tvSupplier.setText("");
                else
                    tvSupplier.setText(customers.get(options1).getPickerViewText());

            }
        })
                .setTitleText("供应商选择")
                .setContentTextSize(18)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
//                .setBgColor(Color.BLACK)
//                .setTitleBgColor(Color.DKGRAY)
//                .setTitleColor(Color.LTGRAY)
//                .setCancelColor(Color.YELLOW)
//                .setSubmitColor(Color.YELLOW)
//                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .isDialog(true)
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
//                        Toast.makeText(StorageDetailActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvCustom.setPicker(customers);//一级选择器
//        pvCustom.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
        Dialog mDialog = pvCustom.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvCustom.getDialogContainerLayout().setLayoutParams(params);
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
     * 初始化仓库
     */
    private void initPvStock() {

        pvStock = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText()
//                        + options2Items.get(options1).get(options2);
                /* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*/

                tvStorage.setText(stocks.get(options1).getPickerViewText());
                stockid = stocks.get(options1).getIRecNo();
            }
        })
                .setTitleText("仓库选择")
                .setContentTextSize(18)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isDialog(true)
//                .setBgColor(Color.BLACK)
//                .setTitleBgColor(Color.DKGRAY)
//                .setTitleColor(Color.LTGRAY)
//                .setCancelColor(Color.YELLOW)
//                .setSubmitColor(Color.YELLOW)
//                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
//                        Toast.makeText(StorageDetailActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvStock.setPicker(stocks);//一级选择器
//        pvCustom.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
        Dialog mDialog = pvStock.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvStock.getDialogContainerLayout().setLayoutParams(params);
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
     * 提交主表信息
     *
     * @param code
     * @param i
     */
    private void submit(int code, int i) {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getSubmitParams(i), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    Log.e(TAG, string);
                    StorageMain storageMain = new Gson().fromJson(string, StorageMain.class);
                    if (storageMain.isSuccess()) {
                        if (code == SCANCODE)
                            goScan(string);
                        else if (code == ADDCODE) {
                            goAdd(string);
                        } else if (code == SUMITCODE && i == 0) {
                            back("保存成功", i);
                        } else {
                            back("提交成功", i);
                        }
                    } else {
//                        Toasts.showShort(StorageDetailActivity.this, storageMain.getMessage());
                        loadFail(storageMain.getMessage());
//                        Log.e(TAG, storageMain.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG,e.getMessage());
                    e.printStackTrace();
                    if (code == ADDCODE || code == SCANCODE)
                        loadFail("加载失败");
                    else if (code == SUMITCODE && i == 0)
                        loadFail("保存失败");
                    else
                        loadFail("提交失败");
                }
            }

            @Override
            public void onFail(IOException e) {
                if (code == ADDCODE || code == SCANCODE)
                    loadFail("加载失败");
                else if (code == SUMITCODE && i == 0)
                    loadFail("保存失败");
                else
                    loadFail("提交失败");
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 返回列表
     *
     * @param message
     */
    private void back(String message, int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.cancelDialogForLoading();
                if (StringUtil.isNotEmpty(message))
                    Toasts.showShort(StorageDetailActivity.this, message);
                if (i == 1)
                    setResult(CodeConfig.DELETECODE);
                else
                    setResult(CodeConfig.REFRESHCODE);
                finish();
            }
        });
    }

    /**
     * 开始扫描
     */
    private void goAdd(String data) throws Exception {
        StorageMain storageMain = new Gson().fromJson(data, StorageMain.class);
        if (storageMain.isSuccess() == true) {
            mainID = storageMain.getDataset().getResult().get(0).getResult();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelDialogForLoading();
                    Intent intent = new Intent();
                    intent.setClass(StorageDetailActivity.this, ScanManualActivity.class);
                    intent.putExtra("stockid", stockid);
                    intent.putExtra("mainID", mainID);
                    intent.putExtra("tableName", "MMProductInD");
                    startActivityForResult(intent, 1);
                }
            });
        } else {
            loadFail(storageMain.getMessage());
            Log.e(TAG, storageMain.getMessage());
        }
    }

    private void goAdd() {
        Intent intent = new Intent();
        intent.setClass(StorageDetailActivity.this, ScanManualActivity.class);
        intent.putExtra("stockid", stockid);
        intent.putExtra("mainID", mainID);
        intent.putExtra("tableName", "MMProductInD");
        startActivityForResult(intent, 1);
    }


    /**
     * 开始扫描
     */
    private void goScan(String data) throws Exception {
        StorageMain storageMain = new Gson().fromJson(data, StorageMain.class);
        if (storageMain.isSuccess() == true) {
            mainID = storageMain.getDataset().getResult().get(0).getResult();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelDialogForLoading();
                    Intent intent = new Intent();
                    intent.setClass(StorageDetailActivity.this, ScanRFIDActivity.class);
                    intent.putExtra("stockid", stockid);
                    intent.putExtra("mainID", mainID);
                    intent.putExtra("tableName", "MMProductInD");
                    startActivityForResult(intent, 1);
                }
            });
        } else {
            loadFail(storageMain.getMessage());
            Log.e(TAG, storageMain.getMessage());
        }
    }

    private void goScan() {
        Intent intent = new Intent();
        intent.setClass(StorageDetailActivity.this, ScanRFIDActivity.class);
        intent.putExtra("stockid", stockid);
        intent.putExtra("mainID", mainID);
        intent.putExtra("tableName", "MMProductInD");
        startActivityForResult(intent, 1);
    }

    /**
     * 提交主表参数
     *
     * @param isSubmit
     * @return
     */
    private List<NetParams> getSubmitParams(int isSubmit) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("sTableName", "MMProductInM"));
        if (mainID == 0)
            params.add(new NetParams("iRecNo", ""));
        else
            params.add(new NetParams("iRecNo", mainID + ""));
        params.add(new NetParams("iBscDataStockMRecNo", stockid + ""));
        params.add(new NetParams("iRed", isSelect + ""));
        params.add(new NetParams("iQty", codeNum + ""));
        params.add(new NetParams("iBscDataCustomerRecNo", customerid + ""));
        params.add(new NetParams("dDate", tvDate.getText().toString()));
        params.add(new NetParams("sRemark", etRemark.getText().toString()));
        params.add(new NetParams("sUserID", (String) preferencesHelper.getSharedPreference("userid", "")));
        params.add(new NetParams("iSumbit", isSubmit + ""));
        params.add(new NetParams("otype", "MMProductSave"));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        return params;
    }

    /**
     * 删除入库单
     */
    private void deleteMain() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getDeleteParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                StorageMain storageMain = new Gson().fromJson(string, StorageMain.class);
                if (storageMain.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasts.showShort(StorageDetailActivity.this, "删除成功");
                            LoadingDialog.cancelDialogForLoading();
                            setResult(CodeConfig.DELETECODE);
                            finish();
                        }
                    });
                } else {
                    loadFail("删除失败");
                }
            }

            @Override
            public void onFail(IOException e) {
                loadFail("删除失败");
            }
        });
    }

    /**
     * 删除入库单参数
     *
     * @return
     */
    private List<NetParams> getDeleteParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("sTableName", "MMProductInM"));
        params.add(new NetParams("iRecNo", mainID + ""));
        params.add(new NetParams("otype", "DeleteProduct"));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));

        return params;
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
                if (StringUtil.isNotEmpty(message))
                    Toasts.showShort(StorageDetailActivity.this, message);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            switch (resultCode) {
                case 1:
                    codeNum = data.getIntExtra("total", 0);
                    tvNum.setText("商品数量：" + codeNum);
                    break;
                case CUSTOMERCODE:
                    customerid = data.getIntExtra("id", 0);
                    tvSupplier.setText(data.getStringExtra("name"));
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 清空子表数据
     */
    private void clearChild() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(clearChildParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(string);
                    boolean isSuccess = jsonObject.optBoolean("success");
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvNum.setText("商品数量：0");
                                loadFail("清空成功");
                            }
                        });
                    } else {
                        loadFail(jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadFail("清空失败");
                }

            }

            @Override
            public void onFail(IOException e) {
                loadFail("清空失败");
            }
        });
    }

    private List<NetParams> clearChildParams() {
        List<NetParams> params = new ArrayList<>();
        String codes = "";
        params.add(new NetParams("otype", "MMProductDsave"));
        params.add(new NetParams("sTableName", "MMProductInD"));
        params.add(new NetParams("iRecNo", mainID + ""));
        params.add(new NetParams("data", codes));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));
        return params;
    }
}
