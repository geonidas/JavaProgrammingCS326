package Instruction;

public class Instruction {

	public static final int INTEGER = 1;
	public static final int FLOAT   = 2;
	public static final int LONG    = 3;
	public static final int DOUBLE  = 4;
	public static final int STRING  = 5;

	private int opCode;

	public Instruction(String inst) {
		this.opCode = InstInfo.get(inst).opcode;
	}

	public int getOpCode() {
		return opCode;
	}

	public String getName() {
		return RuntimeConstants.opcNames[opCode];
	}
	public String toString() {
		return getName();
	}

	// this won't be useful for all instructions, but it saves us a lot
	// of casting if it is available in this class rather than only in 
	// the subclasses.
	public int getOperand() {
		return 0;
	}

}
