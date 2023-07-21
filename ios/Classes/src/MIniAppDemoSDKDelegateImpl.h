//
//  MIniAppDemoSDKDelegateImpl.h
//  MiniAppDemo
//
//  Created by 石磊 on 2022/11/3.
//  Copyright © 2022 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TMFMiniAppSDKDelegate.h"

NS_ASSUME_NONNULL_BEGIN

@interface MIniAppDemoSDKDelegateImpl : NSObject <TMFMiniAppSDKDelegate>

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END
