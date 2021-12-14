package EVM;
import Value.*;

public class FieldValue {
	private Value value;
	private Field field;

	public FieldValue(Value value, Field field) {
		this.value = value;
		this.field = field;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value se) {
		value = se;
	}

	public Field getField() {
		return field;
	}

	public String toString() {
		return "" + value;
	}

}
