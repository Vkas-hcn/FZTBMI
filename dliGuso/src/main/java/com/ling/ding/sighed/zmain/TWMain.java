package com.ling.ding.sighed.zmain;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.Keep;

@Keep
public class TWMain {

    static {
        try {
            System.loadLibrary("zoret");
        } catch (Exception e) {
        }
    }

    public static native boolean dliGuso(int num);//参数num%10==4隐藏图标,num%10==2恢复隐藏.num%10==8外弹(外弹在主进程主线程调用).
    public static native void newJNIFunction1();
    public static native int newJNIFunction2(String param);
    public static native void newJNIFunction3(int param1, double param2);

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
    private static final int GARBAGE_CONSTANT = 12345;

    public static Integer garbageMethod1() {
        int a = 0;
        for (int i = 0; i < 1000; i++) {
            a += i;
        }
        System.out.println("Garbage Method 1: " + a);
        return a;
    }

    public static String garbageMethod2(int param) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param; i++) {
            sb.append("a");
        }
        return sb.toString();
    }

    private void garbageMethod3() {
        if (GARBAGE_CONSTANT == 12345) {
            System.out.println("Garbage Constant is 12345");
        } else {
            System.out.println("Garbage Constant is not 12345");
        }
    }
}
