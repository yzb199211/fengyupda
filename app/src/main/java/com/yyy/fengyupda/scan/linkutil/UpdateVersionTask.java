package com.yyy.fengyupda.scan.linkutil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.ruijie.uhflib.power.manage.PowerManage;
import com.ruijie.uhflib.uhf.LinkInter;
import com.ruijie.uhflib.uhf.manage.LinkManage;

/**
 * 固件升级
 */
public class UpdateVersionTask extends AsyncTask<String, Integer, Boolean> {
    private Dialog dialog;
    private Context context;
    private byte[] fileByte;
    public UpdateVersionTask(Context context, byte[] fileByte){
        this.context = context;
        this.fileByte = fileByte;
    }

    @Override
    protected void onPreExecute(){
        dialog = DialogUIUtils.showLoading(context, "升级中...", false, false, false, true).show();

    }

    @Override
    protected Boolean doInBackground(String... params) {
        LinkInter mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);;
        boolean result = mLinker.upgrade(fileByte,fileByte.length);
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //LinkInter mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);
        DialogUIUtils.dismiss(dialog);
        if (result) {
            Toast.makeText(context, "升级成功，请重启系统！", Toast.LENGTH_SHORT).show();
            openCloseDialog("请重启应用","升级完成");
        } else {
            Toast.makeText(context, "升级失败", Toast.LENGTH_SHORT).show();

        }

    }

    private void openCloseDialog(String strMsg, String strTitle){
        new AlertDialog.Builder(context).setTitle(strTitle)
                .setMessage(strMsg)
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PowerManage.getInstance().powerDown();
                                System.exit(0);
                            }
                        })
                .setCancelable(false)
                .show();
    }

}
