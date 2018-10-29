package queens;

import java.util.*;

/**
 * One instance of a chess board containing n queens placed 
 * in random positions.
 * @author Shenal Abeyasinghe
 *
 */
public class Board {

	private int n; // Board size
	// Number of position clashes between n-queens on chess board.
	private int conflictCount = 0;
	private List<Integer> queens = null; // List of positions for each queen.

	/**
	 * Constructor for board.
	 * 
	 * @param n
	 *            - size of board (nxn) and the number of queens on board.
	 */
	public Board(int n) {
		this.n = n;
		queens = initialiseBoard(n);
		checkConflicts();
	}

	/**
	 * Get the total number of conflicts on the board.
	 * 
	 * @return number of conflicts in board configuration.
	 */
	public int getConflictCount() {
		return conflictCount;
	}

	/**
	 * Make a new n-queens candidate solution/chess board and initialise the
	 * positions of n new queens
	 * 
	 * @param n
	 * @return configuration of queens on board.
	 */
	public static List<Integer> initialiseBoard(int n) {
		Random r = new Random();
		List<Integer> queenConfiguration = new ArrayList<Integer>();
		// Create n queens placed in initial diagonal line configuration.
		for (int i = 1; i < n + 1; i++) {
			queenConfiguration.add(i);			
		}
		// Swap positions of queens to create randomised board configuration.
		for (int j = 0; j < queenConfiguration.size(); j++) {
			int randomPosition = r.nextInt(queenConfiguration.size());
			int temp = queenConfiguration.get(j);
			queenConfiguration.set(j, queenConfiguration.get(randomPosition));
			queenConfiguration.set(randomPosition, temp);
		}
		return queenConfiguration;
	}
	
	/**
	 * Get the list of positions with a queen placed on them.
	 * 
	 * @return queens
	 */
	public List<Integer> getQueens() {
		return queens;
	}
	
	/**
	 * Count the number of position conflicts between the queens on a board.
	 */
	public void checkConflicts() {
		int up = 1;
		int down = 1;
		// Browse through the position of queens on the board.
		for (int i = 0; i < n; i++) {
			if (i < (n-1)) { // Deal with last row (nth row) on board.
				while (up < n && down < n) {
					// Detect conflicts on upper diagonal positions.
					if (i + up < n) {
						if (queens.get(i) + up < (n+1) && queens.get(i) + up == queens.get(i + up)) {
							conflictCount++;
						}
					}
					// Detect conflicts on lower diagonal positions.
					if (i + down < n) {
						if (queens.get(i) - down > -2 && queens.get(i) - down == queens.get(i + down)) {
							conflictCount++;
						}
					}
					up++;
					down++;
				}
			}			
			up = 1;
			down = 1;
		}
	}
}
