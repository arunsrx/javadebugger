package com.debugger.event.visitor;

import com.debugger.util.StaticDebuggerUtil;
import com.sun.jdi.event.StepEvent;

public class StepVisitor extends DefaultJDIEventVisitor {

   @Override
   public void visit(StepEvent event) {
       event.request().disable();
       System.out.println("=================== StepEvent =================");
       StaticDebuggerUtil.getVm().resume();
   }
}
