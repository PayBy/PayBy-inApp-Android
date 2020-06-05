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

#### Step 2: Add Library

Add **AndroidX** library dependencies in **build.gradle** below the level of **app module**

```
dependencies{
    ...
    def iap_version="1.0.0-RELEASE"
    implementation "com.payby.lego.android.payment:lib-iap-sdk-view-x:${iap_version}"
}
```
or **Android Support**
```
dependencies{
    ...
    def iap_version="1.0.0-RELEASE"
    implementation "com.payby.lego.android.payment:lib-iap-sdk-view-support:${iap_version}"
}
```
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

# Parameter Preparation

The payment parameter descriptions and methods to get are as follows. The following parameters can be used to construct a **PayTask** object, which describes a payment task, by calling **pay (PayTask task, boolean isTest)** method in **PbManager** to complete payment.

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

#### Step 4: Pay

Construct a **PayTask** object according to the parameters prepared before. It should be noted that the order of the parameters must be as follows: the first parameter is **token**, which represents the order token; the second parameter is **iapDeviceId**, which is used to distinguish the unique identifier of different devices ; The third parameter **iapPartnerId** is used to distinguish the id of different merchants; the fourth parameter is **iapSign**, which represents the signature information, which is the signature information generated after the token, iapDeviceId, iapPartnerId, iapAppId are signed by the private key; the fifth parameter It is **iapAppId**, used to distinguish the id of different apps of the merchant. Then initiate the payment by calling its pay method through the initialized PbManager object. The first parameter is the PayTask type, the second parameter is a Boolean type, true means **test environment**, and false means **production environment**.

```
PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mIapAppId);
manager.pay(task, isTest);    
```

#### Step 5: Get The Payment Result

Implement the **OnPayResultListener** interface and rewrite its **onGetPayState(String result)** method to get the payment result.

#### Payment Result Code Description

- **SUCCESS**: the payee has received the payment successfully, and the entire payment process for the order is completed.
- **FAIL**: payment failed.
- **PAID**: the payer paid successfully. Wait for the payee to receive the payment, at the same time, you can also query and track the payment status of the order by order NO.
- **PAYING**: processing. Wait for the payment process to complete and return the final payment result.

# Sample

Taking the integration of AndroidX dependency library as an example, the complete payment process sample code is as follows. It should be noted that in the actual development process, you need to make your order payment interface to implement the **OnPayResultListener** interface, the order payment interface can be an Activity or a Fragment. Here MainActivity will be used as an example to simulate the payment process.

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
  
   // Step 1: get PbManager and generate IapDeviceId
    manager = PbManager.getInstance(this);

   //Step 2: generate the iapDeviceId
    String iapDeviceID = manager.getIAPDeviceID();
    et_deviceId.setText(iapDeviceID);

   // Step 3: set the payment result listener
    manager.onPayResultListener = this;
    pay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startPay();
      }
    });

  }
 
  //Step 4: start to pay
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
       // Step 5: get the payment result and do different processing according to different payment result status
    if (TextUtils.equals(result, "SUCCESS")) {
      // Successful, the payment has been received, the transaction is over
    } else if (TextUtils.equals(result, "PAID")) {
      // The payer has successfully paid and is waiting for the payee to receive the payment
    } else if (TextUtils.equals(result, "PAYING")) {
      // The payment is being processed
    } else if (TextUtils.equals(result, "FAIL")) {
      // Payment failed
    }else{
      // Other unknown errors
    }
  }
}
```
