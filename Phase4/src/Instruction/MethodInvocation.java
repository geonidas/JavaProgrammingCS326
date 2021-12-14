package Instruction;
import EVM.Signature;
import Utilities.ScannerUtils;

/**
 * invokenonvirtual, invokespecial, invokestatic, invokevirtual
 */
public class MethodInvocation extends Instruction {
    private String className;
    private String methodName;
    private Signature signature;
    
    public MethodInvocation(String inst, String method) {
	super(inst);
	String[] a,b;
	
	a = ScannerUtils.splitMethodSignature(method);
	b = ScannerUtils.splitClassField(a[0]);
	this.className = b[0];
	this.methodName = b[1];
	this.signature = new Signature(a[1]);
    }   

    public String toString() {
	return super.toString() + " " + className + "/" + methodName + " " + signature;
    }
    
    public String getClassName() {
	return className;
    }

    public String getMethodName() {
	return methodName;
    }

    public Signature getSignature() {
	return signature;
    }
}
