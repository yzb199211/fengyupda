package com.yyy.fengyupda.scan.linkutil;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;


import com.dou361.dialogui.DialogUIUtils;
import com.yyy.fengyupda.scan.model.InventoryModel;
import com.yyy.fengyupda.scan.util.FileComparator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 盘点结果保存文件
 */
public class SaveDataTask extends AsyncTask<String, Integer, String> {

    private Dialog dialog;
    private Context context;
    private List<InventoryModel> infoList;
    private Integer noMatch = 0;

    public SaveDataTask(Context context, List<InventoryModel> infoList){
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    protected void onPreExecute(){
        dialog = DialogUIUtils.showLoading(context, "数据保存中...", false, false, false, true).show();

    }
    @Override
    protected String doInBackground(String... params) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory()+"/checkResultHistory");
            if(!dir.exists()){
                dir.mkdir();
            }
//            for(int i=0;i<infoList.size();i++){
//                skuMap.put(SkuUtil.calSkuFromEpc(infoList.get(i)),1);
//            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date date = new Date();
            File file = new File(Environment.getExternalStorageDirectory()+"/checkResultHistory", df.format(date)+".txt");
            FileOutputStream fos = new FileOutputStream(file);
            String info = "";
            StringBuilder sb = new StringBuilder();
            for(int i=1;i<=infoList.size();i++){
                //for (BizGoodsInTaskSkuDetailParams entry : infoList) {
                if(i != infoList.size()){
                    sb.append(infoList.get(i-1).getEpc()).append("\r\n");
                }else{
                    sb.append(infoList.get(i-1).getEpc());
                }

            }
            fos.write(sb.toString().getBytes());
            fos.close();
            // 删除
            // 先按照最后修改时间排序
            File[] files = dir.listFiles();
            List<File> fileList = new ArrayList<>();
            for (int i=0; i < files.length; i++){
                fileList.add(files[i]);
            }
            Collections.sort(fileList, new FileComparator());

            int fileCount =0;
            for(int i=0; i < fileList.size(); i++){
                fileCount++;
                if(fileCount > 30){
                    fileList.get(i).delete();
                }
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    @Override
    protected void onPostExecute(String result) {
        if(result==null ){
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }
        DialogUIUtils.dismiss(dialog);

    }

}