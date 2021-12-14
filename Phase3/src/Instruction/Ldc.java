package Instruction;
import Value.*;

/**
 * ldc, ldc_w, ldc2_w
 */

public abstract class Ldc extends Instruction {
    Ldc(String inst) {
	super(inst);
    }
    
    public abstract Value getValue() ;

}








