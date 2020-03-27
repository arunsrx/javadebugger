package com.debugger;

/**
 * Debug this program with JDI & read all local variables.
 * 
 *
 */
public class HelloWorld {
	public static String welcome = "Welcome to Its All Binary !";

	public static void main(String[] args) {
		String helloWorld = "Hello World. ";

		

		String greeting = helloWorld + welcome;

		System.out.println("Hi Everyone, " + greeting);// Put a break point at this line.
		
		/*
		 * try { Thread.sleep(10000); }catch(Exception ex) {
		 * 
		 * }
		 */
		for(int i=0;i<100000;i++) {
			//System.out.println("al;djf;addsaf");
		}

	}

}
