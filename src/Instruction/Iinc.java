package Instruction;

/**
 * iinc
 */
public class Iinc extends Instruction {
	private int operand1;
	private int operand2;

	public Iinc(String inst, int operand1, int operand2) {
		super(inst);
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public String toString() {
		return super.toString() + " " + operand1 + " " + operand2;
	}

	public int getVarNo() {
		return operand1;
	}

	public int getInc() {
		return operand2;
	}
}


