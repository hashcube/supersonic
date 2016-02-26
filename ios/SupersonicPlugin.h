#import "PluginManager.h"
#import "Supersonic/Supersonic.h"

@interface SupersonicPlugin : GCPlugin <SupersonicRVDelegate, SupersonicOWDelegate>

@property (nonatomic, strong, readonly) NSString *supersonicAppKey;
@property (nonatomic, strong) SupersonicPlacementInfo *placementInfo;

@end