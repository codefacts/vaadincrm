package vaadincrm.view.collection;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import io.crm.mc;
import io.vertx.core.json.JsonObject;

/**
 * Created by someone on 16-Aug-2015.
 */
final public class CollectionTable {
    private final Table table = new Table();
    private static final Object NAME_PROPERTY = "Collection Name";
    private static final String COUNT_PROPERTY = "Element Count";

    public CollectionTable init() {
        table.setSizeFull();
        table.setSelectable(true);

        table.addContainerProperty(NAME_PROPERTY, String.class, "");
        table.addContainerProperty(COUNT_PROPERTY, Link.class, "");

        return this;
    }

    public void populateData(final JsonObject data) {
        for (mc m : mc.values()) {
            if (!m.isInternal()) table.addItem(item(m, data.getInteger(m.name())), null);
        }
    }

    private Object[] item(final mc m, final Object elementCount) {
        final Link link = new Link(elementCount + "", new ExternalResource("/#!collection/" + m.fieldName));
        return new Object[]{m.name(), link};
    }

    public Table getTable() {
        return table;
    }
}
