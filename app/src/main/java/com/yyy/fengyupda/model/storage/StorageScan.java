package com.yyy.fengyupda.model.storage;

import java.util.List;

public class StorageScan {

    /**
     * success : true
     * message :
     * dataset : {"sBarCode":[{"sBarCode":"101010891001","sStyleNo":"TEST","sColorName":"HONG ","sSizeName":"S"}]}
     */

    private boolean success;
    private String message;
    private DatasetBean dataset;

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

    public DatasetBean getDataset() {
        return dataset;
    }

    public void setDataset(DatasetBean dataset) {
        this.dataset = dataset;
    }

    public static class DatasetBean {
        private List<StorageScanBean> sBarCode;

        public List<StorageScanBean> getSBarCode() {
            return sBarCode;
        }

        public void setSBarCode(List<StorageScanBean> sBarCode) {
            this.sBarCode = sBarCode;
        }

    }
}
