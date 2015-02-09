#import "SupersonicPlugin.h"
#import "SSAMobileSDK/headers/SupersonicAdsPublisher.h"

@interface SupersonicPlugin()<SSAOfferWallDelegate>

@property(nonatomic, retain) NSString * supersonicAppKey;
@property(nonatomic, retain) SupersonicAdsPublisher * ssaPub;

@end

@implementation SupersonicPlugin

// The plugin must call super dealloc.
- (void) dealloc {
	[super dealloc];
}

/* The plugin must call super init.
	Initializing the properties
	_superSonicAppKey with nil value and _ssaPub with SupersonicAdsPublisher singleton
*/
- (id) init {
	self = [super init];
	if (!self) {
		return nil;
	}
	_supersonicAppKey = nil;
    _ssaPub = [SupersonicAdsPublisher sharedInstance];
	return self;
}

- (void) initializeWithManifest:(NSDictionary *)manifest appDelegate:(TeaLeafAppDelegate *)appDelegate {
	@try {
		NSDictionary *ios = [manifest valueForKey:@"ios"];
	    self.supersonicAppKey = [ios valueForKey:@"supersonicAppKey"];
		NSLog(@"{supersonic} Initializing with manifest supersonicAppKey: '%@'", self.supersonicAppKey);
	}
	@catch (NSException *exception) {
		NSLog(@"{supersonic} Failed during startup: %@", exception);
	}
}

- (void) showOffersForUserID:(NSDictionary *)jsonObject {
	[self.ssaPub showOfferWallWithApplicationKey:self.supersonicAppKey
									 userId:(NSString *)[jsonObject objectForKey:@"userID"]
									 delegate:self
									 additionalParameters:@{@"useClientSideCallbacks" : @(YES)}];

}

- (BOOL)ssaOfferWallDidReceiveCredit:(NSDictionary *)creditInfo{
	NSLog(@"Credit Recieved %@",creditInfo);
	[[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
		@"onCreditRecieved",@"name",
		nil]];
    return YES;
}
@end
