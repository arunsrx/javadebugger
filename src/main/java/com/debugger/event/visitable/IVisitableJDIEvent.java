package com.debugger.event.visitable;

import com.debugger.event.visitor.IJDIEventVisitor;

/**
 * Visitable JDI event interface.  Each JDI event must implement this class and invoke
 * appropriate visitor to handle the event.
 *
 * @author arunkumar
 */
public interface IVisitableJDIEvent {
    /**
     * This method is responsible for invoking the appropriate eventVisitor class capable of
     * handling the respective event.
     *
     * @param evtVisitor {@link IJDIEventVisitor} appropriate eventVisitor class capable of
     *                   handling the respective event
     */
    public void accept(IJDIEventVisitor evtVisitor);
}
