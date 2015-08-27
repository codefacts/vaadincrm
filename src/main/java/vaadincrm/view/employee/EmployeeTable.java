package vaadincrm.view.employee;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import io.crm.FailureCode;
import io.crm.util.SimpleCounter;
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
import vaadincrm.model.User;
import vaadincrm.util.FutureResult;
import vaadincrm.util.VaadinUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static io.crm.util.ExceptionUtil.sallowCall;
import static io.crm.util.Util.*;
import static vaadincrm.model.Model.id;
import static vaadincrm.util.VaadinUtil.asMap;
import static vaadincrm.util.VaadinUtil.okCancelFooter;

/**
 * Created by someone on 20/08/2015.
 */
final public class EmployeeTable {
    private static final String collection = "Employee";

    private static final String ID_PROPERTY = "Id";
    private static final Object NAME_PROPERTY = "Name";
    private static final Object MOBILE_PROPERTY = "Mobile";
    private static final Object USERNAME_PROPERTY = "Username";

    private static final Object USER_TYPE_PROPERTY = "User Type";
    private static final Object JOIN_DATE_PROPERTY = "Join Date";
    private static final Object RESIGN_DATE_PROPERTY = "Resign Date";

    private static final Action ADD_ITEM_ACTION = new Action("Add new " + collection);
    private static final Action EDIT_ITEM_ACTION = new Action("Edit this " + collection);
    private static final Action VIEW_ITEM_ACTION = new Action("View this " + collection);
    private static final Action VIEW_PASSWORD_ACTION = new Action("View password");
    private static final String UPDATE_REQUEST = Events.UPDATE_EMPLOYEE;
    private static final String CREATE_REQUEST = Events.CREATE_EMPLOYEE;

    private String params;

    private final Table table = new Table() {
        private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("MMM dd, yyyy");
            }
        };

        @Override
        protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
            final Object value = property.getValue();
            if (value instanceof Date) {
                return dateFormat.get().format(value);
            }
            return super.formatPropertyValue(rowId, colId, property);
        }
    };

    private NativeSelect userTypeSelect;

    public EmployeeTable(String params) {
        this.params = params;
        System.out.println(String.format("params: %s", params));
    }

    public EmployeeTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(ID_PROPERTY, String.class, "");
        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(MOBILE_PROPERTY, String.class, "");
        table.addContainerProperty(USERNAME_PROPERTY, String.class, "");
        table.addContainerProperty(USER_TYPE_PROPERTY, String.class, "");
        table.addContainerProperty(JOIN_DATE_PROPERTY, Date.class, null);
        table.addContainerProperty(RESIGN_DATE_PROPERTY, Date.class, null);

        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
                if (target == null) {
                    return new Action[]{ADD_ITEM_ACTION};
                } else {
                    return new Action[]{VIEW_ITEM_ACTION, EDIT_ITEM_ACTION, ADD_ITEM_ACTION, VIEW_PASSWORD_ACTION};
                }
            }

            @Override
            public void handleAction(final Action action, final Object sender, final Object target) {
                if (action == ADD_ITEM_ACTION) {
                    addItemForm();
                } else if (action == EDIT_ITEM_ACTION) {
                    editItemForm(getUserById((String) target));
                } else if (action == VIEW_ITEM_ACTION) {
                    viewItemForm(getUserById((String) target));
                } else if (action == VIEW_PASSWORD_ACTION) {
                    viewPasswordForm(getUserById((String) target));
                }
            }
        });

        return this;
    }

    private JsonObject getUserById(String userId) {
        final FutureResult<JsonObject> futureResult = new FutureResult<>();

        App.bus.send(Events.FIND_EMPLOYEE, userId, (AsyncResult<Message<JsonObject>> r) -> {
            if (r.failed()) {
                futureResult.signalError(r.cause());
                return;
            }

            futureResult.signal(r.result().body());
        });

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewPasswordForm(JsonObject user) {

        if (isEmptyOrNull(user.getString(User.password))) {
            Notification.show(Resp.This_user_does_not_have_password, Notification.Type.WARNING_MESSAGE);
            return;
        }

        VaadinUtil.showConfirmDialog("Password", new Label(VaadinUtil.p("Password: " + user.getString(User.password, "")), ContentMode.HTML));
    }

    private void viewItemForm(final JsonObject obj) {
        final Window window = new Window(collection + " Details");
        window.setWidth(400.0f, PIXELS);
        window.setHeight(100.0f, PIXELS);
        window.center();
        final VerticalLayout content = new VerticalLayout();
        window.setContent(content);

        content.addStyleName("outlined");
        content.setSizeFull();
        content.setSpacing(true);
        content.setMargin(true);

        content.addComponents(
                addDetailsField("ID", obj.getLong(Query.id)),
                addDetailsField("Name", obj.getString(Query.name)));

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
        window.setWidth(400.0f, PIXELS);
        window.setHeight(200.0f, PIXELS);
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

        final Button updateButton = new Button("Update");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            App.bus.send(UPDATE_REQUEST, new JsonObject().put(id, area.getLong(id))
                    .put(Query.name, nameField.getValue()), respond(ui, window, nameField, collection + "updated successfully."));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void addItemForm() {
        final Window window = new Window(collection + " Details");
        window.setWidth(400.0f, PIXELS);
        window.setHeight(200.0f, PIXELS);
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

        final Button updateButton = new Button("Create");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            App.bus.send(CREATE_REQUEST, new JsonObject()
                    .put(Query.name, nameField.getValue()), respond(ui, window, nameField, collection + " created successfully."));
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
        data.forEach(v -> {
            JsonObject user = (JsonObject) v;
            final String userId = user.getString(Query.userId);

            table.addItem(item(userId, user.getString(Query.name, ""), user.getString(User.mobile, ""),
                    user.getString(User.username, ""),
                    user.getJsonObject(User.userType, new JsonObject()).getString(User.name),
                    sallowCall(() -> parseMongoDate(user.getJsonObject(User.joinDate))),
                    sallowCall(() -> parseMongoDate(user.getJsonObject(User.resignDate)))), userId);
        });
    }

    private Object[] item(final String id, final String name, final String mobile, final String username,
                          final String userType, final Date joinDate, final Date resignDate) {

        return new Object[]{
                id, name, mobile, username,
                userType, joinDate, resignDate
        };
    }

    public Table getTable() {
        return table;
    }

    public NativeSelect getUserTypeSelect() {
        if (userTypeSelect == null) {
            initUserTypeSelect();
        }
        return userTypeSelect;
    }

    private void initUserTypeSelect() {
        userTypeSelect = new NativeSelect();
        final JsonArray userTypes = findAllUserTypes();
        final Long ZERO = 0L;
        userTypeSelect.addItem(ZERO);

        final SimpleCounter totalUsers = new SimpleCounter();
        userTypes.forEach(u -> {
            JsonObject userType = (JsonObject) u;
            final Long typeId = userType.getLong(Query.id);
            userTypeSelect.addItem(typeId);
            userTypeSelect.setItemCaption(typeId, String.format("%s (%s): %d", userType.getString(Query.name, ""),
                    userType.getString(Query.prefix, ""),
                    userType.getLong(Query.count, 0L)));
            totalUsers.counter += userType.getLong(Query.count, 0L);
        });
        userTypeSelect.setValue(ZERO);
        userTypeSelect.setItemCaption(ZERO, "All Users: " + totalUsers.counter);

        userTypeSelect.addValueChangeListener(e -> {
            final Long userTypeId = (Long) e.getProperty().getValue();

            if (userTypeId == null || userTypeId.longValue() <= 0) {
                filterAndUpdateTable(new JsonObject());
                return;
            }

            filterAndUpdateTable(new JsonObject().put(Query.userTypeId, userTypeId));
        });

        userTypeSelect.setNullSelectionAllowed(false);
    }

    private void filterAndUpdateTable(JsonObject criteria) {
        final UI ui = UI.getCurrent();
        App.bus.send(Events.FIND_ALL_EMPLOYEES, new JsonObject()
                .put(Query.params, criteria), (AsyncResult<Message<JsonArray>> r) -> {
            ui.access(() -> {
                if (r.failed()) {
                    Notification.show("Error in server. Please try again later.", Notification.Type.ERROR_MESSAGE);
                    io.crm.util.ExceptionUtil.logException(r.cause());
                    return;
                }

                final JsonArray jsonArray = r.result().body();
                table.removeAllItems();
                populateData(jsonArray);
            });
        });
    }

    private JsonArray findAllUserTypes() {
        final FutureResult<JsonArray> futureResult = new FutureResult<>();
        App.bus.send(Events.FIND_ALL_USER_TYPES, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.failed()) {
                futureResult.signalError(r.cause());
                return;
            }

            futureResult.signal(r.result().body());
        });

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
