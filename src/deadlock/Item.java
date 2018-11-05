package deadlock;

/**
 * Item object which is a general term to represent people, processes, etc, 
 * in the Deadlock Solver Program.
 * 
 * @author Shenal
 *
 */
public class Item {
	
	private String name = null; // Name of this item
	
	/**
	 * Constructor for Item class
	 * 
	 * @param name - name given to the Item.
	 */
	public Item(String name) {
		this.name = name;
	}
	
	/**
	 * Get name of Item.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * String representation for this Item
	 */
	@Override
	public String toString() {
		return name;
	}

}
