package vaadincrm;

import vaadincrm.util.ExceptionUtil;
import vaadincrm.util.FutureResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by someone on 19/08/2015.
 */
public class Resp {

    public static final String value_is_invalid = "Value is invalid.";
    public static final String _updated_successfully = " updated successfully.";
    public static final String _created_successfully = " created successfully.";
    public static final String Area = "Area";
    public static final String Region = "Region";
    public static final String House = "House";

    public static void main(String... args) throws ExecutionException, InterruptedException {
        final FutureResult futureTask = new FutureResult();

        new Thread(() -> {
            ExceptionUtil.toRuntime(() -> Thread.sleep(10000));
            futureTask.signal("ok");
        }).start();

        System.out.println("GOT: " + futureTask.get());
    }
}

