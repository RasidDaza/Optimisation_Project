package deadlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource object which is a general term to represent Resources in the
 * Deadlock Solver Program.
 * 
 * @author Shenal
 *
 */
public class Resource {
	
	private String name = null; // Name of this Resource.
	private List<Timeslot> plan = new ArrayList<Timeslot>(); // Plan for this Resource.
	private List<Timeslot> schedule = new ArrayList<Timeslot>(); // Schedule for this Resource.
	private int time = 0; // Amount of time for a new Timeslot to be added to Plan/Schedule.
	private int totalTime = 0; // Total time for this Resource to finish Schedule.
	private int expandedSize = 0; // Account for expanded size of Schedule after adding each new Delay Timeslot.
	
	/**
	 * Constructor for Resource Class.
	 * 
	 * @param name - name of the Resource.
	 */
	public Resource(String name) {
		this.name = name;
	}
	
	/**
	 * Special constructor for Resource Class for reproducing delays in the final solution.
	 * 
	 * @param newName      - name f recreated Resource.
	 * @param newSchedule  - Schedule for recreated Resource.
	 * @param newTotalTime - total time for recreated Resource.
	 */
	public Resource(String newName, List<Timeslot> newSchedule, int newTotalTime) {
		this.name = newName;
		this.schedule = newSchedule;
		this.totalTime = newTotalTime;
	}
	
	/**
	 * Get name of Resource.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get Plan containing all the items which are requesting to use this Resource.
	 * 
	 * @return plan
	 */
	public List<Timeslot> getPlan() {
		return plan;
	}
	
	/**
	 * Get the Schedule containing which items occupied this Resource.
	 * 
	 * @return actualSchedule
	 */
	public List<Timeslot> getSchedule() {
		return schedule;
	}
	
	/**
	 * Get the timespan taken by an Item to use this Resource in a Timeslot.
	 * 
	 * @return time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Set the timespan duration for the current Item occupying this Resource.
	 * 
	 * @param newTime
	 */
	public void setTime(int newTime) {
		time = newTime;
	}
	
	/**
	 * Reset time value in this Resource
	 */
	public void resetTime() {
		time = 0;
	}
	
	/**
	 * Get the total amount of time for the resource to be used by each requesting
	 * item.
	 * 
	 * @return totalTime
	 */
	public int getTotalTime() {
		return totalTime;
	}
	
	/**
	 * Reset total time value in this resource.
	 */
	public void resetTotalTime() {
		totalTime = 0;
	}
	
	/**
	 * Number of timeslots in the schedule.
	 * 
	 * @return size of schedule
	 */
	public int getScheduleSize() {
		return schedule.size();
	}
	
	/**
	 * Add Timeslot to Plan.
	 * 
	 * @param newTimeslot - new timeslot added to resource plan
	 */
	public void addToPlan(Timeslot newTimeslot) {
		plan.add(newTimeslot);
	}
	
	/**
	 * Remove Timeslot from the Resource Plan after creating the Schedule.
	 * 
	 * @param timeslot
	 */
	public void removeFromPlan(Timeslot timeslot) {
		plan.remove(timeslot);
	}
	
	/**
	 * Add Timeslot to Schedule archive.
	 * 
	 * @param newTimeslot
	 */
	public void addToSchedule(Timeslot newTimeslot) {
		schedule.add(newTimeslot);
	}
	
	/**
	 * Remove all timeslots from schedule.
	 */
	public void clearSchedule() {
		schedule.clear();
	}

	/**
	 * Add time from new Timeslot to the amount of time taken for Resource to
	 * complete the Schedule while accounting for delays.
	 * 
	 * @param newTime      - time duration of new timeslot.
	 * @param previousTime - time elapsed for current item in its previous timeslot.
	 */
	public void addToTotalTime(int newTime, int previousTime) {
		// total time = totalTime + the new timeslot time + waiting time for current item to be freed.
		totalTime += newTime + (previousTime - totalTime);
	}
	
	/**
	 * Add time of new Timeslot to total time for Resource.
	 * 
	 * @param newTime - time for new timeslot
	 */
	public void addTime(int newTime) {
		totalTime += newTime;
	}
	
	/**
	 * Get current value of expanded size after one or more delays. have been added
	 * to the schedule
	 * 
	 * @return expandedSize
	 */
	public int getExpandedSize() {
		return expandedSize;
	}
	
	/**
	 * Increment expanded size to account for a new delay added.
	 * to the resource schedule
	 */
	public void expandScheduleSize() {
		expandedSize += 1;
	}
	
	/**
	 * Reset the value of expanded size to recalculate delays.
	 */
	public void resetExpandedSize() {
		expandedSize = 0;
	}	
	
	/**
	 * String representation for this Resource.
	 */
	@Override
	public String toString() {
		return name;
	}

}
