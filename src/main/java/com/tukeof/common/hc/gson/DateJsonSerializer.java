package com.tukeof.common.hc.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Create by tuke on 2019-02-16
 */
class DateJsonSerializer implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        // format Date to Long
        // the number of milliseconds since January 1, 1970, 00:00:00 GMT
        return new JsonPrimitive(src.getTime());
    }
}
