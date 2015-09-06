package vaadincrm.view.campaign;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TreeTable;
import io.crm.mc;
import io.crm.util.Touple2Boolean;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.model.*;

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
//    private static final String CHILD_COUNT_PROPERTY = "Child Count";

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
//        treeTable.addContainerProperty(CHILD_COUNT_PROPERTY, String.class, "");

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
                    try {
                        recursiveRunning = true;
                        selectAllRecursively(target, false);
                    } finally {
                        recursiveRunning = false;
                    }
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

    private void selectAllRecursively(final Object target, final boolean value) {
        final CheckBox checkbox = (CheckBox) treeTable.getItem(target).getItemProperty(NAME_PROPERTY).getValue();
        checkbox.setValue(value);

        traverseRecursivelyEagerly(target, null, (t, p) -> {
            final CheckBox cbox = (CheckBox) treeTable.getItem(t).getItemProperty(NAME_PROPERTY).getValue();
            cbox.setValue(value);
            return true;
        });
    }

    private void selectAllChilds(final Object target, final boolean value) {
        final Collection<?> children = getOrDefault(treeTable.getChildren(target), Collections.emptyList());
        if (children.size() <= 0) {
            return;
        }
        for (Object child : children) {
            if (child == null) continue;
            ((CheckBox) treeTable.getItem(child).getItemProperty(NAME_PROPERTY).getValue()).setValue(value);
        }
        final CheckBox checkbox = (CheckBox) treeTable.getItem(target).getItemProperty(NAME_PROPERTY).getValue();
        checkbox.setValue(value);
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
            final Object areaListItemId = treeTable.addItem(item(regionAreaId, "", "All Areas", areaCount), regionAreaId);
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
                final Object houseListItemId = treeTable.addItem(item(areaHouseId, "", "All Houses", area.getInteger(Query.houseCount, 0)), areaHouseId);
                treeTable.setParent(houseListItemId, areaItemId);
                treeTable.setCollapsed(houseListItemId, false);

                final String areaAcId = Query._all_area_ac_id + "-" + areaId;
                final Object acListItemId = treeTable.addItem(item(areaAcId, "", "All Area Coordinators", area.getInteger(Query.acCount, 0)), areaAcId);
                treeTable.setParent(acListItemId, areaItemId);
                treeTable.setCollapsed(acListItemId, false);

                for (final Object acObj : area.getJsonArray(Query.areaCoordinators, emptyArray)) {
                    JsonObject ac = (JsonObject) acObj;
                    final Long acId = ac.getLong(id);
                    final Object acItemId = treeTable.addItem(item(Query.ac + "-" + acId, ac.getString(User.name), ""),
                            Query.ac + "-" + acId);
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

                    final Object locationSubLink = treeTable.addItem(item(houseLocationId, "", "All Locations", locsCount), houseLocationId);
                    final Object brSubLink = treeTable.addItem(item(houseBrId, "", "All BRS", brsCount), houseBrId);
                    final Object supSubLink = treeTable.addItem(item(houseSupId, "", "All BR Supervisors", supsCount), houseSupId);

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
        if (!(idStr.startsWith(EmployeeType.br.prefix)
                || idStr.startsWith(EmployeeType.area_coordinator.prefix)
                || idStr.startsWith(EmployeeType.br_supervisor.prefix)
                || idStr.startsWith(Query.location))) {
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

        if (!(idStr.startsWith(EmployeeType.br.prefix)
                || idStr.startsWith(EmployeeType.area_coordinator.prefix)
                || idStr.startsWith(EmployeeType.br_supervisor.prefix)
                || idStr.startsWith(Query.location))) {
            checkBox.setReadOnly(true);
        }

        checkBox.addValueChangeListener(e -> {
            onCheckBoxValueChange(e, id, checkBox);
        });
        return new Object[]{checkBox, idLabel};
    }

    private void onCheckBoxValueChange(final Property.ValueChangeEvent e, final Object id, final CheckBox checkBox) {
        final Object value = e.getProperty().getValue();
        if (Boolean.TRUE.equals(value)) {
            checkAllParents((String) treeTable.getParent(id));
        } else {
            uncheckAllParentsOnEmpy((String) treeTable.getParent(id));
        }

    }

    private void checkAllParents(String parent) {
        for (; ; ) {
            if (parent == null) {
                return;
            } else /*if (!parent.toString().startsWith(Query._all))*/ {
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
                parent = getRootParent(parent);
                if (parent == null) return;
                final CheckBox checkBox = checkBoxAt(parent);

                if (isTermilan(parent)) {
                    return;
                }

                if (checkIfEmpty(getOrDefault(treeTable.getChildren(parent), Collections.EMPTY_LIST)).t1) {
                    setCheckboxValue(checkBox, false);
                } else {
//                    if (!parent.startsWith(Query._all)) {
                    setCheckboxValue(checkBox, true);
//                    }
                }
            }
        } finally {
            recursiveRunning = false;
        }
    }

    private Touple2Boolean checkIfEmpty(final Collection<Object> children) {
        boolean found = false;
        int count = children.size();
        for (final Object childObj : children) {

            final String child = (String) childObj;

            final CheckBox checkBox = checkBoxAt(childObj);
            boolean empty;

            if (isTermilan(child)) {
                final boolean checked = checkBox.getValue();
                found |= checked;
                if (checked) {
                    count--;
                }
                continue;
            }

            final Touple2Boolean tpl = checkIfEmpty(getOrDefault(treeTable.getChildren(childObj), Collections.EMPTY_LIST));

            if (tpl.t1) {
                setCheckboxValue(checkBox, false);
            } else {
//                if (!child.startsWith(Query._all)) {
                setCheckboxValue(checkBox, true);
//                }
            }
        }
        return new Touple2Boolean(!found, count <= 0);
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
