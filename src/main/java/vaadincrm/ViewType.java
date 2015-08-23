package vaadincrm;

import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import vaadincrm.view.area.AreaView;
import vaadincrm.view.region.RegionView;
import vaadincrm.view.brand.BrandView;
import vaadincrm.view.client.ClientView;
import vaadincrm.view.collection.CollectionView;
import vaadincrm.view.contact.ContactView;
import vaadincrm.view.dashboard.DashboardView;
import vaadincrm.view.employee.EmployeeView;
import vaadincrm.view.house.HouseView;
import vaadincrm.view.location.LocationView;
import vaadincrm.view.usertype.UserTypeView;

/**
 * Created by someone on 28-Jul-2015.
 */
public enum ViewType {

    REGION("collection/region", "Region", null, RegionView.class, true, false),

    AREA("collection/area", "Area", null, AreaView.class, true, false),

    HOUSE("collection/distributionHouse", "Distribution House", null, HouseView.class, true, false),

    LOCATION("collection/location", "Location", null, LocationView.class, true, false),

    BRAND("collection/brand", "Brand", null, BrandView.class, true, false),

    EMPLOYEE("collection/employee", "Employee User", null, EmployeeView.class, true, false),

    CLIENT("collection/client", "Client User", null, ClientView.class, true, false),

    CONTACT("collection/contact", "Consumer Contact", null, ContactView.class, true, false),

    USER_TYPE("collection/userType", "User Type", null, UserTypeView.class, true, false),

    COLLECTION("collection", "Collection", null, CollectionView.class, true, true),
    DASHBOARD("dashboard", "Dashboard", null, DashboardView.class, true, true);

    private final String viewName;
    private final String label;
    private final Resource icon;
    private final Class<? extends View> viewClass;
    private final boolean stateful;
    private final boolean showInMenu;

    ViewType(String viewName, String label, Resource icon, Class<? extends View> viewClass, boolean stateful, boolean showInMenu) {
        this.viewName = viewName;
        this.label = label;
        this.icon = icon;
        this.viewClass = viewClass;
        this.stateful = stateful;
        this.showInMenu = showInMenu;
    }

    public static ViewType getByViewName(final String viewName) {
        ViewType result = null;
        for (ViewType typeType : values()) {
            if (typeType.viewName.equals(viewName)) {
                result = typeType;
                break;
            }
        }
        return result;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public boolean isStateful() {
        return stateful;
    }

    public Resource getIcon() {
        return icon;
    }

    public boolean isShowInMenu() {
        return showInMenu;
    }

    public String getLabel() {
        return label;
    }
}
