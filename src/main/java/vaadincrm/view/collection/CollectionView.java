package vaadincrm.view.collection;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import io.crm.Events;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.util.VaadinUtil;

/**
 * Created by someone on 16-Aug-2015.
 */
final public class CollectionView extends Panel implements View {

    private boolean initialized = false;
    private CollectionTable collectionTable;
    private final VerticalLayout root = new VerticalLayout();

    private void initialize() {

        root.setSizeFull();
        root.setMargin(true);

        setContent(root);
        Responsive.makeResponsive(root);

        collectionTable = new CollectionTable().init();

        root.addComponent(collectionTable.getTable());

        final UI ui = UI.getCurrent();
        App.bus.send(Events.GET_COLLECTION_COUNT, null, (AsyncResult<Message<JsonObject>> r) -> {
            ui.access(() -> {
                if (r.failed()) {
                    VaadinUtil.handleError(r.cause());
                    return;
                }
                collectionTable.populateData(r.result().body());
            });
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!initialized) {
            initialize();
            initialized = true;
        }
    }
}
