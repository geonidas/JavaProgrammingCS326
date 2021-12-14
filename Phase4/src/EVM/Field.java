package EVM;
import Value.*;
import Instruction.RuntimeConstants;

public class Field {
	private int access;
	private String name;
	private String signature;
	private Object initValue;
	private FieldValue fieldVal; // holds the value if the field is static.


	public Field(int access, String name, String signature, Object initValue) {
		this.name      = name;
		this.signature = signature;
		this.initValue = initValue; 
		this.access    = access;
		if (isStatic()) {
			// The field is static, so we keep its value here ... This initializer is only
			// executed when the class is loaded, so static fields are only initialized
			// once, on class load time.
			if (initValue == null) 
				fieldVal = new FieldValue(Value.makeDefaultValueFromSignature(signature), this);
			else 
				fieldVal = new FieldValue(Value.makeValue(initValue), this);
		}
		else 
			// The field is not static, so it doesn't have a value here.
			fieldVal = null;
	} 

	public void setFieldValue(Value va) {
		fieldVal.setValue(va);
	}

	public boolean isStatic() {
		return RuntimeConstants.isStatic(access);
	}

	public FieldValue getFieldValue() {
		return fieldVal;
	}

	public String getName() {
		return name;
	}

	public String getSignature() {
		return signature;
	}

	public String toString() {
		return "(" + signature + ") " +getName() + (fieldVal==null?"":" = (" + fieldVal.getValue().type2String() + ") " + fieldVal.toString());
	}

}
