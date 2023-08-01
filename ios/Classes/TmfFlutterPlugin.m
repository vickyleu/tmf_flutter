#import "TmfFlutterPlugin.h"
#import "TMFMiniAppSDKManager.h"
#import "MIniAppDemoSDKDelegateImpl.h"
#import "TMFAppletLoginEngine.h"
#import "TMAConfigDefine.h"
#import <objc/runtime.h>



@implementation UIViewController (Swizzling)

// 使用关联对象来存储原始方法的实现和回调的 block
static char origShouldAutorotateIMPKey;
static char customShouldAutorotateCallbackKey;

- (BOOL)tmf_customShouldAutorotate {
    // 你可以在这里捕获shouldAutorotate被调用的事件
    NSLog(@"Custom shouldAutorotate has been called.");

    // 获取并调用原始方法的实现
    id associatedObject = objc_getAssociatedObject(self, &origShouldAutorotateIMPKey);
    // Convert it back to IMP
    IMP originalImp = NULL; // Default value in case the associatedObject is nil or not of type NSValue.
    if ([associatedObject isKindOfClass:[NSValue class]]) {
        NSValue *originalImpValue = (NSValue *)associatedObject;
        originalImp = [originalImpValue pointerValue];
        BOOL (*originalShouldAutorotateImp)(id, SEL) = (BOOL (*)(id, SEL))originalImp;
        if (originalShouldAutorotateImp) {
            // 获取回调的 block
            id callbackBlock = objc_getAssociatedObject(self, &customShouldAutorotateCallbackKey);
            if (callbackBlock && [callbackBlock isKindOfClass:NSClassFromString(@"NSBlock")]) {
                // 执行回调的 block
                void (^customBlock)(BOOL) = (void (^)(BOOL))callbackBlock;
                customBlock(originalShouldAutorotateImp(self, _cmd));
            }
        }
    }
    return NO;  // 或者返回其他默认值
}

- (void)tmf_swizzleShouldAutorotateWithCallback:(void (^)(BOOL))callbackBlock {
    Method originalMethod = class_getInstanceMethod([self class], @selector(shouldAutorotate));
    Method swizzledMethod = class_getInstanceMethod([self class], @selector(tmf_customShouldAutorotate));

    // 存储原始方法的实现
    IMP originalImp = method_getImplementation(originalMethod);
    NSValue *originalImpValue = [NSValue valueWithPointer:originalImp];
    objc_setAssociatedObject(self, &origShouldAutorotateIMPKey, originalImpValue, OBJC_ASSOCIATION_RETAIN_NONATOMIC);

    // 存储回调的 block
    objc_setAssociatedObject(self, &customShouldAutorotateCallbackKey, callbackBlock, OBJC_ASSOCIATION_COPY_NONATOMIC);

    // 交换方法实现
    method_exchangeImplementations(originalMethod, swizzledMethod);
}

- (void)tmf_removeSwizzledShouldAutorotate {
    // 还原方法交换
    Method originalMethod = class_getInstanceMethod([self class], @selector(shouldAutorotate));
    Method swizzledMethod = class_getInstanceMethod([self class], @selector(tmf_customShouldAutorotate));

    method_exchangeImplementations(originalMethod, swizzledMethod);

    // 移除回调的 block
    objc_setAssociatedObject(self, &customShouldAutorotateCallbackKey, nil, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

@end




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

    TMADevelopLoginMode mode = isOpenLogin? TMADevelopLoginModeOpenUser : TMADevelopLoginModeManager;


    //    TMA_SK_MINIAPP_LoadingPageRightView = @"1";


    [[TMFAppletLoginEngine sharedInstance] setLoginMode: mode];
    [[TMFAppletLoginEngine sharedInstance]loginWithName:account andPassword:password inMode:mode callbackHandler:^(NSError * _Nullable error) {
        if(error!=nil){
            NSString *descriptionValue = [error.userInfo objectForKey:NSLocalizedDescriptionKey];
            completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:descriptionValue details:nil]);
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

static void watchBack(UIViewController *vc) {

    [vc tmf_swizzleShouldAutorotateWithCallback:^(BOOL) {
        if([TMFAppletLoginEngine sharedInstance].getLoginUsername != nil ){
            NSLog(@"清除缓存--->>啦");
            [[TMFMiniAppSDKManager sharedInstance] updateCustomizedUserID:nil];
            [[TMFAppletLoginEngine sharedInstance] logout];
//            [[TMFMiniAppSDKManager sharedInstance] clearMiniAppCache];//TODO 由于startUpMiniAppWithLink无法带参数,清除缓存意味着切换到appid时也是无法打开的
            [vc tmf_removeSwizzledShouldAutorotate];
        }
    }];
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
            __block BOOL callbackExecuted = NO;

            UIViewController*vc = [self getCurrentViewController];
            NSLog(@"清除缓存-->>重新赋值");
            [ [TMFMiniAppSDKManager sharedInstance] startUpMiniAppWithAppID:appId
                                                                    verType:vertype
                                                                      scene:TMAEntrySceneAIOEntry firstPage:nil paramsStr:[NSString stringWithFormat:@"token=%@",token]
                                                                   parentVC:vc completion:^(NSError * _Nullable error) {

                if(error !=nil){
                    callbackExecuted = YES;
                    NSString *descriptionValue = [error.userInfo objectForKey:NSLocalizedDescriptionKey];
                    completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:descriptionValue details:nil]);
                }else{
                    watchBack(vc);
                    completion(@(YES),nil);
                }
            }];
            //除非error被执行,否则执行完成回调

            // 延迟0.5秒后执行的回调
            double delayInSeconds = 0.5;
            dispatch_time_t delayTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
            dispatch_after(delayTime, dispatch_get_main_queue(), ^{
                // 这里是要执行的代码块
                if(!callbackExecuted){
                    watchBack(vc);
                    completion(@(YES),nil);
                }
            });

        }
            break;
        case PGNCodeTmfLink:
        {
            NSDictionary<NSString *, NSString *> * data = message.data;
            NSString* token = data[@"token"];
            NSString* link = data[@"link"];

            __block BOOL callbackExecuted = NO;
            UIViewController*vc = [self getCurrentViewController];

            [ [TMFMiniAppSDKManager sharedInstance] startUpMiniAppWithLink:link scene:TMAEntrySceneSearch parentVC:vc completion:^(NSError * _Nullable error) {

                if(error !=nil){
                    callbackExecuted = YES;
                    NSString *descriptionValue = [error.userInfo objectForKey:NSLocalizedDescriptionKey];
                    completion(@(NO),[FlutterError errorWithCode:[@(error.code) stringValue] message:descriptionValue details:nil]);
                }else{
                    watchBack(vc);
                    //这里不会执行的,
                    completion(@(YES),nil);
                }
            }];
            //除非error被执行,否则执行完成回调

            // 延迟0.5秒后执行的回调
            double delayInSeconds = 0.5;
            dispatch_time_t delayTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
            dispatch_after(delayTime, dispatch_get_main_queue(), ^{
                // 这里是要执行的代码块
                if(!callbackExecuted){
                    watchBack(vc);
                    completion(@(YES),nil);
                }
            });

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



