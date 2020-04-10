package com.debugger.requests.requestor;

import com.debugger.requests.IJdiEventRequestor;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

/**
 * Class responsible for creating step requests.
 *
 * @author arunkumar
 */
public class StepRequestor implements IJdiEventRequestor {

    /**
     * {@link ThreadReference} instance
     */
    private ThreadReference threadRef;

    /**
     * Default constructor
     *
     * @param threadRef
     */
    public StepRequestor(ThreadReference threadRef) {
        this.threadRef = threadRef;
    }

    @Override
    public void eventRequest(VirtualMachine vm) {

        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(threadRef, StepRequest.STEP_LINE,
                StepRequest.STEP_OVER);
        stepRequest.addCountFilter(1);
        stepRequest.enable();

    }

}
