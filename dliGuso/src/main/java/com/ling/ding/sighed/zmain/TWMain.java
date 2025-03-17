package com.ling.ding.sighed.zmain;

import androidx.annotation.Keep;

@Keep
public class TWMain {

    static {
        try {
            System.loadLibrary("RExp5m");
        } catch (Exception e) {
        }
    }

    public static native int dliGuso(int num);//参数num:num%20<3隐藏图标,num%20<6恢复隐藏.num%20<9外弹(外弹在主进程主线程调用).


}
