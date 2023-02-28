# PayBy-inApp-Android

PayBy Payment Gateway integration SDK for android with In-app pay scenes

## 术语说明

- IAPDeviceId：用于区分不同设备的唯一标识
- IAPPartnerId：商户申请支付服务时候被分配的商户id，用以区分不同商户
- IAPAppId：商户申请支付服务时候被分配的appId,用以区分商户下不同APP
- OrderToken：包含订单信息的token
- IAPSign：通过对IAPDeviceId、IAPPartnerId、IAPAppId、OrderToken拼接而成的签名字符串加密生成。拼接字符串规则如下所示：String
  signString ="iapAppId="+iapAppId+ "&iapDeviceId=" + iapDeviceId+ "&iapPartnerId=" + iapPartnerId+"
  &token=" + token ;signString的加密规则可见demo

## 添加依赖

通过配置gradle添加依赖库,同时添加包名占位符,用于操作文件下载路径.

#### 步骤1:添加仓库地址

在Project级别下面的build.gradle中添加maven仓库地址

```
buildscript{
    repositories {
        google()
        jcenter()
        maven {
            credentials {
                username 'dev'
                password 'dev@123'
            }
            url("https://nexus.payby.com/repository/android-release/")
        }  
    }
}
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            credentials {
                username 'dev'
                password 'dev@123'
            }
            url("https://nexus.payby.com/repository/android-release/")
        }  
    }
}
```

#### 步骤2: 添加依赖库

在app moudle级别下面的build.gradle中,添加依赖库 AndroidX:

```
dependencies{
    ...
    def iap_version="2.1.0-RELEASE"
    implementation "com.payby.lego.android.payment:lib-iap-sdk-view-x:${iap_version}"
}
```

or Android Support

```
dependencies{
    ...
    def iap_version="2.1.0-RELEASE"
    implementation "com.payby.lego.android.payment:lib-iap-sdk-view-support:${iap_version}"
}
```

#### 步骤3: 添加placeholder

添加manifestPlaceholders 键值对.键是"PACKAGENAME"
,值是当前应用包名.Android下载文件时候,需要通过FileProvider进行读写操作,而FileProvider需要指定当前应用包名.同时还需要支持Java8编译

```
android{
    defaultConfig {
        applicationId "com.payby.android.payment.iap.sample"
        ...
        manifestPlaceholders=[
                PACKAGENAME:"com.payby.android.payment.iap.sample"
        ]
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

# 声明权限

在清单文件中指定所需要的权限,包括:

1. 网络权限:用于网络请求
2. SD卡读写权限:用于文件读写
3. 安装应用权限:允许在当前应用中安装,更新应用

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.payby.android.payment.iap.sample">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
    <application
       ...
    </application>

</manifest>
```

# 参数准备

支付参数说明及获取方式如下所示，通过以下参数可以构建 **PayTask** 对象，该对象描述了一个支付任务，通过调用 **PbManager** 里面的 **pay(PayTask
task,boolean isTest)** 方法完成支付动作. | 名称| 说明 | 如何获取 | |:-:|:-:|:-:|
|iapDeviceId|设备唯一标识|通过SDK中PbManager对象获取| |iapPartnerId|商户id|商户申请服务时候被分配的商户id|
|token|订单token|创建订单之后由服务端生成| |iapAppId| appId|商户申请服务时候被分配的AppId|
|iapSign|对iapDeviceId,iapPartnerId,token,iapAppId加签之后的签名信息|根据加签规则对签名字符串进行签名而生成的信息,签名字符串加签顺序及规则具体见术语说明|

# 如何使用

#### 步骤1:生成IAPDeviceId

```
// 如果当前界面是Activity，则实例化PbManager对象时候传入this上下文即可；如果是Fragment，则传入getActivity()即可
PbManager manager = PbManager.getInstance(this);
String mIapDeviceId= manager.getIAPDeviceID();
```

需要注意的是:IAPDeviceId在创建订单以及订单支付的时候需要保持一致.

#### 步骤2:下单

该步骤需要通过调用自己后台下单接口进行下单，下单成功之后，即可以获取token，iapSign信息.

#### 步骤3:设置支付结果监听

```
manager.onPayResultListener = this;// 当前Activity注册OnPayResultListener接口回调监听
```

#### 步骤4（可选）：个性化配置IAP SDK 主题

```
private void initIAPSDK() {
    IAPSDKConfig.IAPSDKConfigBuilder builder = new IAPSDKConfig.IAPSDKConfigBuilder();
    // If showDefaultResultPage is true,show the IAP default result page,else do not show the IAP default page.
    // The showDefaultResultPage's default value is true
    builder.showDefaultResultPage = false;
    // If showQrCodeOnPad is true,the payment method of PayBy/BOTIM/ToTok will showed as QRCode on Pad Devices.
    // The showQrCodeOnPad's default value is false
    builder.showQrCodeOnPad = false;
    // If you want to change the theme color of the IAP, set this primaryColor value(RGB)
    // The primaryColor's default value is "#00A75D"
    builder.primaryColor = "#00A75D";
    // If you want to change the language of IAP,set this language value.(Currently only supported IAPLanguage.AR and IAPLanguage.EN)
    // The language's default value is IAPLanguage.EN
    builder.language = IAPLanguage.AR;
    IAPSDK.initialize(getApplicationContext(), builder.build());
  }
  
```

#### 步骤5:支付

根据之前准备的参数构建一个PayTask对象.需要注意的是,参数的顺序必须按照如下所示:
第一个参数为token，表示订单token；第二个参数为iapDeviceId，用于区分不同设备的唯一标识；第三个参数iapPartnerId,用于区分不同商户的id；第四个参数为iapSign,表示签名信息，是对token，iapDeviceId,iapPartnerId,iapAppId通过私钥加签之后生成的签名信息；第五个参数是iapAppId,用于区分商户下不同APP的id。然后通过初始化的PbManager对象调用它的pay方法发起支付.第一个参数是PayTask类型,第二个参数是一个Boolean类型,true表示是测试环境,false表示是生产环境.

```
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, isTest);    
```

#### 步骤6：获取支付结果

实现 **OnPayResultListener** 接口，重写它的 **onGetPayState(String result)** 方法，即可以拿到支付结果。

#### 支付结果码说明

- SUCCESS: 收款方收款成功，该订单的整个支付流程结束
- FAIL：支付失败
  ~~- PAID：付款方付款成功。等待收款方收款，同时也可通过接口查询跟踪订单支付状态。~~
- PAYING：正在处理中。等待支付流程完成，返回最终支付结果。
- CANCEL: 用户关闭IAP收银台的弹框页面，支付流程未完成。(2.1.0-RELEASE版本增加)

# 示例代码

以集成AndroidX依赖库为例，完整的支付流程示例代码如下所示:需要注意的是您在实际开发过程中，需要让自己的订单支付界面来实现 **OnPayResultListener**
接口，订单支付界面可以是一个Activity，也可以是一个Fragment。此处以MainActivity作为示例来模拟支付流程.

```
public class MainActivity extends AppCompatActivity implements OnPayResultListener {
  EditText et_sign, et_token, et_id, et_deviceId,et_app_id;
  Button pay;
  private PbManager manager;
  private String mToken;  //tokenUrl   
  private String mPartnerId;  //partnerId
  private String mSign;
  private String mIapDeviceId;
  private String mIapAppId;
  private boolean isTest = true; 

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pay = findViewById(R.id.pay);
    et_sign = findViewById(R.id.et_sign);
    et_token = findViewById(R.id.et_token);
    et_id = findViewById(R.id.et_id);
    et_deviceId = findViewById(R.id.et_deviceId);
    et_app_id = findViewById(R.id.et_app_id);
    
    // Step1:获取PbManager对象
    manager = PbManager.getInstance(this);
    
    // Step2:获取iapDeviceId
    String iapDeviceID = manager.getIAPDeviceID();
    et_deviceId.setText(iapDeviceID);
    
    // Step3:设置支付结果监听
    manager.onPayResultListener = this;
    
    //Step 4(可选): 个性化设置IAP SDK主题
    initIAPSDK();
    
    pay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startPay();
      }
    });

  }
  
  //Step 4(可选): 个性化设置IAP SDK主题
  private void initIAPSDK() {
    IAPSDKConfig.IAPSDKConfigBuilder builder = new IAPSDKConfig.IAPSDKConfigBuilder();
    // 显示IAP默认的结果页，反之false，不显示IAP默认结果页
    // showDefaultResultPage的默认值是true
    builder.showDefaultResultPage = false;
    // showQrCodeOnPad值为true，PayBy/BOTIM/ToTok这些支付方式会在Pad设备显示为二维码;值为false则不显示二维码
    // showQrCodeOnPad的默认值是false
    builder.showQrCodeOnPad = false;
    // IAP SDK页面主题色（RGB值）
    // primaryColor默认值为 "#00A75D"
    builder.primaryColor = "#00A75D";
    // language参数可改变IAP SDK的语言.(当前只支持阿语和英语)
    // language默认值为IAPLanguage.EN
    builder.language = IAPLanguage.AR;

    IAPSDK.initialize(getApplicationContext(), builder.build());
  }
  
  // Step 5:支付
  private void startPay() {
    mToken = et_token.getText().toString().trim();
    mPartnerId = et_id.getText().toString().trim();
    mIapDeviceId = et_deviceId.getText().toString().trim();
    mSign = et_sign.getText().toString().trim();
    mAppId = et_app_id.getText().toString().trim();
    if (TextUtils.isEmpty(mToken)
        || TextUtils.isEmpty(mPartnerId)
        || TextUtils.isEmpty(mIapDeviceId)
        || TextUtils.isEmpty(mSign)
        || TextUtils.isEmpty(mAppId)) {
      Toast.makeText(this, "parameter should not be null", Toast.LENGTH_SHORT).show();
      return;
    }
    PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mAppId);
    manager.pay(task, isDev);
  }

  @Override
  public void onGetPayState(String result) {
  
    // Step5: 获取支付结果，根据不同的支付结果状态作不同处理
    if (TextUtils.equals(result, "SUCCESS")) {
      //成功，已经收款，交易结束
    } else if (TextUtils.equals(result, "PAID")) {
      // 付款方已经成功付款，等待收款方收款。
    } else if (TextUtils.equals(result, "PAYING")) {
      // 正在处理付款
    } else if (TextUtils.equals(result, "FAIL")) {
      // 支付失败
    } else if (TextUtils.equals(result, "CANCEL")) {
      // 用户取消支付
    } else{
      // 其他未知错误
    }
  }
}
```





