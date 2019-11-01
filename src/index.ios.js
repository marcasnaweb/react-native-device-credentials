import {NativeModules} from 'react-native';

const {DeviceCredentials} = NativeModules;

const deviceCredentials = {};

deviceCredentials.isDeviceSecure = () => {
  function promise(res, rej){
    function check (err, result){
      if (err) rej(err);
      else res(result);
    }

    DeviceCredentials.isDeviceSecure(check);
  }
  return new Promise(promise)
}

deviceCredentials.createKey = (key, requireAuth, value) => {
  function promise(res, rej){
    function check (err, result){
      if (err) rej(err);
      else res(result);
    }

    DeviceCredentials.createKey({key, requireAuth,value}, check);
  }
  return new Promise(promise)
}

deviceCredentials.retrieveValue = (key) => {
  function promise(res, rej){
    function check (err, result){
      if (err && err !== DeviceCredentials.errSecItemNotFound) rej(err);
      else res(result);
    }

    DeviceCredentials.retrieveValue(key, check);
  }
  return new Promise(promise)
}
deviceCredentials.deleteKey = (key) => {
  function promise(res, rej){
    function check (err, result){
      if (err) rej(err);
      else res(result);
    }

    DeviceCredentials.deleteKey(key, check);
  }
  return new Promise(promise)
}

export default deviceCredentials;
