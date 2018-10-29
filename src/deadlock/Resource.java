package deadlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource object which is a general term to represent resources in Deadlock
 * Prevention Problems.
 * 
 * @author Shenal
 *
 */
public class Resource {
	
	private String name = null; // Name of this resource.
	private List<Timeslot> plan = new ArrayList<Timeslot>(); // Plan for this resource.
	private List<Timeslot> schedule = new ArrayList<Timeslot>(); // Schedule for this resource.
	private int time = 0; // Amount of time for a new timeslot to be added to plan/schedule.
	private int totalTime = 0;	// Total Time for this resource to finish schedule.
	private int expandedSize = 0; // Account for expanded size of schedule after adding each new timeslot.
	
	/**
	 * Constructor for Resource Class.
	 * @param name
	 */
	public Resource(String name) {
		this.name = name;
	}
	
	/**
	 * Constructor for Resource Class for reproducing delays in final solution.
	 * @param newName - name for recreated resource
	 * @param newSchedule - schedule for recreated resource
	 * @param newTotalTime - total time for recreated resource
	 */
	public Resource(String newName, List<Timeslot> newSchedule, int newTotalTime) {
		this.name = newName;
		this.schedule = newSchedule;
		this.totalTime = newTotalTime;
	}
	
	/**
	 * Get name of Resource.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get plan containing all the items which are requesting to use this Resource.
	 * @return plan
	 */
	public List<Timeslot> getPlan() {
		return plan;
	}
	
	/**
	 * Get the schedule containing which items occupied this Resource.
	 * @return actualSchedule
	 */
	public List<Timeslot> getSchedule() {
		return schedule;
	}
	
	/**
	 * Get the timespan taken by an Item to use this Resource in a timeslot.
	 * @return time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Set the timespan duration for the current Item occupying this Resource.
	 * @param newTime
	 */
	public void setTime(int newTime) {
		time = newTime;
	}
	
	/**
	 * Reset time value in this resource
	 */
	public void resetTime() {
		time = 0;
	}
	
	/**
	 * Get the total amount of time for the resource to be used by each requesting item.
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
	 * @return size of schedule
	 */
	public int getScheduleSize() {
		return schedule.size();
	}
	
	/**
	 * Add timeslot to plan.
	 * @param newTimeslot - new timeslot added to resource plan
	 */
	public void addToPlan(Timeslot newTimeslot) {
		plan.add(newTimeslot);
	}
	
	/**
	 * Remove timeslot from the resource plan after creating the schedule.
	 * @param timeslot
	 */
	public void removeFromPlan(Timeslot timeslot) {
		plan.remove(timeslot);
	}
	
	/**
	 * Add timeslot to schedule archive.
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
	 * Add time from new timeslot to the amount of time taken for resource to
	 * complete the schedule while accounting for delays.
	 * 
	 * @param newTime      - time duration of new timeslot.
	 * @param previousTime - time elapsed for current item in its previous timeslot.
	 */
	public void addToTotalTime(int newTime, int previousTime) {
		// total time = totalTime + the new timeslot time + waiting time for current item to be freed.
		totalTime += newTime + (previousTime - totalTime);
	}
	
	/**
	 * Add time of new timeslot to total time for resource.
	 * @param newTime - time for new timeslot
	 */
	public void addTime(int newTime) {
		totalTime += newTime;
	}
	
	/**
	 * Get current value of expanded size after one or more delays. 
	 * have been added to the schedule
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
