package Instruction;
import EVM.Signature;
import Utilities.ScannerUtils;

/**
 * invokeinterface
 */
public class InterfaceInvocation extends Instruction {
	private String className;
	private String methodName;
	private Signature signature;
	private int paramCount;

	public InterfaceInvocation(String inst, String method, int paramCount) {
		super(inst);
		String[] a, b;

		a = ScannerUtils.splitMethodSignature(method);
		b = ScannerUtils.splitClassField(a[0]);
		this.className = b[0];
		this.methodName = b[1];
		this.signature = new Signature(a[1]);
		this.paramCount = paramCount;
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

	public int getParamCount() {
		return paramCount;
	}

	public String toString() {
		return super.toString() + " " + className + "/" + methodName + signature + " " + paramCount;
	}

}







