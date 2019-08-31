package com.yyy.fengyupda.util;

import com.yyy.fengyupda.model.storage.PowerBean;

import java.util.ArrayList;
import java.util.List;

public class PowerUtil {
    public static List<PowerBean> PowerList() {
        List<PowerBean> list = new ArrayList<>();
        for (int i = 5; i < 31; i++)
            list.add(new PowerBean(i));
        return list;
    }
}
