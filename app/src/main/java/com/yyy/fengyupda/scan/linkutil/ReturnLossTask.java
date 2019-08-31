package com.yyy.fengyupda.scan.linkutil;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.dou361.dialogui.DialogUIUtils;
import com.ruijie.uhflib.uhf.LinkInter;
import com.ruijie.uhflib.uhf.manage.LinkManage;

/**
 * 回波损耗测试
 */
public class ReturnLossTask extends AsyncTask<String, Integer, Boolean> {
    private int regionDiv;
    private Dialog dialog;
    private Context context;
    private float[] returnLossArray;
    private Handler returnLossHandler;

    public ReturnLossTask(int regionDiv, Context context, Handler returnLossHandler){
        this.regionDiv = regionDiv;
        this.context = context;
        this.returnLossHandler = returnLossHandler;
    }

    @Override
    protected void onPreExecute(){
        dialog = DialogUIUtils.showLoading(context, "回波检测中...", false, false, false, true).show();

    }

    @Override
    protected Boolean doInBackground(String... params) {
        LinkInter mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);
        this.returnLossArray = mLinker.startReadAntennaChara(0,regionDiv);
        if(this.returnLossArray != null){
            return true;
        }else{
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        DialogUIUtils.dismiss(dialog);
        Message mes = returnLossHandler.obtainMessage();
        mes.what = 1;
        mes.obj = this.returnLossArray;
        returnLossHandler.sendMessage(mes);
    }

}
