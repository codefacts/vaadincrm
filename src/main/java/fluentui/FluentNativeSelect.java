package fluentui;

import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.NativeSelect;
import io.vertx.core.json.JsonObject;
import vaadincrm.model.Query;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static vaadincrm.model.Query.caption;
import static vaadincrm.model.Query.id;

/**
 * Created by someone on 31/08/2015.
 */
public class FluentNativeSelect {
    /**
     * delegate
     */
    private final NativeSelect nativeSelect;

    /**
     * Hide constructor, use static factory methods.
     */
    private FluentNativeSelect(final NativeSelect nativeSelect) {
        this.nativeSelect = nativeSelect;
    }

    /**
     * @return the created instance
     */
    public final NativeSelect get() {
        return nativeSelect;
    }

    public static FluentNativeSelect nativeSelect() {
        return new FluentNativeSelect(new NativeSelect());
    }

    public static FluentNativeSelect nativeSelect(final String caption, final Collection<?> options) {
        return new FluentNativeSelect(new NativeSelect(caption, options));
    }

    public static FluentNativeSelect nativeSelect(final String caption, final Container dataSource) {
        return new FluentNativeSelect(new NativeSelect(caption, dataSource));
    }

    public static FluentNativeSelect nativeSelect(final String caption) {
        return new FluentNativeSelect(new NativeSelect(caption));
    }

    public FluentNativeSelect width(final String width) {
        nativeSelect.setWidth(width);
        return this;
    }

    public FluentNativeSelect enabled(final boolean enabled) {
        nativeSelect.setEnabled(enabled);
        return this;
    }

    /**
     * @return #enabled(true)
     */
    public FluentNativeSelect enabled() {
        return this.enabled(true);
    }

    public FluentNativeSelect invalidCommitted(final boolean invalidCommitted) {
        nativeSelect.setInvalidCommitted(invalidCommitted);
        return this;
    }

    /**
     * @return #invalidCommitted(true)
     */
    public FluentNativeSelect invalidCommitted() {
        return this.invalidCommitted(true);
    }

    public FluentNativeSelect locale(final Locale locale) {
        nativeSelect.setLocale(locale);
        return this;
    }

    public FluentNativeSelect addBlurListener(final com.vaadin.event.FieldEvents.BlurListener... blurListeners) {
        for (final com.vaadin.event.FieldEvents.BlurListener blurListener : blurListeners) {
            nativeSelect.addBlurListener(blurListener);
        }
        return this;
    }

    public FluentNativeSelect tabIndex(final int tabIndex) {
        nativeSelect.setTabIndex(tabIndex);
        return this;
    }

    public FluentNativeSelect data(final Object data) {
        nativeSelect.setData(data);
        return this;
    }

    public FluentNativeSelect nullSelectionItemId(final Object nullSelectionItemId) {
        nativeSelect.setNullSelectionItemId(nullSelectionItemId);
        return this;
    }

    public FluentNativeSelect addShortcutListener(final ShortcutListener... shortcutListeners) {
        for (final ShortcutListener shortcutListener : shortcutListeners) {
            nativeSelect.addShortcutListener(shortcutListener);
        }
        return this;
    }

    public FluentNativeSelect height(final String height) {
        nativeSelect.setHeight(height);
        return this;
    }

    public FluentNativeSelect addAttachListener(final com.vaadin.server.ClientConnector.AttachListener... attachListeners) {
        for (final com.vaadin.server.ClientConnector.AttachListener attachListener : attachListeners) {
            nativeSelect.addAttachListener(attachListener);
        }
        return this;
    }

    public FluentNativeSelect invalidAllowed(final boolean invalidAllowed) {
        nativeSelect.setInvalidAllowed(invalidAllowed);
        return this;
    }

    /**
     * @return #invalidAllowed(true)
     */
    public FluentNativeSelect invalidAllowed() {
        return this.invalidAllowed(true);
    }

    public FluentNativeSelect visible(final boolean visible) {
        nativeSelect.setVisible(visible);
        return this;
    }

    /**
     * @return #visible(true)
     */
    public FluentNativeSelect visible() {
        return this.visible(true);
    }

    public FluentNativeSelect itemIconPropertyId(final Object itemIconPropertyId) {
        nativeSelect.setItemIconPropertyId(itemIconPropertyId);
        return this;
    }

    public FluentNativeSelect containerDataSource(final Container containerDataSource) {
        nativeSelect.setContainerDataSource(containerDataSource);
        return this;
    }

    public FluentNativeSelect styleName(final String styleName) {
        nativeSelect.setStyleName(styleName);
        return this;
    }

    public FluentNativeSelect addPropertySetChangeListener(final Container.PropertySetChangeListener... propertySetChangeListeners) {
        for (final Container.PropertySetChangeListener propertySetChangeListener : propertySetChangeListeners) {
            nativeSelect.addPropertySetChangeListener(propertySetChangeListener);
        }
        return this;
    }

    public FluentNativeSelect required(final boolean required) {
        nativeSelect.setRequired(required);
        return this;
    }

    /**
     * @return #required(true)
     */
    public FluentNativeSelect required() {
        return this.required(true);
    }

    public FluentNativeSelect addValueChangeListener(final com.vaadin.data.Property.ValueChangeListener... valueChangeListeners) {
        for (final com.vaadin.data.Property.ValueChangeListener valueChangeListener : valueChangeListeners) {
            nativeSelect.addValueChangeListener(valueChangeListener);
        }
        return this;
    }

    public FluentNativeSelect addReadOnlyStatusChangeListener(final com.vaadin.data.Property.ReadOnlyStatusChangeListener... readOnlyStatusChangeListeners) {
        for (final com.vaadin.data.Property.ReadOnlyStatusChangeListener readOnlyStatusChangeListener : readOnlyStatusChangeListeners) {
            nativeSelect.addReadOnlyStatusChangeListener(readOnlyStatusChangeListener);
        }
        return this;
    }

    public FluentNativeSelect errorHandler(final ErrorHandler errorHandler) {
        nativeSelect.setErrorHandler(errorHandler);
        return this;
    }

    public FluentNativeSelect addValidator(final Validator... validators) {
        for (final Validator validator : validators) {
            nativeSelect.addValidator(validator);
        }
        return this;
    }

    public FluentNativeSelect itemCaptionPropertyId(final Object itemCaptionPropertyId) {
        nativeSelect.setItemCaptionPropertyId(itemCaptionPropertyId);
        return this;
    }

    public FluentNativeSelect componentError(final ErrorMessage componentError) {
        nativeSelect.setComponentError(componentError);
        return this;
    }

    public FluentNativeSelect nullSelectionAllowed(final boolean nullSelectionAllowed) {
        nativeSelect.setNullSelectionAllowed(nullSelectionAllowed);
        return this;
    }

    /**
     * @return #nullSelectionAllowed(true)
     */
    public FluentNativeSelect nullSelectionAllowed() {
        return this.nullSelectionAllowed(true);
    }

    public FluentNativeSelect convertedValue(final Object convertedValue) {
        nativeSelect.setConvertedValue(convertedValue);
        return this;
    }

    public FluentNativeSelect primaryStyleName(final String primaryStyleName) {
        nativeSelect.setPrimaryStyleName(primaryStyleName);
        return this;
    }

    public FluentNativeSelect icon(final Resource icon) {
        nativeSelect.setIcon(icon);
        return this;
    }

    public FluentNativeSelect addListener(final com.vaadin.ui.Component.Listener... listeners) {
        for (final com.vaadin.ui.Component.Listener listener : listeners) {
            nativeSelect.addListener(listener);
        }
        return this;
    }

    public FluentNativeSelect addFocusListener(final com.vaadin.event.FieldEvents.FocusListener... focusListeners) {
        for (final com.vaadin.event.FieldEvents.FocusListener focusListener : focusListeners) {
            nativeSelect.addFocusListener(focusListener);
        }
        return this;
    }

    public FluentNativeSelect addItemSetChangeListener(final Container.ItemSetChangeListener... itemSetChangeListeners) {
        for (final Container.ItemSetChangeListener itemSetChangeListener : itemSetChangeListeners) {
            nativeSelect.addItemSetChangeListener(itemSetChangeListener);
        }
        return this;
    }

    public FluentNativeSelect addItemWithCaption(final Object itemId, final String caption) {
        nativeSelect.addItem(itemId);
        nativeSelect.setItemCaption(itemId, caption);
        return this;
    }

    public FluentNativeSelect addItemWithCaption(final String caption) {
        return addItemWithCaption(caption, caption);
    }

    public FluentNativeSelect description(final String description) {
        nativeSelect.setDescription(description);
        return this;
    }

    public FluentNativeSelect conversionError(final String conversionError) {
        nativeSelect.setConversionError(conversionError);
        return this;
    }

    public FluentNativeSelect validationVisible(final boolean validationVisible) {
        nativeSelect.setValidationVisible(validationVisible);
        return this;
    }

    /**
     * @return #validationVisible(true)
     */
    public FluentNativeSelect validationVisible() {
        return this.validationVisible(true);
    }

    public FluentNativeSelect readOnly(final boolean readOnly) {
        nativeSelect.setReadOnly(readOnly);
        return this;
    }

    /**
     * @return #readOnly(true)
     */
    public FluentNativeSelect readOnly() {
        return this.readOnly(true);
    }

    public FluentNativeSelect converter(final Converter<Object, ?> converter) {
        nativeSelect.setConverter(converter);
        return this;
    }

    public FluentNativeSelect itemCaptionMode(final com.vaadin.ui.AbstractSelect.ItemCaptionMode itemCaptionMode) {
        nativeSelect.setItemCaptionMode(itemCaptionMode);
        return this;
    }

    public FluentNativeSelect addStyleName(final String... styleNames) {
        for (final String styleName : styleNames) {
            nativeSelect.addStyleName(styleName);
        }
        return this;
    }

    public FluentNativeSelect immediate(final boolean immediate) {
        nativeSelect.setImmediate(immediate);
        return this;
    }

    /**
     * @return #immediate(true)
     */
    public FluentNativeSelect immediate() {
        return this.immediate(true);
    }

    public FluentNativeSelect newItemHandler(final com.vaadin.ui.AbstractSelect.NewItemHandler newItemHandler) {
        nativeSelect.setNewItemHandler(newItemHandler);
        return this;
    }

    public FluentNativeSelect value(final Object value) {
        nativeSelect.setValue(value);
        return this;
    }

    public FluentNativeSelect addDetachListener(final com.vaadin.server.ClientConnector.DetachListener... detachListeners) {
        for (final com.vaadin.server.ClientConnector.DetachListener detachListener : detachListeners) {
            nativeSelect.addDetachListener(detachListener);
        }
        return this;
    }

    public FluentNativeSelect requiredError(final String requiredError) {
        nativeSelect.setRequiredError(requiredError);
        required(requiredError != null);
        return this;
    }

    public FluentNativeSelect buffered(final boolean buffered) {
        nativeSelect.setBuffered(buffered);
        return this;
    }

    /**
     * @return #buffered(true)
     */
    public FluentNativeSelect buffered() {
        return this.buffered(true);
    }

    public FluentNativeSelect caption(final String caption) {
        nativeSelect.setCaption(caption);
        return this;
    }

    public FluentNativeSelect newItemsAllowed(final boolean newItemsAllowed) {
        nativeSelect.setNewItemsAllowed(newItemsAllowed);
        return this;
    }

    /**
     * @return #newItemsAllowed(true)
     */
    public FluentNativeSelect newItemsAllowed() {
        return this.newItemsAllowed(true);
    }

    public FluentNativeSelect options(Collection<JsonObject> list) {
        list.forEach(j -> addItemWithCaption(j.getValue(id), j.getString(Query.caption)));
        return this;
    }
}
