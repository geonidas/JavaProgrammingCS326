package Value;
/** 
 * An object of this class represents a long value.
 */

public class LongValue extends Value {
     /** 
     * Holds the actual value of this object
     */
    long value;

    /**
     * Creates a LongValue object.
     * @param lv The initial value.
     */
    public LongValue(long lv) {
	super(Value.s_long);
	value = lv;
    }
        
    /**
     * (Accessor) Returns the long value stored in the object.    
     */
    public long getValue() {
	return value;
    }

    /**
     * Returns the value of the object as a String.
     */
    public String toString() {
	return "" + value;
    }

}
