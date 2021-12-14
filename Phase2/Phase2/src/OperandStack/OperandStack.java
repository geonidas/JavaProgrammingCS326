package OperandStack;
import Value.*;
import java.io.*;
import Utilities.Error;
/**
 * A stack for doing arithmetic and logic operations. Stack elements are of type {@link Value}.
 * @author   Matt B. Pedersen
 * @version  1.0
*/
public class OperandStack {

    /**
     * The size of the operand stack. We could have made this dynamic, but as long as we choose a number
     * large enough it should be fine with a fixed sized stack
     */
    private int stackSize;
    /** 
     * The internal stack holding the actual values. These values are of type {@link Value}.
     */
    // Hint, if you don't make it an array the toString() method wont work as it is ;-)
    //<--
    private Value[] stack;
    //-->
    /**
     * The stack pointer. The stack pointer always points to the next free location in the <a href="#stack">stack<a> array.
     */
    //<--
    private int sp = 0;
    //-->
    /**
     * Just a name to give to the stack - helps with debugging later.
     */
    private String name;
    /**
     * We keep track of the numbers of stacks created - just for statistics later.
     */
    private static int stackNo = 0;
    public int stackNumber;

    /**
     * Creates a new operand stack of size <b>size</b> and sets the stack pointer to 0.
     * @param size The size of the newly created operand stack.
     * @see #stack
     * @see #sp 
     */
    public OperandStack(int size, String name) {
	this.name = name;
	stackNumber = stackNo;
	stackNo++;

	//<--
	stackSize = size;
	stack = new Value[stackSize];
	sp = 0;
	//-->
    }

    /**
     * Pushes one element of type {@link Value} on to the operand stack and increments the stack pointer (sp) by one.
     * <p>
     * stack before push: .... X<br>
     * push(Y);<br>
     * stack after push:  .... X Y
     * <p>
     * An error is signaled if no more room is available on the stack.
     * @param e An object of the {@link Value} type to be placed on the stack.
     */
    public void push(Value e) {
	//<--
	if (sp < stackSize) {
	    stack[sp++] = e;
	    //dump();
	} else 
	    Error.error(stackNumber + " OperandStack.push: Stack overflow.");
	//-->
    }

    /**
     * Pops one element of type {@link Value} off the operand stack and decrements the stack pointer (sp) by one.
     * <p>
     * stack before pop: .... X Y<br>
     * Z = pop();<br>
     * stack after pop:  .... X<br>
     * and Z = Y
     * <p>
     * An error is signaled if the stack is empty.
     * @return Returns an object of type {@link Value}.
     */
    public Value pop() {
	//<--
	if (sp >0) 
	    return stack[--sp];
	else
	    Error.error(stackNumber +" OperandStack.pop: Stack underflow.");
	return null;
	//-->
    }
    
    /** 
     * Returns the n'th element on the stack (counted from the top)
     * without removing it.
     *
     * @param n The index (counting from the top of the stack) of the 
     * element to be returned. The top element is at index 1.
     */
    public Value peek(int n) {
	//<--
	if (sp-(n-1) > 0)
	    return stack[sp-n];
	else 
	    Error.error(stackNumber + " OperandStack.peek: Stack underflow.");
	return null;
	//-->
    }
	    
    /**
     * Prints out the operand stack with information about every elements type.
     */
    public void dump(PrintWriter out) {
	out.println(toString());
    }

    public String toString() {
	String s = "";
	s = "| Operand Stack " + stackNumber + " - "+ name + " (size = "+sp+") --------------- \n| ";
        for (int i=0;i<sp;i++) {
            if (stack[i] == null)
                s = s + "null ";
            else
                s = s + stack[i] + "{" + stack[i].type2String()+ "} ";
        }
	s = s + "\n+----------------------------------------------------------------";
	return s;
    }


    public void dump_(PrintWriter out) {
	int max = 20;
	// compute the max width of any element
	for (int i=0;i<sp;i++) {
	    if (stack[i] == null)
		max = 6 > max ? 6 : max; // ' null '
	    else {
		int l;
		l  = (stack[i] + " {" + stack[i].type2String()+ "}").length();
		max = l > max ? l : max;
	    }   
	}
	
	int left, right, spcs;

	for (int i=0;i<sp;i++) {
	    if (stack[sp-i-1] == null) {
		spcs = max - 6;
		left = spcs/2;
		right = spcs - left;
		out.println("|" + spaces(left) + " null " + spaces(right) + "|");
	    } else {
		String st = "" + stack[sp-i-1] + " {" + stack[sp-i-1].type2String()+ "}";
		spcs = max - st.length();
		left = spcs / 2;
		right = spcs - left;
		out.println("|" + spaces(left) + st + spaces(right) + "|");
	    }
	}
    }
    
    private String spaces(int n) {
	String s = "";
	for (int i =0; i<n; i++) 
	    s += " ";
	return s;
    }


}


	    
