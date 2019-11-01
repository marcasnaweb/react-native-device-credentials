/**
 * MNWHeyHelper.java - a react native module helper for Android to authenticate user and
 * save/load credentials securely.
 *
 * @author  Marcas na Web | Bruno Almeida <bruno@marcasnaweb.com.br>
 * @version 1.0
 * @see github.com/marcasnaweb
 */

package br.com.mnw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;

public class MNWKeyHelper {
 private static final String TAG = "AndroidKeyStoreHelper";
 private static final String PROVIDER = "AndroidKeyStore";
 private static final String CONTENT_ENCRYPTED_FILENAME = "content_encrypted.crpt";
 private static final String CONTENT_IV_FILENAME = "pin_iv.crpt";

 private static final String AES = KeyProperties.KEY_ALGORITHM_AES;

 private KeyStore getKeyStore() throws Exception {
  KeyStore keyStore;

  keyStore = KeyStore.getInstance(PROVIDER);
  keyStore.load(null);

  return keyStore;
 }

 private byte[] encrypt(Context ctx, String keyName, String pin) throws Exception {

  Key secretKey = getKey(keyName);

  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
  cipher.init(Cipher.ENCRYPT_MODE, secretKey);

  byte[] encryptedData = cipher.doFinal(pin.getBytes(Charset.defaultCharset()));
  writeData(ctx, CONTENT_IV_FILENAME, cipher.getIV());

  return encryptedData;
 }

 private void writeData(Context context, String fileName, byte[] data) throws Exception {
  FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
  fos.write(data);
  fos.close();
 }

 private void transfer(FileInputStream fis, ByteArrayOutputStream baos) throws Exception {
  byte buf[] = new byte[1024];
  int numRead = fis.read(buf);
  while (numRead > 0) {
   baos.write(buf, 0, numRead);
   numRead = fis.read(buf);
  }
 }

 private byte[] readData(Context context, String fileName) throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();

  FileInputStream fis;
  fis = context.openFileInput(fileName);
  transfer(fis, baos);
  fis.close();

  return baos.toByteArray();
 }

 private Key getKey(String keyName) throws Exception {
  KeyStore keyStore = getKeyStore();
  return keyStore.getKey(keyName, null);
 }

 public boolean keyExistis(String keyName) throws Exception {
  return getKey(keyName) != null;
 }

 public boolean isDeviceSecure(Context context) {
  KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
  return keyguardManager.isDeviceSecure();
 }

 public void createKey(
  String keyName,
  int timeout,
  boolean invalidate,
  boolean requireAuth) throws Exception {

  KeyStore keyStore;
  KeyGenerator keyGenerator;
  KeyGenParameterSpec.Builder builder;

  keyStore = getKeyStore();
  keyGenerator = KeyGenerator.getInstance(AES, PROVIDER);

  int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;

  builder = new KeyGenParameterSpec.Builder(keyName, purposes);

  builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
  builder.setUserAuthenticationRequired(requireAuth);
  builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

  if (requireAuth) {
   builder.setUserAuthenticationValidityDurationSeconds(timeout);

   if (android.os.Build.VERSION.SDK_INT >= 24) {
    builder.setInvalidatedByBiometricEnrollment(invalidate);
   }
  }

  keyGenerator.init(builder.build());
  keyGenerator.generateKey();
 }

 public void deleteKey(String keyName) throws Exception {
  KeyStore keyStore = getKeyStore();
  keyStore.deleteEntry(keyName);
 }

 public void storeContent(Context context, String keyName, String content) throws Exception {
  byte[] encryptedData = encrypt(context, keyName, content);
  writeData(context, CONTENT_ENCRYPTED_FILENAME, encryptedData);
 }

 public String getContent(Context context, String keyName) throws Exception {
  byte[] encryptedData = readData(context, CONTENT_ENCRYPTED_FILENAME);
  Key secretKey = getKey(keyName);

  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
  IvParameterSpec ivParams = new IvParameterSpec(readData(context, CONTENT_IV_FILENAME));
  cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
  return new String(cipher.doFinal(encryptedData));
 }

 public void authenticateUser(Activity activity, int code, String title, String desc) throws Exception {
  Context context = activity.getApplicationContext();

  KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
  Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(title, desc);
  activity.startActivityForResult(intent, code);
 }
}