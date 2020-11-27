package com.netcore.reactnative;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.netcore.android.Smartech;
import com.netcore.android.inapp.InAppCustomHTMLListener;
import com.netcore.android.notification.SMTNotificationClickListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmartechReactNativeModule extends ReactContextBaseJavaModule implements SMTNotificationClickListener, InAppCustomHTMLListener {

    private final ReactApplicationContext reactContext;
    private static final String TAG = SmartechReactNativeModule.class.getSimpleName();
    private Smartech smartech = null;
    public static Intent mIntent = null;
    private static final String MODULE_NAME = "SmartechReactNative";
    private static final String SmartechDeeplinkNotification = "SmartechDeeplinkNotification";
    private static final String SmartechDeepLinkIdentifier = "deeplink";
    private static final String SmartechCustomPayloadIdentifier = "customPayload";

    public SmartechReactNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        initSDK();
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @javax.annotation.Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(SmartechDeeplinkNotification, SmartechDeeplinkNotification);
        return constants;
    }

    private void initSDK() {
        if (smartech == null) {
            smartech = Smartech.getInstance(new WeakReference<Context>(this.reactContext));
            smartech.setSMTNotificationClickListener(this);
        }
    }

    @ReactMethod
    public static void init(Intent intent) {
        mIntent = intent;
    }

    @ReactMethod
    public void getDeepLinkUrl(Callback callback) {
        ReadableMap payload = processDeeplinkIntent(mIntent);
        callbackHandler(callback, payload);
    }


    // This method will be used to handle CustomHTML data
    @Override
    public void customHTMLCallback(@Nullable HashMap<String, Object> hashMap) {
        if (hashMap != null) {
            try {
                JSONObject smtCustomPayload = new JSONObject(hashMap);
                ReadableMap objPayload = SmartechHelper.jsonToWritableMap(smtCustomPayload);
                this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(SmartechDeeplinkNotification, objPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // This method will be handle notification click.
    @Override
    public void onNotificationClick(@NotNull Intent intent) {
        try {
            ReadableMap deeplinkPayload = processDeeplinkIntent(intent);
            System.out.println("Deeplink Pyload : "+deeplinkPayload);
            this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(SmartechDeeplinkNotification, deeplinkPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Used to process intent.
    private ReadableMap processDeeplinkIntent(Intent intent) {
        WritableMap smtData = new WritableNativeMap();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String value = extras.toString();
                String deeplinkPath = "";
                String customPayload = "";
                if (extras.containsKey("clickDeepLinkPath")) {
                    deeplinkPath = extras.getString("clickDeepLinkPath");
                    System.out.println("Deeplink Pyload : "+deeplinkPath);
                    if (extras.containsKey("clickCustomPayload")) {
                        customPayload = extras.getString("clickCustomPayload");
                    }
                    try {
                        smtData.putString(SmartechDeepLinkIdentifier, deeplinkPath);
                        smtData.putString(SmartechCustomPayloadIdentifier, customPayload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return smtData;
    }

    // This method is used to track app install event.
    @ReactMethod
    public void trackAppInstall() {
        try {
            smartech.trackAppInstall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to track app update event.
    @ReactMethod
    public void trackAppUpdate() {
        try {
            smartech.trackAppUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to track app install or update event by Smartech SDK itself.
    @ReactMethod
    public void trackAppInstallUpdateBySmartech() {
        try {
            smartech.trackAppInstallUpdateBySmartech();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to track custom event done by the user.
    @ReactMethod
    public void trackEvent(String eventName, ReadableMap payload) {
        try {
            HashMap<String, Object> hmapPayload = SmartechHelper.convertReadableMapToHashMap(payload);
            smartech.trackEvent(eventName, hmapPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to send login event to Smartech backend.
     * This method should be called only when the app gets the user's identity
     * or when the user does a login activity in the application.
     */
    @ReactMethod
    public void login(String identity) {
        try {
            smartech.setUserIdentity(identity);
            smartech.login(identity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method would logout the user and clear identity on Smartech backend.
     * This method should be called only when the user log out of the application.
     */
    @ReactMethod
    public void logoutAndClearUserIdentity(Boolean isLogout) {
        try {
            smartech.logoutAndClearUserIdentity(isLogout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method would set the user identity locally and with all subsequent events this identity will be send.
    @ReactMethod
    public void setUserIdentity(String identity, Callback callback) {
        try {
            if (identity != null && identity.length() > 0) {
                smartech.setUserIdentity(identity);
                callbackHandler(callback, "Identity is set successfully.");
            } else {
                callbackHandler(callback, "Expected one non-empty string argument.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method would get the user identity that is stored in the SDK.
    @ReactMethod
    private void getUserIdentity(Callback callback) {
        try {
            String userIdentity = smartech.getUserIdentity();
            callbackHandler(callback, userIdentity);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method would clear the identity that is stored in the SDK.
    @ReactMethod
    public void clearUserIdentity() {
        try {
            smartech.clearUserIdentity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to update the user profile.
    @ReactMethod
    public void updateUserProfile(ReadableMap profileData) {
        try {
            HashMap<String, Object> hmapProfile = SmartechHelper.convertReadableMapToHashMap(profileData);
            smartech.updateUserProfile(hmapProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----- GDPR Methods -----

    // This method is used to opt tracking.
    @ReactMethod
    public void optTracking(Boolean value) {
        try {
            smartech.optTracking(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to get the current status of opt tracking.
    @ReactMethod
    public void hasOptedTracking(Callback callback) {
        try {
            Boolean isTracking = smartech.hasOptedTracking();
            callbackHandler(callback, isTracking);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method is used to opt push notifications.
    @ReactMethod
    public void optPushNotification(Boolean value) {
        try {
            smartech.optPushNotification(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to get the current status of opt push notification.
    @ReactMethod
    public void hasOptedPushNotification(Callback callback) {
        try {
            Boolean isPushNotificationOpted = smartech.hasOptedPushNotification();
            callbackHandler(callback, isPushNotificationOpted);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    //  This method is used to opt in-app messages.
    @ReactMethod
    public void optInAppMessage(Boolean value) {
        try {
            smartech.optInAppMessage(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to get the current status of opt in-app messages.
    @ReactMethod
    public void hasOptedInAppMessage(Callback callback) {
        try {
            Boolean isInAppOpted = smartech.hasOptedInAppMessage();
            callbackHandler(callback, isInAppOpted);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // ----- Location Methods -----

    // This method is used to set the user's location to the SDK.
    @ReactMethod
    public void setUserLocation(Double latitude, Double longitude) {
        try {
            Location location = new Location("Smartech");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            smartech.setUserLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----- Helper Methods -----

    // This method is used to get the app id used by the Smartech SDK.
    @ReactMethod
    public void getAppId(Callback callback) {
        try {
            String appId = smartech.getAppID();
            callbackHandler(callback, appId);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method is used to get the device push token used by Smartech SDK.
    @ReactMethod
    public void getDevicePushToken(Callback callback) {
        try {
            String token = smartech.getDevicePushToken();
            callbackHandler(callback, token);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method is used to get the device unique id used by Smartech SDK.
    @ReactMethod
    public void getDeviceGuid(Callback callback) {
        try {
            String GUID = smartech.getDeviceUniqueId();
            callbackHandler(callback, GUID);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method is used to get the current Smartech SDK version.
    @ReactMethod
    public void getSDKVersion(Callback callback) {
        try {
            String sdkVersion = smartech.getSDKVersion();
            callbackHandler(callback, sdkVersion);
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandler(callback, "Exception: " + e.getMessage());
        }
    }

    // This method is used to set device push tokens which is used by SDK to send notifications.
    @ReactMethod
    public void setDevicePushToken(String token) {
        try {
            smartech.setDevicePushToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method will be used to fetch already generated tokens for existings users.
    @ReactMethod
    public void fetchAlreadyGeneratedTokenFromFCM() {
        try {
            smartech.fetchAlreadyGeneratedTokenFromFCM();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Used to handle callback.
    private void callbackHandler(Callback callback, Object response) {
        if (callback == null) {
            Log.i(TAG, "Callback is null.");
            return;
        }

        try {
            callback.invoke(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // for convertind JSON to hashmap.
    private static HashMap<String, Object> jsonToHashMap(JSONObject jsonObject) throws JSONException {
        HashMap<String, Object> hashMap = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            hashMap.put(key, value);
        }
        return hashMap;
    }
}
