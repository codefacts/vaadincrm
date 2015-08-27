package vaadincrm.view.employee;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import vaadincrm.App;
import vaadincrm.Events;

import static com.vaadin.server.Responsive.makeResponsive;

/**
 * Created by someone on 16-Aug-2015.
 */
public class EmployeeView extends Panel implements View {
    private boolean initialized = false;
    private EmployeeTable collectionTable;
    private final VerticalLayout root = new VerticalLayout();

    private void initialize(ViewChangeListener.ViewChangeEvent event) {
        root.setSizeFull();
        root.setMargin(true);

        setContent(root);
        makeResponsive(root);

        collectionTable = new EmployeeTable(event.getParameters()).init();

        final NativeSelect userTypeSelect = collectionTable.getUserTypeSelect();

        root.addComponents(userTypeSelect, collectionTable.getTable());

        final UI ui = UI.getCurrent();
        App.bus.send(Events.FIND_ALL_EMPLOYEES, null, (AsyncResult<Message<JsonArray>> r) -> {
            System.out.println("ui: " + ui);
            ui.access(() -> collectionTable.populateData(r.result().body()));
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
