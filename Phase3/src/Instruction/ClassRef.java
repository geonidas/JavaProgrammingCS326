package Instruction;

/**
 * anewarray, checkcast, instanceof, new
 */
public class ClassRef extends Instruction {
	private String className;

	public ClassRef(String inst, String className) {
		super(inst);
		this.className = className;
	}

	public String toString() {
		return super.toString() + " " + className;
	} 

	public String getClassName() {
		return className;
	}

}







