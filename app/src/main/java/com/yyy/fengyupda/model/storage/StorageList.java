package com.yyy.fengyupda.model.storage;

public class StorageList {

    /**
     * success : true
     * message :
     * dataset : {"ListProductM":[{"iRecNo":3042,"sBillNo":"MR1906-0001","dDate":"2019-06-20T00:00:00","iRed":0,"iQty":0,"sStockName":"成品仓","sCustShortName":"成衣供应商","sStatusName":"未提交","sReMark":"","iBscDataStockMRecNo":203,"iBscDataCustomerRecNo":1230}]}
     */

    private boolean success;
    private String message;
    private StorageListBean dataset;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StorageListBean getDataset() {
        return dataset;
    }

    public void setDataset(StorageListBean dataset) {
        this.dataset = dataset;
    }

}
