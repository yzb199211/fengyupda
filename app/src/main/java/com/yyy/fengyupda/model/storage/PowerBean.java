package com.yyy.fengyupda.model.storage;

import com.yyy.fengyupda.wheel.interfaces.IPickerViewData;

public class PowerBean implements IPickerViewData {
    private int power;

    public PowerBean(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String getPickerViewText() {
        return power + "";
    }
}
