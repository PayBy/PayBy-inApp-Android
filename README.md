# PayBy-inApp-Android
PayBy Payment Gateway integration SDK for android with In-app pay scenes
## 术语说明
- IAPDeviceId：用于区分不同设备的唯一标识
- IAPPartnerId：商户申请支付服务时候被分配的商户id，用以区分不同商户
- IAPAppId：商户申请支付服务时候被分配的appId,用以区分商户下不同APP
- OrderToken：包含订单信息的token
- IAPSign：通过对IAPDeviceId、IAPPartnerId、IAPAppId、OrderToken加密生成的签名信息
## 添加依赖
通过配置gradle添加依赖库,同时添加包名占位符,用于操作文件下载路径.
#### 步骤1:添加maven地址
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
            url("http://nexus.payby.com/repository/android-release/")
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
            url("http://nexus.payby.com/repository/android-release/")
        }  
    }
}
```
#### 步骤2: 添加依赖库
在app moudle级别下面的build.gradle中,添加依赖库
```
dependencies{
    ...
    def iap_version="1.0.0-RELEASE"
    implementation "com.payby.lego.android.payment:lib-iap-sdk-view-x:${iap_version}"
}
```
#### 步骤3: 添加placeholder
添加manifestPlaceholders 键值对.键是"PACKAGENAME",值是当前应用包名.Android下载文件时候,需要通过FileProvider进行读写操作,而FileProvider需要指定当前应用包名.同时还需要支持Java8编译
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
1.  网络权限:用于网络请求
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
支付参数说明及获取方式如下:
| 名称| 说明 | 如何获取 |
|:-:|:-:|:-:|
|iapDeviceId|设备唯一标识|通过SDK中PbManager对象获取|
|iapPartnerId|商户id|商户申请服务时候被分配的商户id|
|token|订单token|创建订单之后由服务端生成|
|iapAppId| appId|商户申请服务时候被分配的AppId|
|iapSign|对iapDeviceId,iapPartnerId,token,iapAppId加签之后的签名信息|根据加签规则对签名字符串进行签名而生成的信息,签名字符串加签顺序具体如下|
- String signString ="iapAppId="+iapAppId+ "&iapDeviceId=" + iapDeviceId+ "&iapPartnerId=" + iapPartnerId+"&token=" + token ;
# 如何使用
#### 步骤1:生成IAPDeviceId
```
PbManager manager = PbManager.getInstance(this);
String mIapDeviceId= manager.getIAPDeviceID();
```
需要注意的是:IAPDeviceId在创建订单以及订单支付的时候需要保持一致.

#### 步骤2:下单
该步骤需要通过调用自己后台下单接口进行下单
#### 步骤3:设置支付结果监听
```
manager.onPayResultListener = this;
```
#### 步骤4:支付
根据之前准备的参数构建一个PayTask对象.需要注意的是,参数的顺序必须按照如下所示:第一个为token,第二个为iapDeviceId...然后通过初始化的PbManager对象调用它的pay方法发起支付.第一个参数是PayTask类型,第二个参数是一个Boolean类型,true表示是测试环境,false表示是生产环境.
```
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, isTest);    
```
# 示例代码
集成支付完整示例如下所示:
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
  private boolean isTest = true; //What's the environment?Sim environment fill in true, product environment fill in false.

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
    pay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      // Step4:支付
        startPay();
      }
    });

  }

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
  public void onGetPayState(String s) {
    pay.setText(s);
  }
}
```





