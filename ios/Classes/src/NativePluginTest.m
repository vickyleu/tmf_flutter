//
//  NativePluginTest.m
//  MiniAppDemo
//
//  Created by 石磊 on 2022/11/26.
//  Copyright © 2022 tencent. All rights reserved.
//

#import "NativePluginTest.h"
#import "TMAExternalJSPlugin.h"
#import "TMFMiniAppInfo.h"
#import "TmfFlutterPlugin.h"
#import "TMFMiniAppSDKManager.h"

@implementation NativePluginTest

TMA_REGISTER_EXTENAL_JSPLUGIN;

//Custom api demo, use External JSAPI
TMAExternalJSAPI_IMP(test) {
    TMFMiniAppInfo *appInfo = context.tmfAppInfo;
    NSDictionary *data = params[@"data"];
    
    NSLog(@"************ invokeNativePlugin test,appId:%@,data is %@",appInfo.appId, data);

    TMAExternalJSPluginResult *pluginResult = [TMAExternalJSPluginResult new];
    pluginResult.result = @{};
    [context doCallback:pluginResult];
    return pluginResult;
}

//销毁当前小程序
TMAExternalJSAPI_IMP(destroyTMF) {
    TMFMiniAppInfo *appInfo = context.tmfAppInfo;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [[TMFMiniAppSDKManager sharedInstance] closeAllApplications];
        [TmfFlutterPlugin logout];
    });
    TMAExternalJSPluginResult *pluginResult = [TMAExternalJSPluginResult new];
    pluginResult.result = @{};
    [context doCallback:pluginResult];
    return pluginResult;
}


//自定义api,打印日志
TMAExternalJSAPI_IMP(log) {
    NSDictionary *data = params[@"data"];
    NSLog(@"invokeNativePlugin : %@",data);
    TMAExternalJSPluginResult *pluginResult = [TMAExternalJSPluginResult new];
    pluginResult.result = @{};
    [context doCallback:pluginResult];
    return pluginResult;
}


@end
