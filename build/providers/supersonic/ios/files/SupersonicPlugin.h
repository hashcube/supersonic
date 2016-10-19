#import "PluginManager.h"
#import "Supersonic/Supersonic.h"

@interface SupersonicPlugin : GCPlugin <SupersonicRVDelegate, SupersonicOWDelegate, SupersonicISDelegate>

@property (nonatomic, strong, readonly) NSString *supersonicAppKey;
@property (nonatomic, strong) SupersonicPlacementInfo *placementInfo;
@property (retain, nonatomic) UIViewController *viewController;

@end
