package br.com.mnw;

import br.com.mnw.MNWKeyHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.security.keystore.UserNotAuthenticatedException;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.Map;
import java.util.HashMap;

public class DeviceCredentialsModule extends ReactContextBaseJavaModule {

 private static final String TAG = "DeviceCredentials";
 private static final int AUTH_CODE = 1;

 private final MNWKeyHelper m_helper;

 private String m_authTitle = "";
 private String m_authDesc = "";

 public DeviceCredentialsModule(ReactApplicationContext reactContext) {
  super(reactContext);
  this.m_helper = new MNWKeyHelper();
 }

 @Override
 public String getName() {
  return this.TAG;

 }

 @Override
 public Map < String, Object > getConstants() {
  final Map < String, Object > constants = new HashMap < > ();
  //constants.put(name, value);
  return constants;
 }

 @ReactMethod
 public void setAuthTitle(String title) {
  m_authTitle = title;
 }

 @ReactMethod
 public void setDescTitle(String desc) {
  m_authDesc = desc;
 }

 /**
  * Creates a symmetric key in the Android Key Store which can only be used after
  * the user has authenticated with device credentials within the last reAuthTimeout seconds.
  *
  * @keyName: The name of the key that will created.
  * @requireAuth: Sets whether this key is authorized to be used only if the user has been
  * authenticated. http://tiny.cc/z0crbz
  * @timeout: If auth is required timeout Sets the duration of time (seconds) for which this key
  * is authorized to be used after the user is successfully authenticated. http://tiny.cc/z0crbz
  * @invalid: If auth is required invalid sets whether this key should be invalidated on biometric
  * enrollment. http://tiny.cc/e2crbz
  */
 @ReactMethod
 public void createKey(
  final String keyName,
  boolean requireAuth,
  int timeout,
  boolean invalid,
  Promise promise) {

  try {
   m_helper.createKey(keyName, timeout, invalid, requireAuth);
   promise.resolve(true);
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }

 /**
  * Check if a key existis with the name keyName.
  *
  * @keyName: The name of the key that will be checked.
  */
 @ReactMethod
 public void keyExistis(final String keyName, Promise promise) {
  try {
   promise.resolve(m_helper.keyExistis(keyName));
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }


 /**
  * Delete the key with name keyName.
  *
  * @keyName: The name of the key that will be deleted.
  */
 @ReactMethod
 public void deleteKey(final String keyName, Promise promise) {
  try {
   m_helper.deleteKey(keyName);
   promise.resolve(true);
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }

 /**
  * Save some content securely.
  *
  * @keyName: The name of the key that will be used to secure the info.
  * @content: The content that will be saved
  */
 @ReactMethod
 public void storeContent(final String keyName, final String content, final Promise promise) {
  try {
   m_helper.storeContent(getReactApplicationContext(), keyName, content);
   promise.resolve(true);
  } catch (UserNotAuthenticatedException e) {

   //Creating runnable inside catch so it can mantain the promise object.
   final Runnable callback = new Runnable() {
    @Override
    public void run() {
     storeContent(keyName, content, promise);
    }
   };

   //doAuthentication will handle future catchs and sent it throught  promise.
   doAuthentication(promise, callback);

  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }

 /**
  * Retrive the content that was securely saved. The content will be delivered via promise.
  *
  * This function might call doAuthentication so if you want to change the auth title and desc
  * call {@link #setAuthTitle(String) setAuthTitle} and
  * {@link #setDescTitle(String) setDescTitle} methods before calling this function.
  *
  * @keyName: The name of the key that will be used to secure the info.
  **
  */
 @ReactMethod
 public void retrieveValue(final String keyName, final Promise promise) {
  try {
   String content = m_helper.getContent(getReactApplicationContext(), keyName);
   promise.resolve(content);
  } catch (UserNotAuthenticatedException e) {

   //Creating runnable inside catch so it can mantain the promise object.
   final Runnable callback = new Runnable() {
    @Override
    public void run() {
     retrieveValue(keyName, promise);
    }
   };

   //doAuthentication will handle future catchs and sent it throught  promise.
   doAuthentication(promise, callback);
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }

 /**
  * Returns whether the device is secured with a PIN, pattern or password.
  */
 @ReactMethod
 public void isDeviceSecure(Promise promise) {
  try {
   boolean result = m_helper.isDeviceSecure(getReactApplicationContext());
   promise.resolve(result);
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }

 /**
  * if you want to change the auth title and desc
  * call {@link #setAuthTitle(String) setAuthTitle} and
  * {@link #setDescTitle(String) setDescTitle} methods before calling this function.
  */

 @ReactMethod
 public void authenticate(Promise promise) {
  doAuthentication(promise, null);
 }

 private void doAuthentication(final Promise promise, final Runnable callback) {
  /**
   * As it is not possible to pass promise as extra to intent yet, the activity listner will
   * be created here in orther to be able to call Promise methods.
   * */

  ActivityEventListener activityEventListener = new ActivityEventListener() {
   @Override
   public void onActivityResult(Activity activity, int request, int result, Intent intent) {
    if (request == AUTH_CODE) {
     if (result == Activity.RESULT_OK) {
      if (callback != null) {
       callback.run();
      } else {
       promise.resolve(true);
      }
     } else {
      promise.reject(TAG + ": " + getName(), new UserNotAuthenticatedException());
     }
    }

    getReactApplicationContext().removeActivityEventListener(this);
   }

   @Override
   public void onNewIntent(Intent intent) {}
  };

  try {
   getReactApplicationContext().addActivityEventListener(activityEventListener);
   Activity activity = getCurrentActivity();

   m_helper.authenticateUser(activity, this.AUTH_CODE, this.m_authTitle, this.m_authDesc);
  } catch (Exception e) {
   promise.reject(TAG + ": " + getName(), e);
  }
 }
}