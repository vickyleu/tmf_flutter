//
//  TMFAppletLoginEngine.h
//  TMFDemo
//
//  Created by 石磊 on 2022/4/19.
//  Copyright © 2022 Tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TMFMiniAppSDKManager.h"
NS_ASSUME_NONNULL_BEGIN

typedef void (^LoginHandler)(NSError* _Nullable);

@interface TMFAppletLoginEngine : NSObject
@property TMADevelopLoginMode loginMode;

+ (instancetype)sharedInstance;

- (void)loginWithName:(NSString *)username andPassword:(NSString *)password inMode:(TMADevelopLoginMode)mode callbackHandler:(LoginHandler)handler;

- (NSString *_Nullable)getLoginUsername;

- (NSDictionary *_Nullable)getLoginCookies;

- (void)logout;

@end

NS_ASSUME_NONNULL_END
