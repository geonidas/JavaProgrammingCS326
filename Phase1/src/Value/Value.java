package Value;
/**
 * An object of this class' subclasses represent a value in the EVM
 * system. Such values are stored on the stack ({@link EVM.OperandStack})
 * or in the activation records ({@link EVM.Activation}).
 */
public abstract class Value {
    /**
     * Denotes that the object represents an integer value.
     * <p>
     * The EVM/JVM signature of integer values is I.
     */
    public static final int s_integer   = 1;
    /**
     * Denotes that the object represents an object value.
     * <p>
     * The EVM/JVM signature of object values is L&lt;Class-name&gt;;.
     */
    public static final int s_reference = 2;
    /**
     * Denotes that the object represents a double value.
     * <p>
     * The EVM/JVM signature of double values is D.
     */
    public static final int s_double    = 3;
    /**
     * Denotes that the object represents a float value.
     * <p>
     * The EVM/JVM signature of float values is F.
     */
    public static final int s_float     = 4;
    /**
     * Denotes that the object represents a long value.
     * <p>
     * The EVM/JVM signature of long values is J.
     */
    public static final int s_long      = 5;
    /**
     * Denotes that the object represents a byte value.
     * Values of this type <b>cannot</b> be stored on the operand stack. 
     * They must be promoted to integers before being pushed on the stack.
     * <p>
     * The EVM/JVM signature of byte values is B.
     */
    public static final int s_byte      = 6;
    /**
     * Denotes that the object represents a short value.
     * Values of this type <b>cannot</b> be stored on the operand stack. 
     * They must be promoted to integers before being pushed on the stack.
     * <p>
     * The EVM/JVM signature of short values is S.
     */
    public static final int s_short     = 7;
    /**
     * Denotes that the object represents a short value.
     * Values of this type <b>cannot</b> be stored on the operand stack. 
     * They must be promoted to integers before being pushed on the stack.
     * <p>
     * The EVM/JVM signature of char values is C.
     */
    public static final int s_char      = 8;
    /**
     * Denotes that the object represents a boolean value.
     * <p>
     * The EVM/JVM signature of boolean values is Z.
     */
    public static final int s_boolean   = 9;
    /**
     * Denotes that the object represents a string value.  The string
     * type is atomic in Espresso, thus we treat it like an atomic
     * value in the EVM. However, it is really a reference to an
     * object of the class java/lang/String. 
     * <p>
     * The EVM/JVM signature of string values is Ljava/lang/String;
     */
    public static final int s_string    = 10;

    /**
     * Denotes the type that an instance of this class represents.
     */
    private final int type;

    /**
     * This constructor is only called as 'super(...)' from a subclass
     * of this class as this class is abstract. It serves to assure
     * that the type field is set for all object created from sub
     * classes of this class.
     */
    Value(int type) {
	this.type = type;
    }

    /**
     * Creates an object of one of Value's subclasses based on the
     * value of the parameter s.  
     * All numeric values are initialized to 0, booleans to true, chars to ' ', and string to "".
     * <p>
     * @param s Possible values for this parameter are:<p>
     * <table border=1 cellpadding=2>
     * <tr> <th> s </th> <th> Type </th> <th> Initial value </th></tr>
     * <tr> <td> I </td> <td> integer </td> <td> 0 </td></tr>
     * <tr> <td> D </td> <td> double </td> <td> 0.00 </td></tr>
     * <tr> <td> F </td> <td> float </td> <td> 0.00 </td></tr>
     * <tr> <td> Z </td> <td> boolean </td> <td> true </td></tr>
     * <tr> <td> J </td> <td> long </td> <td> 0 </td></tr>
     * <tr> <td> S </td> <td> short </td> <td> 0 </td></tr>
     * <tr> <td> C </td> <td> char </td> <td> ' ' </td></tr>
     * <tr> <td> B </td> <td> byte </td> <td> 0 </td></tr>
     * <tr> <td> Ljava/lang/String; </td> <td> string </td> <td> "" </td></tr>
     * </table>
     * <p>
     * Note, object of reference type cannot be created using this method.
     * @return Returns a new object of a subclass of the {@link Value} type.
     * This object is initialized as described above.
     */
    public static Value makeDefaultValueFromSignature(String s) {
	if (s.equals("I")) 
	    return new IntegerValue(0);
	else if (s.equals("D")) 
	    return new DoubleValue(0.00);
	else if (s.equals("F")) 
	    return new FloatValue(0.00F);
	else if (s.equals("Z")) 
	    return new IntegerValue(1);    // We represent booleans as integers
	else if (s.equals("J")) 
	    return new LongValue(0L);
	else if (s.equals("S")) 
	    return new IntegerValue(0);
	else if (s.equals("C"))           // We repreent chars as integers as well
	    return new IntegerValue(0);
	else if (s.equals("B")) 
	    return new IntegerValue(0);
	else if (s.equals("Ljava/lang/String;")) 
	    return new StringValue("");
	else 
	    return null; // return new ReferenceValue((Object_)null);
    }
    
    /**
     * Creates a EVM Value object based on a java object of type
     * String, Integer, Double, Float or Long. Remember that the
     * Integer and the integer types are different, Integer is a class
     * where as integer is an atomic (i.e., built in) type. The same
     * goes for Long/long, Double/double, and Float/float.
     *
     * @param o An object of type String, Integer, Double, Float or Long
     * @return A new object of the Value type with the same value as
     * the o parameter.
    */
    public static Value makeValue(Object o) {
	if (o instanceof String) {
	    Value va = new StringValue((String)o); 
	    return va;
	} else if (o instanceof Integer)
	    return makeValue(((Integer)o).intValue());
	else if (o instanceof Double)
	    return makeValue(((Double)o).doubleValue());
	else if (o instanceof Float)
	    return makeValue(((Float)o).floatValue());
	else if (o instanceof Long) 
	    return makeValue(((Long)o).longValue());
	else if (o instanceof IntegerValue)
	    return new IntegerValue(((IntegerValue)o).getValue());
	else if (o instanceof LongValue)
	    return new LongValue(((LongValue)o).getValue());
	else if (o instanceof FloatValue)
	    return new FloatValue(((FloatValue)o).getValue());
	else if (o instanceof DoubleValue)
	    return new DoubleValue(((DoubleValue)o).getValue());
	else if (o instanceof IntegerValue)
	    return new IntegerValue( ((IntegerValue)o).getValue() );
	else if (o instanceof LongValue)
	    return new LongValue( ((LongValue)o).getValue());
	else if (o instanceof FloatValue)
	    return new FloatValue( ((FloatValue)o).getValue());
	else if (o instanceof DoubleValue)
	    return new DoubleValue( ((DoubleValue)o).getValue());
	//else if (o instanceof ReferenceValue)
	    //    return new ReferenceValue( ((ReferenceValue)o).getValue());
	else if (o instanceof StringValue)
            return new StringValue(((StringValue)o).getValue());
	System.out.println("Unknown type encountered." + o);
	Thread.dumpStack();
	System.exit(1);
	return null;
    }
    
    
    /**
     * Creates a value object based on a (atomic) long value.
     * @param lv long value.
     * @return LongValue object with a long value.
     */
    public static Value makeValue(long lv) {
	return new LongValue(lv);
    }

    /**
     * Creates a value object based on a (atomic) int value.
     * @param iv integer value.
     * @return IntegerValue object with a integer value.
     */
    public static Value makeValue(int iv) {
	return new IntegerValue(iv);
    }

    /**
     * Creates a value object based on a (atomic) float value.
     * @param fv float value.
     * @return FloatValue object with a float value.
     */
    public static Value makeValue(float fv) {
	return new FloatValue(fv);
    }

    /**
     * Creates a value object based on a (atomic) double value.
     * @param dv double value.
     * @return DoubleValue object with a double value.
     */
    public static Value makeValue(double dv) {
	return new DoubleValue(dv);
    }

    /**
     * Creates a value object based on a (atomic) char value.
     * @param cv char value.
     * @return IntegerValue object with a char value.
     */
    public static Value makeValue(char cv) {
	return new IntegerValue(cv);
    }

    /**
     * Creates a value object based on a (atomic) boolean value.
     * @param bv boolean value.
     * @return IntegerValue object with a boolean value.
     */    
    public static Value makeValue(boolean bv) {
	return new IntegerValue(bv?1:0);
    }

    /**
     * Returns the type of value.
     */
    public int getType() {
	return type;
    }
    
    /**
     * Converts a signature string to a type constant.
     *
     * @param s string representing the signature of the type
     * @return integer value representing the corresponding type (as
     * defined in {@link Value}.
    */
    public static int signature2Type(String s) {
	if (s.equals("I")) 
	    return s_integer;
	else if (s.equals("D")) 
	    return s_double;
	else if (s.equals("F")) 
	    return s_float;
	else if (s.equals("Z")) 
	    return s_boolean;
	else if (s.equals("J")) 
	    return s_long;
	else if (s.equals("S")) 
	    return s_short;
	else if (s.equals("C")) 
	    return s_char;
	else if (s.equals("B")) 
	    return s_byte;
	else if (s.equals("Ljava/lang/String;")) 
	    return s_string;
	else 
	    return s_reference;
    }

    /**
     * Returns a value's type as a string.
     *
     * @return A string representing the type of the value.
     */
    public String type2String() {
	switch (type) {
	case s_integer   : return "integer";
	case s_reference : return "reference";
	case s_double    : return "double";
	case s_float     : return "float";
	case s_long      : return "long";
	case s_byte      : return "byte";
	case s_short     : return "short";
	case s_char      : return "char";
	case s_boolean   : return "boolean";
	case s_string    : return "string";
	}
	return "";
    }
    
    /**
     * Abstract method for all subclasses to implement. An
     * implementation of this method should return the value of the
     * object as a string.
     */
    public abstract String toString();
    



}    
