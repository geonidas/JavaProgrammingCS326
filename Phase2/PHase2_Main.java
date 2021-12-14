package bin;
import java.util.*;
import java.io.*;
import EVM.*;
import Utilities.*;
import Utilities.Error;
import Value.*;
import OperandStack.*;
import Instruction.*;


public class PHase2_Main{

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
    System.out.println("///// Negate Test //////");

    v1 = new DoubleValue(4.7);
    operandStack.push(v1);

    //negate the DoubleValue 4.7
    negate(Value.s_double, operandStack);
    //print negated DoubleValue
    System.out.println("DoubleValue 4.7 negated: " + operandStack.pop());

    v1 = IntegerValue(45);;
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
    System.out.println("longValue 661 negates: " + operandStack.pop());

    ///Q6///

    Value a;
    IntegerValue va;

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

    //=====IntegerValue

    operandStack.push(new IntegerValue(60));
    operandStack.push(new IntegerValue(13));

    logic(RuntimeConstants.opc_ishl, operandStack);
    va8 = operandStack.pop();

    System.out.println("IntegerValue 60 << 13 = " + ((IntegerValue)va8).getValue());

    operandStack.push(new IntegerValue(60));
    operandStack.push(new IntegerValue(13));

    logic(RuntimeConstants.opc_ishr, operandStack);
    va8 = operandStack.pop();

    System.out.println("IntegerValue 60 >> 13 = " + ((IntegerValue)va8).getValue());

    operandStack.push(new IntegerValue(60));
    operandStack.push(new IntegerValue(13));

    logic(RuntimeConstants.opc_ishr, operandStack);
    va8 = operandStack.pop();

    System.out.println("IntegerValue 60 >>> 13 = " + ((IntegerValue)va8).getValue());

    //=======LongValue

    operandStack.push(new LongValue(60));
    operandStack.push(new LongValue(13));

    logic(RuntimeConstants.opc_lshl, operandStack);
    va8 = operandStack.pop();

    System.out.println("LongValue 60 << 13 = " + ((LongValue)va8).getValue());

    operandStack.push(new LongValue(60));
    operandStack.push(new LongValue(13));

    logic(RuntimeConstants.opc_lshr, operandStack);
    va8 = operandStack.pop();

    System.out.println("IntegerValue 60 >> 13 = " + ((IntegerValue)va8).getValue());

    operandStack.push(new LongValue(60));
    operandStack.push(new LongValue(13));

    logic(RuntimeConstants.opc_lshr, operandStack);
    va8 = operandStack.pop();

    System.out.println("LongValue 60 >>> 13 = " + ((LongValue)va8).getValue());

  }
}
