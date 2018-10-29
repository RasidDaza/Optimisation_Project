package queens;

/**
 * Main class for the n-queens Genetic Algorithm implementation.
 * 
 * @author Shenal Abeyasinghe
 *
 */
public class Main {

	private Population population = null; // Genetic Algorithm Population.

	public static void main(String[] args) {
		// Record start time of running algorithm + printing results.
		final long startTime = System.currentTimeMillis();
		
		// Board Size = 8, Population Size = 10
		Population p = new Population(8, 10, startTime);
//		Population p = new Population(10, 100, startTime);
//		Population p = new Population(10, 100, startTime);
		
//		ModifiedPopulation p2 = new ModifiedPopulation(8, 10, startTime);
//		ModifiedPopulation p2 = new ModifiedPopulation(10, 100, startTime);
//		ModifiedPopulation p2 = new ModifiedPopulation(100, 200, startTime);
		
		p.printFinalResult(); // Display final population.
//		p2.printFinalResult(); // Display final population.
		
		// Record end time of running algorithm + printing results.
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + ((endTime - startTime) / 1000) + " seconds\t"
				+ (endTime - startTime) + " ms");
	}

}
