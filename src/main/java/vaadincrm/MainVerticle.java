package vaadincrm;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import vaadincrm.codec.*;
import vaadincrm.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by someone on 26-Jul-2015.
 */
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        App.bus = getVertx().eventBus();
        App.vertx = getVertx();

        vertx.executeBlocking((r) -> {
            final ConfigurableApplicationContext context = SpringApplication.run(App.class);
            r.complete(context);
        }, (AsyncResult<ConfigurableApplicationContext> r) -> {
            if (r.succeeded()) {
                App.ctx = r.result();
                registerCodecs(r.result());
                startFuture.complete();
            } else {
                startFuture.fail(r.cause());
            }
        });
    }

    @Override
    public void stop() throws Exception {

    }

    private void registerCodecs(ConfigurableApplicationContext ctx) {
        final EventBus bus = getVertx().eventBus();
        bus.registerDefaultCodec(ArrayList.class, ctx.getBean(ArrayListToJsonArrayCodec.class));
    }

    public static String loadConfig(String file) {
        InputStream stream = MainVerticle.class.getResourceAsStream(file);
        try {
            String string = IOUtils.toString(stream, "UTF-8");
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
