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

//自定义api demo，使用External JSAPI
TMAExternalJSAPI_IMP(log) {
    NSDictionary *data = params[@"data"];
    NSLog(@"invokeNativePlugin : %@",data);
    TMAExternalJSPluginResult *pluginResult = [TMAExternalJSPluginResult new];
    pluginResult.result = @{};
    [context doCallback:pluginResult];
    return pluginResult;
}


@end
