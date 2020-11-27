import { DeviceEventEmitter, NativeEventEmitter, NativeModules } from 'react-native';

const SmartechReactBridge = NativeModules.SmartechReactNative;
const SmartechEventEmitter = NativeModules.SmartechReactEventEmitter ? new NativeEventEmitter(NativeModules.SmartechReactEventEmitter) : DeviceEventEmitter;

function defaultCallback(method, err, res) {
    if (err) {
        console.log('Smartech ' + method + ' default callback error', err);
    }
    else {
        console.log('Smartech ' + method + ' default callback result', res);
    }
}

// Used to handle callback
function callWithCallback(method, args, callback) {
    if (typeof callback === 'undefined' || callback == null || typeof callback !== 'function') {
        callback = (err, res) => {
            defaultCallback(method, err, res);
        };
    }
    if (args == null) {
        args = [];
    }
    args.push(callback);
    SmartechReactBridge[method].apply(this, args);
}

var SmartechReact = {

    // All the constants declared in the Smartech React Bridge.
    SmartechDeeplinkNotification: SmartechReactBridge.SmartechDeeplinkNotification,

    // This method is used to register listener.
    addListener: function (eventName, handler) {
        if (SmartechEventEmitter) {
            SmartechEventEmitter.addListener(eventName, handler);
        }
    },

    // This method is used to unregister registered listener.
    removeListener: function (eventName, handler) {
        if (SmartechEventEmitter) {
            SmartechEventEmitter.removeListener(eventName, handler);
        }
    },

    /**
     *  This method will be used to handle the deeplink
     *  used to open the app.
     */
    getDeepLinkUrl: function (callback) {
        callWithCallback('getDeepLinkUrl', null, callback);
    },

    /**
     * This method is used to track app update event.
     * This method should be called by the developer to track the app updates event to Smartech.
     */
    trackAppInstall: function () {
        SmartechReactBridge.trackAppInstall();
    },

    /**
     * This method is used to track app update event.
     * This method should be called by the developer to track the app updates event to Smartech.
     */
    trackAppUpdate: function () {
        SmartechReactBridge.trackAppUpdate();
    },

    /**
     * This method is used to track app install or update event by Smartech SDK itself.
     * This method should be called by the developer to track the app install or update event by Smartech SDK itself. 
     * If you are calling this method then you should not call trackAppInstall or trackAppUpdate method.
     */
    trackAppInstallUpdateBySmartech: function () {
        SmartechReactBridge.trackAppInstallUpdateBySmartech();
    },

    /**
     * This method is used to track custom event done by the user.
     * This method should be called by the developer to track any custom activites
     * that is performed by the user in the app to Smartech backend.
     */
    trackEvent: function (eventName, payload) {
        SmartechReactBridge.trackEvent(eventName, payload);
    },

    /**
     * This method is used to send login event to Smartech backend.
     * This method should be called only when the app gets the user's identity
     * or when the user does a login activity in the application.
     */
    login: function (identity) {
        SmartechReactBridge.login(identity);
    },

    /**
     * This method would logout the user and clear identity on Smartech backend.
     * This method should be called only when the user log out of the application.
     */
    logoutAndClearUserIdentity: function (isLougoutClearIdentity) {
        SmartechReactBridge.logoutAndClearUserIdentity(isLougoutClearIdentity)
    },

    /**
     * This method would set the user identity locally and with all subsequent events this identity will be send.
     * This method should be called only when the user gets the identity.
     */
    setUserIdentity: function (identity, callback) {
        callWithCallback('setUserIdentity', [identity], callback);
    },

    /**
     * This method would get the user identity that is stored in the SDK.
     * This method should be called to get the user's identity.
     */
    getUserIdentity: function (identity, callback) {
        callWithCallback('getUserIdentity', null, callback);
    },

    /**
     * This method would clear the identity that is stored in the SDK.
     * This method will clear the user's identity by removing it from.
     */
    clearUserIdentity: function () {
        SmartechReactBridge.clearUserIdentity();
    },

    /**
     * This method is used to update the user profile.
     * This method should be called by the developer to update all the user related attributes to Smartech.
     */
    updateUserProfile: function (profilePayload) {
        SmartechReactBridge.updateUserProfile(profilePayload);
    },

    // ----- GDPR Methods ----- 

    /**
     * This method is used to opt tracking.
     * If you call this method then we will opt in or opt out the user of tracking.
     */
    optTracking: function (isTrackingOpted) {
        SmartechReactBridge.optTracking(isTrackingOpted);
    },

    /**
     * This method is used to get the current status of opt tracking.
     * If you call this method you will get the current status of the tracking which can be used to render the UI at app level.
     */
    hasOptedTracking: function (callback) {
        callWithCallback('hasOptedTracking', null, callback);
    },

    /**
     * This method is used to opt push notifications.
     * If you call this method then we will opt in or opt out the user of recieving push notifications.
     */
    optPushNotification: function (isPushNotificationOpted) {
        SmartechReactBridge.optPushNotification(isPushNotificationOpted);
    },

    /**
     * This method is used to get the current status of opt push notification.
     * If you call this method you will get the current status of the tracking which can be used to render the UI at app level.
     */
    hasOptedPushNotification: function (callback) {
        callWithCallback('hasOptedPushNotification', null, callback);
    },

    /**
     * This method is used to opt in-app messages.
     * If you call this method then we will opt in or opt out the user of in-app messages.
     */
    optInAppMessage: function (isInappOpted) {
        SmartechReactBridge.optInAppMessage(isInappOpted);
    },

    /**
     * This method is used to get the current status of opt in-app messages.
     * If you call this method you will get the current status of the opt in-app messages which can be used to render the UI at app level.
     */
    hasOptedInAppMessage: function (callback) {
        callWithCallback('hasOptedInAppMessage', null, callback);
    },

    // ----- Location Methods ----- 

    /**
     * This method is used to set the user's location to the SDK.
     * You need to call this method to set location which will be passed on the Smartech SDK.
     */
    setUserLocation: function (latitude, longitude) {
        SmartechReactBridge.setUserLocation(latitude, longitude);
    },

    // ----- Helper Methods ----- 

    /**
     * This method is used to get the app id used by the Smartech SDK.
     * If you call this method you will get the app id used by the Smartech SDK.
     */

    getAppId: function (callback) {
        callWithCallback('getAppId', null, callback);
    },

    /**
     * This method is used to get the device push token used by Smartech SDK.
     * If you call this method you will get the device push token which is used for sending push notification.
     */
    getDevicePushToken: function (callback) {
        callWithCallback('getDevicePushToken', null, callback);
    },

    /**
     * This method is used to set the device push token used by Smartech SDK.
     * If you call this method you will set the device push token which is used for sending push notification.
     */
    setDevicePushToken: function (token) {
        SmartechReactBridge.setDevicePushToken(token);
    },

    /**
     * This method is used to get the device unique id used by Smartech SDK.
     * If you call this method you will get the device unique id which is used to identify a device on Smartech.
     */

    getDeviceGuid: function (callback) {
        callWithCallback('getDeviceGuid', null, callback);
    },

    /**
     * This method is used to get the current Smartech SDK version.
     * If you call this method you will get the current Smartech SDK version used inside the app.
     */
    getSDKVersion: function (callback) {
        callWithCallback('getSDKVersion', null, callback);
    },

    /**
     * This method is used to fetch the existing device Token already generated by FCM.
     * You should call this method only if you don't have device tokens of existing user's.
     */
    fetchAlreadyGeneratedTokenFromFCM: function () {
        SmartechReactBridge.fetchAlreadyGeneratedTokenFromFCM();
    },

};

module.exports = SmartechReact;

