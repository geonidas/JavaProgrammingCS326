package Value;

/** 
 * An object of this class represents a float value.
 */ 

public class FloatValue extends Value {
    /** 
     * Holds the actual value of this object
     */
    float value;

    /**
     * Creates an FloatValue object.
     * @param fv The initial value.
     */
    public FloatValue(float fv) {
	super(Value.s_float);
	value = fv;
    }
    
    /**
     * (Accessor) Returns the float value stored in the object.    
     */
    public float getValue() {
	return value;
    }

    /**
     * Returns the value of the object as a String.
     */
    public String toString() {
	return "" + value;
    }

}
