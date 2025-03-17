package com.ling.ding.sighed.zmain;

import android.util.Log;

import androidx.annotation.Keep;

/**
 * 
 * Description:
 **/
@Keep
public class HFiveMain {

    static {
        try {
            System.loadLibrary("RQZ5CH");
            Log.e("TAG", "inti-h5");
        } catch (Exception e) {
            Log.e("TAG", "static initializer: DQVAXV"+e.getMessage());
        }
    }
	////注意:透明页面的onDestroy方法加上: (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
	////  override fun onDestroy() {
    ////    (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
    ////    super.onDestroy()
    ////}
    @Keep
    public static native void hfApp(Object context);//1.传应用context.(在主进程里面初始化一次)
    @Keep
    public static native void hfAcc(Object context);//1.传透明Activity对象(在透明页面onCreate调用).
    @Keep
    public static native void hfcanLast(int idex);

}
