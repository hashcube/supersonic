#import "SupersonicPlugin.h"

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
  
  if (self = [super init]) {
    _supersonicAppKey = nil;
    return self;
  }
  return nil;
}

- (void) initializeWithManifest:(NSDictionary *)manifest
                    appDelegate:(TeaLeafAppDelegate *)appDelegate {
  @try {
    NSDictionary *ios = [manifest valueForKey:@"ios"];
    _supersonicAppKey = [ios valueForKey:@"supersonicAppKey"];
    NSLog(@"{supersonic} Initializing with manifest supersonicAppKey: '%@'",
      self.supersonicAppKey);
    self.viewController = appDelegate.tealeafViewController;
    [SupersonicIntegrationHelper validateIntegration];
  }
  @catch (NSException *exception) {
    NSLog(@"{supersonic} Failed during startup: %@", exception);
  }
}

- (void) initVideoAd:(NSDictionary *)jsonObject {
  NSLog(@"{supersonic} Init VideoAd");
  [[Supersonic sharedInstance] setRVDelegate:self];

  [[Supersonic sharedInstance] initRVWithAppKey:self.supersonicAppKey
      withUserId:(NSString *)[jsonObject objectForKey:@"user_id"]];
}

- (void) initInterstitial:(NSDictionary *)jsonObject {
  NSLog(@"{supersonic} Init Interstitials");
  [[Supersonic sharedInstance] setISDelegate:self];
  [[Supersonic sharedInstance] initISWithAppKey:self.supersonicAppKey
      withUserId:(NSString *)[jsonObject objectForKey:@"user_id"]];
}

- (void) cacheInterstitial:(NSDictionary *)jsonObject {
  NSLog(@"{supersonic} cacheInterstitial");
  [[Supersonic sharedInstance] loadIS];
}

- (void) showInterstitial:(NSDictionary *)jsonObject {
  [[Supersonic sharedInstance] showISWithViewController:self.viewController];
}

- (void) initOfferWallAd:(NSDictionary *)jsonObject {
  [[Supersonic sharedInstance] setOWDelegate:self];

  [[Supersonic sharedInstance] initOWWithAppKey:self.supersonicAppKey
      withUserId:(NSString *)[jsonObject objectForKey:@"user_id"]];
}

- (void) showRVAd:(NSDictionary *)jsonObject {
  [[Supersonic sharedInstance] showRV];
}

- (void) showOffersForUserID:(NSDictionary *)jsonObject {
  [[Supersonic sharedInstance] showOW];
}

#pragma mark SupersonicOWDelegate Functions

// This method gets invoked after a successful initialization of the Offerwall.
- (void)supersonicOWInitSuccess {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

// This method gets invoked each time the Offerwall loaded successfully.
- (void)supersonicOWShowSuccess {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

// This method gets invoked after a failed attempt to initialize the Offerwall.
// If it does happen, check out 'error' for more information and consult our
// Knowledge center.
- (void)supersonicOWInitFailedWithError:(NSError *)error {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

// This method gets invoked after a failed attempt to load the Offerwall.
// If it does happen, check out 'error' for more information and consult our
// Knowledge center.
- (void)supersonicOWShowFailedWithError:(NSError *)error {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

// This method gets invoked after the user had clicked the little
// 'x' button at the top-right corner of the screen.
- (void)supersonicOWAdClosed {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

// This method will be called each time the user has completed an offer.
// All relative information is stored in 'creditInfo' and it is
// specified in more detail in 'SupersonicOWDelegate.h'.
// If you return NO the credit for the last offer will be added to
// Everytime you return 'NO' we aggragate the credit and return it all
// at one time when you return 'YES'.
- (BOOL)supersonicOWDidReceiveCredit:(NSDictionary *)creditInfo {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    return YES;
}

// This method get invoked when the ‘-getOWCredits’ fails to retrieve
// the user's credit balance info.
- (void)supersonicOWFailGettingCreditWithError:(NSError *)error {
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

#pragma mark SupersonicRVDelegate Functions

// This method is invoked after a successful initialization with Supersonic's servers
// It does not mean that there is a video ready to be presented, but it does mean
// that our SDK has all the information it needs.
- (void)supersonicRVInitSuccess {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

// This method is invoked if initialization failed, in which
// case check out 'error'.
- (void)supersonicRVInitFailedWithError:(NSError *)error {
    NSLog(@"%s%@", __PRETTY_FUNCTION__, error);
}

// This method lets you know whether or not there is a video
// ready to be presented. It is only after this method is invoked
// with 'hasAvailableAds' set to 'YES' that you can should 'showRV'.
- (void)supersonicRVAdAvailabilityChanged:(BOOL)hasAvailableAds {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    NSString *available = @"";
    if(hasAvailableAds) {
        available = @"true";
    }
    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
        @"supersonicOnRVAvailabilityChange",@"name",
        available, @"available",
        nil]];
}

// This method gets invoked after the user has been rewarded.
- (void)supersonicRVAdRewarded:(SupersonicPlacementInfo*)placementInfo {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.placementInfo = placementInfo;
}

// This method gets invoked when there is a problem playing the video.
// If it does happen, check out 'error' for more information and consult
// our knowledge center for help.
- (void)supersonicRVAdFailedWithError:(NSError *)error {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

// This method gets invoked when we take control, but before
// the video has started playing.
- (void)supersonicRVAdOpened {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

// This method gets invoked when we return controlback to your hands.
// We chose to notify you about rewards here and not in 'supersonicRVAdRewarded'.
// This is because reward can occur in the middle of the video.
- (void)supersonicRVAdClosed {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    NSString *rewardName = nil;
    NSNumber *rewardedCount = 0;
    if (self.placementInfo) {
        rewardName = self.placementInfo.rewardName;
        rewardedCount = self.placementInfo.rewardAmount;
    }

    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
        @"supersonicRVAdClosed",@"name",
        rewardName, @"placement",
        rewardedCount, @"rewardedCount",
        nil]];

    _placementInfo = nil;
}

// This method gets invoked when the video has started playing.
- (void)supersonicRVAdStarted {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

// This method gets invoked when the video has stopped playing.
- (void)supersonicRVAdEnded {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

/**
* Called when initiation process of the Interstitial products has finished successfully.
**/
-(void)supersonicISInitSuccess{
    NSLog(@"{supersonic} onInterstitialInitSuccess");
}
/**
* Called each time an initiation stage fails, or if you have a problem in
* the integration
* You can learn about the reason by examining the 'error' value
**/
-(void)supersonicISInitFailedWithError:(NSError *)error{
    NSLog(@"{supersonic} onInterstitialInitFailed %@", error);
}
/*
Invoked when Interstitial Ad is ready to be shown after load function was called.
*/
-(void) supersonicISReady{
    NSLog(@"{supersonic} onInterstitialReady");
    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
        @"SupersonicAdAvailable",@"name",
        nil]];
}
/**
* Called each time the Interstitial window has opened successfully.
**/
-(void)supersonicISShowSuccess{
    NSLog(@"{supersonic} onInterstitialShown");
}
/**
* Called if showing the Interstitial for the user has failed.
* You can learn about the reason by examining the ‘error’ value
**/
-(void)supersonicISShowFailWithError:(NSError *)error{ }
/**
* Called each time the end user has clicked on the Interstitial ad.
**/
-(void)supersonicISAdClicked{ }
/**
* Called each time the Interstitial window is about to close
**/
-(void)supersonicISAdClosed{
    NSLog(@"{supersonic} onInterstitialClose");
    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
        @"SupersonicAdDismissed",@"name",
        nil]];
}
/**
Called each time the Interstitial window is about to open
**/
-(void)supersonicISAdOpened{}
/**
Invoked when there is no Interstitial Ad available after calling load function.
*/
-(void)supersonicISFailed{
    NSLog(@"{supersonic} onInterstitialNotAvailable");
    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
        @"SupersonicAdNotAvailable",@"name",
        nil]];
}
@end
