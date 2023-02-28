
# PayBy-inApp-Android

PayBy Payment Gateway integration SDK for android with In-app pay scenes

## Term Definition

- IAPDeviceId: every device has its own unique deviceId
- IAPPartnerId: every merchant is assigned a partnerId while applying for the payment service
- IAPAppId: every app of a merchant is assigned an appId while applying for the payment service
- OrderToken: it contains order information
- IAPSign: first, generate a singString by arranging IAPDeviceId、IAPPartnerId、IAPAppId and OrderToken in order. The rules are as follows: String signString ="iapAppId="+iapAppId+ "&iapDeviceId=" + iapDeviceId+ "&iapPartnerId=" + iapPartnerId+"&token=" + token. Second,sign the signString with privateKey， and the encryption rules can be seen in the demo.

## Add Dependencies

Use gradle to add dependencies,and also add manifestPlaceholders for downloading APK.

#### Step 1: Add Repositories

Add the Maven repositories address in **build.gradle** at the root directory **project**

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

#### Step 2: Add Library
if your project is AndroidX,please add the following code in the **gradle.properties**

```
android.useAndroidX=true
android.enableJetifier=true
```
Add **AndroidX** library dependencies in **build.gradle** below the level of **app module**

```
dependencies{
    ...
    def iap_version="2.1.0-RELEASE"
    implementation "com.payby.android.module.iap:lib-iap-view:${iap_version}"
}
```
or **Android Support**
```
dependencies{
    ...
    def iap_version="2.1.0-RELEASE"
    implementation "com.payby.android.module.iap:lib-iap-view:${iap_version}"
}
```
**Notice:**
The 2.1.0-RELEASE version of the IAP SDK currently does not support flutter applications, and the version that supports flutter is under development.
If you want to use the IAP SDK in the flutter application, it is recommended to use the 2.0.6.2-RELEASE version.

#### Step 3: Add Placeholder

Add the manifestPlaceholders key-value pair. The key is "PACKAGENAME" and the value is the current application package name. When Android downloads files, you need to read and write through the FileProvider, and you need to assign the path of Fileprovider according to the **applicationId**. It also needs to support Java8 compilation.

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

**Note:** if **FileProvider** has been used for your project, you need to add attribute **android:authorities=${PACKAGENAME}** at the node, for example:

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.payby.android.payment.iap.sample">
    ...
    <application>
        ...
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${PACKAGENAME}"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        ...
    </application>
</manifest>
```

# Declare Permissions

Declare the necessary permissions in manifest, including:

1. INTERNET: it allows an application to download files.
2. Read/Write SD card: it allows an application to read from / write to external storage.
3. Install Package: it allows an application to install packages.

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
if your project targetVersion is 30,you also need declare the following permission in the manifest.
、、、
<uses-permission android:name="android.permission.INTERNET" />
        <uses-permission
          android:name="android.permission.QUERY_ALL_PACKAGES"
          tools:ignore="QueryAllPackagesPermission" />
、、、
# Parameter Preparation

The payment parameter descriptions and methods to get are as follows. The following parameters can be used to construct a **PayTask** object, which describes a payment task, by calling **pay (PayTask task, Environment env)** method in **PbManager** to complete payment.

| Name         | Descriptioin                                                 | How to get                                                   |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| iapDeviceId  | Unique identification of device                              | It is from the api in IAP SDK                                |
| iapPartnerId | partnerId                                                    | It is assigned when a merchant applies for the payment service |
| token        | orderToken                                                   | After placing an order, you can get it from the response     |
| iapAppId     | appId                                                        | It is assigned when a merchant applies for the payment service |
| iapSign      | Signature information after signing iapDeviceId, iapPartnerId, token, iapAppId | It is generated by signing the signature string according to the signing rules. For the signing string signing sequence and rules, see *Term Definition* |

# How To Use

#### Step 1: Generate IAPDeviceId

```
// If your payment page is Activity, just pass in this, but if it is Fragment, you should pass in getActivity()
PbManager manager = PbManager.getInstance(this);
String mIapDeviceId= manager.getIAPDeviceID();
```

Note: When placing an order and paying for the order, the IapDeviceId must not be different.

#### Step 2: Place An Order

You should place an order by the server yourself. After that you can get information about token and iapSign from the response.

#### Step 3: Set The Payment Listener

```
// register the payment callback listener
manager.onPayResultListener = this;
```
#### Step 4(Optional): Customized configuration IAP SDK

```
 //Step 4(Optional): Customized configuration IAP SDK
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

#### Step 5: Pay

Construct a **PayTask** object according to the parameters prepared before. It should be noted that the order of the parameters must be as follows: the first parameter is **token**, which represents the order token; the second parameter is **iapDeviceId**, which is used to distinguish the unique identifier of different devices ; The third parameter **iapPartnerId** is used to distinguish the id of different merchants; the fourth parameter is **iapSign**, which represents the signature information, which is the signature information generated after the token, iapDeviceId, iapPartnerId, iapAppId are signed by the private key; the fifth parameter It is **iapAppId**, used to distinguish the id of different apps of the merchant.

Then initiate the payment by calling its pay method through the initialized PbManager object. The first parameter is the PayTask type, the second parameter is an Enum type, the value includes Environment.DEV、Environment.UAT and
Environment.PRO.

- **Environment.DEV**:the environment for developing and testing
- **Environent.UAT**:the environment for customer debuging
- **Environment.PRO**:the environment for product online 

```
//dev environment
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, Environment.DEV);    
```
```
//uat environment
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, Environment.UAT);    
```
```
//product environment online
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, Environment.PRO);    
```

#### Step 5: Get The Payment Result

Implement the **OnPayResultListener** interface and rewrite its **onGetPayState(String result)** method to get the payment result.

#### Payment Result Code Description

- **SUCCESS**: the payee has received the payment successfully, and the entire payment process for the order is completed.
- **FAIL**: payment failed.
- ~~PAID~~: ~~the payer paid successfully. Wait for the payee to receive the payment, at the same time, you can also query and track the payment status of the order by order NO.~~
- **PAYING**: processing. Wait for the payment process to complete and return the final payment result.
- **CANCEL**: payment canceled by user when they presses the cross button on the PayBy popup.(Added on version 2.1.0-RELEASE)

# proguard-rules
-keep class com.payby.android.iap.domain.value**{
*;
}
# Sample

Taking the integration of AndroidX dependency library as an example, the complete payment process sample code is as follows. It should be noted that in the actual development process, you need to make your order payment interface to implement the **OnPayResultListener** interface, the order payment interface can be an Activity or a Fragment. Here MainActivity will be used as an example to simulate the payment process.

```
public class MainActivity extends AppCompatActivity implements OnPayResultListener {
  EditText et_sign, et_token, et_id, et_deviceId, et_app_id;
  Button pay;
  private PbManager manager;
  private String mToken;  //tokenUrl
  private String mPartnerId;  //partnerId
  private String mSign;
  private String mIapDeviceId;
  private String mIapAppId;
  private String keyDev = "";//the public key of merchant，

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

    // Step 1: get PbManager and generate IapDeviceId
    manager = PbManager.getInstance(this);

    //Step 2: generate the iapDeviceId
    String iapDeviceID = manager.getIAPDeviceID();
    et_deviceId.setText(iapDeviceID);

    // Step 3: set the payment result listener
    manager.onPayResultListener = this;

    //Step 4(Optional): Customized configuration IAP SDK theme
    initIAPSDK();

    pay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startPay();
      }
    });

  }

  //Step 4(Optional): Customized configuration IAP SDK
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

  //Step 5: start to pay
  private void startPay() {
    mToken = et_token.getText().toString().trim();
    mPartnerId = et_id.getText().toString().trim();
    mIapDeviceId = et_deviceId.getText().toString().trim();
    mSign = et_sign.getText().toString().trim();
    mIapAppId = et_app_id.getText().toString().trim();
    if (TextUtils.isEmpty(mToken)
        || TextUtils.isEmpty(mPartnerId)
        || TextUtils.isEmpty(mIapDeviceId)
        || TextUtils.isEmpty(mSign)
        || TextUtils.isEmpty(mIapAppId)) {
      Toast.makeText(this, "parameter should not be null", Toast.LENGTH_SHORT).show();
      return;
    }
    // support DEV/UAT/PRO
    PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
    manager.pay(task, Environment.UAT);
    //also,you can use the other method to pay
    //manager.payWithOrderCallback(this);
    // after calling this method,the loading dialog will not be canceled until gettting the paying app list.you need implement the method onOrder,in the method,you can pass the order information to SDK by successCallback.
  }

  @Override
  public void onGetPayState(String result) {
    // Step 5: get the payment result and do different processing according to different payment result status
    if (TextUtils.equals(result, "SUCCESS")) {
      // Successful, the payment has been received, the transaction is over
    } else if (TextUtils.equals(result, "PAYING")) {
      // The payment is being processed
    } else if (TextUtils.equals(result, "FAIL")) {
      // Payment failed
    } else if (TextUtils.equals(result, "CANCEL")) {
      // Payment canceled by user when they presses the cross button on the PayBy popup
    } else {
      // Other unknown errors
    }
  }

  @Override
  public void onGetProtocolState(String protocolState) {
    //PROTOCOL-SUCCESS,PROTOCOL-FAIL
    if (TextUtils.equals(protocolState, "PROTOCOL-SUCCESS")) {
      //success......
    } else if (TextUtils.equals(protocolState, "PROTOCOL-FAIL")) {
      //fail.....
    }
  }

  @Override
  public void onOrder(OnOrderSuccessCallback onOrderSuccessCallback, OnOrderFailCallback onOrderFailCallback) {
    //Tips:if you call method manager.pay(task,environment),do nothing here.
    // if call method manager.payWithOrderCallback(this),you can do the following codes show.
    // step1:in here,you need get order information by placing order.
    // step2:construct a PayTask with the order information
    // step3: if success,pass the order information to sdk with OnOrderSuccessCallback,if fail,just notify SDK the state with OnOrderFailCallback
    // the following code simulates the process of placeing order and pass the parameter to sdk
    mToken = et_token.getText().toString().trim();
    mPartnerId = et_id.getText().toString().trim();
    mIapDeviceId = et_deviceId.getText().toString().trim();
    mSign = et_sign.getText().toString().trim();
    mIapAppId = et_app_id.getText().toString().trim();
    if (TextUtils.isEmpty(mToken)
        || TextUtils.isEmpty(mPartnerId)
        || TextUtils.isEmpty(mIapDeviceId)
        || TextUtils.isEmpty(mSign)
        || TextUtils.isEmpty(mIapAppId)) {
      Toast.makeText(this, "parameter should not be null", Toast.LENGTH_SHORT).show();
      return;
    }

    //
//    String signString ="iapAppId="+mAppId+ "&iapDeviceId=" + mIapDeviceId+ "&iapPartnerId=" + mPartnerId+"&token=" + mToken ;
//    String sign = Base64.encode(
//        RsaUtils.sign(
//            signString, StandardCharsets.UTF_8, RsaUtils.getPrivateKey(privateKay)));

    if (!TextUtils.isEmpty(mToken)) {
      String signString =
          "iapAppId=" + mIapAppId + "&iapDeviceId=" + mIapDeviceId + "&iapPartnerId=" + mPartnerId +
          "&token=" + mToken;

      String sign = Base64.encode(

          RsaUtils.sign(
              signString, StandardCharsets.UTF_8, RsaUtils.getPrivateKey(keyDev)));
      PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, sign, mIapAppId);
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          onOrderSuccessCallback.onSuccess(task, Environment.DEV);
        }
      }, 3000);
    } else {
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          onOrderFailCallback.onFail();
        }
      }, 4000);
    }
  }
}

```
