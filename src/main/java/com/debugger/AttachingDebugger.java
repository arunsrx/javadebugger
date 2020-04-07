package com.debugger;

import java.util.Map;

import com.debugger.event.visitable.IVisitableJDIEvent;
import com.debugger.event.visitor.IJDIEventVisitor;
import com.debugger.requests.requestor.BreakPointRequestor;
import com.debugger.util.StaticDebuggerUtil;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

public class AttachingDebugger {
    private static final String SOCKET_ATTACH = "com.sun.jdi.SocketAttach";
    private static final String HOSTNAME = "hostname";
    private static final String PORT = "port";

    public static VirtualMachine attachToVM(String hostname, String port) {
        VirtualMachine vm = null;
        try {
            // 1. Debugger needs a connector (launching connector or attaching connector)
            // so that debugger can connect to the process being debugged.
            AttachingConnector aconnector = Bootstrap.virtualMachineManager().attachingConnectors().stream()
                    .filter(c -> c.name().equals(SOCKET_ATTACH)).findFirst()
                    .orElseThrow(() -> new RuntimeException("unable to locate socket attaching connector"));

            // provide the hostname and port for the attaching connector to connect to...
            // TODO: need to externalize hostname and port number.
            Map<String, Connector.Argument> env = aconnector.defaultArguments();
            env.get(HOSTNAME).setValue(hostname);
            env.get(PORT).setValue(port);

            // Attach to the debuggee process/JVM
            System.out.println("Debugger is attaching to: " + env.get("port") + " ...");

            vm = aconnector.attach(env);
            StaticDebuggerUtil.setVm(vm);
            vm.resume();
            System.out.println("Attached to port " + env.get("port") + " ...");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return vm;
    }
    
    public static void processEvents() {
        try {
            VirtualMachine vm = StaticDebuggerUtil.getVm();
            EventQueue eventQueue = StaticDebuggerUtil.getVm().eventQueue();
            while (true) {
                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    try {
                        if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                            System.out.println("VM being debugged is terminated or finished execution.");
                            System.out.println("Debugger exiting.");
                            return;
                        } else if (event instanceof VMStartEvent) {
                            System.out.println("Debugging of VM started.");
                            System.out.println("Debugger started.");
                            // eventSet.resume();
                        } else {
                            System.out.println("Event type is " + event + " === " + event.toString());
                            IVisitableJDIEvent visitableEvent = StaticDebuggerUtil.getVisitableEvent(event);
                            IJDIEventVisitor visitor = StaticDebuggerUtil.getVisitor(visitableEvent);
                            visitableEvent.accept(visitor);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex);
                    }
                }
                // eventSet.resume();
                vm.resume();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }
    
    public static void addBreakPoint(String className, int lineno) {

        BreakPointRequestor bpr = new BreakPointRequestor(className, lineno);
        bpr.eventRequest(StaticDebuggerUtil.getVm());
        System.out.println("addBreakPoint method line " + lineno + " for class " + className);

    }

    public static void removeBreakPoint(String className, int lineno) {
        BreakPointRequestor bpr = new BreakPointRequestor(className, lineno, true);
        bpr.eventRequest(StaticDebuggerUtil.getVm());
        System.out.println("removed Breakpoint method line " + lineno + " for class " + className);
    }
}
