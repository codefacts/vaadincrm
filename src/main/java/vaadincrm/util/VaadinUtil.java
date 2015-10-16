package vaadincrm.util;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.*;
import io.crm.intfs.*;
import io.crm.util.ExceptionUtil;
import vaadincrm.Resp;

import java.util.*;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Created by someone on 18/08/2015.
 */
final public class VaadinUtil {
    public static final float DEFAULT_DIALOG_WINDOW_HEIGHT = 400.0f;
    public static final float DEFAULT_DIALOG_WINDOW_WIDTH = 800.0f;

    public static Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

    public static ErrorMessage errorMessage(final String errorMessages) {
        System.out.println("Error Messages: " + errorMessages);
        return new AbstractErrorMessage(errorMessages) {
        };
    }

    public static void handleError(final Throwable throwable) {
        Notification.show(Resp.server_error_pleasy_try_again_later, Notification.Type.ERROR_MESSAGE);
        ExceptionUtil.logException(throwable);
    }

    public static void showConfirmDialog(final String title, final Component dialogContent, final ConsumerUnchecked<Window> onClose) {
        showConfirmDialog(title, dialogContent, onClose, DEFAULT_DIALOG_WINDOW_WIDTH, DEFAULT_DIALOG_WINDOW_HEIGHT);
    }

    public static void showConfirmDialog(final String title, final Component content, final ConsumerUnchecked<Window> onClose, final float width, final float height) {
        final Window window = new Window(title);
        window.setWidth(width, PIXELS);
        window.setHeight(height, PIXELS);
        window.center();

        final VerticalLayout root = new VerticalLayout();

        root.setSizeFull();
        root.setMargin(true);

        root.addComponents(content, okFooter(window, onClose));
        root.setExpandRatio(content, 1);

        window.setContent(root);
        UI.getCurrent().addWindow(window);
    }

    public static void showYesNoDialog(final String title, final Component content, final ConsumerUnchecked<Boolean> onComplete) {
        showOkCancelDialog(title, content, onComplete, DEFAULT_DIALOG_WINDOW_WIDTH, DEFAULT_DIALOG_WINDOW_HEIGHT, "Yes", "No");
    }

    public static void showOkCancelDialog(final String title, final Component content, final ConsumerUnchecked<Boolean> onComplete) {
        showOkCancelDialog(title, content, onComplete, DEFAULT_DIALOG_WINDOW_WIDTH, DEFAULT_DIALOG_WINDOW_HEIGHT, "Ok", "Cancel");
    }

    public static void showYesNoDialog(final String title, final Component content, final ConsumerUnchecked<Boolean> onComplete,
                                       final float width, final float height) {
        showOkCancelDialog(title, content, onComplete, width, height, "Yes", "No");
    }

    public static void showOkCancelDialog(final String title, final Component content, final ConsumerUnchecked<Boolean> onComplete,
                                          final float width, final float height) {
        showOkCancelDialog(title, content, onComplete, width, height, "Ok", "Cancel");
    }

    public static void showOkCancelDialog(final String title, final Component content, final ConsumerUnchecked<Boolean> anInterface,
                                          final float width, final float height, final String okButtonText, final String cancelButtonText) {
        final Window window = new Window(title);
        window.setWidth(width, PIXELS);
        window.setHeight(height, PIXELS);
        window.center();

        final VerticalLayout root = new VerticalLayout();

        root.setSizeFull();
        root.setMargin(true);

        root.addComponents(content, okCancelFooter(okButtonText, cancelButtonText, anInterface));
        root.setExpandRatio(content, 1);

        window.setContent(root);
        UI.getCurrent().addWindow(window);
    }

    private static Component okFooter(final Window window, final ConsumerUnchecked<Window> onClose) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName("v-window-bottom-toolbar");

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button ok = new Button("OK");
        ok.addStyleName("primary");
        ok.addClickListener(e -> {
            try {
                onClose.accept(window);
            } catch (Exception e1) {
                VaadinUtil.handleError(e1);
            }
        });

        footer.addComponents(footerText, ok);
        footer.setExpandRatio(footerText, 1);
        return footer;
    }

    public static Component okCancelFooter(final String okButtonText, final String cancelButtonText, final ConsumerUnchecked<Boolean> onComplete) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName("v-window-bottom-toolbar");

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button ok = new Button(okButtonText);
        ok.addStyleName("primary");
        ok.addClickListener(e -> {
            try {
                onComplete.accept(true);
            } catch (Exception e1) {
                VaadinUtil.handleError(e1);
            }
        });

        Button cancel = new Button(cancelButtonText);
        cancel.addClickListener(e -> {
            try {
                onComplete.accept(false);
            } catch (Exception e1) {
                VaadinUtil.handleError(e1);
            }
        });

        footer.addComponents(footerText, ok, cancel);
        footer.setExpandRatio(footerText, 1);
        return footer;
    }

    public static String p(final String text) {
        return String.format("<p>%s</p>", text);
    }
}
