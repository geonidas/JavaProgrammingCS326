package Instruction;
import Value.*;

public class LdcString extends Ldc {
	private String value;
	public LdcString(String inst, String stringOperand) {
		super(inst);
		value = stringOperand;
	}

	public Value getValue() {
	    return new StringValue(value);
	}

	public String toString() {
		return super.toString() + " \"" + value + "\"";
	}
}








