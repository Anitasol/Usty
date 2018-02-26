package com.ru.usty.elevator;

public class Person implements Runnable {

	int srcFloor, dstFloor;
	public Person(int source, int destination) {
		this.srcFloor = source;
		this.dstFloor = destination;
	}
	
	@Override
	public void run() {
		try {
			
			ElevatorScene.elevatorWaitMutex.acquire();
				// inside critical state
				ElevatorScene.semaphore1.acquire(); //wait
			
			ElevatorScene.elevatorWaitMutex.release(); 
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Person is through barrier
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(srcFloor);
		
		System.out.println("Person Thread released");
	}
	
}
