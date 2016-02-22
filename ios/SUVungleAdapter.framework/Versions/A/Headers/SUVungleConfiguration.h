//
//  Copyright (c) 2015 Supersonic. All rights reserved.
//


#import <Supersonic/SupersonicConfiguration.h>

@interface SUVungleConfiguration : SupersonicConfiguration

@property (nonatomic, strong)   NSString *      appID;
@property (nonatomic, strong)   NSString *      userId;
@property (nonatomic, strong)   NSString *      incentivizedAlertText;
@property (nonatomic, strong)   NSDictionary *  options;
@property (nonatomic)           BOOL            s2sCallbackSupport;

@property (nonatomic)           BOOL            enableLogging;
@property (nonatomic)           BOOL            muted;


@end
