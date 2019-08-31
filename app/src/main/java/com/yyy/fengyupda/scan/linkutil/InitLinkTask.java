package com.yyy.fengyupda.scan.linkutil;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.android.scanner.impl.ReaderManager;

import com.dou361.dialogui.DialogUIUtils;
import com.ruijie.uhflib.power.manage.PowerManage;
import com.ruijie.uhflib.uhf.LinkInter;
import com.ruijie.uhflib.uhf.bean.BatteryConfigBean;
import com.ruijie.uhflib.uhf.bean.RunLevelConfigBean;
import com.ruijie.uhflib.uhf.manage.LinkManage;
import com.ruijie.uhflib.uhf.platform.AndroidPlatform;
import com.yyy.fengyupda.scan.util.SharedPreferencesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.BATTERY_SERVICE;

/**
 * 模组初始化类
 */
public class InitLinkTask extends AsyncTask<String, Integer, Integer> {
    private Dialog dialog;
    private Context context;
    private int baudrate = 115200;

    public InitLinkTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        dialog = DialogUIUtils.showLoading(context, "初始化连接中...", false, false, false, true).show();

    }

    @Override
    protected Integer doInBackground(String... params) {
        boolean powerUpResult = PowerManage.getInstance().powerUp();
        if(!powerUpResult) {
            Log.e("initTask","模组上电失败");
        }
        SystemClock.sleep(1000);
        LinkInter mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);;
        //手持机（ttyHSL0）
        long startTime = System.currentTimeMillis();
        int initState = mLinker.initInventory(getDevTty(), baudrate);
        Log.e("test","初始化时间 = "+(System.currentTimeMillis()-startTime));
        if (initState == 0) {
            //初始化天线
//            mLinker.enableAnt(0, 330, 2000);
//            mLinker.enableAnt(1, 300, 200);//增强天线

            //初始化保存的温控配置
            RunLevelConfigBean runLevelConfigBean = (RunLevelConfigBean) SharedPreferencesUtil.getObject(context,"runLevelConfigBean");
            if(runLevelConfigBean != null){
                mLinker.setRunLevelConfig(runLevelConfigBean);
            }
            //初始化保存的低电量配置
            BatteryConfigBean batteryConfigBeanSave = (BatteryConfigBean)SharedPreferencesUtil.getObject(context,"batteryConfigBean");
            if(batteryConfigBeanSave != null){
                mLinker.setBatteryConfigBean(batteryConfigBeanSave);
                if(AndroidPlatform.batteryManager == null){
                    AndroidPlatform.batteryManager = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
                }
            }
        }
        return initState;
    }

    @Override
    protected void onPostExecute(Integer initState) {
        //LinkInter mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);
        if (initState == 0) {
            Toast.makeText(context, "初始化连接成功！", Toast.LENGTH_SHORT).show();

        } else if (initState == -1) {
            Toast.makeText(context, "连接失败 -1 (连接设备失败)", Toast.LENGTH_SHORT).show();

        } else if (initState == -2) {
            Toast.makeText(context, "连接失败 -2(获取天线信息失败)", Toast.LENGTH_SHORT).show();
        }else if (initState == -3) {
            Toast.makeText(context, "连接失败 -3(模组上电失败)", Toast.LENGTH_SHORT).show();
        }
        DialogUIUtils.dismiss(dialog);

//        if(initState !=0){
//            openCloseDialog("请重启应用","初始化失败",context);
//        }

    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public static String getDevTty(){
        String deviceSerial = android.os.Build.SERIAL;
        Log.e("deviceSerial",deviceSerial);
        if(deviceSerial.contains("MT90")){//新大陆
            return "/dev/ttyMT0";
        }
//        else if(deviceSerial.contains("unknow")){//苏宁工控版
//            return "/dev/ttymxc4";
//        }
        else{//肖邦
            //关闭按钮扫条码功能
            try{
                ReaderManager mReaderManager = ReaderManager.getInstance();//只有二维码功能读写器支持
//                mReaderManager.SetActive(false);//二维码对rfid扫描有影响需关闭
//                mReaderManager.setEnableScankey(false);//连续按会开启，bug，屏蔽按钮
            }catch (NoClassDefFoundError e){
                Log.e("rfid","mReaderManager init failed NoClassDefFoundError");
            }catch (Exception e){
                Log.e("rfid","mReaderManager init failed " + e.getMessage());
            }
            //return "/dev/ttyHSL0";
            String devicePlatform = getSystemProperty("ro.board.platform");
            if("mt6735".equals(devicePlatform)){//肖邦mtk
                return "/dev/ttyMT3";
            }else{//肖邦高通
                return "/dev/ttyHSL0";
                //return "/dev/ttyMT4";
            }
        }

    }

//    public static boolean rootCommand(String command) {
//        Process process = null;
//        DataOutputStream os = null;
//        try {
//            process = Runtime.getRuntime().exec("su");
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes(command + "\n");
//            os.writeBytes("exit\n");
//            os.flush();
//            process.waitFor();
//        } catch (Exception e) {
//            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
//            return false;
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                process.destroy();
//            } catch (Exception e) {
//                // nothing
//            }
//        }
//
//        Log.d("*** DEBUG ***", "Root SUC ");
//        return true;
//
//    }

    private static String getSystemProperty(String propName) {
        BufferedReader input = null;

        Object var4;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String line = input.readLine();
            input.close();
            return line;
        } catch (IOException var14) {
            var14.printStackTrace();
            var4 = null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        return (String)var4;
    }

//    public void PowerOn()
//    {
//        native_SetDTRLevel(1);
//        native_SetRTSLevel(1);
//    }
//
//    public void PowerOff()
//    {
//        native_SetDTRLevel(0);
//        native_SetRTSLevel(0);
//    }


//    public native int native_close_fd(FileDescriptor fd);
//    public native int native_setFlowcontrol(int flow);
//    public native int native_SetDTRLevel(int level);
//    public native int native_SetRTSLevel(int level);
//    public native int native_GetDTRLevel();
//    public native int native_GetRTSLevel();
//
//    static {
//        System.loadLibrary("deviceSerial");
//    }
}
