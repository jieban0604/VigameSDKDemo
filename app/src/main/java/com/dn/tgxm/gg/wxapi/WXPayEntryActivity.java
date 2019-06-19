package com.dn.tgxm.gg.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.libSocial.WeChat.WeChatPayActivityHandler;

public class WXPayEntryActivity extends Activity {
    private WeChatPayActivityHandler weChatPayActivityHandler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weChatPayActivityHandler = new WeChatPayActivityHandler();
        weChatPayActivityHandler.onCreate(this);
        this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (weChatPayActivityHandler != null) {
            weChatPayActivityHandler.onNewIntent(intent);
        }
    }
}