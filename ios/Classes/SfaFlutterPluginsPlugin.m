#import "SfaFlutterPluginsPlugin.h"
#import <sfa_flutter_plugins/sfa_flutter_plugins-Swift.h>

@implementation SfaFlutterPluginsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSfaFlutterPluginsPlugin registerWithRegistrar:registrar];
}
@end
