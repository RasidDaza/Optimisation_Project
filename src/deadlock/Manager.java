package deadlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * A candidate solution/individual for one type/instance of a Deadlock
 * Prevention Problem.
 * 
 * @author Shenal
 *
 */
public class Manager {

	private List<Item> items = new ArrayList<Item>(); // List of items
	private List<Resource> resources = new ArrayList<Resource>(); // List of resources
	// List of resources unique to this candidate solution
	private List<Resource> personalResources = new ArrayList<Resource>();
	private int result = 0; // Result (fitness) of this candidate solution.

	/**
	 * Constructor for Manager when user selects random problem setup button (create
	 * Random Setup and run GA on it).
	 * 
	 * @param itemNumber     - amount of items.
	 * @param resourceNumber - amount of resources.
	 */
	public Manager(int itemNumber, int resourceNumber) {
		int itemCount = itemNumber; // User input dictates number of items
		int resourceCount = resourceNumber; // User input dictates number of resources
		initialiseItems(itemCount); // Make new Items
		initialiseResources(resourceCount); // Make new Resources
		initialisePlans(); // Create randomised plans for each resource
	}

	/**
	 * Constructor for Manager when user uses GUI to setup problem.
	 * 
	 * @param items     - list of items.
	 * @param resources - list of resources.
	 */
	public Manager(List<Item> items, List<Resource> resources) {
		this.items = items;
		this.resources = resources;
		clearSchedules(); // Reset schedule for the resource.
		createSchedules(); // Formalise ordering of timeslots for each resource.
		calculateScheduleTime(); // Calculate times for each resource to be fully utilised.
		calculateResult(); // Find fitness of this solution.
		copyResources(); // Save schedule with delays for this individual permanently.
		removeDelays(); // Remove Delay timeslots from schedule.
	}

	/**
	 * Get list of items.
	 * 
	 * @return items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * Get list of resources.
	 * 
	 * @return resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * Create Items for a Random Setup Problem.
	 * 
	 * @param quantity - amount of items to be created.
	 */
	private void initialiseItems(int quantity) {
		for (int i = 0; i < quantity; i++) {
			String itemName = "I" + Integer.toString(i + 1);
			Item newItem = new Item(itemName);
			items.add(newItem);
		}
	}

	/**
	 * Create Resources for a Random Setup Problem.
	 * 
	 * @param quantity - amount of resources to be created.
	 */
	private void initialiseResources(int quantity) {
		for (int i = 0; i < quantity; i++) {
			String resourceName = "R" + Integer.toString(i + 1);
			Resource newResource = new Resource(resourceName);
			resources.add(newResource);
		}
	}

	/**
	 * Create plan to simulate Deadlock Prevention model/problem when running a
	 * Random Setup Problem.
	 */
	private void initialisePlans() {
		Random r = new Random();
		for (Resource resource : resources) {
			int itemNumber = r.nextInt(items.size()) + 1;
			for (int j = 0; j < itemNumber; j++) {
				int itemIndex = r.nextInt(items.size()) + 1;
				int newTime = r.nextInt(20) + 1; // Make random time for timeslot.
				Timeslot newTimeslot = new Timeslot(items.get(itemIndex - 1).getName(), items.get(itemIndex - 1),
						newTime);
				resource.addToPlan(newTimeslot);
			}
		}
	}

	/**
	 * Randomly shuffles timeslots in plan to create new schedule for each resource.
	 */
	private void createSchedules() {
		for (Resource resource : resources) {
//			resource.getSchedule().clear(); // Reset Schedule for resource
//			resource.resetTotalTime(); // Reset Total Time for resource
			for (Timeslot timeslot : resource.getPlan()) {
				resource.addToSchedule(timeslot);
			}
			Collections.shuffle(resource.getSchedule());
		}
	}

	/**
	 * Calculates total time for each resource to be occupied by each item in order
	 * to obtain the fitness of this solution/individual.
	 */
	public void calculateScheduleTime() {

		List<Timeslot> previousTimeslots = new ArrayList<Timeslot>();
		boolean delayFlag = false; // Indicate whether there is a delay before this timeslot
		Timeslot deletedTimeslot = null; // Non-empty if a previous timeslot is to be overwritten with new timeslot (in
											// previousTimeslots)
		Timeslot newTimeslot = null; // Latest timeslot to be analysed.
		int previousTime = 0; // total time of resource containing a previous timeslot.

		List<Timeslot> delays = new ArrayList<Timeslot>();
		List<Integer> delayIndexes = new ArrayList<Integer>();
		List<Integer> resourcesIndex = new ArrayList<Integer>();

		for (int i = 0; i < resources.size(); i++) { 							
			for (Resource r : resources) { // For each resource
				if (r.getSchedule().size() > i) { // Go through each timeslot in a resource

					newTimeslot = r.getSchedule().get(i);
					delayFlag = false;
					deletedTimeslot = null;
					previousTime = 0;

					for (Timeslot oldTimeslot : previousTimeslots) { // Check all old timeslots with unique items.
						// If item in new timeslot is same as item in previous timeslot.
						if (newTimeslot.getItem() == oldTimeslot.getItem()) { 
																				

							for (Resource resource : resources) { // Check each resource
								// Check which resource owns the previous timeslot.
								if (resource.getSchedule().contains(oldTimeslot)
										&& resource.getTotalTime() > r.getTotalTime()) { 
																							
									previousTime = resource.getTotalTime(); // Get total time of that resource
									break;
								}
							}
							// Create delays inbetween each resource schedule
							// Deal with special cases where previous Time is less than current time
							if (previousTime != 0) { 		
								if (previousTime > r.getTotalTime()) { // Need to add delay in this case
									Timeslot t = new Timeslot(previousTime - r.getTotalTime()); // Make Delay Timeslot
									delays.add(t);
									// Show where the new delay timeslot should be placed inside the resource schedule.
									delayIndexes.add(r.getSchedule().indexOf(newTimeslot) + r.getExpandedSize());
									resourcesIndex.add(resources.indexOf(r));
									r.expandScheduleSize(); // Account for previously added new delay timeslots.
								}

								r.addToTotalTime(newTimeslot.getTime(), previousTime); // Add new time plus time delay.
								delayFlag = true;
								deletedTimeslot = oldTimeslot; // Set the soon to be deleted old timeslot.
								break;
							}
						}
					}

					// If there is no delay with the new timeslot
					if (delayFlag == false) {
						r.addTime(newTimeslot.getTime());

						// Replace oldTimeslot with same item as the new timeslot
						for (Timeslot oldTimeslot : previousTimeslots) {
							if (oldTimeslot.getItem() == newTimeslot.getItem()) {
								deletedTimeslot = oldTimeslot;
							}
						}
						// If the item for the new Timeslot has an exisiting timeslot in previousItems
						if (deletedTimeslot != null) {
							previousTimeslots.remove(deletedTimeslot);
						}

						previousTimeslots.add(newTimeslot);
					} else { // Delete previous timeslot if they were marked for deletion
						previousTimeslots.remove(deletedTimeslot);
						previousTimeslots.add(newTimeslot);
						delayFlag = false;
					}
				}
			}
		}

		// Add delay timeslots to each resource schedule
		for (int j = 0; j < delayIndexes.size(); j++) {
			resources.get(resourcesIndex.get(j)).getSchedule().add(delayIndexes.get(j), delays.get(j));
		}
	}

	/**
	 * Remove delays after calculating fitness for this individual
	 */
	public void removeDelays() {
		for (Resource r : resources) {
			ListIterator<Timeslot> iter = r.getSchedule().listIterator();
			while (iter.hasNext()) {
				if (iter.next().getItemName().equals("Delay")) {
					iter.remove();
				}
			}
		}

	}

	/**
	 * Get objective function/fitness of this solution which is the longest amount
	 * of time for every resource to finish being utilised.
	 * 
	 */
	public void calculateResult() {
		for (Resource r : resources) {
			if (result < r.getTotalTime()) {
				result = r.getTotalTime();
			}
		}
	}

	/**
	 * Get the fitness (quality) of this solution/individual.
	 * 
	 * @return result - fitness of this solution.
	 */
	public int getResult() {
		return result;
	}

	/**
	 * Reset resource specific variables to create the next new individual with its
	 * own schedule.
	 */
	private void clearSchedules() {
		for (Resource r : resources) {
			r.clearSchedule(); // Reset schedule for resource
			r.resetTotalTime(); // Reset Total Time for resource
			r.resetTime(); // Reset time for timeslots added to resource
			r.resetExpandedSize(); // Reset expanded size for resource
		}
	}

	/**
	 * Reset resource variables except the schedule to recalculate fitness.
	 */
	public void resetStuff() {
		for (Resource r : resources) {
			r.resetTotalTime();
			r.resetTime();
			r.resetExpandedSize();
		}
	}

	/**
	 * Make permanent copy of schedule for each resource in this individual.
	 */
	public void copyResources() {
		for (Resource r : resources) {
			List<Timeslot> personalSchedule = new ArrayList<Timeslot>();
			for (Timeslot t : r.getSchedule()) {
				Timeslot timeslot = null;
				if (t.getItemName().equals("Delay")) {
					timeslot = new Timeslot(t.getTime());
				} else {
					timeslot = new Timeslot(t.getItemName(), t.getItem(), t.getTime());
				}
				personalSchedule.add(timeslot);
			}
			Resource newResource = new Resource(r.getName(), personalSchedule, r.getTotalTime());
			personalResources.add(newResource);
		}
	}
	
	/**
	 * Make permanent copy of schedule for each resource in this individual.
	 */
	public void copyParent() {
		for (Resource r : resources) {
			List<Timeslot> personalSchedule = new ArrayList<Timeslot>();
			for (Timeslot t : r.getSchedule()) {
				Timeslot timeslot = null;
				if (t.getItemName().equals("Delay")) {
					timeslot = new Timeslot(t.getTime());
				} else {
					timeslot = new Timeslot(t.getItemName(), t.getItem(), t.getTime());
				}
				personalSchedule.add(timeslot);
			}
			Resource newResource = new Resource(r.getName(), personalSchedule, r.getTotalTime());
			personalResources.add(newResource);
		}
	}

	/**
	 * Empty the list of personalResources (resources with already evaluated
	 * schedule specific to this solution only.
	 */
	public void clearResources() {
		personalResources.clear();
	}

	/**
	 * Retrieve original schedule to show final solution for GA.
	 */
	public void setResources() {
		resources = personalResources;
	}

}
