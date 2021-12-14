
import java.util.*;
import Utilities.Error;
import OperandStack.*;
import Value.*;

public class EVM {

    public static void main(String argv[]) {
        
        OperandStack operandStack = new OperandStack(100, "OperandStack Test");
        if (argv.length == 0) {
            // This is a small example of how to use the stack
            operandStack.push(new IntegerValue(100));
            operandStack.push(Value.makeDefaultValueFromSignature("Z"));
            operandStack.push(Value.makeValue("Hello"));
            System.out.println(operandStack.peek(1));
            System.out.println(operandStack.peek(2));
            // if we have an expression line 1 + 2 we can translate that into postfix 
            // like this: 1 2 +
            // so it becomes: push(1), push (2), pop() twice and add and push the result.
            operandStack.push(new IntegerValue(1));
            operandStack.push(new IntegerValue(2));
            Value v1, v2;
            v2 = operandStack.pop();
            v1 = operandStack.pop();
            // Note, since the stack holds 'Value' values we have to cast the 
            // values popped to the right kind.
            IntegerValue v3, v4;
            v4 = (IntegerValue) v2;
            v3 = (IntegerValue) v1;

            operandStack.push(new IntegerValue(v3.getValue() + v4.getValue()));
            System.out.println(operandStack.pop()); // ===> 11
            System.out.println(operandStack.pop()); // ===> Hello
            System.out.println(operandStack.pop()); // ===> true
            System.out.println(operandStack.pop()); // ===> 100
//      System.out.println(operandStack.pop()); // ===> Error message & program termination
//      System.out.println("This line should never be printed out");
            // Remove the code above and replace it by your own tests from the assignment
        }
        else { // this section tests operand stack using an input file of infix expressions
            // we need translate an expression line a + b * c into postfix like
            // this: a b c * +
            for (int i = 0; i < argv.length; ++i) {
                PostfixExpr postfix = new PostfixExpr();
                postfix.translate(argv[0]);
                postfix.evaluate(operandStack);
            }
        }
    }

    // DO NOT MODIFY!
    
    /**
     * A class used to translate an arithmetic expression into postfix given
     * an infix like 2 + 3 * 5 into 2 3 5 * +
     * @author      Ben Cisneros
     * @version     1.0
     */
    private static class PostfixExpr {
        
        /**
         * The input to be translated.
         */
        private String line;
        /**
         * The operators and operands to be processed.
         */
        private String[] tokens;
        /**
         * The internal list holding some postfix values. These values are of
         * type {@link Value}.
         */
        private Stack result = new Stack();
        
        /**
         * Precedence levels for arithmetic operators.
         */
        private static final int PARENTHESES = 0;
        private static final int ADDITIVE = 1;
        private static final int MULTIPLICATIVE = 2;
        private static final int EXPONENTIATION = 3;
        
        public void translate(String line) {
            this.line = line;
            this.tokens = line.split("\\s+");
            Stack s = new Stack();
            int pos = 0;
            while (pos < tokens.length) {
                if (!isOperator(tokens[pos]))
                    result.push(Integer.parseInt(tokens[pos]));
                else if (tokens[pos].equals("(")) {
                    s.push(tokens[pos]);
                } else if (tokens[pos].equals(")")) {
                    while (!s.peek().equals("("))
                        result.push(s.pop());
                    s.pop();
                } else {
                    while (s.size() > 0 && precedes((String) s.peek(), tokens[pos])) {
                        if (!s.peek().equals("("))
                            result.push(s.pop());
                        else
                            s.pop();
                    }
                    if (!tokens[pos].equals(")"))
                        s.push(tokens[pos]);
                }
                ++pos;
            }
            while (s.size() > 0)
                result.push(s.pop());
        }
        
        public void evaluate(OperandStack operandStack) {
            if (result.empty())
                throw new RuntimeException("Can't evaluate an empty postfix expression.");
            Iterator it = result.iterator();
            while (it.hasNext()) {
                Object token = it.next();
                if (!(token instanceof Integer)) {
                    IntegerValue v2 = (IntegerValue) operandStack.pop();
                    IntegerValue v1 = (IntegerValue) operandStack.pop();
                    operandStack.push(evaluateOp(v1, v2, (String) token));
                } else
                    operandStack.push(new IntegerValue((Integer) token));
            }
            System.out.println(((IntegerValue) operandStack.pop()).getValue());
        }
        
        private IntegerValue evaluateOp(IntegerValue v1, IntegerValue v2, String op) {
            switch (op) {
                case "+": return new IntegerValue(v1.getValue() + v2.getValue());
                case "-": return new IntegerValue(v1.getValue() - v2.getValue());
                case "*": return new IntegerValue(v1.getValue() * v2.getValue());
                case "/": return new IntegerValue(v1.getValue() / v2.getValue());
                case "^": return new IntegerValue((int) Math.pow(v1.getValue(), v2.getValue()));                    
                default:
                    throw new IllegalArgumentException("Invalid operator: '" + op + "'");
            }
        }
        
        private boolean precedes(String op1, String op2) {
            return (getPrecedence(op1) > getPrecedence(op2)) ||
                   (getPrecedence(op1) == getPrecedence(op2) &&
                    getAssociativity(op1) == LEFT_ASSOCIATIVITY);
        }
        
        private static final int RIGHT_ASSOCIATIVITY = 0;
        private static final int LEFT_ASSOCIATIVITY = 1;
        
        private int getAssociativity(String op) {
            switch (op) {
                case "+":
                case "-":
                case "*":
                case "/": return LEFT_ASSOCIATIVITY;
                case "^": return RIGHT_ASSOCIATIVITY;
                    
                default:
                    throw new IllegalArgumentException("Invalid operator: '" + op + "'");
            }
        }
        
        private int getPrecedence(String op) {
            switch (op) {
                case "+":
                case "-": return ADDITIVE;
                case "*":
                case "/": return MULTIPLICATIVE;
                case "^": return EXPONENTIATION;
                case "(":
                case ")": return PARENTHESES;
                    
                default:
                    throw new IllegalArgumentException("Invalid operator: '" + op + "'");
            }
        }
        
        private boolean isOperator(String op) {
            return op.length() == 1 && "()*-/+^".contains(op);
        }
        
        public String toString() {
            return "";
        }
    }
}
