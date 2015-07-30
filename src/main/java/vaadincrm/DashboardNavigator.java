package vaadincrm;

import com.vaadin.navigator.*;
import com.vaadin.navigator.View;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import vaadincrm.event.BrowserResizeEvent;
import vaadincrm.event.CloseOpenWindowsEvent;
import vaadincrm.event.DashboardEventBus;
import vaadincrm.event.PostViewChangeEvent;

public class DashboardNavigator extends Navigator {

    private static final ViewType ERROR_VIEW = ViewType.DASHBOARD;
    private ViewProvider errorViewProvider;

    public DashboardNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);

        initViewChangeListener();
        initViewProviders();

    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                ViewType view = ViewType.getByViewName(event
                        .getViewName());
                // Appropriate events get fired after the view is changed.
                DashboardEventBus.post(new PostViewChangeEvent(view));
                DashboardEventBus.post(new BrowserResizeEvent());
                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private void initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (final ViewType viewType : ViewType.values()) {
            ViewProvider viewProvider = new ClassBasedViewProvider(
                    viewType.getViewName(), viewType.getViewClass()) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private com.vaadin.navigator.View cachedInstance;

                @Override
                public com.vaadin.navigator.View getView(final String viewName) {
                    com.vaadin.navigator.View result = null;
                    if (viewType.getViewName().equals(viewName)) {
                        if (viewType.isStateful()) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = super.getView(viewType
                                        .getViewName());
                            }
                            result = cachedInstance;
                        } else {
                            // Non-stateful views get instantiated every time
                            // they're navigated to
                            result = super.getView(viewType.getViewName());
                        }
                    }
                    return result;
                }
            };

            if (viewType == ERROR_VIEW) {
                errorViewProvider = viewProvider;
            }

            addProvider(viewProvider);
        }

        setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public com.vaadin.navigator.View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName());
            }
        });
    }
}
