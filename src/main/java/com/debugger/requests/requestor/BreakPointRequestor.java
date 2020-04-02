package com.debugger.requests.requestor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.debugger.requests.IJdiEventRequestor;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotPreparedException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;

/**
 * Class responsible for added breakpoints as requested/needed by the user.
 * 
 * @author arunkumar
 *
 */
public class BreakPointRequestor implements IJdiEventRequestor {

	/**
	 * map to hold the set breakpoints.
	 */
	public static ConcurrentHashMap<String, List<Integer>> setBreakpoints = new ConcurrentHashMap<String, List<Integer>>();
	/**
	 * map to hold breakpoints that could not be set or break points that have been
	 * deferred coz class has not been loaded yet.
	 */
	public static ConcurrentHashMap<String, List<Integer>> deferredBreakpoints = new ConcurrentHashMap<String, List<Integer>>();

	/**
	 * name of class on which breakpoint is being set
	 */
	private String className;

	/**
	 * line on which breakpoint is being set
	 */
	private int lineNo;

	public BreakPointRequestor() {

	}

	/**
	 * 
	 * @param className
	 * @param lineNo
	 */
	public BreakPointRequestor(String className, int lineNo) {
		this.className = className;
		this.lineNo = lineNo;
	}

	@Override
	public void eventRequest(VirtualMachine vm) {

		Location location1 = null;
		List<Integer> breakpointlist = setBreakpoints.getOrDefault(className, new ArrayList<Integer>());
		// first check if class is already loaded by the classloader if so then simply
		// set the breakpoint.
		// if class has not been loaded then register for class prepare event and add
		// the breakpoint to be set as a deferred breakpoint
		// then in the classprepareevent set the breakpoints from the deferred
		// breakpoint map/list.
		ReferenceType clazz = vm.classesByName(className).get(0);
		if (clazz != null) {
			System.out.println("########################### class already loaded ####################");
			List<com.sun.jdi.Location> locations;
			try {
				locations = clazz.locationsOfLine(lineNo);
			} catch (AbsentInformationException | ClassNotPreparedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}

			if (locations.isEmpty()) {
				throw new RuntimeException("Line " + lineNo + " not found in class " + className);
			}

			location1 = locations.get(0);
			/*
			 * if (location1 == null || location1.method() == null) { // Line is out of
			 * method. throw new RuntimeException("Invalid line " + lineNo + " in class " +
			 * className); }
			 */

			System.out.println("break point requestor =============  breakpoint added @ " + lineNo + " -- "
					+ location1.toString());
			BreakpointRequest bpReq1 = vm.eventRequestManager().createBreakpointRequest(location1);
			/*
			 * if(lineNo ==21) { bpReq1.setSuspendPolicy(BreakpointRequest.SUSPEND_ALL); }
			 */
			bpReq1.enable();

			breakpointlist.add(lineNo);
			setBreakpoints.put(className, breakpointlist);

		} else {
			System.out.println("============== class not yet loaded =============");
			// class is still not loaded but breakpoint is being set...
			// in this case create a classpreparerequest and then set this breakpoint when
			// class is loaded.
			ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
			cpr.addClassFilter(className);
			cpr.setEnabled(true);

			List<Integer> newList = new ArrayList<>();
			List<Integer> list = deferredBreakpoints.putIfAbsent(className, newList);
			if (list == null) {
				list = newList;
			}
			list.add(lineNo);
			return;
		}

	}

}
