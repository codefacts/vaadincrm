package vaadincrm.event;

import vaadincrm.ViewType;

public final class PostViewChangeEvent {
    private final ViewType viewType;

    public PostViewChangeEvent(final ViewType viewType) {
        this.viewType = viewType;
    }

    public ViewType getView() {
        return viewType;
    }
}
