//
//  DeviceCredentials.swift
//  DeviceCredentials
//
//  Created by Bruno Almeida on 29/10/19.

import Foundation
import LocalAuthentication

@objc(DeviceCredentials)
class DeviceCredentials: NSObject {
  @objc
  static func requiresMainQueueSetup() -> Bool {return false}
  
  @objc
  func isDeviceSecure(_
    callback: RCTResponseSenderBlock
  ) -> Void {
    let context = LAContext()
    var error : NSError?

    if context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) {
      callback([NSNull(), true]);
    } else {
      callback([NSNull(), false]);
    }
  }

  @objc
  func createKey(_
    object: NSDictionary,
    callback: RCTResponseSenderBlock
  ) -> Void {
    let server = object["key"] as! String
    let value = object["value"] as! String
    let requireAuth = object["requireAuth"] as! Bool


    let account = SecAccessControlCreateWithFlags(nil,
                                              kSecAttrAccessibleWhenUnlocked,
                                              .userPresence,
                                              nil)

    // Build the query for use in the add operation.
    let data = value.data(using: String.Encoding.utf8)!

    var query: [String: Any] = [
       String(kSecClass): kSecClassInternetPassword,
       String(kSecAttrServer): server,
       String(kSecValueData): data
    ]

    if requireAuth{
      query[String(kSecAttrAccessControl)] = account as Any
    }

    let status = SecItemAdd(query as CFDictionary, nil)

    if status == errSecSuccess {
      callback([NSNull(), true]);
    } else {
      callback([status, NSNull()])
    }
  }

  @objc
  func retrieveValue(_
    server: String,
    callback: RCTResponseSenderBlock
  ) -> Void {
        let query: [String: Any] = [
          String(kSecReturnData): true,
          String(kSecAttrServer): server,
          String(kSecReturnAttributes): true,
          String(kSecMatchLimit): kSecMatchLimitOne,
          String(kSecClass): kSecClassInternetPassword,
          String(kSecUseOperationPrompt): "Autentique para acessar o applicativo MyCapital"
        ]

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)

        guard status == errSecSuccess else {
          callback([status, NSNull()])
          return;
        }

        guard let existingItem = item as? [String: Any],
          let passwordData = existingItem[kSecValueData as String] as? Data,
          let password = String(data: passwordData, encoding: String.Encoding.utf8)
        else {
          callback([NSNull(), item as Any])
          return
        }

        callback([NSNull(), password])
    }

  @objc
  func deleteKey(_
    server: String,
    callback: RCTResponseSenderBlock
  ) -> Void {
    let query: [String: Any] = [
      String(kSecClass): kSecClassInternetPassword,
      String(kSecAttrServer): server,
    ]

    let status = SecItemDelete(query as CFDictionary)
    guard status == errSecSuccess else {
      callback([status, NSNull()])
      return;
    }
    
    callback([NSNull(), true])
  }

  @objc
  func constantsToExport() -> [AnyHashable : Any]! {
    return [
      "errSecItemNotFound": errSecItemNotFound,
      "errSecDuplicateItem": errSecDuplicateItem
    ]
  }

}
