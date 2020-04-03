package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;

/**
 * 
 * @author arunkumar
 *
 */
public interface IVisitableJDIEvent {
    /**
     * 
     * @param evtVisitor
     */
    public void accept(IJDIEventVisitor evtVisitor);
}
