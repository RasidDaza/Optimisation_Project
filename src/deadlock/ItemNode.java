package deadlock;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Object representation of an Item within the GUI. An ItemNode can be
 * considered as a substitute for an Item while the user is defining the
 * Deadlock Prevention Problem in the GUI. Extends StackPane
 * Class/Object from JavaFX.
 * 
 * @author Shenal
 *
 */
public class ItemNode extends StackPane {
	
	// Item object of Item type associated with this ItemNode.
	private Item correspondingItem = null;
	
	/**
	 * Constructor for ItemNode.
	 */
	public ItemNode() {
		
	}
	
	/**
	 * Get name of the Item in this itemnode
	 * 
	 * @return name of item node
	 */
	public String getName() {
		return ((Label) this.getChildren().get(1)).getText();
	}
	
	/**
	 * Get association Item object of the Item type.
	 * 
	 * @return item associated with this ItemNode
	 */
	public Item getMatchingItem() {
		return correspondingItem;
	}
	
	/**
	 * Establish association with a Item object of the Item type.
	 * 
	 * @param item associated with this ItemNode
	 */
	public void setMatchingItem(Item item) {
		correspondingItem = item;
	}
}
