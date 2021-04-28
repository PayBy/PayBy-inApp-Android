This document shows how to call the payby cashdesk in third part application,and list all the ways of integration.such as IAP and Deeplink
## IAP
if third part application developer wants to call cashdesk by IAP,the steps can be found in:https://github.com/PayBy/PayBy-inApp-Android/blob/master/README.md

## Deeplink 
The deeplink is also supported to call the payby cashdesk not only int the system application such as SMS,browser,but also int the customer webview of third part application.

Now,Payby has supported two deeplinks,one is https://app.payby.com/open-iap-cashdesk ,and the other is payby://payment/open-iap-cashdesk。Both of them need four parameters,and they can be found in:https://github.com/PayBy/PayBy-inApp-Android#parameter-preparation.

The final deeplink may like as follows:
 
```
https://app.payby.com/open-iap-cashdesk?ft=xxx&iapAppId=xxx&iapPartnerId=xxx&iapSign=xxx&iapDeviceId=xxx.
```
**or**

```
payby://payment/open-iap-cashdesk?ft=xxx&iapAppId=xxx&iapPartnerId=xxx&iapSign=xxx&iapDeviceId=xxx
```
### 1.call in SMS or Browser

Just type in deeplink and open with Payby if it has been installed on the phone.

### 2.call in webview
 The developers need to override the the method named shouldOverrideUrlLoading in WebViewClient of webview,and intercept the request url.if the current url equal the cashdesk deeplink,then startActivity with Uri.The example is as below.
 
```
  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    try {
      url = URLDecoder.decode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    Uri uri = Uri.parse(url);
    String scheme = uri.getScheme();
    String host = uri.getHost();
    if ((("https".equalsIgnoreCase(scheme)) &&
        "app.payby.com".equalsIgnoreCase(host))||(("payby".equalsIgnoreCase(scheme)) &&
        "payment".equalsIgnoreCase(host))) {
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      webView.getContext().startActivity(intent);
      return true;
    }
    return super.shouldOverrideUrlLoading(view, url);

  }
```



