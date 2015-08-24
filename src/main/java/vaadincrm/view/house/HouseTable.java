package vaadincrm.view.house;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import io.crm.FailureCode;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.Resp;
import vaadincrm.model.House;
import vaadincrm.model.Query;
import vaadincrm.util.FutureResult;
import vaadincrm.util.Util;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static vaadincrm.Resp.*;
import static vaadincrm.model.Model.id;
import static vaadincrm.util.ExceptionUtil.toRuntime;
import static vaadincrm.util.Util.asMap;
import static vaadincrm.util.Util.errorMessage;
import static vaadincrm.util.Util.nullToEmpty;

public class HouseTable {
    public static final String collection = "Distribution House";
    public static final String GET_PARENT_REQUEST = Events.FIND_ALL_AREAS;

    private static final String ID_PROPERTY = "Id";
    private static final Object NAME_PROPERTY = "Name";
    private static final String AREA_PROPERTY = "Area";
    private static final String REGION_PROPERTY = "Region";
    private static final String LOCATION_PROPERTY = "Location";

    private static final String UPDATE_REQUEST = Events.UPDATE_HOUSE;
    private static final String CREATE_REQUEST = Events.CREATE_HOUSE;
    private static final String PARENT_FIELD = Query.area;
    private static final String PARENT_LABEL = Resp.Area;
    private static final String PARENT_ID_FIELD = Query.areaId;

    private static final Action ADD_ITEM_ACTION = new Action("Add new " + collection);
    private static final Action EDIT_ITEM_ACTION = new Action("Edit this " + collection);
    private static final Action VIEW_ITEM_ACTION = new Action("View this " + collection);

    private final float VIEW_WINDOW_WIDTH = 600.0f;
    private final float VIEW_WINDOW_HEIGHT = 400.0f;
    private final float EDIT_WINDOW_WIDTH = 600.0f;
    private final float EDIT_WINDOW_HEIGHT = 400.0f;

    private String params;

    private final Table table = new Table();
    private final Map<Long, JsonObject> dataMap = new HashMap<>();
    private Map<Long, JsonObject> areaMap = new HashMap<>();
    private Map<Long, JsonObject> regionMap = new HashMap<>();
    private volatile boolean ignoreSelectionChange = false;

    public HouseTable(String params) {
        this.params = params;
        System.out.println(String.format("params: %s", params));
    }

    public HouseTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(ID_PROPERTY, Long.class, 0);
        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(AREA_PROPERTY, Link.class, "");
        table.addContainerProperty(REGION_PROPERTY, Link.class, "");
        table.addContainerProperty(LOCATION_PROPERTY, Link.class, "");

        table.addActionHandler(actionHandler());

        return this;
    }

    private Action.Handler actionHandler() {
        return new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
                if (target == null) {
                    return new Action[]{ADD_ITEM_ACTION};
                } else {
                    return new Action[]{VIEW_ITEM_ACTION, EDIT_ITEM_ACTION, ADD_ITEM_ACTION};
                }
            }

            @Override
            public void handleAction(final Action action, final Object sender, final Object target) {
                if (action == ADD_ITEM_ACTION) {
                    toRuntime(() -> addItemForm());
                } else if (action == EDIT_ITEM_ACTION) {
                    toRuntime(() -> editItemForm(dataMap.get(target)));
                } else if (action == VIEW_ITEM_ACTION) {
                    viewItemForm(dataMap.get(target));
                }
            }
        };
    }

    private void viewItemForm(final JsonObject house) {
        final Window window = new Window(collection + " Details");
        window.setWidth(VIEW_WINDOW_WIDTH, PIXELS);
        window.setHeight(VIEW_WINDOW_HEIGHT, PIXELS);
        window.center();
        final VerticalLayout content = new VerticalLayout();
        window.setContent(content);

        content.addStyleName("outlined");
        content.setSizeFull();
        content.setSpacing(true);
        content.setMargin(true);

        final JsonObject area = house.getJsonObject(PARENT_FIELD, new JsonObject());
        content.addComponents(
                addDetailsField("ID", house.getLong(Query.id)),
                addDetailsField("Name", house.getString(Query.name)),
                addDetailsFieldWithLink(PARENT_LABEL, area
                        .getString(Query.name)),
                addDetailsFieldWithLink("Region", area
                        .getJsonObject(Query.region, new JsonObject()).getString(Query.name)));

        UI.getCurrent().addWindow(window);
    }

    private Component addDetailsField(String caption, Object value) {
        final HorizontalLayout content = new HorizontalLayout();
        final Label label = new Label(caption);
        final Label valueLabel = new Label(value + "");
        content.addComponents(label, valueLabel);

        content.setSpacing(true);
        return content;
    }

    private Component addDetailsFieldWithLink(String caption, Object value) {
        final HorizontalLayout content = new HorizontalLayout();
        final Label label = new Label(caption);
        final Link valueLabel = new Link(value + "", new ExternalResource(""));
        content.addComponents(label, valueLabel);

        content.setSpacing(true);
        return content;
    }

    private void editItemForm(final JsonObject house) throws ExecutionException, InterruptedException {
        final Window window = new Window("Edit " + collection);
        window.setWidth(EDIT_WINDOW_WIDTH, PIXELS);
        window.setHeight(EDIT_WINDOW_HEIGHT, PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final TextField nameField = new TextField("Name", house.getString(Query.name));
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final NativeSelect parentSelect = new NativeSelect(PARENT_LABEL);
        parentSelect.setRequired(true);
        parentSelect.setNullSelectionAllowed(false);

        final Long Zero = 0L;
        parentSelect.addItem(Zero);
        parentSelect.setItemCaption(Zero, "Select " + PARENT_LABEL);
        areaMap = findAllParent(parentSelect, GET_PARENT_REQUEST);
        parentSelect.setValue(house.getJsonObject(PARENT_FIELD).getLong(id));
        form.addComponent(parentSelect);

        final NativeSelect regionSelect = new NativeSelect("Region");
        regionSelect.setRequired(true);
        regionSelect.setNullSelectionAllowed(false);

        regionSelect.addItem(Zero);
        regionSelect.setItemCaption(Zero, "Select Region");
        regionMap = findAllParent(regionSelect, Events.FIND_ALL_REGIONS);
        regionSelect.setValue(house.getJsonObject(PARENT_FIELD, new JsonObject()).getJsonObject(Query.region).getLong(id));
        form.addComponent(regionSelect);

        onAreaRegionSelection(parentSelect, regionSelect);

        final Button updateButton = new Button("Update");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            parentSelect.setComponentError(null);
            App.bus.send(UPDATE_REQUEST, new JsonObject().put(id, house.getLong(id))
                    .put(Query.name, nameField.getValue())
                    .put(PARENT_FIELD, parentSelect.getValue()), respond(ui, window, nameField, parentSelect, collection + _updated_successfully));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void addItemForm() throws ExecutionException, InterruptedException {
        final Window window = new Window("Create " + collection);
        window.setWidth(EDIT_WINDOW_WIDTH, PIXELS);
        window.setHeight(EDIT_WINDOW_HEIGHT, PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final TextField nameField = new TextField("Name", "");
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final NativeSelect parentSelect = new NativeSelect(PARENT_LABEL);
        parentSelect.setRequired(true);
        parentSelect.setNullSelectionAllowed(false);

        final Long Zero = 0L;
        parentSelect.addItem(Zero);
        parentSelect.setItemCaption(Zero, "Select " + PARENT_LABEL);
        areaMap = findAllParent(parentSelect, GET_PARENT_REQUEST);
        parentSelect.setValue(Zero);
        form.addComponent(parentSelect);

        final NativeSelect regionSelect = new NativeSelect("Region");
        regionSelect.setRequired(true);
        regionSelect.setNullSelectionAllowed(false);

        regionSelect.addItem(Zero);
        regionSelect.setItemCaption(Zero, "Select Region");
        regionMap = findAllParent(regionSelect, Events.FIND_ALL_REGIONS);
        regionSelect.setValue(Zero);
        form.addComponent(regionSelect);

        onAreaRegionSelection(parentSelect, regionSelect);

        final Button updateButton = new Button("Create");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            parentSelect.setComponentError(null);
            App.bus.send(CREATE_REQUEST, new JsonObject()
                    .put(Query.name, nameField.getValue())
                    .put(PARENT_FIELD, parentSelect.getValue()), respond(ui, window, nameField, parentSelect, collection + _created_successfully));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void onAreaRegionSelection(final NativeSelect areaSelect, final NativeSelect regionSelect) {
        final Long Zero = 0L;
        regionSelect.addValueChangeListener(event -> {
            if (!ignoreSelectionChange) {
                try {
                    ignoreSelectionChange = true;
                    final Object selectedRegionId = event.getProperty().getValue();
                    Collection<JsonObject> areas = selectedRegionId.equals(0L) ? areaMap.values() : areaMap.values().stream().filter(j -> j.getJsonObject(Query.region, new JsonObject()).getLong(Query.id, 0L).equals(selectedRegionId)).collect(Collectors.toSet());
                    areaSelect.clear();
                    areaSelect.removeAllItems();
                    areaSelect.addItem(Zero);
                    areaSelect.setItemCaption(Zero, "Select Area");
                    areas.forEach(c -> {
                        final Long aId = c.getLong(Query.id);
                        areaSelect.addItem(aId);
                        areaSelect.setItemCaption(aId, c.getString(Query.name));
                    });
                    areaSelect.setValue(Zero);
                } finally {
                    ignoreSelectionChange = false;
                }
            }
        });

        areaSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent e) {
                if (!ignoreSelectionChange) {
                    try {
                        ignoreSelectionChange = true;
                        final Object selectedAreaId = e.getProperty().getValue();
                        final Long regionId = selectedAreaId.equals(0L) ? 0L : areaMap.get(selectedAreaId).getJsonObject(Query.region, new JsonObject()).getLong(Query.id, 0L);
                        regionSelect.setValue(regionId);
                    } finally {
                        ignoreSelectionChange = false;
                    }
                }
            }
        });
    }

    private Map<Long, JsonObject> findAllParent(final NativeSelect parentSelect, final String destination) throws ExecutionException, InterruptedException {
        final JsonArray parentList = parentList(destination);
        final Map<Long, JsonObject> map = new LinkedHashMap<>();
        parentList.forEach(doc -> {
            JsonObject document = (JsonObject) doc;
            final Long parentId = document.getLong(Query.id);
            parentSelect.addItem(parentId);
            parentSelect.setItemCaption(parentId, document.getString(Query.name));
            map.put(document.getLong(Query.id, 0L), document);
        });
        return map;
    }

    private JsonArray parentList(String destination) throws ExecutionException, InterruptedException {
        final FutureResult<JsonArray> result = new FutureResult<>();
        App.bus.send(destination, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.failed()) {
                result.signalError(r.cause());
                return;
            }

            result.signal(r.result().body());
        });
        return result.get();
    }

    private Handler<AsyncResult<Message<JsonObject>>> respond(UI ui, Window window, TextField nameField, NativeSelect parentSelect, String successMessage) {
        return r -> {
            if (r.failed()) {
                final Throwable cause = r.cause();
                if (cause instanceof ReplyException) {
                    ReplyException ex = (ReplyException) cause;
                    if (ex.failureCode() == FailureCode.validationError.code) {
                        JsonObject errorObject = new JsonObject(ex.getMessage());

                        ui.access(() -> {
                            errorObject.forEach(e -> {
                                final JsonArray list = ((JsonArray) e.getValue());
                                String errorMessages = "";
                                switch (e.getKey()) {
                                    case Query.name:
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(Query.message) + "").collect(Collectors.toList()));
                                        nameField.setComponentError(Util.errorMessage(errorMessages));
                                        break;
                                    case PARENT_ID_FIELD:
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(Query.message) + "").collect(Collectors.toList()));
                                        parentSelect.setComponentError(errorMessage(errorMessages));
                                        break;
                                }
                            });
                        });
                        return;
                    }
                }
                ui.access(() -> {
                    Notification.show("Error: " + nullToEmpty(cause.getMessage()) + " Please try again.", Notification.Type.ERROR_MESSAGE);
                    window.close();
                });
                return;
            }
            ui.access(() -> {
                Notification.show(successMessage, Notification.Type.TRAY_NOTIFICATION);
                window.close();
            });
        };
    }

    public void populateData(final JsonArray data) {
        dataMap.clear();
        data.forEach(v -> {
            JsonObject house = (JsonObject) v;
            final Long houseId = house.getLong(id);
            table.addItem(item(houseId, house.getString(Query.name, ""), house.getJsonObject(PARENT_FIELD, new JsonObject()), house.getJsonArray(Query.locations, new JsonArray())), houseId);
            dataMap.put(houseId, house);
        });
    }

    private Object[] item(final Long id, final String name, final JsonObject area, final JsonArray locations) {
        final Link link = new Link(area.getString(Query.name, ""), new ExternalResource(""));
        final Link linkRegion = new Link(area.getJsonObject(Query.region, new JsonObject()).getString(Query.name, ""), new ExternalResource(""));
        final ArrayList<String> locationList = new ArrayList<>();
        locations.forEach(l -> {
            JsonObject loc = (JsonObject) l;
            locationList.add(loc.getString(Query.name, ""));
        });
        final Link linkLocation = new Link(String.join("\n", locationList), new ExternalResource(""));
        return new Object[]{id, name, link, linkRegion, linkLocation};
    }

    public Table getTable() {
        return table;
    }
}
