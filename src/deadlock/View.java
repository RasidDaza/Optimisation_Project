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

	private Pane root = null; // root pane for GUI
	private Stage stage = null; // Stage for JavaFX window
	private ItemNode selectedItem = null; // Nominated item for item-resource linking
	private double originalSceneX, originalSceneY; // Original positions of point pressed inside node
	private double originalTranslateX, originalTranslateY; // Original positions of pressed nodes
	private int itemNameNumber = 1; // Unique number the name of each item
	private int resourceNameNumber = 1; // Unique number for the name of each resource

	private List<Item> items = new ArrayList<Item>(); // List of items involved in current problem
	private List<Resource> resources = new ArrayList<Resource>(); // List of resources involved in current problem

	private TextField populationField = null; // Textbox where users specify desired population size
	private TextField itemField = null; // Textbox where users specify desired number of items
	private TextField resourceField = null; // Textbox where users specify desired number of resources

	/**
	 * Start/main method for JavaFX GUI.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			Scene scene = new Scene(root, 1400, 800);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
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
	 * Create new Item node
	 * 
	 * @param root - root pane
	 * @return itemNode
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

		// Place new item node in random location
		Random r = new Random();
		itemNode.setTranslateX(r.nextInt(1200) + 1);
		itemNode.setTranslateY(r.nextInt(600) + 1);

		// Add mouse functionality to item nodes
		itemNode.setOnMousePressed(itemOnMousePressedEventHandler);
		itemNode.setOnMouseDragged(itemOnMouseDraggedEventHandler);

		itemNode.setPickOnBounds(false); // Constrain size of StackPane
		itemNode.getChildren().addAll(circle, label);
		root.getChildren().add(itemNode);
		return itemNode;
	}

	/**
	 * Create new Resource node
	 * 
	 * @param root - root pane
	 * @return resourceNode
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

		// Place new resource node in random location
		Random r = new Random();
		resourceNode.setTranslateX(r.nextInt(1200) + 1);
		resourceNode.setTranslateY(r.nextInt(600) + 1);

		// Add mouse functionality to resource nodes
		resourceNode.setOnMousePressed(resourceOnMousePressedEventHandler);
		resourceNode.setOnMouseDragged(resourceOnMouseDraggedEventHandler);

		resourceNode.setPickOnBounds(false); // Constrain size of StackPane
		resourceNode.getChildren().addAll(rectangle, label);
		root.getChildren().add(resourceNode);
		return resourceNode;
	}

	/**
	 * Create one line between an Item and a Resource
	 * 
	 * @param c1 - Item to be connected
	 * @param r1 - Resource to be connected
	 * @return line
	 */
	private NewLine createConnection(ItemNode c1, ResourceNode r1) {
		NewLine line = new NewLine(c1, r1);

		// Bind one end of line to Item
		line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = c1.getBoundsInParent();
			return b.getMinX() + b.getWidth() / 2;
		}, c1.boundsInParentProperty()));
		line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
			Bounds b = c1.getBoundsInParent();
			return b.getMinY() + b.getHeight() / 2;
		}, c1.boundsInParentProperty()));

		// Bind other end of line to Resource
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
	 * Create toolbar on top left of GUI
	 * 
	 * @param root - root pane
	 */
	private void createToolbar(Pane root) {
		ToolBar toolbar = new ToolBar();

		Button button1 = new Button("Add Item"); // Add new item
		Button button2 = new Button("Add Resource"); // Add new resource
		// Save current mapping of nodes and edges as a new schedule.
		Button button3 = new Button("Save Plan"); // Save current plan in csv file to use later
		Button button4 = new Button("Load Plan"); // Load previously generated plan for all resources
		Button button5 = new Button("Show Plan"); // Show current state of plan for all resources
		Button button6 = new Button("Create Random Problem"); // Run GA with random problem setup
		Button button7 = new Button("Run GA"); // Run GA with problem setup created in GUI
		Button button8 = new Button("Show Timetable"); // Generate timetable chart of final solution
		// Create Population Size label and user input box
		Label populationLabel = new Label("Population Size: ");
		populationLabel.setStyle("-fx-font-size: 16;");
		populationField = new TextField("10");
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
	 * Event when user clicks item node (red circle).
	 */
	EventHandler<MouseEvent> itemOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			ItemNode node = ((ItemNode) (e.getSource())); // current node being moved

			selectedItem = node; // Nominate this item to be linked to a resource

			// Delete Node if right click button is pressed.
			if (e.isSecondaryButtonDown()) {
				List<Node> lines = new ArrayList<Node>(); // List of lines to be deleted
				// Check if any lines are connected to the soon to be deleted item.
				for (Node c : root.getChildren()) {
					if (c.getTypeSelector().equals("NewLine")) {
						NewLine l = (NewLine) c;
						// Check if the end points of the line are within the item.
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
				root.getChildren().remove(e.getSource());

				selectedItem = null; // reset item to resource linking
				itemNameNumber = 1; // Reset item naming
				root.getChildren().remove(e.getSource()); // Delete item
				return;
			}
			originalSceneX = e.getSceneX();
			originalSceneY = e.getSceneY();

			originalTranslateX = node.getTranslateX();
			originalTranslateY = node.getTranslateY();
		}
	};

	/**
	 * Event when user drags item node (red circle).
	 */
	EventHandler<MouseEvent> itemOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			double offsetX = e.getSceneX() - originalSceneX;
			double offsetY = e.getSceneY() - originalSceneY;
			double newTranslateX = originalTranslateX + offsetX;
			double newTranslateY = originalTranslateY + offsetY;

			StackPane node = ((StackPane) (e.getSource())); // current node being moved

			node.setTranslateX(newTranslateX);
			node.setTranslateY(newTranslateY);
			selectedItem = null; // reset item to resource linking
		}
	};

	/**
	 * Event when user clicks on a resource node (yellow rectangle).
	 */
	EventHandler<MouseEvent> resourceOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			ResourceNode node = ((ResourceNode) (e.getSource())); // Current node being moved
			if (selectedItem != null) {
				NewLine line = createConnection(selectedItem, node);
			} else {
				selectedItem = null; // Reset Item-Resource linking
			}

			// Delete Node if right click button is pressed.
			if (e.isSecondaryButtonDown()) {
				List<Node> lines = new ArrayList<Node>(); // List of lines to be deleted
				// Check if any lines are connected to the soon to be deleted resource.
				for (Node c : root.getChildren()) {
					if (c.getTypeSelector().equals("NewLine")) {
						NewLine l = (NewLine) c;
						// Check if the end points of the line are within the resource.
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
	 * Event when user drags a resource node (yellow rectangle).
	 */
	EventHandler<MouseEvent> resourceOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			double offsetX = e.getSceneX() - originalSceneX;
			double offsetY = e.getSceneY() - originalSceneY;
			double newTranslateX = originalTranslateX + offsetX;
			double newTranslateY = originalTranslateY + offsetY;

			StackPane node = ((StackPane) (e.getSource())); // current node being moved

			node.setTranslateX(newTranslateX);
			node.setTranslateY(newTranslateY);

		}
	};

	/**
	 * Event when user clicks on a line (green line).
	 */
	EventHandler<MouseEvent> lineOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			NewLine line = ((NewLine) (e.getSource())); // current line being deleted

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
	 * Event for creating a new item node in the GUI which represents a new item
	 * added to the problem setup.
	 */
	EventHandler<ActionEvent> addItemButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // reset item to resource linking
			createItemNode(root, null);
		}
	};

	/**
	 * Event for creating a new resource node in the GUI which represents a new
	 * resource added to the problem setup.
	 */
	EventHandler<ActionEvent> addResourceButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // reset item to resource linking
			createResourceNode(root, null);
		}
	};

	/**
	 * Event for saving all items, resources and resource plans created in GUI for
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
	 * Event for reloading the items, resources and resource plans from a previous
	 * problem.
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
	 * Event for showing the current schedule plans in text form for the resources
	 * created in the GUI.
	 */
	EventHandler<ActionEvent> showPlanButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			selectedItem = null; // reset item to resource linking
			Pane root2 = new Pane();
			Stage stage2 = new Stage();
			stage2.setTitle("Current Plan");
			TextArea ta = new TextArea(); // Box containing Plan
			ta.setMaxSize(900, 600);
			ta.fontProperty().set(Font.font(30));
			convertNodes(); // Record which nodes are present and their connections
			// Print list of timeslots in each resource plan
			for (Resource r : resources) {
				ta.appendText(r.getName() + ": " + r.getPlan().toString() + "\n");
			}
			root2.getChildren().add(ta);

			stage2.setScene(new Scene(root2, 1100, 600));
			stage2.show();
		}
	};

	/**
	 * Event for Starting and Initialising a Random Setup Problem.
	 */
	EventHandler<ActionEvent> randomProblemEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			int populationSize = Integer.parseInt(populationField.getText()); // Get user specified population size
			int itemSize = Integer.parseInt(itemField.getText()); // Get user specified population size
			int resourceSize = Integer.parseInt(resourceField.getText()); // Get user specified population size
			root.getChildren().clear();
			createToolbar(root);
//			Population p = new Population(10);
			Population p = new Population(populationSize, itemSize, resourceSize);

			items = p.getFinalItems(); // Retrieve modified items after GA to view timetable
			resources = p.getFinalResources(); // Retrieve modified resources after GA to view timetable
		}
	};

	/**
	 * Event for running Genetic Algorithm using the problem setup in GUI.
	 */
	EventHandler<ActionEvent> runAlgorithmButtonEventHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			int populationSize = Integer.parseInt(populationField.getText()); // Get user specified population size
			convertNodes(); // Get all items and resources currently present to feed them into algorithm
//			Population p = new Population(2, items, resources);
			Population p = new Population(populationSize, items, resources);
			items = p.getFinalItems(); // Retrieve modified items after GA to view timetable
			resources = p.getFinalResources(); // Retrieve modified resources after GA to view timetable
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
	 * Get all nodes and lines created and convert them into their equivalent
	 * objects used for the Genetic Algorithm.
	 */
	public void mapProblem() {
		convertNodes();
		try {
			saveNewPlan(resources);
		} catch (IOException e) {
			// Could not save plans
		}
	}

	/**
	 * Save the state of all nodes and line connections on screen into a csv file.
	 * 
	 * @param resourceList - list of resources being saved from the GUI.
	 * @throws IOException
	 */
	public void saveNewPlan(List<Resource> resourceList) throws IOException {
		String FILE_HEADER = "item,time,resource";
		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";

		FileWriter fw = new FileWriter("deadlock.csv"); // CHANGE NAME OF SAVED CSV FILE HERE

		// Write the CSV file header
		fw.append(FILE_HEADER);

		// Add a new line separator after the header
		fw.append(NEW_LINE_SEPARATOR);

		// Append timeslot information to csv file
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
	 * Load new items, resources and connections from archived csv file.
	 * 
	 * @param selectedFile - file containing saved data
	 */
	public void loadNewPlan(File selectedFile) {
		try {
			List<String> newItemList = new ArrayList<String>(); // List for tracking loaded items
			List<String> newResourceList = new ArrayList<String>(); // List for tracking loaded resources
			ItemNode nominatedItemNode = null; // Recreated Items
			ResourceNode nominatedResourceNode = null; // Recreated Resources
			BufferedReader br = new BufferedReader(new FileReader(selectedFile));
			String line = "";
			br.readLine(); // SKIP FIRST LINE IN CSV FILE (META DATA HEADINGS)
			while ((line = br.readLine()) != null) {
				// Use comma as separator
				String[] stuff = line.split(",");

				nominatedItemNode = null;
				nominatedResourceNode = null;

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
						// Check for existing resources
						ResourceNode resourceNode = (ResourceNode) c;
						if (resourceNode.getName().equals(stuff[2])) {
							nominatedResourceNode = resourceNode;
						}
					}
				}
				// If Item Node hasn't already been created, then create it
				if (!(newItemList.contains(stuff[0]))) {
					itemNameNumber = Character.getNumericValue(stuff[0].charAt(1)) + 1; // Change new item names to
																						// start after loaded item names
					nominatedItemNode = createItemNode(root, stuff[0]);
					newItemList.add(nominatedItemNode.getName());
				}
				// If Resource Node hasn't already been created, then create it
				if (!(newResourceList.contains(stuff[2]))) {
					resourceNameNumber = Character.getNumericValue(stuff[0].charAt(1)) + 1; // Change new resource names
																							// to start after loaded
																							// resource names
					nominatedResourceNode = createResourceNode(root, stuff[2]);
					newResourceList.add(nominatedResourceNode.getName());
				}
				// Connect lines between items and resources
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
		int newTime = 10;
		// Make random times for each new timeslot UNCOMMENT LATER
//		Random r = new Random();
//		int newTime = r.nextInt(20) + 1; // Make random time for timeslot.

		// Clear all current items and resources
		items.clear();
		resources.clear();

		for (Node c : root.getChildren()) {
			// Get item and resource nodes
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

		// Get lines between nodes
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
	 * Create the Timetable Chart/Gantt Chart to show the all resource schedules for
	 * the final solution found by the Genetic Algorithm. Creation of Timetable
	 * Chart is derived from the JavaFX StackedBarChart and its properties.
	 * 
	 * @param root - root pane
	 */
	private void createTimetableChart(Pane root) {

		// Create X Axis (time)
		NumberAxis xAxis = new NumberAxis();
		xAxis.tickLabelFontProperty().set(Font.font(26));

		// Create Y Axis (resources)
		CategoryAxis yAxis = new CategoryAxis();
		yAxis.tickLabelFontProperty().set(Font.font(26));

		// Create Stacked Bar Chart
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

		stackedBarChart.categoryGapProperty().set(50.0); // Set gap between each resource schedule (column)

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
					l.setHgap(10); // Set padding gap between each legend item
				}
			}
		}
		stackedBarChart.setPrefSize(1380, 800);
		root.getChildren().add(stackedBarChart);

	}

//	public static void main(String[] args) {
//		launch(args);
//	}

}
