package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.BreakpointEvent;

/**
 * Class for handling JDI BreakpointEvent and forwarding the same Event to BreakPointVisitor for proper handling.
 *
 * @author arunkumar
 */
public class VisitableBreakPointEvent implements IVisitableJDIEvent {
    /**
     * {@link BreakpointEvent}
     */
    private BreakpointEvent event;

    /**
     * Default constructor.
     *
     * @param event
     */
    public VisitableBreakPointEvent(BreakpointEvent event) {
        this.event = event;
    }

    @Override
    public void accept(IJDIEventVisitor evtVisitor) {
        evtVisitor.visit(event);
    }

}