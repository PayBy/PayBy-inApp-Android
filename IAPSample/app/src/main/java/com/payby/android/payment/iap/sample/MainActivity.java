package com.payby.android.payment.iap.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.payby.android.payment.iap.view.x.OnPayResultListener;
import com.payby.android.payment.iap.view.x.PayTask;
import com.payby.android.payment.iap.view.x.PbManager;

public class MainActivity extends AppCompatActivity implements OnPayResultListener {
  EditText et_sign, et_token, et_id, et_deviceId;
  Button pay;
  private PbManager manager;
  private String mToken;  //tokenUrl   
  private String mPartnerId;  //partnerId
  private String mSign;
  private String mIapDeviceId;
  private boolean isDev = true; //生产环境还是开发环境

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pay = findViewById(R.id.pay);
    et_sign = findViewById(R.id.et_sign);
    et_token = findViewById(R.id.et_token);
    et_id = findViewById(R.id.et_id);
    et_deviceId = findViewById(R.id.et_deviceId);

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
    if (TextUtils.isEmpty(mToken)
        || TextUtils.isEmpty(mPartnerId)
        || TextUtils.isEmpty(mIapDeviceId)
        || TextUtils.isEmpty(mSign)) {
      Toast.makeText(this, "parameter should not be null", Toast.LENGTH_SHORT).show();
      return;
    }
    PayTask task = PayTask.with(mToken, mIapDeviceId, mPartnerId, mSign);
    manager.pay(task, isDev);
  }

  @Override
  public void onGetPayState(String s) {
    pay.setText(s);
  }
}
