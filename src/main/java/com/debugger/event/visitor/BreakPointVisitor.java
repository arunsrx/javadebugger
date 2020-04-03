package com.debugger.event.visitor;

import com.sun.jdi.event.BreakpointEvent;

/**
 * 
 * @author arunkumar
 *
 */
public class BreakPointVisitor extends DefaultJDIEventVisitor {

    @Override
    public void visit(BreakpointEvent event) {
        System.out.println("************************** BreakPoint Event **************************");
        // event.request().disable();
        BreakpointEvent brevent = (BreakpointEvent) event;
        int lineNum = ((BreakpointEvent) event).location().lineNumber();
        System.out.println(" ************************** Hit breakpoint at line no = " + lineNum + " class name is "
                + brevent.location().toString());
        System.out.println(" location === " + brevent.location().toString());

        /*
         * StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
         * ((BreakpointEvent) event).thread(), StepRequest.STEP_LINE,
         * StepRequest.STEP_OVER); stepRequest.addCountFilter(1); stepRequest.enable();
         */
    }

}
