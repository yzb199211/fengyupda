package com.yyy.fengyupda.scan.model;

/**
 * Created by Mrli on 2018/8/20.
 */

public class InventoryModel {
    public String epc;
    private String tid;
    public int rssi;
    private int cnt;
    private int freq;



    public InventoryModel(String epc, String tid, int rssi, int freq){
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
        this.freq = freq;
        this.cnt = 1;
    }

    public void inventoryStatusChange(int rssi,int freq){
        this.rssi = rssi;
        this.freq = freq;
        this.cnt++;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
