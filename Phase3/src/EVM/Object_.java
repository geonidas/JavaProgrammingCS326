package EVM;
import java.util.*;
import Value.*;
import Instruction.FieldRef;
import Utilities.Error;

// All StackElements of reference type are instances of Object_ !!

public class Object_ {
	private Class    thisClass;
	public Object_   superClassObj;
	public Hashtable<String,FieldValue> fieldValues;

	// An object stores all its field values in a hashtable as pairs
	// (field-name,value)= (name:string,value:Fieldvalue(Value,Field))
	// 
	// All static fields are stored in the class and not in the object.

	public Object_(Class thisClass) {
		this.thisClass = thisClass;
		fieldValues = new Hashtable<String,FieldValue>();

		if (thisClass.hasSuperClass()) 
			superClassObj = new Object_(ClassList.getClass(thisClass.getSuperClass()));
		for(Enumeration e=thisClass.getFields().elements();  e.hasMoreElements(); ) {
			// insert into the hash table with the name of the field a fieldvalue:
			// (Stackelement: the type of the field, Field: the field definition itself)
			Field f = (Field)e.nextElement();
			FieldValue fv;
			// If the field is static then it should not belong to the object.
			if (!f.isStatic()) {
				fv = new FieldValue(Value.makeDefaultValueFromSignature(f.getSignature()), f);
				fieldValues.put(f.getName(), fv);
			}
		}
	}

	public Class getThisClass() {
		return thisClass;
	}

	public Object_ getSuperClassObject() {
		return superClassObj;
	}

	public String className() {
		return thisClass.getClassName();
	}

	public String toString() {
		return "&" + className(); 
	}   

	public String printFields() {
		String s = "";
		Enumeration elements = fieldValues.elements();
		FieldValue fv;
		while (elements.hasMoreElements()) {
			fv = (FieldValue) elements.nextElement();
			if (fv != null)
				s = s + fv.getField().getSignature()+": "+ fv.getField().getName() + " = " + fv.getValue() + "\n";
		}
		return s;
	}


	public Value getField(FieldRef fr) {
		FieldValue fv = (FieldValue) fieldValues.get(fr.getFieldName());
		if (fv == null && superClassObj != null) 
			return superClassObj.getField(fr);
		else if (fv == null)
			Error.error("getFieldValue Error: " + fr.getFieldName() + " not found.");
		else
			return fv.getValue();
		return null;
	}

	public void putField(FieldRef fr, Value v) {
		FieldValue fv = (FieldValue) fieldValues.get(fr.getFieldName());
		if (fv == null && superClassObj != null)
			superClassObj.putField(fr, v);
		else if (fv == null) 
			Error.error("putField Error: field '" + fr.getFieldName() + "' not found.");
		else {
			if (Value.signature2Type(fr.getSignature()) == Value.s_char && v.getType() == Value.s_integer) {
				fv.setValue(new IntegerValue((char)((IntegerValue)v).getValue()));
				return;
			}
			else if (Value.signature2Type(fr.getSignature()) == Value.s_byte && v.getType() == Value.s_integer) {
				fv.setValue(new IntegerValue((byte)((IntegerValue)v).getValue()));
				return;
			}
			else if (Value.signature2Type(fr.getSignature()) == Value.s_short && v.getType() == Value.s_integer) {
				fv.setValue(new IntegerValue((short)((IntegerValue)v).getValue()));
				return;
			}
			int fieldType = Value.signature2Type(fr.getSignature());
			if (Value.signature2Type(fr.getSignature()) == Value.s_boolean ||
					Value.signature2Type(fr.getSignature()) == Value.s_char ||
					Value.signature2Type(fr.getSignature()) == Value.s_byte ||
					Value.signature2Type(fr.getSignature()) == Value.s_short)
				fieldType = Value.s_integer;

			if (v.getType() != fieldType)
				Error.error("putField: Type mismatch. " + v.getType() + " vs. " + Value.signature2Type(fr.getSignature()));
			fv.setValue(v);
		}
	}
}






