package vaadincrm.service;

import com.vaadin.ui.UI;
import org.springframework.stereotype.Component;
import vaadincrm.Sessions;
import vaadincrm.domain.User;

@Component
public class AuthService {

    public boolean isAuthenticated() {
        return UI.getCurrent().getSession().getAttribute(Sessions.current_user) != null;
    }

    public User authenticate(String username, String password) {

        return new User("admin", "fa");
    }
}
