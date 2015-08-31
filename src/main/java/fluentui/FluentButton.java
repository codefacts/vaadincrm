package fluentui;

import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by someone on 31/08/2015.
 */
public class FluentButton {
    /**
     * delegate
     */
    private final Button button;

    /**
     * Hide constructor, use static factory methods.
     */
    private FluentButton(final Button button) {
        this.button = button;
    }

    /**
     * @return the created instance
     */
    public final Button get() {
        return button;
    }

    public static FluentButton button() {
        return new FluentButton(new Button());
    }

    public static FluentButton button(final String caption, final Button.ClickListener clickListener) {
        return new FluentButton(new Button(caption, clickListener));
    }

    public static FluentButton button(final String caption) {
        return new FluentButton(new Button(caption));
    }

    public FluentButton width(final String width) {
        button.setWidth(width);
        return this;
    }

    public FluentButton enabled(final boolean enabled) {
        button.setEnabled(enabled);
        return this;
    }

    /**
     * @return #enabled(true)
     */
    public FluentButton enabled() {
        return this.enabled(true);
    }

    public FluentButton locale(final Locale locale) {
        button.setLocale(locale);
        return this;
    }

    public FluentButton addBlurListener(final com.vaadin.event.FieldEvents.BlurListener... blurListeners) {
        for (final com.vaadin.event.FieldEvents.BlurListener blurListener : blurListeners) {
            button.addBlurListener(blurListener);
        }
        return this;
    }

    public FluentButton tabIndex(final int tabIndex) {
        button.setTabIndex(tabIndex);
        return this;
    }

    public FluentButton data(final Object data) {
        button.setData(data);
        return this;
    }

    public FluentButton addShortcutListener(final ShortcutListener... shortcutListeners) {
        for (final ShortcutListener shortcutListener : shortcutListeners) {
            button.addShortcutListener(shortcutListener);
        }
        return this;
    }

    public FluentButton height(final String height) {
        button.setHeight(height);
        return this;
    }

    public FluentButton addAttachListener(final com.vaadin.server.ClientConnector.AttachListener... attachListeners) {
        for (final com.vaadin.server.ClientConnector.AttachListener attachListener : attachListeners) {
            button.addAttachListener(attachListener);
        }
        return this;
    }

    public FluentButton visible(final boolean visible) {
        button.setVisible(visible);
        return this;
    }

    /**
     * @return #visible(true)
     */
    public FluentButton visible() {
        return this.visible(true);
    }

    public FluentButton styleName(final String styleName) {
        button.setStyleName(styleName);
        return this;
    }

    public FluentButton errorHandler(final ErrorHandler errorHandler) {
        button.setErrorHandler(errorHandler);
        return this;
    }

    public FluentButton componentError(final ErrorMessage componentError) {
        button.setComponentError(componentError);
        return this;
    }

    public FluentButton primaryStyleName(final String primaryStyleName) {
        button.setPrimaryStyleName(primaryStyleName);
        return this;
    }

    public FluentButton icon(final Resource icon) {
        button.setIcon(icon);
        return this;
    }

    public FluentButton addListener(final com.vaadin.ui.Component.Listener... listeners) {
        for (final com.vaadin.ui.Component.Listener listener : listeners) {
            button.addListener(listener);
        }
        return this;
    }

    public FluentButton addFocusListener(final com.vaadin.event.FieldEvents.FocusListener... focusListeners) {
        for (final com.vaadin.event.FieldEvents.FocusListener focusListener : focusListeners) {
            button.addFocusListener(focusListener);
        }
        return this;
    }

    public FluentButton description(final String description) {
        button.setDescription(description);
        return this;
    }

    public FluentButton readOnly(final boolean readOnly) {
        button.setReadOnly(readOnly);
        return this;
    }

    /**
     * @return #readOnly(true)
     */
    public FluentButton readOnly() {
        return this.readOnly(true);
    }

    public FluentButton addStyleName(final String... styleNames) {
        for (final String styleName : styleNames) {
            button.addStyleName(styleName);
        }
        return this;
    }

    public FluentButton immediate(final boolean immediate) {
        button.setImmediate(immediate);
        return this;
    }

    /**
     * @return #immediate(true)
     */
    public FluentButton immediate() {
        return this.immediate(true);
    }

    public FluentButton addDetachListener(final com.vaadin.server.ClientConnector.DetachListener... detachListeners) {
        for (final com.vaadin.server.ClientConnector.DetachListener detachListener : detachListeners) {
            button.addDetachListener(detachListener);
        }
        return this;
    }

    public FluentButton caption(final String caption) {
        button.setCaption(caption);
        return this;
    }
}
