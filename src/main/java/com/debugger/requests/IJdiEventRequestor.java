package com.debugger.requests;

import com.sun.jdi.VirtualMachine;

/**
 * Interface which allows different type of JDI requests to be created
 *
 * @author arunkumar
 */
public interface IJdiEventRequestor {

    /**
     * method to create different JDI request
     *
     * @param vm {@link VirtualMachine} through which events have to be created for
     *           the vm.
     */
    public void eventRequest(VirtualMachine vm);

}
