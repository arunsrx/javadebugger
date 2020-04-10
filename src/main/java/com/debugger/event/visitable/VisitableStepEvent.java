package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.StepEvent;

/**
 * Class for handling JDI StepEvent and forwarding the same Event to StepVisitor for proper handling.
 *
 * @author arunkumar
 */
public class VisitableStepEvent implements IVisitableJDIEvent {
    /**
     * {@link StepEvent}
     */
    private StepEvent event;

    /**
     * Default constructor
     *
     * @param event
     */
    public VisitableStepEvent(StepEvent event) {
        this.event = event;
    }

    @Override
    public void accept(IJDIEventVisitor evtVisitor) {
        evtVisitor.visit(event);
    }

}
