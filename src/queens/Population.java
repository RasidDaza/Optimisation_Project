package queens;

import java.util.*;

/**
 * Represents the population of candidate solutions to incorporate Genetic
 * Algorithm to the N-Queens Problem.
 * 
 * @author Shenal Abeyasinghe
 *
 */
public class Population {

	private int n; // Board size
	private int populationSize; // Population Size
	private List<Board> boards = null; // Individuals in population
	private List<Board> children = null; // Offspring from crossover parents.
	private long startTime; // Starting time when the program runs.

	/**
	 * Constructor for Population.
	 * 
	 * @param n
	 *            - size of board (nxn) and the number of queens on board.
	 * @param p
	 *            - size of population for Genetic Algorithm.
	 */
	public Population(int n, int p, long startTime) {
		this.n = n;
		this.populationSize = p;
		this.startTime = startTime;
		boards = new ArrayList<Board>(n);
		initialisePopulation();
		recheckConflicts();
		Random r = new Random();
		Board parent1 = null; // First crossover parent.
		Board parent2 = null; // Second crossover parent.		
		// Run algorithm for 10000 cycles.
		for (int i = 0; i < 10000; i++) {			
			// "Original" Genetic Algorithm Implementation
			// Crossover Fraction = 0.8
			float crossoverPercentage2 = r.nextFloat();
			// Mutation Fraction = 0.2
			float mutationPercentage2 = r.nextFloat();
			// Roulette Wheel Selection for selecting parents.
			parent1 = selectParent(null);
			parent2 = selectParent(parent1);
			if (crossoverPercentage2 < 0.8) { // 80% chance of doing crossover.
				children = performCrossover(parent1, parent2);
				// 20% chance of doing mutation.
				recheckConflicts(children);
				if (mutationPercentage2 < 0.2) {
					performMutation(children.get(0)); // Mutation for child 1
					performMutation(children.get(1)); // Mutation for child 2
				}
				recheckConflicts(children);
				performTournamentSelection(parent1, parent2);
			}
		}
	}

	/**
	 * Create initial population to initialise the first step of the Genetic
	 * Algorithm.
	 */
	public void initialisePopulation() {
		for (int i = 0; i < populationSize; i++) {
			Board newBoard = new Board(n);
			boards.add(newBoard);
		}
	}
	
	/**
	 * Check the fitness of all individuals in population.
	 */
	public void recheckConflicts() {
		for (Board b : boards) {
			b.checkConflicts();
			if (b.getConflictCount() == 0) {
				System.out.println("Solution Found!");
				System.out.println("Board: " + b.getQueens().toString() + "\t" + "Number of Conflicts: " + b.getConflictCount());
				final long endTime = System.currentTimeMillis();
				System.out.println("Total execution time: " + ((endTime - startTime) / 1000) + " seconds\t"
						+ (endTime - startTime) + " ms");
				System.exit(0);
			}
		}
	}
		
	/**
	 * Check the fitness of children after the crossover/mutation operators.
	 * @param boardsList - list of children.
	 */
	public void recheckConflicts(List<Board> boardsList) {
		for (Board b : boardsList) {
			b.checkConflicts();
			if (b.getConflictCount() == 0) {
				System.out.println("Solution Found!");
				System.out.println("Board: " + b.getQueens().toString() + "\t" + "Number of Conflicts: " + b.getConflictCount());
				final long endTime = System.currentTimeMillis();
				System.out.println("Total execution time: " + ((endTime - startTime) / 1000) + " seconds\t"
						+ (endTime - startTime) + " ms");
				System.exit(0); // Terminate program
			}
		}
	}

	/**
	 * Use Roulette Wheel Selection to choose one of the individuals/candidate
	 * solutions to be a parent for crossover/mutation operations.
	 * 
	 * @return one individual chosen to be a parent by roulette wheel selection.
	 */
	public Board selectParent(Board previousParent) {
		Random r = new Random();
		
		// If one parent has already been selected, temporarily remove it for second parent selection
		if (previousParent != null) {
			boards.remove(previousParent);
		}
		
		// Total fitness of all individuals in population.
		int totalFitness = 0;
		// Fitness of each individual relative to whole population.
		List<Float> relFitness = new ArrayList<Float>();
		for (Board b : boards) {
			float num = b.getConflictCount();
			relFitness.add(num);
			totalFitness += b.getConflictCount();
		}
		// Calculate all relative fitnesses.
		for (int c = 0; c < relFitness.size(); c++) {
			relFitness.set(c, relFitness.get(c) / totalFitness);
		}
		// Find bounds for all fitnesses in population.
		float min = 0;
		float max = 0;
		for (float num : relFitness) {
			if (num > max) {
				max = num; // Set fitness to be maximum fitness bound.
			} else if (num < min) {
				min = num; // Set fitness to be minimum fitness bound.
			}
		}
		float c = 0;
		float roulette = min + r.nextFloat() * (max - min);
		int chosenIndex = 0;

		// Roulette Wheel Selection
		for (int d = 0; d < populationSize; d++) {
			c += relFitness.get(d);
			if (roulette <= c) {
				chosenIndex = d;
				break;
			}
		}
		// If one parent has already been selected and removed from population, add it back in
		if (previousParent != null) {
			boards.add(previousParent);
		}
		
		return boards.get(chosenIndex);
	}

	/**
	 * Perform the crossover step between two candidate solutions. Crossover is
	 * two point crossover.
	 * 
	 * @param parent1
	 *            - one parent individual for crossover step.
	 * @param parent2
	 *            - another parent individual for crossover step.
	 * @return list of new children created by crossover step.
	 */
	public List<Board> performCrossover(Board parent1, Board parent2) {
		// Two Point Crossover
		Random r = new Random();
		int crossoverPoint = r.nextInt(n) + 1;
		int crossoverPoint2 = r.nextInt(n) + 1;
		// Ensure crossover segment is indexed from left to right.
		if (crossoverPoint > crossoverPoint2) {
			int temp = crossoverPoint;
			crossoverPoint = crossoverPoint2;
			crossoverPoint2 = temp;
		}
		Board child1 = new Board(n);
		Board child2 = new Board(n);
		// Avoid java copy variable problem (cloning)
		for (int k = 0; k < n; k++) {
			child1.getQueens().set(k, parent1.getQueens().get(k));
			child2.getQueens().set(k, parent2.getQueens().get(k));
		}
		// Modify queens within the crossover segment for both children.
		for (int i = crossoverPoint; i < crossoverPoint2; i++) {
			child1.getQueens().set(i, parent2.getQueens().get(i));
			child2.getQueens().set(i, parent1.getQueens().get(i));
		}

		List<Board> newChildren = boards;
		child1 = performRepairFunction(child1); // Remove duplicates in child 1.
		child2 = performRepairFunction(child2); // Remove duplicates in child 2.
		newChildren.add(child1);
		newChildren.add(child2);
		return newChildren; // Feed new children back into population.
	}

	/**
	 * Perform the mutation step for one candidate solution. Mutation is single
	 * bit mutation.
	 * 
	 * @param board
	 *            - child (newly created individual) from population.
	 */
	public void performMutation(Board board) {
		// Single bit Mutation (Work Around)
		Random r = new Random();
		int mutationPoint = r.nextInt(n) + 1;
		int remove = board.getQueens().get(mutationPoint - 1); 															
		board.getQueens().remove(mutationPoint - 1); // Remove queen.
		// Add the removed queen back while on the last column.
		board.getQueens().add(remove); 
	}

	/**
	 * Perform repair function step for one candidate solution/individual.
	 * 
	 * @param child
	 *            - new individual from population of n-queens candidate
	 *            solutions.
	 * @return child from population which has been repaired.
	 */
	public Board performRepairFunction(Board child) {
		List<Integer> replacementList = new ArrayList<Integer>();
		for (int i = 1; i < n + 1; i++) {
			if (!(child.getQueens().contains(i))) {
				// Find positions which are not occupied by a queen.
				replacementList.add(i);
			}
		}

		int index = 0; // Traverse through unoccupied positions.
		Set<Integer> queenSet = new HashSet<Integer>();
		for (int j = 0; j < n; j++) {
			// Replace duplicate positions with unoccupied positions.
			if (queenSet.add(child.getQueens().get(j)) == false) {
				child.getQueens().set(j, replacementList.get(index));
				index++;
			}
		}
		return child;
	}
	
	/**
	 * Remove two worst solutions to reduce population.
	 * 
	 * @param parentIndex1
	 *            - index of the first parent in the crossover step.
	 * @param parentIndex2
	 *            - index of the second parent in the crossover step.
	 */
	public void performTournamentSelection(Board parent1, Board parent2) {
//		for (int i = 0; i < 2; i++) { // Pick two individuals.
//			Board maxBoard = children.get(0); // Worst individual
//			for (int j = 0; j < children.size(); j++) {
//				if (maxBoard.getConflictCount() < children.get(j).getConflictCount()) {
//					maxBoard = children.get(j); 
//				}
//			}
//			children.remove(maxBoard); // Remove individual with worst fitness.
//		}
		
		// IMPLEMENTATION FROM DEADLOCK PROJECT
//		// Remove parents from population and add two tournament winners later
		boards.remove(parent1);
		boards.remove(parent2);	
		List<Board> tournamentParticipants = new ArrayList<Board>();
		List<Board> winners = new ArrayList<Board>();
		Board winner = null;
		
		// Put both parents and children in tournament
		tournamentParticipants.add(parent1);
		tournamentParticipants.add(parent2);
		tournamentParticipants.add(children.get(0));
		tournamentParticipants.add(children.get(1));
		
		// Get two best solutions out of both parents and children (2 out of 4)
		for (int i = 0; i < 2; i++) { 
			winner = tournamentParticipants.get(0);
			for (int j = 0; j < tournamentParticipants.size(); j++) {
				if (winner.getConflictCount() > tournamentParticipants.get(j).getConflictCount()) {
					winner = tournamentParticipants.get(j);
				}
			}	
			winners.add(winner);
			tournamentParticipants.remove(winner);
			
		}
		
		// Add winners back into population
		for (int k = 0; k < 2; k++) {
			boards.add(winners.get(k));
		}
	}

	/**
	 * Get one of the candidate solutions (board configurations) in the
	 * population.
	 * 
	 * @param index
	 *            - list index of a board
	 * @return one board configuration (candidate solution)
	 */
	public Board getBoard(int index) {
		return boards.get(index);
	}

	/**
	 * Print each individual from the final population.
	 */
	public void printFinalResult() {
		for (Board b : boards) {
//			 System.out.println("Board: " + b.getQueens().toString() + "\t" + "Number of Conflicts: " + b.getConflictCount());
			if (b.getConflictCount() == 0) {
				System.out.println(
						"Board: " + b.getQueens().toString() + "\t" + "Number of Conflicts: " + b.getConflictCount());
			}
		}
	}

}
