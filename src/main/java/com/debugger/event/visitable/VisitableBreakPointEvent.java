package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.BreakpointEvent;

/**
 * 
 * @author arunkumar
 *
 */
public class VisitableBreakPointEvent implements IVisitableJDIEvent {

    private BreakpointEvent event;

    /**
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
