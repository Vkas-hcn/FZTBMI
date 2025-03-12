package com.ling.ding.sighed.zwthf;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Keep;

import com.ling.ding.sighed.adtool.ShowDataTool;
import com.ling.ding.sighed.zmain.HFiveMain;

@Keep
public class WTNNChanged extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);

        if (i10 == 100) {
            ShowDataTool.INSTANCE.showLog(" onPageStarted=url=");
            HFiveMain.hfcanLast(i10);
        }
    }
}
