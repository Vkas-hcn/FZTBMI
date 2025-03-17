package com.ling.ding.sighed.zwthf;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Keep;

import com.ling.ding.sighed.zmain.HFiveMain;

@Keep
public class FmessT extends Handler {
    public FmessT() {

    }
    @Override
    public void handleMessage(Message message) {
        int r0 = message.what;
        HFiveMain.hfcanLast(r0);
    }
}

