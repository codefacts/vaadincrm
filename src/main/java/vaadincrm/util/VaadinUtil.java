package vaadincrm.util;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Notification;
import io.crm.util.ExceptionUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.Resp;
import vaadincrm.exceptions.InvalidArgumentException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by someone on 18/08/2015.
 */
final public class VaadinUtil {

    public static Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

    public static ErrorMessage errorMessage(String errorMessages) {
        System.out.println("Error Messages: " + errorMessages);
        return new AbstractErrorMessage(errorMessages) {
        };
    }

    public static void handleError(Throwable throwable) {
        Notification.show(Resp.server_error_pleasy_try_again_later, Notification.Type.ERROR_MESSAGE);
        ExceptionUtil.logException(throwable);
    }
}
