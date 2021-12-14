package EVM;
import java.util.*;
import Parser.*;
import Scanner.Scanner;
import Execution.*;

public class ClassList {
	static private String path; 
	static private final Hashtable<String,Class> classList = new Hashtable<String,Class>();

	public static void insertClass(Class c) {
		classList.put(c.getClassName(),c);
	}

	public static void setPath(String p) {
		path = p;
	}

	public static Class getClass(String className) {
		Class c = (Class) classList.get(className);
		if (c == null) {
			c = loadClass(className);
			if (c == null) {
			    System.out.println("Class " + className + " not found - might be a Lib file...");
			    c = loadClass("Lib/"+className);
			    if (c == null) {
				System.out.println("File not found : \""+path + className+".j"+"\"");    
				System.exit(1);
			    }
			}
			insertClass(c);
		}
		return c;
	}

	private static Class loadClass(String className) {
		Class newclass = new Class();
		Scanner s;
		parser p = null;
		System.out.print("Loading class : " + path + className + "...");
		try {       
			s = new Scanner(new java.io.FileReader(path + className+ ".j"));
			p = new parser(newclass, s);
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("File not found : \""+path + className+".j"+"\"");
			//System.exit(1);
			return null;
		}
		//		catch (java.io.IOException e) {
		//	System.out.println("Error opening file \""+path + className+".j"+"\"");
		//	//System.exit(1);
		//	return null;
		//}      
		try {        
			java_cup.runtime.Symbol r = p.parse();
			insertClass(newclass);
		}
		catch (java.io.IOException e) {
			System.out.println("An I/O error occured while scanning :");
			System.out.println(e);
			System.exit(1);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}     
		System.out.println("Done!");
		if (newclass.getNonvirtualMethod("<clinit>/()V") != null) {
			Execution e = new Execution(newclass.getClassName(), "<clinit>/()V", null /* No OperandStack yet */);
			e.execute(Execution.doTrace);
		}
		return newclass;
	}
}




