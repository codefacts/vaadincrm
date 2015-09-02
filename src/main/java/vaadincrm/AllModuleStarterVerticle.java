package vaadincrm;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by someone on 02/09/2015.
 */
public class AllModuleStarterVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        getVertx().deployVerticle(MainVerticle.class.getName());
        getVertx().deployVerticle(io.crm.core.MainVerticle.class.getName());
        getVertx().deployVerticle(io.crm.query.MainVerticle.class.getName());
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        getVertx().undeploy(MainVerticle.class.getName());
        getVertx().undeploy(io.crm.core.MainVerticle.class.getName());
        getVertx().undeploy(io.crm.query.MainVerticle.class.getName());
    }
}
