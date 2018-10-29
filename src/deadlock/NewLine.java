package deadlock;

import javafx.scene.shape.Line;

/**
 * Represent line object in GUI connecting one itemnode to one resource node.
 * Extends Line Class/Object from JavaFX.
 * 
 * @author Shenal
 *
 */
public class NewLine extends Line {
	
	private ItemNode bindedItem; // Item node on one end of the line
	private ResourceNode bindedResource; // Resource node on the other end of the line
	
	/**
	 * Constructor for NewLine class.
	 * @param item - item which is requesting to use the resource
	 * @param resource - resource receiving request
	 */
	public NewLine(ItemNode item, ResourceNode resource) {
		bindedItem = item; // Set this item in the new item-resource relationship
		bindedResource = resource; // Set resource in new item-resource relationship
	}
	
	/**
	 * Retrieve Item node on one end of the line.
	 * @return bindedItem - item at one end of line.
	 */
	public ItemNode getBindedItem() {
		return bindedItem;
	}
	
	/**
	 * Retrieve Resource node on one end of the line.
	 * @return bindedResource - resource at one end of line.
	 */
	public ResourceNode getBindedResource() {
		return bindedResource;
	}
	
	/**
	 * Get name of associated Item node.
	 * @return name of the item in Item-Resource connection
	 */
	public String getBindedItemName() {
		return bindedItem.getName();
	}
	
	/**
	 * Get name of associated Resource node.
	 * @return name of the resource in Item-Resource connection
	 */
	public String getBindedResourceName() {
		return bindedResource.getName();
	}
}
