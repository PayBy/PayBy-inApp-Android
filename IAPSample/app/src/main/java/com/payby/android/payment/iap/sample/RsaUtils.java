package com.payby.android.payment.iap.sample;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaUtils {

  /**
   * 加密
   *
   * @param str
   * @param publicKey
   * @return
   */
  public static String encrypt(String str, String publicKey) {
    // base64编码的公钥
    try {
      byte[] decoded = Base64.decode(publicKey);
      RSAPublicKey pubKey =
          (RSAPublicKey)
              KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
      // RSA加密
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, pubKey);
      String outStr = Base64.encode(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
      return outStr;
    } catch (Exception e) {
      System.out.println(e);
    }
    return "加密失败";
  }

  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder();
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  public static byte[] sign(String plain, Charset charset, PrivateKey privateKey) {
    Base64 base64 = new Base64();
    try {
      return sign(plain.getBytes(charset), privateKey);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
    return "加签失败".getBytes();
  }

  /**
   * 生成sign的2进制格式 ，如果要转成字符串，请用 Base64.encodeBase64String转一下
   *
   * @param plain
   * @param privateKey
   * @return
   * @throws InvalidKeyException
   * @throws SignatureException
   */
  public static byte[] sign(byte[] plain, PrivateKey privateKey)
      throws InvalidKeyException, SignatureException {
    try {
      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initSign(privateKey);
      signature.update(plain);
      return signature.sign();
    } catch (NoSuchAlgorithmException var3) {
      throw new RuntimeException(var3);
    }
  }

  /**
   * 字符串转为私钥KEY
   *
   * @param privateKey
   * @return
   */
  public static PrivateKey getPrivateKey(String privateKey) {
    byte[] keyBytes = Base64.decode(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePrivate(pkcs8KeySpec);
    } catch (Exception var4) {
      throw new RuntimeException(var4);
    }
  }

}
