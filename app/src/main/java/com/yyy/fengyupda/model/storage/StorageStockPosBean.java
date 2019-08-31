package com.yyy.fengyupda.model.storage;


import com.yyy.fengyupda.wheel.interfaces.IPickerViewData;

public class StorageStockPosBean implements IPickerViewData {
    /**
     * iRecNo : 75788
     * sBerChID : A01
     */

    private int iRecNo;
    private String sBerChID;

    public StorageStockPosBean(int iRecNo, String sBerChID) {
        this.iRecNo = iRecNo;
        this.sBerChID = sBerChID;
    }

    public int getIRecNo() {
        return iRecNo;
    }

    public void setIRecNo(int iRecNo) {
        this.iRecNo = iRecNo;
    }

    public String getSBerChID() {
        return sBerChID;
    }

    public void setSBerChID(String sBerChID) {
        this.sBerChID = sBerChID;
    }

    @Override
    public String getPickerViewText() {
        return sBerChID;
    }
}