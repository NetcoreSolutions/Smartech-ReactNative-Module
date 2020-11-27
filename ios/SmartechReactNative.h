//
//  SmartechReactNative.h
//
//  Created by Jobin Kurian on 30/09/20.
//

#import <React/RCTBridgeModule.h>

static NSString *const kSMTDeeplinkNotificationIdentifier = @"SmartechDeeplinkNotification";
static NSString *const kSMTDeeplinkIdentifier = @"deeplink";
static NSString *const kSMTCustomPayloadIdentifier = @"customPayload";

@interface SmartechReactNative : NSObject <RCTBridgeModule>


@end
