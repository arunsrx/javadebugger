package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;
import com.sun.jdi.event.StepEvent;
/**
 * 
 * @author arunkumar
 *
 */
public class VisitableStepEvent implements IVisitableJDIEvent {
    
    private StepEvent event;
    
    public VisitableStepEvent(StepEvent event) {
        this.event = event;
    }

    @Override
    public void accept(IJDIEventVisitor evtVisitor) {
        evtVisitor.visit(event);
    }

}
