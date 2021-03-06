package vaadincrm.view.area;

import com.vaadin.event.Action;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import io.crm.Events;
import io.crm.FailureCode;
import io.crm.QC;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Resp;
import vaadincrm.util.FutureResult;
import vaadincrm.util.VaadinUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.crm.QC.id;
import static io.crm.util.ExceptionUtil.toRuntime;
import static io.crm.util.Util.isEmptyOrNullOrSpaces;
import static vaadincrm.Resp._created_successfully;
import static vaadincrm.Resp._updated_successfully;
import static vaadincrm.Resp.value_is_invalid;
import static vaadincrm.util.VaadinUtil.asMap;
import static vaadincrm.util.VaadinUtil.errorMessage;

/**
 * Created by someone on 16-Aug-2015.
 */
final public class AreaTable {
    public static final String collection = "Area";
    public static final String GET_PARENT_REQUEST = Events.FIND_ALL_REGIONS;

    private static final String PARENT_FIELD = QC.region;
    private static final String PARENT_LABEL = Resp.Region;
    private static final String PARENT_ID_FIELD = QC.regionId;

    private static final String ID_PROPERTY = "Id";
    private static final Object NAME_PROPERTY = "Name";
    private static final String REGION_PROPERTY = PARENT_LABEL;

    private static final String UPDATE_REQUEST = Events.UPDATE_AREA;
    private static final String CREATE_REQUEST = Events.CREATE_AREA;

    private static final Action ADD_ITEM_ACTION = new Action("Add new " + collection);
    private static final Action EDIT_ITEM_ACTION = new Action("Edit this " + collection);
    private static final Action VIEW_ITEM_ACTION = new Action("View this " + collection);

    private String params;

    private final Table table = new Table();
    private final Map<Long, JsonObject> dataMap = new HashMap<>();

    public AreaTable(String params) {
        this.params = params;
        System.out.println(String.format("params: %s", params));
    }

    public AreaTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(ID_PROPERTY, Long.class, 0);
        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(REGION_PROPERTY, Link.class, "");

        table.addActionHandler(new Action.Handler() {
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
        });

        return this;
    }

    private void viewItemForm(final JsonObject area) {
        final Window window = new Window(collection + " Details");
        window.setWidth(400.0f, Sizeable.Unit.PIXELS);
        window.setHeight(200.0f, Sizeable.Unit.PIXELS);
        window.center();
        final VerticalLayout content = new VerticalLayout();
        window.setContent(content);

        content.addStyleName("outlined");
        content.setSizeFull();
        content.setSpacing(true);
        content.setMargin(true);

        content.addComponents(
                addDetailsField("ID", area.getLong(id)),
                addDetailsField("Name", area.getString(QC.name)),
                addDetailsFieldWithLink(PARENT_LABEL, area.getJsonObject(PARENT_FIELD)
                        .getString(QC.name)));

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

    private void editItemForm(final JsonObject area) throws ExecutionException, InterruptedException {
        final Window window = new Window(collection + " Details");
        window.setWidth(400.0f, Sizeable.Unit.PIXELS);
        window.setHeight(200.0f, Sizeable.Unit.PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final TextField nameField = new TextField("Name", area.getString(QC.name));
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final NativeSelect parentSelect = new NativeSelect(PARENT_LABEL);
        parentSelect.setRequired(true);
        parentSelect.setNullSelectionAllowed(false);

        final Long Zero = 0L;
        parentSelect.addItem(Zero);
        parentSelect.setItemCaption(Zero, "Select " + PARENT_LABEL);
        findAllParent(parentSelect, GET_PARENT_REQUEST);
        parentSelect.setValue(area.getJsonObject(PARENT_FIELD).getLong(id));
        form.addComponent(parentSelect);

        final Button updateButton = new Button("Update");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            parentSelect.setComponentError(null);
            App.bus.send(UPDATE_REQUEST, new JsonObject().put(id, area.getLong(id))
                    .put(QC.name, nameField.getValue())
                    .put(PARENT_FIELD, parentSelect.getValue()), respond(ui, window, nameField, parentSelect, collection + _updated_successfully));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private JsonArray findAllParent(final NativeSelect parentSelect, final String destination) throws ExecutionException, InterruptedException {
        final JsonArray parentList = parentList(destination);

        parentList.forEach(doc -> {
            JsonObject document = (JsonObject) doc;
            final Long parentId = document.getLong(id);
            parentSelect.addItem(parentId);
            parentSelect.setItemCaption(parentId, document.getString(QC.name));
        });
        return parentList;
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
                                    case QC.name:
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(QC.message) + "").collect(Collectors.toList()));
                                        nameField.setComponentError(VaadinUtil.errorMessage(errorMessages));
                                        break;
                                    case "regionId":
                                        errorMessages = list == null ? value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(QC.message) + "").collect(Collectors.toList()));
                                        parentSelect.setComponentError(errorMessage(errorMessages));
                                        break;
                                }
                            });
                        });
                        return;
                    }
                }
                ui.access(() -> {
                    Notification.show("Error: " + isEmptyOrNullOrSpaces(cause.getMessage()) + " Please try again.", Notification.Type.ERROR_MESSAGE);
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

    private void addItemForm() throws ExecutionException, InterruptedException {
        final Window window = new Window(collection + " Details");
        window.setWidth(400.0f, Sizeable.Unit.PIXELS);
        window.setHeight(200.0f, Sizeable.Unit.PIXELS);
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
        parentSelect.setItemCaption(Zero, "Select a " + PARENT_LABEL);
        findAllParent(parentSelect, GET_PARENT_REQUEST);
        parentSelect.setValue(Zero);
        form.addComponent(parentSelect);

        final Button updateButton = new Button("Create");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            parentSelect.setComponentError(null);
            App.bus.send(CREATE_REQUEST, new JsonObject()
                    .put(QC.name, nameField.getValue())
                    .put(PARENT_FIELD, parentSelect.getValue()), respond(ui, window, nameField, parentSelect, collection + _created_successfully));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    public void populateData(final JsonArray data) {
        dataMap.clear();
        data.forEach(v -> {
            JsonObject area = (JsonObject) v;
            final Long areaId = area.getLong(id);
            table.addItem(item(areaId, area.getString(QC.name), area.getJsonObject(PARENT_FIELD)), areaId);
            dataMap.put(areaId, area);
        });
    }

    private Object[] item(final Long id, final String name, final JsonObject parent) {
        final Link link = new Link(parent.getString(QC.name), new ExternalResource(""));
        return new Object[]{id, name, link};
    }

    public Table getTable() {
        return table;
    }
}
