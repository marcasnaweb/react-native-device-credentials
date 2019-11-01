# react-native-device-credentials

## Getting started

`$ npm install react-native-device-credentials --save`

### Mostly automatic installation

`$ react-native link react-native-device-credentials`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-device-credentials` and add `DeviceCredentials.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libDeviceCredentials.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.DeviceCredentialsPackage;` to the imports at the top of the file
  - Add `new DeviceCredentialsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-device-credentials'
  	project(':react-native-device-credentials').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-device-credentials/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-device-credentials')
  	```


## Usage
```javascript
import DeviceCredentials from 'react-native-device-credentials';

// TODO: What to do with the module?
DeviceCredentials;
```
