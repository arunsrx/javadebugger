package com.debugger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.debugger.event.visitable.IVisitableJDIEvent;
import com.debugger.event.visitor.BreakPointVisitor;
import com.debugger.event.visitor.IJDIEventVisitor;
import com.debugger.requests.requestor.BreakPointRequestor;
import com.debugger.requests.requestor.ClassPrepareRequestor;
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

/**
 * An attaching custom debugger which uses the java debug interface APIs
 * Debugger driver code.
 * 
 * @author arun
 *
 */
public class JavaDebugger {

    public static void main(String[] args) throws Exception {

        if (args.length > 3) {
            System.out.println("Invalid number of program arguments.  See usage below.");
            System.out.println("Usage :: This program needs three arguments & limited in its use as of now.");
            System.out.println("1. Name of class to be debugged.");
            System.out.println("2. Breakpoint line number.");
            System.out.println("3. Breakpoint line number.");
        }
        String debugClass = args[0];
        // Class cla = LinkMergeSort.class;
        int brline1 = Integer.parseInt(args[1]);
        int brline2 = Integer.parseInt(args[2]);

        // 1. Debugger needs a connector (launching connector or attaching connector)
        // so that debugger can connect to the process being debugged.
        AttachingConnector aconnector = Bootstrap.virtualMachineManager().attachingConnectors().stream()
                .filter(c -> c.name().equals("com.sun.jdi.SocketAttach")).findFirst()
                .orElseThrow(() -> new RuntimeException("unable to locate socket attaching connector"));

        // provide the hostname and port for the attaching connector to connect to...
        // TODO: need to externalize hostname and port number.
        Map<String, Connector.Argument> env = aconnector.defaultArguments();
        env.get("hostname").setValue("localhost");
        env.get("port").setValue("8001");

        // Attach to the debuggee process/JVM
        System.out.println("Debugger is attaching to: " + env.get("port") + " ...");
        VirtualMachine vm = aconnector.attach(env);
        try {
            System.out.println("Attached to port " + env.get("port") + " ...");

            // 2. Prepare class to be debugged so that we can add breakpoints etc...
            ClassPrepareRequestor cpr = new ClassPrepareRequestor();
            cpr.eventRequest(vm);
            vm.resume();
            // once we are connected to the debugee VM, add/set it to our utility for use
            // elsewhere in the code.
            StaticDebuggerUtil.setVm(vm);

            // TODO: temporary runnables to simulate user adding breakpoints when debugger &
            // dubugee are both running.
            Runnable runnable = () -> {
                System.out.println("pausing for 10 seconds...");
                try {
                    TimeUnit.SECONDS.sleep(10);
                    addBreakPoint(debugClass, brline1, vm);
                    addBreakPointVisitor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Thread t = new Thread(runnable);
            t.start();

            Runnable runnable1 = () -> {
                System.out.println("pausing for 20 seconds...");
                try {
                    TimeUnit.SECONDS.sleep(35);
                    addBreakPoint(debugClass, brline2, vm);
                    addBreakPointVisitor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Thread t1 = new Thread(runnable1);
            t1.start();

            // process events
            EventQueue eventQueue = vm.eventQueue();
            while (true) {
                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        System.out.println("VM being debugged is terminated or finished execution.");
                        System.out.println("Debugger exiting.");
                        return;
                    } else if (event instanceof VMStartEvent) {
                        System.out.println("Debugging of VM started.");
                        System.out.println("Debugger started.");
                    } else {
                        System.out.println("Event type is " + event + " === " + event.toString());
                        IVisitableJDIEvent visitableEvent = StaticDebuggerUtil.getVisitableEvent(event);
                        IJDIEventVisitor visitor = StaticDebuggerUtil.getVisitor(visitableEvent);
                        visitableEvent.accept(visitor);
                    }
                }
                eventSet.resume();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        } finally {
            System.out.println("executing finally block of debugger.");
        }

    }

    /**
     * 
     * @return
     */
    private static BreakPointVisitor addBreakPointVisitor() {
        return new BreakPointVisitor();
    }

    /**
     * 
     * @param className
     * @param lineno
     * @param vm
     */
    private static void addBreakPoint(String className, int lineno, VirtualMachine vm) {

        BreakPointRequestor bpr = new BreakPointRequestor(className, lineno);
        bpr.eventRequest(vm);
        System.out.println("addBreakPoint method line " + lineno + " for class " + className);

    }
}
