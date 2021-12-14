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
    * Possible instructions: i2b, i2s, i2c,i2d, i2f, i2l, d2f, d2l,
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
                case RuntimeConstants.opc_if_icmpeq: return (value1 == value2 ? labelAddress : pc+1);

                case RuntimeConstants.opc_if_icmpne: return (value1 != value2 ? labelAddress : pc+1);
                case RuntimeConstants.opc_if_icmplt: return (value2 < value1  ? labelAddress : pc+1);
                case RuntimeConstants.opc_if_icmpge: return (value2 >= value1 ? labelAddress : pc+1);
                case RuntimeConstants.opc_if_icmpgt: return (value2 > value1  ? labelAddress : pc+1);
                case RuntimeConstants.opc_if_icmple: return (value2 <= value1 ? labelAddress : pc+1);

            }
        }
        else if (RuntimeConstants.opc_if_acmpeq == opCode ||
        RuntimeConstants.opc_if_acmpne == opCode) {

            if (va1.getType() != Value.s_reference || va2.getType() != Value.s_reference)
                Error.error("Error: '" +  RuntimeConstants.opcNames[opCode] + "' requires object references on the stack.");
            if (RuntimeConstants.opc_if_acmpeq == opCode)
                return (((ReferenceValue)va1).getValue() == ((ReferenceValue)va2).getValue() ? labelAddress : pc+1);
            if (RuntimeConstants.opc_if_acmpne == opCode)
                return (((ReferenceValue)va1).getValue() != ((ReferenceValue)va2).getValue() ? labelAddress : pc+1);

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
            case RuntimeConstants.opc_ifeq: return (value == 0 ? labelAddress : pc+1);
            case RuntimeConstants.opc_ifne: return (value != 0 ? labelAddress : pc+1);
            case RuntimeConstants.opc_iflt: return (value < 0  ? labelAddress : pc+1);
            case RuntimeConstants.opc_ifle: return (value <= 0 ? labelAddress : pc+1);
            case RuntimeConstants.opc_ifgt: return (value > 0  ? labelAddress : pc+1);
            case RuntimeConstants.opc_ifge: return (value >= 0 ? labelAddress : pc+1);

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

    public void returns(int opCode, boolean done, OperandStack operandStack)
    {

        switch(opCode){
            case RuntimeConstants.opc_dreturn:
            {
                Value val;
                val = operandStack.pop();
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
                operandStack.push(val);
            }
            case RuntimeConstants.opc_freturn:
            {
                Value val;
                val = operandStack.pop();
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
                operandStack.push(val);
            }
            case RuntimeConstants.opc_areturn:
            {
                Value val;
                val = operandStack.pop();
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
                operandStack.push(val);
            }
            case RuntimeConstants.opc_ireturn:
            {
                Value val;
                val = operandStack.pop();
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
                operandStack.push(val);
            }
            case RuntimeConstants.opc_return:
            {
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
            }
            case RuntimeConstants.opc_lreturn:
            {
                Value val;
                val = operandStack.pop();
                pc = activation.getReturnAddress();
                currentMethod = activation.getReturnCode();
                activation = activationStack.pop();
                this_ = activation.getThis();
                operandStack = activation.getOperandStack();
                operandStack.push(val);
            }
        //2) otherwise check if the function returns something which can by checking opcode, pop value and add to opstack of the activation of the caller



        //2.1) update pc current method make sure to pop the activation as no longer active and update activation and this_ (look at activation)


        /*
        Notes
        if the method we are returning from is a static initializer we need to pop the activation stack and ternminate the excecution loop
        (Remember we create a new excecution for static initializers) and for regular methods we need to find the return address
        of the caller(this is stored in the activation) we need to pop the activation stack and set curernt method and activation and this_.
        Like the jump instructions we will continue instead of break.
        */
        }
    }

    public static void doIO(BufferedReader in, MethodInvocation mi, OperandStack operandStack) {
        String className = mi.getClassName();
        String methodName = mi.getMethodName();

        try {
            if (methodName.equals("print")) {
                Value va = operandStack.pop();
                if ((mi.getSignature().getSignature())[0].equals("C"))
                    System.out.print((char)((IntegerValue)va).getValue());
                else
                    System.out.print(va);
            } else if (methodName.equals("println")) {
                Value va = operandStack.pop();
                if ((mi.getSignature().getSignature())[0].equals("C"))
                    System.out.println((char)((IntegerValue)va).getValue());
                else
                    System.out.println(va);
            } else if (methodName.equals("readInt"))
                operandStack.push(new IntegerValue(Integer.parseInt(in.readLine())));
            else if (methodName.equals("readFloat"))
                operandStack.push(new FloatValue(Float.parseFloat(in.readLine())));
            else if (methodName.equals("readLong"))
                operandStack.push(new LongValue(Long.parseLong(in.readLine())));
            else if (methodName.equals("readDouble"))
                operandStack.push(new DoubleValue(Double.parseDouble(in.readLine())));
            else
                operandStack.push(new StringValue(in.readLine()));
        } catch (IOException e) {}
    }


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

                case RuntimeConstants.opc_bipush:      operandStack.push(new IntegerValue((byte)inst.getOperand())); break;
                case RuntimeConstants.opc_sipush:      operandStack.push(new IntegerValue((short)inst.getOperand())); break;
                case RuntimeConstants.opc_dconst_0:    operandStack.push(new DoubleValue((double)0.0)); break;
                case RuntimeConstants.opc_dconst_1:    operandStack.push(new DoubleValue((double)1.0)); break;
                case RuntimeConstants.opc_fconst_0:    operandStack.push(new FloatValue(0.0F)); break;
                case RuntimeConstants.opc_fconst_1:    operandStack.push(new FloatValue(1.0F)); break;
                case RuntimeConstants.opc_fconst_2:    operandStack.push(new FloatValue(2.0F)); break;
                case RuntimeConstants.opc_iconst_m1:   operandStack.push(new IntegerValue(-1)); break;
                case RuntimeConstants.opc_iconst_0:    operandStack.push(new IntegerValue(0)); break;

                case RuntimeConstants.opc_iconst_1:    operandStack.push(new IntegerValue(1)); break;
                case RuntimeConstants.opc_iconst_2:    operandStack.push(new IntegerValue(2)); break;

                case RuntimeConstants.opc_iconst_3:    operandStack.push(new IntegerValue(3)); break;
                case RuntimeConstants.opc_iconst_4:    operandStack.push(new IntegerValue(4)); break;
                case RuntimeConstants.opc_iconst_5:    operandStack.push(new IntegerValue(5)); break;
                case RuntimeConstants.opc_lconst_0:    operandStack.push(new LongValue(0L)); break;
                case RuntimeConstants.opc_lconst_1:    operandStack.push(new LongValue(1L)); break;
                case RuntimeConstants.opc_ldc:
                case RuntimeConstants.opc_ldc_w:
                case RuntimeConstants.opc_ldc2_w:      operandStack.push(((Ldc)inst).getValue()); break;

                // LOADING AND STORING
                case RuntimeConstants.opc_aload:       activation.load(Value.s_reference, inst.getOperand(), operandStack); break;

                case RuntimeConstants.opc_aload_0:     activation.load(Value.s_reference, 0, operandStack); break;
                case RuntimeConstants.opc_aload_1:     activation.load(Value.s_reference, 1, operandStack); break;
                case RuntimeConstants.opc_aload_2:     activation.load(Value.s_reference, 2, operandStack); break;
                case RuntimeConstants.opc_aload_3:     activation.load(Value.s_reference, 3, operandStack); break;
                case RuntimeConstants.opc_astore:      activation.store(Value.s_reference, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_astore_0:    activation.store(Value.s_reference, 0, operandStack); break;
                case RuntimeConstants.opc_astore_1:    activation.store(Value.s_reference, 1, operandStack); break;
                case RuntimeConstants.opc_astore_2:    activation.store(Value.s_reference, 2, operandStack); break;
                case RuntimeConstants.opc_astore_3:    activation.store(Value.s_reference, 3, operandStack); break;

                case RuntimeConstants.opc_dload:       activation.load(Value.s_double, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_dload_0:     activation.load(Value.s_double, 0, operandStack); break;
                case RuntimeConstants.opc_dload_1:     activation.load(Value.s_double, 1, operandStack); break;
                case RuntimeConstants.opc_dload_2:     activation.load(Value.s_double, 2, operandStack); break;
                case RuntimeConstants.opc_dload_3:     activation.load(Value.s_double, 3, operandStack); break;
                case RuntimeConstants.opc_dstore:      activation.store(Value.s_double, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_dstore_0:    activation.store(Value.s_double, 0, operandStack); break;
                case RuntimeConstants.opc_dstore_1:    activation.store(Value.s_double, 1, operandStack); break;
                case RuntimeConstants.opc_dstore_2:    activation.store(Value.s_double, 2, operandStack); break;
                case RuntimeConstants.opc_dstore_3:    activation.store(Value.s_double, 3, operandStack); break;

                case RuntimeConstants.opc_fload:       activation.load(Value.s_float, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_fload_0:     activation.load(Value.s_float, 0, operandStack); break;
                case RuntimeConstants.opc_fload_1:     activation.load(Value.s_float, 1, operandStack); break;
                case RuntimeConstants.opc_fload_2:     activation.load(Value.s_float, 2, operandStack); break;
                case RuntimeConstants.opc_fload_3:     activation.load(Value.s_float, 3, operandStack); break;
                case RuntimeConstants.opc_fstore:      activation.store(Value.s_float, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_fstore_0:    activation.store(Value.s_float, 0, operandStack); break;
                case RuntimeConstants.opc_fstore_1:    activation.store(Value.s_float, 1, operandStack); break;
                case RuntimeConstants.opc_fstore_2:    activation.store(Value.s_float, 2, operandStack); break;
                case RuntimeConstants.opc_fstore_3:    activation.store(Value.s_float, 3, operandStack); break;

                case RuntimeConstants.opc_iload:       activation.load(Value.s_integer, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_iload_0:     activation.load(Value.s_integer, 0, operandStack); break;
                case RuntimeConstants.opc_iload_1:     activation.load(Value.s_integer, 1, operandStack); break;
                case RuntimeConstants.opc_iload_2:     activation.load(Value.s_integer, 2, operandStack); break;
                case RuntimeConstants.opc_iload_3:     activation.load(Value.s_integer, 3, operandStack); break;
                case RuntimeConstants.opc_istore:      activation.store(Value.s_integer, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_istore_0:    activation.store(Value.s_integer, 0, operandStack); break;
                case RuntimeConstants.opc_istore_1:    activation.store(Value.s_integer, 1, operandStack); break;
                case RuntimeConstants.opc_istore_2:    activation.store(Value.s_integer, 2, operandStack); break;
                case RuntimeConstants.opc_istore_3:    activation.store(Value.s_integer, 3, operandStack); break;

                case RuntimeConstants.opc_lload:       activation.load(Value.s_long, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_lload_0:     activation.load(Value.s_long, 0, operandStack); break;
                case RuntimeConstants.opc_lload_1:     activation.load(Value.s_long, 1, operandStack); break;
                case RuntimeConstants.opc_lload_2:     activation.load(Value.s_long, 2, operandStack); break;
                case RuntimeConstants.opc_lload_3:     activation.load(Value.s_long, 3, operandStack); break;
                case RuntimeConstants.opc_lstore:      activation.store(Value.s_long, inst.getOperand(), operandStack); break;
                case RuntimeConstants.opc_lstore_0:    activation.store(Value.s_long, 0, operandStack); break;
                case RuntimeConstants.opc_lstore_1:    activation.store(Value.s_long, 1, operandStack); break;
                case RuntimeConstants.opc_lstore_2:    activation.store(Value.s_long, 2, operandStack); break;
                case RuntimeConstants.opc_lstore_3:    activation.store(Value.s_long, 3, operandStack); break;

                // DATA CONVERSION
                case RuntimeConstants.opc_d2f:      two(opCode, Value.s_double, Value.s_float,   operandStack); break;
                case RuntimeConstants.opc_d2i:      two(opCode, Value.s_double, Value.s_integer, operandStack); break;
                case RuntimeConstants.opc_d2l:      two(opCode, Value.s_double, Value.s_long,    operandStack); break;
                case RuntimeConstants.opc_f2d:      two(opCode, Value.s_float, Value.s_double,   operandStack); break;
                case RuntimeConstants.opc_f2i:      two(opCode, Value.s_float, Value.s_integer,  operandStack); break;
                case RuntimeConstants.opc_f2l:      two(opCode, Value.s_float, Value.s_long,     operandStack); break;
                case RuntimeConstants.opc_i2b:      two(opCode, Value.s_integer, Value.s_byte,   operandStack); break;
                case RuntimeConstants.opc_i2c:      two(opCode, Value.s_integer, Value.s_char,   operandStack); break;
                case RuntimeConstants.opc_i2d:      two(opCode, Value.s_integer, Value.s_double, operandStack); break;
                case RuntimeConstants.opc_i2f:      two(opCode, Value.s_integer, Value.s_float,  operandStack); break;
                case RuntimeConstants.opc_i2l:      two(opCode, Value.s_integer, Value.s_long,   operandStack); break;
                case RuntimeConstants.opc_i2s:      two(opCode, Value.s_integer, Value.s_short,  operandStack); break;
                case RuntimeConstants.opc_l2d:      two(opCode, Value.s_long, Value.s_double,    operandStack); break;
                case RuntimeConstants.opc_l2f:      two(opCode, Value.s_long, Value.s_float,     operandStack); break;
                case RuntimeConstants.opc_l2i:      two(opCode, Value.s_long, Value.s_integer,   operandStack); break;

                // BINARY OPERATIONS
                case RuntimeConstants.opc_dadd:
                case RuntimeConstants.opc_dsub:
                case RuntimeConstants.opc_dmul:
                case RuntimeConstants.opc_ddiv:
                case RuntimeConstants.opc_drem:     binOp(opCode, Value.s_double,  operandStack); break;

                case RuntimeConstants.opc_fadd:     binOp(opCode, Value.s_float,   operandStack); break;
                case RuntimeConstants.opc_fsub:
                case RuntimeConstants.opc_fmul:
                case RuntimeConstants.opc_fdiv:
                case RuntimeConstants.opc_frem:     binOp(opCode, Value.s_float,   operandStack); break;

                case RuntimeConstants.opc_iadd:
                case RuntimeConstants.opc_isub:
                case RuntimeConstants.opc_imul:
                case RuntimeConstants.opc_idiv:
                case RuntimeConstants.opc_irem:     binOp(opCode, Value.s_integer, operandStack); break;

                case RuntimeConstants.opc_ladd:
                case RuntimeConstants.opc_lsub:
                case RuntimeConstants.opc_lmul:
                case RuntimeConstants.opc_ldiv:
                case RuntimeConstants.opc_lrem:     binOp(opCode, Value.s_long,    operandStack); break;

                // COMPARING VALUES
                case RuntimeConstants.opc_dcmpg:    cmp(opCode, Value.s_double, operandStack); break;
                case RuntimeConstants.opc_dcmpl:    cmp(opCode, Value.s_double, operandStack); break;
                case RuntimeConstants.opc_fcmpg:    cmp(opCode, Value.s_float,  operandStack); break;
                case RuntimeConstants.opc_fcmpl:    cmp(opCode, Value.s_float,  operandStack); break;
                case RuntimeConstants.opc_lcmp:     cmp(opCode, Value.s_long,   operandStack); break;

                //JUMPING
                case RuntimeConstants.opc_goto:
                case RuntimeConstants.opc_goto_w:    pc = currentMethod.getLabelAddress(((Jump)inst).getLabel()); continue;
                case RuntimeConstants.opc_if_acmpeq:
                case RuntimeConstants.opc_if_acmpne:
                case RuntimeConstants.opc_if_icmpeq:
                case RuntimeConstants.opc_if_icmpne:
                case RuntimeConstants.opc_if_icmplt:
                case RuntimeConstants.opc_if_icmpge:
                case RuntimeConstants.opc_if_icmpgt:
                case RuntimeConstants.opc_if_icmple: pc = ifcmpBranch(opCode, currentMethod.getLabelAddress(((Jump)inst).getLabel()), operandStack, pc); continue;
                case RuntimeConstants.opc_ifeq:
                case RuntimeConstants.opc_ifne:
                case RuntimeConstants.opc_iflt:
                case RuntimeConstants.opc_ifge:
                case RuntimeConstants.opc_ifgt:
                case RuntimeConstants.opc_ifle:      pc = ifBranch(opCode, currentMethod.getLabelAddress(((Jump)inst).getLabel()), operandStack, pc); continue;

                // NEGATING
                case RuntimeConstants.opc_dneg:     negate(opCode, Value.s_double,  operandStack); break;
                case RuntimeConstants.opc_fneg:     negate(opCode, Value.s_float,   operandStack); break;
                case RuntimeConstants.opc_ineg:     negate(opCode, Value.s_integer, operandStack); break;
                case RuntimeConstants.opc_lneg:     negate(opCode, Value.s_long,    operandStack); break;

                // DUPLICATING VALUE ON THE STACK
                case RuntimeConstants.opc_dup:
                case RuntimeConstants.opc_dup_x1:
                case RuntimeConstants.opc_dup_x2:
                case RuntimeConstants.opc_dup2:
                case RuntimeConstants.opc_dup2_x1:
                case RuntimeConstants.opc_dup2_x2:  dup(opCode, operandStack); break;

                // LOGIC
                case RuntimeConstants.opc_iand:
                case RuntimeConstants.opc_ior:
                case RuntimeConstants.opc_ixor:     logic(opCode, Value.s_integer, operandStack); break;
                case RuntimeConstants.opc_land:
                case RuntimeConstants.opc_lor:
                case RuntimeConstants.opc_lxor:     logic(opCode, Value.s_long,    operandStack); break;

                // SHIFTING
                case RuntimeConstants.opc_ishl:
                case RuntimeConstants.opc_ishr:
                case RuntimeConstants.opc_iushr:    shift(opCode, Value.s_integer, operandStack); break;
                case RuntimeConstants.opc_lshl:
                case RuntimeConstants.opc_lshr:
                case RuntimeConstants.opc_lushr:    shift(opCode, Value.s_long,    operandStack); break;

                // SWAPPING
                case RuntimeConstants.opc_swap:     swap(opCode, operandStack); break;

                // IFNONNULL/IFNULL
                case RuntimeConstants.opc_ifnonnull:
                case RuntimeConstants.opc_ifnull:
                {
                    Value va;
                    va = operandStack.pop();
                    if (va != null && va.getType() != Value.s_reference)
                        Error.error("Error: Value of reference type expected on stack.");

                    if (opCode == RuntimeConstants.opc_ifnonnull) {
                        if (((ReferenceValue)va).getValue() != null)
                            pc = currentMethod.getLabelAddress(((Jump)inst).getLabel());
                    } else
                        if (((ReferenceValue)va).getValue() == null)
                            pc = currentMethod.getLabelAddress(((Jump)inst).getLabel());
                    continue ; // ;-)
                }

                // NOP and POP/POP2
                case RuntimeConstants.opc_nop:      break;
                case RuntimeConstants.opc_pop:      operandStack.pop(); break;
                case RuntimeConstants.opc_pop2:
                {
                    Value va = operandStack.pop();
                    if (!(va instanceof DoubleValue || va instanceof LongValue)) {
                        va = operandStack.pop();
                        if (va instanceof DoubleValue || va instanceof LongValue)
                            Error.error("Error: First word of pop2 was a single word, so should word2 be.");
                    }
                    break;
                }


                // IINC
                case RuntimeConstants.opc_iinc:
                {
                    Value va;
                    int varCount = currentMethod.getVarSize();
                    int varNo = ((Iinc)inst).getVarNo();

                    if (varNo >= varCount)
                        Error.error("Error: Variable index out of range for current activation.");

                    va = activation.getVar(varNo);
                    if (va.getType() != Value.s_integer)
                        Error.error("Error: 'iinc' can only be used on integer values.");

                    va = new IntegerValue(((IntegerValue)va).getValue() + ((Iinc)inst).getInc());
                    activation.setVar(va, varNo);
                    break ;
                }
                // CHECKCAST
                case RuntimeConstants.opc_checkcast:
                {
                    Value val = operandStack.peek(1);
                    // convert/ cast val to a ReferenceValue
/*wtfValue cannot be converted to object_*/                    //ReferenceValue rVal = (ReferenceVal)val;
                    //Get object_
/*wtfcannot find getSuperClassObject*/
                    Object_ osObj = ((ReferenceValue)val).getValue();
                    //Object_ iObj = new getSuperClassObject(obVal);
                    //Using inst get the name of the classes
                    //Compare the object_'s class name w/ inst's classes
                    //check for ingeritance (super class) and interfaces

                    ClassRef cRinst = (ClassRef)inst;
                    String cName = cRinst.getClassName();
                    if (cName == osObj.className()) // how do i check for inheritance?
                    {

                        Object_ osscObj = osObj.getSuperClassObject();
                        String osscName = osscObj.className();
                      //if(!(scObj.isAssignableForm(cRinst) && cRinst.isInterface()))
                      if(osscName != cName)
                        Error.error("Error: class cast error!");
                      else
                        Error.error("Error: inst name and classRef name don't match");
                    }
                    break;
                }

                // FIELD RELATED
                case RuntimeConstants.opc_getfield: // YOUR CODE HERE
                {

                  FieldRef fRinst = (FieldRef)inst;

                  String cName = fRinst.getClassName();
                  Class theClass = ClassList.getClass(cName);
                  Field f = theClass.getField(fRinst.getFieldName());
                  if(f.isStatic())
                    Error.error("Error: getfield used for static field");
                  FieldValue fVal= f.getFieldValue();
                  Value val = fVal.getValue();
                  operandStack.push(val);
                  break;
                }
                case RuntimeConstants.opc_getstatic: // YOUR CODE HERE
                {
                  //cast/convert inst to a FieldRef
                  FieldRef fRinst = (FieldRef)inst;
                  //get the name of the classes
                  String cName = fRinst.getClassName();
                  Class theClass = ClassList.getClass(cName);
                  Field f = theClass.getField(fRinst.getFieldName());
                  if(!f.isStatic())
                    Error.error("Error: getstatic used for non-static field");
                  //FieldValue fVal= f.getFieldValue();
                  //Value val = fVal.getValue();
                  //4)use the getStatic helpper function
                  Value val = theClass.getStatic(fRinst);
                  //push the value on stack
                  operandStack.push(val);
                  break;
                }
                case RuntimeConstants.opc_putfield: // YOUR CODE HERE
                {
                  //cast/convert inst to a FieldRef
                  FieldRef fRinst = (FieldRef)inst;
                  //pop the value of the STACK
                  Value val = operandStack.pop();
                  //convert to field
                  Value objVal = operandStack.pop();
                  ReferenceValue rVal = (ReferenceValue)objVal;
                  Object_ stkObj = rVal.getValue();

                  //get the name of the classes
                  String cName = fRinst.getClassName();
                  Class theClass = ClassList.getClass(cName);
                  Field f = theClass.getField(fRinst.getFieldName());
                  if(f.isStatic())
                    Error.error("Error: putfield used for static field");
                  stkObj.putField(fRinst, val);
                  break;
                }
                case RuntimeConstants.opc_putstatic: // YOUR CODE HERE
                {
                    //cast/convert inst to a FieldRef
                    FieldRef fRinst = (FieldRef)inst;
                    //pop the value of the STACK
                    Value val = operandStack.pop();
                    //get the name of the classes
                    String cName = fRinst.getClassName();
                    Class theClass = ClassList.getClass(cName);
                    Field f = theClass.getField(fRinst.getFieldName());
                    if(!f.isStatic())
                      Error.error("Error: putstatic used for nonstatic field");
                    FieldValue fVal= f.getFieldValue();
                    Value fieldVal = fVal.getValue();
                    //4)use the put static helper function
                    theClass.putStatic(theClass, fRinst, fieldVal);
                    break;
                }

                // INSTANCEOF
                case RuntimeConstants.opc_instanceof:// YOUR CODE HERE
                {
                  Value val = operandStack.pop();
                  //implement check for referenceVal

                  //cast to ReferenceValue
                  ReferenceValue rVal = ((ReferenceValue)val); //new?

                  //get the object from the ReferenceValue
                  //Object_ osObj = new getSuperClassObject(rVal);
                  Object_ osObj = ((ReferenceValue)val).getValue();
                  //using inst get the name of the class
                  ClassRef cRinst = (ClassRef)inst;
                  String cName = cRinst.getClassName();

                  //compare the object_'s class name with inst's class name
                  //check for inheritiance (super class) and interfaces interface=c.isInterface() inheritance=super.isAssignableForm(child)
                  //leave an Integer Value on the Stack
                  if (cName == osObj.className()) // how do i check for inheritance?
                  {
                      Object_ osscObj = osObj.getSuperClassObject();
                      String osscName = osscObj.className();
                    IntegerValue iValOne, iValZero;
                    iValOne = new IntegerValue(1);
                    iValZero = new IntegerValue(0);
                    if(osscName != cName)
                      operandStack.push(((Value)iValOne));
                    else
                      operandStack.push(((Value)iValZero));
                  }
                  else
                    Error.error("Error: inst name and classRef name don't match");
                  break;
                }


                // INVOCATIONS
                case RuntimeConstants.opc_invokeinterface: // YOUR CODE HERE
                {
                  /*type interfaceinvocation similar to invoke virtual
                  syntax: invokeinterface classname/methodname(signature)returntype paramcount*/
                  //Cast the inst to a Method INVOCATIONS
                  InterfaceInvocation iInv = ((InterfaceInvocation)inst);
                  String instCName = iInv.getClassName();
                  //get the name of the classes
                  int parCount = iInv.getParamCount();
                  parCount = parCount + 1;
                  Value cNameVal = operandStack.peek(parCount);
                  ReferenceValue rVal = ((ReferenceValue)cNameVal);
                  // val-> Ref-> obj -> class
                  Object_ stkObj = rVal.getValue();
                  Class stkCl = stkObj.getThisClass();
                  String stkcName = stkCl.getClassName();
                  //get the name of the method
                  String mName = iInv.getMethodName();
                  //get the class object
                  //Class theStackClass = ClassList.getClass(cName);
                  //Method cObj = new Object_(rVal); possible alternative to that^
                  // {new} if the corresponding class isn't on stack we must check super class
                  Class instCl = ClassList.getClass(instCName);
                  if (!stkCl.doesExtend(stkCl, instCl)) //WTF (might need to use recursion)
                  {
                    Error.error("Error: ClassName error in invokeVirtual");
                  }
                  //get the signature of the method
                  Signature mSig = iInv.getSignature();
                  //append the name of the method to the signature
                  String smSig = mName + "/" + mSig.getSignature();
                  Method cObj = instCl.getVirtualMethod(smSig);
                  Activation tempActivation;
                  tempActivation = new Activation(cObj, pc, currentMethod, operandStack, stkCl);
                  activationStack.push(tempActivation);
                  this_ = tempActivation.getThis();
                  operandStack = tempActivation.getOperandStack();
                  currentMethod = cObj; //the method we're invoking -Geo
                  pc = 0; //we need to set pc to 0 becuase we started a new activation record, when the method is done, it changes back to match the PREVIOUS activation -Geo
                  activation = tempActivation; //we need to set this to be the new 'activation'
                  break; //we basically continue the executionLoop with the new activation until we hit a return
                }

                case RuntimeConstants.opc_invokestatic: // YOUR CODE HERE
                {
                  //check if Static
                  // Cast the inst to methods
                  MethodInvocation mInv = ((MethodInvocation)inst);
                  //String cName = mInv.getClassName();
                  String mName = mInv.getMethodName();
                  Class theClass = activation.getThisClass();
                  Signature mSig = mInv.getSignature();
                  String smSig = mName + "/" + mSig.getSignature();
                  Method cObj = theClass.getNonvirtualMethod(smSig);
                  Activation tempActivation;
                  tempActivation = new Activation(cObj, pc, currentMethod, operandStack, theClass);
                  activationStack.push(tempActivation);
                  this_ = tempActivation.getThis();
                  operandStack = tempActivation.getOperandStack();
                  currentMethod = cObj; //the method we're invoking -Geo
                  pc = 0; //we need to set pc to 0 becuase we started a new activation record, when the method is done, it changes back to match the PREVIOUS activation -Geo
                  activation = tempActivation; //we need to set this to be the new 'activation'
                  break; //we basically continue the executionLoop with the new activation until we hit a return
                }
                case RuntimeConstants.opc_invokenonvirtual:
                {
                    //Cast the inst to a Method INVOCATIONS
                    MethodInvocation mInv = ((MethodInvocation)inst);
                    //get the name of the classes
                    String cName = mInv.getClassName();
                    //*NEW* check for class exceptions (if these are the class names we must do a workaround)
                    Value val;
                    if(cName == "java/lang/Object")
                      {
                        val = operandStack.pop();
                        break;
                      }
                    else if (cName == "Io")
                      {
                        doIO(in, mInv, operandStack);
                        break;
                      }
                    //get the name of the method
                    String mName = mInv.getMethodName();
                    //get the class object that represents the type of classes (check the class list)
                    Class theClass = ClassList.getClass(cName);
                    //get the signature of the method
                    Signature mSig = mInv.getSignature();
                    //String smSig = mSig.getSignature(); (maybe this?)
                    //append the name of the method to the signature
                    String smSig = mName + "/" + mSig.getSignature();
                    // get<>Method()
                    Method cObj = theClass.getNonvirtualMethod(smSig);
                    Activation tempActivation;
                    tempActivation = new Activation(cObj, pc, currentMethod, operandStack, theClass);
                    activationStack.push(tempActivation);
                    this_ = tempActivation.getThis();
                    operandStack = tempActivation.getOperandStack();
                    currentMethod = cObj; //the method we're invoking -Geo
                    pc = 0; //we need to set pc to 0 becuase we started a new activation record, when the method is done, it changes back to match the PREVIOUS activation -Geo
                    activation = tempActivation; //we need to set this to be the new 'activation'
                    break; //we basically continue the executionLoop with the new activation until we hit a return
                }

                case RuntimeConstants.opc_invokevirtual: // YOUR CODE HERE
                {
                    //Cast the inst to a Method INVOCATIONS
                    MethodInvocation mInv = ((MethodInvocation)inst);
                    //get the name of the classes
                    String cName = mInv.getClassName();
                    Class theClass = ClassList.getClass(cName);
                    //get the name of the method
                    String mName = mInv.getMethodName();
                    //get the class object that represents the type of classes

                    //pop the ref vlaue on the STACK
                    Value val = operandStack.peek(1);
                    ReferenceValue rVal = ((ReferenceValue)val);
                    //get the class name
                    Object_ stkObj = rVal.getValue();
                    Class stkCl = stkObj.getThisClass();
                    String stkCName = stkCl.getClassName();
                    //get the class object
                    Class theOtherClass = ClassList.getClass(stkCName);
                    // {new} if the corresponding class isn't on stack we must check super class

                    Class instCl = ClassList.getClass(cName);
                    if (!stkCl.doesExtend(stkCl, instCl)) //WTF (might need to use recursion)
                    {
                      Error.error("Error: ClassName error in invokeVirtual");
                    }

                    //get the signature of the method
                    Signature msig = mInv.getSignature();
                    //Signature mSig = mSig.getSignature();
                    //append the name of the method to the signature
                    String smSig = mName + "/" + msig.getSignature();
                    Method stkMethod = stkCl.getVirtualMethod(smSig);
                    Activation tempActivation = new Activation(stkMethod, pc, currentMethod, operandStack, theOtherClass);
                    activationStack.push(tempActivation);
                    this_ = tempActivation.getThis();
                    operandStack = tempActivation.getOperandStack();
                    currentMethod = stkMethod; //the method we're invoking -Geo
                    pc = 0; //we need to set pc to 0 becuase we started a new activation record, when the method is done, it changes back to match the PREVIOUS activation -Geo
                    activation = tempActivation; //we need to set this to be the new 'activation'
                    break; //we basically continue the executionLoop with the new activation until we hit a return
                }

                // RETURN
                case RuntimeConstants.opc_dreturn:
                case RuntimeConstants.opc_freturn:
                case RuntimeConstants.opc_areturn:
                case RuntimeConstants.opc_ireturn:
                case RuntimeConstants.opc_return:
                case RuntimeConstants.opc_lreturn:
                {
                    Signature sig = currentMethod.getSignature();
                    String name = currentMethod.getMethodName();
                    //String[] sgist = sig.getSignature();
                    String sigStr = sig.toString();
                    //1) check for main funct (method.methodname) sig = ()V if static set done to true
                    if(name == "main" && sigStr == "()V" && currentMethod.isStatic())
                    {
                        done = true;
                    }
                    else
                    {
                        returns(opCode, done, operandStack);
                    }
                }
                continue;

                // NEW
                case RuntimeConstants.opc_new: // YOUR CODE HERE
              {
                    // 1) convert the "inst" to a ff instruction
                    ClassRef cRinst = ((ClassRef)inst);
                    // 2) Use the classRef to get the name of the class
                    String cName = cRinst.getClassName();
                    // 3) use the classList to get an object that represents the class
                    Class theClass = ClassList.getClass(cName);
                    Object_ cObj = new Object_(theClass);
                    // 4) using a ReferenceValue, create one to leave a ref to the newly created object on the stack
                    ReferenceValue crVal = new ReferenceValue(cObj); // NOTE idk about this
                    Value val = ((Value)crVal);
                    operandStack.push(val);
                }
                    break;

                // LOOKUPSWITCH
                case RuntimeConstants.opc_lookupswitch:
                {
                    LookupSwitch ls = (LookupSwitch)inst;
                    TreeMap tm = ls.getValues();
                    Value va = operandStack.pop();

                    if (va.getType() != Value.s_integer)
                        Error.error("Error: lookupswitch only works for integers.");

                    IntegerValue iv = (IntegerValue)va;
                    Object o = tm.get(""+iv.getValue());

                    if (o == null) {
                        o = tm.get("default");
                        if (o == null)
                            Error.error("Error: no 'default' found in lookupswitch.");
                    }
                    pc = currentMethod.getLabelAddress((String)o);
                }
                continue;
            }
            pc = pc + 1;
        }
    }
}
