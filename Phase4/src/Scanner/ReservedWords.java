package Scanner;
import java.util.Hashtable;
import java_cup.runtime.*;
import Parser.*;

abstract class ReservedWords {
    static Hashtable<String, Integer> reserved_words;

    public static int get(String name) {
    	return ((Integer)reserved_words.get(name)).intValue();
    }

    public static boolean contains(String name) {
    	return reserved_words.get(name) != null;
    }

    //
    // scanner initializer - sets up reserved_words table
    //
    static {
        reserved_words = new Hashtable<String,Integer>();

        // Jasmin directives
        reserved_words.put(".catch",      new Integer(sym.DCATCH));
        reserved_words.put(".class",      new Integer(sym.DCLASS));
        reserved_words.put(".end",        new Integer(sym.DEND));
        reserved_words.put(".field",      new Integer(sym.DFIELD));
        reserved_words.put(".implements", new Integer(sym.DIMPLEMENTS));
        reserved_words.put(".interface",  new Integer(sym.DINTERFACE));
        reserved_words.put(".limit",      new Integer(sym.DLIMIT));
        reserved_words.put(".line",       new Integer(sym.DLINE));
        reserved_words.put(".method",     new Integer(sym.DMETHOD));
        reserved_words.put(".set",        new Integer(sym.DSET));
        reserved_words.put(".source",     new Integer(sym.DSOURCE));
        reserved_words.put(".super",      new Integer(sym.DSUPER));
        reserved_words.put(".throws",     new Integer(sym.DTHROWS));
        reserved_words.put(".var",        new Integer(sym.DVAR));

        // reserved_words used in Jasmin directives
        reserved_words.put("from",        new Integer(sym.FROM));
        reserved_words.put("method",      new Integer(sym.METHOD));
        reserved_words.put("to",          new Integer(sym.TO));
        reserved_words.put("is",          new Integer(sym.IS));
        reserved_words.put("using",       new Integer(sym.USING));

        // Special-case instructions
        reserved_words.put("tableswitch",  new Integer(sym.TABLESWITCH));
        reserved_words.put("lookupswitch", new Integer(sym.LOOKUPSWITCH));
        reserved_words.put("default",      new Integer(sym.DEFAULT));

        // Access flags
        reserved_words.put("public",       new Integer(sym.PUBLIC));
        reserved_words.put("private",      new Integer(sym.PRIVATE));
        reserved_words.put("protected",    new Integer(sym.PROTECTED));
        reserved_words.put("static",       new Integer(sym.STATIC));
        reserved_words.put("final",        new Integer(sym.FINAL));
        reserved_words.put("synchronized", new Integer(sym.SYNCHRONIZED));
        reserved_words.put("volatile",     new Integer(sym.VOLATILE));
        reserved_words.put("transient",    new Integer(sym.TRANSIENT));
        reserved_words.put("native",       new Integer(sym.NATIVE));
        reserved_words.put("interface",    new Integer(sym.INTERFACE));
        reserved_words.put("abstract",     new Integer(sym.ABSTRACT));
    }
}
