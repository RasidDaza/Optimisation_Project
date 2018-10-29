package queens;

import java.util.*;

/**
 * Represents the population of candidate solutions to incorporate Genetic
 * Algorithm to the n-queens problem.
 * 
 * @author Shenal Abeyasinghe
 *
 */
public class ModifiedPopulation {

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
	public ModifiedPopulation(int n, int p, long startTime) {
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
			 // Modified Genetic Algorithm Implementation
			 float mutationPercentage = r.nextFloat(); // Mutation % = 80%
			 // Best 2 out of random 5 individuals to be parents.
			 List<Board> parents = selectParents();
			 parent1 = parents.get(0);
			 parent2 = parents.get(1);
			 children = performCrossover(parent1, parent2);
			 recheckConflicts(children);
			 if (mutationPercentage < 0.8) { // 80% chance of doing mutation.
				 performMutation(children.get(0));
				 performMutation(children.get(1));
			 }
			 recheckConflicts(children);
			 performTournamentSelection(parent1, parent2);
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
			// End algorithm if a solution with 0 conflicts appears.
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
	 * Selects two individuals from the population to be parents for the
	 * crossover operation. Parents are picked by first choosing 5 random
	 * individuals. Then the 2 individuals with the highest fitness are chosen
	 * out of the 5 individuals.
	 * 
	 * @return one list of two individuals chosen to be a parent by the 2 out of
	 *         5 selection criteria.
	 */
	public List<Board> selectParents() {
		Random r = new Random();
		List<Board> possibleParents = new ArrayList<Board>(); // 5 individuals.
		List<Board> parents = new ArrayList<Board>(); // 2 parents.
		List<Integer> indexes = new ArrayList<Integer>();
		int i = 0;
		while (i < 5) { // Get 5 individuals
			int parentIndex = r.nextInt(populationSize);
			// Add unique individuals to be potential parents.
			if (!(indexes.contains(parentIndex))) {
				// Ensure individual is not picked again.
				indexes.add(parentIndex); 
				possibleParents.add(boards.get(parentIndex));
				i++;
			}
		}
		for (int j = 0; j < 2; j++) { // Pick 2 individuals to be parents.
			Board minBoard = possibleParents.get(0); // Remove best solution.
			for (int k = 0; k < possibleParents.size(); k++) {
				if (minBoard.getConflictCount() > possibleParents.get(k).getConflictCount()) {
					minBoard = possibleParents.get(k); 
				}
			}
			parents.add(minBoard); // Add fittest 2 out of 5 individuals.
			possibleParents.remove(minBoard);
		}
		return parents;
	}

	/**
	 * Perform the mutation step for one candidate solution. Mutation is swap
	 * mutation.
	 * 
	 * @param board
	 *            - indivdual (candidate solution) from population.
	 */
	public void performMutation(Board board) {
		// Swap Mutation
		Random r = new Random();
		int startPoint = r.nextInt(n) + 1;
		int endPoint = r.nextInt(n) + 1;
		int temp = board.getQueens().get(startPoint - 1);
		// Swap the first and second queens.
		board.getQueens().set(startPoint - 1, board.getQueens().get(endPoint - 1));
		board.getQueens().set(endPoint - 1, temp);
	}

	/**
	 * Perform the crossover step between two candidate solutions. Crossover is
	 * single point crossover or cut-and-crossfill crossover.
	 * 
	 * @param parent1
	 *            - one parent individual for crossover step.
	 * @param parent2
	 *            - another parent individual for crossover step.
	 * @return list of new children created by crossover step.
	 */
	public List<Board> performCrossover(Board parent1, Board parent2) {
		// Cut and Crossfill Crossover (Single Point Crossover)
		Random r = new Random();
		int crossoverPoint = r.nextInt(n) + 1;
		// Divide both parents into two segments each.
		List<Integer> parent1List1 = parent1.getQueens().subList(0, crossoverPoint);
		List<Integer> parent1List2 = parent1.getQueens().subList(crossoverPoint, n);
		List<Integer> parent2List1 = parent2.getQueens().subList(0, crossoverPoint);
		List<Integer> parent2List2 = parent2.getQueens().subList(crossoverPoint, n);
		Board child1 = new Board(n);
		Board child2 = new Board(n);

		// Crossover for first segments of each child.
		for (int i = 0; i < crossoverPoint; i++) {
			child1.getQueens().set(i, parent1List1.get(i));
			child2.getQueens().set(i, parent2List1.get(i));
		}
		int count1 = crossoverPoint; // Keep track of second segment of child 1.
		int count2 = crossoverPoint; // Keep track of second segment of child 2.
		
		// Crossover for second segments of each child.
		for (int j = 0; j < n; j++) {
			if (!(child1.getQueens().contains(parent2.getQueens().get(j)))) {
				child1.getQueens().set(count1, parent2.getQueens().get(j));
				count1++;
			}
			if (!(child2.getQueens().contains(parent1.getQueens().get(j)))) {
				child2.getQueens().set(count2, parent1.getQueens().get(j));
				count2++;
			}
		}
		List<Board> newChildren = boards;
		child1 = performRepairFunction(child1); // Remove duplicates in child 1.
		child2 = performRepairFunction(child2); // Remove duplicates in child 2.
		newChildren.add(child1);
		newChildren.add(child2);
		return newChildren; // Feed new children back into population.
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
