package deadlock;

import javafx.scene.shape.Line;

/**
 * Represents a line object in GUI connecting an ItemNode (white and yellow
 * circle) to a ResourceNode (white and yellow rectangle). Extends Line
 * Class/Object from JavaFX.
 * 
 * @author Shenal
 *
 */
public class NewLine extends Line {
	
	private ItemNode bindedItem; // ItemNode on one end of the line.
	private ResourceNode bindedResource; // ResourceNode on the other end of the line.
	
	/**
	 * Constructor for NewLine class.
	 * 
	 * @param item     - Item which is requesting to use the Resource.
	 * @param resource - Resource receiving request from the Item.
	 */
	public NewLine(ItemNode item, ResourceNode resource) {
		bindedItem = item; // Set this Item in the new Item-Resource relationship.
		bindedResource = resource; // Set Resource in new Item-Resource relationship.
	}
	
	/**
	 * Retrieve ItemNode on one end of the line.
	 * 
	 * @return bindedItem - Item at one end of the line.
	 */
	public ItemNode getBindedItem() {
		return bindedItem;
	}
	
	/**
	 * Retrieve ResourceNode on one end of the line.
	 * 
	 * @return bindedResource - Resource at one end of line.
	 */
	public ResourceNode getBindedResource() {
		return bindedResource;
	}
	
	/**
	 * Get the name of the associated ItemNode in the line connection.
	 * 
	 * @return name of the Item in Item-Resource connection
	 */
	public String getBindedItemName() {
		return bindedItem.getName();
	}
	
	/**
	 * Get the name of associated ResourceNode in the line connection.
	 * 
	 * @return name of the Resource in Item-Resource connection
	 */
	public String getBindedResourceName() {
		return bindedResource.getName();
	}
}
