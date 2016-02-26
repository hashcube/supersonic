//
//  Copyright (c) 2015 Supersonic. All rights reserved.
//

#ifndef SUPERSONIC_IS_DELEGATE_H
#define SUPERSONIC_IS_DELEGATE_H

#import <Foundation/Foundation.h>

@protocol SupersonicISDelegate <NSObject>

@required

/*!
 * @discussion Called when initiation process of the Interstitial ad unit has finished successfully.
 */
- (void)supersonicISInitSuccess;

/*!
 * @discussion Called when initiation stage fails, or if you have a problem in the integration.
 *
 *              You can learn about the reason by examining the 'error' value
 */
- (void)supersonicISInitFailedWithError:(NSError *)error;


/*!
 * @discussion Called each time the Interstitial window has opened successfully.
 */
- (void)supersonicISShowSuccess;

/*!
 * @discussion Called if showing the Interstitial for the user has failed.
 * 
 *              You can learn about the reason by examining the ‘error’ value
 */
- (void)supersonicISShowFailWithError:(NSError *)error;

/*!
 * @discussion Called each time the Interstitial availability state has changed. 
 *
 *              When available value is YES the next interstitial ad is ready to display, 
 *              When receiving NO value make sure you don't call the showInterstitial method.
 */
- (void)supersonicISAdAvailable:(BOOL)available;

/*!
 * @discussion Called each time the end user has clicked on the Interstitial ad.
 */
- (void)supersonicISAdClicked;

/*!
 * @discussion Called each time the Interstitial window is about to close
 */
- (void)supersonicISAdClosed;

@end

#endif