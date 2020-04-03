package com.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.debugger.requests.requestor.BreakPointRequestor;
import com.debugger.requests.requestor.ClassPrepareRequestor;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Location;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.StepRequest;

/**
 * An attaching custom debugger which uses the java debug interface APIs
 * Debugger driver code.
 * 
 * @author arun
 *
 */
public class JDIDebugger {

    private static Class debugClass = HelloWorld.class;

    private static final Map<String, Integer> methodCountMap = new HashMap<String, Integer>();

    public static void main(String[] args) throws Exception {

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

            Runnable runnable = () -> {
                System.out.println("pausing for 10 seconds...");
                try {
                    TimeUnit.SECONDS.sleep(3);
                    addBreakPoint(debugClass.getName(), 15, vm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Thread t = new Thread(runnable);
            t.start();

            Runnable runnable1 = () -> {
                System.out.println("pausing for 20 seconds...");
                try {
                    TimeUnit.SECONDS.sleep(10);
                    addBreakPoint(debugClass.getName(), 21, vm);
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
                        // exit
                        return;
                    } else if (event instanceof ClassPrepareEvent) {
                        // watch field on loaded class
                        ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;

                        ClassType classType = (ClassType) classPrepEvent.referenceType();
                        // add breakpoint on loaded class @ specified location.
                        Iterator<Entry<String, List<Integer>>> itr = BreakPointRequestor.deferredBreakpoints.entrySet()
                                .iterator();
                        Map<String, List<Integer>> setbrMap = BreakPointRequestor.setBreakpoints;
                        System.out.println(classType.name() + " ===========================");
                        List<Integer> setLineList = setbrMap.getOrDefault(classType.name(), new ArrayList<Integer>());
                        while (itr.hasNext()) {
                            Entry<String, List<Integer>> entry = itr.next();
                            if (entry.getKey().equals(classType.name())) {
                                List<Integer> lineList = entry.getValue();
                                Iterator<Integer> lineitr = lineList.iterator();

                                while (lineitr.hasNext()) {
                                    int lineno = lineitr.next();
                                    Location location = classType.locationsOfLine(lineno).get(0);
                                    BreakpointRequest bpReq = vm.eventRequestManager()
                                            .createBreakpointRequest(location);
                                    bpReq.enable();
                                    lineitr.remove();
                                    setLineList.add(lineno);
                                }
                                if (lineList.isEmpty()) {
                                    itr.remove();
                                }
                                setbrMap.put(classType.name(), setLineList);
                            }
                        }

                    } else if (event instanceof BreakpointEvent) {
                        System.out.println("=================== BreakPoint Event =================");
                        // event.request().disable();
                        BreakpointEvent brevent = (BreakpointEvent) event;
                        int lineNum = ((BreakpointEvent) event).location().lineNumber();
                        System.out.println("Hit breakpoint at line no = " + lineNum + " class name is "
                                + brevent.thread().frame(0).location().toString());
                        System.out.println(" location === " + brevent.location().toString());

                        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                                ((BreakpointEvent) event).thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
                        stepRequest.addCountFilter(1);
                        stepRequest.enable();
                    } else if (event instanceof StepEvent) {
                        event.request().disable();
                        System.out.println("=================== StepEvent =================");

                    } else if (event instanceof MethodEntryEvent) {
                        // System.out.println("================= Method Entry Event ==============");
                        String classname = ((MethodEntryEvent) event).method().declaringType().name();
                        String method = classname + "." + ((MethodEntryEvent) event).method().name();
                        int count = 0;
                        if (methodCountMap.containsKey(method)) {
                            count = methodCountMap.get(method);
                            count++;
                        } else {
                            count = 1;
                        }
                        methodCountMap.put(method, count);
                    }
                }
                eventSet.resume();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            methodCountMap.entrySet().stream()
                    .forEach(e -> System.out.println("Count of method " + e.getKey() + " = " + e.getValue()));
        }

    }

    private static void addBreakPoint(String className, int lineno, VirtualMachine vm) {

        BreakPointRequestor bpr = new BreakPointRequestor(className, lineno);
        bpr.eventRequest(vm);
        System.out.println("addBreakPoint method line " + lineno);
        System.out.println("addBreakPoint method class " + className);

    }
}
