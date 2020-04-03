package com.debugger.event.visitor;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;

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
}
