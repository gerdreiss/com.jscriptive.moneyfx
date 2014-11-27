package com.jscriptive.moneyfx.ui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class TabSelectionEvent extends Event {

    /**
     * The only valid EventType for the TabSelectionEvent.
     */
    public static final EventType<TabSelectionEvent> TAB_SELECTION =
            new EventType<>(Event.ANY, "TAB_SELECTION");

    private final Object[] params;

    public TabSelectionEvent(Object... params) {
        super(TAB_SELECTION);
        this.params = params;
    }

    public boolean isWithParams() {
        return ArrayUtils.isNotEmpty(params);
    }

    public List<?> getParams() {
        if (isWithParams()) {
            return Arrays.asList(params);
        }
        return Collections.emptyList();
    }

    public Object getFirstParam() {
        if (isWithParams()) {
            return getParams().iterator().next();
        }
        return null;
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
