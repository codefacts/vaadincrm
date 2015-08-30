package vaadincrm;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by someone on 13-Jul-2015.
 */
@SpringBootApplication
public class App {
    public static final String home = "localhost";
    public static final String office = "192.168.100.203";
    public static EventBus bus;
    public static Vertx vertx;
    public static ConfigurableApplicationContext ctx;

    public static final JsonObject config = new JsonObject(MainVerticle.loadConfig("/mongo-config.json"));

    public static void main(String... args) {
        Vertx.clusteredVertx(new VertxOptions(), new Handler<AsyncResult<Vertx>>() {
            @Override
            public void handle(AsyncResult<Vertx> event) {
                if (event.succeeded()) {
                    event.result().deployVerticle(new MainVerticle());
                }
            }
        });
    }
}
