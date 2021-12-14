package Instruction;

import java.util.TreeMap;

public class LookupSwitch extends Instruction {
	private TreeMap tm; 
	public LookupSwitch(String inst, TreeMap tm) {
		super(inst);
		this.tm = tm;
	}
	public TreeMap getValues() {
		return tm;
	}
}











