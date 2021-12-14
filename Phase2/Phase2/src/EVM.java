import EVM.*;
import Utilities.*;
import Utilities.Error;
import Value.*;
import OperandStack.*;
import Instruction.*;
import java.util.*;
import java.io.*;

public class EVM {
    
    static private void binOp(int opcode, int type, OperandStack operandStack) {
	Value o1, o2;

        o1 = operandStack.pop();
        o2 = operandStack.pop();

        // Check that the operands have the right type
        if (!(o1.getType() == type && o2.getType() == type))
            Error.error("Error: Type mismatch - operands do not match operator.");

        switch (opcode) {
        case RuntimeConstants.opc_dadd: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() + ((DoubleValue)o1).getValue())); break;
	    

	    // Finish this method

	}

    }
    
    static private void swap(OperandStack operandStack) {
	
	// Yours code goes here.
    Value o1, o2;

    o1 = operandStack.pop();
    o2 = operandStack.pop();
    operandStack.push(o1);
    operandStack.push(o2);
    }
    
    static private void negate(int type, OperandStack operandStack) {
	
	// Yours code goes here.
    Value o1;

    o1 = operandStack.pop();

    switch(type)
    {
        case /*integer*/:
            operandStack.push(new IntegerValue(0 - ((IntegerValue)o1).getValue()));
        break;

        case /*double*/:
            operandStack.push(new DoubleValue(0 - ((DoubleValue)o1).getValue()));
        break;

        case /*long*/:
            operandStack.push(new LongValue(0 - ((LongValue)o1).getValue()));
        break;

        case /*float*/:
            operandStack.push(new FloatValue(0 - ((FloatValue)o1).getValue()));
        break;

        default: // error code
            println("ERROR: negate method, incompatible type arg");
        break;
    }

    }
    
    static private void cmp(int type, OperandStack operandStack) {
	//CHECK LATER AFTER COMPILING FOR ORDER OF OPERANDS WHICH MAY BE REVERSED

	// Yours code goes here.
    Value o1, o2;

    o1 = operandStack.pop();
    o2 = operandStack.pop();

    switch(type)
    {
        case /*double*/:
            double d1 = ((DoubleValue)o1).getValue();
            double d2 = ((DoubleValue)o2).getValue();

            if(d1 == d2)
                operandStack.push(new Value);
            else if(d1 > d2)
                return 1;
            else if(d1 < d2)
                return -1;


        break;

        case /*long*/:
            operandStack.push(new LngValue(0 - ((LongValue)o1).getValue())o);
            break;

        case /*float*/:
            operandStack.push(new FloatValue(0 - ((FloatValue)o1).getValue()));
            break;

        default: // error code
            println("ERROR: cmp method, incompatible type arg");
            break;
    }
	
    }

    static private void two(int from, int to, OperandStack operandStack) {
	
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
		
		// Your code goes here
		
	    }

	    // Your code goes here
	}
    }
    
    
    static private void dup(int opCode, OperandStack operandStack) {
	// In real JVM a Double or a Long take up 2 stack words, but EVM Doubles and Longs
        // do not, so since dup2 can be used to either duplicate 2 single word values or
        // 1 double word value, we need to check the type of what is on the stack before
        // we decide if we should duplicate just one value or two.
        switch (opCode) {
        case RuntimeConstants.opc_dup:   operandStack.push(operandStack.peek(1)); break;
        case RuntimeConstants.opc_dup2: {
            Value o1 = operandStack.peek(1);
            Value o2;
            if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue))
                operandStack.push(o1);
            else {
                o2 = operandStack.peek(2);
                operandStack.push(o2);
                operandStack.push(o1);
            }
        }
            break;
        case RuntimeConstants.opc_dup_x1: {
            Value o1 = operandStack.pop();
            Value o2 = operandStack.pop();
            if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue) ||
                (o2 instanceof DoubleValue) || (o2 instanceof LongValue))
                Error.error("Error: dup_x1 cannot be used on value of type Double or Long.");
            operandStack.push(o1);
            operandStack.push(o2);
            operandStack.push(o1);
        }
            break;
        case RuntimeConstants.opc_dup_x2: {
            Value o1 = operandStack.pop();
            Value o2 = operandStack.pop();
            if ((o1 instanceof DoubleValue) || (o1 instanceof LongValue))
                Error.error("Error: dup_x2 cannot be used on value of type Double or Long.");
            if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue)) {
                operandStack.push(o1);
                operandStack.push(o2);
                operandStack.push(o1);
            } else {
                Value o3 = operandStack.pop();
                if ((o3 instanceof DoubleValue) || (o3 instanceof LongValue))
                    Error.error("Error: word3 of dup_x2 cannot be  of type Double or Long.");
                operandStack.push(o1);
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
                operandStack.push(o1);
                operandStack.push(o2);
                operandStack.push(o1);
            } else {
                Value o2 = operandStack.pop();
                if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue))
                    Error.error("Error: word2 of dup2_x1 cannot be of type Double or Long when word1 is not.");
                Value o3 = operandStack.pop();
                if ((o3 instanceof DoubleValue) || (o3 instanceof LongValue))
                    Error.error("Error: word3 of dup2_x1 cannot be of type Double or Long.");
                operandStack.push(o2);
                operandStack.push(o1);
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
                operandStack.push(o1);
                operandStack.push(o2);
                operandStack.push(o1);
            } else {
                Value o2 = operandStack.pop();
                if ((o2 instanceof DoubleValue) || (o2 instanceof LongValue))
                    Error.error("Error: word2 of dup2_x2 cannot be of type Double or Long when word1 is not.");
                Value o3 = operandStack.pop();
                if (!((o3 instanceof DoubleValue) || (o3 instanceof LongValue)))
                    Error.error("Error: word3/4 of dup2_x2 must be of type Double or Long.");
                operandStack.push(o2);
                operandStack.push(o1);
                operandStack.push(o3);
                operandStack.push(o2);
                operandStack.push(o1);
            }
        }
            break;
        }
    }
    
    
    static private void logic(int inst, OperandStack operandStack) {

	// Your code goes here
	
    }
    
    static private void shift(int opCode, OperandStack operandStack) {
	
	// Your code goes here

    }

    



    public static void main(String argv[]) {
	OperandStack operandStack = new OperandStack(100, "Phase 2");
	Value v1, v2;
	IntegerValue v3, v4;
	
	operandStack.push(new IntegerValue(100));

	v1 = Value.makeValue((double)5.7);
	v2 = new DoubleValue(6);
	operandStack.push(v1);
	operandStack.push(v2);

	System.out.println(operandStack);

	binOp(RuntimeConstants.opc_dadd, Value.s_double, operandStack);
	System.out.println(operandStack.pop()); // ==> 11.7

    }
}
