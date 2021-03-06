
~~（gitbook线上文档：http://vigame.cn/_book/index.html）~~

# 修订日志  

日期 | 版本 | 内容 | 修订者
---|---|---|---
2016/11/10 | V1.1.0 | 支付方法中增加userdata参数 | Tyson
2017/01/04 | V1.1.1 | 增加支付和广告新的接口方法 | Tyson
2018/04/19 | V1.2.0 | 重新整合SDK及Demo  | Tyson 
2018/05/08 | V1.2.1 | 增加统计初始化,视频限制,关卡限制 | Tyson
2018/08/11 | V1.2.2 | 增加互推数据获取和响应；增加isAdOpen | Tyson
2018/09/26 | V1.2.3 | 自动出现开屏；增加关卡广告控制 | Tyson
2019/05/08 | V1.2.4 | 增加首次权限提示；广告回调中可查到广告位名称；投诉反馈功能；广告更新 | Tyson
2019/06/26 | V1.2.5 | 支持64位cpu；支持原生广告；统计支持计算事件；广告更新 | Tyson
##### SDK下载地址：  

链接：https://pan.baidu.com/s/1O0eDJeuLCpm6jo4eeQ3uXw   
提取码：xkqi 


#### Demo克隆地址
https://github.com/jieban0604/VigameSDKDemo.git

# 1.在项目中集成SDK  
## 1.1导入SDK
### 1.1.1分别拷贝assets和libs,jniLibs目录下的内容到工程的同名目录  
### 1.1.2在应用的build.gradle中添加如下引用：


```
repositories {
    flatDir {
        dirs 'libs'//project(':app').file('libs')
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    //基础模块
    implementation 'com.android.support:support-v4:28.0.0'
    implementation 'com.android.support:percent:28.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation(name: 'libPushService-release', ext: 'aar')
    implementation(name: 'libGCOffers-release', ext: 'aar')
    implementation(name: 'libVigameLoader-release', ext: 'aar')

    //百度广告（仅供测试）
    //compile(name: 'libAD_Baidu-release', ext: 'aar')
    //支付宝支付（仅供测试）
    //compile(name: 'libPayExt_AliPay-release', ext: 'aar')
    //compile(name: 'AliPayApp-release', ext: 'aar')
}
```

## 1.2修改Manifest文件  


```
<meta-data
    android:name="com.vigame.sdk.appid"
    android:value="10001" />
<meta-data
    android:name="com.vigame.sdk.appkey"
    android:value="abcdefg" />
<meta-data
    android:name="com.vigame.sdk.prjid"
    android:value="333360" />
<meta-data
    android:name="com.vigame.purchase.channel"
    android:value="${WB_CHANNEL}" />

<activity android:name="com.libVigame.VigameStartActivity"
    android:theme="@android:style/Theme.NoTitleBar.Fullscreen">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
注意，此时启动的Activity已经是VigameStartActivity。请通过assets/ConfigVigame.xml设置启动的Activity。

## 1.3添加JAVA调用代码
### 1.3.1修改应用的Application，在以下生命周期加入调用代码。

```
public class MyApplication extends Application {
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        VigameLoader.applicationAttachBaseContext(this, base);
    }

    public void onCreate() {
        super.onCreate();
        VigameLoader.applicationOnCreate(this);
    }

}
```
### 1.3.2修改应用的主Activity，将以下代码加入到主Activity对应的生命周期中。

```
    VigameLoader.activityOnCreate(this);
    VigameLoader.activityOnDestroy(this);
    VigameLoader.activityOnResume(this);
    VigameLoader.activityOnPause(this);
    VigameLoader.activityOnWindowFocusChanged(this,hasFocus);
    VigameLoader.activityOnActivityResult(this, requestCode, resultCode, data);
    VigameLoader.activityOnRestart(this);
```



## 1.4 注意事项
### 1.4.1 关于targetSdkVersion
因sdk并未完全兼容最新的安卓系统，targetSdkVersion勿使用28及以上版本，建议使用26.

# 2.调用方法详解  
## 2.1基础功能模块  
### 2.1.1 初始化（必接）

```
CoreNative.init();
```

### 2.1.2 设置退出的代码（必接）

```
PayNative.setGameExitCallback(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CoreNative.gameExit();//该方法必须调用
				currentActivity.finish();
				System.exit(0);
			}
		});
```

### 2.1.3 使用兑换码

```
String dhm = "111111";
CDKey.use(dhm,new CDKey.DhmCallback() {

	@Override
	public void onFinish(CDKey.DhmData data) {
		// TODO Auto-generated method stub
		showToast(data.message);
	}
});
```

### 2.1.4 打开活动页面  

```
com.vigame.Activity.open();
```

### 2.1.5 打开公告

```
Notice.open();
```


### 2.1.6 打开排行榜


```
Rank.open();
```

### 2.1.7 打开用户协议


```
UserAgreement.open();
```
注：用户协议的内容可通过assets/agrement.html修改  

### 2.1.8 打开投诉反馈
*sdk1.2.4中加入*

```
Feedback.open();
```
注：用户协议的内容可通过assets/agrement.html修改  
## 2.2 计费相关  
### 2.2.1 初始化（必接）

```
PayNative.init();
```
### 2.2.2 设置支付回调

```
PayNative.setPayResultCallback(new PayManager.PayCallback() {

	@Override
	public void onPayFinish(PayParams params) {
		// TODO Auto-generated method stub
		switch (params.getPayResult()){
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
	}

	@Override
	public void onInitPayAgentFinish(BasePayAgent arg0) {
		// TODO Auto-generated method stub

	}
});
```
### 2.2.3 获取支付按钮类型


```
PayNative.getButtonType(payID);
```
payID--计费点ID  
返回值：0 购买  1领取

### 2.2.4 SDK中是否支持退出

```
PayNative.isExitGame();
```
返回值：true-SDK支持退出  false-不支持退出

### 2.2.5 调用退出（必接）

```
PayNative.openExitGame();
```

*因游戏有自己的退出界面，建议按照如下方法判断：*

```
if(PayNative.isExitGame()){
    //通过调用下面的方法弹出渠道的退出
    PayNative.openExitGame();
}
else{
    //这里弹出引擎的退出界面
    ...
}
```

### 2.2.6 获取所在的市场类型

```
PayNative.getMarketType();

```
返回值：参考PayDef中的定义，eg:PayDef.PAY_TYPE_MM  
### 2.2.7 获取礼包控制信息

```
PayNative.getGiftCtrlFlagUse(int ctrl);
```

ctrl：控制参数（  
1-新手礼包 - 进入游戏首页时如果没购买过新手礼包则弹出  
2-快冲 - 用户虚拟货币不足时弹出  
3-限时礼包 - 进入游戏首页时如果已经买过新手礼包则弹出  
4-过关奖励 - 过关时弹出  
5-vip礼包 - 进入关卡选择页时弹出
6-今日特价  
7-实惠大礼包  
8-商城） 

返回值：ctrl控制参数

### 2.2.8 调用支付  

```
PayNative.orderPay(id);
```
id:计费点ID  


```
orderPay(int id,String userdata);
```

id:游戏中定义的计费点id  
userdata:用户数据  


```
orderPay(int id,int price, String userdata);
```

id:游戏中定义的计费点id  
price:计费点的价格，单位为分。  
userdata:用户数据  


```
orderPay(int id, int price, int payType, String userdata);
```

id:游戏中定义的计费点id  
price:计费点的价格，单位为分。  
payType:指定的支付类型。eg:PayDef.PAY_TYPE_MM  
userdata:用户数据  

### 2.2.9 是否需要有更多游戏按钮  

```
PayNative.isMoreGame();
```

### 2.2.10 打开更多游戏  

```
PayNative.openMoreGame();
```

## 2.3 广告相关  
### 2.3.1 初始化（必接）

```
ADNative.init();
```

### 2.3.2 设置广告打开回调


```
//设置广告回调
ADNative.setAdResultCallback(new ADManager.AdParamCallback() {

	@Override
	public void onStatusChanged(ADParam arg0, int status) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onStatusChanged:"+status);
	}

	@Override
	public void onRequestAddGameCoin(ADParam arg0, int arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onRequestAddGameCoin");

	}

	@Override
	public void onOpenResult(ADParam adParam, int result) {
		// TODO Auto-generated method stub
		//获取广告位名称
		String positionName = adParam.getPositionName();
		Log.d(TAG,"onOpenResult:"+result+",positionName="+positionName);


    	//判断广告类型是否是视频
    	if(adParam.getType().equals(ADDef.AD_TypeName_Video)){
    
    	}
    	
        if(result== ADParam.ADOpenResult_Success){//广告打开成功

		}
		else {//广告打开失败

		}
	}
});
```

回调方法说明：  
**AdParamCallback .onOpenResult(onOpenResult(ADParam adParam
, int result))**  //广告打开结果通知  
adParam:广告相关参数（可获取广告ID，广告状态，广告代理商）
result:广告打开结果：0成功  1失败  2取消


**AdParamCallback.onRequestAddGameCoin(ADParam adParam, int result, int num, String reason)**//积分墙类广告的钻石奖励通知  
adParam:广告相关参数  
result：奖励结果类型：0成功  1失败  2取消  
num：奖励数量  
reason：通知字符串  

### 2.3.3 打开广告（必接）

```
ADNative.openAd(String adPositionName);
```
adPositionName：             广告位名称  

### 2.3.4 查看广告是否就绪  

```
ADNative.isAdReady(String adPositionName);
```

```
ADNative.isAdReady2(String adPositionName, String type);
```

adPositionName:广告位名称  
type: 广告类型 (plaque-插屏 video-视频 banner-广告条 wall-积分墙 splash  启动画)

### 2.3.5 关闭广告  

```
ADNative.closeAd(String adPositioname);
```

adPositionName：             广告位名称



### 2.3.6 获取视频限制次数  

```
ADNative.getVideoLimitOpenNum();
```

返回值：可播放次数，为-1表示无限次。

### 2.3.7 广告位是否已打开
可用于防止用户连续多次点击广告按钮

```
ADNative.isAdOpen(String adPositionName);
```
返回值：true-广告已被打开   false-广告未被打开

### 2.3.8 某关卡是否出现广告

```
ADNative.isAdBeOpenInLevel(String adPositionname,int lv);
```
参数：  adPositionname-广告位名称  lv-关卡编号(1-无限)  
返回值：true-可出现   false-不可出现

## 2.4  统计相关 
### 2.4.1 初始化  （必接）

```
TJNative.init();
```

### 2.4.2 自定义统计  
```
//自定义计数事件
TJNative.event(String eventId);
TJNative.event(String eventId, String label);
TJNative.event(String eventId, HashMap<String, String> attributes);
//自定义计算事件
TJNative.eventValue(String eventId, HashMap<String, String> attributes, int duration);

```
eventId: 为当前统计的事件ID  
label：事件的标签属性

## 2.5 互推相关

### 2.5.1 初始化

```
XYXNative.init();
```

### 2.5.2 获取互推数据


```
XYXNative.getConfig();
```


### 2.5.3 上报互推展示


```
XYXConfig config = XYXNative.getConfig();
ArrayList<XYXItem> list = config.getItemList();
if (list != null && list.size() > 0) {
	XYXItem item = list.get(0);
	config.exposureShow(item);
}
```


### 2.5.4 响应互推点击

```
XYXConfig config = XYXNative.getConfig();
ArrayList<XYXItem> list = config.getItemList();
if (list != null && list.size() > 0) {
	XYXItem item = list.get(0);
	config.handleClick(item);
}
```

# 3.混淆过滤  


```
-keep public class com.libVigame.**
-keepclassmembers class com.libVigame.**{*;}
-keep public class com.libAD.**
-keepclassmembers class com.libAD.**{*;}
-keep public class com.libPay.**
-keepclassmembers class com.libPay.**{*;}
-keep class com.google.extra.platform.Utils
-keepclassmembers public class com.google.extra.platform.Utils{*;}
```


# 4.产品参数配置   
## 4.1 配置产品参数  

测试时可按照demo中的参数进行配置，正式上线前由出包方替换正式参数。

```
<meta-data
    android:name="com.vigame.sdk.appid"
    android:value="10001" />
<meta-data
    android:name="com.vigame.sdk.appkey"
    android:value="abcdefgh" />
<meta-data
    android:name="com.vigame.sdk.prjid"
    android:value="333360" />
<meta-data
    android:name="com.vigame.sdk.channel"
    android:value="default" />
```

## 4.2 配置游戏   
通过assets文件夹中的ConfigVigame.xml进行配置，属性说明如下：  

名称 | 解释 | 是否必须
---|---|---
GameOpenActivity | 闪屏后进入的Activity路径名称 | 是
ScreenOrientation | 屏幕方向  | 是
SupportAdPositions  |  支持的广告位名称 | 否
SplashFile  | 默认的闪屏文件（assets中） | 否
WithSplashAD | 是否出现闪屏广告（默认出现） | 否


# 5.FAQ

**5.1 我的游戏应该接入哪些功能？**  
解答：  
接入文档中所有标记“必接”的接口都需要接入。如含有计费功能还需要接入PayNative.orderPay购买接口。其他接口是否接入由项目运营人员视情况而定。

**5.2 如何测试广告？**  
解答：  
需请使用demo中的包名和产品参数，并在build.gradle中引入广告补丁，即可进行测试。注意：更换包名或产品参数，将可能导致广告加载失败。

**5.3 为什么使用有些广告位名称无法展现广告？**  
解答：  
因为每个项目所用到的广告位名称有所区别。使用Demo中的参数时，以下常用广告位名称可直接出广告：

| 名称             | 广告类型 |
| ---------------- | -------- |
| banner           | 横幅     |
| home             | 插屏     |
| pause            | 插屏     |
| exit_game        | 插屏     |
| level_restart    | 插屏     |
| level_fail       | 插屏     |
| level_win        | 插屏     |
| game_win         | 插屏     |
| game_fail        | 插屏     |
| game_pause       | 插屏     |
| level_fail_mfzs  | 视频     |
| rotary_mfzs      | 视频     |
| home_mfzs        | 视频     |
| gift_mfzs        | 视频     |
| double_fail_mfzs | 视频     |
| dj_mfzs          | 视频     |
| double_mfzs      | 视频     |
| item_mfzs        | 视频     |
| sign_game        | 视频     |

其他广告位名称，可能无法出广告。只需保证正确调用即可。

 **5.4 开屏(splash)和唤醒(game_awaken)广告，如何埋点？**  
 解答：  
 这两个广告位名称无需做埋点，按照SDK接入流程正常接入即可自动触发。
 
**5.5 如何测试计费?**  
解答：  
因为微信支付限制较多，建议使用支付宝测试。   
首先确保在build.gradle中引入支付宝计费补丁；  
然后注意assets目录中需要放置ConfigPay.xml和feeedata_ali.xml文件，其中feedata_ali.xml的内容根据游戏计费设置自行配制；  
最后通过调用orderPay接口即可进行支付。


**5.6 sdk接入成功后需要提供什么？**  
解答：  
需要提供完整的Android工程文件，包括已编译好的so文件以及引用的本地模块。

**5.7 为什么需要提供工程？是否有安全隐患？**  
解答：  
提供工程文件的意义在于我们可以自行制作渠道上线包及针对广告做运营控制，将可以大大降低双方的沟通成本和工作量。  
关于安全隐患，也不必担心。安卓工程本身并无法还原游戏源码。如果工程中有部分java代码不希望公开，可通过混淆的jar文件进行提供。
    
**5.8 cp服务器能够查询支付回调通知吗？**  
解答：  
通常单机游戏可以通过客户端直接处理支付结果。如果部分游戏确实需要服务端处理支付结果的，需要给我们提供通知地址，我们服务器端在收到支付回调后转发给cp服务器。

