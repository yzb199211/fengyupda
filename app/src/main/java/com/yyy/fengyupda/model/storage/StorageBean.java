package com.yyy.fengyupda.model.storage;

import java.util.List;

public class StorageBean {

    private List<StorageStockMBean> BscDataStockM;
    private List<StorageCustomerBean> BscDataCustomer;
    private List<StorageCustomerBean> SDSendM;

    public List<StorageCustomerBean> getSDSendM() {
        return SDSendM;
    }

    public void setSDSendM(List<StorageCustomerBean> SDSendM) {
        this.SDSendM = SDSendM;
    }

    public List<StorageStockMBean> getBscDataStockM() {
        return BscDataStockM;
    }

    public void setBscDataStockM(List<StorageStockMBean> BscDataStockM) {
        this.BscDataStockM = BscDataStockM;
    }

    public List<StorageCustomerBean> getBscDataCustomer() {
        return BscDataCustomer;
    }

    public void setBscDataCustomer(List<StorageCustomerBean> BscDataCustomer) {
        this.BscDataCustomer = BscDataCustomer;
    }

}

