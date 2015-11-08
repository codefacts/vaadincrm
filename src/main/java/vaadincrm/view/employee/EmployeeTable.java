package vaadincrm.view.employee;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import io.crm.Events;
import io.crm.FailureCode;
import io.crm.QC;
import io.crm.util.SimpleCounter;
import io.crm.util.touple.MutableTpl1;
import io.crm.util.touple.MutableTpl2;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.Resp;
import io.crm.model.EmployeeType;
import io.crm.model.User;
import vaadincrm.service.QueryService;
import vaadincrm.service.SelectionService;
import vaadincrm.util.*;
import vaadincrm.util.PopupWindowBuilder.ContentBuilder;
import vaadincrm.util.PopupWindowBuilder.ContentBuilder.FooterBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static fluentui.FluentFormLayout.formLayout;
import static fluentui.FluentTextField.textField;
import static io.crm.util.ExceptionUtil.sallowCall;
import static io.crm.util.Util.*;
import static vaadincrm.App.bus;
import static io.crm.Events.CREATE_EMPLOYEE;
import static io.crm.QC.id;
import static vaadincrm.util.VaadinUtil.asMap;
import static vaadincrm.util.VaadinUtil.handleError;
import static vaadincrm.util.VaadinUtil.showConfirmDialog;

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
    private static final String CREATE_REQUEST = CREATE_EMPLOYEE;
    private static final String NEXT = "Next";
    private static final String CREATE = "Create";

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
                    selectUserTypePopup();
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

        bus.send(Events.FIND_EMPLOYEE, userId, (AsyncResult<Message<JsonObject>> r) -> {
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

        if (isEmptyOrNullOrSpaces(user.getString(User.password))) {
            Notification.show(Resp.This_user_does_not_have_password, Notification.Type.WARNING_MESSAGE);
            return;
        }

        showConfirmDialog("Password", new Label(VaadinUtil.p("Password: " + user.getString(User.password, "")), ContentMode.HTML),
                w -> w.close());
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
                addDetailsField("ID", obj.getLong(QC.id)),
                addDetailsField("Name", obj.getString(QC.name)));

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

        final TextField nameField = new TextField("Name", area.getString(QC.name));
        nameField.setNullSettingAllowed(false);
        nameField.setRequired(true);
        form.addComponent(nameField);

        final Button updateButton = new Button("Update");
        updateButton.setImmediate(true);
        final UI ui = UI.getCurrent();
        updateButton.addClickListener(event -> {
            nameField.setComponentError(null);
            bus.send(UPDATE_REQUEST, new JsonObject().put(id, area.getLong(id))
                    .put(QC.name, nameField.getValue()), respond(ui, window, nameField, collection + "updated successfully."));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void selectUserTypePopup() {
        final NativeSelect userTypeSelect = userTypeSelect();
        final FormLayout root = formLayout()
                .spacing(true)
                .sizeFull()
                .addComponent(userTypeSelect)
                .get();

        showConfirmDialog("Select User Type", root, w -> {
            final Long value = (Long) userTypeSelect.getValue();
            if (value.equals(0L)) {
                Notification.show("Please select user type to create a user of that type.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            w.close();
            addItemForm(value);
        });
    }

    private void addItemForm(final long userTypeId) {
        final UI ui = UI.getCurrent();
        final Button prevButton = new Button("Previous");
        prevButton.addStyleName("primary");

        final MutableTpl2<FormLayout, Map<String, Field>> userBasicForm = userBasicForm();

        final MutableTpl1<PopupWindow> mutableTpl1 = new MutableTpl1<>();
        final PopupWindow popupWindow = mutableTpl1.t1 = PopupWindowBuilder.create("Create User")
                .height(600, PIXELS)
                .content(new ContentBuilder()
                        .addContent(userBasicForm.t1)
                        .footer(new FooterBuilder("")
                                .okButton(okButtonText(userTypeId), e -> {
                                    final PopupWindow pw = mutableTpl1.t1;
                                    final Map<String, Field> fieldMap = userBasicForm.t2;

                                    if (pw.getOkButton().getCaption().equals(CREATE)) {
                                        bus.send(CREATE_EMPLOYEE, user(userTypeId, fieldMap), r -> {
                                            ui.access(() -> {
                                                if (r.failed()) {
                                                    handleError(r.cause());
                                                    return;
                                                }
                                            });
                                        });
                                        return;
                                    }

                                    final VerticalLayout content = mutableTpl1.t1.getContent();
                                    final Window window = mutableTpl1.t1.getWindow();
                                    content.removeAllComponents();
                                    mutableTpl1.t1.getOkButton().setCaption(CREATE);

                                    if (userTypeId == EmployeeType.areaCoordinator.id) {
                                        content.addComponent(createACForm(fieldMap));
                                        window.setCaption("Select Area for Area Coordinator");
                                    } else if (userTypeId == EmployeeType.brSupervisor.id) {
                                        content.addComponent(createSupForm(fieldMap));
                                        window.setCaption("Select Distribution Houses for Supervisor");
                                    } else if (userTypeId == EmployeeType.br.id) {
                                        content.addComponent(createBrForm(fieldMap));
                                        window.setCaption("Select Distribution House for BR");
                                    }
                                })
                                .cancelButton()))
                .build();

        ui.addWindow(popupWindow.getWindow());
    }

    private Component createBrForm(Map<String, Field> fieldMap) {
        final Long Zero = 0L;
        final NativeSelect houseSelect = new NativeSelect("Distribution House");
        final NativeSelect areaSelect = new NativeSelect("Area");
        final NativeSelect regionSelect = new NativeSelect("Region");

        houseSelect.addItem(Zero);
        areaSelect.addItem(Zero);
        regionSelect.addItem(Zero);

        houseSelect.setItemCaption(Zero, "Select Distribution House");
        areaSelect.setItemCaption(Zero, "Select Area");
        regionSelect.setItemCaption(Zero, "Select Region");

        houseSelect.setNullSelectionAllowed(false);
        areaSelect.setNullSelectionAllowed(false);
        regionSelect.setNullSelectionAllowed(false);

        houseSelect.setRequired(true);

        final List<JsonObject> houseList = QueryService.getService().findAll(Events.FIND_ALL_HOUSES, new JsonObject());
        houseList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            houseSelect.addItem(objId);
            houseSelect.setItemCaption(objId, j.getString(QC.name));
        });

        final List<JsonObject> areaList = QueryService.getService().findAll(Events.FIND_ALL_AREAS, new JsonObject());
        areaList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            areaSelect.addItem(objId);
            areaSelect.setItemCaption(objId, j.getString(QC.name));
        });

        final List<JsonObject> regionList = QueryService.getService().findAll(Events.FIND_ALL_REGIONS, new JsonObject());
        regionList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            regionSelect.addItem(objId);
            regionSelect.setItemCaption(objId, j.getString(QC.name));
        });

        houseSelect.setValue(Zero);
        areaSelect.setValue(Zero);
        regionSelect.setValue(Zero);

        houseSelect.setWidth("100%");
        areaSelect.setWidth("100%");
        regionSelect.setWidth("100%");

        new SelectionService(houseList.stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)),
                areaList.stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)))
                .onAreaRegionSelection(houseSelect, areaSelect, regionSelect);

        final FormLayout form = formLayout()
                .addComponent(houseSelect)
                .addComponent(areaSelect)
                .addComponent(regionSelect)
                .spacing()
                .margin()
                .sizeFull()
                .get();
        return form;
    }

    private Component createSupForm(Map<String, Field> fieldMap) {
        final Long Zero = 0L;
        final NativeSelect houseSelect = new NativeSelect("Distribution House");
        final NativeSelect areaSelect = new NativeSelect("Area");
        final NativeSelect regionSelect = new NativeSelect("Region");

        houseSelect.addItem(Zero);
        areaSelect.addItem(Zero);
        regionSelect.addItem(Zero);

        houseSelect.setItemCaption(Zero, "Select Distribution House");
        areaSelect.setItemCaption(Zero, "Select Area");
        regionSelect.setItemCaption(Zero, "Select Region");

        houseSelect.setNullSelectionAllowed(false);
        areaSelect.setNullSelectionAllowed(false);
        regionSelect.setNullSelectionAllowed(false);

        houseSelect.setRequired(true);

        final List<JsonObject> houseList = QueryService.getService().findAll(Events.FIND_ALL_HOUSES, new JsonObject());
        houseList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            houseSelect.addItem(objId);
            houseSelect.setItemCaption(objId, j.getString(QC.name));
        });

        final List<JsonObject> areaList = QueryService.getService().findAll(Events.FIND_ALL_AREAS, new JsonObject());
        areaList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            areaSelect.addItem(objId);
            areaSelect.setItemCaption(objId, j.getString(QC.name));
        });

        final List<JsonObject> regionList = QueryService.getService().findAll(Events.FIND_ALL_REGIONS, new JsonObject());
        regionList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            regionSelect.addItem(objId);
            regionSelect.setItemCaption(objId, j.getString(QC.name));
        });

        houseSelect.setValue(Zero);
        areaSelect.setValue(Zero);
        regionSelect.setValue(Zero);

        houseSelect.setWidth("100%");
        areaSelect.setWidth("100%");
        regionSelect.setWidth("100%");

        new SelectionService(houseList.stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)),
                areaList.stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)))
                .onAreaRegionSelection(houseSelect, areaSelect, regionSelect);

        final FormLayout form = formLayout()
                .addComponent(houseSelect)
                .addComponent(areaSelect)
                .addComponent(regionSelect)
                .spacing()
                .margin()
                .sizeFull()
                .get();
        return form;
    }

    private Component createACForm(Map<String, Field> fieldMap) {
        final Long Zero = 0L;
        final NativeSelect areaSelect = new NativeSelect("Area");
        final NativeSelect regionSelect = new NativeSelect("Region");

        areaSelect.addItem(Zero);
        regionSelect.addItem(Zero);
        areaSelect.setItemCaption(Zero, "Select Area");
        regionSelect.setItemCaption(Zero, "Select Region");

        areaSelect.setNullSelectionAllowed(false);
        regionSelect.setNullSelectionAllowed(false);

        areaSelect.setRequired(true);

        final List<JsonObject> areaList = QueryService.getService().findAll(Events.FIND_ALL_AREAS, new JsonObject());
        areaList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            areaSelect.addItem(objId);
            areaSelect.setItemCaption(objId, j.getString(QC.name));
        });

        final List<JsonObject> regionList = QueryService.getService().findAll(Events.FIND_ALL_REGIONS, new JsonObject());
        regionList.forEach(j -> {
            final Long objId = j.getLong(QC.id);
            regionSelect.addItem(objId);
            regionSelect.setItemCaption(objId, j.getString(QC.name));
        });

        areaSelect.setValue(Zero);
        regionSelect.setValue(Zero);

        areaSelect.setWidth("100%");
        regionSelect.setWidth("100%");

        new SelectionService(QueryService.getService()
                .findAll(Events.FIND_ALL_HOUSES, new JsonObject())
                .stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)),
                areaList.stream().collect(Collectors.toMap(j -> j.getLong(QC.id), j -> j)))
                .onAreaRegionSelection(areaSelect, regionSelect);

        final FormLayout form = formLayout()
                .addComponent(areaSelect)
                .addComponent(regionSelect)
                .spacing()
                .margin()
                .sizeFull()
                .get();
        return form;
    }

    private JsonObject user(long userTypeId, Map<String, Field> userMap) {
        final JsonObject user = new JsonObject();
        userMap.forEach((k, v) -> {
            final Object value = v.getValue();
            if (value instanceof Date) {
                user.put(k, toMongoDate((Date) value));
            } else {
                user.put(k, value);
            }
        });
        user.put(User.userType, userTypeId);
        return user;
    }

    private String okButtonText(final long userTypeId) {
        return userTypeId == EmployeeType.areaCoordinator.id
                || userTypeId == EmployeeType.brSupervisor.id
                || userTypeId == EmployeeType.br.id ? NEXT : CREATE;
    }

    private MutableTpl2<FormLayout, Map<String, Field>> userBasicForm() {

        final Map<String, Field> map = new LinkedHashMap<>();

        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setNullSettingAllowed(false);
        passwordField.setRequired(true);
        passwordField.setWidth("100%");

        final DateField dobField = new DateField("Date of Birth");
        dobField.setRequired(true);
        dobField.setWidth("100%");

        final DateField joinDate = new DateField("Join Date");
        joinDate.setRequired(true);
        joinDate.setWidth("100%");

        final MapBuilder<String, Field> mapBuilder = new MapBuilder<>(map);

        final FormLayout root = formLayout().spacing(true)
                .sizeFull()
                .spacing()
                .margin()
                .addComponent(
                        mapBuilder.putAndReturn(QC.name, textField("Name", "")
                                .nullSettingAllowed(false)
                                .required(true)
                                .width("100%")
                                .get()),
                        mapBuilder.putAndReturn(User.mobile, textField("Mobile", "")
                                .nullSettingAllowed(false)
                                .required()
                                .width("100%")
                                .get()),
                        mapBuilder.putAndReturn(User.mail, textField("Email", "")
                                .nullSettingAllowed(false)
                                .required()
                                .width("100%")
                                .get()),
                        mapBuilder.putAndReturn(User.username, textField("Username")
                                .required(false)
                                .nullSettingAllowed()
                                .width("100%")
                                .get()),
                        mapBuilder.putAndReturn(User.password, passwordField),
                        mapBuilder.putAndReturn(User.dateOfBirth, dobField),
                        mapBuilder.putAndReturn(User.joinDate, joinDate),
                        mapBuilder.putAndReturn(User.designation, textField("Designation")
                                .required()
                                .nullSettingAllowed(false)
                                .width("100%")
                                .get())
                )
                .get();

        return new MutableTpl2<>(root, map);
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
                                    case QC.name:
                                        errorMessages = list == null ? Resp.value_is_invalid : String.join("\n", list.stream().map(j -> asMap(j).get(QC.message) + "").collect(Collectors.toList()));
                                        nameField.setComponentError(VaadinUtil.errorMessage(errorMessages));
                                        break;
                                }
                            });
                        });
                        return;
                    }
                }
                ui.access(() -> {
                    Notification.show("Error: " + isEmptyOrNullOrSpaces(cause.getMessage()), Notification.Type.ERROR_MESSAGE);
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
            final String userId = user.getString(QC.userId);

            table.addItem(item(userId, user.getString(QC.name, ""), user.getString(User.mobile, ""),
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

    private NativeSelect userTypeSelect() {
        final JsonArray userTypes = findAllUserTypes();

        Long Zero = 0L;
        final NativeSelect nativeSelect = new NativeSelect("User Type");
        nativeSelect.addItem(Zero);

        userTypes.forEach(u -> {
            JsonObject userType = (JsonObject) u;
            final Long typeId = userType.getLong(QC.id);
            nativeSelect.addItem(typeId);
            nativeSelect.setItemCaption(typeId, String.format("%s (%s)", userType.getString(QC.name, ""),
                    userType.getString(QC.prefix, "")));
        });

        nativeSelect.setItemCaption(Zero, "Select User Type");
        nativeSelect.setValue(Zero);
        nativeSelect.setNullSelectionAllowed(false);
        return nativeSelect;
    }

    private void initUserTypeSelect() {
        userTypeSelect = new NativeSelect();
        final JsonArray userTypes = findAllUserTypes();
        final Long ZERO = 0L;
        userTypeSelect.addItem(ZERO);

        final SimpleCounter totalUsers = new SimpleCounter();
        userTypes.forEach(u -> {
            JsonObject userType = (JsonObject) u;
            final Long typeId = userType.getLong(QC.id);
            userTypeSelect.addItem(typeId);
            userTypeSelect.setItemCaption(typeId, String.format("%s (%s): %d", userType.getString(QC.name, ""),
                    userType.getString(QC.prefix, ""),
                    userType.getLong(QC.count, 0L)));
            totalUsers.counter += userType.getLong(QC.count, 0L);
        });
        userTypeSelect.setValue(ZERO);
        userTypeSelect.setItemCaption(ZERO, "All Users: " + totalUsers.counter);

        userTypeSelect.addValueChangeListener(e -> {
            final Long userTypeId = (Long) e.getProperty().getValue();

            if (userTypeId == null || userTypeId.longValue() <= 0) {
                filterAndUpdateTable(new JsonObject());
                return;
            }

            filterAndUpdateTable(new JsonObject().put(QC.userTypeId, userTypeId));
        });

        userTypeSelect.setNullSelectionAllowed(false);
    }

    private void filterAndUpdateTable(JsonObject criteria) {
        final UI ui = UI.getCurrent();
        bus.send(Events.FIND_ALL_EMPLOYEES, new JsonObject()
                .put(QC.params, criteria), (AsyncResult<Message<JsonArray>> r) -> {
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
        bus.send(Events.FIND_ALL_USER_TYPES, null, (AsyncResult<Message<JsonArray>> r) -> {
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
