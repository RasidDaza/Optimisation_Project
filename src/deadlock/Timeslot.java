package deadlock;

/**
 * A timeslot represents a request made by an item to use a 
 * resource for a requested amount of time.
 * @author Shenal
 *
 */
public class Timeslot {
	
	private String itemName = null; // Name of the item in the timeslot.
	private Item item = null; // Item in the timeslot.
	private int time = 0; // Specified Time in the timeslot.
	
	/**
	 * Constructor for Timeslot class when creating normal Timeslots.
	 * @param itemName - name of item making request in new timeslot.
	 * @param item - item making request in new timeslot.
	 * @param time - amount of time for new timeslot.
	 */
	public Timeslot(String itemName, Item item, int time) {
		this.itemName = itemName;
		this.item = item;
		this.time = time;
	}
	
	/**
	 * Constructor for Timeslot class when creating Delay Timeslots.
	 * @param time - amount of time for new timeslot.
	 */
	public Timeslot(int time) {
		this.itemName = "Delay";
		this.time = time;
	}
	
	/**
	 * Get name of the Item in the timeslot.
	 * @return itemName - name of item in timeslot.
	 */
	public String getItemName() {
		return itemName;
	}
	
	/**
	 * Get the Item in the timeslot.
	 * @return item - item in this timeslot.
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Get the time needed for the Item in the timeslot to use a resource.
	 * @return time- amount of time for this timeslot.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * String representation of a timeslot
	 */
	@Override
	public String toString() {
		return "(" + itemName + ", " + time + ")";
		
	}

}
