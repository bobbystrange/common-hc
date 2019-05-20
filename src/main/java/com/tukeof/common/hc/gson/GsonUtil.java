package com.tukeof.common.hc.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2018/10/14
 */
public class GsonUtil {

    /**
     * object serializable
     *
     * @param object List, Map or Pojo
     * @return json string
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    // Boolean, Double, String, ArrayList, Map
    public static <T> List<T> toList(String jsonArray) {
        Type typeOfT = new TypeToken<Collection<T>>() {
        }.getType();
        return gson.fromJson(jsonArray, typeOfT);
    }

    public static Map<String, Object> toMap(String jsonObject) {
        JsonElement root = toJsonElement(jsonObject);
        return toMap(root.getAsJsonObject());
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static Gson newGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Date.class, new DateJsonSerializer());
        gb.registerTypeAdapter(Date.class, new DateJsonDeserializer());
        gb.setDateFormat(DateFormat.LONG);
        return gb.create();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static JsonElement toJsonElement(String json) {
        return gson.fromJson(json, JsonElement.class);
    }

    public static Integer getInt(String json, String memberName){
        return getInt(toJsonElement(json), memberName);
    }

    public static Integer getInt(JsonElement jsonElement, String memberName){
        if (jsonElement.isJsonObject()){
            JsonElement memberElement = jsonElement.getAsJsonObject().get(memberName);
            if (memberElement != null && memberElement.isJsonPrimitive()){
                JsonPrimitive memberPrimitive = memberElement.getAsJsonPrimitive();
                if (memberPrimitive.isNumber()){
                    return memberPrimitive.getAsInt();
                }
            }
        }
        return null;
    }

    public static Long getLong(String json, String memberName){
        return getLong(toJsonElement(json), memberName);
    }

    public static Long getLong(JsonElement jsonElement, String memberName){
        if (jsonElement.isJsonObject()){
            JsonElement memberElement = jsonElement.getAsJsonObject().get(memberName);
            if (memberElement != null && memberElement.isJsonPrimitive()){
                JsonPrimitive memberPrimitive = memberElement.getAsJsonPrimitive();
                if (memberPrimitive.isNumber()){
                    return memberPrimitive.getAsLong();
                }
            }
        }
        return null;
    }

    public static Double getDouble(String json, String memberName){
        return getDouble(toJsonElement(json), memberName);
    }

    public static Double getDouble(JsonElement jsonElement, String memberName){
        if (jsonElement.isJsonObject()){
            JsonElement memberElement = jsonElement.getAsJsonObject().get(memberName);
            if (memberElement != null && memberElement.isJsonPrimitive()){
                JsonPrimitive memberPrimitive = memberElement.getAsJsonPrimitive();
                if (memberPrimitive.isNumber()){
                    return memberPrimitive.getAsDouble();
                }
            }
        }
        return null;
    }

    public static Boolean getBoolean(String json, String memberName){
        return getBoolean(toJsonElement(json), memberName);
    }

    public static Boolean getBoolean(JsonElement jsonElement, String memberName){
        if (jsonElement.isJsonObject()){
            JsonElement memberElement = jsonElement.getAsJsonObject().get(memberName);
            if (memberElement != null && memberElement.isJsonPrimitive()){
                JsonPrimitive memberPrimitive = memberElement.getAsJsonPrimitive();
                if (memberPrimitive.isBoolean()){
                    return memberPrimitive.getAsBoolean();
                }
            }
        }
        return null;
    }

    public static String getString(String json, String memberName){
        return getString(toJsonElement(json), memberName);
    }

    public static String getString(JsonElement jsonElement, String memberName){
        if (jsonElement.isJsonObject()){
            JsonElement memberElement = jsonElement.getAsJsonObject().get(memberName);
            if (memberElement != null && memberElement.isJsonPrimitive()){
                JsonPrimitive memberPrimitive = memberElement.getAsJsonPrimitive();
                if (memberPrimitive.isString()){
                    return memberPrimitive.getAsString();
                }
            }
        }
        return null;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static final Gson gson = newGson();

    private static Object toObject(JsonElement element) {
        if (element.isJsonObject()) {
            toMap(element.getAsJsonObject());
        }
        if (element.isJsonArray()) {
            return toList(element.getAsJsonArray());
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isBoolean()){
                return jsonPrimitive.getAsBoolean();
            }
            if (jsonPrimitive.isNumber()){
                return jsonPrimitive.getAsDouble();
            }
            if (jsonPrimitive.isString()){
                return jsonPrimitive.getAsString();
            }
        }

        return null;
    }

    private static Map<String, Object> toMap(JsonObject object) {
        Map<String, Object> map = new HashMap<>();
        object.entrySet().forEach(entry -> {
            map.put(entry.getKey(), toObject(entry.getValue()));
        });
        return map;
    }

    private static List<Object> toList(JsonArray jsonArray) {
        int size = jsonArray.size();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JsonElement member = jsonArray.get(i);
            list.add(toObject(member));
        }
        return list;
    }
}
