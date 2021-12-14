package EVM;
import java.util.*;
import Instruction.*;

public class Method { 
	private String    methodName;
	private int       access;
	private Signature signature;
	private ArrayList<Instruction> instructions;
	private int       varSize;
	private int       stackSize;
	private Hashtable<String,Integer> labels;


	public Method(String methodName, String signatureString, int access) {
		this.methodName = methodName;
		this.access     = access;
		this.signature  = new Signature(signatureString);
		varSize         = 0;
		stackSize       = 0;
		instructions    = new ArrayList<Instruction>();
		labels          = new Hashtable<String,Integer>();
	}

	public Signature getSignature() {
		return signature;
	}

	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

	public void addLabel(String label) {
		labels.put(label, new Integer(instructions.size()));
		// No need to keep labels around, just pretend it was actually on the following line.
	}

	public int getLabelAddress(String label) {
		return ((Integer)labels.get(label)).intValue();
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}
	public int getStackSize() {
		return stackSize;
	}

	public void setVarSize(int varSize) {
		this.varSize = varSize;
	}
	public int getVarSize() {
		return varSize;
	}

	public int getCodeSize() {
		return instructions.size();
	}

	public Instruction getInstruction(int address) {
		return (Instruction) instructions.get(address);
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isStatic() {
		return RuntimeConstants.isStatic(access);
	}

	public String toString() {
		return methodName;
	}

}
