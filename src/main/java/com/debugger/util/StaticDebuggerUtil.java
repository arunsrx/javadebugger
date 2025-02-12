package com.debugger.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.debugger.event.visitable.IVisitableJDIEvent;
import com.debugger.event.visitable.VisitableBreakPointEvent;
import com.debugger.event.visitable.VisitableClassPrepareEvent;
import com.debugger.event.visitable.VisitableStepEvent;
import com.debugger.event.visitor.BreakPointVisitor;
import com.debugger.event.visitor.ClassPrepareVisitor;
import com.debugger.event.visitor.IJDIEventVisitor;
import com.debugger.event.visitor.StepVisitor;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.StepEvent;

/**
 * Utility class providing some useful methods.
 */
public class StaticDebuggerUtil {
    /**
     * Virtual machine instance to which we are debugging. This is basically the
     * debugee VM
     */
    private static VirtualMachine vm = null;

    /**
     * Set the {@link VirtualMachine}
     *
     * @param virtualMac {@link VirtualMachine}
     */
    public static void setVm(VirtualMachine virtualMac) {
        vm = virtualMac;
    }

    /**
     * Get the {@link VirtualMachine}
     *
     * @return {@link VirtualMachine}
     */
    public static VirtualMachine getVm() {
        return vm;
    }

    private static Map<Class<? extends Event>, Class<? extends IVisitableJDIEvent>> jdiEventToVisitableMapper = new HashMap<Class<? extends Event>, Class<? extends IVisitableJDIEvent>>();
    private static Map<Class<? extends IVisitableJDIEvent>, Class<? extends IJDIEventVisitor>> visitableToVisitorMapper = new HashMap<Class<? extends IVisitableJDIEvent>, Class<? extends IJDIEventVisitor>>();

    static {
        jdiEventToVisitableMapper.put(BreakpointEvent.class, VisitableBreakPointEvent.class);
        jdiEventToVisitableMapper.put(ClassPrepareEvent.class, VisitableClassPrepareEvent.class);
        jdiEventToVisitableMapper.put(StepEvent.class, VisitableStepEvent.class);

        visitableToVisitorMapper.put(VisitableBreakPointEvent.class, BreakPointVisitor.class);
        visitableToVisitorMapper.put(VisitableClassPrepareEvent.class, ClassPrepareVisitor.class);
        visitableToVisitorMapper.put(VisitableStepEvent.class, StepVisitor.class);
    }

    /**
     * Given a JDI event this method will return an appropriate Visitable Event
     *
     * @param event {@link Event}
     * @return {@link IVisitableJDIEvent}
     */
    public static IVisitableJDIEvent getVisitableEvent(Event event) {
        try {
            Class<?> eventClass = event.getClass().getInterfaces()[0];
            Class<? extends IVisitableJDIEvent> visitableEvent = jdiEventToVisitableMapper.get(eventClass);
            Constructor<? extends IVisitableJDIEvent> constructor = visitableEvent.getConstructor(eventClass);

            return constructor.newInstance(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a Visitable event this method will return an appropriate/corresponding
     * Visitor for this visitable Event
     *
     * @param event {@link IVisitableJDIEvent}
     * @return {@link IJDIEventVisitor}
     */
    public static IJDIEventVisitor getVisitor(IVisitableJDIEvent event) {
        try {
            // Class<?> eventClass = event.getClass().getInterfaces()[0];
            Class<? extends IJDIEventVisitor> visitorClass = visitableToVisitorMapper.get(event.getClass());
            Constructor<? extends IJDIEventVisitor> constructor = visitorClass.getConstructor();

            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
