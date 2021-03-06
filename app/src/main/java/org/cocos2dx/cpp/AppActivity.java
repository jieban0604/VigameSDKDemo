/****************************************************************************
 Copyright (c) 2008-2010 Ricardo Quesada
 Copyright (c) 2010-2012 cocos2d-x.org
 Copyright (c) 2011      Zynga Inc.
 Copyright (c) 2013-2014 Chukong Technologies Inc.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.cpp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.libAD.ADDef;
import com.libAD.ADManager;
import com.libAD.ADParam;
import com.libPay.BasePayAgent;
import com.libPay.PayDef;
import com.libPay.PayManager;
import com.libPay.PayParams;
import com.libSocial.SocialManager;
import com.libVigame.VigameLoader;
import com.vigame.CDKey;
import com.vigame.CoreNative;
import com.vigame.Feedback;
import com.vigame.Notice;
import com.vigame.Rank;
import com.vigame.UserAgreement;
import com.vigame.ad.ADNative;
import com.vigame.pay.PayNative;
import com.example.myapplication.R;
import com.vigame.tj.TJNative;
import com.vigame.xyx.XYXConfig;
import com.vigame.xyx.XYXItem;
import com.vigame.xyx.XYXNative;

import java.util.ArrayList;
import java.util.Random;

public class AppActivity extends Activity {
    private static final String TAG = "AppActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        VigameLoader.setOnResetGameFocus(new Runnable() {
            @Override
            public void run() {
                Log.d("AppActivity", "Need RequestFocus");
//                Cocos2dxGLSurfaceView.getInstance().requestFocus();
            }
        });
        VigameLoader.activityOnCreate(this);
        //模块初始化
        CoreNative.init();
        PayNative.init();
        ADNative.init();
        TJNative.init();
        //互推初始化
        XYXNative.init();
        String value = ADNative.getAdPositionParam("pause", "test");
        //设置广告回调
        ADNative.setAdResultCallback(mADCallback);
        //设置支付回调
        PayNative.setPayResultCallback(mPayCallback);
        //设置关闭当前应用的代码
        PayNative.setGameExitCallback(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                CoreNative.gameExit();
                System.exit(0);
            }
        });
        SocialManager.getInstance().init();

        freshButtonString();
    }

    ADManager.AdParamCallback mADCallback = new ADManager.AdParamCallback() {

        @Override
        public void onStatusChanged(ADParam arg0, int arg1) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onRequestAddGameCoin(ADParam arg0, int arg1, int arg2,
        String arg3) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onRequestAddGameCoin");

        }

        @Override
        public void onOpenResult(ADParam adParam, int result) {
            // TODO Auto-generated method stub
            //判断广告是否打开成功
            if (result == ADParam.ADOpenResult_Success) {

            }
            //获取广告位名称
            String positionName = adParam.getPositionName();

            //判断广告类型是否是视频
            if (adParam.getType().equals(ADDef.AD_TypeName_Video)) {

            }

            String log = "onOpenResult:" + result + ",type=" + adParam.getType() + ",agent=" + adParam.getAgentName() + ",positionName=" + positionName;
            Log.d(TAG, log);
            Toast.makeText(AppActivity.this, log, Toast.LENGTH_SHORT).show();
        }
    };
    PayManager.PayCallback mPayCallback = new PayManager.PayCallback() {

        @Override
        public void onPayFinish(PayParams params) {
            // TODO Auto-generated method stub
            switch (params.getPayResult()) {
                //支付成功
                case PayDef.PAY_RESULT_SUCCESS:
                    break;
                //支付失败
                case PayDef.PAY_RESULT_FAIL:
                    break;
                //取消支付
                case PayDef.PAY_RESULT_CANCEL:
                    break;
            }
            freshButtonString();
            showToast(params.getReason());
        }

        @Override
        public void onInitPayAgentFinish(BasePayAgent arg0) {
            // TODO Auto-generated method stub

        }
    };

    void freshButtonString() {
        int type = PayNative.getButtonType(1101);
        Button btn = (Button) findViewById(R.id.btn_pay);
        if (type == 1) {
            btn.setText("领取");
        } else {
            btn.setText("购买");
        }
    }

    @Override
    protected void onDestroy() {
        VigameLoader.activityOnDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VigameLoader.activityOnResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VigameLoader.activityOnPause(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        VigameLoader.activityOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VigameLoader.activityOnActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        PayNative.openExitGame();
    }

    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.btn_openad1:
                ADNative.openAd("pause");
                break;
            case R.id.btn_openad2:
                ADNative.openAd("banner");
                break;
            case R.id.btn_openad3:
                //获取可播放视频次数
                int num = ADNative.getVideoLimitOpenNum();
                Log.d(TAG, "VideoLimitOpenNum = " + num);
                ADNative.openAd("home_mfzs");
                break;
            case R.id.btn_openad33:
                //判断第5关能不能弹出广告
                boolean isAd = ADNative.isAdBeOpenInLevel("level_fail_mfzs", 5);
                if (isAd)
                    ADNative.openAd("level_fail_mfzs");
                break;
            case R.id.btn_openad44:
                DisplayMetrics dm = this.getResources().getDisplayMetrics();
                int width = dm.widthPixels * 4 / 5;
                int height = dm.heightPixels / 4;
                int x = (dm.widthPixels-width) / 2;
                int y = (dm.heightPixels-height) / 2;
                Log.i(TAG,"x="+x+",y="+y+",width="+width+",height="+height);
                ADNative.openAd("yuans", width, height, x, y);
                break;
            case R.id.btn_closeAd:
                ADNative.closeAd("banner");
                break;
            case R.id.btn_dhm:
                String dhm = "111111";
                CDKey.use(dhm, new CDKey.DhmCallback() {

                    @Override
                    public void onFinish(CDKey.DhmData data) {
                        // TODO Auto-generated method stub
                        showToast(data.message);
                    }
                });

                break;
            case R.id.btn_getCtrlFlag:
                int flag = PayNative.getGiftCtrlFlagUse(0);
                showToast("CtrlFlag = " + flag);
                break;
            case R.id.btn_showExit:
                PayNative.openExitGame();
                break;
            case R.id.btn_openActivity:
                com.vigame.Activity.open();
                break;
            case R.id.btn_openNotice:
                Notice.open();
                break;
            case R.id.btn_openRank:
                Rank.open();
                break;
            case R.id.btn_openUseragreement:
                UserAgreement.open();
                break;
            case R.id.btn_pay:
                this.showPayDialog();
                break;
            case R.id.btn_xyxOnShow: {
                XYXConfig config = XYXNative.getConfig();
                ArrayList<XYXItem> list = config.getItemList();
                if (list != null && list.size() > 0) {
                    XYXItem item = list.get(0);
                    config.exposureShow(item);
                }
            }
            break;
            case R.id.btn_xyxOnClick: {
                XYXConfig config = XYXNative.getConfig();
                ArrayList<XYXItem> list = config.getItemList();
                if (list != null && list.size() > 0) {
					//此处随机仅为了测试，产品中应以实际索引为参数
                    int index = new Random().nextInt(list.size());
                    XYXItem item = list.get(index);
                    config.handleClick(item);
                }
            }
            break;
            case R.id.btn_feedback: {
                Feedback.open();
            }
            break;
        }
    }

    void showToast(final String text) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(AppActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VigameLoader.activityOnRestart(this);
    }


    private void showPayDialog(){
        final String[] items = { "支付宝","微信","默认" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(AppActivity.this);
        listDialog.setTitle("请选择支付方式");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        PayNative.orderPay(1, 1, PayDef.PAY_TYPE_AliPay,"");
                        break;
                    case 1:
                        PayNative.orderPay(1, 1, PayDef.PAY_TYPE_WXPay,"");
                        break;
                    case 2://默认支付类型通过ConfigPay.xml配置
                        PayNative.orderPay(1);
                        break;
                }
            }
        });
        listDialog.show();
    }
}
