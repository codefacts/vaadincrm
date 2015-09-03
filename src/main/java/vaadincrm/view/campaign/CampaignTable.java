package vaadincrm.view.campaign;

import com.vaadin.event.Action;
import com.vaadin.ui.*;
import fluentui.FluentFormLayout;
import io.crm.Events;
import io.crm.FailureCode;
import io.crm.util.Touple1;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.Resp;
import vaadincrm.model.Campaign;
import vaadincrm.model.Query;
import vaadincrm.service.QueryService;
import vaadincrm.util.MapBuilder;
import vaadincrm.util.PopupWindow;
import vaadincrm.util.PopupWindowBuilder;
import vaadincrm.util.VaadinUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION;
import static com.vaadin.ui.Notification.show;
import static fluentui.FluentButton.button;
import static fluentui.FluentDateField.dateField;
import static fluentui.FluentNativeSelect.nativeSelect;
import static fluentui.FluentUI.textField;
import static io.crm.util.Util.isEmptyOrNull;
import static io.crm.util.Util.parseMongoDate;
import static io.crm.util.Util.toMongoDate;
import static vaadincrm.App.bus;
import static io.crm.Events.CREATE_CAMPAIGN;
import static io.crm.Events.FIND_ALL_BRANDS;
import static vaadincrm.model.Campaign.*;
import static vaadincrm.model.Model.id;
import static vaadincrm.util.VaadinUtil.asMap;
import static vaadincrm.util.VaadinUtil.handleError;

/**
 * Created by someone on 30/08/2015.
 */
final public class CampaignTable {
    private static final String collection = "Campaign";

    private static final String ID_PROPERTY = "Id";
    private static final Object NAME_PROPERTY = "Name";
    private static final Object BRAND_PROPERTY = "Brand";
    private static final Object SALARY_START_DATE_PROPERTY = "Salary Start Date";
    private static final Object SALARY_END_DATE_PROPERTY = "Salary End Date";
    private static final Object LAUNCH_DATE_PROPERTY = "Launch Date";
    private static final Object CLOSE_DATE_PROPERTY = "Close Date";

    private static final Action ADD_ITEM_ACTION = new Action("Add new " + collection);
    private static final Action EDIT_ITEM_ACTION = new Action("Edit this " + collection);
    private static final Action VIEW_ITEM_ACTION = new Action("View this " + collection);
    private static final String UPDATE_REQUEST = Events.UPDATE_CAMPAIGN;
    private static final String CREATE_REQUEST = CREATE_CAMPAIGN;
    private static final String NEXT = "Next";
    private static final String FINISH = "Finish";
    private static final float WINDOW_CONTENT_HEIGHT = 490;

    private String params;

    private final Table table = new Table();
    private final Map<Long, JsonObject> campaignMap = new HashMap<>();

    public CampaignTable(String params) {
        this.params = params;
        System.out.println(String.format("params: %s", params));
    }

    public CampaignTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(ID_PROPERTY, Long.class, 0);
        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(BRAND_PROPERTY, String.class, "");

        table.addContainerProperty(SALARY_START_DATE_PROPERTY, Date.class, null);
        table.addContainerProperty(SALARY_END_DATE_PROPERTY, Date.class, null);
        table.addContainerProperty(LAUNCH_DATE_PROPERTY, Date.class, null);
        table.addContainerProperty(CLOSE_DATE_PROPERTY, Date.class, null);

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
                    editItemForm(campaignMap.get(target));
                } else if (action == VIEW_ITEM_ACTION) {
                    viewItemForm(campaignMap.get((Long) target));
                }
            }
        });

        return this;
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
            bus.send(UPDATE_REQUEST, new JsonObject().put(id, area.getLong(id))
                    .put(Query.name, nameField.getValue()), respond(ui, window, nameField, collection + "updated successfully."));
        });
        form.addComponent(updateButton);

        ui.addWindow(window);
    }

    private void addItemForm() {
        final UI ui = UI.getCurrent();
        final LinkedHashMap<String, Field> map = new LinkedHashMap<>();
        final MapBuilder<String, Field> mapBuilder = new MapBuilder<>(map);

        final Touple1<PopupWindow> touple1 = new Touple1<>();

        final PopupWindow popupWindow = touple1.t1 = PopupWindowBuilder.create("Create Campaign")
                .height(600, PIXELS)
                .content(new PopupWindowBuilder.ContentBuilder()
                        .addContent(FluentFormLayout.formLayout()
                                .sizeFull()
                                .margin()
                                .spacing()
                                .addComponent(
                                        mapBuilder.putAndReturn(Query.name, textField("Name", "")
                                                .width("100%")
                                                .required()
                                                .get()),
                                        mapBuilder.putAndReturn(Campaign.brand, nativeSelect("Select Brand")
                                                .width("100%")
                                                .required()
                                                .addItemWithCaption(0L, "Select Brand")
                                                .options(QueryService.getService().findAll(FIND_ALL_BRANDS, new JsonObject())
                                                        .stream().map(j -> new JsonObject().put(Query.id, j.getLong(Query.id)).put(Query.caption, j.getString(Query.name)))
                                                        .collect(Collectors.toList()))
                                                .value(0L)
                                                .nullSelectionAllowed(false)
                                                .get()),
                                        mapBuilder.putAndReturn(salaryStartDate, dateField("Salary Start Date")
                                                .width("100%")
                                                .get()),
                                        mapBuilder.putAndReturn(salaryEndDate, dateField("Salary End Date")
                                                .width("100%")
                                                .get()),
                                        mapBuilder.putAndReturn(launchDate, dateField("Launch Date")
                                                .width("100%")
                                                .get()),
                                        mapBuilder.putAndReturn(closeDate, dateField("Close Date")
                                                .width("100%")
                                                .get())
                                )
                                .get())
                        .footer(new PopupWindowBuilder.ContentBuilder.FooterBuilder("")
                                .okButton(NEXT,
                                        e -> {
                                            touple1.t1.getContent().removeAllComponents();
                                            final ConfigureCampaignTree configureTree = new ConfigureCampaignTree();
                                            final TreeTable treeTable = configureTree.init();
                                            treeTable.setWidth("100%");
                                            touple1.t1.getContent().addComponent(treeTable);
                                            configureTree.populateData(QueryService.getService().getDBTreeWithUsers());
                                            touple1.t1.getFooter().removeComponent(touple1.t1.getOkButton());


                                        })
                                .addComponent(button(FINISH,
                                        e -> {
                                            final JsonObject campaign = new JsonObject();
                                            map.forEach((k, v) -> {
                                                final Object value = v.getValue();
                                                if (value instanceof Date) {
                                                    campaign.put(k, toMongoDate((Date) value, null));
                                                } else campaign.put(k, value);
                                            });
                                            bus.send(CREATE_CAMPAIGN, campaign, r -> {
                                                ui.access(() -> {
                                                    if (r.failed()) {
                                                        handleError(r.cause());
                                                        return;
                                                    }
                                                    touple1.t1.getWindow().close();
                                                    show("Campaign created successfully.", TRAY_NOTIFICATION);
                                                });
                                            });
                                        })
                                        .addStyleName("primary")
                                        .get())
                                .cancelButton()))
                .build();

        ui.addWindow(popupWindow.getWindow());
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
                    show("Error: " + isEmptyOrNull(cause.getMessage()) + " Please try again.", Notification.Type.ERROR_MESSAGE);
                    window.close();
                });
                return;
            }
            ui.access(() -> {
                show(successMessage, TRAY_NOTIFICATION);
                window.close();
            });
        };
    }

    public void populateData(final JsonArray data) {
        campaignMap.clear();
        data.forEach(v -> {
            JsonObject campaign = (JsonObject) v;
            final Long campaignId = campaign.getLong(id);
            table.addItem(item(campaignId, campaign.getString(Query.name, ""),
                    campaign.getJsonObject(Query.brand, new JsonObject()).getString(Query.name, ""),
                    parseMongoDate(campaign.getJsonObject(salaryStartDate), null),
                    parseMongoDate(campaign.getJsonObject(salaryEndDate), null),
                    parseMongoDate(campaign.getJsonObject(launchDate), null),
                    parseMongoDate(campaign.getJsonObject(closeDate), null)), campaignId);
            campaignMap.put(campaignId, campaign);
        });
    }

    private Object[] item(final Long id, final String name, final String brand, final Date salaryStartDate, final Date salaryEndDate, final Date lauchDate, final Date closeDate) {
        return new Object[]{id, name, brand, salaryStartDate, salaryEndDate, lauchDate, closeDate};
    }

    public Table getTable() {
        return table;
    }
}
