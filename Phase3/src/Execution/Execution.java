package Execution;
import java.util.*;
import Value.*;
import java.io.*;
import Utilities.Error;
import EVM.*;
import EVM.Class;
import Instruction.*;
import Activation.*;
import OperandStack.*;

/** The execution object for the EVM.  This class implements the
 *  execution loop for the EVM. There is onlyl one instance of this
 *  class, except for classes that contain static initializers. When a
 *  class with a static initializer is loaded, a new Execution object
 *  is created to execute the initialization code.
*/

public class Execution {
    /**
     * Holds the method that is currently being executed.
     */
    private Method currentMethod;
    /**
     * Holds the activation stack.
     */
    private ActivationStack activationStack;
    /**
     * Holds the activation associated with the code currently being executed.
     */
    private Activation activation;
    /**
     * Holds a reference to the object which code is currently being executed.
     * this_.thisClass holds a reference to the current class's Class object.
     */
    private Object_         this_;
    /**
     * 'pc' is the program counter. Holds the 'address' of the next
     * instruction to be executed. Keep in mind that all instructions
     * that change the program counter should 'continue' the execution
     * loop rather than 'break' it, OR set pc to one less than the
     * target address because a break causes pc to be incremented by
     * one.
     */
    private int pc;
    public PrintWriter pw;

    public static boolean doTrace = false;

    public static int instructionCount = 0;
    public static int invocationCount = 0;



    // TEST
    //public static java.util.AbstractQueue<Execution> runQueue = new java.util.AbstractQueue<Execution>();


    /**
     * Sets up a new execution, loads the class and method that is to
     * be executed and sets up a new activation record for it.
     *
     * We need to take in the name of the method as it can be either
     * "main" or "<clinit>".
     *
     * @param className The name of the class that contains the method
     * that this execution will start with.
     * @param methodName   The name of the method that this execution will start with.
     */
    public Execution(String className, String methodName, OperandStack os) {

	try {
	    pw = new PrintWriter(new FileWriter("EVM.log"), true);
	} catch (IOException e) {
	    System.out.println("Could not create the EMV.log file.");
	    e.printStackTrace();
	}



	// 3/14/12 Moved the activation stack here from the static init as
	// each execution must have its own activation stack.
	activationStack = new ActivationStack();

	// Ask the class loader from the ClassList class to load the class
	// associated with the class named 'className'; this should be a file
	// with the 'className' and .j extension
	Class thisClass = ClassList.getClass(className);
	// Get the method to execute from the class we just loaded.
	currentMethod = thisClass.getNonvirtualMethod(methodName);

	pw.println("Creating execution for " + className + "/"  + methodName);
	// Create an activation record for this execution.
	activation = new Activation(currentMethod,
				    -1,                  // No return address because this is 'main' or '<clinit>'
				    null,                // no return code either
				    os,                  //
				    thisClass);

	// Push the acrivation onto the activation stack
	activationStack.push(activation);

	// Initialize the program counter (PC) to point to the first instruction.
	pc = 0;
    }


    /**
     * Duplicates words on the operandstack.
     *
     * In real JVM a Double or a Long take up 2 stack words, but EVM
     * Doubles and Longs do not, so since dup2 can be used to either
     * duplicate 2 single word values or 1 double word value, we need
     * to check the type of what is on the stack before we decide if
     * we should duplicate just one value or two.
     *
     * Possible instructions: dup, dup2, dup_x1, dup_x2, dup2_x1,
     * dup2_x2.
     *
     * @param opCode The opcode of the instruction.
     * @param operandStack The operand stack passed from the current Execution.
     */
    public void dup(int opCode, OperandStack operandStack) {
	switch (opCode) {
	case RuntimeConstants.opc_dup:   operandStack.push(Value.makeValue(operandStack.peek(1))); break;
	case RuntimeConstants.opc_dup2: {
	    Value o1 = operandStack.peek(1);
	    Value o2;
	    if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue))
		operandStack.push(Value.makeValue(o1));
	    else {
		o2 = operandStack.peek(2);
		operandStack.push(Value.makeValue(o2));
		operandStack.push(Value.makeValue(o1));
	    }
	}
	    break;
	case RuntimeConstants.opc_dup_x1: {
	    Value o1 = operandStack.pop();
	    Value o2 = operandStack.pop();
	    if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue) ||
		(o2 instanceof DoubleValue) || (o2 instanceof LongValue))
		Error.error("Error: dup_x1 cannot be used on value of type Double or Long.");
	    operandStack.push(Value.makeValue(Value.makeValue(o1)));
	    operandStack.push(Value.makeValue(o2));
	    operandStack.push(Value.makeValue(o1));
	}
	    break;
	case RuntimeConstants.opc_dup_x2: {
	    Value o1 = operandStack.pop();
	    Value o2 = operandStack.pop();
	    if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue))
		Error.error("Error: dup_x2 cannot be used on value of type Double or Long.");
	    if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue)) {
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o2);
		operandStack.push(o1);
	    } else {
		Value o3 = operandStack.pop();
		if ((o3 instanceof DoubleValue) || (o3 instanceof LongValue))
		    Error.error("Error: word3 of dup_x2 cannot be  of type Double or Long.");
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o3);
		operandStack.push(o2);
		operandStack.push(o1);
	    }
	}
	    break;
	case RuntimeConstants.opc_dup2_x1: {
	    Value o1 = operandStack.pop();
	    if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue)) {
		Value o2 = operandStack.pop();
		if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue))
		    Error.error("Error: word3 of dup2_x1 cannot be of type Double or Long.");
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o2);
		operandStack.push(o1);
	    } else {
		Value o2 = operandStack.pop();
		if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue))
		    Error.error("Error: word2 of dup2_x1 cannot be of type Double or Long when word1 is not.");
		Value o3 = operandStack.pop();
		if ((o3 instanceof DoubleValue) || (o3 instanceof LongValue))
		    Error.error("Error: word3 of dup2_x1 cannot be of type Double or Long.");
		operandStack.push(Value.makeValue(o2));
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o3);
		operandStack.push(o2);
		operandStack.push(o1);
	    }
	}
	    break;
	case RuntimeConstants.opc_dup2_x2: {
	    Value o1 = operandStack.pop();
	    if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue)) {
		Value o2 = operandStack.pop();
		if (!((o2 instanceof DoubleValue) || (o2 instanceof LongValue)))
		    Error.error("Error: word3 of dup2_x2 must be of type Double or Long.");
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o2);
		operandStack.push(o1);
	    } else {
		Value o2 = operandStack.pop();
		if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue))
		    Error.error("Error: word2 of dup2_x2 cannot be of type Double or Long when word1 is not.");
		Value o3 = operandStack.pop();
		if (!((o3 instanceof DoubleValue) || (o3 instanceof LongValue)))
		    Error.error("Error: word3/4 of dup2_x2 must be of type Double or Long.");
		operandStack.push(Value.makeValue(o2));
		operandStack.push(Value.makeValue(o1));
		operandStack.push(o3);
		operandStack.push(o2);
		operandStack.push(o1);
	    }
	}
	    break;
	}
    }


    /**
     * Converts the type and value of the top element on the operand stack.
     *
     * Possible instructions: i2b, i2s, i2c, i2d, i2f, i2l, d2f, d2l,
     * d2i, f2i, f2d, f2l, l2i, l2f, l2d
     *
     * @param from The type we are converting from - this should
     * match the type of the object at the top of the stack.
     * @param to The type we are converting to.
     * @param operandStack The operand stack.
     */
    public void two(int opCode, int from, int to, OperandStack operandStack) {
	Value e = operandStack.pop();
	if (e.getType() != from)
	    Error.error("OperandStack.two: Type mismatch.");

	switch (from) {
	case Value.s_integer:
	    int iv = ((IntegerValue)e).getValue();
	    switch (to) {
	    case Value.s_byte:   operandStack.push(new IntegerValue((int)((byte) iv))); break;
	    case Value.s_char:   operandStack.push(new IntegerValue((int)((char) iv))); break;
	    case Value.s_short:  operandStack.push(new IntegerValue((int)((short)iv))); break;
	    case Value.s_double: operandStack.push(new DoubleValue((double)iv)); break;
	    case Value.s_float:  operandStack.push(new FloatValue((float)iv)); break;
	    case Value.s_long:   operandStack.push(new LongValue((long)iv)); break;
	    }
	    break;
	case Value.s_reference: break; /* none */
	case Value.s_double:
	    double dv = ((DoubleValue)e).getValue();
	    switch (to) {
	    case Value.s_float:   operandStack.push(new FloatValue((float)dv)); break;
	    case Value.s_integer: operandStack.push(new IntegerValue((int)dv)); break;
	    case Value.s_long:    operandStack.push(new LongValue((long)dv)); break;
	    }
	    break;
	case Value.s_float:
	    float fv = ((FloatValue)e).getValue();
	    switch (to) {
	    case Value.s_integer: operandStack.push(new IntegerValue((int)fv)); break;
	    case Value.s_double:  operandStack.push(new DoubleValue((double)fv)); break;
	    case Value.s_long:    operandStack.push(new LongValue((long)fv)); break;
	    }
	    break;
	case Value.s_long:
	    long lv = ((LongValue)e).getValue();
	    switch (to) {
	    case Value.s_double: operandStack.push(new DoubleValue((double)lv)); break;
     	    case Value.s_float:  operandStack.push(new FloatValue((float)lv)); break;
	    case Value.s_integer:    operandStack.push(new IntegerValue((int)lv)); break;
	    }
	    break;
	case Value.s_byte:
	    break; /* none */
	case Value.s_short:
	    break; /* none */
	case Value.s_char:
	    break;  /* none */
	default:
	    Error.error("Illegal conversion.");
	}
    }

    /**
     * Negates the value on top of the operand stack.
     *
     * Possible instructions: ineg, fneg, dneg, lneg
     *
     * @param type The type of the element we want to negate. This
     * should match the type of the element on the top of the operand
     * stack.
     * @param operandStack The operand stack.
     */
    public void negate(int opCode, int type, OperandStack operandStack) {
	Value e = operandStack.pop();

	if (e.getType() == Value.s_integer && type == Value.s_integer)
	    operandStack.push(new IntegerValue(((int)-1)*((IntegerValue)e).getValue()));
	else if (e.getType() == Value.s_long && type == Value.s_long)
	    operandStack.push(new LongValue(((long)-1)*((LongValue)e).getValue()));
	else if (e.getType() == Value.s_float && type == Value.s_float)
	    operandStack.push(new FloatValue(((float)-1.0)*((FloatValue)e).getValue()));
	else if (e.getType() == Value.s_double && type == Value.s_double)
	    operandStack.push(new DoubleValue(((double)-1.0)*((DoubleValue)e).getValue()));
	else {
	    if (e.getType() != type)
		Error.error("negate: Type mismatch.");
	    else
		Error.error("negate: Illegal type .");
	}
    }

    /**
     * Compares two values on top of the operand stack and pushes
     * either -1, 0, or 1 on to the operand stack.
     *
     * Possible instructions: fcmpl, fcmpg, dcmpl, dcmpg, lcmp
     *
     * @param type The type of the two elements on top of the stack to be compared.
     * @param operandStack The operand stack.
     */
    public void cmp(int opCode, int type, OperandStack operandStack) {
	Value e1,e2;

	e2 = operandStack.pop();
	e1 = operandStack.pop();

	switch (type) {
	case Value.s_float:
	    float fv1 = ((FloatValue)e1).getValue();
	    float fv2 = ((FloatValue)e2).getValue();

	    if (e1.getType() != Value.s_float || e2.getType() != Value.s_float)
		Error.error("OperandStack.cmp: Type mismatch.");
	    if (fv1 > fv2)
		operandStack.push(new IntegerValue(1));
	    else if (fv1 == fv2)
		operandStack.push(new IntegerValue(0));
	    else
		operandStack.push(new IntegerValue(-1));
	    break;
	case Value.s_double:
	    double dv1 = ((DoubleValue)e1).getValue();
	    double dv2 = ((DoubleValue)e2).getValue();

	    if (e1.getType() != Value.s_double || e2.getType() != Value.s_double)
		Error.error("OperandStack.cmp: Type miscmatch.");
	    if (dv1 > dv2)
		operandStack.push(new IntegerValue(1));
	    else if (dv1 == dv2)
		operandStack.push(new IntegerValue(0));
	    else
		operandStack.push(new IntegerValue(-1));
	    break;
	case Value.s_long:
	    long lv1 = ((LongValue)e1).getValue();
	    long lv2 = ((LongValue)e2).getValue();

	    if (e1.getType() != Value.s_long || e2.getType() != Value.s_long)
		Error.error("OperandStack.cmp: Type mismatch.");
	    if (lv1 > lv2)
		operandStack.push(new IntegerValue(1));
	    else if (lv1 == lv2)
		operandStack.push(new IntegerValue(0));
	    else
		operandStack.push(new IntegerValue(-1));
	    break;
	default:
	    Error.error("Illegal values in cmp");
	}
    }

    /**
     * Swaps the top two elements on the operand stack. Note, this
     * only works if neither are double of long.
     *
     * Possible instructions: swap
     *
     * @param operandStack The operand stack.
     */
    public void swap(int opCode, OperandStack operandStack) {
	Value e1,e2;

	e1 = operandStack.pop();
	e2 = operandStack.pop();
	if (e1 instanceof DoubleValue || e1 instanceof LongValue ||
	    e2 instanceof DoubleValue || e2 instanceof LongValue)
	    Error.error("Error: Swap cannot be used on Double or Long values.");
	operandStack.push(e1);
	operandStack.push(e2);
    }

    /**
     * Performs all the logic operands that can be applied to elements
     * on the operand stack.
     *
     * Possible instructions: iand, ior, ixor, lan, lor, lxor
     *
     * @param opCode The operation code of the instruction.
     * @param type The type of the operation
     * @param operandStack The operand Stack
     */
    public void logic(int opCode, int type, OperandStack operandStack) {
	Value e1,e2;
	int et1, et2;

	e1 = operandStack.pop();
	e2 = operandStack.pop();
	et1 = e1.getType();
	et2 = e2.getType();

	if (et1 != et2)
	    Error.error("logic: Type mismatch.");

	if (et1 == Value.s_integer) {
	    int va1 = ((IntegerValue)e1).getValue();
	    int va2 = ((IntegerValue)e2).getValue();
	    switch(opCode) {
	    case RuntimeConstants.opc_iand: operandStack.push(new IntegerValue(va1 & va2)); break;
	    case RuntimeConstants.opc_ior:  operandStack.push(new IntegerValue(va1 | va2)); break;
	    case RuntimeConstants.opc_ixor: operandStack.push(new IntegerValue(va1 ^ va2)); break;
	    }
	} else if (et1 == Value.s_long) {
	     long va1 = ((LongValue)e1).getValue();
	     long va2 = ((LongValue)e2).getValue();
	     switch(opCode) {
	     case RuntimeConstants.opc_land: operandStack.push(new LongValue(va1 & va2)); break;
	     case RuntimeConstants.opc_lor:  operandStack.push(new LongValue(va1 | va2)); break;
	     case RuntimeConstants.opc_lxor: operandStack.push(new LongValue(va1 ^ va2)); break;
	     }
	} else
	    Error.error("logic: Type mismatch.");

    }

    private int ifcmpBranch(int opCode, int labelAddress, OperandStack operandStack, int pc) {
	int value1, value2;
	Value va1 = operandStack.pop();
	Value va2 = operandStack.pop();

	if (RuntimeConstants.opc_if_icmpeq <= opCode && opCode <= RuntimeConstants.opc_if_icmple) {
	    if (va1.getType() != Value.s_integer || va2.getType() != Value.s_integer)
		Error.error("Error: '" + RuntimeConstants.opcNames[opCode] + "' requires integer values on the stack.");

	    value1 = ((IntegerValue)va1).getValue();
	    value2 = ((IntegerValue)va2).getValue();

	    switch(opCode) {
	    case RuntimeConstants.opc_if_icmpeq: return (value1 == value2 ? labelAddress : pc+1);continue;
      case RuntimeConstants.opc_if_icmpne: return (value1 != value2 ? labelAddress : pc+1);continue;
      case RuntimeConstants.opc_if_icmplt: return (value1  < value2 ? labelAddress : pc+1);continue;
      case RuntimeConstants.opc_if_icmple: return (value1 <= value2 ? labelAddress : pc+1);continue;
      case RuntimeConstants.opc_if_icmpgt: return (value1  > value2 ? labelAddress : pc+1);continue;
      case RuntimeConstants.opc_if_icmpge: return (value1 >= value2 ? labelAddress : pc+1);continue;
      default: println("Error ifcmpBranch"); break;

	    }
	}
	else if (RuntimeConstants.opc_if_acmpeq == opCode ||
		 RuntimeConstants.opc_if_acmpne == opCode) {

      ReferenceValue rvalue1 = ((ReferenceValue)va1).getValue();
 	    ReferenceValue rvalue2 = ((ReferenceValue)va2).getValue();

	    if (va1.getType() != Value.s_reference || va2.getType() != Value.s_reference)
		    Error.error("Error: '" + RuntimeConstants.opcNames[opCode] + "' requires reference values on the stack.");

      switch(opCode){
        case RuntimeConstants.opc_if_acmpeq: return (rvalue1 == rvalue2 ? labelAddress : pc+1);continue;
        case RuntimeConstants.opc_if_acmpne: return (rvalue1 != rvalue2 ? labelAddress : pc+1);continue;
        default: println("Error acmp"); break;
      }
  else if (RuntimeConstants.opc_if_null == opCode ||
    	RuntimeConstants.opc_if_nonnull == opCode) {

      operandStack.push(va2);
      ReferenceValue rvalue1 = ((ReferenceValue)va1).getValue();

	    if (va1.getType() != Value.s_reference || va2.getType() != Value.s_reference)
		    Error.error("Error: '" + RuntimeConstants.opcNames[opCode] + "' requires reference values on the stack.");

        switch(opCode){
          case RuntimeConstants.opc_if_null: return (rvalue1 == null ? labelAddress : pc+1);continue;
          case RuntimeConstants.opc_if_nonnull: return (rvalue1 != null ? labelAddress : pc+1);continue;
          default: println("Error ifnnull"); break;
        }
      }
  else{
      println("Error: ifcmpBranch else default");break;
    }
	}
	return pc;
    }


    private int ifBranch(int opCode, int labelAddress, OperandStack operandStack, int pc) {
	int value;
	Value va = operandStack.pop();

	if (va.getType() != Value.s_integer) {
	    System.out.println("Error: '" + RuntimeConstants.opcNames[opCode] + "' requires an integer value on the stack.");
	    System.exit(1);
	}

	value = ((IntegerValue)va).getValue();

	switch (opCode) {
	case RuntimeConstants.opc_ifeq: return (value == 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_ifne: return (value != 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_iflt: return (value  < 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_ifle: return (value <= 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_ifgt: return (value  > 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_ifge: return (value >= 0 ? labelAddress : pc+1);continue;
  case RuntimeConstants.opc_goto: return labelAddress; continue;
  case RuntimeConstants.opc_goto_w: return labelAddress; continue;
  default: println("Error ifBranch"); break;


	}
	return pc+1;
    }


    private void binOp(int opCode, int type, OperandStack operandStack) {
        Value o1, o2;

        o1 = operandStack.pop();
        o2 = operandStack.pop();

        // Check that the operands have the right type
        if (!(o1.getType() == type && o2.getType() == type))
            Error.error("Error: Type mismatch - operands do not match operator.");

	switch (opCode) {
	case RuntimeConstants.opc_dadd: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() + ((DoubleValue)o1).getValue())); break;
	case RuntimeConstants.opc_ddiv: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() / ((DoubleValue)o1).getValue())); break;
	case RuntimeConstants.opc_dmul: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() * ((DoubleValue)o1).getValue())); break;
	case RuntimeConstants.opc_drem: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() % ((DoubleValue)o1).getValue())); break;
	case RuntimeConstants.opc_dsub: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() - ((DoubleValue)o1).getValue())); break;

	case RuntimeConstants.opc_fadd: operandStack.push(new FloatValue(((FloatValue)o2).getValue() + ((FloatValue)o1).getValue())); break;
	case RuntimeConstants.opc_fdiv: operandStack.push(new FloatValue(((FloatValue)o2).getValue() / ((FloatValue)o1).getValue())); break;
	case RuntimeConstants.opc_fmul: operandStack.push(new FloatValue(((FloatValue)o2).getValue() * ((FloatValue)o1).getValue())); break;
	case RuntimeConstants.opc_frem: operandStack.push(new FloatValue(((FloatValue)o2).getValue() % ((FloatValue)o1).getValue())); break;
	case RuntimeConstants.opc_fsub: operandStack.push(new FloatValue(((FloatValue)o2).getValue() - ((FloatValue)o1).getValue())); break;

	case RuntimeConstants.opc_iadd: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() + ((IntegerValue)o1).getValue())); break;
	case RuntimeConstants.opc_idiv: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() / ((IntegerValue)o1).getValue())); break;
	case RuntimeConstants.opc_imul: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() * ((IntegerValue)o1).getValue())); break;
	case RuntimeConstants.opc_irem: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() % ((IntegerValue)o1).getValue())); break;
	case RuntimeConstants.opc_isub: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() - ((IntegerValue)o1).getValue())); break;

	case RuntimeConstants.opc_ladd: operandStack.push(new LongValue(((LongValue)o2).getValue() + ((LongValue)o1).getValue())); break;
	case RuntimeConstants.opc_ldiv: operandStack.push(new LongValue(((LongValue)o2).getValue() / ((LongValue)o1).getValue())); break;
	case RuntimeConstants.opc_lmul: operandStack.push(new LongValue(((LongValue)o2).getValue() * ((LongValue)o1).getValue())); break;
	case RuntimeConstants.opc_lrem: operandStack.push(new LongValue(((LongValue)o2).getValue() % ((LongValue)o1).getValue())); break;
	case RuntimeConstants.opc_lsub: operandStack.push(new LongValue(((LongValue)o2).getValue() - ((LongValue)o1).getValue())); break;
	}
    }


    private void shift(int opCode, int type, OperandStack operandStack) {
	IntegerValue va1 = (IntegerValue)operandStack.pop();
	Value va2 = operandStack.pop();

	switch (opCode) {
	case RuntimeConstants.opc_ishl: operandStack.push(new IntegerValue(((IntegerValue)va2).getValue() << (31 & va1.getValue()))); break;
	case RuntimeConstants.opc_ishr: operandStack.push(new IntegerValue(((IntegerValue)va2).getValue() >> (31 & va1.getValue()))); break;
	case RuntimeConstants.opc_iushr: operandStack.push(new IntegerValue(((IntegerValue)va2).getValue() >>> (31 & va1.getValue()))); break;
	case RuntimeConstants.opc_lshl: operandStack.push(new LongValue(((LongValue)va2).getValue() << (63 & va1.getValue()))); break;
	case RuntimeConstants.opc_lshr: operandStack.push(new LongValue(((LongValue)va2).getValue() >> (63 & va1.getValue()))); break;
	case RuntimeConstants.opc_lushr: operandStack.push(new LongValue(((LongValue)va2).getValue() >>> (63 & va1.getValue()))); break;

	}
    }



    // YOUR CODE HERE

    public void execute(final boolean trace) {
	this.doTrace = trace;
	// Let us assume that we aren't done before we even start.
	// It will be set to true when we hit the 'return' of the
	// 'main' method.
	boolean done = false;
	// Holds a reference to the current instruction.
	Instruction inst;
	// The opcode of the instruction held in inst
	int opCode;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	// get the operandstack from the activation - remember to update it when
	// a new Activation is created. We could have used activation.getOperandStack()
	// everytime we need to reference the operand stack for the current activation,
	// but that is a little too much extra typing!

	OperandStack operandStack = activation.getOperandStack();

	while (!done) {

	    if (currentMethod.getCodeSize() <= pc)
		Error.error("Error: Fell of the end of the code!");

	    // Get next instruction
	    inst = currentMethod.getInstruction(pc);
	    instructionCount++;
	    opCode = inst.getOpCode();
	    if (doTrace) {
	    	activation.print(pw);
	      operandStack.dump(pw);
        pw.println("");
        pw.println("pc/next instruction: " + pc + " /  " + inst);
	    }
	    switch(opCode) {

		// LOADING CONSTANTS
        case RuntimeConstants.opc_aconst_null: operandStack.push(new ReferenceValue(null)); break;
		case RuntimeConstants.opc_dconst_0:    operandStack.push(new DoubleValue(0)); break;
        case RuntimeConstants.opc_dconst_1:    operandStack.push(new DoubleValue(1)); break;
        case RuntimeConstants.opc_dconst_0:    operandStack.push(new FloatValue(0)); break;
        case RuntimeConstants.opc_dconst_1:    operandStack.push(new FloatValue(1)); break;
        case RuntimeConstants.opc_dconst_2:    operandStack.push(new FloatValue(2)); break;
        case RuntimeConstants.opc_lconst_0:    operandStack.push(new LongValue(0)); break;
        case RuntimeConstants.opc_lconst_1:    operandStack.push(new LongValue(1)); break;
        case RuntimeConstants.opc_iconst_m1:   operandStack.push(new IntegerValue(-1)); break;
	    case RuntimeConstants.opc_iconst_1:    operandStack.push(new IntegerValue(1)); break;
	    case RuntimeConstants.opc_iconst_2:    operandStack.push(new IntegerValue(2)); break;
		case RuntimeConstants.opc_iconst_3:    operandStack.push(new IntegerValue(3)); break;
        case RuntimeConstants.opc_iconst_4:    operandStack.push(new IntegerValue(4)); break;
        case RuntimeConstants.opc_iconst_5:    operandStack.push(new IntegerValue(5)); break;
        case RuntimeConstants.opc_ldc:         operandStack.push(new Ldc(inst.getOperand())); break;                      //testing
        case RuntimeConstants.opc_ldc_w:       operandStack.push(new Ldc(inst.getOperand())); break;
        case RuntimeConstants.opc_ldc2_w:      operandStack.push(new Ldc(inst.getOperand())); break;
        case RuntimeConstants.opc_bipush:      operandStack.push(new IntegerValue(inst.getOperand())); break;
        case RuntimeConstants.opc_sipush:      operandStack.push(new IntegerValue(inst.getOperand())); break;

		// LOADING AND STORING
	    case RuntimeConstants.opc_aload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_astore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dstore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fstore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_iload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_istore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lstore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;

        case RuntimeConstants.opc_iload_0:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_iload_1:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_iload_2:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_iload_3:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lload_0:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lload_1:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lload_2:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lload_3:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fload_0:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fload_1:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fload_2:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fload_3:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dload_0:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dload_1:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dload_2:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dload_3:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_aload_0:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_aload_1:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_aload_2:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_aload_3:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;

        case RuntimeConstants.opc_istore_0:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_istore_1:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_istore_2:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_istore_3:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lstore_0:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lstore_1:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lstore_2:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_lstore_3:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fstore_0:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fstore_1:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fstore_2:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_fstore_3:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dstore_0:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dstore_1:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dstore_2:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_dstore_3:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_astore_0:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_astore_1:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_astore_2:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
        case RuntimeConstants.opc_astore_3:       activation.store(Value.s_reference, inst.getOperand(), operandStack); break;

        /*testing: possibly 'address'/'inst.getOperand' parameters for both function calls*/
          case RuntimeConstants.opc_iinc:           activation.setVar(activation.getVar(inst.getOperand())+1, inst.getOperand()); break;
    // BRANCHING

		// BINARY OPERATIONS
	    case RuntimeConstants.opc_dadd:     binOp(opCode, Value.s_double,  operandStack); break;
	    case RuntimeConstants.opc_dsub:     binOp(opCode, Value.s_double,  operandStack); break;
	    case RuntimeConstants.opc_dmul:     binOp(opCode, Value.s_double,  operandStack); break;
	    case RuntimeConstants.opc_ddiv:     binOp(opCode, Value.s_double,  operandStack); break;
	    case RuntimeConstants.opc_drem:     binOp(opCode, Value.s_double,  operandStack); break;

        case RuntimeConstants.opc_fadd:     binOp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_fsub:     binOp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_fmul:     binOp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_fdiv:     binOp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_frem:     binOp(opCode, Value.s_float,  operandStack); break;

        case RuntimeConstants.opc_iadd:     binOp(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_isub:     binOp(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_imul:     binOp(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_idiv:     binOp(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_irem:     binOp(opCode, Value.s_integer,  operandStack); break;

        case RuntimeConstants.opc_ladd:     binOp(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lsub:     binOp(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lmul:     binOp(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_ldiv:     binOp(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lrem:     binOp(opCode, Value.s_long,  operandStack); break;

        // LOGIC
        case RuntimeConstants.opc_iand:     logic(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_ior:      logic(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_ixor:     logic(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_land:     logic(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lor:      logic(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lxor:     logic(opCode, Value.s_long,  operandStack); break;

        // SWAP
        case RuntimeConstants.opc_iand:     swap(opCode,  operandStack); break;

        // COMPAIR
        case RuntimeConstants.opc_fcmpl:     cmp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_fcmpq:     cmp(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_dcmpl:     cmp(opCode, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_dcmpg:     cmp(opCode, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_lcmp:      cmp(opCode, Value.s_long,  operandStack); break;

        // NEGATE
        case RuntimeConstants.opc_ineg:     negate(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_fneg:     negate(opCode, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_dneg:     negate(opCode, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_lneg:     negate(opCode, Value.s_long,  operandStack); break;

        // SHIFT
        case RuntimeConstants.opc_ishl:     shift(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_ishr:     shift(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_iushr:    shift(opCode, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_lshl:     shift(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lshr:     shift(opCode, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_lushr:    shift(opCode, Value.s_long,  operandStack); break;

        // TWO
        case RuntimeConstants.opc_i2b:     two(opCode, Value.s_integer, Value.s_byte,  operandStack); break;
        case RuntimeConstants.opc_i2s:     two(opCode, Value.s_integer, Value.s_short,  operandStack); break;
        case RuntimeConstants.opc_i2c:     two(opCode, Value.s_integer, Value.s_char,  operandStack); break;
        case RuntimeConstants.opc_i2d:     two(opCode, Value.s_integer, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_i2f:     two(opCode, Value.s_integer, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_i2l:     two(opCode, Value.s_integer, Value.s_long,  operandStack); break;

        case RuntimeConstants.opc_d2f:     two(opCode, Value.s_double, Value.s_float,  operandStack); break;
        case RuntimeConstants.opc_d2l:     two(opCode, Value.s_double, Value.s_long,  operandStack); break;
        case RuntimeConstants.opc_d2i:     two(opCode, Value.s_double, Value.s_integer,  operandStack); break;

        case RuntimeConstants.opc_f2i:     two(opCode, Value.s_float, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_f2d:     two(opCode, Value.s_float, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_f2l:     two(opCode, Value.s_float, Value.s_long,  operandStack); break;

        case RuntimeConstants.opc_l2i:     two(opCode, Value.s_long, Value.s_integer,  operandStack); break;
        case RuntimeConstants.opc_l2d:     two(opCode, Value.s_long, Value.s_double,  operandStack); break;
        case RuntimeConstants.opc_l2f:     two(opCode, Value.s_long, Value.s_float,  operandStack); break;

        // DUPLICATE
        case RuntimeConstants.opc_dup:        dup(opCode,  operandStack); break;
        case RuntimeConstants.opc_dup2:       dup(opCode,  operandStack); break;
        case RuntimeConstants.opc_dup_x1:     dup(opCode,  operandStack); break;
        case RuntimeConstants.opc_dup_x2:     dup(opCode,  operandStack); break;
        case RuntimeConstants.opc_dup2_x1:    dup(opCode,  operandStack); break;
        case RuntimeConstants.opc_dup2_x2:    dup(opCode,  operandStack); break;

        //POP
        case RuntimeConstants.pop:  operandStack.pop(); break;
        case RuntimeConstants.pop2:  operandStack.pop(); break;
	    }
	    pc = pc + 1;
	}
    }
}
