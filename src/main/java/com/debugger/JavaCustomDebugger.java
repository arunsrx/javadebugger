package com.debugger;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;

/**
 * An attaching custom debugger which uses the java debug interface APIs
 * 
 * @author arun
 *
 */
public class JavaCustomDebugger {
	
	private static Class debugClass = HelloWorld.class;

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
			//System.out.println("Attached! Now listing threads ...");
			//vm.allThreads().stream().forEach(System.out::println);

			//2. Prepare class to be debugged so that we can add breakpoints etc...
			ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
			
			cpr.addClassFilter(debugClass.getName());
			cpr.setEnabled(true);

			/*
			 * ReferenceType refType = vm.allClasses().stream().filter(c ->
			 * c.name().equals(debugClass.getName())) .findFirst().orElseThrow(() -> new
			 * RuntimeException("unable to locate locate class HelloWorld"));
			 * 
			 * Field field = refType.fieldByName("welcome"); cpr.addClassFilter(refType);
			 * vm.eventRequestManager().createModificationWatchpointRequest(field).
			 * setEnabled(true);
			 */
			 
			

			vm.resume();
			
			// process events
		    EventQueue eventQueue = vm.eventQueue();
		    while (true) {
		      EventSet eventSet = eventQueue.remove();
		      for (Event event : eventSet) {
		        if (event instanceof VMDeathEvent
		            || event instanceof VMDisconnectEvent) {
		        	System.out.println("VM being debugged is terminated or finished execution.");
		          // exit
		          return;
		        } else if (event instanceof ClassPrepareEvent) {
		          // watch field on loaded class
		          ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
		          
		          ClassType classType = (ClassType) classPrepEvent.referenceType();
		          Location location = classType.locationsOfLine(18).get(0);
		            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
		            bpReq.enable();
		          
		          ReferenceType refType1 = classPrepEvent.referenceType();
					
		          Field field1 = refType1.fieldByName("welcome");
		          //cpr.addClassFilter(refType1);
		          vm.eventRequestManager().createModificationWatchpointRequest(field1).setEnabled(true);

		        } else if (event instanceof ModificationWatchpointEvent) {
		          ModificationWatchpointEvent modEvent = (ModificationWatchpointEvent) event;
		          System.out.println("old="
		              + modEvent.valueCurrent());
		          System.out.println("new=" + modEvent.valueToBe());
		          System.out.println();
		        } else if (event instanceof BreakpointEvent) {
                    event.request().disable();
                    displayVariables((BreakpointEvent) event);
                    //enableStepRequest(vm, (BreakpointEvent)event);
                }
		      }
		      eventSet.resume();
		    }
		    //System.out.println("Debugger done.");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Debugger done.");
		}

	}
	
	public static void displayVariables(LocatableEvent event) throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame stackFrame = event.thread().frame(0);
        if(stackFrame.location().toString().contains(debugClass.getName())) {
            Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
            System.out.println("Variables at " +stackFrame.location().toString() +  " > ");
            for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
                System.out.println(entry.getKey().name() + " = " + entry.getValue());
            }
        }
    }

}
