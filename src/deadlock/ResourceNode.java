package deadlock;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Object representation of Resource within the GUI. An ResourceNode can be
 * considered as a substitute for a Resource while the user is defining the
 * Deadlock Prevention Problem in the GUI. Extends StackPane
 * Class/Object from JavaFX.
 * 
 * @author Shenal
 *
 */
public class ResourceNode extends StackPane {
	
	// Resource object of Resource type associated with this ResourceNode.
	private Resource correspondingResource = null; 
	
	/**
	 * Constructor for ResourceNode class
	 */
	public ResourceNode() {
		
	}

	/**
	 * Get name of Resource.
	 * 
	 * @return name of resource node.
	 */
	public String getName() {
		return ((Label) this.getChildren().get(1)).getText();
	}
	
	/**
	 * Get association Resource object of the Resource type.
	 * 
	 * @return correspondingResource - Resource which the ResourceNode is
	 *         representing.
	 */
	public Resource getMatchingResource() {
		return correspondingResource;
	}
	
	/**
	 * Establish association with a Resource object of the Resource type.
	 * 
	 * @param resource - resource to be basis for this ResourceNode.
	 */
	public void setMatchingItem(Resource resource) {
		correspondingResource = resource;
	}
	
	

}
