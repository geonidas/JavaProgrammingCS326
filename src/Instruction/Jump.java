package Instruction;

/** 
 * goto, goto_w, if_acmpeq, if_acmpne, if_icmpeq, if_icmpge, if_icmpgt, if_icmple
 * if_icmplt, if_icmpne, ifeq, ifge, ifgt, ifle, iflt, ifne, ifnonnull, ifnull
 * jsr, jsr_w
 */
public class Jump extends Instruction {
	private String label;

	public Jump(String inst, String label) {
		super(inst);
		this.label = label;
	}

	public String toString() {
		return super.toString() + " " + label;
	}

	public String getLabel() {
		return label;
	}
}
