package vaadincrm.view.brand;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.Resp;
import vaadincrm.util.VaadinUtil;

import static com.vaadin.server.Responsive.makeResponsive;

/**
 * Created by someone on 16-Aug-2015.
 */
public class BrandView extends Panel implements View {
    private boolean initialized = false;
    private BrandTable collectionTable;
    private final VerticalLayout root = new VerticalLayout();

    private void initialize(ViewChangeListener.ViewChangeEvent event) {
        root.setSizeFull();
        root.setMargin(true);

        setContent(root);
        makeResponsive(root);

        collectionTable = new BrandTable(event.getParameters()).init();

        root.addComponent(collectionTable.getTable());

        final UI ui = UI.getCurrent();
        App.bus.send(Events.FIND_ALL_BRANDS, null, (AsyncResult<Message<JsonArray>> r) -> {
            System.out.println("ui: " + ui);
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
            initialize(event);
            initialized = true;
        }
    }
}
