package vaadincrm.util;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.exceptions.InvalidArgumentException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by someone on 18/08/2015.
 */
public class Util {
    public static final String mongoDateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(mongoDateFormatString);
        }
    };

    public static Date toDate(final String isoString) throws ParseException {
        return mongoDateFormat().parse(isoString);
    }

    public static String toIsoString(final Date date) {
        return mongoDateFormat().format(date) + "Z";
    }

    public static void validateMongoDate(String iso_date) {
        try {
            mongoDateFormat().parse(iso_date);
        } catch (ParseException e) {
            throw new InvalidArgumentException("ISO DATE " + iso_date + " is invalid.");
        }
    }

    public static JsonObject toMongoDate(String iso_string) {
        validateMongoDate(iso_string);
        return new JsonObject().put("$date", iso_string);
    }

    public static JsonObject toMongoDate(Date date) {
        return new JsonObject().put("$date", toIsoString(date));
    }

    public static Date parseMongoDate(JsonObject jsonObject) throws ParseException {
        return toDate(jsonObject.getString("$date"));
    }

    public static DateFormat mongoDateFormat() {
        return DATE_FORMAT_THREAD_LOCAL.get();
    }

    public static Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

    public static String nullToEmpty(String message) {
        return message == null ? "" : message;
    }

    public static ErrorMessage errorMessage(String errorMessages) {
        System.out.println("Error Messages: " + errorMessages);
        return new AbstractErrorMessage(errorMessages) {};
    }
}
