package EVM;
import java.util.*;
import java.io.*;
import Value.*;
import Instruction.*;
import Utilities.Error;
import Utilities.ScannerUtils;

public class Class {
	private String source;
	private String className;
	private int access;
	private String superClass;
	private Set<String> interfaces;
	private Hashtable<String,Field> fields;
	private Hashtable<String,Method> methods;
	private Method currentMethod;


	public Class() {
		interfaces = new HashSet<String>();
		fields     = new Hashtable<String,Field>();
		methods    = new Hashtable<String,Method>();
	}

	public static boolean doesExtend(Class objcl, Class cl) {
		if (objcl.getClassName().equals(cl.getClassName())) 
			return true;
		else {
			boolean found = false;
			if (objcl.hasSuperClass()) {
				found = doesExtend(ClassList.getClass(objcl.getSuperClass()), cl);
				if (found) 
					return true;
				for (Iterator ii = objcl.getInterfaces().iterator(); ii.hasNext(); ) 
					found = found || doesExtend(ClassList.getClass((String)ii.next()), cl);
				return found;
			}
			return false;
		}
	}

	public String toString() {
		return className;
	}

	// Source

	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}

	// Class

	public void setClassName(String className, int access) {
		this.className = className;
		this.access    = access;
	}
	public String getClassName() {
		return className;
	}

	// Super Class

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}
	public String getSuperClass() {
		return superClass;
	}

	public boolean hasSuperClass() {
		return !superClass.equals("java/lang/Object");
	}    

	// Interfaces

	public void addInterface(String name) {
		interfaces.add(name);
	}

	public Set getInterfaces() {
		return interfaces;
	}

	// Fields

	public void addField(Field field) {
		fields.put(field.getName(), field);
	}
	public Hashtable getFields() {
		return fields;
	}

	public Field getField(String name) {
		return (Field)fields.get(name);
	}

	// Method

	public void beginMethod(int access, String name, String signature) {
		currentMethod = new Method(name, signature, access);
	}

	public void endMethod() {
		methods.put(currentMethod.getMethodName()+"/"+currentMethod.getSignature(), currentMethod);
		currentMethod = null;
	}

	public Method getNonvirtualMethod(String name) {
		return (Method)methods.get(name);
	}

	public Method getVirtualMethod(String name) {
		Method m = (Method)methods.get(name);
		if (m != null) {
			return m;
		}
		else
			if (hasSuperClass())
				return ClassList.getClass(superClass).getVirtualMethod(name);
		return null;
	}

	public Hashtable getMethods() {
		return methods;
	}

	public Method currentMethod() {
		return currentMethod;
	}

	// Instructions

	public void addInstruction(Instruction instruction) {
		currentMethod.addInstruction(instruction);
	}

	// Labels

	public void addLabel(String label) {
		currentMethod.addLabel(label);
	}

	// Put/Get Field Values

	public void putStatic(Class cl, FieldRef fr, Value va) {
		// Fields from interfaces are by default FINAL and STATIC - so they cannot be assigned to,
		// therefore we only need to look in the chain of superclasses then.
		Field f = cl.getField(fr.getFieldName());
		if (f == null) 
			if (cl.hasSuperClass()) 
				putStatic(ClassList.getClass(cl.getSuperClass()), fr ,va);	    
			else 
				Error.error("Error: field '" + fr.getFieldName() + "' not found.");
		else {
			if (!f.isStatic()) 
				Error.error("Error: field '" + fr.getFieldName() + "' is not static.");
			else {
				if (f.getFieldValue().getValue().getType() == Value.s_char && va.getType() == Value.s_integer) {
					f.setFieldValue(new IntegerValue((char)((IntegerValue)va).getValue()));
					return;
				}
				else if (f.getFieldValue().getValue().getType() == Value.s_byte && va.getType() == Value.s_integer) {
					f.setFieldValue(new IntegerValue((byte)((IntegerValue)va).getValue()));
					return;
				}
				else if (f.getFieldValue().getValue().getType() == Value.s_short && va.getType() == Value.s_integer) {
					f.setFieldValue(new IntegerValue((short)((IntegerValue)va).getValue()));
					return;
				}
				if (f.getFieldValue().getValue().getType() != va.getType())
					Error.error("Error: Static field is of type '" + f.getFieldValue().getValue().type2String() + "', but value is of type '" + va.type2String() + "'.");
				f.setFieldValue(va);
			}
		}
	}


	public Value getStatic(FieldRef fr) {   
		Class cl = this;
		Field f = cl.getField(fr.getFieldName());
		FieldValue fva = null;
		Value va = null;
		if (f == null) {
			if (cl.hasSuperClass()) 
				va = ClassList.getClass(cl.getSuperClass()).getStatic(fr);
			if (va != null)
				return va;
			else 
				// now try the interfaces!
				for (Iterator ii = cl.getInterfaces().iterator(); ii.hasNext(); ) {
					va = ClassList.getClass((String)ii.next()).getStatic(fr);
					if (va != null)
						return va;
				}
			return null;
		} else 
			if (f.isStatic()) 
				return f.getFieldValue().getValue();
			else 
				Error.error("Error: getstatic - field '" + fr.getFieldName() +"' is not static.");
		return null;
	}


	public void print(PrintWriter pw) {
		pw.println(".class: " + getClassName());
		pw.println(".super " + getSuperClass());
		if (getInterfaces().size() > 0) {
			pw.print(".implements ");
			for (Iterator ii = getInterfaces().iterator() ;
					ii.hasNext(); ) 
				pw.print(ii.next() + " ");
			pw.println();
		}

		if (getMethods().size() > 0) {
			String[] a,b;
			Method m;
			for(Enumeration ii=getMethods().elements();ii.hasMoreElements(); ) {
				m = (Method)ii.nextElement();
				a = ScannerUtils.splitMethodSignature(m.toString());
				pw.println(".method " + "   " + a[0]+ " " + a[1]);
				pw.println("\t.limit stack " + m.getStackSize());
				pw.println("\t.limit locals " + m.getVarSize());
				for (int iii=0;iii<m.getCodeSize();iii++) 
					pw.println(iii + "\t" + m.getInstruction(iii));
				pw.println(".end method");
			}
		}

		Field f;
		FieldValue fv;
		for(Enumeration ii=getFields().elements();ii.hasMoreElements(); ) {
			f = (Field)ii.nextElement();
			pw.println(f);
		}

	}

}
