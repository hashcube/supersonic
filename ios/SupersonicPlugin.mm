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
  _superSonicAppKey with nil and _ssaPub with SupersonicAdsPublisher singleton
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

- (void) initializeWithManifest:(NSDictionary *)manifest
                    appDelegate:(TeaLeafAppDelegate *)appDelegate {
  @try {
    NSDictionary *ios = [manifest valueForKey:@"ios"];
    self.supersonicAppKey = [ios valueForKey:@"supersonicAppKey"];
    NSLog(@"{supersonic} Initializing with manifest supersonicAppKey: '%@'",
      self.supersonicAppKey);
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

/**
 * Called each time the user completes an offer.
 * @param creditInfo - A dictionary with the following key-value pairs:
 * @"credits" - (integer) The number of credits the user has Earned since the
 * last ssaOfferwallDidReceiveCredit event that returned 'YES'. Note that the
 * credits may represent multiple completions (see return parameter).
 * @"totalCredits" - (integer) The total number of credits ever earned by the
 * user.
 * @"totalCreditsFlag" - (boolean) In some cases, we won’t be able to provide
 * the exact amount of credits since the last event(specifically if the user
 * clears the app’s data). In this case the ‘credits’ will be equal to the
 * @"totalCredits", and this flag will be @(YES).
 * @return The publisher should return a boolean stating if he handled this
 * call (notified the user for example). if the return value is 'NO' the
 * 'credits' value will be added to the next call.
**/
- (BOOL)ssaOfferWallDidReceiveCredit:(NSDictionary *)creditInfo {
  [[PluginManager get] dispatchJSEvent:
    [NSDictionary dictionaryWithObjectsAndKeys:@"onCreditReceived",@"name",
      [creditInfo valueForKey: @"credits"], @"credits", nil]];
    return YES;
}
@end
