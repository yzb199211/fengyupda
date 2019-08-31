package com.yyy.fengyupda.model.storage;

public class StorageScanBean {
    private int iBscDataStockDRecNo;
    private int type = 1;
    private int count = 1;
    private String sBerChID = "";
    private String sColorName;
    private String sSizeName;
    private String sStyleNo;
    private String sBarCode;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getiBscDataStockDRecNo() {
        return iBscDataStockDRecNo;
    }

    public void setiBscDataStockDRecNo(int iBscDataStockDRecNo) {
        this.iBscDataStockDRecNo = iBscDataStockDRecNo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getsBerChID() {
        return sBerChID;
    }

    public void setsBerChID(String sBerChID) {
        this.sBerChID = sBerChID;
    }

    public String getsColorName() {
        return sColorName;
    }

    public void setsColorName(String sColorName) {
        this.sColorName = sColorName;
    }

    public String getsSizeName() {
        return sSizeName;
    }

    public void setsSizeName(String sSizeName) {
        this.sSizeName = sSizeName;
    }

    public String getsStyleNo() {
        return sStyleNo;
    }

    public void setsStyleNo(String sStyleNo) {
        this.sStyleNo = sStyleNo;
    }

    public String getsBarCode() {
        return sBarCode;
    }

    public void setsBarCode(String sBarCode) {
        this.sBarCode = sBarCode;
    }
}
