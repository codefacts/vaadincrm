package fluentui;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.ui.DateField;

import java.util.Date;
import java.util.Locale;

/**
 * Created by someone on 31/08/2015.
 */
public class FluentDateField {
    /**
     * delegate
     */
    private final DateField dateField;

    /**
     * Hide constructor, use static factory methods.
     */
    private FluentDateField(final DateField dateField) {
        this.dateField = dateField;
    }

    /**
     * @return the created instance
     */
    public final DateField get() {
        return dateField;
    }

    public static FluentDateField dateField() {
        return new FluentDateField(new DateField());
    }

    public static FluentDateField dateField(final String string0) {
        return new FluentDateField(new DateField(string0));
    }

    public static FluentDateField dateField(final Property<?> property0) {
        return new FluentDateField(new DateField(property0));
    }

    public static FluentDateField dateField(final String string0, final Property<?> property1) {
        return new FluentDateField(new DateField(string0, property1));
    }

    public static FluentDateField dateField(final String string0, final Date date) {
        return new FluentDateField(new DateField(string0, date));
    }

    public FluentDateField width(final String width) {
        dateField.setWidth(width);
        return this;
    }

    public FluentDateField enabled(final boolean enabled) {
        dateField.setEnabled(enabled);
        return this;
    }

    /**
     * @return #enabled(true)
     */
    public FluentDateField enabled() {
        return this.enabled(true);
    }

    public FluentDateField invalidCommitted(final boolean invalidCommitted) {
        dateField.setInvalidCommitted(invalidCommitted);
        return this;
    }

    /**
     * @return #invalidCommitted(true)
     */
    public FluentDateField invalidCommitted() {
        return this.invalidCommitted(true);
    }

    public FluentDateField addValueChangeListener(final Property.ValueChangeListener... valueChangeListeners) {
        for (final Property.ValueChangeListener valueChangeListener : valueChangeListeners) {
            dateField.addValueChangeListener(valueChangeListener);
        }
        return this;
    }

    public FluentDateField locale(final Locale locale) {
        dateField.setLocale(locale);
        return this;
    }

    public FluentDateField tabIndex(final int tabIndex) {
        dateField.setTabIndex(tabIndex);
        return this;
    }

    public FluentDateField addReadOnlyStatusChangeListener(final Property.ReadOnlyStatusChangeListener... readOnlyStatusChangeListeners) {
        for (final Property.ReadOnlyStatusChangeListener readOnlyStatusChangeListener : readOnlyStatusChangeListeners) {
            dateField.addReadOnlyStatusChangeListener(readOnlyStatusChangeListener);
        }
        return this;
    }

    public FluentDateField errorHandler(final ErrorHandler errorHandler) {
        dateField.setErrorHandler(errorHandler);
        return this;
    }

    public FluentDateField addValidator(final Validator... validators) {
        for (final Validator validator : validators) {
            dateField.addValidator(validator);
        }
        return this;
    }

    public FluentDateField data(final Object data) {
        dateField.setData(data);
        return this;
    }

    public FluentDateField componentError(final ErrorMessage componentError) {
        dateField.setComponentError(componentError);
        return this;
    }

    public FluentDateField value(final Date value) {
        dateField.setValue(value);
        return this;
    }

    public FluentDateField convertedValue(final Object convertedValue) {
        dateField.setConvertedValue(convertedValue);
        return this;
    }

    public FluentDateField primaryStyleName(final String primaryStyleName) {
        dateField.setPrimaryStyleName(primaryStyleName);
        return this;
    }

    public FluentDateField addShortcutListener(final ShortcutListener... shortcutListeners) {
        for (final ShortcutListener shortcutListener : shortcutListeners) {
            dateField.addShortcutListener(shortcutListener);
        }
        return this;
    }

    public FluentDateField icon(final Resource icon) {
        dateField.setIcon(icon);
        return this;
    }

    public FluentDateField height(final String height) {
        dateField.setHeight(height);
        return this;
    }

    public FluentDateField addListener(final com.vaadin.ui.Component.Listener... listeners) {
        for (final com.vaadin.ui.Component.Listener listener : listeners) {
            dateField.addListener(listener);
        }
        return this;
    }

    public FluentDateField addAttachListener(final com.vaadin.server.ClientConnector.AttachListener... attachListeners) {
        for (final com.vaadin.server.ClientConnector.AttachListener attachListener : attachListeners) {
            dateField.addAttachListener(attachListener);
        }
        return this;
    }

    public FluentDateField invalidAllowed(final boolean invalidAllowed) {
        dateField.setInvalidAllowed(invalidAllowed);
        return this;
    }

    /**
     * @return #invalidAllowed(true)
     */
    public FluentDateField invalidAllowed() {
        return this.invalidAllowed(true);
    }

    public FluentDateField description(final String description) {
        dateField.setDescription(description);
        return this;
    }

    public FluentDateField conversionError(final String conversionError) {
        dateField.setConversionError(conversionError);
        return this;
    }

    public FluentDateField validationVisible(final boolean validationVisible) {
        dateField.setValidationVisible(validationVisible);
        return this;
    }

    /**
     * @return #validationVisible(true)
     */
    public FluentDateField validationVisible() {
        return this.validationVisible(true);
    }

    public FluentDateField readOnly(final boolean readOnly) {
        dateField.setReadOnly(readOnly);
        return this;
    }

    /**
     * @return #readOnly(true)
     */
    public FluentDateField readOnly() {
        return this.readOnly(true);
    }

    public FluentDateField visible(final boolean visible) {
        dateField.setVisible(visible);
        return this;
    }

    /**
     * @return #visible(true)
     */
    public FluentDateField visible() {
        return this.visible(true);
    }

    public FluentDateField addFocusListener(final com.vaadin.event.FieldEvents.FocusListener... focusListeners) {
        for (final com.vaadin.event.FieldEvents.FocusListener focusListener : focusListeners) {
            dateField.addFocusListener(focusListener);
        }
        return this;
    }

    public FluentDateField addStyleName(final String... styleNames) {
        for (final String styleName : styleNames) {
            dateField.addStyleName(styleName);
        }
        return this;
    }

    public FluentDateField immediate(final boolean immediate) {
        dateField.setImmediate(immediate);
        return this;
    }

    /**
     * @return #immediate(true)
     */
    public FluentDateField immediate() {
        return this.immediate(true);
    }

    public FluentDateField addBlurListener(final com.vaadin.event.FieldEvents.BlurListener... blurListeners) {
        for (final com.vaadin.event.FieldEvents.BlurListener blurListener : blurListeners) {
            dateField.addBlurListener(blurListener);
        }
        return this;
    }

    public FluentDateField addDetachListener(final com.vaadin.server.ClientConnector.DetachListener... detachListeners) {
        for (final com.vaadin.server.ClientConnector.DetachListener detachListener : detachListeners) {
            dateField.addDetachListener(detachListener);
        }
        return this;
    }

    public FluentDateField requiredError(final String requiredError) {
        dateField.setRequiredError(requiredError);
        required(requiredError != null);
        return this;
    }

    public FluentDateField buffered(final boolean buffered) {
        dateField.setBuffered(buffered);
        return this;
    }

    /**
     * @return #buffered(true)
     */
    public FluentDateField buffered() {
        return this.buffered(true);
    }

    public FluentDateField caption(final String caption) {
        dateField.setCaption(caption);
        return this;
    }

    public FluentDateField styleName(final String styleName) {
        dateField.setStyleName(styleName);
        return this;
    }

    public FluentDateField required(final boolean required) {
        dateField.setRequired(required);
        return this;
    }

    /**
     * @return #required(true)
     */
    public FluentDateField required() {
        return this.required(true);
    }
}
