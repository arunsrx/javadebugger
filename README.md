# Java debugger using Java Debugging Interface (javadebugger)
## Java Platform Debugger Architecture (JDPA) Overview
JDPA provides tools to developers to easily create debugger applications.  JDPA consists of three layers.
	1. JVM TI - defines services that a JVM provides for debugging.
	2. JDWP - defines format and communication protocol between the debug VM and debugger API/UI.
	3. JDI - provides high level API to implement debugger applications.

### Enabling Debug mode
The debuggee JVM must be start with the below option

***-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=some valid port number***

***Xdebug*** -> argument enables debugging of the VM
***agentlib***  -> configures the JDWP protocol (communication protocol b/w debugger and debuggee)
***transport*** -> defines the transport mechanism.  There are basically 2 types of transport mechanism.
			1. *dt_socket* -> uses a socket interface
			2. *dt_shmem* -> both the debugger and debuggee will share the same memory.
***server*** -> ("n" or "y") If "y," listen for a debugger application to attach; otherwise, attach to the debugger application at the specified address.
***suspend*** -> should the debuggee suspend its start and wait for debugger to attach or not.  “y” means the debuggee will wait for the debugger to connect. “n” indicates the debuggee can proceed and the debugger will attach at a later point.  If "y," JVM suspends the execution until a debugger connects to the debuggee JVM.
***address*** -> port number to connect to for the debugger.

A debugger can connect to the debuggee via three different ways or connectors
1. **attaching connector** - the debugger attach to an already running debuggee VM.
2. **launching connector** - the debugger simultaneously launches itself and the program being debugged.
3. **listening connector** - the debugger is started before the debuggee and waits and listen for an incoming connection from the debuggee VM.

### ***Project Overview***
This debugger project is a work in progress and will evolve with time, so will the readme :-)
The debugger package contains 3 different versions of the debugger driver code, the most recent and relevant one is the file name JavaDebugger.
This is a debugger which uses the attaching connector to connect to the application being debugged, the debuggee application must be started with the above mentioned debug parameters, it can either be started in the suspend mode or the running mode (see suspend for more information).
The debugger application must be started with the following three arguments:
1. the fully qualified class name of the debuggee application main class (ex: com.example.HelloWorld)
2. line number on which a breakpoint needs to be set.
3. line number on which the second breakpoint has to be set.

Example of debugger application startup args: ***algorithms.com.java.revision.sorting.LinkMergeSort 16 41***

The debugger application mimics the case where in the user sets a breakpoint on an already running debuggee application and again sets another breakpoint after some time.  The debugger program does this by spawning two threads which sets 2 breakpoints at 2 different instances of time.

**NOTE: This readme will be updated as and when the project/debugger program changes vastly.** 
