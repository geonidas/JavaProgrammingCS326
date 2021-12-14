package Value;

public class StringValue extends Value {
    String value;

    public StringValue(String sv) {
	super(Value.s_string);
	value = sv;
    }

    public String getValue() {
	return value;
    }

    public String toString() {
	return value;
    }

}
