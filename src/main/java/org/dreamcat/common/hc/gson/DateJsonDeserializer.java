package org.dreamcat.common.hc.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Create by tuke on 2019-02-16
 */
class DateJsonDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // format Long to Date
        return new Date(json.getAsLong());
    }
}
