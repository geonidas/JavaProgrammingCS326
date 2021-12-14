package EVM;
import Value.*;
import java.util.*;

public class Signature {
    private String stringsig;
    private String[] signature;
    private String returnType;

    public Signature(String sig) {
	stringsig = sig;
	signature = breaksignature(sig);
	returnType = signature[signature.length-1];
    }

    public String[] getSignature() {
	return signature;
    }

    public String getReturnType() {
	return returnType;
    }

    public int size() {
	return signature.length-1;
    }

    public String toString() {
	String s = "(";
	for (int i=0;i<size();i++) {
	    s = s + signature[i];
	}
	s = s + ")" + returnType;
	return s;
    }

    public static String[] breaksignature(String s) {
        char[] chars = s.toCharArray();
        int cc = 0;
        int len;
        String news ="";
        int count = 0;

        len = chars.length;

	List<String> strings = new ArrayList<String>();

        for (int i=0; i<len; i++) {
            if (cc == 1) {
                if (chars[i] == ';') {
                    cc = 0;
                    strings.add(news + chars[i]);
                    news = "";
                }
                else
                    news = news + chars[i];
            }
            else {
                if (chars[i] == 'L') {
                    cc = 1;
                    news = "" + chars[i];
                }
                else {
                    if (chars[i] == '(' || chars[i] == ')')
                        ;
                    else {
                        strings.add("" + chars[i]);
                    }
                }
            }
        }
        return (String[]) strings.toArray(new String[strings.size()]);
    }
    
    public static int stringSigToType(String s) {
	if (s.equals("I"))
	    return Value.s_integer;
	else if (s.equals("D"))
	    return Value.s_double;
	else if (s.equals("F"))
	    return Value.s_float;
	else if (s.equals("L"))
	    return Value.s_long;
	else if (s.equals("Z"))
	    return Value.s_boolean;
	else if (s.equals("S"))
	    return Value.s_short;
	else if (s.equals("C"))
	    return Value.s_char;
	else if (s.equals("B"))
	    return Value.s_byte;
	else 
	    return Value.s_reference;
    }    

}

		    
		


