package com.netcore.reactnative;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SmartechHelper {

    public static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, JSONObject.NULL);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    ReadableMap map = readableMap.getMap(key);
                    if (map != null) {
                        object.put(key, convertMapToJson(map));
                    }
                    break;
                case Array:
                    ReadableArray readableArray = readableMap.getArray(key);
                    if (readableArray != null) {
                        object.put(key, convertArrayToJson(readableArray));
                    }
                    break;
            }
        }
        return object;
    }

    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    ReadableMap map = readableArray.getMap(i);
                    if (map != null) {
                        array.put(convertMapToJson(map));
                    }
                    break;
                case Array:
                    ReadableArray arr = readableArray.getArray(i);
                    if (arr != null) {
                        array.put(convertArrayToJson(arr));
                    }
                    break;
            }
        }
        return array;
    }

    public static HashMap<String, Object> convertReadableMapToHashMap(ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        HashMap<String, Object> deconstructedMap = new HashMap<>();
        try {
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                ReadableType type = readableMap.getType(key);
                switch (type) {
                    case Null:
                        deconstructedMap.put(key, null);
                        break;
                    case Boolean:
                        deconstructedMap.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        deconstructedMap.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        deconstructedMap.put(key, readableMap.getString(key));
                        break;
                    case Map:
                        ReadableMap map = readableMap.getMap(key);
                        if (map != null) {
                            deconstructedMap.put(key, convertReadableMapToHashMap(map));
                        }
                        break;
                    case Array:
                        ReadableArray readableArray = readableMap.getArray(key);
                        if (readableArray != null) {
                            deconstructedMap.put(key, convertReadableArrayToList(readableArray));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return deconstructedMap;
    }

    private static List<Object> convertReadableArrayToList(ReadableArray readableArray) {
        List<Object> deconstructedList = new ArrayList<>(readableArray.size());
        try {
            for (int i = 0; i < readableArray.size(); i++) {
                ReadableType indexType = readableArray.getType(i);
                switch (indexType) {
                    case Null:
                        deconstructedList.add(i, null);
                        break;
                    case Boolean:
                        deconstructedList.add(i, readableArray.getBoolean(i));
                        break;
                    case Number:
                        deconstructedList.add(i, readableArray.getDouble(i));
                        break;
                    case String:
                        deconstructedList.add(i, readableArray.getString(i));
                        break;
                    case Map:
                        ReadableMap map = readableArray.getMap(i);
                        if (map != null) {
                            deconstructedList.add(i, convertReadableMapToHashMap(map));
                        }
                        break;
                    case Array:
                        ReadableArray array = readableArray.getArray(i);
                        if (array != null) {
                            deconstructedList.add(i, convertReadableArrayToList(array));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Could not convert object at index " + i + ".");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return deconstructedList;
    }

    public static WritableMap jsonToWritableMap(JSONObject jsonObject) {
        WritableMap writableMap = new WritableNativeMap();

        if (jsonObject == null) {
            return null;
        }


        Iterator<String> iterator = jsonObject.keys();
        if (!iterator.hasNext()) {
            return null;
        }

        while (iterator.hasNext()) {
            String key = iterator.next();

            try {
                Object value = jsonObject.get(key);

                if (value == null) {
                    writableMap.putNull(key);
                } else if (value instanceof Boolean) {
                    writableMap.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    writableMap.putInt(key, (Integer) value);
                } else if (value instanceof Double || value instanceof Long || value instanceof Float) {
                    String str = String.valueOf(value);
                    writableMap.putDouble(key, Double.parseDouble(str));
                } else if (value instanceof String) {
                    writableMap.putString(key, value.toString());
                } else if (value instanceof JSONObject) {
                    writableMap.putMap(key, jsonToWritableMap((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    writableMap.putArray(key, jsonArrayToWritableArray((JSONArray) value));
                } else if (value.getClass().isEnum()) {
                    writableMap.putString(key, value.toString());
                }
            } catch (JSONException ex) {
                // Do nothing and fail silently
            }
        }

        return writableMap;
    }

    public static WritableArray jsonArrayToWritableArray(JSONArray jsonArray) {
        WritableArray writableArray = new WritableNativeArray();

        if (jsonArray == null) {
            return null;
        }

        if (jsonArray.length() <= 0) {
            return null;
        }

        for (int i = 0 ; i < jsonArray.length(); i++) {
            try {
                Object value = jsonArray.get(i);

                if (value == null) {
                    writableArray.pushNull();
                } else if (value instanceof Boolean) {
                    writableArray.pushBoolean((Boolean) value);
                } else if (value instanceof Integer) {
                    writableArray.pushInt((Integer) value);
                } else if (value instanceof Double || value instanceof Long || value instanceof Float) {
                    String str = String.valueOf(value);
                    writableArray.pushDouble(Double.parseDouble(str));
                } else if (value instanceof String) {
                    writableArray.pushString(value.toString());
                } else if (value instanceof JSONObject) {
                    writableArray.pushMap(jsonToWritableMap((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    writableArray.pushArray(jsonArrayToWritableArray((JSONArray) value));
                } else if (value.getClass().isEnum()) {
                    writableArray.pushString(value.toString());
                }
            } catch (JSONException e) {
                // Do nothing and fail silently
            }
        }

        return writableArray;
    }
}
