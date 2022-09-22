package com.payby.android.payment.iap.sample;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.payby.android.iap.view.Environment;
import com.payby.android.iap.view.OnOrderFailCallback;
import com.payby.android.iap.view.OnOrderSuccessCallback;
import com.payby.android.iap.view.OnPayResultListener;
import com.payby.android.iap.view.PayTask;
import com.payby.android.iap.view.PbManager;
import com.payby.android.iap.view.sdk.IAPLanguage;
import com.payby.android.iap.view.sdk.IAPSDK;
import com.payby.android.iap.view.sdk.IAPSDKConfig;

import java.nio.charset.StandardCharsets;


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

