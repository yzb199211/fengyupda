package com.yyy.fengyupda.scan.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.yyy.fengyupda.R;

/**
 * 盘点声音
 */
public class SoundUtil {

    public static SoundPool soundPool;

    public static void initSoundUtil(Context context){
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(context, R.raw.beep, 1);
        soundPool.load(context, R.raw.beep5per1s, 1);
    }

    public static void playSound(){
        soundPool.play(1, 1, 1, 0, 0, 1);

    }
    public static void playSoundError(){
        soundPool.play(2, 1, 1, 0, 0, 1);

    }
}
