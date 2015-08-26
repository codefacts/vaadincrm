package vaadincrm.view.usertype;

import com.vaadin.event.Action;
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
import vaadincrm.model.Query;
import vaadincrm.util.VaadinUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.crm.util.Util.isEmptyOrNull;
import static vaadincrm.model.Model.id;
import static vaadincrm.util.VaadinUtil.asMap;

/**
 * Created by someone on 20/08/2015.
 */
public class UserTypeTable {
    private static final String collection = "UserType";

    private static final String ID_PROPERTY = "Id";
    private static final Object NAME_PROPERTY = "Name";
    private static final Object ID_PREFIX_PROPERTY = "ID Prefix";
    private static final Object LABEL_PROPERTY = "Label";
    private static final Object USER_COUNT_PROPERTY = "User Count";

    private static final Action ADD_ITEM_ACTION = new Action("Add new " + collection);
    private static final Action EDIT_ITEM_ACTION = new Action("Edit this " + collection);
    private static final Action VIEW_ITEM_ACTION = new Action("View this " + collection);
    private static final String UPDATE_REQUEST = Events.UPDATE_USER_TYPE;
    private static final String CREATE_REQUEST = Events.CREATE_USER_TYPE;
    private static final float WIDTH_HEIGHT = 500.0f;
    private static final float WINDOW_WIDTH = 800.0f;
    private static final float VIEW_WINDOW_HEIGHT = 500.0f;
    private static final float VIEW_WINDOW_WIDTH = 800.0f;

    private String params;

    private final Table table = new Table();
    private final Map<Long, JsonObject> dataMap = new HashMap<>();

    public UserTypeTable(String params) {
        this.params = params;
        System.out.println(String.format("params: %s", params));
    }

    public UserTypeTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(ID_PROPERTY, Long.class, 0L);
        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(ID_PREFIX_PROPERTY, String.class, "");
        table.addContainerProperty(LABEL_PROPERTY, String.class, "");
        table.addContainerProperty(USER_COUNT_PROPERTY, Long.class, 0L);

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
                    addItemForm();
                } else if (action == EDIT_ITEM_ACTION) {
                    editItemForm(dataMap.get((Long) target));
                } else if (action == VIEW_ITEM_ACTION) {
                    viewItemForm(dataMap.get((Long) target));
                }
            }
        });

        return this;
    }

    private void viewItemForm(final JsonObject obj) {
        final Window window = new Window(collection + " Details");
        window.setWidth(VIEW_WINDOW_WIDTH, Sizeable.Unit.PIXELS);
        window.setHeight(VIEW_WINDOW_HEIGHT, Sizeable.Unit.PIXELS);
        window.center();
        final VerticalLayout content = new VerticalLayout();
        window.setContent(content);

        content.addStyleName("outlined");
        content.setSizeFull();
        content.setSpacing(true);
        content.setMargin(true);

        content.addComponents(
                addDetailsField("ID", obj.getLong(Query.id)),
                addDetailsField("Name", obj.getString(Query.name)),
                addDetailsField("Label", obj.getString(Query.label)));

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

    private void editItemForm(final JsonObject area) {
        final Window window = new Window(collection + " Details");
        window.setWidth(WINDOW_WIDTH, Sizeable.Unit.PIXELS);
        window.setHeight(WIDTH_HEIGHT, Sizeable.Unit.PIXELS);
        window.center();
        final FormLayout form = new FormLayout();
        window.setContent(form);

        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);
        form.setMargin(true);

        final TextField nameField = new TextField("Name", area.getString(Query.name));
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final TextField labelField = new TextField("Label", area.getString(Query.label));
        labelField.setNullSettingAllowed(false);
        labelField.setRequired(true);
        form.addComponent(labelField);

        final Button updateButton = new Button("Update");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            App.bus.send(UPDATE_REQUEST, new JsonObject().put(id, area.getLong(id))
                    .put(Query.name, nameField.getValue())
                    .put(Query.label, labelField.getValue()), respond(ui, window, nameField, collection + "updated successfully."));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void addItemForm() {
        final Window window = new Window(collection + " Details");
        window.setWidth(WINDOW_WIDTH, Sizeable.Unit.PIXELS);
        window.setHeight(WIDTH_HEIGHT, Sizeable.Unit.PIXELS);
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

        final TextField labelField = new TextField("Label", "");
        labelField.setNullSettingAllowed(false);
        labelField.setRequired(true);
        form.addComponent(labelField);

        final Button updateButton = new Button("Create");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            App.bus.send(CREATE_REQUEST, new JsonObject()
                    .put(Query.name, nameField.getValue())
                    .put(Query.label, labelField.getValue()), respond(ui, window, nameField, collection + " created successfully."));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private Handler<AsyncResult<Message<Object>>> respond(final UI ui, final Window window, final TextField nameField, String successMessage) {
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
                                        errorMessages = list == null ? Resp.value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(Query.message) + "").collect(Collectors.toList()));
                                        nameField.setComponentError(VaadinUtil.errorMessage(errorMessages));
                                        break;
                                }
                            });
                        });
                        return;
                    }
                }
                ui.access(() -> {
                    Notification.show("Error: " + isEmptyOrNull(cause.getMessage()), Notification.Type.ERROR_MESSAGE);
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
            JsonObject userType = (JsonObject) v;
            final Long areaId = userType.getLong(id);
            table.addItem(item(areaId, userType.getString(Query.name), userType.getString(Query.prefix),
                    userType.getString(Query.label), userType.getLong(Query.count)), areaId);
            dataMap.put(areaId, userType);
        });
    }

    private Object[] item(final Long id, final String name, final String prefix, final String label, final Long userCount) {
        return new Object[]{id, name, prefix, label, userCount == null ? 0L : userCount};
    }

    public Table getTable() {
        return table;
    }
}
