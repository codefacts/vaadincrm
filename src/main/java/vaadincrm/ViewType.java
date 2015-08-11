package vaadincrm;

import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import vaadincrm.view.dashboard.DashboardView;

/**
 * Created by someone on 28-Jul-2015.
 */
public enum ViewType {
    DASHBOARD("index", "dashboard", "Dashboard", null, DashboardView.class, true);

    public final String value;
    public final String viewName;
    public final String label;
    private final Resource icon;
    private final Class<? extends View> viewClass;
    private final boolean stateful;

    ViewType(String value, String viewName, String label, Resource icon, Class<? extends View> viewClass, boolean stateful) {
        this.value = value;
        this.viewName = viewName;
        this.label = label;
        this.icon = icon;
        this.viewClass = viewClass;
        this.stateful = stateful;
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
}
