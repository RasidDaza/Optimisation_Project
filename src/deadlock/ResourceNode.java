package deadlock;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Representation of Resource within the GUI. An ResourceNode can be considered
 * as a substitute for an Resource while the user is defining the Deadlock
 * Prevention related problem in the GUI. Extends StackPane Class/Object from
 * JavaFX.
 * 
 * @author Shenal
 *
 */
public class ResourceNode extends StackPane {
	
	// Resource object of Resource type associated with this resource node.
	private Resource correspondingResource = null; 
	
	/**
	 * Constructor for ResourceNode class
	 */
	public ResourceNode() {
		
	}
	
	/**
	 * Get name of resource.
	 * @return name of resource node.
	 */
	public String getName() {
		return ((Label) this.getChildren().get(1)).getText();
	}
	
	/**
	 * Get association resource object of the Resource type.
	 * @return correspondingResource - resource which the resource node is representing.
	 */
	public Resource getMatchingResource() {
		return correspondingResource;
	}
	
	/**
	 * Establish association with a resource object of the Resource type.
	 * @param resource - resource to be basis for this resource node.
	 */
	public void setMatchingItem(Resource resource) {
		correspondingResource = resource;
	}
	
	

}
