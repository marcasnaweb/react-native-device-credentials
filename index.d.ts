export type DeviceCredentialsModule = {
  setAuthTitle: (desc: string) => void;
  setAuthDesc: (desc: string) => void;
  createKey: (
    keyname: string,
    requireAuth: boolean,
    timout: number,
    invalid: boolean,
  ) => Promise<boolean>;
  keyExistis: (keyName: string) => Promise<boolean>;
  deleteKey: (keyName: string) => Promise<boolean>;
  storeContent: (keyName: string, content: string) => Promise<boolean>;
  retrieveValue: (keyName: string) => Promise<string>;
  isDeviceSecure: () => Promise<boolean>;
  authenticate: () => Promise<boolean>;
};

