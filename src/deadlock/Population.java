package deadlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genetic Algorithm (Optimisation) Class to find optimal solution for Deadlock
 * Prevention Problems. Size of Population must be greater than 5.
 * 
 * @author Shenal
 *
 */
public class Population {

	private Manager firstIndividual = null; // Gives resource plan for Random Setup GA.
	private int populationSize; // Population Size
	private List<Manager> deadlocks = null;
	private List<Manager> children = null; // Offspring from crossover parents.
	private long startTime; // Starting time when the program runs.
	private List<Item> items = null; // List of items used by individuals in population.
	private List<Resource> resources = null; // List of resources used by individuals in population.

	private List<Item> finalItems = null; // Items used by final solution (fittest individual).
	private List<Resource> finalResources = null; // Resources used by final solution (fittest individual)

	private int averageResult = 0; // Average result of population in one generation.

	/**
	 * Constructor for Population Class when running a Random Setup Problem. After
	 * specifying the numnber of items and resources, the items, resources and
	 * resource plan are automatically generated.
	 * 
	 * @param populationSize - fixed number of individuals in the population.
	 * @param itemSize       - fixed number of items
	 * @param resourceSize   - fixed number of resources
	 */
	public Population(int populationSize, int itemSize, int resourceSize) {
		startTime = System.currentTimeMillis(); // Start time of running GA
		firstIndividual = new Manager(itemSize, resourceSize);
		this.populationSize = populationSize;

		this.items = firstIndividual.getItems();
		this.resources = firstIndividual.getResources();

		System.out.println("Start----------------------------------------------------");
		deadlocks = new ArrayList<Manager>(populationSize);

		initialisePopulation(); // Population Initialisation

		System.out.println("Initial Population Fitnesses --------------------");
		printIndividualFitness();

		Random r = new Random();
		Manager parent1 = null; // First crossover parent.
		Manager parent2 = null; // Second crossover parent.
		// Start Genetic Algorithm
		for (int cycles = 0; cycles < 100; cycles++) {

			// Crossover Fraction = 0.8
			float crossoverPercentage = r.nextFloat();
			// Mutation Fraction = 0.2
			float mutationPercentage = r.nextFloat();

			// Best 2 out of random 5 for selecting parents
			List<Manager> parents = selectParents(); // Parent Selection
			parent1 = parents.get(0);
			parent2 = parents.get(1);
//			Manager child1 = new Manager(items, resources);
//			Manager child2 = new Manager(items, resources);
			Manager child1 = new Manager(parents.get(0).getItems(), parents.get(0).getResources());
			Manager child2 = new Manager(parents.get(1).getItems(), parents.get(1).getResources());

			if (crossoverPercentage < 0.8) { // 80% chance of doing crossover.
				children = performCrossover(parent1, parent2, child1, child2);
				// 20% chance of doing mutation.
				if (mutationPercentage < 0.2) {
					performMutation(children.get(0)); // Mutation for child 1
					performMutation(children.get(1)); // Mutation for child 2
				}
				rescoreChildren(child1, child2); // Recheck Child Fitnesses
				performTournamentSelection(parent1, parent2); // Survivor Selection
			}
			// Check if GA is still effective at making progress or not
			// if there is a high amount of resources
			if (resources.size() > 100 && cycles % 100 == 0) { // Check every 100 cycles
				if (!(checkAlgorithmProgress())) {
//					System.out.print("Iteration: " + cycles);
					break; // Finish GA
				}
			}
		}
		System.out.println("Final Population Fitnesses --------------------");
		printIndividualFitness(); // OUTPUT OF POPULATION SHOULD BE DIFFERENT AFTER GA
		System.out.println("Get Final Solution----------------------------");
		returnSolution(); // Get final solution with highest fitness produced by GA
		printExecutionTime(); // Get full GA runtime
	}

	/**
	 * Constructor for Population class when running a GUI or user defined problem.
	 * Users use the GUI to setup all items resources and the resource plan that
	 * will be fed into every individual in the population.
	 * 
	 * @param populationSize - fixed number of individuals in the population.
	 * @param items          - list of items.
	 * @param resources      - list of resources.
	 */
	public Population(int populationSize, List<Item> items, List<Resource> resources) {
		startTime = System.currentTimeMillis();
		this.populationSize = populationSize;
		this.items = items;
		this.resources = resources;

		System.out.println("Start----------------------------------------------------");
		deadlocks = new ArrayList<Manager>(populationSize);
		initialisePopulation(); // Population Initialisation
		System.out.println("Initial Population Fitnesses --------------------");
//		printIndividualFitness();

		Random r = new Random();
		Manager parent1 = null; // First crossover parent.
		Manager parent2 = null; // Second crossover parent.
		// Start Genetic Algorithm
		for (int cycles = 0; cycles < 1000; cycles++) {

			// Crossover Percentage = 0.8
			float crossoverPercentage = r.nextFloat();
			// Mutation Percentage = 0.2
			float mutationPercentage = r.nextFloat();

			// Best 2 out of random 5 for selecting parents
			List<Manager> parents = selectParents(); // Parent Selection
			parent1 = parents.get(0);
			parent2 = parents.get(1);
			Manager child1 = new Manager(items, resources);
			Manager child2 = new Manager(items, resources);

			if (crossoverPercentage < 0.8) { // 80% chance of doing crossover.
				children = performCrossover(parent1, parent2, child1, child2);
				// 20% chance of doing mutation.
				if (mutationPercentage < 0.2) {
					performMutation(children.get(0)); // Mutation for child 1
					performMutation(children.get(1)); // Mutation for child 2
				}
				rescoreChildren(child1, child2); // Recheck Child Fitnesses
				performTournamentSelection(parent1, parent2); // Survivor Selection
			}

			// Check if GA is still effective at making progress or not
			if (cycles % 1000 == 0) { // Check every 100 cycles
				if (!(checkAlgorithmProgress())) {
					break; // Finish GA
				}
			}
		}
		System.out.println("Final Population Fitnesses --------------------");
//		printIndividualFitness();
		System.out.println("Get Final Solution----------------------------");
		returnSolution(); // Get final solution with highest fitness produced by GA
		printExecutionTime(); // Get full GA runtime
	}

	/**
	 * Create initial population based on predefined population size to start first
	 * stage of Genetic Algorithm.
	 */
	private void initialisePopulation() {
		for (int i = 0; i < populationSize; i++) {
			Manager newDeadlock = new Manager(items, resources);
			deadlocks.add(newDeadlock);
		}
	}

	/**
	 * Pick two individuals from population to be two parents for crossover and
	 * mutation.
	 * 
	 * @return parents - both parent individuals
	 */
	private List<Manager> selectParents() {
		Random r = new Random();
		List<Manager> possibleParents = new ArrayList<Manager>(); // 5 individuals.
		List<Manager> parents = new ArrayList<Manager>(); // 2 parents.
		List<Integer> indexes = new ArrayList<Integer>();
		int i = 0;
		while (i < 5) { // Get 5 individuals
			int parentIndex = r.nextInt(populationSize);
			// Add unique individuals to be potential parents.
			if (!(indexes.contains(parentIndex))) {
				// Ensure individual is not picked again.
				indexes.add(parentIndex);
				possibleParents.add(deadlocks.get(parentIndex));
				i++;
			}
		}
		for (int j = 0; j < 2; j++) { // Pick 2 individuals to be parents.
			Manager minDeadlock = possibleParents.get(0); // Remove best solution.
			for (int k = 0; k < possibleParents.size(); k++) {
				if (minDeadlock.getResult() > possibleParents.get(k).getResult()) {
					minDeadlock = possibleParents.get(k);
				}
			}
			parents.add(minDeadlock); // Add fittest 2 out of 5 individuals.
			possibleParents.remove(minDeadlock);
		}
		return parents;
	}

	/**
	 * Run crossover mutation on two new children individuals to ensure both
	 * children inherit genes (structure) from both parents.
	 * 
	 * @param parent1 - Parent 1
	 * @param parent2 - Parent 2
	 * @param child1  - Child 1
	 * @param child2  - Child 2
	 * @return newChildren - list containing both (two) children individuals
	 */
	private List<Manager> performCrossover(Manager parent1, Manager parent2, Manager child1, Manager child2) {
		// One Point Crossover
		Random r = new Random();
		// Determines which resource will have its schedule swapped between both
		// children.
		int crossoverPoint = r.nextInt(resources.size());

//		System.out.println("-------Parent-----------");
//		printDead21(parent1, parent2);
//		System.out.println("-------Children-----------");
//		printDead3(child1, child2);

		// Swap schedules for randomly picked resource
		for (int k = 0; k < resources.get(crossoverPoint).getScheduleSize(); k++) {
			Timeslot temp = parent1.getResources().get(crossoverPoint).getSchedule().get(k);
			child1.getResources().get(crossoverPoint).getSchedule().set(k,
					parent2.getResources().get(crossoverPoint).getSchedule().get(k));
			child2.getResources().get(crossoverPoint).getSchedule().set(k, temp);
		}

//		System.out.println("-------Modified Children---------");
//		printDead3(child1, child2);

		List<Manager> newChildren = new ArrayList<Manager>();
		newChildren.add(child1);
		newChildren.add(child2);
		return newChildren; // Feed new children back into population.
	}

	/**
	 * Run mutation operation on two new children to independently change both
	 * individuals structure/genotype.
	 * 
	 * @param deadlock - child individual
	 */
	private void performMutation(Manager deadlock) {
		// Swap Mutation
		Random r = new Random();
		int scheduleIndex = r.nextInt(deadlock.getResources().size()); // Which resource will have its schedule mutated
		// startPoint (First timeslot in schedule) to be swapped with endPoint (another timeslot in schedule)
		int startPoint = r.nextInt(deadlock.getResources().get(scheduleIndex).getScheduleSize()); 																							
		int endPoint = r.nextInt(deadlock.getResources().get(scheduleIndex).getScheduleSize()); 																							
		Timeslot temp = deadlock.getResources().get(scheduleIndex).getSchedule().get(startPoint);
		// Swap the two randomly selected timeslots in the resource schedule
		deadlock.getResources().get(scheduleIndex).getSchedule().set(startPoint,
				deadlock.getResources().get(scheduleIndex).getSchedule().get(endPoint));
		deadlock.getResources().get(scheduleIndex).getSchedule().set(endPoint, temp);

	}

	/**
	 * Perform Survivor Selection operation to eliminate enough individuals to
	 * maintain fixed population size after children individuals are created. Done
	 * via Tournament Selection where the two best/fittest individuals among both
	 * parents and both children are retained and the other two eliminated.
	 * 
	 * @param parent1 - Parent 1
	 * @param parent2 - Parent 2
	 */
	private void performTournamentSelection(Manager parent1, Manager parent2) {
		// Remove parents from population and add two tournament winners later
		deadlocks.remove(parent1);
		deadlocks.remove(parent2);
		List<Manager> tournamentParticipants = new ArrayList<Manager>();
		List<Manager> winners = new ArrayList<Manager>();
		Manager winner = null;

		// Put both parents and children in tournament
		tournamentParticipants.add(parent1);
		tournamentParticipants.add(parent2);
		tournamentParticipants.add(children.get(0));
		tournamentParticipants.add(children.get(1));

		// Get two best solutions out of both parents and children (2 out of 4)
		for (int i = 0; i < 2; i++) {
			winner = tournamentParticipants.get(0);
			for (int j = 0; j < tournamentParticipants.size(); j++) {
				if (winner.getResult() > tournamentParticipants.get(j).getResult()) {
					winner = tournamentParticipants.get(j);
				}
			}
			winners.add(winner);
			tournamentParticipants.remove(winner);

		}

		// Add winners back into population
		for (int k = 0; k < 2; k++) {
			deadlocks.add(winners.get(k));
		}

	}

	/**
	 * Get the final or at least best effort solution after running Genetic
	 * Algorithm for user defined or random problem.
	 * 
	 * @return finalSolution - Optimal/Best Effort Solution found after running GA.
	 */
	private Manager returnSolution() {
		int index = 0; // Index of best solution in the population list
		int fitness = deadlocks.get(0).getResult(); // fitness value of best solution
		for (int i = 0; i < deadlocks.size(); i++) {
			if (deadlocks.get(i).getResult() < fitness) {
				fitness = deadlocks.get(i).getResult();
				index = i;
			}
		}

		System.out.println("Final Result: " + deadlocks.get(index).getResult());

		deadlocks.get(index).setResources(); // Retrieve schedule with delays for each resource in final solution
		finalItems = deadlocks.get(index).getItems(); // Transfer current Items setup to GUI
		finalResources = deadlocks.get(index).getResources(); // Transfer current Resources setup to GUI
		return deadlocks.get(index);
	}

	/**
	 * Get bad solution from early stage of algorithm to get a benchmark to show
	 * improvement done by GA.
	 * 
	 * @return worstSolution - individual from population with lowest fitness.
	 */
	private Manager returnWorstSolutionResult() {
		Manager worstSolution = null;
		for (Manager m : deadlocks) {
			if (m.getResult() > worstSolution.getResult()) {
				worstSolution = m;
			}
		}
		return worstSolution;
	}

	/**
	 * Reevaluate fitnesses for both new children after crossover and/or mutation
	 * operators
	 * 
	 * @param child1 - Child 1
	 * @param child2 - Child 2
	 */
	private void rescoreChildren(Manager child1, Manager child2) {
		// Recalculate fitness for modified version of child1
		child1.clearResources();
		child1.resetStuff();
		child1.calculateScheduleTime();
		child1.calculateResult();
		child1.copyResources();
		child1.removeDelays();

		// Recalculate fitness for modified version of child2
		child2.clearResources();
		child2.resetStuff();
		child2.calculateScheduleTime();
		child2.calculateResult();
		child2.copyResources();
		child2.removeDelays();
	}

	/**
	 * Evaluate how well the Genetic Algorithm is working at finding improved
	 * solutions periodically. Stop GA if rate of progress is nill.
	 * 
	 * @return boolean result whether GA should be stopped or not.
	 */
	private boolean checkAlgorithmProgress() {
		int averageFitness = 0; // Average Fitness of current population
		// Calculate average fitness of population
		for (Manager m : deadlocks) {
			averageFitness += m.getResult();
		}
		averageFitness = averageFitness / deadlocks.size();
		// Check if average fitness has improved (is lower) than
		// last time algorithm progress was checked.
//		System.out.println("Average Fitness: " + averageFitness);
		if (averageFitness == averageResult) {
			return false;
		} else {
			averageResult = averageFitness;
			return true;
		}
	}

	/**
	 * Get amount of time taken to run Genetic Algorithm on problem.
	 */
	private void printExecutionTime() {
		// Record end time of running algorithm + printing results.
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + ((endTime - startTime) / 1000) + " seconds\t"
				+ (endTime - startTime) + " ms");
	}

	/**
	 * Get items of final solution.
	 * 
	 * @return finalItems
	 */
	public List<Item> getFinalItems() {
		return finalItems;
	}

	/**
	 * Get resources of final solution.
	 * 
	 * @return finalResources
	 */
	public List<Resource> getFinalResources() {
		return finalResources;
	}

	/**
	 * Print results (fitnesses) for each individual currently in population.
	 */
	private void printIndividualFitness() {
		for (Manager m : deadlocks) {
			System.out.println("Indivdual Result: " + m.getResult());
		}
	}

	private void printDead() {
		for (Manager m : deadlocks) {
			for (Resource r : m.getResources()) {
				System.out.println(r.getName() + ": " + r.getSchedule());
			}
		}
	}

	private void printDead2() {
		for (Manager m : children) {
			System.out.println("Child Result: " + m.getResult());
		}
	}

	private void printDead21(Manager parent1, Manager parent2) {

		for (Resource r : parent1.getResources()) {
			System.out.println(r.getName() + ": " + r.getSchedule());
		}
		System.out.println("Parent 2 ------------------");
		for (Resource r : parent2.getResources()) {
			System.out.println(r.getName() + ": " + r.getSchedule());
		}
	}

	private void printDead3(Manager child1, Manager child2) {

		for (Resource r : child1.getResources()) {
			System.out.println(r.getName() + ": " + r.getSchedule());
		}
		System.out.println("---------Child 2------");
		for (Resource r : child2.getResources()) {
			System.out.println(r.getName() + ": " + r.getSchedule());
		}
		System.out.println("----------------------");

	}

}
