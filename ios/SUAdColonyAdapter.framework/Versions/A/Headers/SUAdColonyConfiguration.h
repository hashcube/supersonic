//
//  Copyright (c) 2015 Supersonic. All rights reserved.
//

#import <Supersonic/SupersonicConfiguration.h>

@interface SUAdColonyConfiguration : SupersonicConfiguration

@property (nonatomic, strong)   NSString *      appID;
@property (nonatomic, strong)   NSString *      zoneID;
@property (nonatomic, strong)   NSString *      customID;
@property (nonatomic, strong)   NSDictionary *  options;
@property (nonatomic, strong)   NSString *      userInterestedIn;
@property (nonatomic)           BOOL            turnAllAdsOff;

@property (nonatomic)           BOOL            showLogs;
@property (nonatomic)           BOOL            showPrePopup;
@property (nonatomic)           BOOL            showPostPopup;

@end
