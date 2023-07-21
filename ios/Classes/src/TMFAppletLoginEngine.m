//
//  TMFAppletLoginEngine.m
//  TMFDemo
//
//  Created by 石磊 on 2022/4/19.
//  Copyright © 2022 Tencent. All rights reserved.
//

#import "TMFAppletLoginEngine.h"
#import "TMFSharkCenter.h"

#define DEV_LOGIN_NAME @"dev_login_name"
#define DEV_LOGIN_COOKIES @"dev_login_cookies"
#define DEV_LOGIN_MODE @"dev_login_mode"
#define DEV_LOGIN_TIME @"dev_login_time"


@implementation TMFAppletLoginEngine {
    NSString *_username;
    NSDictionary *_loginCookies;
}

+ (instancetype)sharedInstance {
    static TMFAppletLoginEngine* manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[TMFAppletLoginEngine alloc] init];
        [manager readLoginInfo];
    });
    return manager;
}

- (void)loginWithName:(NSString *)username andPassword:(NSString *)password inMode:(TMADevelopLoginMode)mode  callbackHandler:(LoginHandler)handler {
    
    __weak TMFAppletLoginEngine *weakSelf = self;
    [[TMFMiniAppSDKManager sharedInstance] loginWithName:username andPassword:password inMode:mode callbackHandler:^(NSDictionary * _Nullable cookies, NSError * _Nullable error) {
        if(cookies) {
            [weakSelf writeInfoFile:username andCookies:cookies];
        }
        if(handler) {
            handler(error);
        }
    }];
}

- (NSString *)getLoginUsername {
    return _username;
}

- (NSDictionary *)getLoginCookies {
    return _loginCookies;
}

- (void)logout {
    _username = nil;
    _loginCookies = nil;
    [self writeInfoFile:nil andCookies:nil];
    
    //退出当前帐号时，关闭所有打开的小程序
    [[TMFMiniAppSDKManager sharedInstance] closeAllApplications];
}

- (void)readLoginInfo {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    

    NSDate *time = [userDefaults objectForKey:DEV_LOGIN_TIME];
    if(time) {
        NSDate *now = [NSDate date];
        
        //保存登录信息24小时,超过24小时打开程序需要重新登录
        NSTimeInterval secondsPerDay = 24*60*60;
        NSTimeInterval secondsBetweenDates= [now timeIntervalSinceDate:time];
        if(secondsBetweenDates < secondsPerDay) {
            _username = [userDefaults objectForKey:DEV_LOGIN_NAME];
            _loginMode = [[userDefaults objectForKey:DEV_LOGIN_MODE] integerValue];
            _loginCookies = [userDefaults objectForKey:DEV_LOGIN_COOKIES];
        }
    }
}


- (void)writeInfoFile:(NSString *)username andCookies:(NSDictionary *)cookie {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];


    if(username == nil || username.length<=0) {
        [userDefaults removeObjectForKey:DEV_LOGIN_NAME];
        [userDefaults removeObjectForKey:DEV_LOGIN_COOKIES];
        [userDefaults removeObjectForKey:DEV_LOGIN_TIME];
        [userDefaults removeObjectForKey:DEV_LOGIN_MODE];
    } else {
        _loginCookies = [[NSDictionary alloc] initWithDictionary:cookie];
        _username = username;
        
        [userDefaults setObject:_username forKey:DEV_LOGIN_NAME];
        [userDefaults setObject:cookie forKey:DEV_LOGIN_COOKIES];
        [userDefaults setObject:[NSDate date] forKey:DEV_LOGIN_TIME];
        [userDefaults setObject:[NSNumber numberWithInteger:_loginMode] forKey:DEV_LOGIN_MODE];
    }
}

@end
