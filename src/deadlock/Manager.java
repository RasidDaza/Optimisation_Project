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

	private List<Item> items = new ArrayList<Item>(); // List of Items
	private List<Resource> resources = new ArrayList<Resource>(); // List of Resources
	// List of Resources unique to this solution.
	private List<Resource> personalResources = new ArrayList<Resource>();
	private int result = 0; // Result (fitness) of this solution.

	/**
	 * Constructor for Manager when user runs Random Setup Problem without
	 * automatically generating Items, Resources and Resource Plan.
	 * 
	 * @param itemNumber     - amount of items.
	 * @param resourceNumber - amount of resources.
	 */
	public Manager(int itemNumber, int resourceNumber) {
		int itemCount = itemNumber; // User input dictates number of items.
		int resourceCount = resourceNumber; // User input dictates number of resources.
		initialiseItems(itemCount); // Make new Items
		initialiseResources(resourceCount); // Make new Resources
		initialisePlans(); // Create randomised plans for each Resource
	}

	/**
	 * Constructor for Manager when user uses GUI to setup problem (User Setup
	 * mode).
	 * 
	 * @param items     - list of items.
	 * @param resources - list of resources.
	 */
	public Manager(List<Item> items, List<Resource> resources) {
		this.items = items;
		this.resources = resources;
		clearSchedules(); // Reset Schedule for the Resource.
		createSchedules(); // Formalise ordering of Timeslots for each Resource.
		calculateScheduleTime(); // Calculate times for each Resource to be fully utilised.
		calculateResult(); // Find fitness of this solution.
		copyResources(); // Save Schedule with delays for this individual permanently.
		removeDelays(); // Remove Delay Timeslots from Schedule.
	}

	/**
	 * Get list of Items.
	 * 
	 * @return items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * Get list of Resources.
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
	 * Randomly shuffles Timeslots in plan to create new schedule for each resource.
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
		boolean delayFlag = false; // Indicate whether there is a delay before this timeslot.
		// Non-empty if a previous timeslot is to be overwritten with new timeslot (in
		// previousTimeslots)
		Timeslot deletedTimeslot = null;
		Timeslot newTimeslot = null; // Latest Timeslot to be analysed during Schedule check.
		int previousTime = 0; // Total time of Resource which contains a previous timeslot.

		List<Timeslot> delays = new ArrayList<Timeslot>();
		List<Integer> delayIndexes = new ArrayList<Integer>();
		List<Integer> resourcesIndex = new ArrayList<Integer>();

		for (int i = 0; i < resources.size(); i++) {
			for (Resource r : resources) { // For each resource
				if (r.getSchedule().size() > i) { // Go through each Timeslot in a Resource Schedule

					newTimeslot = r.getSchedule().get(i);
					delayFlag = false;
					deletedTimeslot = null;
					previousTime = 0;

					for (Timeslot oldTimeslot : previousTimeslots) { // Check all old Timeslots with unique Items.
						// If item in new Timeslot is same as item in previous timeslot.
						if (newTimeslot.getItem() == oldTimeslot.getItem()) {
							for (Resource resource : resources) {
								// Check which Resource owns the previous timeslot.
								if (resource.getSchedule().contains(oldTimeslot)
										&& resource.getTotalTime() > r.getTotalTime()) {

									previousTime = resource.getTotalTime(); // Get total time of that resource
									break;
								}
							}
							// Create delays inbetween each Resource Schedule
							// Deal with special cases where previous time is less than current time
							if (previousTime != 0) {
								if (previousTime > r.getTotalTime()) {
									Timeslot t = new Timeslot(previousTime - r.getTotalTime()); // Make Delay Timeslot
									delays.add(t);
									// Show where the new delay timeslot should be placed inside which Resource
									// Schedule.
									delayIndexes.add(r.getSchedule().indexOf(newTimeslot) + r.getExpandedSize());
									resourcesIndex.add(resources.indexOf(r));
									r.expandScheduleSize(); // Account for previously added new Delay Timeslots.
								}

								r.addToTotalTime(newTimeslot.getTime(), previousTime); // Add new time plus time delay.
								delayFlag = true;
								deletedTimeslot = oldTimeslot; // Set the soon to be deleted old Timeslot.
								break;
							}
						}
					}

					// If there is no delay with the new Timeslot.
					if (delayFlag == false) {
						r.addTime(newTimeslot.getTime());

						// Replace the oldTimeslot with same Item as the new Timeslot.
						for (Timeslot oldTimeslot : previousTimeslots) {
							if (oldTimeslot.getItem() == newTimeslot.getItem()) {
								deletedTimeslot = oldTimeslot;
							}
						}
						// If the Item for the new Timeslot has an exisiting Timeslot in previousItems.
						if (deletedTimeslot != null) {
							previousTimeslots.remove(deletedTimeslot);
						}

						previousTimeslots.add(newTimeslot);
					} else { // Delete previous Timeslot if they were marked for deletion
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
	 * Make permanent copy of the Schedule for each Resource in an individual.
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
	 * Empty the list of personalResources (resources with already evaluated
	 * schedule specific to this solution only.
	 */
	public void clearResources() {
		personalResources.clear();
	}

	/**
	 * Retrieve original schedule to show final solution for Genetic Algorithm.
	 */
	public void setResources() {
		resources = personalResources;
	}

}
