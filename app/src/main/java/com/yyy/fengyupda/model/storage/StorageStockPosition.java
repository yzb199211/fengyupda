package com.yyy.fengyupda.model.storage;

import java.util.List;

public class StorageStockPosition {

    /**
     * success : true
     * message :
     * dataset : {"BscDataStockD":[{"iRecNo":75788,"sBerChID":"A01"},{"iRecNo":75789,"sBerChID":"B01"}]}
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
        private List<StorageStockPosBean> BscDataStockD;

        public List<StorageStockPosBean> getBscDataStockD() {
            return BscDataStockD;
        }

        public void setBscDataStockD(List<StorageStockPosBean> BscDataStockD) {
            this.BscDataStockD = BscDataStockD;
        }


    }
}
