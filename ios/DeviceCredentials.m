#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(DeviceCredentials, NSObject)

RCT_EXTERN_METHOD(isDeviceSecure:
    (RCTResponseSenderBlock) callback
)

RCT_EXTERN_METHOD(createKey:
    (NSDictionary) object 
    callback: (RCTResponseSenderBlock)
)

RCT_EXTERN_METHOD(retrieveValue: 
    (NSString) key 
    callback: (RCTResponseSenderBlock)
)

RCT_EXTERN_METHOD(deleteKey: 
    (NSString) key 
    callback: (RCTResponseSenderBlock)
)
@end