package vaadincrm.view.house;

import com.vaadin.event.Action;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import io.crm.Events;
import io.crm.FailureCode;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Resp;
import vaadincrm.model.Query;
import vaadincrm.service.SelectionService;
import vaadincrm.util.FutureResult;
import vaadincrm.util.VaadinUtil;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static io.crm.util.ExceptionUtil.toRuntime;
import static vaadincrm.Resp.*;
import static vaadincrm.model.Model.id;

final public class HouseTable {
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

    private static final Action ADD_LOCATION_ACTION = new Action("Add new Location");
    private static final Action RENAME_LOCATION_ACTION = new Action("Rename this Location");
    private static final Action MOVE_LOCATION_ACTION = new Action("Move to another house");

    private static final float ROW_HEIGHT = 100.0f;

    private final float VIEW_WINDOW_WIDTH = 1000.0f;
    private final float VIEW_WINDOW_HEIGHT = 450.0f;
    private final float EDIT_WINDOW_WIDTH = 1000.0f;
    private final float EDIT_WINDOW_HEIGHT = 450.0f;

    private String params;

    private final Table table = new Table();
    private final Map<Long, JsonObject> dataMap = new HashMap<>();
    private final Map<Long, JsonObject> areaMap = new HashMap<>();
    private SelectionService selectionService = new SelectionService(dataMap, areaMap);

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

        content.addStyleName("outlined");
        content.setSpacing(true);
        content.setMargin(true);
        content.setWidth(600, PIXELS);
        content.setHeight(300, PIXELS);

        final JsonObject area = house.getJsonObject(PARENT_FIELD, new JsonObject());
        content.addComponents(
                addDetailsField("ID", house.getLong(Query.id)),
                addDetailsField("Name", house.getString(Query.name)),
                addDetailsFieldWithLink(PARENT_LABEL, area
                        .getString(Query.name)),
                addDetailsFieldWithLink("Region", area
                        .getJsonObject(Query.region, new JsonObject()).getString(Query.name)));


        final VerticalLayout content2 = new VerticalLayout();
        content2.setWidth(380, PIXELS);
        content2.setHeight(400, PIXELS);
        content2.setSpacing(true);
        content2.setMargin(true);

        content2.addComponent(addDetailsFieldMultiLink("Locations", house.getJsonArray(Query.locations, new JsonArray())));


        final HorizontalLayout layout = new HorizontalLayout(content, content2);
        layout.setSizeFull();
        layout.setExpandRatio(content, 0.5f);
        window.setContent(layout);
        UI.getCurrent().addWindow(window);
    }

    private Component addDetailsFieldMultiLink(String caption, JsonArray jsonArray) {

        Table table = new Table(caption);

        table.setSizeFull();
        table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        table.setSelectable(false);

        table.addContainerProperty("Locations", Link.class, "");
        jsonArray.forEach(j -> {
            JsonObject loc = (JsonObject) j;
            table.addItem(new Object[]{new Link(loc.getString(Query.name), new ExternalResource(""))}, loc.getLong(Query.id));
        });

        return table;
    }

    private Component addDetailsField(String caption, Object value) {
        final HorizontalLayout content = new HorizontalLayout();
        content.setHeight(ROW_HEIGHT, PIXELS);

        final Label label = new Label(caption);
        final Label valueLabel = new Label(value + "");
        content.addComponents(label, valueLabel);

        content.setSpacing(true);
        return content;
    }

    private Component addDetailsFieldWithLink(String caption, Object value) {
        final HorizontalLayout content = new HorizontalLayout();
        content.setHeight(ROW_HEIGHT, PIXELS);

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
        areaMap.clear();
        areaMap.putAll(findAllAndPopulate(parentSelect, GET_PARENT_REQUEST));
        parentSelect.setValue(house.getJsonObject(PARENT_FIELD).getLong(id));
        form.addComponent(parentSelect);

        final NativeSelect regionSelect = new NativeSelect("Region");
        regionSelect.setRequired(true);
        regionSelect.setNullSelectionAllowed(false);

        regionSelect.addItem(Zero);
        regionSelect.setItemCaption(Zero, "Select Region");
        findAllAndPopulate(regionSelect, Events.FIND_ALL_REGIONS);
        regionSelect.setValue(house.getJsonObject(PARENT_FIELD, new JsonObject()).getJsonObject(Query.region).getLong(id));
        form.addComponent(regionSelect);

        selectionService.onAreaRegionSelection(parentSelect, regionSelect);

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

        final VerticalLayout tableContainer = new VerticalLayout();
        tableContainer.addComponent(locationTable(house));
        tableContainer.setHeight(300, PIXELS);
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(form, tableContainer);

        window.setContent(splitPanel);
        ui.addWindow(window);
    }

    private Table locationTable(final JsonObject house) {
        Table table = new Table("Locations");

        table.setSizeFull();
        table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        table.setSelectable(false);

        table.addContainerProperty("Locations", Link.class, "");
        house.getJsonArray(Query.locations, new JsonArray()).forEach(j -> {
            JsonObject loc = (JsonObject) j;
            table.addItem(new Object[]{new Link(loc.getString(Query.name), new ExternalResource(""))}, loc.getLong(Query.id));
        });

        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
                if (target == null) {
                    return new Action[]{ADD_LOCATION_ACTION};
                } else {
                    return new Action[]{RENAME_LOCATION_ACTION, ADD_LOCATION_ACTION, MOVE_LOCATION_ACTION};
                }
            }

            @Override
            public void handleAction(final Action action, final Object sender, final Object target) {
                if (action == ADD_LOCATION_ACTION) {
                    toRuntime(() -> addLocationForm(house));
                } else if (action == RENAME_LOCATION_ACTION) {
                    toRuntime(() -> renameLocationForm(
                            house, (Long) target));
                } else if (action == MOVE_LOCATION_ACTION) {
                    moveLocationForm(
                            house, (Long) target);
                }
            }
        });

        return table;
    }

    private void moveLocationForm(final JsonObject house, final Long locationId) {
        final Window window = new Window("Move Location");
        window.setWidth(600, PIXELS);
        window.setHeight(300, PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final NativeSelect houseSelect = new NativeSelect("Distribution House");
        houseSelect.setRequired(true);
        houseSelect.setNullSelectionAllowed(false);
        try {
            findAllAndPopulate(houseSelect, Events.FIND_ALL_HOUSES);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        houseSelect.setValue(house.getLong(Query.id));
        form.addComponent(houseSelect);


        final NativeSelect parentSelect = new NativeSelect(PARENT_LABEL);
        parentSelect.setRequired(true);
        parentSelect.setNullSelectionAllowed(false);

        final Long Zero = 0L;
        parentSelect.addItem(Zero);
        parentSelect.setItemCaption(Zero, "Select " + PARENT_LABEL);
        try {
            findAllAndPopulate(parentSelect, GET_PARENT_REQUEST);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        parentSelect.setValue(house.getJsonObject(PARENT_FIELD).getLong(id));
        form.addComponent(parentSelect);

        final NativeSelect regionSelect = new NativeSelect("Region");
        regionSelect.setRequired(true);
        regionSelect.setNullSelectionAllowed(false);

        regionSelect.addItem(Zero);
        regionSelect.setItemCaption(Zero, "Select Region");
        try {
            findAllAndPopulate(regionSelect, Events.FIND_ALL_REGIONS);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        regionSelect.setValue(house.getJsonObject(PARENT_FIELD, new JsonObject()).getJsonObject(Query.region).getLong(id));
        form.addComponent(regionSelect);

        selectionService.onAreaRegionSelection(houseSelect, parentSelect, regionSelect);

        final Button button = new Button("Move");
        button.addClickListener(e -> {

            final UI ui = UI.getCurrent();
            final JsonObject jsonObject = new JsonObject();

            house.getJsonArray(Query.locations, new JsonArray()).forEach(j -> {
                JsonObject jo = (JsonObject) j;
                if (jo.getLong(Query.id).equals(locationId)) {
                    jsonObject.put(Query.id, locationId)
                            .put(Query.name, jo.getString(Query.name))
                            .put(Query.distributionHouse, houseSelect.getValue());
                }
            });

            App.bus.send(Events.UPDATE_LOCATION, jsonObject, r -> {
                ui.access(() -> {
                    if (r.failed()) {
                        ui.access(() -> {
                            if (r.cause() instanceof ReplyException && ((ReplyException) r.cause()).failureCode() == FailureCode.validationError.code) {
                                Notification.show(String.format("%s", new JsonObject(r.cause().getMessage()).getJsonArray(Query.name, new JsonArray()).getJsonObject(0).getString(Query.message, "Unknown validation error.")), Notification.Type.ERROR_MESSAGE);
                                return;
                            }
                            Notification.show(String.format("%s Please try again.", r.cause().getMessage()), Notification.Type.ERROR_MESSAGE);
                        });
                        System.err.println("ERROR: " + r.cause().getMessage());
                        return;
                    }
                    window.close();
                    Notification.show("Location moved successfully.", Notification.Type.TRAY_NOTIFICATION);
                });
            });
            System.out.println("LOCATION MOVED TO: ");
        });
        form.addComponent(button);

        window.setVisible(true);
        UI.getCurrent().addWindow(window);
    }

    private void renameLocationForm(final JsonObject house, final Long locationId) {
        editLocationForm(locationId, house, "", Events.UPDATE_LOCATION, "Rename Location", "Update", "Location updated successfully.");
    }

    private void addLocationForm(final JsonObject house) {
        editLocationForm(null, house, "", Events.CREATE_LOCATION, "Create new Location", "Create", "Location created successfully.");
    }

    public void editLocationForm(final Long locationId, final JsonObject house, final String initValue, final String reqAddress, String windowTitle, String buttionCaption, String successMsg) {
        final Window window = new Window(windowTitle);
        window.setWidth(600, PIXELS);
        window.setHeight(300, PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final TextField nameField = new TextField("Name", initValue);
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final Button button = new Button(buttionCaption);
        button.addClickListener(e -> {

            final UI ui = UI.getCurrent();
            final JsonObject jsonObject = (locationId == null || locationId.equals(0L))
                    ? new JsonObject().put(Query.name, nameField.getValue())
                    : new JsonObject().put(Query.id, locationId).put(Query.name, nameField.getValue());

            jsonObject.put(Query.distributionHouse, house.getLong(Query.id, 0L));

            App.bus.send(reqAddress, jsonObject, r -> {
                ui.access(() -> {
                    if (r.failed()) {
                        ui.access(() -> {
                            if (r.cause() instanceof ReplyException && ((ReplyException) r.cause()).failureCode() == FailureCode.validationError.code) {
                                Notification.show(String.format("%s", new JsonObject(r.cause().getMessage()).getJsonArray(Query.name, new JsonArray()).getJsonObject(0).getString(Query.message, "Unknown validation error.")), Notification.Type.ERROR_MESSAGE);
                                return;
                            }
                            Notification.show(String.format("%s Please try again.", r.cause().getMessage()), Notification.Type.ERROR_MESSAGE);
                        });
                        System.err.println("ERROR: " + r.cause().getMessage());
                        return;
                    }
                    window.close();
                    Notification.show(successMsg, Notification.Type.TRAY_NOTIFICATION);
                });
            });
            System.out.println(successMsg);
        });
        form.addComponent(button);

        window.setVisible(true);
        UI.getCurrent().addWindow(window);
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
        areaMap.clear();
        areaMap.putAll(findAllAndPopulate(parentSelect, GET_PARENT_REQUEST));
        parentSelect.setValue(Zero);
        form.addComponent(parentSelect);

        final NativeSelect regionSelect = new NativeSelect("Region");
        regionSelect.setRequired(true);
        regionSelect.setNullSelectionAllowed(false);

        regionSelect.addItem(Zero);
        regionSelect.setItemCaption(Zero, "Select Region");
        findAllAndPopulate(regionSelect, Events.FIND_ALL_REGIONS);
        regionSelect.setValue(Zero);
        form.addComponent(regionSelect);

        selectionService.onAreaRegionSelection(parentSelect, regionSelect);

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

    private Map<Long, JsonObject> findAllAndPopulate(final NativeSelect parentSelect, final String destination) throws ExecutionException, InterruptedException {
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
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> VaadinUtil.asMap(j).get(Query.message) + "").collect(Collectors.toList()));
                                        nameField.setComponentError(VaadinUtil.errorMessage(errorMessages));
                                        break;
                                    case PARENT_ID_FIELD:
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> VaadinUtil.asMap(j).get(Query.message) + "").collect(Collectors.toList()));
                                        parentSelect.setComponentError(VaadinUtil.errorMessage(errorMessages));
                                        break;
                                }
                            });
                        });
                        return;
                    }
                }
                ui.access(() -> {
                    Notification.show("Error: " + io.crm.util.Util.isEmptyOrNullOrSpaces(cause.getMessage()) + " Please try again.", Notification.Type.ERROR_MESSAGE);
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
