//
//  MIniAppDemoSDKDelegateImpl.m
//  MiniAppDemo
//
//  Created by 石磊 on 2022/11/3.
//  Copyright © 2022 tencent. All rights reserved.
//

#import "MIniAppDemoSDKDelegateImpl.h"
#import "TMFAppletLoginEngine.h"
#import "TMFMiniAppSDKManager.h"
@implementation MIniAppDemoSDKDelegateImpl

+ (instancetype)sharedInstance {
    static MIniAppDemoSDKDelegateImpl *_imp;
    static dispatch_once_t _token;
    dispatch_once(&_token, ^{
        _imp = [MIniAppDemoSDKDelegateImpl new];
    });
    return _imp;
}

- (void)log:(MALogLevel)level msg:(NSString *)msg {
    NSString *strLevel = nil;
    switch (level) {
        case MALogLevelError:
            strLevel = @"Error";
            break;
        case MALogLevelWarn:
            strLevel = @"Warn";
            break;
        case MALogLevelInfo:
            strLevel = @"Info";
            break;
        case MALogLevelDebug:
            strLevel = @"Debug";
            break;
        default:
            strLevel = @"Undef";
            break;
    }
    NSLog(@"TMFMiniApp %@|%@", strLevel, msg);
}

- (NSString *)getAppUID {
    NSString *username = [[TMFAppletLoginEngine sharedInstance] getLoginUsername];
    if(username && username.length>0) {
        return username;
    }
    return @"unknown";
}

- (void)handleStartUpSuccessWithApp:(TMFMiniAppInfo *)app {
     NSLog(@"start successfully %@", app);
    
     [[NSNotificationCenter defaultCenter] postNotificationName:@"com.tencent.tmf.miniapp.demo.change.notification" object:nil];
}

- (void)handleStartUpError:(NSError *)error app:(NSString *)app parentVC:(id)parentVC {
     NSLog(@"Failed to start %@ %@", app, error);
}

- (nonnull NSString *)appName {
    return @"MiniAppDemo";
}

- (NSDictionary *)developLoginCookie {
    return [[TMFAppletLoginEngine sharedInstance] getLoginCookies];
}

- (void)fetchAppUserInfoWithScope:(NSString *)scope block:(TMAAppFetchUserInfoBlock)block {
    if (block) {
        UIImage *defaultAvatar = [UIImage imageWithContentsOfFile:[[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"avatar.png"]];
        UIImageView *avatarView = [[UIImageView alloc] initWithImage:defaultAvatar];
        TMAAppUserInfo *userInfo = [TMAAppUserInfo new];
        userInfo.avatarView = avatarView;
        userInfo.nickName = @"SunWukong";
        block(userInfo);
    }
}
@end
