package com.yyy.fengyupda.scan;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scanner.impl.ReaderManager;
import com.google.gson.Gson;
import com.ruijie.uhflib.power.manage.PowerManage;
import com.ruijie.uhflib.uhf.LinkInter;
import com.ruijie.uhflib.uhf.manage.LinkManage;
import com.yyy.fengyupda.R;
import com.yyy.fengyupda.dialog.JudgeDialog;
import com.yyy.fengyupda.dialog.LoadingDialog;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.interfaces.ResponseListener;
import com.yyy.fengyupda.model.storage.PowerBean;
import com.yyy.fengyupda.model.storage.StorageScan;
import com.yyy.fengyupda.model.storage.StorageScanBean;
import com.yyy.fengyupda.model.storage.StorageStockPosBean;
import com.yyy.fengyupda.model.storage.StorageStockPosition;
import com.yyy.fengyupda.pick.builder.OptionsPickerBuilder;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectChangeListener;
import com.yyy.fengyupda.pick.listener.OnOptionsSelectListener;
import com.yyy.fengyupda.pick.view.OptionsPickerView;
import com.yyy.fengyupda.scan.linkutil.InitLinkTask;
import com.yyy.fengyupda.scan.util.LogcatHelper;
import com.yyy.fengyupda.scan.util.SoundUtil;
import com.yyy.fengyupda.util.NetConfig;
import com.yyy.fengyupda.util.NetParams;
import com.yyy.fengyupda.util.NetUtil;
import com.yyy.fengyupda.util.PowerUtil;
import com.yyy.fengyupda.util.SharedPreferencesHelper;
import com.yyy.fengyupda.util.StringUtil;
import com.yyy.fengyupda.util.Toasts;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ruijie.com.uhflib.uhf.InventoryData;
import ruijie.com.uhflib.uhf.Linker;

public class ScanRFIDActivity extends AppCompatActivity {
    private static final String TAG = "ScanRFIDActivity";
    private final static int SETTINGCODE = 10;
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
    @BindView(R.id.tv_syn)
    TextView tvSyn;
    @BindView(R.id.rv_total)
    RecyclerView rvTotal;
    @BindView(R.id.tv_totals)
    TextView tvTotals;
    @BindView(R.id.tv_unsyn)
    TextView tvUnSyn;
    @BindView(R.id.tv_details)
    TextView tvDetials;
    @BindView(R.id.fl_stock)
    FrameLayout flStock;
    @BindView(R.id.fl_stock_pos)
    FrameLayout flStockPos;
    @BindView(R.id.et_stock_pos)
    EditText etStockPos;

    int mainID;
    int stockID;
    String url;
    List<StorageStockPosBean> stockPosBeans;

    OptionsPickerView pvStockPos;
    OptionsPickerView pvPower;
    int stockPosID = 0;

    private JudgeDialog deleteDialog;
    private JudgeDialog synDialog;

    ScanNewAdapter mAdapter;
    ScanTotalAdapter totalAdapter;
    Message msg = null;
    /**
     * pda参数
     */
    private String strMsg = "";
    private boolean isFrist = true;
    private boolean isClean = false;
    private boolean canExit = false;
    private boolean isStart = false;
    private boolean isTotal = false;//判断是否在汇总页
    boolean isShow = false;
    boolean isDown = false;
    boolean isManaul = false;


    List<StorageScanBean> products;
    List<StorageScanBean> waitCodes = new ArrayList<>();//为请求参数
    List<StorageScanBean> totals = new ArrayList<>();
    List<PowerBean> powerList;

    Set<String> codes = new HashSet<>();

    boolean isLoading = false;
    int holdTime;
    boolean loaded = false;
    String tableName = "";

    SharedPreferencesHelper preferencesHelper;

    private LinkInter mLinker;
    private SharedPreferences cfgSP;
    private ExecutorService mThreadPool;

    private int checkRepeatSound = 0;
    private int powerLevel;
    private int dwellTime;

    private boolean isResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);
        cfgSP = getSharedPreferences("ServerConf", Context.MODE_PRIVATE);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        mThreadPool = Executors.newFixedThreadPool(5);
        setContentView(R.layout.activity_scan_rfid);
        ButterKnife.bind(this);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
//        initPick();
        powerList = PowerUtil.PowerList();
        url = NetConfig.url + NetConfig.Pda_Method;
        products = new ArrayList<>();
        stockPosBeans = new ArrayList<>();
        tvTitle.setText("扫描条码");
        ivRight.setImageResource(R.mipmap.icon_scan_setting);
        bottomLayout.setVisibility(View.VISIBLE);
        tvDelete.setText("清空");
        tvSave.setText("开始");
        tvSubmit.setText("保存");
        tvTotal.setText("数量：0");
        tvSyn.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        mainID = intent.getIntExtra("mainID", 0);
        stockID = intent.getIntExtra("stockid", 0);
        tableName = intent.getStringExtra("tableName");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvScan.setLayoutManager(layoutManager);
        rvTotal.setLayoutManager(new LinearLayoutManager(this));
        getProduct();
    }


    /**
     * 初始化弹窗
     */
    private void initPick() {

        pvStockPos = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

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
                .setSelectOptions(0)//默认选中项
                .isDialog(true)

                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
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


    private void startOrStopScan() {
        if (!mLinker.getIsInventoryRunning()) {
            checkRepeatSound = cfgSP.getInt("checkRepeatSound", 0);
            SoundUtil.initSoundUtil(this);
            startScan();
        } else {
            stopScan();
        }
    }

    private void startScan() {
        checkRepeatSound = cfgSP.getInt("checkRepeatSound", 0);
        SoundUtil.initSoundUtil(this);
        try {
            ReaderManager mReaderManager = ReaderManager.getInstance();//只有二维码功能读写器支持
            mReaderManager.SetActive(false);//二维码对rfid扫描有影响需关闭
            mReaderManager.setEnableScankey(false);//连续按会开启，bug，屏蔽按钮
        } catch (NoClassDefFoundError e) {
            Log.e("rfid", "mReaderManager init failed NoClassDefFoundError");
        } catch (Exception e) {
            Log.e("rfid", "mReaderManager init failed " + e.getMessage());
        }
        mLinker.setCallbackHandler(checkhandler);

        mLinker.turnInventoryCarrierWaveOff();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLinker.startInventory();
            }
        });
        mThreadPool.execute(() -> LogcatHelper.getInstance(this).start());
        //Toast.makeText(getContext(), "开始盘点", Toast.LENGTH_SHORT).show();
        tvSave.setText("停止");
        isStart = true;
    }

    private void stopScan() {
        try {
            ReaderManager mReaderManager = ReaderManager.getInstance();//只有二维码功能读写器支持
            mReaderManager.SetActive(true);//二维码对rfid扫描有影响需关闭
            mReaderManager.setEnableScankey(true);//连续按会开启，bug，屏蔽按钮
        } catch (NoClassDefFoundError e) {
            Log.e("rfid", "mReaderManager init failed NoClassDefFoundError");
        } catch (Exception e) {
            Log.e("rfid", "mReaderManager init failed " + e.getMessage());
        }
        long startTime = System.currentTimeMillis();
        mThreadPool.execute(() -> LogcatHelper.getInstance(this).stop());
        mLinker.stopInventory();
        tvSave.setText("开始");
        isStart = false;
        if (waitCodes.size() > 0)
            getScamData(waitCodes, 1);
        Toast.makeText(this, "盘点已停止", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!isFrist) {
        isResume = true;
        InitLinkTask initLinkTask = new InitLinkTask(this);//初始化比较耗时，遮造效果
        int baudrate = cfgSP.getInt("baudrate", 115200);
        initLinkTask.setBaudrate(baudrate);
        initLinkTask.execute();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onPowerOff();
        mLinker.closeDev();
        //设备下电
        PowerManage.getInstance().powerDown();
    }

    public void onPowerOff() {
        if (mLinker.getIsInventoryRunning()) {
            stopScan();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler checkhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {

                    case 20000:
                        InventoryData[] inventoryData = (InventoryData[]) msg.obj;
                        if (inventoryData == null || inventoryData.length == 0) {
                            Log.e("test", "盘点空");
                            return;
                        }

                        for (InventoryData data : inventoryData) {
                            String epc = data.getEpc();//数据源
                            if (StringUtil.isNotEmpty(epc))
                                getCodes(epc);
                            Log.e("InventoryData", epc);
                            SoundUtil.playSound();

                        }
                        break;
                    case 20001:
                        //Log.e("test","on18k6cAntennaBegin "+msg.obj);
                        break;
                    case 20002:
                        Log.e("Inventory Error", "on18K16cInverntoyAbort " + msg.obj);
                        Toast.makeText(ScanRFIDActivity.this, "on18K16cInverntoyAbort " + msg.obj, Toast.LENGTH_SHORT).show();
                        SoundUtil.playSoundError();
                        break;
                    case 20003:
                        Log.e("test", "on18K16cInverntoyEndAbort " + msg.obj);

                        Toast.makeText(ScanRFIDActivity.this, "on18K16cInverntoyEndAbort " + msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 获取数据
     *
     * @param epc
     */
    private void getCodes(String epc) {
        int size;
        try {
            size = codes.size();
            codes.add(epc);
            if (size != codes.size()) {
                StorageScanBean code = new StorageScanBean();
                code.setiBscDataStockDRecNo(stockPosID);
                code.setsBerChID(tvStovkPos.getText().toString());
                code.setsBarCode(epc);
                code.setType(0);
                waitCodes.add(code);
                products.add(0, code);
            }
            refreshList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.iv_back, R.id.tv_total, R.id.tv_delete, R.id.tv_save, R.id.tv_submit, R.id.fl_stock, R.id.fl_stock_pos,
            R.id.iv_right, R.id.tv_syn, R.id.tv_unsyn, R.id.tv_details, R.id.tv_totals, R.id.iv_stock_scan, R.id.tv_manual})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (!isStart) {
                    finish();
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "正在扫码。。。");
                }
                break;
            case R.id.tv_delete:
                if (!isTotal) {
                    if (!isStart) {
                        codes.clear();
                        Log.e(TAG, codes.size() + "");
                        products.clear();
                        waitCodes.clear();
                        refreshList();
                    } else {
                        Toasts.showShort(ScanRFIDActivity.this, "正在扫码。。。");
                    }
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "切换到明细");
                }
                break;
            case R.id.tv_save:
                try {
                    if (!isTotal) {
                        if (!isStart) {
                            if (isResume) {
                                Linker.AntCfg antCfg = mLinker.getAntCfg(0);
                                int dwellTime = antCfg.dwellTime;
                                mLinker.enableAnt(0, (Integer) preferencesHelper.getSharedPreference("powerLevel", 30) * 10, dwellTime);
                            }
                            startScan();
                        } else
                            stopScan();
                    } else {
                        Toasts.showShort(ScanRFIDActivity.this, "切换到明细");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_submit:

                if (!isStart) {
                    if (waitCodes.size() == 0) {
                        saveCode();
                    } else {
                        isSubmit();
                    }
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }

                break;
            case R.id.fl_stock:
                if (stockPosBeans.size() > 0) {
                    pvStockPos.show();
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
            case R.id.iv_right:
                Linker.AntCfg antCfg = mLinker.getAntCfg(0);
                dwellTime = antCfg.dwellTime;
                Log.e("powerLevel", antCfg.powerLevel + "");
                if (antCfg.powerLevel / 10 < 5) powerLevel = 5;
                else if (antCfg.powerLevel / 10 > 30) powerLevel = 30;
                else powerLevel = antCfg.powerLevel / 10;
                Log.e("powerLevel1", powerLevel + "");
                if (!isStart) {
                    if (pvPower != null) {
                        pvPower.show();
                    } else {
                        intiPowerPick();
                        pvPower.show();
                    }
//                    Intent intent = new Intent();
//                    intent.setClass(ScanRFIDActivity.this, ScanSettingActivity.class);
//                    intent.putExtra("code", SETTINGCODE);
//                    startActivityForResult(intent, SETTINGCODE);
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }
                break;
            case R.id.tv_syn:
                if (!isTotal)
                    if (!isStart)
                        getScamData(waitCodes, 1);
                    else
                        Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                else {
                    Toasts.showShort(ScanRFIDActivity.this, "切换到明细");
                }
                break;
            case R.id.tv_totals:
                if (!isStart) {
                    if (!isTotal) {
                        try {
                            isTotal = true;
                            rvScan.setVisibility(View.GONE);
                            rvTotal.setVisibility(View.VISIBLE);
                            flStock.setVisibility(View.GONE);
                            getTotalList();
                            refreshTotal();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }
                break;
            case R.id.tv_details:
                if (!isStart) {
                    if (isTotal) {
                        rvScan.setVisibility(View.VISIBLE);
                        rvTotal.setVisibility(View.GONE);
                        flStock.setVisibility(View.VISIBLE);
                        isTotal = false;
                    }
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }

                break;
            case R.id.fl_stock_pos:
                if (!isStart) {
                    showORhide();
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }

                break;
            case R.id.tv_manual:
                if (!isStart) {
                    showManaul();
                } else {
                    Toasts.showShort(ScanRFIDActivity.this, "扫码中。。。");
                }

                break;
            default:
                break;
        }
    }

    /**
     * 判断是否保存
     */
    private void isSubmit() {
        if (deleteDialog == null)
            deleteDialog = new JudgeDialog(this, R.style.JudgeDialog, "有未同步数据，是否保存？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm) {
                        saveCode();
                    }

                }
            });
        deleteDialog.show();
    }

    /**
     * 获取已有条码
     */
    private void getProduct() {
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
                                //以后条码放入codes，以便去重
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

    private List<NetParams> getProductParams() {
        List<NetParams> list = new ArrayList<>();
        list.add(new NetParams("otype", "GetProductD"));
        list.add(new NetParams("sTableName", tableName));
        list.add(new NetParams("iMainRecNo", mainID + ""));
        list.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));
        return list;
    }

    /**
     * 获取条码数据
     */
    private void getScamData(List<StorageScanBean> Codes, int type) {
        LoadingDialog.showDialogForLoading(this);
        isLoading = true;
        new NetUtil(getScanParams(Codes), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    Log.e(TAG, string + ",");
                    StorageScan storageScan = new Gson().fromJson(string, StorageScan.class);
                    if (storageScan.isSuccess()) {

                        List<StorageScanBean> list = storageScan.getDataset().getSBarCode();
                        Log.e(TAG, "productsize=" + list.size());
                        if (type == 2)
                            ScanRFIDActivity.this.codes.add(Codes.get(0).getsBarCode());
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                StorageScanBean storageScanBean = list.get(i);
                                String code = storageScanBean.getsBarCode();
                                for (int j = 0; j < Codes.size(); j++) {//查找返回数据在请求数据中的位置
                                    StorageScanBean scanCode = Codes.get(j);
                                    if (code.equals(scanCode.getsBarCode())) {//处理列表中相同的数据
                                        storageScanBean.setsBerChID(scanCode.getsBerChID());
                                        storageScanBean.setiBscDataStockDRecNo(scanCode.getiBscDataStockDRecNo());
                                        products.remove(scanCode);
                                        waitCodes.remove(scanCode);//移除已同步数据
                                        break;
                                    }
                                }
                                storageScanBean.setiBscDataStockDRecNo(stockPosID);
                                products.add(0, storageScanBean);
                            }
                            Log.e(TAG, waitCodes.size() + "");
                            for (int i = 0; i < waitCodes.size(); i++) {
                                StorageScanBean waitProduct = waitCodes.get(i);
                                String code = waitProduct.getsBarCode();
                                for (int j = 0; j < products.size(); j++) {
                                    StorageScanBean product = products.get(j);
                                    if (code.equals(product.getsBarCode())) {
                                        products.remove(j);
                                        break;
                                    }
                                }
                            }
                            products.addAll(0, waitCodes);
//                            products.remove(waitCodes);
                            Log.e(TAG, products.size() + "");
                            refreshList();

                            //上移未同步代码
//                            if (waitCodes.size() > 0)
//                                for (int j = 0; j < products.size(); j++) {
//                                    StorageScanBean storageScanBean = list.get(j);
//                                    if (TextUtils.isEmpty(storageScanBean.getsStyleNo())) {
//                                        products.remove(j);
//                                        products.add(0,storageScanBean);
//                                    }
//                                }
//                            refreshList();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.cancelDialogForLoading();
                            }
                        });
                        isLoading = false;

                    } else {
                        loadFail(storageScan.getMessage());
                        isLoading = false;
                        Log.e(TAG, storageScan.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    loadFail("返回数据错误");
                    isLoading = false;
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                loadFail("请求失败");
                isLoading = false;
            }
        });
    }

    /**
     * 扫码参数
     *
     * @param codes
     */
    private List<NetParams> getScanParams(List<StorageScanBean> codes) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "GetsBarCode"));
        String data = "";
        Log.e("CODES", "paramsize" + codes.size());
        for (int i = 0; i < codes.size(); i++) {
            if (i != codes.size() - 1)
                data = data + codes.get(i).getsBarCode() + ",";
            else
                data = data + codes.get(i).getsBarCode();
        }
        params.add(new NetParams("sbarcode", data));
        params.add(new NetParams("sCompanyCode", (String) preferencesHelper.getSharedPreference("db", "")));
        return params;
    }

    /**
     * 刷新列表
     */
    private void refreshList() {
//        Log.d(TAG, products.size() + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter == null) {
                    mAdapter = new ScanNewAdapter(ScanRFIDActivity.this, products);
                    rvScan.setAdapter(mAdapter);
//                    tvTotal.setText("数量：" + products.size());
                    tvTitle.setText("扫描条码" + "(" + products.size() + ")");
                    tvUnSyn.setText("未同步：" + waitCodes.size());
                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            isDelete(position);
                        }
                    });
                } else {
                    mAdapter.notifyDataSetChanged();
                    tvTitle.setText("扫描条码" + "(" + products.size() + ")");
                    tvUnSyn.setText("未同步：" + waitCodes.size());
                }
                isLoading = false;
            }
        });
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
                        //移除未同步数据
                        if (waitCodes.contains(products.get(position))) {
                            waitCodes.remove(products.get(position));
                            codes.remove(products.get(position).getsBarCode());
                            products.remove(position);
                        }
                        Log.e(TAG, codes.size() + "");
                        refreshList();
                    }

                }
            });
        deleteDialog.show();
    }

    /**
     * 获取汇总列表
     */
    private void getTotalList() throws Exception {
        totals.clear();
//        Log.e("productSize", products.size() + "");
        for (int i = 0; i < products.size(); i++) {
            StorageScanBean storageScanBean = new StorageScanBean();
            storageScanBean.setsStyleNo(products.get(i).getsStyleNo());
            storageScanBean.setsColorName(products.get(i).getsColorName());
            storageScanBean.setType(2);
            if (products.get(i).getType() == 1) {//只汇总已同步数据
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
        }
//        Log.e(TAG, new Gson().toJson(totals));
    }

    /*刷新汇总列表*/
    private void refreshTotal() {
        if (totalAdapter == null) {
            totalAdapter = new ScanTotalAdapter(this, totals);
            rvTotal.setAdapter(totalAdapter);
        } else {
            totalAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 初始化频率弹窗
     */
    private void intiPowerPick() {
        pvPower = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                Log.e("Power", powerList.get(options1).getPower() + "");
                boolean isSucess = mLinker.enableAnt(0, powerList.get(options1).getPower() * 10, dwellTime);
                if (isSucess) {
                    Toasts.showShort(ScanRFIDActivity.this, "配置成功");
                    preferencesHelper.put("powerLevel", powerList.get(options1).getPower());
                } else Toasts.showShort(ScanRFIDActivity.this, "配置失败");
            }
        })
                .setTitleText("功率选择")
                .setContentTextSize(18)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(powerLevel - 5)//默认选中项
                .isDialog(true)

                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvPower.setPicker(powerList);//一级选择器
//        pvCustom.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
        Dialog mDialog1 = pvPower.getDialog();
        if (mDialog1 != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvPower.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog1.getWindow();
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
                Log.e(TAG, string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    boolean isSuccess = jsonObject.optBoolean("success");
                    if (isSuccess) {
                        int num = Integer.parseInt(jsonObject.optString("message"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                String data = "";
//                                ArrayList<StorageScanBean> three = new ArrayList<>();
//                                if (products.size() == 0) {
//                                } else if (products.size() > 2) {
//                                    for (int i = 0; i < 3; i++) {
//                                        three.add(products.get(i));
//                                        data = new Gson().toJson(three);
//                                    }
//                                } else {
//                                    for (int j = 0; j > products.size(); j++) {
//                                        three.add(products.get(j));
//                                        data = new Gson().toJson(three);
//                                    }
//                                }
                                LoadingDialog.cancelDialogForLoading();
                                Intent intent = new Intent();
                                intent.putExtra("total", num);
//                                intent.putExtra("list", data);
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
                Toasts.showShort(ScanRFIDActivity.this, message);
            }
        });
    }

    /**
     * 输入框隐藏和显示
     */
    /**
     * 输入框隐藏和显示
     */
    private void showORhide() {
        if (!isShow) {
            etStockPos.requestFocus();
            flStockPos.setVisibility(View.VISIBLE);
            isShow = true;
        } else {
            etStockPos.setText("");
            flStockPos.setVisibility(View.GONE);
            isShow = false;
        }
    }

    private void showManaul() {
        if (!isManaul) {
            etStockPos.requestFocus();
            flStockPos.setVisibility(View.VISIBLE);
            isManaul = true;
        } else {
            etStockPos.setText("");
            flStockPos.setVisibility(View.GONE);
            isManaul = false;
        }
    }

    int downKey = 0;//只有按下和抬起为同一事件是才能出发up事件中的方法。防止扫描之后响应up事件的enrty方法！！！！！！！！！！！！！

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, event.getScanCode() + "down");
        downKey = keyCode;
        if (isShow) {
            if (!isDown && event.getScanCode() == 261) {
                etStockPos.requestFocus();
                etStockPos.setText("");
                isDown = true;
            }
        }
        if (isManaul) {
            if (!isDown && event.getScanCode() == 261) {
                etStockPos.requestFocus();
                etStockPos.setText("");
                isDown = true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /* 释放按键事件 */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG + 12, event.getScanCode() + "," + keyCode);
        isDown = false;
        if (downKey == keyCode) {
            if (!isShow && !isManaul) {
                if (event.getScanCode() == 261 && keyCode == 0 && !mLinker.getIsInventoryRunning())
                    startScan();
                else if (event.getScanCode() == 261 && keyCode == 0 && mLinker.getIsInventoryRunning()) {
                    try {
                        stopScan();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (isShow && !isManaul) {
                if (event.getScanCode() == 261 || keyCode == KeyEvent.KEYCODE_ENTER) {
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
                        Toasts.showShort(ScanRFIDActivity.this, "该仓位不存在");
                    }
                }
            } else if (!isShow && isManaul) {
                if (event.getScanCode() == 261 || keyCode == KeyEvent.KEYCODE_ENTER) {
                    String code = etStockPos.getText().toString();
                    if (StringUtil.isNotEmpty(code))
                        if (codes.contains(code)) {
                            Toasts.showShort(ScanRFIDActivity.this, "条码已存在");
                        } else {
                            showManaul();

                            List<StorageScanBean> beans = new ArrayList<>();
                            StorageScanBean bean = new StorageScanBean();
                            beans.add(bean);

                            bean.setsBarCode(code);
                            getScamData(beans, 2);
                        }
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

}
