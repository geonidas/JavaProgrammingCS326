package Value;
/** 
 * An object of this class represents an double value.
 */ 

public class DoubleValue extends Value {
    /** 
     * Holds the actual value of this object
     */
    double value;

    /**
     * Creates an DoubleValue object.
     * @param dv The initial value.
     */
    public DoubleValue(double dv) {
	super(Value.s_double);
	value = dv;
    }

    /**
     * (Accessor) Returns the double value stored in the object.    
     */
    public double getValue() {
	return value;
    }

    /**
     * Returns the value of the object as a String.
     */
    public String toString() {
	return "" + value;
    }

}
