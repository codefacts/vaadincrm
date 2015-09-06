package vaadincrm.view.campaign;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.TreeTable;
import io.crm.mc;
import io.crm.util.Touple2Boolean;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import static vaadincrm.model.Model.id;
import static io.crm.util.Util.getOrDefault;

/**
 * Created by someone on 01/09/2015.
 */
final public class ConfigureCampaignTree {
    private static final Object NAME_PROPERTY = "Name";
    private static final String ID_PROPERTY = "Id";

    private static final Action SELECT_ALL_RECURSIVELY = new Action("Select recursively");
    private static final Action UNSELECT_ALL_RECURSIVELY = new Action("Unselect recursively");

    private static final Action EXPAND_RECURSIVELY = new Action("Expand recursively");
    private static final Action COLLAPSE_RECURSIVELY = new Action("Collapse recursively");
    private static final Action EXPAND_CHILDS = new Action("Expand childs");
    private static final Action COLLPASE_CHILDS = new Action("Collapse childs");

    private boolean recursiveRunning = false;

    private TreeTable treeTable = new TreeTable();

    public TreeTable init() {
        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        treeTable.addContainerProperty(NAME_PROPERTY, CheckBox.class, null);
        treeTable.addContainerProperty(ID_PROPERTY, String.class, "");

        treeTable.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {

                if (target == null) return new Action[0];
                String targetStr = target.toString();

                if (targetStr.startsWith(Query.region)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_region_area_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};

                } else if (targetStr.startsWith(Query.area)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_area_ac_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_area_house_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};


                } else if (targetStr.startsWith(Query.house)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_house_br_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_house_location_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};
                } else if (targetStr.startsWith(Query._all_house_sup_id)) {
                    return new Action[]{SELECT_ALL_RECURSIVELY, UNSELECT_ALL_RECURSIVELY,
                            EXPAND_CHILDS, COLLPASE_CHILDS, EXPAND_RECURSIVELY, COLLAPSE_RECURSIVELY};


                } else {
                    return new Action[0];
                }
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {

                if (action == SELECT_ALL_RECURSIVELY) {
                    selectAllRecursively(target, true);
                } else if (action == UNSELECT_ALL_RECURSIVELY) {
                    selectAllRecursively(target, false);
                } else if (action == EXPAND_CHILDS) {
                    collapseChilds(target, false);
                } else if (action == COLLPASE_CHILDS) {
                    collapseChilds(target, true);
                } else if (action == EXPAND_RECURSIVELY) {
                    collapseRecursively(target, false);
                } else if (action == COLLAPSE_RECURSIVELY) {
                    collapseRecursively(target, true);
                }
            }
        });

        return treeTable;
    }

    public JsonObject getTree() {
        final JsonObject root = new JsonObject();
        for (Object region : getOrDefault(treeTable.rootItemIds(), Collections.EMPTY_LIST)) {
            if (!checkBoxAt(region).getValue()) continue;

            Long regionId = getId((String) region, Query.region);
            final JsonObject regionJson = new JsonObject()
                    .put(Query.id, regionId);

            final JsonObject areaListJson = new JsonObject();
            getOrDefault(treeTable.getChildren(region), Collections.EMPTY_LIST).forEach(areaList -> {

                for (Object area : getOrDefault(treeTable.getChildren(areaList), Collections.EMPTY_LIST)) {
                    if (!checkBoxAt(area).getValue()) continue;

                    final Long areaId = getId((String) area, Query.area);
                    final JsonObject areaJson = new JsonObject().put(Query.id, areaId);

                    final JsonObject houseListJson = new JsonObject();
                    final JsonObject acListJson = new JsonObject();
                    getOrDefault(treeTable.getChildren(area), Collections.EMPTY_LIST).forEach(houseAcList -> {

                        if (houseAcList.toString().startsWith(Query._all_area_house_id)) {

                            for (Object house : getOrDefault(treeTable.getChildren(houseAcList), Collections.EMPTY_LIST)) {
                                if (!checkBoxAt(house).getValue()) continue;

                                final Long houseId = getId((String) house, Query.house);
                                final JsonObject houseJson = new JsonObject().put(Query.id, houseId);

                                final JsonObject brListJson = new JsonObject();
                                final JsonObject locationListJson = new JsonObject();
                                final JsonObject supListJson = new JsonObject();

                                getOrDefault(treeTable.getChildren(house), Collections.EMPTY_LIST).forEach(brLocSupList -> {

                                    if (brLocSupList.toString().startsWith(Query._all_house_br_id)) {
                                        for (Object brId : getOrDefault(treeTable.getChildren(brLocSupList), Collections.EMPTY_LIST)) {
                                            if (!checkBoxAt(brId).getValue()) continue;
                                            final JsonObject brJson = new JsonObject().put(Query.id, brId);
                                            brListJson.put(brId.toString(), brJson);
                                        }
                                    }

                                    if (brLocSupList.toString().startsWith(Query._all_house_location_id)) {
                                        for (Object location : getOrDefault(treeTable.getChildren(brLocSupList), Collections.EMPTY_LIST)) {
                                            if (!checkBoxAt(location).getValue()) continue;
                                            final Long locId = getId(location.toString(), Query.location);
                                            final JsonObject locJson = new JsonObject().put(Query.id, locId);
                                            locationListJson.put(locId.toString(), locJson);
                                        }
                                    }

                                    if (brLocSupList.toString().startsWith(Query._all_house_sup_id)) {
                                        for (Object supId : getOrDefault(treeTable.getChildren(brLocSupList), Collections.EMPTY_LIST)) {
                                            if (!checkBoxAt(supId).getValue()) continue;
                                            final JsonObject supJson = new JsonObject().put(Query.id, supId);
                                            supListJson.put(supId.toString(), supJson);
                                        }
                                    }
                                });

                                houseJson.put(Query.brSupervisors, supListJson);
                                houseJson.put(mc.locations.name(), locationListJson);
                                houseJson.put(Query.brs, brListJson);

                                houseListJson.put(houseId.toString(), houseJson);
                            }
                        } else if (houseAcList.toString().startsWith(Query._all_area_ac_id)) {

                            for (Object acId : getOrDefault(treeTable.getChildren(houseAcList), Collections.EMPTY_LIST)) {
                                if (!checkBoxAt(acId).getValue()) continue;

                                final JsonObject acJson = new JsonObject().put(Query.id, acId);
                                acListJson.put(acId.toString(), acJson);
                            }
                        }
                    });

                    areaJson.put(mc.distribution_houses.name(), houseListJson);
                    areaJson.put(Query.areaCoordinators, acListJson);

                    areaListJson.put(areaId.toString(), areaJson);
                }
            });

            regionJson.put(mc.areas.name(), areaListJson);
            root.put(regionId.toString(), regionJson);
        }
        return root;
    }

    private Long getId(final String idStr, final String prefix) {
        return Long.parseLong(idStr.substring(prefix.length() + 1));
    }

    private void selectAllRecursively(final Object target, final boolean value) {
        if (!recursiveRunning) {
            try {
                recursiveRunning = true;
                final CheckBox checkbox = checkBoxAt(target);
                setCheckboxValue(checkbox, value);

                traverseRecursivelyEagerly(target, null, (t, p) -> {
                    final CheckBox cbox = (CheckBox) treeTable.getItem(t).getItemProperty(NAME_PROPERTY).getValue();
                    setCheckboxValue(cbox, value);
                    return true;
                });
            } finally {
                recursiveRunning = false;
            }
        }

        onCheckBoxValueChange(target, value);
    }

    private void collapseRecursively(Object target, boolean value) {
        treeTable.setCollapsed(target, value);
        traverseRecursivelyEagerly(target, null, (t, p) -> {
            treeTable.setCollapsed(t, value);
            return true;
        });
    }

    private void collapseChilds(Object target, boolean value) {
        final Collection<?> children = getOrDefault(treeTable.getChildren(target), Collections.emptyList());
        if (children.size() <= 0) {
            return;
        }
        for (Object child : children) {
            if (child == null) continue;
            treeTable.setCollapsed(child, value);
        }
    }

    public void populateData(final JsonObject tree) {

        final JsonArray emptyArray = new JsonArray();
        for (final Object regionObj : tree.getJsonArray(Query.regions, emptyArray)) {
            final JsonObject region = (JsonObject) regionObj;
            final Long regionId = region.getLong(id);
            JsonArray areas = region.getJsonArray(mc.areas.name(), emptyArray);
            final int areaCount = areas.size();

            final Object regionItemId = treeTable.addItem(item(Query.region + "-" + regionId, region.getString(Region.name)),
                    Query.region + "-" + regionId);
            treeTable.setCollapsed(regionItemId, false);

            final String regionAreaId = Query._all_region_area_id + "-" + regionId;
            final Object areaListItemId = treeTable.addItem(item(regionAreaId, "", "Areas", areaCount), regionAreaId);
            treeTable.setParent(areaListItemId, regionItemId);
            treeTable.setCollapsed(areaListItemId, false);

            for (final Object areaObj : areas) {
                final JsonObject area = (JsonObject) areaObj;
                final Long areaId = area.getLong(id);
                final JsonArray houses = area.getJsonArray(mc.distribution_houses.name(), emptyArray);

                final Object areaItemId = treeTable.addItem(item(Query.area + "-" + areaId, area.getString(Area.name)), Query.area + "-" + areaId);
                treeTable.setParent(areaItemId, areaListItemId);
                treeTable.setCollapsed(areaItemId, false);

                final String areaHouseId = Query._all_area_house_id + "-" + areaId;
                final Object houseListItemId = treeTable.addItem(item(areaHouseId, "", "Houses", area.getInteger(Query.houseCount, 0)), areaHouseId);
                treeTable.setParent(houseListItemId, areaItemId);
                treeTable.setCollapsed(houseListItemId, false);

                final String areaAcId = Query._all_area_ac_id + "-" + areaId;
                final Object acListItemId = treeTable.addItem(item(areaAcId, "", "Area Coordinators", area.getInteger(Query.acCount, 0)), areaAcId);
                treeTable.setParent(acListItemId, areaItemId);
                treeTable.setCollapsed(acListItemId, false);

                for (final Object acObj : area.getJsonArray(Query.areaCoordinators, emptyArray)) {
                    JsonObject ac = (JsonObject) acObj;
                    final String acId = ac.getString(User.userId);
                    final Object acItemId = treeTable.addItem(item(acId, ac.getString(User.name), ""),
                            acId);
                    treeTable.setParent(acItemId, acListItemId);
                }

                for (final Object houseObj : houses) {
                    final JsonObject house = (JsonObject) houseObj;
                    final Long houseId = house.getLong(id);
                    final JsonArray brs = house.getJsonArray(Query.brs, emptyArray);
                    final JsonArray locations = house.getJsonArray(Query.locations, emptyArray);
                    final JsonArray supervisors = house.getJsonArray(Query.brSupervisors, emptyArray);
                    final int brsCount = brs.size();
                    final int locsCount = locations.size();
                    final int supsCount = supervisors.size();

                    final Object houseItemId = treeTable.addItem(item(Query.house + "-" + houseId, house.getString(House.name), String.format("BRS: %d, Loc: %d, Sups: %d", brsCount, locsCount, supsCount)),
                            Query.house + "-" + houseId);
                    treeTable.setParent(houseItemId, houseListItemId);

                    final String houseLocationId = Query._all_house_location_id + "-" + houseId;
                    final String houseBrId = Query._all_house_br_id + "-" + houseId;
                    final String houseSupId = Query._all_house_sup_id + "-" + houseId;

                    final Object locationSubLink = treeTable.addItem(item(houseLocationId, "", "Locations", locsCount), houseLocationId);
                    final Object brSubLink = treeTable.addItem(item(houseBrId, "", "BRS", brsCount), houseBrId);
                    final Object supSubLink = treeTable.addItem(item(houseSupId, "", "BR Supervisors", supsCount), houseSupId);

                    treeTable.setParent(locationSubLink, houseItemId);
                    treeTable.setParent(brSubLink, houseItemId);
                    treeTable.setParent(supSubLink, houseItemId);

                    for (final Object locObj : locations) {
                        final JsonObject loc = (JsonObject) locObj;
                        final Long locId = loc.getLong(id);

                        final Object locItemId = treeTable.addItem(item(Query.location + "-" + locId,
                                        loc.getString(User.name)),
                                Query.location + "-" + locId);
                        treeTable.setParent(locItemId, locationSubLink);
                    }

                    for (final Object brObj : brs) {
                        final JsonObject br = (JsonObject) brObj;
                        final String brId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(brId, br.getString(User.name)), brId);
                        treeTable.setParent(brItemId, brSubLink);
                    }

                    for (final Object brObj : supervisors) {
                        final JsonObject br = (JsonObject) brObj;
                        final String supId = br.getString(User.userId);

                        final Object brItemId = treeTable.addItem(item(supId, br.getString(User.name)), supId);
                        treeTable.setParent(brItemId, brSubLink);
                    }
                }
            }
        }
        System.out.println("TREE WITH USERS >> REGION: " + tree.size() + " AREA: " + tree.getInteger(Query.areaCount) + " house: " + tree.getInteger(Query.houseCount) + " loc: " + tree.getInteger(Query.locationCount) + " ac: " + tree.getInteger(Query.acCount) + " sup: " + tree.getInteger(Query.supCount) + " br: " + tree.getInteger(Query.brCount));
    }

    private Object[] item(final Object id, final String name) {
        final CheckBox checkBox = new CheckBox(name);

        final String idStr = getOrDefault(id.toString(), "");
        if (!(isTermilan(idStr))) {
            checkBox.setReadOnly(true);
        }

        checkBox.addValueChangeListener(e -> {
            onCheckBoxValueChange(e, id, checkBox);
        });
        return new Object[]{checkBox, id};
    }

    private Object[] item(final Object id, final String name, final Object childCount) {
        return item(id, id, name, childCount);
    }

    private Object[] item(final Object id, final Object idLabel, final String name, final Object childCount) {
        final String idStr = getOrDefault(id.toString(), "");
        final String des = idStr.startsWith(Query.house) ? "    (" + childCount + ")" : " (" + childCount + ")";
        final CheckBox checkBox = new CheckBox(name + des);

        if (!(isTermilan(idStr))) {
            checkBox.setReadOnly(true);
        }

        checkBox.addValueChangeListener(e -> {
            onCheckBoxValueChange(e, id, checkBox);
        });
        return new Object[]{checkBox, idLabel};
    }

    private void onCheckBoxValueChange(final Property.ValueChangeEvent e, final Object target, final CheckBox checkBox) {
        if (!recursiveRunning) {
            onCheckBoxValueChange(target, (Boolean) e.getProperty().getValue());
        }
    }

    private void onCheckBoxValueChange(Object target, boolean value) {
        final String parent = (String) treeTable.getParent(target);
        if (value) {
            checkAllParents(parent);
        } else {
            uncheckAllParentsOnEmpy(parent);
        }
    }

    private void checkAllParents(String parent) {
        for (; ; ) {
            if (parent == null) {
                return;
            } else {
                final CheckBox parentCheckbox = (CheckBox) treeTable.getItem(parent).getItemProperty(NAME_PROPERTY).getValue();
                setCheckboxValue(parentCheckbox, true);
            }
            parent = (String) treeTable.getParent(parent);
        }
    }

    private void uncheckAllParentsOnEmpy(String parent) {
        try {
            if (!recursiveRunning) {
                recursiveRunning = true;
                for (; ; ) {
                    if (parent == null) return;
                    final boolean val = !checkIfEmpty(treeTable.getChildren(parent));
                    final CheckBox checkBox = checkBoxAt(parent);
                    setCheckboxValue(checkBox, val);
                    if (val) {
                        return;
                    }
                    parent = (String) treeTable.getParent(parent);
                }
            }
        } finally {
            recursiveRunning = false;
        }
    }

    private boolean checkIfEmpty(final Collection<?> children) {
        for (final Object childObj : children) {
            final CheckBox checkBox = checkBoxAt(childObj);
            final boolean checked = checkBox.getValue();
            if (checked) {
                return false;
            }
        }
        return true;
    }

    private final boolean isTermilan(final String childObj) {
        return childObj.startsWith(EmployeeType.br.prefix)
                || childObj.startsWith(EmployeeType.area_coordinator.prefix)
                || childObj.startsWith(EmployeeType.br_supervisor.prefix)
                || childObj.startsWith(Query.location);
    }

    private CheckBox checkBoxAt(final Object parent) {
        return (CheckBox) treeTable.getItem(parent).getItemProperty(NAME_PROPERTY).getValue();
    }

    private String getRootParent(String parent) {
        String prev = null;
        for (; ; ) {
            if (parent == null) {
                return prev;
            }
            prev = parent;
            parent = (String) treeTable.getParent(parent);
        }
    }

    private void setCheckboxValue(final CheckBox checkBox, final boolean value) {
        boolean v = checkBox.isReadOnly();
        try {
            checkBox.setReadOnly(false);
            checkBox.setValue(value);
        } finally {
            checkBox.setReadOnly(v);
        }
    }

    private void traverseRecursivelyEagerly(final Object target, final Object parent, final BiFunction<Object, Object, Boolean> traverser) {
        final Collection<?> children = getOrDefault(treeTable.getChildren(target), Collections.emptyList());
        if (children.size() <= 0) {
            return;
        }
        for (final Object child : children.toArray()) {
            if (!traverser.apply(child, target)) return;
            traverseRecursivelyEagerly(child, target, traverser);
        }
    }

    private void traverseRecursivelyLazy(final Object target, final Object parent, final BiFunction<Object, Object, Boolean> traverser) {
        final Collection<?> children = getOrDefault(treeTable.getChildren(target), Collections.emptyList());
        if (children.size() <= 0) {
            return;
        }
        for (final Object child : children.toArray()) {
            traverseRecursivelyLazy(child, target, traverser);
            if (!traverser.apply(child, target)) return;
        }
    }
}
