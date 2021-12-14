package Instruction;

/**
 * aaload, aastore, aconst_null, aload_0, aload_1, aload_2, aload_3,
 * areturn, arraylength, astore_0, astore_1, astore_2, astore_3,
 * athrow, baload, bastore, breakpoint,caload, castore, d2f, d2i, d2l,
 * dadd, daload, dastore, dcmpg, dcmpl, dconst_0, dconst_1, ddiv,
 * dload_0, dload_1, dload_2, dload_3, dmul, dneg, drem, dreturn,
 * dstore_0, dstore_1, dstore_2, dstore_3, dsub, dup, dup2, dup2_x1,
 * dup2_x2, dup_x1, dup_x2, f2d, f2i, f2l, fadd, faload, fastore,
 * fcmpg, fcmpl, fconst_0, fconst_1, fconst_2, fdiv, fload_0, fload_1,
 * fload_2, fload_3, fmul, fneg, frem, freturn, fstore_0, fstore_1,
 * fstore_2, fstore_3, fsub, i2d, i2f, i2l, iadd, iaload, iand,
 * iastore, iconst_0, iconst_1, iconst_2, iconst_3, iconst_4,
 * iconst_5, iconst_m1, idiv, iload_0, iload_1, iload_2, iload_3,
 * imul, ineg, int2byte, int2char, int2short, i2b, i2c, i2s, ior,
 * irem, ireturn, ishl, ishr, istore_0, istore_1, istore_2, istore_3,
 * isub, iushr, ixor, l2d, l2f, l2i, ladd, laload, land, lastore,
 * lcmp, lconst_0, lconst_1, ldiv, lload_0, lload_1, lload_2, lload_3,
 * lmul, lneg, lor, lrem, lreturn, lshl, lshr, lstore_0, lstore_1,
 * lstore_2, lstore_3, lsub, lushr, lxor, monitorenter,
 * monitorexit,nop, pop, pop2, ret, return, saload, sastore, swap  
 * bipush, sipush
 */
public class SimpleInstruction extends Instruction {
	private int operand;

	public SimpleInstruction(String inst, int operand) {
		super(inst);
		this.operand = operand;
	}

	public String toString() {
		return super.toString() + " " + operand;
	}

	public int getOperand() {
		return operand;
	}

}
