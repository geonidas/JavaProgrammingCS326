package Utilities;

public class Error {
    public static void error(String s) {
	System.out.println("Error: " + s);
	Thread.dumpStack();
	System.exit(1);
    }
}
    
