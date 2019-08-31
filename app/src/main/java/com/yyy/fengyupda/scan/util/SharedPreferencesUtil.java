package com.yyy.fengyupda.scan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SharedPreferencesUtil {

    public static void saveObject(Context context, String key, Object obj){
        if(obj instanceof Serializable) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("ServerConf", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);//把对象写到流里
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString(key, temp);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            throw new RuntimeException("Object must implements Serializable");
        }
    }

    public static Object getObject(Context context, String key) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("ServerConf",context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, "");
        if("".equals(temp)){
            return null;
        }
        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        }catch (Exception e) {
            Log.e("rfid","getObject:"+e.getMessage());
        }
        return obj;
    }


}
