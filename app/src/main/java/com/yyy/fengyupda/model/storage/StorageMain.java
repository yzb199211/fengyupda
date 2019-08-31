package com.yyy.fengyupda.model.storage;

import java.util.List;

public class StorageMain {

    /**
     * success : true
     * message :
     * dataset : {"result":[{"result":3045}]}
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
        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * result : 3045
             */

            private int result;

            public int getResult() {
                return result;
            }

            public void setResult(int result) {
                this.result = result;
            }
        }
    }
}
