#import "TmfFlutterPlugin.h"
#import "TMFMiniAppSDKManager.h"
#import "MIniAppDemoSDKDelegateImpl.h"
#import "TMFAppletLoginEngine.h"

@implementation TmfFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    TmfFlutterPlugin* plugin = [[TmfFlutterPlugin alloc]init];
    PGNTmfHostApiSetup(registrar.messenger,(NSObject<PGNTmfHostApi> *)plugin);
}

- (void)initTmfWithCompletion:(void (^)(FlutterError *))completion {
    [TMFMiniAppSDKManager sharedInstance].miniAppSdkDelegate = [MIniAppDemoSDKDelegateImpl sharedInstance];
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"tmf-ios-configurations" ofType:@"json"];
    if(filePath) {
        TMAServerConfig *config = [[TMAServerConfig alloc] initWithFile:filePath];
        //Configure the device id, which is used to release the applet in gray scale according to the device identification on the management platform
        config.customizedUDID = [self getUDID];
        [[TMFMiniAppSDKManager sharedInstance] setConfiguration:config];
        completion(nil);
    }else{
        FlutterError*error = [FlutterError errorWithCode:@"0" message:@"配置文件不存在" details:nil];
        completion(error);
    }
}



- (void)loginTmfAccount:(NSString *)account password:(NSString *)password isOpenLogin:(NSNumber *)isOpenLogin completion:(void (^)(NSNumber *, FlutterError *))completion {
    [[TMFAppletLoginEngine sharedInstance]loginWithName:account andPassword:password inMode:TMADevelopLoginModeOpenUser callbackHandler:^(NSError * _Nullable error) {
        if(error!=nil){
            completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:error.userInfo.description details:nil]);
        }else{
            [[TMFMiniAppSDKManager sharedInstance] updateCustomizedUserID:account];
            completion(@(YES),nil);
        }
    }];
}

- (void)destroyWithCompletion:(void (^)(FlutterError *))completion {
    [[TMFAppletLoginEngine sharedInstance] logout];
    [[TMFMiniAppSDKManager sharedInstance] updateCustomizedUserID:@""];
    completion(nil);
}

- (void)sendMessageMessage:(PGNMessageData *)message completion:(void (^)(NSNumber *, FlutterError *))completion {
    switch (message.code) {
        case PGNCodeTmfId:
            
        {
            NSDictionary<NSString *, NSString *> * data = message.data;
            NSString* token = data[@"token"];
            NSString* appId = data[@"appId"];
            NSInteger appVerType = [data[@"appVerType"] integerValue];
            //                TMAVersionDevelop = 0,        // 开发版本
            //                TMAVersionPreview = 1,        // 预览版本
            //                TMAVersionAudit = 2,            // 审核版本
            //                TMAVersionOnline = 3,            // 线上版本（全量发布）
            //                TMAVersionLocal = 10,          //本地预置版本，不会进行更新
            TMAVersionType vertype;
            switch (appVerType) {
                case 0:
                    vertype = TMAVersionDevelop;
                    break;
                case 1:
                    vertype = TMAVersionPreview;
                    break;
                case 2:
                    vertype = TMAVersionAudit;
                    break;
                case 3:
                    vertype = TMAVersionOnline;
                    break;
                case 10:
                    vertype = TMAVersionLocal;
                    break;
                default:
                    vertype = TMAVersionDevelop;
                    break;
            }
            
            [ [TMFMiniAppSDKManager sharedInstance] startUpMiniAppWithAppID:appId
                                                                    verType:vertype
                                                                      scene:TMAEntrySceneAIOEntry firstPage:nil paramsStr:[NSString stringWithFormat:@"token=%@",token]
                                                                   parentVC:[self getCurrentViewController] completion:^(NSError * _Nullable error) {
                if(error !=nil){
                    completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:error.userInfo.description details:nil]);
                }else{
                    completion(@(YES),nil);
                }
            }];
        }
            break;
        case PGNCodeTmfLink:
        {
            NSDictionary<NSString *, NSString *> * data = message.data;
            NSString* token = data[@"token"];
            NSString* link = data[@"link"];
            [ [TMFMiniAppSDKManager sharedInstance] startUpMiniAppWithLink:link scene:TMAEntrySceneNone parentVC:[self getCurrentViewController] completion:^(NSError * _Nullable error) {
                if(error !=nil){
                    completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:error.userInfo.description details:nil]);
                }else{
                    completion(@(YES),nil);
                }
            }];
        }
            break;
        default:
            break;
    }
}



- (NSString *)getUDID {
    // Get the singleton mode of NSUserDefaults
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *udid = [userDefaults objectForKey:@"udid"];
    if(udid)
        return udid;
    else {
        udid = [NSString stringWithFormat:@"test%.0f",[[NSDate date] timeIntervalSince1970]];
        
        [userDefaults setObject:udid forKey:@"udid"];
        return udid;
    }
}

-(UIViewController*)getCurrentViewController {
    UIViewController *viewController = nil;
    UIWindow *keyWindow = [[UIApplication sharedApplication] keyWindow];
    if (keyWindow.windowLevel == UIWindowLevelNormal) {
        viewController = keyWindow.rootViewController;
        while (viewController.presentedViewController) {
            viewController = viewController.presentedViewController;
        }
        if ([viewController isKindOfClass:[UITabBarController class]]) {
            UITabBarController *tabBarController = (UITabBarController *)viewController;
            viewController = tabBarController.selectedViewController;
        }
        if ([viewController isKindOfClass:[UINavigationController class]]) {
            UINavigationController *navigationController = (UINavigationController *)viewController;
            viewController = navigationController.visibleViewController;
        }
    }
    return viewController;
}


@end
