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
	public static Semaphore semaphore1;
	
	public static Semaphore personCountMutex;
	
	public static Semaphore elevatorWaitMutex;
	
	public static ElevatorScene scene;
	
	public static boolean elevatorsMayDie;
	
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
	public static Semaphore exitedCountMutex;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {
		
		elevatorsMayDie = true;
		
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
		
		elevatorsMayDie = false;
		
		scene = this;
		semaphore1 = new Semaphore(0);
		personCountMutex = new Semaphore(1);
		elevatorWaitMutex = new Semaphore(1);
		
		elevatorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					if(ElevatorScene.elevatorsMayDie) {
						return;
					}
					
					for(int i = 0; i < 16; i++) {
						ElevatorScene.semaphore1.release(); //signal
					}
				}
			}
			
		});
		elevatorThread.start();
		
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

		incrementNumberOfPeopleWaitingAtFloor(sourceFloor); 
			
		return thread;  //this means that the testSuite will not wait for the threads to finish
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		//dumb code, replace it!
		return 1;
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		
		//dumb code, replace it!
		switch(elevator) {
		case 1: return 1;
		case 2: return 4;
		default: return 3;
		}
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
