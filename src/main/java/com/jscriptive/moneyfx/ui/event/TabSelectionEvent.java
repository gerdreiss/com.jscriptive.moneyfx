package com.jscriptive.moneyfx.ui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class TabSelectionEvent extends Event {

    /**
     * The only valid EventType for the TabSelectionEvent.
     */
    public static final EventType<TabSelectionEvent> TAB_SELECTION =
            new EventType<>(Event.ANY, "TAB_SELECTION");

    public TabSelectionEvent() {
        super(TAB_SELECTION);
    }

    public TabSelectionEvent(Object source, EventTarget target) {
        super(source, target, TAB_SELECTION);
    }

    @Override
    public TabSelectionEvent copyFor(Object newSource, EventTarget newTarget) {
        return (TabSelectionEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends TabSelectionEvent> getEventType() {
        return (EventType<? extends TabSelectionEvent>) super.getEventType();
    }
}
