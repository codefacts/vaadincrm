package vaadincrm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import vaadincrm.intfcs.*;
import vaadincrm.intfcs.Runnable;

/**
 * Created by someone on 26-Jul-2015.
 */
public class ExceptionUtil {

    public static void toRuntime(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sallowRun(vaadincrm.intfcs.Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T sallowCall(Callable callable) {
        try {
            callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Handler<AsyncResult<T>> handle(ConsumerInterface<T> runnable) {
        return r -> {
            if (r.failed()) {
                throw new RuntimeException(r.cause());
            }
            ExceptionUtil.toRuntime(() -> runnable.accept(r.result()));
        };
    }
}
