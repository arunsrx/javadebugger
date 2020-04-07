package com.debugger.event.visitor;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.StepEvent;

/**
 * 
 * @author arunkumar
 *
 */
public class DefaultJDIEventVisitor implements IJDIEventVisitor {

    @Override
    public void visit(BreakpointEvent event) {

    }

    @Override
    public void visit(ClassPrepareEvent event) {

    }

    @Override
    public void visit(StepEvent event) {
        // TODO Auto-generated method stub
        
    }

}
