package Instruction;
import Value.*;

public class LdcLong extends Ldc {
	private long value;
	public LdcLong(String inst, long longOperand) {
		super(inst);
		value = longOperand;
	}

	public Value getValue() {
	    return new LongValue(value);
	}

	public String toString() {
		return super.toString() + " " + value;
	}

}












