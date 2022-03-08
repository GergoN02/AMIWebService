package uk.ac.mmu.advprog.hackathon;

public class Signal {

	public String ID;

	public String DateSet;

	public String Value;

	/**
	 * Skeleton class for instantiating Signal objects
	 * 
	 * @param ID      Signal ID as String
	 * @param DateSet Date value as String
	 * @param Value   Displayed Value as String
	 */
	public Signal(String ID, String DateSet, String Value) {

		this.ID = ID;
		this.DateSet = DateSet;
		this.Value = Value;
	}

}
