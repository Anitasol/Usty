package com.ru.usty.elevator;

public class Elevator implements Runnable {

	public static int num,cap,currFloor;
	public boolean goingUp = true; 
	
	public Elevator(int elNumber) {
		num = elNumber;
		cap = 6;
		currFloor = 0;
	}
	
	@Override
	public void run() {
		while(true) {
			if(ElevatorScene.elevatorsMayDie) {
				return;
			}
			getPeopleInElevator();
			moveElevator();
			getPeopleOutElevator();
			
		}
	}
	
	public void getPeopleInElevator() {
		if(ElevatorScene.scene.getCurrentFloorForElevator(num) > 0 ){currFloor--;}
		else {
			int freeSpace = (cap - ElevatorScene.scene.getNumberOfPeopleInElevator(num));
			
			for(int i = 0; i < freeSpace; i++) {
				ElevatorScene.semaphoreIn[num][ElevatorScene.countingFloors].release(); 
				ElevatorScene.scene.incrementNumberOfPeopleInElev(num);
				
				System.out.println("people in " + num + "" + " " + ElevatorScene.scene.getNumberOfPeopleInElevator(num));	
			}
			stopElevator();
			
			freeSpace = (cap - ElevatorScene.scene.getNumberOfPeopleInElevator(num));
			for(int i = 0; i < freeSpace; i++) {
				try {
					ElevatorScene.elevatorCountMutex.acquire();
					ElevatorScene.scene.whichElevator = num;
					ElevatorScene.semaphoreIn[num][ElevatorScene.countingFloors].acquire();
					ElevatorScene.scene.decrementNumberOfPeopleInElev(num);
				}	
				catch (InterruptedException e) {
					e.printStackTrace();
				} 
				ElevatorScene.elevatorCountMutex.release();
			}
		}
	}
	
	public void getPeopleOutElevator() {
		// number of people that are going out
		int peopleInElevator = (ElevatorScene.scene.getNumberOfPeopleInElevator(num));
		// releasing people out to their destination floor
		System.out.println("people out" + " " + ElevatorScene.scene.getNumberOfPeopleInElevator(num));
		for(int i = 0; i < peopleInElevator; i++) {
			ElevatorScene.semaphoreOut[num][ElevatorScene.countingFloors].release();
			ElevatorScene.scene.decrementNumberOfPeopleInElev(num);
			System.out.println("people gone" + " " + num);
			System.out.println("Elevator thread released");
		}
		
		stopElevator();
		
		peopleInElevator = (ElevatorScene.scene.getNumberOfPeopleInElevator(num));
		
		for(int i = 0; i < peopleInElevator; i++) {
			try {
				ElevatorScene.semaphoreOut[num][ElevatorScene.countingFloors].acquire();
				ElevatorScene.scene.incrementNumberOfPeopleInElev(num);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		stopElevator();
	}

	public void moveElevator() {
		System.out.println("Moving Elevator...");
		if(ElevatorScene.scene.getCurrentFloorForElevator(num) == 0) {
			ElevatorScene.scene.getCurrentFloorForElevator(ElevatorScene.countingFloors++);
			System.out.println("current floor: " + ElevatorScene.countingFloors);
		}
		else {
			ElevatorScene.scene.getCurrentFloorForElevator(ElevatorScene.countingFloors--);
			System.out.println("current floor: " + ElevatorScene.countingFloors);
		}
		stopElevator();
	}
	
	public void stopElevator() {
		System.out.println("Elevator wait");
		try {
			Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}