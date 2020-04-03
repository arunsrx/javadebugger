package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.ClassPrepareEvent;

/**
 * 
 * @author arunkumar
 *
 */
public class VisitableClassPrepareEvent implements IVisitableJDIEvent {

    private ClassPrepareEvent event;

    /**
     * 
     * @param event
     */
    public VisitableClassPrepareEvent(ClassPrepareEvent event) {
        this.event = event;
    }

    @Override
    public void accept(IJDIEventVisitor evtVisitor) {
        evtVisitor.visit(event);

    }

}
