package vaadincrm;

import io.vertx.core.*;
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
        runAllModules();
    }

    public static void runAllModules(String... args) {
        Vertx.vertx().deployVerticle(AllModuleStarterVerticle.class.getName());
    }

    public static void runAllModulesClustered(String... args) {
        Vertx.clusteredVertx(new VertxOptions(), r -> {
            if (r.failed()) {
                throw new RuntimeException(r.cause());
            }
            r.result().deployVerticle(AllModuleStarterVerticle.class.getName(), new DeploymentOptions(), r1 -> {
                if (r1.failed()) {
                    throw new RuntimeException(r1.cause());
                }
                System.out.println("*******************************-----CLUSTER_COMPLETE-----*****************************************");
            });
        });
    }

    public static void run() {
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
