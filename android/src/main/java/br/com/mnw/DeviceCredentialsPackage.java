/**
 * MNWDeviceCredentialsPackage.java - a react native package for Android to authenticate user and
 * save/load credentials securely.
 *
 * @author  Marcas na Web | Bruno Almeida <bruno@marcasnaweb.com.br>
 * @version 1.0
 * @see github.com/marcasnaweb
 */
package br.com.mnw;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

public class DeviceCredentialsPackage implements ReactPackage {
 @Override
 public List < NativeModule > createNativeModules(ReactApplicationContext reactContext) {
  return Arrays. < NativeModule > asList(new DeviceCredentialsModule(reactContext));
 }

 @Override
 public List < ViewManager > createViewManagers(ReactApplicationContext reactContext) {
  return Collections.emptyList();
 }
}