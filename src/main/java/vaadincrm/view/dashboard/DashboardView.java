package vaadincrm.view.dashboard;

import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.crm.Events;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.event.CloseOpenWindowsEvent;
import vaadincrm.event.DashboardEventBus;
import vaadincrm.model.Query;
import vaadincrm.view.campaign.ConfigureCampaignTree;

public class DashboardView extends Panel implements View {
    public static final String TITLE_ID = "dashboard-title";

    private Label titleLabel;
    private CssLayout dashboardPanels;
    private final VerticalLayout root;
    private ConfigureCampaignTree dbTreeTable;

    private Button regionCountButton;
    private Button areaCountButton;
    private Button houseCountButton;
    private Button brCountButton;
    private Button locationCountButton;

    private boolean initialized = false;

    public DashboardView() {
        root = new VerticalLayout();
    }

    public void initialize() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root.setSizeFull();
        root.setMargin(true);

        setContent(root);
        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());

        root.addComponent(buildSparklines());

        com.vaadin.ui.Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });

        retrieveAndPopulateData();
    }

    private Component buildSparklines() {
        HorizontalLayout sparks = new HorizontalLayout();
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);

        sparks.addComponents(regionCountButton = new Button("Region"), areaCountButton = new Button("Area"),
                houseCountButton = new Button("House"), locationCountButton = new Button("Location"));
        sparks.addComponents(brCountButton = new Button("BR"));

        sparks.setSpacing(true);
        sparks.setHeight(50, Unit.PIXELS);

        return sparks;
    }

    private com.vaadin.ui.Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.setSizeFull();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dbTreeTable = new ConfigureCampaignTree();
        dashboardPanels.addComponent(dbTreeTable.init());

        return dashboardPanels;
    }

    private void retrieveAndPopulateData() {
        final UI ui = UI.getCurrent();
        App.bus.send(Events.GET_DB_TREE_WITH_USERS, null, (AsyncResult<Message<JsonObject>> r) -> {
            if (r.failed()) {
                throw new RuntimeException(r.cause());
            }

            final JsonObject tree = r.result().body();

            ui.access(() -> {
                regionCountButton.setImmediate(true);
                regionCountButton.setCaption("Region: " + tree.getInteger(Query.regionCount));

                areaCountButton.setImmediate(true);
                areaCountButton.setCaption("Area: " + tree.getInteger(Query.areaCount));

                houseCountButton.setImmediate(true);
                houseCountButton.setCaption("House: " + tree.getInteger(Query.houseCount));

                brCountButton.setImmediate(true);
                brCountButton.setCaption("BR: " + tree.getInteger(Query.brCount));

                locationCountButton.setImmediate(true);
                locationCountButton.setCaption("Location: " + tree.getInteger(Query.locationCount));

                dbTreeTable.populateData(tree);
            });

        });
    }

    private com.vaadin.ui.Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Dashboard");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H4);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!initialized) {
            initialize();
            initialized = true;
        }
    }

    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            DashboardEventBus.register(this);
        }
    }
}
