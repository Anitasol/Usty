package com.ru.usty.elevator;

public class Person implements Runnable {

	int srcFloor, dstFloor, elevator;
	
	public Person(int source, int destination) {
		this.srcFloor = source;
		this.dstFloor = destination;
		this.elevator = 0;
	}
	
	@Override
	public void run() {
		ElevatorScene.scene.incrementNumberOfPeopleWaitingAtFloor(srcFloor);
		try {		
			ElevatorScene.semaphoreIn[this.elevator][this.srcFloor].acquire();
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ElevatorScene.scene.incrementNumberOfPeopleInElev(this.elevator);
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(srcFloor);
		
		
		ElevatorScene.semaphoreOut[this.elevator][this.dstFloor].release();
		
		
		ElevatorScene.scene.personExitsAtFloor(dstFloor);
		ElevatorScene.scene.decrementNumberOfPeopleInElev(this.elevator);
		
		//Person is through barrier
		//ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(srcFloor);
		
		System.out.println("Person Thread released");
	}
	
}
