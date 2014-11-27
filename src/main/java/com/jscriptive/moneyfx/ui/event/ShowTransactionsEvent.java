package com.jscriptive.moneyfx.ui.event;

import com.jscriptive.moneyfx.model.TransactionFilter;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class ShowTransactionsEvent extends Event {

    /**
     * The only valid EventType for the TabSelectionEvent.
     */
    public static final EventType<ShowTransactionsEvent> SHOW_TRANSACTIONS =
            new EventType<>(Event.ANY, "SHOW_TRANSACTIONS");

    private final TransactionFilter filter;

    public ShowTransactionsEvent(TransactionFilter filter) {
        super(SHOW_TRANSACTIONS);
        this.filter = filter;
    }

    public ShowTransactionsEvent(Object source, EventTarget target, TransactionFilter filter) {
        super(source, target, SHOW_TRANSACTIONS);
        this.filter = filter;
    }

    public TransactionFilter getFilter() {
        return filter;
    }

    @Override
    public ShowTransactionsEvent copyFor(Object newSource, EventTarget newTarget) {
        return (ShowTransactionsEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends ShowTransactionsEvent> getEventType() {
        return (EventType<? extends ShowTransactionsEvent>) super.getEventType();
    }
}
