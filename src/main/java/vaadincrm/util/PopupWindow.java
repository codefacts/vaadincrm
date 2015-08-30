package vaadincrm.util;

import com.vaadin.ui.*;

/**
 * Created by someone on 27/08/2015.
 */
public class PopupWindow {
    private final Window window;
    private final VerticalLayout content;
    private final HorizontalLayout footer;
    private final Label footerTextLabel;
    private final Button okButton;
    private final Button cancelButton;

    public PopupWindow(Window window, VerticalLayout content, HorizontalLayout footer, Label footerTextLabel, Button okButton, Button cancelButton) {
        this.window = window;
        this.content = content;
        this.footer = footer;
        this.footerTextLabel = footerTextLabel;
        this.okButton = okButton;
        this.cancelButton = cancelButton;
    }

    public Window getWindow() {
        return window;
    }

    public VerticalLayout getContent() {
        return content;
    }

    public HorizontalLayout getFooter() {
        return footer;
    }

    public Label getFooterTextLabel() {
        return footerTextLabel;
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
