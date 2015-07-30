package vaadincrm.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
public class DashboardEventBus implements SubscriberExceptionHandler {

    private static final DashboardEventBus dashboardEventBus = new DashboardEventBus();

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        dashboardEventBus.eventBus.post(event);
    }

    public static void register(final Object object) {
        dashboardEventBus.eventBus.register(object);
    }

    public static void unregister(final Object object) {
        dashboardEventBus.eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
                                      final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
