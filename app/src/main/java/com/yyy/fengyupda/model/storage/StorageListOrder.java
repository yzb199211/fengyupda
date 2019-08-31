package com.yyy.fengyupda.model.storage;

public class StorageListOrder {

    /**
     * iRecNo : 3042
     * sBillNo : MR1906-0001
     * dDate : 2019-06-20T00:00:00
     * iRed : 0
     * iQty : 0
     * sStockName : 成品仓
     * sCustShortName : 成衣供应商
     * sStatusName : 未提交
     * sReMark :
     * iBscDataStockMRecNo : 203
     * iBscDataCustomerRecNo : 1230
     */

    private int iRecNo;
    private String sBillNo;
    private String dDate;
    private int iRed;
    private int iQty;
    private String sStockName;
    private String sCustShortName;
    private String sStatusName;
    private String sSdSendNo;
    private String sReMark;
    private int iBscDataStockMRecNo;
    private int iBscDataCustomerRecNo;
    private int iSDSendMRecNo;

    public String getSSdSendNo() {
        return sSdSendNo;
    }

    public void setSSdSendNo(String sSdSendNo) {
        this.sSdSendNo = sSdSendNo;
    }

    public int getISDSendMRecNo() {
        return iSDSendMRecNo;
    }

    public void setISDSendMRecNo(int iSDSendMRecNo) {
        this.iSDSendMRecNo = iSDSendMRecNo;
    }

    public int getIRecNo() {
        return iRecNo;
    }

    public void setIRecNo(int iRecNo) {
        this.iRecNo = iRecNo;
    }

    public String getSBillNo() {
        return sBillNo;
    }

    public void setSBillNo(String sBillNo) {
        this.sBillNo = sBillNo;
    }

    public String getDDate() {
        return dDate;
    }

    public void setDDate(String dDate) {
        this.dDate = dDate;
    }

    public int getIRed() {
        return iRed;
    }

    public void setIRed(int iRed) {
        this.iRed = iRed;
    }

    public int getIQty() {
        return iQty;
    }

    public void setIQty(int iQty) {
        this.iQty = iQty;
    }

    public String getSStockName() {
        return sStockName;
    }

    public void setSStockName(String sStockName) {
        this.sStockName = sStockName;
    }

    public String getSCustShortName() {
        return sCustShortName;
    }

    public void setSCustShortName(String sCustShortName) {
        this.sCustShortName = sCustShortName;
    }

    public String getSStatusName() {
        return sStatusName;
    }

    public void setSStatusName(String sStatusName) {
        this.sStatusName = sStatusName;
    }

    public String getSReMark() {
        return sReMark;
    }

    public void setSReMark(String sReMark) {
        this.sReMark = sReMark;
    }

    public int getIBscDataStockMRecNo() {
        return iBscDataStockMRecNo;
    }

    public void setIBscDataStockMRecNo(int iBscDataStockMRecNo) {
        this.iBscDataStockMRecNo = iBscDataStockMRecNo;
    }

    public int getIBscDataCustomerRecNo() {
        return iBscDataCustomerRecNo;
    }

    public void setIBscDataCustomerRecNo(int iBscDataCustomerRecNo) {
        this.iBscDataCustomerRecNo = iBscDataCustomerRecNo;
    }
}

