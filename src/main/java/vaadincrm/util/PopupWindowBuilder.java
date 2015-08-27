package vaadincrm.util;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import java.util.Map;

/**
 * Created by someone on 27/08/2015.
 */
public class PopupWindowBuilder {
    private Window window;

    public PopupWindowBuilder setHeight(float height, Sizeable.Unit unit) {
        window.setHeight(height, unit);
        return this;
    }

    public PopupWindowBuilder setWidth(float width, Sizeable.Unit unit) {
        window.setHeight(width, unit);
        return this;
    }

    public ContentBuilder content() {
        return new ContentBuilder();
    }

    public static PopupWindowBuilder create() {
        final PopupWindowBuilder builder = new PopupWindowBuilder();

        final Window window = new Window("View Password");
        window.center();

        builder.window = window;
        return builder;
    }

    public class ContentBuilder {
        final VerticalLayout root;
        final VerticalLayout content;

        public ContentBuilder() {
            root = new VerticalLayout();

            root.setSizeFull();
            root.setMargin(true);

            content = new VerticalLayout();
            content.setSizeFull();
            root.addComponent(content);
        }

        public ContentBuilder addContent(final Component component) {
            content.addComponent(component);
            return this;
        }

        public FooterBuilder footer() {
            return new FooterBuilder("");
        }

        public FooterBuilder footer(final String footerText) {
            return new FooterBuilder(footerText);
        }

        public class FooterBuilder {
            final HorizontalLayout footer;
            final Label footerTextLabel;
            Button okButton;
            Button cancelButton;

            public FooterBuilder(final String footerText) {
                footer = new HorizontalLayout();
                footer.setWidth("100%");
                footer.setSpacing(true);
                footer.addStyleName("v-window-bottom-toolbar");

                footerTextLabel = new Label(footerText);
                footerTextLabel.setSizeUndefined();
            }

            public FooterBuilder addOkButton() {
                return addOkButton("Ok", e -> window.close());
            }

            public FooterBuilder addOkButton(String buttonText, Button.ClickListener clickListener) {
                okButton = new Button(buttonText);
                okButton.addStyleName("primary");
                if (clickListener != null) okButton.addClickListener(clickListener);
                footer.addComponent(okButton);
                return this;
            }

            public FooterBuilder addCancelButton() {
                return addCancelButton("Cancel", e -> window.close());
            }

            public FooterBuilder addCancelButton(String buttonText, Button.ClickListener clickListener) {
                cancelButton = new Button(buttonText);
                if (clickListener != null) cancelButton.addClickListener(clickListener);
                footer.addComponent(cancelButton);
                return this;
            }

            public FooterBuilder addComponent(final Component component) {
                footer.addComponent(component);
                return this;
            }

            public PopupWindow get() {
                if (content == null) throw new IllegalArgumentException("Popup window content can't be null.");
                footer.setExpandRatio(footerTextLabel, 1);
                root.addComponent(footer);
                root.setExpandRatio(content, 1);
                return new PopupWindow(window, root, footer, footerTextLabel, okButton, cancelButton);
            }
        }
    }
}
