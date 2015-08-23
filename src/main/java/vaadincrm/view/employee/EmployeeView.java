package vaadincrm.view.employee;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.util.ExceptionUtil;
import vaadincrm.view.region.RegionTable;

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

        root.addComponent(collectionTable.getTable());

        final UI ui = UI.getCurrent();
        App.bus.send(Events.FIND_ALL_EMPLOYEES, null, ExceptionUtil.handle((Message<JsonArray> v) -> {
            System.out.println("ui: " + ui);
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
