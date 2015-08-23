package vaadincrm.util;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by someone on 20/08/2015.
 */
public class FutureResult<T> extends FutureTask<T> {
    public FutureResult() {
        super(() -> null);
    }

    public void signal(T value) {
        set(value);
    }

    public void signalError(Throwable throwable) {
        setException(throwable);
    }
}
