package Instruction;
import Value.*;

public class LdcFloat extends Ldc {
	private float value;
	public LdcFloat(String inst, float floatOperand) {
		super(inst);
		value = floatOperand;
	}

	public Value getValue() {
	    return new FloatValue(value);
	}

	public String toString() {
		return super.toString() + " " + value;
	}
}
