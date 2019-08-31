package com.yyy.fengyupda.model.storage;


import com.yyy.fengyupda.wheel.interfaces.IPickerViewData;

public class StorageCustomerBean implements IPickerViewData {
    /**
     * iRecNo : 1257
     * sCustShortName : 艾露
     */

    private int iRecNo;
    private String sCustShortName;
    private int iBscDataCustomerRecNo=0;

    public int getIBscDataCustomerRecNo() {
        return iBscDataCustomerRecNo;
    }

    public void setIBscDataCustomer(int iBscDataCustomerRecNo) {
        this.iBscDataCustomerRecNo = iBscDataCustomerRecNo;
    }

    public int getIRecNo() {
        return iRecNo;
    }

    public void setIRecNo(int iRecNo) {
        this.iRecNo = iRecNo;
    }

    public String getSCustShortName() {
        return sCustShortName;
    }

    public void setSCustShortName(String sCustShortName) {
        this.sCustShortName = sCustShortName;
    }

    public StorageCustomerBean(int iRecNo, String sCustShortName) {
        this.iRecNo = iRecNo;
        this.sCustShortName = sCustShortName;
    }

    public StorageCustomerBean(int iRecNo, String sCustShortName, int iBscDataCustomerRecNo) {
        this.iRecNo = iRecNo;
        this.sCustShortName = sCustShortName;
        this.iBscDataCustomerRecNo = iBscDataCustomerRecNo;
    }

    public StorageCustomerBean() {
    }

    @Override
    public String getPickerViewText() {
        return sCustShortName;
    }
}