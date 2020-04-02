package com.debugger.requests.requestor;

import java.util.Arrays;

import com.debugger.requests.IJdiEventRequestor;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassPrepareRequest;

/**
 * Class responsible for creating class prepare requests so that appropriate
 * actions can be taken on class loading.
 * 
 * @author arunkumar
 *
 */
public class ClassPrepareRequestor implements IJdiEventRequestor {

	// exclude some default packages
	private final static String[] EXCLUDE_LIST = { "sun.*", "com.sun.*" };

	@Override
	public void eventRequest(VirtualMachine vm) {

		ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();

		Arrays.asList(EXCLUDE_LIST).stream().forEach(m -> cpr.addClassExclusionFilter(m));
		cpr.addClassFilter("com.debugger.*");
		cpr.enable();

	}

}
