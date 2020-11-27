//
//  SmartechReactEventEmitter.m
//  smartech-reactnative
//
//  Created by Jobin Kurian on 26/10/20.
//

#import "SmartechReactEventEmitter.h"

@implementation SmartechReactEventEmitter

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
    return @[kSMTDeeplinkNotificationIdentifier];
}

- (void)startObserving {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(emitEventInternal:)
                                                 name:kSMTDeeplinkNotificationIdentifier
                                               object:nil];
}

- (void)stopObserving {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)emitEventInternal:(NSNotification *)notification {
    [self sendEventWithName:notification.name body:notification.userInfo];
}

@end
