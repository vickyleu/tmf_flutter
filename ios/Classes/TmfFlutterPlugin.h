#import <Flutter/Flutter.h>
#import "Messages.g.h"
@interface TmfFlutterPlugin : NSObject<FlutterPlugin,PGNTmfHostApi>
+(void)logout;
@end
