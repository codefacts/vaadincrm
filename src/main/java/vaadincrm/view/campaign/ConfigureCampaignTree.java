package vaadincrm.view.campaign;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TreeTable;
import io.crm.mc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.model.*;

import static vaadincrm.model.Model.id;

/**
 * Created by someone on 01/09/2015.
 */
public class ConfigureCampaignTree {
    private static final Object NAME_PROPERTY = "Name";
    private static final String ID_PROPERTY = "Id";
//    private static final String CHILD_COUNT_PROPERTY = "Child Count";

    private TreeTable treeTable = new TreeTable();

    public TreeTable init() {
        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        treeTable.addContainerProperty(NAME_PROPERTY, CheckBox.class, null);
        treeTable.addContainerProperty(ID_PROPERTY, String.class, "");
//        treeTable.addContainerProperty(CHILD_COUNT_PROPERTY, String.class, "");

        return treeTable;
    }

    public void populateData(final JsonObject tree) {

        for (final Object regionObj : orEmpty(tree.getJsonArray(Query.regions))) {
            final JsonObject region = (JsonObject) regionObj;
            final Long regionId = region.getLong(id);
            JsonArray areas = orEmpty(region.getJsonArray(mc.areas.name()));
            final int areaCount = areas.size();

            final String regionSummary = areaCount <= 0 ? "" : String.format("Area: %d House: %d\nLocation: %d, BR: %d",
                    region.getInteger(Query.areaCount), region.getInteger(Query.houseCount),
                    region.getInteger(Query.locationCount), region.getInteger(Query.brCount));

            final Object regionItemId = treeTable.addItem(item("region-" + regionId, region.getString(Region.name), regionSummary), "region-" + regionId);
            treeTable.setCollapsed(regionItemId, false);

            final Object areaListItemId = treeTable.addItem(item("", "Areas", regionSummary), null);
            treeTable.setParent(areaListItemId, regionItemId);
            treeTable.setCollapsed(areaListItemId, false);

            for (final Object areaObj : areas) {
                final JsonObject area = (JsonObject) areaObj;
                final Long areaId = area.getLong(id);
                final JsonArray houses = orEmpty(area.getJsonArray(mc.distribution_houses.name()));

                final String areaSummary = areaCount <= 0 ? "" : String.format("House: %d Location: %d, BR: %d",
                        area.getInteger(Query.houseCount),
                        area.getInteger(Query.locationCount), area.getInteger(Query.brCount));

                final Object areaItemId = treeTable.addItem(item("area-" + areaId, area.getString(Area.name), areaSummary), "area-" + areaId);
                treeTable.setParent(areaItemId, areaListItemId);
                treeTable.setCollapsed(areaItemId, false);

                final Object houseListItemId = treeTable.addItem(item("", "Houses", areaSummary), null);
                treeTable.setParent(houseListItemId, areaItemId);
                treeTable.setCollapsed(houseListItemId, false);

                final Object acListItemId = treeTable.addItem(item("", "Area Coordinators", ""), null);
                treeTable.setParent(acListItemId, areaItemId);
                treeTable.setCollapsed(acListItemId, false);

                for (Object acObj : area.getJsonArray(Query.areaCoordinators, new JsonArray())) {
                    JsonObject ac = (JsonObject) acObj;
                    final Long acId = ac.getLong(id);

                    final Object locItemId = treeTable.addItem(item("ac-" + acId, ac.getString(User.name), ""), "loc-" + acId);
                    treeTable.setParent(locItemId, acListItemId);
                }

                for (final Object houseObj : houses) {
                    final JsonObject house = (JsonObject) houseObj;
                    final Long houseId = house.getLong(id);
                    final JsonArray brs = orEmpty(house.getJsonArray(Query.brs));
                    final JsonArray locations = orEmpty(house.getJsonArray(Query.locations));
                    final JsonArray supervisors = orEmpty(house.getJsonArray(Query.brSupervisors));
                    final int brsCount = brs.size();
                    final int locsCount = locations.size();
                    final int supsCount = supervisors.size();

                    final Object houseItemId = treeTable.addItem(item("house-" + houseId, house.getString(House.name), (brsCount <= 0 && locsCount <= 0) ? "" : "BR: " + brsCount + ", Sups: " + supsCount + ", Location: " + locsCount), "house-" + houseId);
                    treeTable.setParent(houseItemId, houseListItemId);

                    final Object locationSubLink = treeTable.addItem(item("", "Locations", ""), null);
                    final Object brSubLink = treeTable.addItem(item("", "BRS", ""), null);
                    final Object supSubLink = treeTable.addItem(item("", "BR Supervisors", ""), null);

                    treeTable.setParent(locationSubLink, houseItemId);
                    treeTable.setParent(brSubLink, houseItemId);
                    treeTable.setParent(supSubLink, houseItemId);

                    for (final Object locObj : locations) {
                        final JsonObject loc = (JsonObject) locObj;
                        final Long locId = loc.getLong(id);

                        final Object locItemId = treeTable.addItem(item("loc-" + locId, loc.getString(User.name), ""), "loc-" + locId);
                        treeTable.setParent(locItemId, locationSubLink);
                    }

                    for (final Object brObj : brs) {
                        final JsonObject br = (JsonObject) brObj;
                        final String brId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(brId, br.getString(User.name), ""), brId);
                        treeTable.setParent(brItemId, brSubLink);
                    }

                    for (final Object brObj : supervisors) {
                        final JsonObject br = (JsonObject) brObj;
                        final String brId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(brId, br.getString(User.name), ""), brId);
                        treeTable.setParent(brItemId, brSubLink);
                    }
                }
            }
        }
        System.out.println("REGION: " + tree.size() + " AREA: " + tree.getInteger(Query.areaCount) + " house: " + tree.getInteger(Query.houseCount) + " loc: " + tree.getInteger(Query.locationCount) + " ac: " + tree.getInteger(Query.acCount) + " sup: " + tree.getInteger(Query.supCount) + " br: " + tree.getInteger(Query.brCount));
    }

    private JsonArray orEmpty(JsonArray jsonArray) {
        return (jsonArray == null) ? new JsonArray() : jsonArray;
    }

    private Object[] item(final Object id, final String name, final Object childCount) {
        return new Object[]{new CheckBox(name), id + ""};
    }
}
