package com.debugger.event.visitor;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.StepEvent;
/**
 * 
 * @author arunkumar
 *
 */
public interface IJDIEventVisitor {
    /**
     * 
     * @param event
     */
    public void visit(BreakpointEvent event);

    /**
     * 
     * @param event
     */
    public void visit(ClassPrepareEvent event);
    
    /**
     * 
     * @param event
     */
    public void visit(StepEvent event);
}
