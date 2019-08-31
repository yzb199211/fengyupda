package com.yyy.fengyupda.main;

public class Menu {
    int id = -1;
    int img;
    String imgStr;
    String str;
    int msg = 0;

    public Menu(int id, String imgStr, String str, int msg) {
        this.id = id;
        this.img = img;
        this.imgStr = imgStr;
        this.str = str;
        this.msg = msg;
    }

    public Menu(int id, int img, String str, int msg) {
        this.id = id;
        this.img = img;
        this.str = str;
        this.msg = msg;
    }

    public Menu(int id, int img, String str) {
        this.id = id;
        this.img = img;
        this.str = str;
    }

    public String getImgStr() {
        return imgStr;
    }

    public void setImgStr(String imgStr) {
        this.imgStr = imgStr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }
}
