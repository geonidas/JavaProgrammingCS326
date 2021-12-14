package Activation;
import java.io.*;
import Utilities.Error;
import EVM.*;

public class ActivationStack {
	private final int STACK_SIZE = 2000;
	private Activation[] stack;
	private int sp = 0;           // Stack pointer.

	public ActivationStack() {
		stack = new Activation[STACK_SIZE];
		sp = 0;
	}

	public void print(PrintWriter out) {
		out.println("Activation record:");
		for (int i=0; i<sp; i++) {
			out.print(stack[i].getThisCode() + "-> ");
			stack[i].print(out);
		}
	}

	public void push(Activation e) {
		if (sp < STACK_SIZE) 
			stack[sp++] = e;
		else 
			Error.error("ActivationStack.push: Stack overflow.");
	}

	public Activation pop() {
		if (sp >0) {
			Activation a = stack[--sp];
			stack[sp] = null;
			return a;
		} else 
			Error.error("ActivationStack.pop: Stack underflow.");
		return null;
	}

	public Activation top() {
		if (sp > 0) 
			return stack[sp-1];
		else 
			Error.error("ActivationStack.top: Stack underflow.");
		return null;
	}

	public void stackDump(int pc) {
		String mn = top().getThisCode().getMethodName();
		String sc = top().getThisClass().getClassName();
		System.out.println("Exception in method '" + mn + "' (" + sc + ".j:" + pc + ")");
		pc = top().getReturnAddress();
		pop();	
		while (sp>0){
			mn = top().getThisCode().getMethodName();
			sc = top().getThisClass().getClassName();
			System.out.println("                    '" + mn + "' (" + sc + ".j:" + pc + ")");
			pc = top().getReturnAddress();
			pop();
		}
		System.exit(1);
	}


}



