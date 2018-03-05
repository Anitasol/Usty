package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {

	//The semaphore place
	public static Semaphore[][] semaphoreIn;
	public static Semaphore[][] semaphoreOut;
	
	public static Semaphore floorCountMutex;
	public static Semaphore personCountMutex;
	public static Semaphore exitedCountMutex;
	public static Semaphore elevatorCountMutex; //counting people in elevator
	
	public static ElevatorScene scene;
	
	public static boolean elevatorsMayDie; 
	public int whichElevator = 0;
	
	public static int maxElevatorSpace = 6;
	public static int currentPeopleInElev = 0;
	public static int countingFloors = 0; 
	
	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

	private int numberOfFloors;
	private int numberOfElevators;

	private Thread elevatorThread = null;
	
	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you
	ArrayList<Integer> exitedCount = null;
	

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {
		
		//elevatorsMayDie = true;
		if(elevatorThread != null) {
			if(elevatorThread.isAlive()) {
				
				try {
					elevatorThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//elevatorsMayDie = false;
		
		scene = this;
		
		semaphoreIn = new Semaphore[numberOfElevators][numberOfFloors];
		semaphoreOut = new Semaphore[numberOfElevators][numberOfFloors];
		
		for(int i = 0; i < numberOfElevators; i++) {
			for(int j = 0; j < numberOfFloors; j++) {
			semaphoreIn[i][j] = new Semaphore(0);
			}
			//semaphoreOut[i] = new Semaphore(0);
		}
		for(int i = 0; i < numberOfElevators; i++) {
			for(int j = 0; j < numberOfFloors; j++) {
				semaphoreOut[i][j] = new Semaphore(0);
			}
		}
		
		floorCountMutex = new Semaphore(1);
		personCountMutex = new Semaphore(1);
		
		//elevatorThread = new Thread(new Runnable() {

			//@Override
			/*public void run() {
				while(true) {
					
					if(ElevatorScene.elevatorsMayDie) {
	
						return;
					}
					
					for(int i = 0; i < maxElevatorSpace; i++) {
						ElevatorScene.semaphoreIn[i].release(); //signal
					}
				}
			}
			
		});
		elevatorThread.start();*/
		
		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 * 
		 * Also add any other code that initializes
		 * your system for a new run
		 * 
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */
		for (int i = 0 ; i < numberOfElevators; i++) {
			elevatorThread = new Thread(new Elevator(i));
			elevatorThread.start();
		}

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
		thread.start(); 
		
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */

		 
			
		return thread;  //this means that the testSuite will not wait for the threads to finish
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		//dumb code, replace it!
		return countingFloors;
	}
	
	public void decrementNumberOfPeopleInElev(int elevator) {
		try {
			ElevatorScene.floorCountMutex.acquire();
				currentPeopleInElev--;
			ElevatorScene.floorCountMutex.release();  //signal
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void incrementNumberOfPeopleInElev(int elevator) {
		try {
			ElevatorScene.floorCountMutex.acquire();
				currentPeopleInElev++;
			ElevatorScene.floorCountMutex.release();  //signal
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		
		
		//dumb code, replace it!
		 return currentPeopleInElev;
		
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			ElevatorScene.personCountMutex.acquire(); //wait
				//inside critical state
				personCount.set(floor, (personCount.get(floor) - 1));
			ElevatorScene.personCountMutex.release();  //signal
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			ElevatorScene.personCountMutex.acquire(); //wait
				//inside critical state
				personCount.set(floor, (personCount.get(floor) + 1));
			ElevatorScene.personCountMutex.release();  //signal
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {
			
			exitedCountMutex.acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}


}
