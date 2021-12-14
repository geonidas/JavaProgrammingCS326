package Instruction;
import Value.*;

public class LdcDouble extends Ldc {
	private double value;
	public LdcDouble(String inst, double doubleOperand) {
		super(inst);
		value = doubleOperand;
	}

	public Value getValue() {
	    return new DoubleValue(value);
	}

	public String toString() {
		return super.toString() + " " + value;
	}
}


