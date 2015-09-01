package vaadincrm.util;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Created by someone on 27/08/2015.
 */
public class PopupWindowBuilder {
    private static final float DEFAULT_DIALOG_WINDOW_WIDTH = VaadinUtil.DEFAULT_DIALOG_WINDOW_WIDTH;
    private static final float DEFAULT_DIALOG_WINDOW_HEIGHT = VaadinUtil.DEFAULT_DIALOG_WINDOW_HEIGHT;
    private final Window window;
    private ContentBuilder contentBuilder;

    public PopupWindowBuilder(Window window) {
        this.window = window;
    }

    public PopupWindowBuilder height(float height, Sizeable.Unit unit) {
        window.setHeight(height, unit);
        return this;
    }

    public PopupWindowBuilder width(float width, Sizeable.Unit unit) {
        window.setWidth(width, unit);
        return this;
    }

    public PopupWindowBuilder content(ContentBuilder contentBuilder) {
        contentBuilder.window(window);
        this.contentBuilder = contentBuilder;
        return this;
    }

    public static PopupWindowBuilder create(String windowTitle) {
        final Window window = new Window(windowTitle);
        window.center();
        window.setWidth(DEFAULT_DIALOG_WINDOW_WIDTH, PIXELS);
        window.setHeight(DEFAULT_DIALOG_WINDOW_HEIGHT, PIXELS);
        final PopupWindowBuilder builder = new PopupWindowBuilder(window);
        return builder;
    }

    public PopupWindow build() {
        ContentBuilder.FooterBuilder footerBuilder = contentBuilder.footerBuilder;
        if (contentBuilder.content == null) throw new IllegalArgumentException("Popup window content can't be null.");
        footerBuilder.footer.setExpandRatio(footerBuilder.footerTextLabel, 1);
        footerBuilder.footer.setHeight(40, PIXELS);
        contentBuilder.root.addComponent(footerBuilder.footer);
        contentBuilder.root.setExpandRatio(contentBuilder.content, 1);
        window.setContent(contentBuilder.root);
        return new PopupWindow(window, contentBuilder.content, footerBuilder.footer, footerBuilder.footerTextLabel, footerBuilder.okButton, footerBuilder.cancelButton);
    }

    public static class ContentBuilder {
        Window window;
        final VerticalLayout root;
        final VerticalLayout content;
        private FooterBuilder footerBuilder;

        public ContentBuilder() {
            root = new VerticalLayout();

            root.setSizeFull();
            root.setMargin(true);

            content = new VerticalLayout();
            content.setSizeFull();
            root.addComponent(content);
        }

        public ContentBuilder window(Window window) {
            footerBuilder.window(window);
            this.window = window;
            return this;
        }

        public ContentBuilder addContent(final Component component) {
            content.addComponent(component);
            return this;
        }

        public ContentBuilder height(float height, Sizeable.Unit unit) {
            content.setHeight(height, unit);
            return this;
        }

        public ContentBuilder width(float width, Sizeable.Unit unit) {
            content.setWidth(width, unit);
            return this;
        }

        public ContentBuilder footer(FooterBuilder footerBuilder) {
            footerBuilder.window(window);
            this.footerBuilder = footerBuilder;
            return this;
        }

        public static class FooterBuilder {
            Window window;
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
                footer.addComponent(footerTextLabel);
            }

            public FooterBuilder window(final Window window) {
                this.window = window;
                return this;
            }

            public FooterBuilder okButton() {
                return okButton("Ok", e -> window.close());
            }

            public FooterBuilder okButton(String buttonText, Button.ClickListener clickListener) {
                okButton = new Button(buttonText);
                okButton.addStyleName("primary");
                if (clickListener != null) okButton.addClickListener(clickListener);
                footer.addComponent(okButton);
                return this;
            }

            public FooterBuilder cancelButton() {
                return cancelButton("Cancel", e -> window.close());
            }

            public FooterBuilder cancelButton(String buttonText, Button.ClickListener clickListener) {
                cancelButton = new Button(buttonText);
                if (clickListener != null) cancelButton.addClickListener(clickListener);
                footer.addComponent(cancelButton);
                return this;
            }

            public FooterBuilder addComponent(final Component component) {
                footer.addComponent(component);
                return this;
            }
        }
    }
}
