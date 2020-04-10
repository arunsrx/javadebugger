package com.debugger;

import java.util.HashMap;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.StepRequest;

/**
 * An attaching custom debugger which uses the java debug interface APIs
 *
 * @author arun
 */
public class JavaCustomDebugger {

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

            ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
            cpr.addClassFilter(debugClass.getName());
            cpr.setEnabled(true);

            Location location1 = vm.classesByName(debugClass.getName()).get(0).locationsOfLine(15).get(0);
            BreakpointRequest bpReq1 = vm.eventRequestManager().createBreakpointRequest(location1);
            bpReq1.enable();

            /*
             * List<ReferenceType> list = vm.allClasses(); for (ReferenceType string : list)
             * { System.out.println(string.name());
             * if(string.name().equals(debugClass.getName())){
             * //System.out.println("================================= FOUND ======= "
             * +string.name()); } }
             */

            vm.resume();

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
                        Location location = classType.locationsOfLine(15).get(0);
                        BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                        bpReq.enable();

                        ReferenceType refType1 = classPrepEvent.referenceType();

                        Field field1 = refType1.fieldByName("arunsrx");
                        vm.eventRequestManager().createModificationWatchpointRequest(field1).setEnabled(true);

                        MethodEntryRequest methodRequest = vm.eventRequestManager().createMethodEntryRequest();
                        // methodRequest.addClassExclusionFilter("sun.*");
                        // methodRequest.addClassExclusionFilter("java.*");
                        // methodRequest.addClassExclusionFilter("com.sun.*");
                        // methodRequest.addClassExclusionFilter("com.sun.tools.jdi.*");

                        methodRequest.addClassFilter("com.debugger.*");

                        methodRequest.enable();
                        // eventSet.resume();

                    } else if (event instanceof ModificationWatchpointEvent) {
                        ModificationWatchpointEvent modEvent = (ModificationWatchpointEvent) event;
                        System.out.println("old=" + modEvent.valueCurrent());
                        System.out.println("new=" + modEvent.valueToBe());
                        System.out.println();
                        // eventSet.resume();
                    } else if (event instanceof BreakpointEvent) {
                        System.out.println("=================== BreakPoint Event =================");
                        // event.request().disable();
                        displayVariables((BreakpointEvent) event);

                        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                                ((BreakpointEvent) event).thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
                        stepRequest.addCountFilter(1);
                        stepRequest.enable();
                    } else if (event instanceof StepEvent) {
                        event.request().disable();
                        System.out.println("=================== StepEvent =================");

                        displayVariables((StepEvent) event);
                        // eventSet.resume();
                    } else if (event instanceof MethodEntryEvent) {
                        // System.out.println("================= Method Entry Event ==============");
                        String classname = ((MethodEntryEvent) event).method().declaringType().name();
                        String method = classname + "." + ((MethodEntryEvent) event).method().name();
                        int count = 0;
                        if (methodCountMap.containsKey(method)) {
                            count = methodCountMap.get(method);
                        } else {
                            count = 1;
                        }
                        methodCountMap.put(method, count);
                        // eventSet.resume();
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

    /**
     * @param event
     * @throws IncompatibleThreadStateException
     * @throws AbsentInformationException
     */
    public static void displayVariables(LocatableEvent event)
            throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame stackFrame = event.thread().frame(0);
        if (stackFrame.location().toString().contains(debugClass.getName())) {
            Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
            System.out.println("Variables at " + stackFrame.location().toString() + " > ");
            visibleVariables.entrySet().stream()
                    .forEach(e -> System.out.println(e.getKey().name() + " = " + e.getValue()));
        }
    }

}
