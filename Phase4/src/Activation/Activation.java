package Activation;
import java.io.*;
import Value.*;
import EVM.*;
import EVM.Class; // Class by itself is ambigious
import OperandStack.*;
import Utilities.Error;

/** An activation is an instance of a method.
 *
 *  variables is an array of Values, these are used to hold values for locals
 *  and parameters in this activation.
 *
 *  Parameters are stored from address 0..n if the method is static, and 1..n+1 if the method is non static.
 *  The local variables follow the parameters.
 *  @author Matt Pedersen
 */

public class Activation {
        
	/** 
	 * An array of locals and parameters, indexed by their address.
	 */
	private Value[]        variables;
	/**
	 * The number of parameters, i.e., size of the signature.
	 */
	private int            parameterCount;
	/**
	 * The number of locals.
	 */
	private int            localCount;
	/**
	 * The address of the previous activation where execution must
	 * return when this activation is removed.
	 */
	private int            returnAddress;
	/**
	 * The method we return to when this activation is terminated.
	 */
	private Method         returnCode;       
	/**
	 * The method that this activation represents an instance of.
	 */
	private Method         thisCode;          
	/**
	 * Contains the object associated with the invocation of the
	 * current method, that is, the object that the method was invoked
	 * on. this_ is null if the method is static.
	 */
	private Object_        this_;
	/**
	 * The class that this_ is an instance of.
	 */
	private Class          cl;

	private int maxAddress;

	// this operandStack is the operand stack associated with this activation
	private OperandStack operandStack;


	/** Created a new activation record based on a method. Creates a
	 *  new variables array, and filles it with the incoming parameter
	 *  values from the operand stack, and null for all the local
	 *  variables.
	 *  @author Matt Pedersen
	 *  @param thisCode the method that this activation is an instance of.
	 *  @param returnAddress the value of pc at the time the new activation was created,
	 *   this is also the return address of the method call.
	 *  @param returnCode the method from which this method was called.
	 *  @param operandStack a stack of Values that holds the parameters.
	 *  @param cl the class of the method associated with this activation.
	 *  @see Method
	 *  @see Value
	 *  @see OperandStack
	 */
	public Activation(Method thisCode, 
			int    returnAddress, 
			Method returnCode,
			OperandStack operandStack,
			Class cl) {
		int varCount   = thisCode.getVarSize();
		int s;

		parameterCount = thisCode.getSignature().size();
		localCount     = varCount - parameterCount;

		this.returnAddress = returnAddress;
		this.returnCode    = returnCode;
		this.thisCode      = thisCode;
		this.cl            = cl;

		s = (thisCode.isStatic())?1:0;
		variables = new Value[varCount];
		for(int i=0; i<varCount; i++)
			variables[i] = null;

		// Hack ;-) we need varCount to include the 'this' at address 0.
		maxAddress = varCount;

		// Transfer parameters from operand stack to activation record.	
		int adr = 1-s;
		int sigsize = thisCode.getSignature().size();
		for (int i=0; i<thisCode.getSignature().size(); i++) {
			variables[adr] = operandStack.peek(sigsize-i);

			if  (variables[adr].getType() == Value.s_long ||
					variables[adr].getType() == Value.s_double) {
				localCount--;
				adr = adr + 2;
			}
			else
				adr = adr + 1;
		}

		for (int i=0; i<thisCode.getSignature().size(); i++) {
			operandStack.pop();
		}

		if (!thisCode.isStatic()) {
			variables[0] = operandStack.pop();
			this_ = ((ReferenceValue)variables[0]).getValue();

			// Check that the this_ reference actually referes to a class of the correct type.
			// this_.className() must be a subclass of cl.getClassName()
			if (!Class.doesExtend(ClassList.getClass(this_.className()),cl))
				Error.error("Expected object reference of type '" + cl.getClassName() + "' but found '" + this_.className() + "'");

		} else 
			this_ = null;
		// create a new operand stack for this activation.
		// remember, the operandstack passed in is the 
		// operand stack of the calling method (or if it is null,
		// then it is of "main" or "<clinit>"
		this.operandStack = new OperandStack(thisCode.getStackSize(), thisCode.getMethodName());
	}   

	/**
	 * Returns the cl field.
	 */
	public Class getThisClass() {
		return cl;
	}

	/**
	 * Returns the thisCode field.
	 */
	public Method getThisCode() {
		return thisCode;
	}

	/**
	 * Returns the this_ field.
	 */
	public Object_ getThis() {
		return this_;
	}

	/**
	 * Return the returnAddress field.
	 */
	public int getReturnAddress() {
		return returnAddress;
	}

	/**
	 * Returns the returnCode field.
	 */
	public Method getReturnCode() {
		return returnCode;
	}

	/**
	 * Returns the operand stack associated with this activation
	 */
	public OperandStack getOperandStack() {
		return this.operandStack;
	}

	/**
	 * Returns an object of type Value or one of its subclasses. Produces an error if the address is too large. 
	 * This method is only used for the iinc instruction.
	 * @param address the address of the variable of parameter. Used as index into the variables array.
	 */
	public Value getVar(int address) {
		if (address >= thisCode.getVarSize()) 
			Error.error("Error: address out of range for this activation.");
		return variables[address];
	}


	/**
	 * Stores a value object in the variables array at address 'address'.
	 * This method is only used by iinc.
	 * @param val An object of type Value.
	 * @param address the address where the value held in the parameter val will be stored.
	 */
	public void setVar(Value val, int address) {
		if (address >= thisCode.getVarSize()) 
			Error.error("Error: address out of range for this activation.");
		variables[address] = val;
	}

	public void print(PrintWriter pw) {
		pw.println("+- Activation Record --------------------------------------------");
		pw.println("| Method: " + thisCode.getMethodName() + " Return addr.: " + returnAddress + " Param. cnt.: " + parameterCount + " Local cnt: " + localCount);
		//Execution.pw.println("Operand Stack. : ");                                                                                                                        
		//   Execution.pw.println("Code.......... : ");
		//for (int i=0;i<thisCode.getCodeSize();i++) {
		//    Execution.pw.println("                 " + i + " " + thisCode.getInstruction(i));
		// }
		for (int i=0;i<parameterCount + localCount;i++) {
			pw.println("| Activation@[" + i + "] = " + variables[i]);
		}
		//Execution.pw.println("");
	}

	public void print_(PrintWriter pw) {
		//Execution.pw.println("-----------------------------------------------");
		pw.println("Method........ : " + thisCode.getMethodName());
		pw.println("Return address : " + returnAddress);
		//Execution.pw.println("Code.......... : ");
		//for (int i=0;i<thisCode.getCodeSize();i++) {
		//    Execution.pw.println("                 " + i + " " + thisCode.getInstruction(i));
		//}
		for (int i=0;i<maxAddress;i++) {
			pw.println("Activation@[" + i + "] = " + variables[i]);
		}
		pw.println("Operand Stack. : " );
	}

	/** Stores the value of the top element of the operand stack into
	 *  the activation record.  The storage location is determined by
	 *  the address.<p> Produces an error message if the address is
	 *  too large, or if the type of the element on the top of the
	 *  stack does not match with the type of what ever element is
	 *  already stored at that address (the value could be null, in
	 *  that case no type checking is done!)
	 *  @param type
	 *  @param address
	 *  @param operandStack
	 */
	public void store(int type, int address, OperandStack operandStack) {
	    //<--
		if (address >= maxAddress) {
			System.out.println("Activation.store:  Offset " + address + " out of ranger. Maximum offset for this activation record is " + (parameterCount + localCount));
			System.exit(1);
		}
		if (variables[address] != null) {
			if (type != variables[address].getType()) 
				// though we are ok if type is reference and variables[address] is string... (sort of a hack!)
				if (type == Value.s_reference && variables[address].getType() == Value.s_string)
					;
				else
					Error.error("Activation.store: Type mismatch.");
		}
		variables[address] = operandStack.pop();
		//-->
	}


	/** Loads a value from the activation record onto the top of the
	 *  stack.  The address determines which value is pushed onto the
	 *  stack.<p> An error is produced if the addres is too large, if
	 *  the value stored at that address in the activation is null, or
	 *  if the type of the value stored at the address does not match
	 *  the type parameter.
	 *  @param type
	 *  @param address
	 *  @param operandStack
	 */
	public void load(int type, int address, OperandStack operandStack) {
	    //<--
		if (address > maxAddress)
			Error.error("Activation.load: offset " + address + " out of ranger. Maximum offset for this activation record is " + (parameterCount + localCount));
		if (variables[address] == null) 
			Error.error("Activation.load: Trying to load from location " + address + ", which has not been initialized.");
		if (type != variables[address].getType()) 
			// though we are ok if type is reference and variables[address] is string... (sort of a hack!)
			if (type == Value.s_reference && variables[address].getType() == Value.s_string)
				;
			else
				Error.error("Activation.load: Type mismatch at address " + address + ". " +  type + " on stack; " + variables[address].getType() + " in acivation record.");

		operandStack.push(variables[address]); 
		//-->
	}    
}


