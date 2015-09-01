package vaadincrm.service;

import com.vaadin.ui.UI;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.util.FutureResult;
import vaadincrm.util.VaadinUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static vaadincrm.App.bus;
import static vaadincrm.util.VaadinUtil.handleError;

/**
 * Created by someone on 30/08/2015.
 */
final public class QueryService {
    private static final QueryService service = new QueryService();

    public List<JsonObject> findAll(String dest, JsonObject query) {
        final FutureResult<List<JsonObject>> futureResult = new FutureResult<>();

        final UI ui = UI.getCurrent();
        bus.send(dest, query, (AsyncResult<Message<JsonArray>> r) -> {

            if (r.failed()) {
                futureResult.signalError(r.cause());
                return;
            }

            final JsonArray jsonArray = r.result().body();
            final List<JsonObject> list = jsonArray
                    .stream()
                    .map(s -> s instanceof Map ? new JsonObject((Map<String, Object>) s) : (JsonObject) s)
                    .collect(Collectors.toList());
            futureResult.signal(list);
        });

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            handleError(e);
        }
        return new ArrayList<>();
    }

    public static QueryService getService() {
        return service;
    }

    public JsonObject getDBTree() {
        final FutureResult<JsonObject> futureResult = new FutureResult<>();

        App.bus.send(Events.GET_DB_TREE, new JsonObject(), (AsyncResult<Message<JsonObject>> r) -> {
            if (r.failed()) {
                futureResult.signalError(r.cause());
                return;
            }

            futureResult.signal(r.result().body());
        });

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject getDBTreeWithUsers() {
        final FutureResult<JsonObject> futureResult = new FutureResult<>();

        App.bus.send(Events.GET_DB_TREE_WITH_USERS, new JsonObject(), (AsyncResult<Message<JsonObject>> r) -> {
            if (r.failed()) {
                futureResult.signalError(r.cause());
                return;
            }

            futureResult.signal(r.result().body());
        });

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
