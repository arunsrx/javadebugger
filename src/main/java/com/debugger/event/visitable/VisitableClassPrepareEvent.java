package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.ClassPrepareEvent;

/**
 * Class for handling JDI ClassPrepareEvent and forwarding the same Event to ClassPrepareVisitor for proper handling.
 *
 * @author arunkumar
 */
public class VisitableClassPrepareEvent implements IVisitableJDIEvent {

    /**
     * {@link ClassPrepareEvent}
     */
    private ClassPrepareEvent event;

    /**
     * Default Constructor
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
