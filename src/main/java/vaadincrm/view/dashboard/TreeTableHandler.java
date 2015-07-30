package vaadincrm.view.dashboard;

import com.vaadin.event.Action;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeTable;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by someone on 29-Jul-2015.
 */
public class TreeTableHandler {

    private static final Action ADD_CATEGORY_ACTION = new Action("ADD_CATEGORY_ACTION");
    private static final Action ADD_ITEM_ACTION = new Action("ADD_ITEM_ACTION");
    private static final Action REMOVE_ITEM_ACTION = new Action("REMOVE_ITEM_ACTION");
    private static final Object NAME_PROPERTY = "Name";
    private static final String HOURS_PROPERTY = "Hours";
    private static final String MODIFIED_PROPERTY = "Modified";

    public TreeTable createTree() {
        TreeTable treeTable = new TreeTable();
        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        treeTable.addContainerProperty(NAME_PROPERTY, String.class, "");
        treeTable.addContainerProperty(HOURS_PROPERTY, Integer.class, 0);
        treeTable.addContainerProperty(MODIFIED_PROPERTY, Date.class, new Date());

        populateWithRandomHierarchicalData(treeTable);

        treeTable.addActionHandler(actionHandler(treeTable));

        treeTable.addValueChangeListener(e -> Notification.show("Value changed:",
                String.valueOf(e.getProperty().getValue()),
                Notification.Type.TRAY_NOTIFICATION));
        return treeTable;
    }

    private Action.Handler actionHandler(final TreeTable treeTable) {
        return new Action.Handler() {
            @Override
            public void handleAction(final Action action, final Object sender,
                                     final Object target) {
                if (action == ADD_ITEM_ACTION) {
                    // Create new item
                    final Object item = treeTable.addItem(new Object[]{
                            "New Item", 0, new Date()}, null);
                    treeTable.setChildrenAllowed(item, false);
                    treeTable.setParent(item, target);
                } else if (action == ADD_CATEGORY_ACTION) {
                    final Object item = treeTable.addItem(new Object[]{
                            "New Category", 0, new Date()}, null);
                    treeTable.setParent(item, target);
                } else if (action == REMOVE_ITEM_ACTION) {
                    treeTable.removeItem(target);
                }
            }

            @Override
            public Action[] getActions(final Object target, final Object sender) {

                if (target == null) {
                    // Context menu in an empty space -> add a new main category
                    return new Action[]{ADD_CATEGORY_ACTION};

                } else if (treeTable.areChildrenAllowed(target)) {
                    // Context menu for a category
                    return new Action[]{ADD_CATEGORY_ACTION, ADD_ITEM_ACTION,
                            REMOVE_ITEM_ACTION};

                } else {
                    // Context menu for an item
                    return new Action[]{REMOVE_ITEM_ACTION};
                }
            }
        };
    }

    private void populateWithRandomHierarchicalData(final TreeTable sample) {
        final Random random = new Random();
        int hours = 0;
        final Object allProjects = sample.addItem(new Object[]{
                "All Projects", 0, new Date()}, null);
        for (final int year : Arrays.asList(2010, 2011, 2012, 2013)) {
            int yearHours = 0;
            final Object yearId = sample.addItem(new Object[]{"Year " + year,
                    yearHours, new Date()}, null);
            sample.setParent(yearId, allProjects);
            for (int project = 1; project < random.nextInt(4) + 2; project++) {
                int projectHours = 0;
                final Object projectId = sample.addItem(
                        new Object[]{"Customer Project " + project,
                                projectHours, new Date()}, null);
                sample.setParent(projectId, yearId);
                for (final String phase : Arrays.asList("Implementation",
                        "Planning", "Prototype")) {
                    final int phaseHours = random.nextInt(50);
                    final Object phaseId = sample.addItem(new Object[]{phase,
                            phaseHours, new Date()}, null);
                    sample.setParent(phaseId, projectId);
                    sample.setChildrenAllowed(phaseId, false);
                    sample.setCollapsed(phaseId, false);
                    projectHours += phaseHours;
                }
                yearHours += projectHours;
                sample.getItem(projectId).getItemProperty(HOURS_PROPERTY)
                        .setValue(projectHours);
                sample.setCollapsed(projectId, false);
            }
            hours += yearHours;
            sample.getItem(yearId).getItemProperty(HOURS_PROPERTY)
                    .setValue(yearHours);
            sample.setCollapsed(yearId, false);
        }
        sample.getItem(allProjects).getItemProperty(HOURS_PROPERTY)
                .setValue(hours);
        sample.setCollapsed(allProjects, false);
    }
}
