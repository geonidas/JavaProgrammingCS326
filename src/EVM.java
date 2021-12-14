import java.util.*;
import Utilities.*;
import Utilities.Error;
import EVM.*;
import EVM.Class;
import Execution.*;


public class EVM {
  /**
   * Runs the parser on input files.
   *
   * This is a recognizing parser, i.e. it will either be silent or generate
   * an error report indicating why the input is not in the language of the
   * grammar. 
   *
   * @param argv   the command line, contains the filenames to run
   *               the parser on.
   */
  public static void main(String argv[]) {

      String st = argv[0];
      String cp = "";
      boolean log = true;

      int i=0;
      System.out.println(argv.length);
      while (i<argv.length) {
	  if (argv[i].equals("-log")) {
	      Execution.doTrace = true;
	      log = true;
	      System.out.println("Turning on logging.");
	  } else if (argv[i].equals("-cp")) {
	      if (i+1>=argv.length) {
		  System.out.println("The -cp option must be followed by a directory");
		  System.exit(1);
	      }
	      cp = argv[i+1];
	      System.out.println("Setting class path: " + cp);
	      i++;
	  } else {
	      st = argv[i];
	      System.out.println("Setting file name: " + st);
	  }
	  i++;
      }
            
      st = st.replace('.','/');
      //System.out.println(st);
      int li = st.lastIndexOf("/",st.length());
      if (li<0)
	  ClassList.setPath(cp);
      else
	  ClassList.setPath(cp+st.substring(0,li)+"/");
      
      // System.out.println("Execution path = " + st.substring(0,li));
      // System.out.println("Program = " + st.substring(li+1,st.length()));
      Class cl = ClassList.getClass(st.substring(li+1,st.length()));

      //      System.out.println(cl.getMethods());
      Method currentMethod = cl.getNonvirtualMethod("main/()V"); // doesn't work with main/([Ljava/lang/String;)V ....
      if (currentMethod == null || !currentMethod.isStatic()) 
	  Error.error("No static main method found to run.");

      Execution e = new Execution(st.substring(li+1,st.length()),"main/()V", null /* No OperandStack yet */);
      e.execute(log);

  }
}
