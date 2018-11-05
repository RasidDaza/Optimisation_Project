# Optimisation_Project
Contains source code for the N Queens Program from Semester 1, and source code for the Deadlock Solver Program (Deadlock Prevention Problem) from Semester 2.

Deadlock Solver Program located in src/deadlock

Basic Genetic Algorithm implementation for solving N Queens Program located in src/queens

Both programs written in Java.

# Deadlock Solver Program

The Deadlock Solver Program is able to solve generalised versions of a Deadlock Prevention Problem. Only standard Java libraries used (including JavaFX) have been utilised in the program. There are two ways to setup the problem in the program, User Setup Mode or Random Setup Mode.

## User Setup Mode

In User Setup Mode, users set up the problem using the GUI. The typical procedure to run the program in User Setup Mode is as follows:

1. Click the "Add Item" and "Add Resource" buttons to create all the Items (Entities wishing to use a Resource) and Resources.
2. Click an Item (circle) and click a Resource (rectangle) to make a connection between the Item and Resource. This creates a request where the clicked Item wishes to use the clicked Resource.
3. (optional) Click the "Show Plan" button to see what the Resource Plan constructed by the user looks like.
4. (optional) Click the "Save Plan" button to save the problem you have created so it can be reloaded the next time the Deadlock Solver Program is opened.
5. Set the Population Size input box to a number above 5 and (preferably) below 1000. This denotes the population size in the Genetic Algorithm.
6. Press the "Run GA" button to initiate the Genetic Algorithm.
7. After the Genetic Algorithm is finished (approx. 2 seconds), press the "Show Timetable" button to view the Timetable Chart for the Final Solution.

## Random Setup Mode

In Random Setup Mode, users only need to enter the parameters on the top right of the GUI including the Population Size, Number of Items and Number of Resources. The program will then automaticaaly generated the specified number of Items and Resources. It also constructs a randomised Plan for each Resource or in other words, it generates a bunch of random requests from any Item to any Resource. Random Setup Problems offer users the option to skip using the GUI and wasting time.

The typical procedure to run the program in Random Setup Mode is as follows:

1. Enter the values for the Population Size, Number of Items and Number of Resources parameters in the input boxes on the top right of the GUI.
2. Press the "Create Random Problem" button to generate a random problem and then initiate the Genetic Algorithm.
3. Wait for the Genetic Algorithm to complete (the more complex the problem is, the longer the algorithm will take).
4. If the Number of Items and Number of Resources is each below 20, then press the "Show Timetable" button to view the Timetable Chart for the Final Solution.

Random Setup Mode is geared towared testing the performance of the program, therefore users are not expected to use the Random Setup Mode.






