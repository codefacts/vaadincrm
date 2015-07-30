package vaadincrm.util;

import vaadincrm.intfcs.*;

/**
 * Created by someone on 26-Jul-2015.
 */
public class ExceptionUtil {
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
}
