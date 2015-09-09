package vaadincrm;

import io.crm.Events;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import vaadincrm.util.FutureResult;

import java.util.concurrent.ExecutionException;

import static io.crm.util.ExceptionUtil.toRuntime;

/**
 * Created by someone on 19/08/2015.
 */
final public class Resp {

    public static final String value_is_invalid = "Value is invalid.";
    public static final String _updated_successfully = " updated successfully.";
    public static final String _created_successfully = " created successfully.";
    public static final String Area = "Area";
    public static final String Region = "Region";
    public static final String House = "House";
    public static final String This_user_does_not_have_password = "This user does not have password.";
    public static final String server_error_pleasy_try_again_later = "Server Error. Please try again later.";

    public static void main(String... args) throws ExecutionException, InterruptedException {
        Vertx.clusteredVertx(new VertxOptions(), r -> {
            if (r.failed()) throw new RuntimeException(r.cause());
            final Vertx vertx = r.result();

            vertx.eventBus().send(Events.FIND_EMPLOYEE, new JsonObject(), (AsyncResult<Message<JsonObject>> rr) -> {
                if (rr.failed()) throw new RuntimeException(rr.cause());
                System.err.println("RESULT: " + rr.result().body().encodePrettily());
            });
        });
    }
}

