package vaadincrm;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import vaadincrm.domain.User;
import vaadincrm.event.*;
import vaadincrm.service.AuthService;
import vaadincrm.view.LoginView;
import vaadincrm.view.MainView;

import java.util.Locale;

/**
 * Created by someone on 13-Jul-2015.
 */
@Theme("valo")
@SpringUI(path = "")
@Push
public class AppUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (!VaadinService.getCurrent().ensurePushAvailable()) {
            throw new IllegalStateException("Serverpush not available.");
        }

        setLocale(Locale.US);

        DashboardEventBus.register(this);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                event -> DashboardEventBus.post(new BrowserResizeEvent()));
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        final AuthService authService = App.ctx.getBean(AuthService.class);
        if (authService.isAuthenticated()) {
            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        final AuthService authService = App.ctx.getBean(AuthService.class);
        final User user = authService.authenticate(event.getUserName(), event.getPassword());
        VaadinSession.getCurrent().setAttribute(Sessions.current_user, user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {

        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }
}
