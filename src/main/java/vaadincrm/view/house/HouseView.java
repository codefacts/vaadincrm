package vaadincrm.view.house;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.util.ExceptionUtil;
import vaadincrm.view.area.AreaTable;
import vaadincrm.view.collection.CollectionTable;

import static com.vaadin.server.Responsive.makeResponsive;
import static vaadincrm.util.ExceptionUtil.handle;

/**
 * Created by someone on 16-Aug-2015.
 */
public class HouseView extends Panel implements View {
    private static final String REQUEST_MESSAGE = Events.FIND_ALL_HOUSES;
    private boolean initialized = false;
    private HouseTable collectionTable;
    private final VerticalLayout root = new VerticalLayout();

    private void initialize(ViewChangeListener.ViewChangeEvent event) {
        root.setSizeFull();
        root.setMargin(true);

        setContent(root);
        makeResponsive(root);

        collectionTable = new HouseTable(event.getParameters()).init();

        root.addComponent(collectionTable.getTable());

        final UI ui = UI.getCurrent();
        App.bus.send(REQUEST_MESSAGE, null, handle((Message<JsonArray> v) -> {
            ui.access(() -> collectionTable.populateData(v.body()));
        }));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!initialized) {
            initialize(event);
            initialized = true;
        }
    }
}