package com.debugger.event.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.debugger.requests.requestor.BreakPointRequestor;
import com.debugger.util.StaticDebuggerUtil;
import com.sun.jdi.ClassType;
import com.sun.jdi.event.ClassPrepareEvent;

/**
 * 
 * @author arunkumar
 *
 */
public class ClassPrepareVisitor extends DefaultJDIEventVisitor {

    @Override
    public void visit(ClassPrepareEvent event) {
        // watch field on loaded class
        ClassType classType = (ClassType) event.referenceType();
        // add breakpoint on loaded class @ specified location.
        Iterator<Entry<String, List<Integer>>> itr = BreakPointRequestor.deferredBreakpoints.entrySet().iterator();
        Map<String, List<Integer>> setbrMap = BreakPointRequestor.setBreakpoints;
        List<Integer> setLineList = setbrMap.getOrDefault(classType.name(), new ArrayList<Integer>());
        while (itr.hasNext()) {
            Entry<String, List<Integer>> entry = itr.next();
            if (entry.getKey().equals(classType.name())) {
                List<Integer> lineList = entry.getValue();
                Iterator<Integer> lineitr = lineList.iterator();

                while (lineitr.hasNext()) {
                    int lineno = lineitr.next();
                    BreakPointRequestor bpr = new BreakPointRequestor(classType.name(), lineno);
                    bpr.eventRequest(StaticDebuggerUtil.getVm());

                    lineitr.remove();
                    setLineList.add(lineno);
                }
                if (lineList.isEmpty()) {
                    itr.remove();
                }
                setbrMap.put(classType.name(), setLineList);
            }
        }
    }
}
