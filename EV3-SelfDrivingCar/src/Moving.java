import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Moving implements Behavior{
	private Navigator navigator;
	private BufferedInputStream in;
	public Socket connection;
	private BaseRegulatedMotor mSteering;
	private BaseRegulatedMotor mMovement;
	private calibrateSteering calibrate;
	private List<String> list;
	private String message = "";
	private List<String> listOfQRsReadAlready = new ArrayList<String>();

	
	public Moving(BaseRegulatedMotor mMovement, BaseRegulatedMotor mSteering, 
			String mode, Navigator navigator,  BufferedInputStream in, Socket connection) {
		this.mMovement = mMovement;
		this.mSteering = mSteering;
		this.navigator = navigator;
		this.connection = connection;
		this.in = in;
	}
	
	@Override
	public boolean takeControl() {

		try {
			return navigator.getMode() == "moving" && connection != null && in.available() > 0;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public void action() {

		message = MainControl.getAndroidQRMessage();
		//2,3,1 format
		message = message.substring(4);
		String[] split = message.split(",");

		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		int heading = Integer.parseInt(split[2]);

		
		String QRcode = "" + x + "," +  y + ","+ heading;
		if (listOfQRsReadAlready.contains(QRcode)) {
			return;
		}
		listOfQRsReadAlready.add(QRcode);

		
		list = navigator.getList();
		if (!list.isEmpty()) {
			if (list.get(0).equals("left")) {
				turnLeft();
				list.remove(0);
				navigator.setList(list);
			}
			else if (list.get(0).equals("right")) {
				turnRight();
				list.remove(0);
				navigator.setList(list);
			}
			else if (list.get(0).equals("straight")) {
				goStraight();
				list.remove(0);
				navigator.setList(list);
			}
		}
		else {
			navigator.setMode("arrived");
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
	}	
	
	public void turnLeft() {
		LCD.clear();
		LCD.drawString("LEFT TURN", 3, 3);
		mSteering.setStallThreshold(100, 100);
		mSteering.forward();
		while(!mSteering.isStalled()) {
		}
		mMovement.backward();
		Delay.msDelay(6500);
		mSteering.rotate(-calibrate.getTachoHalf());


	}
	public void turnRight() {
		LCD.clear();
		LCD.drawString("RIGHT TURN", 3, 3);
		mSteering.setStallThreshold(100, 100);
		mSteering.backward();
		while(!mSteering.isStalled()) {
		}
		mMovement.backward();
		Delay.msDelay(6500);
		mSteering.rotate(calibrate.getTachoHalf());
	}
	public void goStraight() {
		mMovement.backward();
		Delay.msDelay(2500);
	}
}
