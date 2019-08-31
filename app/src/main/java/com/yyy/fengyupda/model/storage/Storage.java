package com.yyy.fengyupda.model.storage;

public class Storage {

    /**
     * success : true
     * message :
     * dataset : {"BscDataStockM":[{"iRecNo":238,"sStockName":"电商仓"},{"iRecNo":203,"sStockName":"成品仓"}],"BscDataCustomer":[{"iRecNo":1257,"sCustShortName":"艾露"},{"iRecNo":1275,"sCustShortName":"22"},{"iRecNo":1230,"sCustShortName":"成衣供应商"},{"iRecNo":1260,"sCustShortName":"测试供应商9977"},{"iRecNo":1253,"sCustShortName":"成品本厂"},{"iRecNo":1262,"sCustShortName":"曾多次"},{"iRecNo":1259,"sCustShortName":"供应商1"},{"iRecNo":1238,"sCustShortName":"路人甲2"},{"iRecNo":1252,"sCustShortName":"1007"},{"iRecNo":1241,"sCustShortName":"物料供应商"},{"iRecNo":1236,"sCustShortName":"路人甲"},{"iRecNo":1240,"sCustShortName":"路人甲21"},{"iRecNo":1256,"sCustShortName":"路人乙"}]}
     */

    private boolean success;
    private String message;
    private StorageBean dataset;

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

    public StorageBean getDataset() {
        return dataset;
    }

    public void setDataset(StorageBean dataset) {
        this.dataset = dataset;
    }


}
