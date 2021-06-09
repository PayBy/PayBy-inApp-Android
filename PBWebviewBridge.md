
# PBWebView Bridge intergration document

## Bridge integration

```
window.PBJSBridge.init(function (message, responseCallback) {})
sample:
window.onload = function () {
      function onBridgeReady() {
        window.PBJSBridge.init(function (message, responseCallback) {})
      }
      if (typeof PBJSBridge === 'undefined') {
        document.addEventListener('PBJSBridgeReady', onBridgeReady, false)
      } else {
        onBridgeReady()
      }

    }

```


## call Bridge

```
1、H5 call native
1.1、native side registerHandler 。such as bridge share：
registerHandler(Bridge.share, (data, function) -> {
  ShareParam shareParam = gson.fromJson(data, ShareParam.class);
 share(shareParam);
 
});
1.2、H5 side：
window.PBJSBridge.invoke('share',
{
  text :'share content'
},function (data) {
  alert(data)
  const res = JSON.parse(data)
 
})
2、native push content to H5
2.1、H5 side：
window.PBJSBridge.registerHandler('currentLocation', function(data, responseCallback) {
    alert(data)
    const res = JSON.parse(data)
    console.log("onBluetoothData推送："+data)
 
})
2.2、native side：
invoke(Bridge.location, gson.toJson(map), data -> Log.e(TAG, "onCallBack:" + data));
```

## Bridge API  list
### 1.ToPayRequest      open cashdesk

```
params:{
appId: xxx,
token: xxx
}
callback:{
status: 返回结果状态
}
notes:status code
    支付：
    success: 成功
    failed： 失败
    paying：处理中
    提现：
    init：初始化，处理中
    apply：扣款成功 处理中
    submit：提交银行 处理中
    failed:失败
    success：成功 
    充值：
    pending：处理中
    success：成功
    failed:失败
    GP：
    rolling_process：处理中
    success：成功
    paying：处理中
    failed:失败

```
### 2.leaveWeb     close webview

```
入参：无
回调：无
```
### 3.getPublicConfig 获取公共参数

```
params：{}
callback：{
X-Device-Id:xxxx,
}
```
### 4.share  system share

```
params：{
 text:分享文本内容，包含链接,
  url:图片地址
}
callback：无
备注:目前暂不支持分享图文、即若分享文本类型，只传text，不需传url。反之一样。
```
### 5.getVerifyToken  login

```
params：{

appDomain： xxx,

codeChallenge:xxx

}
callback：{

verifyToken: xxx,
authToken： xxx
}

```
### 6.isUserCGSAccessTokenValid     if token is valid

```
params:{}
callback：{

isValid:true/false
}
```
### 7.requestLocating     getCurrentLocation

```
params:{}
callback:{
latitude: xxx,
longitude: xxx,
isEnable:true/false
}
```
### 8.scanQRCode 打开扫码

```
入参：{

needResult: true/false
}
返回值：直接返回扫描到的字符串
```
### 9.openBluetoothService 打开蓝牙服务

```
入参：无
返回值：{
status:active/inactive
}
```
### 10.scanBluetoothDevices 扫描周边蓝牙设备

```
入参：无
返回值：{
address:79832732
}
```
### 11.stopScanBluetoothDevices/stopScanBleDevice 停止扫描

```
入参：无
返回值：无
```
### 12.connectAndRegisterBluetoothDevice 连接并注册蓝牙设备

```
 入参：{
address: xxx   必传
uuidServer：xxx 可空
uuidDescriptor:xxx 可空
uuidWrite:xxx 可空
uuidRead:xxx 可空
producers：oumi  字段可空，若传，则值特定唯一
}
返回值：{
status: connectFailed/success
}
```
### 13.sendBluetoothData  向蓝牙设备写数据

```
入参：{
data:xxxx
}
返回值：无
```
### 14.onBluetoothData 读取蓝牙数据

```
入参：{
result:xxx
}
返回值：无
备注：该方法是Native主动推送给H5
```
### 15.scanBleDevice 扫描周边蓝牙设备

```
入参：无
返回值：[{
"address": 11111111,
"deviceName": "1111"
},
{
"address": 11111111,
"deviceName": "1111"
},
{
"address": 11111111,
"deviceName": "1111"
}
]
```
### 16.sendSMS 调用系统发送短信

```
入参：{
content:短信内容,
phone:手机号
}
返回值：无
```
### 17.requestVerify          request kyc

```
params：{

    "product":"CashNow/PayLater/...",
    "referrerCode":"KYC推荐邀请码",

"fullKyc":true/false,

}
callback：{
verifyFinished:true/false
}
备注：
product 为统计KYC来源的埋点字段，目前取值有：CashNow、PayLater、Transfer、Top-up、

Mobile Transfer Details

fullKyc:是否打开最高等级的KYC（eid认证） 、、不传该字段的话默认打开KYC认证中心

此js不再更新，需要打开kyc，可以使用openNative 调用

具体路由如下

route://native/kyc/startKyc

2.1版本已经修改为打开实名认证管理中心，需要直接打开eid认证 增加参数 route://native/kyc/startKyc?params={"fullKyc":true}, 只是为了兼容老的，不建议继续使用次方式调用

route://native/kyc/startVerifyCenter 打开kyc管理中心

route://native/kyc/startPassProt 打开passport认证

route://native/kyc/startEidKyc 打开eid认证
```
### 18.getDefaultShippingAddress 获取收货地址

```
入参：无
返回值：{
id:xxx,
firstName:用户名,
phoneNumber:用户手机号,
cityId:城市id,
districtId:区id,
cityName:收货城市,
districtName:收货地址所在区,
addressLine1:收货具体地址,
asDefault:该地址是否默认，Y/N
}
```
### 19.openShippingAddressList 打开收货地址页面

```
入参：无
返回值：{
status:success/failed,
msg:请求成功/用户取消选择地址,
result:
{
id:xxx,
firstName:用户名,
phoneNumber:用户手机号,
cityId:城市id,
districtId:区id,
cityName:收货城市,
districtName:收货地址所在区,
addressLine1:收货具体地址,
asDefault:该地址是否默认，Y/N
}
}
```
### 20.openContact 打开通讯录，返回联系人

```
入参：无
返回值：{
name:校长,
phone:123455
}
```
### 21.openRiskIdentify  打开核身

```
入参：{
  "eventType": "PAYMENT",
  "identifyTicket": "111111111",
  "identifyMethods": [{
    "memo": "测试标注",
    "method": "PASSWORD"
  }],
}
返回值：{
result:pass/reject
message:xxxx
}
```
### 22.handleGuard 处理Guard

```
入参：无
返回值：{
"guardCode": "actionVerifyCode",
"guardStatus": "PASS/REJECT",
"guardMessage": "message"
}
其中：
guardCode: guard认证项的认证code,字段类型为String,前端在加载H5的url后面拼接此code url?code=actionVerifyCode
guardStatus: guard认证结果，String类型数据，值为：PASS（认证通过），REJECT（认证失败）
调用此bridge方法，Native会关闭webview
```
### 23.iapProtocolCallback IAP签约回调

```
入参：无
返回值：{
state:PROTOCOL-SUCCESS,PROTOCOL-FAIL
}
```
### 24.requestPermission 请求系统权限

```
入参：{
permissionCode:10007
}
返回值：{
status:pass
}
备注：
permissionCode为枚举值，目前可选值如下：
    10001：GPS定位
    10002：相册
    10003：相机权限
    10004：通讯录权限
    10005：存储权限
    10006：摄像权限
    10007：读取通话记录权限
    10008：读取短信内容权限
    10009：APP列表
status为枚举值，取值范围：
    pass：已经授予该权限
    rejectForOnce:单次拒绝。用户单次拒绝如果还要使用对应功能下次继续申请即可。
    rejectForever:永久拒绝。用户永久拒绝，如果要使用相应功能需要引导用户跳转到权限页面手动打开。
```
### 25.toPermissionPage  跳转系统权限页面

```
入参：无
返回值：无
```
### 26.openBotimOAuth 打开botim授权页面

```
入参：{
scope:userInfo,sendMsg,userFriends,
client_id:xxxxxxx,
state:state,
redirect_uri:xxx
}
返回值：{
authCode:111111111
}
备注：scope:有多个参数的话逗号分隔
```
### 27.openNative 通过路由打开原生页面

```
入参：{
router:打开原生业务的路由地址,
needCallback:true/false
}
返回值：{
result:json字符串
}
备注：
    router:为打开原生业务的路由地址，如果有参数，以params=xxx键值对拼接在后面。
    需要注意：参数是一个json串
    如：router://www.xxx?params=json串
    needCallback,代表是否需要回调，如果false，不会触发回调方法。
    只有原生支持回调和params参数的，才能传params和回调，在使用之前咨询此路由是否支持参数和返回值
    result:为回调结果，以json串形式回传。只是打开原生页面的话，不会触发该回调。
    
```
### 28.openNewUrl    H5通过新的webview打开url    

```
入参：{
url:要打开的url地址
}
返回值：{
url:'https://www.baidu.com'
}
```
### 29.startLogin 登录

```
入参：无
返回值：{
accessToken:xxx
}
登录成功会返回accessToken，如果登录失败或者用户取消不会返回该字段
```
### 30.getAccessToken 获取登录token

```
入参：无
返回时：{
accessToken:xxx
}
```
### 31.startLogout 退出登录

```
入参：无
返回值：无
```
### 32.customMonitorEvent 埋点事件

```
入参：{
page_position_key:h5_load_t、h5_defined_t*、display_t等可以自定义,
page_position_value:当前系统时间戳，
page_position_type：timePage，或其他自定义类型
}
返回值：无
```
### 33.permissionHasGranted 判断用户是否授予某个权限

```
入参：{
permissionCode:权限Code，具体的值参考requestPermission的参数
}
返回值：{
hasPermission:true/false
}
```
### 34.shareToFriends 打开ttk分享

```
参数以json数据传递，客户端透传json数据，方便以后扩展

{
"shareId":"shared activity id",
"shareType":"TEXT/IMAGE",
"shareContent":"shared content or url"
}

客户端接收后追加埋点字段相关数据

{
"shareId":"本地分享活动的id",
"shareType":"TEXT/IMAGE",
"shareContent":"schema url",
"monitorKey":"user_id",
"monitorValue":"EncryptCurrentUserId"
}

字段解释：

shareId：本次分享活动的id

shareType：分享内容类型

-TEXT为分享文本和链接，

-IMAGE为分享图片（URL）

shareContent：分享内容

-ToTok平台支持文本加链接（TEXT），图片(IMAGE)

monitorKey：埋点用户user_id的key

monitorValue：埋点数据加密后的CurrentUserId的value

【扩展】

ToTok分享card

{
"shareId":"",
"shareType":"CARD",
"shareContent":{
"preContent":"Special invitation arrived!",
"preIconUrl":"https://cdn.amusgame.net/game/11/8/646258783377457153.jpg",
"resourceLogo":"",
"resourceName":"",
"jumpUrl":"https://www.amusgame.net/web/game/500664543854903308/931385304778287950?shareType=101",
"btnLabel":"ENTER"
},
"monitorKey":"",
"monitorValue":""
}

字段解释：

preContent:预览内容

preIconUrl：预览图片

resourceLogo：logo

resourceName：Name

jumpUrl：打开网页

btnLabel：按钮文案
```
### 35.openBotimApplets 打开botim小程序

```
入参：{
"url":"跳转Botim小程序和公众号的schema url"
}
返回值：无
```
### 36.recordEvent appsFlyer数据埋点    

```
入参：{
    "eventName":"xxx",
    "eventParams":{
        "key01":"value01",
        "key02":{
            "_key001":"value001"
        },
        "key03":"value03"
    }
}
返回值：无
字段解释：
eventName：埋点事件名称
eventParams：埋点数据的json（key-value）
```
目前Bridge使用PBJSBridge,但是对于老的ToPayJSBridge也是兼容的。
WebView本身自带的API也是支持的，譬如，定位，相机，相册，电话，邮件等。

#### WebView Title：
###### control title visible 

native title is visible default，if not, depend on the part of URL query string  pbw_show_title：
y, true and yes means show native title,otherwise hide native title

##### title content
the title of current page is default.this can be covered by the value of URL query string pbw_title.

the full url shows as following:
https://www.xxx.com/?pbw_show_title=true&pbw_title=my title.


 
 












 







        




























