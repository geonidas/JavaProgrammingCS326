package Instruction;
import Value.*;

public class LdcInteger extends Ldc {
	private int value;
	public LdcInteger(String inst, int intOperand) {
		super(inst);
		value = intOperand;
	}

	public Value getValue() {
	    return new IntegerValue(value);
	}

	public String toString() {
		return super.toString() + " " + value;
	}
}











