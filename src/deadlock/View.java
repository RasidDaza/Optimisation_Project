package deadlock;

import java.util.Random;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * GUI Class for Deadlock Prevention Problem Program
 * 
 * @author Shenal
 *
 */
public class View extends Application {

	private Pane root = null; // Root pane for GUI
	private Stage stage = null; // Stage for JavaFX window
	private ItemNode selectedItem = null; // Nominated Item for Item-Resource linking.
	private double originalSceneX, originalSceneY; // Original positions of point pressed inside a node.
	private double originalTranslateX, originalTranslateY; // Original spatial position of pressed nodes.
	private int itemNameNumber = 1; // Unique number for name of each Item.
	private int resourceNameNumber = 1; // Unique number for the name of each Resource.

	private List<Item> items = new ArrayList<Item>(); // List containing all Items.
	private List<Resource> resources = new ArrayList<Resource>(); // List containing all Resources.

	private TextField populationField = null; // Textfield where users specify desired Population Size.
	private TextField itemField = null; // Textfield where users specify desired Number of Items.
	private TextField resourceField = null; // Textfield where users specify desired Number of Resources.

	/**
	 * Start/main method for JavaFX GUI.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			Scene scene = new Scene(root, 1400, 800);
			scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
			primaryStage.setTitle("Deadlock Solver");
			createToolbar(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			this.stage = primaryStage;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create new ItemNode in the GUI.
	 * 
	 * @param root - Root pane
	 * @return itemNode - newly created ItemNode
	 */
	private ItemNode createItemNode(Pane root, String name) {
		ItemNode itemNode = new ItemNode();
		Label label = null;

		// Drawing a Circle
		Circle circle = new Circle(300.0, 135.0, 60.0);
		if (name == null) {
			label = new Label("I" + itemNameNumber);
			itemNameNumber++; // Setup the name of next item
		} else {
			label = new Label(name);
		}

		circle.setFill(Color.WHITE);
		circle.setStroke(Color.CRIMSON);
		circle.setStrokeWidth(20);

		// Place new ItemNode in random location in GUI.
		Random r = new Random();
		itemNode.setTranslateX(r.nextInt(1200) + 1);
		itemNode.setTranslateY(r.nextInt(600) + 1);

		// Add mouse functionality to ItemNodes
		itemNode.setOnMousePressed(itemOnMousePressedEventHandler);
		itemNode.setOnMouseDragged(itemOnMouseDraggedEventHandler);

		itemNode.setPickOnBounds(false); // Constrain size of StackPane
		itemNode.getChildren().addAll(circle, label);
		root.getChildren().add(itemNode);
		return itemNode;
	}

	/**
	 * Create new ResourceNode in the GUI.
	 * 
	 * @param root - Root pane
	 * @return resourceNode - newly creayed ResourceNode
	 */
	public ResourceNode createResourceNode(Pane root, String name) {
		ResourceNode resourceNode = new ResourceNode();
		Label label = null;
		Rectangle rectangle = new Rectangle(200, 120);
		if (name == null) {
			label = new Label("R" + resourceNameNumber);
			resourceNameNumber++; // Setup the name of next resource
		} else {
			label = new Label(name);
		}

		rectangle.setFill(Color.WHITE);
		rectangle.setStroke(Color.GOLD);
		rectangle.setStrokeWidth(10);

		// Place new ResourceNode in random location in GUI.
		Random r = new Random();
		resourceNode.setTranslateX(r.nextInt(1200) + 1);
		resourceNode.setTranslateY(r.nextInt(600) + 1);

		// Add mouse functionality to ResourceNodes
		resourceNode.setOnMousePressed(resourceOnMousePressedEventHandler);
		resourceNode.setOnMouseDragged(resourceOnMouseDraggedEventHandler);

		resourceNode.setPickOnBounds(false); // Constrain size of StackPane
		resourceNode.getChildren().addAll(rectangle, label);
		root.getChildren().add(resourceNode);
		return resourceNode;
	}

	/**
	 * Create one line connection between an Item and a Resource.
	 * 
	 * @param c1 - Item to be connected
	 * @param r1 - Resource to be connected
	 * @return line - new connection between the Item and Resource.
	 */
	private NewLine createConnection(ItemNode c1, ResourceNode r1) {
		NewLine line = new NewLine(c1, r1);

		// Bind one end of the line to the ItemNode.
		line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = c1.getBoundsInParent();
			return b.getMinX() + b.getWidth() / 2;
		}, c1.boundsInParentProperty()));
		line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = c1.getBoundsInParent();
			return b.getMinY() + b.getHeight() / 2;
		}, c1.boundsInParentProperty()));

		// Bind other end of the line to ResourceNode.
		line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = r1.getBoundsInParent();
			return b.getMinX() + b.getWidth() / 2;
		}, r1.boundsInParentProperty()));
		line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = r1.getBoundsInParent();
			return b.getMinY() + b.getHeight() / 2;
		}, r1.boundsInParentProperty()));

		line.setStroke(Color.GREEN);
		line.setStrokeWidth(6);
		line.setOnMousePressed(lineOnMousePressedEventHandler);

		root.getChildren().add(line);
		line.toBack(); // Send line behind other nodes
		return line;
	}

	/**
	 * Create Toolbar situated on top left of the GUI window.
	 * 
	 * @param root - Root pane
	 */
	private void createToolbar(Pane root) {
		ToolBar toolbar = new ToolBar();
		// Create Buttons in Toolbar
		Button button1 = new Button("Add Item"); // Add new ItemNode
		Button button2 = new Button("Add Resource"); // Add new ResourceNode
		// Save current mapping of nodes and line connections to CSV file.
		Button button3 = new Button("Save Plan");
		Button button4 = new Button("Load Plan"); // Load previously generated Plan for all Resources.
		Button button5 = new Button("Show Plan"); // Show current state of Plan for all Resources.
		Button button6 = new Button("Create Random Problem"); // Run GA with Random Problem Setup.
		Button button7 = new Button("Run GA"); // Run GA with User Setup Problem (GUI).
		Button button8 = new Button("Show Timetable"); // Generate timetable chart of final solution.
		// Create Population Size label and user input box
		Label populationLabel = new Label("Population Size: ");
		populationLabel.setStyle("-fx-font-size: 16;");
		populationField = new TextField("100");
		populationField.setPrefWidth(50);
		// Create Item Number label and user input box
		Label itemLabel = new Label("Number of Items: ");
		itemLabel.setStyle("-fx-font-size: 16;");
		itemField = new TextField("5");
		itemField.setPrefWidth(50);
		// Create Resource Number label and user input box
		Label resourceLabel = new Label("Number of Resources: ");
		resourceLabel.setStyle("-fx-font-size: 16;");
		resourceField = new TextField("5");
		resourceField.setPrefWidth(50);

		// Map buttons with their respective actions
		button1.setOnAction(addItemButtonEventHandler);
		button2.setOnAction(addResourceButtonEventHandler);
		button3.setOnAction(savePlanButtonEventHandler);
		button4.setOnAction(loadPlanButtonEventHandler);
		button5.setOnAction(showPlanButtonEventHandler);
		button6.setOnAction(randomProblemEventHandler);
		button7.setOnAction(runAlgorithmButtonEventHandler);
		button8.setOnAction(showTimetableButtonEventHandler);

		toolbar.getItems().addAll(button1, button2, button3, button4, button5, button6, button7, button8,
				populationLabel, populationField, itemLabel, itemField, resourceLabel, resourceField);
		root.getChildren().add(toolbar);
	}

	/**
	 * Event when user clicks ItemNode (white and red circle).
	 */
	EventHandler<MouseEvent> itemOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			ItemNode node = ((ItemNode) (e.getSource())); // Get pressed Item

			selectedItem = node; // Nominate prssed Item to be linked to a Resource

			// Delete Item if right click button is pressed.
			if (e.isSecondaryButtonDown()) {
				List<Node> lines = new ArrayList<Node>(); // List of lines to be deleted.
				// Check if any lines are connected to the soon to be deleted Item.
				for (Node c : root.getChildren()) {
					if (c.getTypeSelector().equals("NewLine")) {
						NewLine l = (NewLine) c;
						// Check if the end points of the line are within the Item.
						if (node.getBoundsInParent().contains(l.getStartX(), l.getStartY())
								|| node.getBoundsInParent().contains(l.getEndX(), l.getEndY())) {
							lines.add(c);
						}
					}
				}
				// Delete all lines that are connected to the soon to be deleted Item.
				for (Node line : lines) {
					root.getChildren().remove(line);
				}
				root.getChildren().remove(e.getSource());

				selectedItem = null; // Reset Item to Resource linking
				itemNameNumber = 1; // Reset Item naming structure
				root.getChildren().remove(e.getSource()); // Delete Item
				return;
			}
			originalSceneX = e.getSceneX();
			originalSceneY = e.getSceneY();

			originalTranslateX = node.getTranslateX();
			originalTranslateY = node.getTranslateY();
		}
	};

	/**
	 * Event when user drags ItemNode (white and red circle).
	 */
	EventHandler<MouseEvent> itemOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			double offsetX = e.getSceneX() - originalSceneX;
			double offsetY = e.getSceneY() - originalSceneY;
			double newTranslateX = originalTranslateX + offsetX;
			double newTranslateY = originalTranslateY + offsetY;

			StackPane node = ((StackPane) (e.getSource())); // Current ItemNode being moved
			node.setTranslateX(newTranslateX);
			node.setTranslateY(newTranslateY);
			selectedItem = null; // Reset Item to Resource linking
		}
	};

	/**
	 * Event when user clicks on a ResourceNode (white and yellow rectangle).
	 */
	EventHandler<MouseEvent> resourceOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			ResourceNode node = ((ResourceNode) (e.getSource())); // Get pressed Resource
			if (selectedItem != null) {
				NewLine line = createConnection(selectedItem, node);
			} else {
				selectedItem = null; // Reset Item-Resource linking
			}

			// Delete Resource if right click button is pressed.
			if (e.isSecondaryButtonDown()) {
				List<Node> lines = new ArrayList<Node>(); // List of lines to be deleted.
				// Check if any lines are connected to the soon to be deleted Resource.
				for (Node c : root.getChildren()) {
					if (c.getTypeSelector().equals("NewLine")) {
						NewLine l = (NewLine) c;
						// Check if the end points of the line are within the Resource.
						if (node.getBoundsInParent().contains(l.getStartX(), l.getStartY())
								|| node.getBoundsInParent().contains(l.getEndX(), l.getEndY())) {
							lines.add(c);
						}
					}
				}
				// Delete all lines that are connected to the soon to be deleted resource.
				for (Node line : lines) {
					root.getChildren().remove(line);
				}
				root.getChildren().remove(e.getSource()); // Delete resource
				resourceNameNumber = 1; // Reset resource naming
				return;
			}
			originalSceneX = e.getSceneX();
			originalSceneY = e.getSceneY();

			originalTranslateX = node.getTranslateX();
			originalTranslateY = node.getTranslateY();
		}
	};

	/**
	 * Event when user drags a ResourceNode (white and yellow rectangle).
	 */
	EventHandler<MouseEvent> resourceOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			double offsetX = e.getSceneX() - originalSceneX;
			double offsetY = e.getSceneY() - originalSceneY;
			double newTranslateX = originalTranslateX + offsetX;
			double newTranslateY = originalTranslateY + offsetY;

			StackPane node = ((StackPane) (e.getSource())); // Current ResourceNode being moved
			node.setTranslateX(newTranslateX);
			node.setTranslateY(newTranslateY);
		}
	};

	/**
	 * Event when user clicks on a line connecting an Item to a Resource (green
	 * line).
	 */
	EventHandler<MouseEvent> lineOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			NewLine line = ((NewLine) (e.getSource())); // Current line being deleted

			// Delete Node if right click button is pressed.
			if (e.isSecondaryButtonDown()) {
				line.startXProperty().unbind();
				line.startYProperty().unbind();
				line.endXProperty().unbind();
				line.endYProperty().unbind();
				root.getChildren().remove(e.getSource());
				return;
			}
		}
	};

	/**
	 * Event for creating a new ItemNode in the GUI which represents a new Item.
	 */
	EventHandler<ActionEvent> addItemButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // reset item to resource linking
			createItemNode(root, null);
		}
	};

	/**
	 * Event for creating a new Resource node in the GUI which represents a new
	 * resource added to the problem setup.
	 */
	EventHandler<ActionEvent> addResourceButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // Reset Item to Resource linking
			createResourceNode(root, null);
		}
	};

	/**
	 * Event for saving all Items, Resources and Resource Plans created in GUI for
	 * running the algorithm later.
	 */
	EventHandler<ActionEvent> savePlanButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null;
			mapProblem();
		}
	};

	/**
	 * Event for reloading the Items, Resources and Resource Plans from a previously
	 * setup problem.
	 */
	EventHandler<ActionEvent> loadPlanButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			root.getChildren().clear();
			createToolbar(root);
			// Open file
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Previous Deadlock");
			fileChooser.setInitialDirectory(new File("."));
			fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
			File selectedFile = fileChooser.showOpenDialog(stage); // Get file
			loadNewPlan(selectedFile);

		}
	};

	/**
	 * Event for showing the current Plans in text form for belonging to each
	 * Resource created in the GUI.
	 */
	EventHandler<ActionEvent> showPlanButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // reset Item to Resource linking
			Pane root2 = new Pane();
			Stage stage2 = new Stage();
			stage2.setTitle("Current Plan");
			TextArea ta = new TextArea(); // Box containing Plan in text form
			ta.setMaxSize(1100, 600);
			ta.fontProperty().set(Font.font(30));
			convertNodes(); // Record which nodes are present and their connections.
			// Print list of Timeslots in each resource plan
			for (Resource r : resources) {
				ta.appendText(r.getName() + ": " + r.getPlan().toString() + "\n");
			}
			root2.getChildren().add(ta);
			stage2.setScene(new Scene(root2, 1100, 600));
			stage2.show();
		}
	};

	/**
	 * Event for initialising a Random Setup Problem (user lets program make
	 * randomised problem) and running the Genetic Algorithm on that problem.
	 */
	EventHandler<ActionEvent> randomProblemEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			int populationSize = Integer.parseInt(populationField.getText());
			int itemSize = Integer.parseInt(itemField.getText());
			int resourceSize = Integer.parseInt(resourceField.getText());
			root.getChildren().clear();
			createToolbar(root);
			Population p = new Population(populationSize, itemSize, resourceSize);

			items = p.getFinalItems(); // Retrieve modified Items after GA to view timetable.
			resources = p.getFinalResources(); // Retrieve modified Resources after GA to view timetable.
		}
	};

	/**
	 * Event for running Genetic Algorithm for User Setup Problems (user used GUI to
	 * create model of their problem).
	 */
	EventHandler<ActionEvent> runAlgorithmButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			int populationSize = Integer.parseInt(populationField.getText());
			convertNodes(); // Get all Items and Resources in GUI to feed them into algorithm.
			Population p = new Population(populationSize, items, resources);
			items = p.getFinalItems(); // Retrieve modified Items after GA to view timetable
			resources = p.getFinalResources(); // Retrieve modified Resources after GA to view timetable
		}
	};

	/**
	 * Event for showing the Timetable Chart of the final solution found by Genetic
	 * Algorithm.
	 */
	EventHandler<ActionEvent> showTimetableButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			root.getChildren().clear();
			createTimetableChart(root);
		}
	};

	/**
	 * Get all nodes and lines (connections between nodes) in the GUI and convert
	 * them into equivalent Item or Resource objects used for the Genetic Algorithm.
	 */
	public void mapProblem() {
		convertNodes(); // Record which nodes are present and their connections.
		try {
			saveNewPlan(resources);
		} catch (IOException e) {
			// Could not save Plans
		}
	}

	/**
	 * Save the state of all nodes and line connections currently in the GUI Window
	 * into a CSV file.
	 * 
	 * @param resourceList - list of Resources from the GUI about to be saved.
	 * @throws IOException
	 */
	public void saveNewPlan(List<Resource> resourceList) throws IOException {
		String FILE_HEADER = "item,time,resource";
		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";

		FileWriter fw = new FileWriter("deadlock2.csv"); // Name of CSV File to save data.
		// Write the CSV file header
		fw.append(FILE_HEADER);
		// Add a new line separator after the header
		fw.append(NEW_LINE_SEPARATOR);

		// Append timeslot information to CSV file.
		for (Resource r : resources) {
			for (Timeslot t : r.getPlan()) {
				fw.append(String.valueOf(t.getItemName()));
				fw.append(COMMA_DELIMITER);
				fw.append(String.valueOf(t.getTime()));
				fw.append(COMMA_DELIMITER);
				fw.append(String.valueOf(r.getName()));
				fw.append(NEW_LINE_SEPARATOR);
			}
		}
		fw.flush();
		fw.close();
	}

	/**
	 * Load Items, Resources and Item-Resource connections from archived CSV file in
	 * the same state when the CSV File was created/saved.
	 * 
	 * @param selectedFile - CSV file containing saved data
	 */
	public void loadNewPlan(File selectedFile) {
		try {
			List<String> newItemList = new ArrayList<String>(); // List for tracking all loaded Items.
			List<String> newResourceList = new ArrayList<String>(); // List for tracking all loaded Resources.
			ItemNode nominatedItemNode = null;
			ResourceNode nominatedResourceNode = null;
			BufferedReader br = new BufferedReader(new FileReader(selectedFile));
			String line = "";
			br.readLine(); // Skip First Line in CSV File (Meta-data Headings).
			while ((line = br.readLine()) != null) {
				// Use comma as separator
				String[] stuff = line.split(",");

				nominatedItemNode = null; // Item about to be recreated in GUI.
				nominatedResourceNode = null; // Resource about to be recreated in GUI.

				if (newItemList.contains(stuff[0]) && newResourceList.contains(stuff[2]))
					;
				for (Node c : root.getChildren()) {
					// Check for existing Items
					if (c.getTypeSelector().equals("ItemNode")) {
						ItemNode itemNode = (ItemNode) c;
						if (itemNode.getName().equals(stuff[0])) {
							nominatedItemNode = itemNode;
						}
					} else if (c.getTypeSelector().equals("ResourceNode")) {
						// Check for existing Resources
						ResourceNode resourceNode = (ResourceNode) c;
						if (resourceNode.getName().equals(stuff[2])) {
							nominatedResourceNode = resourceNode;
						}
					}
				}
				// If ItemNode hasn't already been created, then create it.
				if (!(newItemList.contains(stuff[0]))) {
					// Change new Item names to start after loaded Item names.
					itemNameNumber = Character.getNumericValue(stuff[0].charAt(1)) + 1;
					nominatedItemNode = createItemNode(root, stuff[0]);
					newItemList.add(nominatedItemNode.getName());
				}
				// If ResourceNode hasn't already been created, then create it.
				if (!(newResourceList.contains(stuff[2]))) {
					// Change new Resource names to start after loaded Resource names.
					resourceNameNumber = Character.getNumericValue(stuff[0].charAt(1)) + 1;
					nominatedResourceNode = createResourceNode(root, stuff[2]);
					newResourceList.add(nominatedResourceNode.getName());
				}
				// Connect lines between Items and Resources.
				createConnection(nominatedItemNode, nominatedResourceNode);

			}
			br.close();

		} catch (IOException e) {
			System.out.println("ERROR: File can not be found");
		}
	}

	/**
	 * Create items, resources and connections based on data from loaded csv file.
	 */
	public void convertNodes() {
		int newTime = 10; // Time for every loaded Timeslot
		// Clear all current Items and Resources
		items.clear();
		resources.clear();

		for (Node c : root.getChildren()) {
			// Get Item and Resource nodes
			if (c.getTypeSelector().equals("ItemNode")) {
				Item newItem = new Item(((ItemNode) c).getName());
				((ItemNode) c).setMatchingItem(newItem);
				items.add(newItem);
			} else if (c.getTypeSelector().equals("ResourceNode")) {
				Resource newResource = new Resource(((ResourceNode) c).getName());
				((ResourceNode) c).setMatchingItem(newResource);
				resources.add(newResource);
			}
		}

		// Get lines (Item-Resource connections) between nodes.
		for (Node c : root.getChildren()) {
			if (c.getTypeSelector().equals("NewLine")) {
				NewLine l = (NewLine) c;
				Timeslot newTimeslot = new Timeslot(l.getBindedItem().getName(), l.getBindedItem().getMatchingItem(),
						newTime);
				l.getBindedResource().getMatchingResource().addToPlan(newTimeslot);
			}
		}
	}

	/**
	 * Create the Timetable Chart to show the all Resource Schedules for the final
	 * solution found by the Genetic Algorithm. Timetable Chart is derived from the
	 * JavaFX StackedBarChart and its properties.
	 * 
	 * @param root - Root pane
	 */
	private void createTimetableChart(Pane root) {

		// Create X Axis (time)
		NumberAxis xAxis = new NumberAxis();
		xAxis.tickLabelFontProperty().set(Font.font(26));

		// Create Y Axis (resources)
		CategoryAxis yAxis = new CategoryAxis();
		yAxis.tickLabelFontProperty().set(Font.font(26));

		// Create regular Stacked Bar Chart
		StackedBarChart<Number, String> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
		stackedBarChart.setTitle("Optimised Deadlock Schedule");

		// Set colour properties for each item
		for (Resource r : resources) {
			for (Timeslot t : r.getSchedule()) {
				XYChart.Series<Number, String> series = new XYChart.Series<>();
				series.getData().add(new XYChart.Data<>(t.getTime(), r.getName()));
				stackedBarChart.getData().add(series);
				for (Item i : items) {
					// Assign unique colors to at most 10 items
					if (t.getItemName().equals("I1")) { // Item 1
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: red;");
						break;
					} else if (t.getItemName().equals("I2")) { // Item 2
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: blue;");
						break;
					} else if (t.getItemName().equals("I3")) { // Item 3
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: green;");
						break;
					} else if (t.getItemName().equals("I4")) { // Item 4
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: orange;");
						break;
					} else if (t.getItemName().equals("I5")) { // Item 5
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: purple;");
						break;
					} else if (t.getItemName().equals("I6")) { // Item 6
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: pink;");
						break;
					} else if (t.getItemName().equals("I7")) { // Item 7
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: brown;");
						break;
					} else if (t.getItemName().equals("I8")) { // Item 8
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: aqua;");
						break;
					} else if (t.getItemName().equals("I9")) { // Item 9
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: black;");
						break;
					} else if (t.getItemName().equals("I10")) { // Item 10
						series.getData().get(0).getNode().setStyle("-fx-bar-fill: yellow;");
						break;
					} else if (t.getItemName().equals("Delay")) { // Delay
						series.getData().get(0).getNode().setStyle("visibility: hidden;");
						break;
					}
				}
			}
		}
		stackedBarChart.categoryGapProperty().set(50.0); // Set gap between each Resource Schedule.

		// Modify Chart Legend
		for (Node n : stackedBarChart.getChildrenUnmodifiable()) {
			if (n instanceof Legend) {
				Legend l = (Legend) n;
				// Remove legend items to match number of Items
				while (l.getItems().size() > items.size()) { // set 3 to number of timeslots
					l.getItems().remove(l.getItems().size() - 1);
				}

				// Alter legend items to accommodate 10 different items.
				for (Legend.LegendItem li : l.getItems()) {
					Region re = (Region) li.getSymbol();
					// Check which item color each respective legend item should be
					int num = l.getItems().indexOf(li);
					switch (num) {
					case 0: // Item 1 = Red Colour
						re.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
						li.setText("I1");
						break;
					case 1: // Item 2 = Blue Colour
						re.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
						li.setText("I2");
						break;
					case 2: // Item 3 = Green Colour
						re.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
						li.setText("I3");
						break;
					case 3: // Item 4 = Orange Colour
						re.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
						li.setText("I4");
						break;
					case 4: // Item 5 = Purple Colour
						re.setBackground(new Background(new BackgroundFill(Color.PURPLE, null, null)));
						li.setText("I5");
						break;
					case 5: // Item 6 = Pink Colour
						re.setBackground(new Background(new BackgroundFill(Color.PINK, null, null)));
						li.setText("I6");
						break;
					case 6: // Item 7 = Brown Colour
						re.setBackground(new Background(new BackgroundFill(Color.BROWN, null, null)));
						li.setText("I7");
						break;
					case 7: // Item 8 = Aqua/Light Blue Colour
						re.setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
						li.setText("I8");
						break;
					case 8: // Item 9 = Black Colour
						re.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
						li.setText("I9");
						break;
					case 9: // Item 10 = Yellow Colour
						re.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
						li.setText("I10");
						break;
					}
					l.setHgap(10); // Set padding gap between each legend item.
				}
			}
		}
		stackedBarChart.setPrefSize(1380, 800);
		root.getChildren().add(stackedBarChart);
	}
}
