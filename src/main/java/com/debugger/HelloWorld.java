package com.debugger;

/**
 * Debug this program with JDI & read all local variables.
 * 
 *
 */
public class HelloWorld {
    public static String arunsrx = "hey it arun trying a few things !";

    public static void main(String[] args) {
        HelloWorld obj = new HelloWorld();
        while (true) {
            String helloWorld = "Hello";
            helloWorld += " ";
            helloWorld += "World";
            helloWorld += "!";

            String welcome = "Welcome to Its All Binary !";

            String greeting = helloWorld.toUpperCase() + welcome;

            // System.out.println("Hi Everyone, " + greeting);// Put a break point at this
            // line.

            obj.printlines();
        }

    }

    public void printlines() {
        // System.out.println("this is a test being done by arun.");
    }

}
