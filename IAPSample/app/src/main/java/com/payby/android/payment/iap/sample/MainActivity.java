package com.payby.android.payment.iap.sample;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.payby.android.payment.iap.view.x.OnPayResultListener;
import com.payby.android.payment.iap.view.x.PayTask;
import com.payby.android.payment.iap.view.x.PbManager;

public class MainActivity extends AppCompatActivity implements OnPayResultListener {
  EditText et_sign, et_token, et_id, et_deviceId, et_app_id;
  Button pay;
  private PbManager manager;
  private String mToken;  //tokenUrl   
  private String mPartnerId;  //partnerId
  private String mSign;
  private String mIapDeviceId;
  private String mAppId;
  private boolean isDev = true; //生产环境还是开发环境
  private String privateKay = "113";  //当前商户的私钥，

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


    //Step1:create PbManager
    manager = PbManager.getInstance(this);

    //Step2:generate iapDeviceId
    String iapDeviceID = manager.getIAPDeviceID();
    et_deviceId.setText(iapDeviceID);

    //Step3:register the payment callback
    manager.onPayResultListener = this;

    //Step4:start to pay

    pay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
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

    // 当前步骤只为模拟根据商户私钥加签，后续加签步骤会放在服务器端
//    String signString ="iapAppId="+mAppId+ "&iapDeviceId=" + mIapDeviceId+ "&iapPartnerId=" + mPartnerId+"&token=" + mToken ;
//    String sign = Base64.encode(
//        RsaUtils.sign(
//            signString, StandardCharsets.UTF_8, RsaUtils.getPrivateKey(privateKay)));

    PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign, mAppId);
    manager.pay(task, isDev);
  }

  @Override
  public void onGetPayState(String result) {
    if (TextUtils.equals(result, "SUCCESS")) {
      //成功，已经收款，交易结束
    } else if (TextUtils.equals(result, "PAID")) {
      // 已经付款
    } else if (TextUtils.equals(result, "PAYING")) {
      // 正在处理
    } else if (TextUtils.equals(result, "FAIL")) {
      // 支付失败
    } else {
      // 其他未知错误
    }
  }
}
