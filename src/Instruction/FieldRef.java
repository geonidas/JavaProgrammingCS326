package Instruction;
import Utilities.ScannerUtils;

/**
 * getfield, getstatic, putfield, putstatic
 */
public class FieldRef extends Instruction {
	private String className;
	private String fieldName;
	private String signature;

	public FieldRef(String inst, String field, String signature) {
		super(inst);
		String[] a;
		a = ScannerUtils.splitClassField(field);
		this.className = a[0];
		this.fieldName = a[1];
		this.signature = signature;
	}

	public String toString() {
		return super.toString() + " " + className + " " + fieldName + " " + signature;
	}

	public String getClassName() {
		return className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getSignature() {
		return signature;
	}
}






