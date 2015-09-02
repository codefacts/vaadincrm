package vaadincrm.view.campaign;

import com.vaadin.event.Action;
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
    private static final String CHILD_COUNT_PROPERTY = "Child Count";

    private static final Action SELECT_ALL_RECURSIVELY = new Action("Select recursively");
    private static final Action SELECT_ALL__AREAS = new Action("Select all areas.");
    private static final Action SELECT_ALL_HOUSES = new Action("Select all houses");
    private static final Action SELECT_ALL_ACS = new Action("Select all area coordinators");
    private static final Action SELECT_ALL_BRS = new Action("Select all BRS");
    private static final Action SELECT_ALL_LOCATIONS = new Action("Select all locations");
    private static final Action SELECT_ALL_BR_SUPERVISORS = new Action("Select all Supervisors");

    private static final Action EXPAND_RECURSIVELY = new Action("Expand recursively");
    private static final Action COLLAPSE_RECURSIVELY = new Action("Collapse recursively");
    private static final Action EXPAND_CHILDS = new Action("Expand childs");
    private static final Action COLLPASE_CHILDS = new Action("Collapse childs");

    private TreeTable treeTable = new TreeTable();

    public TreeTable init() {
        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        treeTable.addContainerProperty(NAME_PROPERTY, CheckBox.class, null);
        treeTable.addContainerProperty(ID_PROPERTY, String.class, "");
        treeTable.addContainerProperty(CHILD_COUNT_PROPERTY, String.class, "");

        treeTable.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {

                if (target == null) return new Action[0];
                String targetStr = target.toString();

                if (targetStr.startsWith(Query.region)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, SELECT_ALL__AREAS, EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query.area)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, SELECT_ALL_HOUSES, SELECT_ALL_ACS, EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query.house)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, SELECT_ALL_LOCATIONS, SELECT_ALL_BRS, SELECT_ALL_BR_SUPERVISORS, EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else {
                    return new Action[0];
                }
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {

                if (action == SELECT_ALL_RECURSIVELY) {
                    selectAllRecursively(target);
                } else if (action == SELECT_ALL__AREAS) {
                    selectAllAreas(target);
                } else if (action == SELECT_ALL_HOUSES) {
                    selectAllHouses(target);
                } else if (action == SELECT_ALL_ACS) {
                    selectAllACS(target);
                } else if (action == SELECT_ALL_LOCATIONS) {
                    selectAllLocations(target);
                } else if (action == SELECT_ALL_BRS) {
                    selectAllBRS(target);
                } else if (action == SELECT_ALL_BR_SUPERVISORS) {
                    selectAllSupervisors(target);
                } else if (action == EXPAND_CHILDS) {
                    expandChilds(target);
                } else if (action == COLLPASE_CHILDS) {
                    collapseChilds(target);
                } else if (action == EXPAND_RECURSIVELY) {
                    expandRecursively(target);
                } else if (action == COLLAPSE_RECURSIVELY) {
                    collapseRecursively(target);
                }
            }
        });

        return treeTable;
    }

    private void collapseRecursively(Object target) {

    }

    private void expandRecursively(Object target) {

    }

    private void collapseChilds(Object target) {

    }

    private void expandChilds(Object target) {

    }

    private void selectAllSupervisors(Object target) {

    }

    private void selectAllBRS(Object target) {

    }

    private void selectAllLocations(Object target) {

    }

    private void selectAllACS(Object target) {

    }

    private void selectAllHouses(Object target) {

    }

    private void selectAllAreas(Object target) {

    }

    private void selectAllRecursively(Object objId) {

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

            final Object regionItemId = treeTable.addItem(item(Query.region + "-" + regionId, region.getString(Region.name), regionSummary), Query.region + "-" + regionId);
            treeTable.setCollapsed(regionItemId, false);

            final Object areaListItemId = treeTable.addItem(item("", "Areas", regionSummary), Query.region + "." + Query.areas + "-" + regionId);
            treeTable.setParent(areaListItemId, regionItemId);
            treeTable.setCollapsed(areaListItemId, false);

            for (final Object areaObj : areas) {
                final JsonObject area = (JsonObject) areaObj;
                final Long areaId = area.getLong(id);
                final JsonArray houses = orEmpty(area.getJsonArray(mc.distribution_houses.name()));

                final String areaSummary = areaCount <= 0 ? "" : String.format("House: %d Location: %d, BR: %d",
                        area.getInteger(Query.houseCount),
                        area.getInteger(Query.locationCount), area.getInteger(Query.brCount));

                final Object areaItemId = treeTable.addItem(item(Query.area + "-" + areaId, area.getString(Area.name), areaSummary), Query.area + "-" + areaId);
                treeTable.setParent(areaItemId, areaListItemId);
                treeTable.setCollapsed(areaItemId, false);

                final Object houseListItemId = treeTable.addItem(item("", "All Houses", areaSummary), Query.area + "." + Query.houses + "-" + areaId);
                treeTable.setParent(houseListItemId, areaItemId);
                treeTable.setCollapsed(houseListItemId, false);

                final Object acListItemId = treeTable.addItem(item("", "All Area Coordinators", ""), Query.area + "." + Query.acs + "-" + areaId);
                treeTable.setParent(acListItemId, areaItemId);
                treeTable.setCollapsed(acListItemId, false);

                for (Object acObj : area.getJsonArray(Query.areaCoordinators, new JsonArray())) {
                    JsonObject ac = (JsonObject) acObj;
                    final Long acId = ac.getLong(id);

                    final Object locItemId = treeTable.addItem(item(Query.ac + "-" + acId, ac.getString(User.name), ""), Query.ac + "-" + acId);
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

                    final Object houseItemId = treeTable.addItem(item(Query.house + "-" + houseId, house.getString(House.name), (brsCount <= 0 && locsCount <= 0) ? "" : "BR: " + brsCount + ", Sups: " + supsCount + ", Location: " + locsCount), Query.house + "-" + houseId);
                    treeTable.setParent(houseItemId, houseListItemId);

                    final Object locationSubLink = treeTable.addItem(item("", "All Locations", ""), Query.house + "." + Query.locations + "-" + houseId);
                    final Object brSubLink = treeTable.addItem(item("", "All BRS", ""), Query.house + "." + Query.brs + "-" + houseId);
                    final Object supSubLink = treeTable.addItem(item("", "All BR Supervisors", ""), Query.house + "." + Query.brSupervisors + "-" + houseId);

                    treeTable.setParent(locationSubLink, houseItemId);
                    treeTable.setParent(brSubLink, houseItemId);
                    treeTable.setParent(supSubLink, houseItemId);

                    for (final Object locObj : locations) {
                        final JsonObject loc = (JsonObject) locObj;
                        final Long locId = loc.getLong(id);

                        final Object locItemId = treeTable.addItem(item(Query.location + "-" + locId, loc.getString(User.name), ""), Query.location + "-" + locId);
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
                        final String supId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(supId, br.getString(User.name), ""), supId);
                        treeTable.setParent(brItemId, brSubLink);
                    }
                }
            }
        }
        System.out.println("TREE WITH USERS >> REGION: " + tree.size() + " AREA: " + tree.getInteger(Query.areaCount) + " house: " + tree.getInteger(Query.houseCount) + " loc: " + tree.getInteger(Query.locationCount) + " ac: " + tree.getInteger(Query.acCount) + " sup: " + tree.getInteger(Query.supCount) + " br: " + tree.getInteger(Query.brCount));
    }

    private JsonArray orEmpty(JsonArray jsonArray) {
        return (jsonArray == null) ? new JsonArray() : jsonArray;
    }

    private Object[] item(final Object id, final String name, final Object childCount) {
        return new Object[]{new CheckBox(name), id + "", childCount};
    }
}
