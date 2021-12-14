package Value;
/** 
 * An object of this class represents an integer value. This class
 * is also used for representing characters (char), shorts (short),
 * bytes (byte) and boolean values (1=true, 0=false)
 */ 

public class IntegerValue extends Value {
    /** 
     * Holds the actual value of this object
     */
    private int value;
    
    /**
     * Creates an IntegerValue object.
     * @param iv The initial value.
     */
    public IntegerValue(int iv) {
	super(Value.s_integer);
	value = iv;
    }
    
    /**
     * (Accessor) Returns the integer value stored in the object.    
     */
    public int getValue() {
	return value;
    }
    
    /**
     * Returns the value of the object as a String.
     */
    public String toString() {
	return "" + value;
    }

}
