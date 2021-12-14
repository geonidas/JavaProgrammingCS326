package Value;
import EVM.Object_;

/** 
 * An object of this class represents a reference value.
 */

public class ReferenceValue extends Value {
	/** 
	 * Holds the actual value of this object. All reference values in
	 * EVM are reference to an object of thew Object_ class.
	 */
	Object_ value;

	/**
	 * Creates a ReferenceValue object.
	 * @param rv The initial value.
	 */
	public ReferenceValue(Object_ rv) {
		super(Value.s_reference);
		value = rv;
	}

	/**
	 * (Accessor) Returns the reference value stored in the object.    
	 */
	public Object_ getValue() {
		return value;
	}

	/**
	 * Returns the name of the class that the reference value represents.
	 * If the value is null, the string "null" is returned.
	 */
	public String toString() {
		if (value == null)
			return "null";
		else
			return "&" + value.className();
	}

}
