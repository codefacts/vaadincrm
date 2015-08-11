package vaadincrm.view.dashboard;

import com.vaadin.event.Action;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.mc;
import vaadincrm.model.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static vaadincrm.model.Model.id;

/**
 * Created by someone on 29-Jul-2015.
 */
final public class DBTree {

    private static final Action ADD_CATEGORY_ACTION = new Action("ADD_CATEGORY_ACTION");
    private static final Action ADD_ITEM_ACTION = new Action("ADD_ITEM_ACTION");
    private static final Action REMOVE_ITEM_ACTION = new Action("REMOVE_ITEM_ACTION");
    private static final Object NAME_PROPERTY = "Name";
    private static final String ID_PROPERTY = "Id";
    private static final String CHILD_COUNT_PROPERTY = "Child Count";
    private static final String TYPE_PROPERTY = "Type";

    private TreeTable treeTable = new TreeTable();

    public TreeTable createTree() {
        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        treeTable.addContainerProperty(NAME_PROPERTY, String.class, "");
        treeTable.addContainerProperty(ID_PROPERTY, String.class, "");
        treeTable.addContainerProperty(CHILD_COUNT_PROPERTY, String.class, "");
        treeTable.addContainerProperty(TYPE_PROPERTY, String.class, "");

        retrieveAndPopulateData();

        treeTable.addActionHandler(actionHandler(treeTable));

        treeTable.addValueChangeListener(e -> Notification.show("Value changed:",
                String.valueOf(e.getProperty().getValue()),
                Notification.Type.TRAY_NOTIFICATION));
        return treeTable;
    }

    private Action.Handler actionHandler(final TreeTable treeTable) {
        return new Action.Handler() {
            @Override
            public void handleAction(final Action action, final Object sender,
                                     final Object target) {
                if (action == ADD_ITEM_ACTION) {
                    // Create new item
                    final Object item = treeTable.addItem(new Object[]{
                            "New Item", 0, new Date()}, null);
                    treeTable.setChildrenAllowed(item, false);
                    treeTable.setParent(item, target);
                } else if (action == ADD_CATEGORY_ACTION) {
                    final Object item = treeTable.addItem(new Object[]{
                            "New Category", 0, new Date()}, null);
                    treeTable.setParent(item, target);
                } else if (action == REMOVE_ITEM_ACTION) {
                    treeTable.removeItem(target);
                }
            }

            @Override
            public Action[] getActions(final Object target, final Object sender) {

                if (target == null) {
                    // Context menu in an empty space -> add a new main category
                    return new Action[]{ADD_CATEGORY_ACTION};

                } else if (treeTable.areChildrenAllowed(target)) {
                    // Context menu for a category
                    return new Action[]{ADD_CATEGORY_ACTION, ADD_ITEM_ACTION,
                            REMOVE_ITEM_ACTION};

                } else {
                    // Context menu for an item
                    return new Action[]{REMOVE_ITEM_ACTION};
                }
            }
        };
    }

    private void retrieveAndPopulateData() {
        final UI ui = UI.getCurrent();
        App.bus.send(Events.GET_DB_TREE, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.failed()) {
                throw new RuntimeException(r.cause());
            }
            ui.access(() -> populateData(r.result().body()));
        });
    }

    private void populateData(final JsonArray tree) {

        int total_area = 0, total_house = 0, total_br = 0;
        for (final Object regionObj : tree) {
            final JsonObject region = (JsonObject) regionObj;
            final Long regionId = region.getLong(id);
            final JsonArray areas = region.getJsonArray(mc.area);
            final int areaCount = areas.size();
            total_area += areaCount;
            final Object regionItemId = treeTable.addItem(item("region-" + regionId, region.getString(Region.name), "Region", areaCount <= 0 ? "" : areaCount + " Areas"), "region-" + regionId);
            treeTable.setCollapsed(regionItemId, false);

            for (final Object areaObj : areas) {
                final JsonObject area = (JsonObject) areaObj;
                final Long areaId = area.getLong(id);
                final JsonArray houses = area.getJsonArray(mc.distribution_house);
                final int houseCount = houses.size();
                total_house += houseCount;
                final Object areaItemId = treeTable.addItem(item("area-" + areaId, area.getString(Area.name), "Area", houseCount <= 0 ? "" : houseCount + " Houses"), "area-" + areaId);
                treeTable.setParent(areaItemId, regionItemId);
                treeTable.setCollapsed(areaItemId, false);


                for (final Object houseObj : houses) {
                    final JsonObject house = (JsonObject) houseObj;
                    final Long houseId = house.getLong(id);
                    final JsonArray brs = house.getJsonArray(Query.brs);
                    final int brsCount = brs.size();
                    total_br += brsCount;
                    final Object houseItemId = treeTable.addItem(item("house-" + houseId, house.getString(House.name), "House", brsCount <= 0 ? "" : brsCount + " BRS"), "house-" + houseId);
                    treeTable.setParent(houseItemId, areaItemId);

                    for (final Object brObj : brs) {
                        final JsonObject br = (JsonObject) brObj;
                        final String brId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(brId, br.getString(User.name), "BR", ""), brId);
                        treeTable.setParent(brItemId, houseItemId);
                    }
                }
            }
        }
        System.out.println("REGION: " + tree.size() + " AREA: " + total_area + " house: " + total_house + " br: " + total_br);
    }

    private Object[] item(final Object id, final String name, final String type, final Object childCount) {
        return new Object[]{name, id + "", childCount + "", type};
    }
}
