package deadlock;

/**
 * A Timeslot represents a request made by an Item to use a 
 * Resource for a requested amount of time.
 * @author Shenal
 *
 */
public class Timeslot {
	
	private String itemName = null; // Name of the Item in the Timeslot.
	private Item item = null; // Item in the Timeslot.
	private int time = 0; // Specified time in the Timeslot.
	
	/**
	 * Constructor for Timeslot class when creating normal Timeslots.
	 * 
	 * @param itemName - name of Item making request in new Timeslot.
	 * @param item     - Item making request in new Timeslot.
	 * @param time     - amount of time for new Timeslot.
	 */
	public Timeslot(String itemName, Item item, int time) {
		this.itemName = itemName;
		this.item = item;
		this.time = time;
	}
	
	/**
	 * Constructor for Timeslot class specially for creating Delay Timeslots.
	 * 
	 * @param time - amount of time for new Timeslot.
	 */
	public Timeslot(int time) {
		this.itemName = "Delay";
		this.time = time;
	}
	
	/**
	 * Get name of the Item in the Timeslot.
	 * 
	 * @return itemName - name of Item in Timeslot.
	 */
	public String getItemName() {
		return itemName;
	}
	
	/**
	 * Get the Item in the Timeslot.
	 * 
	 * @return item - item in this Timeslot.
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Get the time needed for the Item in the Timeslot to use a Resource.
	 * 
	 * @return time - amount of time for this Timeslot.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * String representation of a Timeslot.
	 */
	@Override
	public String toString() {
		return "(" + itemName + ", " + time + ")";		
	}

}
