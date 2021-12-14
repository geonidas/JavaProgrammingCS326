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
        if (!(o1.getType() ==  o2.getType()))
            Error.error("Error: Type mismatch - operands do not match operator.");

        switch (opcode) {
        case RuntimeConstants.opc_dadd: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() + ((DoubleValue)o1).getValue())); break; //add double operation
        case RuntimeConstants.opc_dsub: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() - ((DoubleValue)o1).getValue())); break; //sub double operation
        case RuntimeConstants.opc_ddiv: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() / ((DoubleValue)o1).getValue())); break; //div double operation
        case RuntimeConstants.opc_dmul: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() * ((DoubleValue)o1).getValue())); break; //mul double operation
        case RuntimeConstants.opc_drem: operandStack.push(new DoubleValue(((DoubleValue)o2).getValue() % ((DoubleValue)o1).getValue())); break; //rem double operation

        case RuntimeConstants.opc_fadd: operandStack.push(new FloatValue(((FloatValue)o2).getValue() + ((FloatValue)o1).getValue())); break; //add float operation
        case RuntimeConstants.opc_fsub: operandStack.push(new FloatValue(((FloatValue)o2).getValue() - ((FloatValue)o1).getValue())); break; //sub float operation
        case RuntimeConstants.opc_fdiv: operandStack.push(new FloatValue(((FloatValue)o2).getValue() / ((FloatValue)o1).getValue())); break; //div float operation
        case RuntimeConstants.opc_fmul: operandStack.push(new FloatValue(((FloatValue)o2).getValue() * ((FloatValue)o1).getValue())); break; //mul float operation
        case RuntimeConstants.opc_frem: operandStack.push(new FloatValue(((FloatValue)o2).getValue() % ((FloatValue)o1).getValue())); break; //rem float operation

        case RuntimeConstants.opc_ladd: operandStack.push(new LongValue(((LongValue)o2).getValue() + ((LongValue)o1).getValue())); break; //add long operation
        case RuntimeConstants.opc_lsub: operandStack.push(new LongValue(((LongValue)o2).getValue() - ((LongValue)o1).getValue())); break; //sub long operation
        case RuntimeConstants.opc_ldiv: operandStack.push(new LongValue(((LongValue)o2).getValue() / ((LongValue)o1).getValue())); break; //div long operation
        case RuntimeConstants.opc_lmul: operandStack.push(new LongValue(((LongValue)o2).getValue() * ((LongValue)o1).getValue())); break; //mul long operation
        case RuntimeConstants.opc_lrem: operandStack.push(new LongValue(((LongValue)o2).getValue() % ((LongValue)o1).getValue())); break; //rem long operation

        case RuntimeConstants.opc_iadd: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() + ((IntegerValue)o1).getValue())); break; //add integer operation
        case RuntimeConstants.opc_isub: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() - ((IntegerValue)o1).getValue())); break; //sub integer operation
        case RuntimeConstants.opc_idiv: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() / ((IntegerValue)o1).getValue())); break; //div integer operation
        case RuntimeConstants.opc_imul: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() * ((IntegerValue)o1).getValue())); break; //mul integer operation
        case RuntimeConstants.opc_irem: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() % ((IntegerValue)o1).getValue())); break; //rem integer operation

        default:
        System.out.println("ERROR: opcode arg error in binOP method switch");
        break;
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
        case Value.s_integer:
            operandStack.push(new IntegerValue(0 - ((IntegerValue)o1).getValue()));
        break;

        case Value.s_double:
            operandStack.push(new DoubleValue(0 - ((DoubleValue)o1).getValue()));
        break;

        case Value.s_long:
            operandStack.push(new LongValue(0 - ((LongValue)o1).getValue()));
        break;

        case Value.s_float:
            operandStack.push(new FloatValue(0 - ((FloatValue)o1).getValue()));
        break;

        default: // error code
            System.out.println("ERROR: negate method, incompatible type arg");
        break;
    }

    }

  static private void cmp(int type, OperandStack operandStack) {
	//CHECK LATER AFTER COMPILING FOR ORDER OF OPERANDS WHICH MAY BE REVERSED

	// Yours code goes here.
    Value o1, o2;

    o1 = operandStack.pop();
    o2 = operandStack.pop();

    // Check that the operands have the right type
    if (!(o1.getType() == type && o2.getType() == type))
        Error.error("Error: Type mismatch - operands do not match operator.");

    switch(type)
    {
        case Value.s_long:
            long l1 = ((LongValue)o1).getValue();
            long l2 = ((LongValue)o2).getValue();

            if(l1 == l2)
                operandStack.push(new IntegerValue(0));
            else if(l1 < l2)
                operandStack.push(new IntegerValue(1));
            else if(l1 > l2)
                operandStack.push(new IntegerValue(-1));
            else
              System.out.println("ERROR: cmp method, case long else");

            break;

        case Value.s_double:
            double d1 = ((DoubleValue)o1).getValue();
            double d2 = ((DoubleValue)o2).getValue();

            if(d1 == d2)
                operandStack.push(new IntegerValue(0));
            else if(d1 < d2)
                operandStack.push(new IntegerValue(1));
            else if(d1 > d2)
                operandStack.push(new IntegerValue(-1));
            else
              System.out.println("ERROR: cmp method, case double else");

            break;

       case Value.s_float:
            float f1 = ((FloatValue)o1).getValue();
            float f2 = ((FloatValue)o2).getValue();

            if(f1 == f2)
                operandStack.push(new IntegerValue(0));
            else if(f1 < f2)
                operandStack.push(new IntegerValue(1));
            else if(f1 > f2)
                operandStack.push(new IntegerValue(-1));
            else
              System.out.println("ERROR: cmp method, case float else");

            break;

        default: // error code
            System.out.println("ERROR: cmp method, incompatible type arg");
            break;
    }

    }

    static private void two(int from, int to, OperandStack operandStack) {

	     Value e = operandStack.pop();
        if (e.getType() != from)
            Error.error("OperandStack.two: Type mismatch.");

        switch (from)
        {
        case Value.s_integer:
            int iv = ((IntegerValue)e).getValue();
            switch (to)
            {
              case Value.s_byte:   operandStack.push(new IntegerValue((int)((byte) iv))); break;
              case Value.s_char:   operandStack.push(new IntegerValue((int)((char) iv))); break;
              case Value.s_short:  operandStack.push(new IntegerValue((int)((short)iv))); break;
              case Value.s_double: operandStack.push(new DoubleValue((double)iv)); break;
              case Value.s_long:   operandStack.push(new LongValue((long)iv)); break;
              case Value.s_float:  operandStack.push(new FloatValue((float)iv)); break;
              default: System.out.println("ERROR: conversion/'two' method, nested switch, FROM IntegerValue"); break;
	          }
            break;

        case Value.s_float:
            float fv = ((FloatValue)e).getValue();
            switch (to)
            {
              case Value.s_integer: operandStack.push(new IntegerValue((int)fv)); break;
              case Value.s_long:    operandStack.push(new LongValue((long)fv)); break;
              case Value.s_double:  operandStack.push(new DoubleValue((double)fv)); break;
              default: System.out.println("ERROR: conversion/'two' method, nested switch FROM FloatValue"); break;
            }
            break;

        case Value.s_long:
            long lv = ((LongValue)e).getValue();
            switch(to)
            {
              case Value.s_integer: operandStack.push(new IntegerValue((int)lv)); break;
              case Value.s_float:   operandStack.push(new FloatValue((int)lv)); break;
              case Value.s_double:  operandStack.push(new DoubleValue((int)lv)); break;
              default: System.out.println("ERROR: conversion/'two' method, nested switch FROM LongValue"); break;
            }
            break;

        case Value.s_double:
            double dv = ((DoubleValue)e).getValue();
            switch(to)
            {
              case Value.s_integer: operandStack.push(new IntegerValue((int)dv)); break;
              case Value.s_float:   operandStack.push(new FloatValue((int)dv)); break;
              case Value.s_long:    operandStack.push(new LongValue((int)dv)); break;
              default: System.out.println("ERROR: conversion/'two' method, nested switch FROM DoubleValue"); break;
            }
            break;

        default: System.out.println("ERROR: converstion/'two' method, main switch"); break;
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

        Value o1, o2;

        o1 = operandStack.pop();
        o2 = operandStack.pop();

        // Check that the operands have the right type
        if (!(o1.getType() ==  o2.getType()))
            Error.error("Error: Type mismatch - operands do not match.");

        switch(inst)
        {
            case RuntimeConstants.opc_iand: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() & ((IntegerValue)o1).getValue())); break;
            case RuntimeConstants.opc_ior: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() | ((IntegerValue)o1).getValue())); break;
            case RuntimeConstants.opc_ixor: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() ^ ((IntegerValue)o1).getValue())); break;

            case RuntimeConstants.opc_land: operandStack.push(new LongValue(((LongValue)o2).getValue() & ((LongValue)o1).getValue())); break;
            case RuntimeConstants.opc_lor: operandStack.push(new LongValue(((LongValue)o2).getValue() | ((LongValue)o1).getValue())); break;
            case RuntimeConstants.opc_lxor: operandStack.push(new LongValue(((LongValue)o2).getValue() ^ ((LongValue)o1).getValue())); break;

            default: System.out.println("ERROR: logic method, switch statement error"); break;
      }
}
    static private void shift(int opCode, OperandStack operandStack) {

	     // Your code goes here
      Value o1, o2;

      o1 = operandStack.pop();
      o2 = operandStack.pop();

      switch(opCode)
      {
          case RuntimeConstants.opc_ishl: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() << (31 & ((IntegerValue)o1).getValue()))); break;
          case RuntimeConstants.opc_ishr: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() >> (31 & ((IntegerValue)o1).getValue()))); break;
          case RuntimeConstants.opc_iushr: operandStack.push(new IntegerValue(((IntegerValue)o2).getValue() >>> (31 & ((IntegerValue)o1).getValue()))); break;

          case RuntimeConstants.opc_lshl: operandStack.push(new LongValue(((LongValue)o2).getValue() << (63 & ((LongValue)o1).getValue()))); break;
          case RuntimeConstants.opc_lshr: operandStack.push(new LongValue(((LongValue)o2).getValue() >> (63 & ((LongValue)o1).getValue()))); break;
          case RuntimeConstants.opc_lushr: operandStack.push(new LongValue(((LongValue)o2).getValue() >>> (63 & ((LongValue)o1).getValue()))); break;

          default: System.out.println("ERROR: shift method, switch statement error"); break;
    }

    }





    public static void main(String argv[]) {
      OperandStack operandStack = new OperandStack(100, "Phase 2");
      Value v1, v2;
      IntegerValue v3, v4;

      operandStack.push (new IntegerValue(100));

      v1 = Value.makeValue((double) 5.7);
      v2 = new DoubleValue(6);
      operandStack.push(v1);
      operandStack.push(v2);

      System.out.println(operandStack);

      binOp(RuntimeConstants.opc_dadd, Value.s_double, operandStack);
      System.out.println(operandStack.pop());

      ////Q3////
      System.out.println("");
      System.out.println("//// binOp Test /////");

      v1 = new IntegerValue(13);
      v2 = new IntegerValue(8);
      operandStack.push(v1);
      operandStack.push(v2);

      //multiply integer values
      binOp(RuntimeConstants.opc_imul, Value.s_integer, operandStack);
      //result of 8*13
      System.out.println("Integer Result of 8 * 13 = " + operandStack.pop());

      v1 = new LongValue(23);
      v2 = new LongValue(7);
      operandStack.push(v1);
      operandStack.push(v2);

      //divide the two long values
      binOp(RuntimeConstants.opc_ldiv, Value.s_long, operandStack);
      //result of 23 / 7
      System.out.println("Long Result of 23 / 7 = " + operandStack.pop());

      v1 = new FloatValue(34.5f);
      v2 = new FloatValue(44.1f);
      operandStack.push(v1);
      operandStack.push(v2);

      //add float values
      binOp(RuntimeConstants.opc_fadd, Value.s_float, operandStack);
      //Result of 34.5f + 44.1f
      System.out.println("Float Result of 34.5f + 44.1f = " + operandStack.pop());

      ////Q4////
      System.out.println("");
      System.out.println("///// Swap Test /////");

      System.out.println("Integer Values: Value 1 = 45 Value 2 = 78");
      v1 = new IntegerValue(45);
      v2 = new IntegerValue(78);
      operandStack.push(v1);
      operandStack.push(v2);

      // ==== IntegerValues in stack
      //78
      //45
      //+===+

      swap(operandStack);
      //pop() and pront swaped IntegerValues in opeandStack
      System.out.println("The values before swap are 45 78");
      System.out.println("The Values after swapping are " + operandStack.pop() + " " + operandStack.pop());

      v1 = new FloatValue(66.6f);
      v2 = new FloatValue(82.8f);
      operandStack.push(v1);
      operandStack.push(v2);

      //======FloatValues in opeandStack
      // 82.8
      // 66.6
      // +===+

      System.out.println("Float Values: Value 1 = 66.6 Value 2 = 82.8");

      swap(operandStack);
      // pop() and print swapped FloatValue in opeandStack
      System.out.println("The values before swap are 66.6 82.8");
      System.out.println("The Values after swappiing are " + operandStack.pop() + " " + operandStack.pop());

      ////Q5////
      System.out.println("");
      System.out.println("///// Negate Test //////");

      v1 = new DoubleValue(4.7);
      operandStack.push(v1);

      //negate the DoubleValue 4.7
      negate(Value.s_double, operandStack);
      //print negated DoubleValue
      System.out.println("DoubleValue 4.7 negated: " + operandStack.pop());

      v1 = new IntegerValue(45);
      operandStack.push(v1);

      //negate the IntegerValue 45
      negate(Value.s_integer, operandStack);
      //print negated IntegerValue
      System.out.println("IntegerValue 45 negated: " + operandStack.pop());

      v1 = new FloatValue(66.6f);
      operandStack.push(v1);

      //negate the Float 66.6f
      negate(Value.s_float, operandStack);
      //Print negated FloatValues
      System.out.println("FloatValue 66.6 negates: " + operandStack.pop());

      ///Q6///

      Value a;
      IntegerValue va;

      System.out.println("");
      System.out.println("//// CMP TEST ////");

      // =======longValue
      operandStack.push(new LongValue(100));
      operandStack.push(new LongValue(50));

      cmp(Value.s_long, operandStack);
      a = operandStack.pop();
      va = (IntegerValue) a;

      System.out.println("long Values: Value 1 = 100, Value 2 = 50");

      switch (va.getValue()){
        case 0:
        System.out.println("Equal values.");
        break;
        case 1:
        System.out.println("Value 1 is greater than Value 2.");
        break;
        case -1:
        System.out.println("Value 1 is less than Value 2.");
        break;
        default:
        break;
      }

      // ==== FloatValue
      operandStack.push(new FloatValue(10.56f));
      operandStack.push(new FloatValue(10.56f));

      cmp(Value.s_float, operandStack);
      a = operandStack.pop();
      va = (IntegerValue) a;

      System.out.println("Float Values: Value 1 = 10.56f, Value 2 = 10.56f");

      switch (va.getValue()){
        case 0:
        System.out.println("Equal values.");
        break;
        case 1:
        System.out.println("Value 1 is greater than Value 2.");
        break;
        case -1:
        System.out.println("Value 1 is less than Value 2.");
        break;
        default:
        break;
      }

      /////Q7////

      Value a7;
      IntegerValue ai;
      FloatValue af;
      DoubleValue ad;

      System.out.println("");
      System.out.println("///// TWO TEST /////");

      //========Integer Values

      operandStack.push(new IntegerValue(40));
      two(Value.s_integer, Value.s_float, operandStack);
      a7 = operandStack.pop();
      af = (FloatValue) a7;
      System.out.println("IntegerValue = 40 to FloatValue = " + af.getValue());

      // =======FloatValues
      operandStack.push(new FloatValue(af.getValue()));

      two(Value.s_float, Value.s_double, operandStack);
      a7 = operandStack.pop();
      ad = (DoubleValue) a7;

      System.out.println("FloatValue = 40.0f to Double Value = " + ad. getValue());

      // ======= DoubleValue

      operandStack.push(new DoubleValue(ad.getValue()));

      two(Value.s_double, Value.s_integer, operandStack);
      a7 = operandStack.pop();
      ai = (IntegerValue) a7;

      System.out.println("DoubleValue = 40.0 to IntegerValue = " + ai.getValue());

      /////Q8/////

      Value va8;
      IntegerValue a8;

      System.out.println("");
      System.out.println("///// LOGIC TEST /////");

      // ======IntegerValue

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      logic(RuntimeConstants.opc_iand, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 & 13 = " + ((IntegerValue)va8).getValue());

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      logic(RuntimeConstants.opc_ior, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 | 13 = " + ((IntegerValue)va8).getValue());

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      logic(RuntimeConstants.opc_ixor, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 ^ 13 = " + ((IntegerValue)va8).getValue());

      // =====longValue

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      logic(RuntimeConstants.opc_land, operandStack);
      va8 = operandStack.pop();

      System.out.println("LongValue 60 & 13 = " + ((LongValue)va8).getValue());

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      logic(RuntimeConstants.opc_lor, operandStack);
      va8 = operandStack.pop();

      System.out.println("LongValue 60 | 13 = " + ((LongValue)va8).getValue());

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      logic(RuntimeConstants.opc_lxor, operandStack);
      va8 = operandStack.pop();

      System.out.println("LongValue 60 ^ 13 = " + ((LongValue)va8).getValue());

      /////Q9/////
      System.out.println("");
      System.out.println("///// SHIFT TEST /////");

      //=====IntegerValue

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      shift(RuntimeConstants.opc_ishl, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 << 13 = " + ((IntegerValue)va8).getValue());

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      shift(RuntimeConstants.opc_ishr, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 >> 13 = " + ((IntegerValue)va8).getValue());

      operandStack.push(new IntegerValue(60));
      operandStack.push(new IntegerValue(13));

      shift(RuntimeConstants.opc_iushr, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 >>> 13 = " + ((IntegerValue)va8).getValue());

      //=======LongValue

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      shift(RuntimeConstants.opc_lshl, operandStack);
      va8 = operandStack.pop();

      System.out.println("LongValue 60 << 13 = " + ((LongValue)va8).getValue());

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      shift(RuntimeConstants.opc_lshr, operandStack);
      va8 = operandStack.pop();

      System.out.println("IntegerValue 60 >> 13 = " + ((LongValue)va8).getValue());

      operandStack.push(new LongValue(60));
      operandStack.push(new LongValue(13));

      shift(RuntimeConstants.opc_lushr, operandStack);
      va8 = operandStack.pop();

      System.out.println("LongValue 60 >>> 13 = " + ((LongValue)va8).getValue());
      System.out.println("");
    }
}
