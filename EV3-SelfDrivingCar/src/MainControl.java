import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class MainControl {

	//SPEED/TURN VALUES
	final static int FORWARDS_SPEED = 50;
	final static int TURN_LEFT = 20;
	final static int TURN_RIGHT = -20;

	//SAMPLES
	final static int NUMBER_OF_SAMPLES = 2;
	final static int DELAY_OF_SAMPLES_MS = 1000;

	//CLASS/VERSION
	final static String CLASS_NAME = "Main";
	final static double VERSION_NUMBER = 2.0;

	//ANDROID
	protected static String IPaddress = "10.0.1.3";
	protected static int port = 1234;
	protected static Socket connection = new Socket();
	protected static DataInputStream dis;
	protected static DataOutputStream dos;
	protected static int MAX_READ = 30;
	protected static BufferedInputStream in = null;
	protected static OutputStream out = null;
	protected static byte[] buffer;
	protected static Navigator navigator = new Navigator();

	//MOTORS
	public static BaseRegulatedMotor mSteering;
	public static BaseRegulatedMotor mMovement;	



	public static void main(String[] args) throws InterruptedException {
		//Version number and authors
		LCD.drawString("Class: " + CLASS_NAME, 0, 0);
		LCD.drawString("Version: " + VERSION_NUMBER, 0, 1);
		LCD.drawString("Authors: ", 0, 3);
		LCD.drawString("Leo Codron ", 0, 4);
		LCD.drawString("Faik Shah", 0, 5);
		LCD.drawString("Saad Imran", 0, 6);
		LCD.drawString("Riley Emilova", 0, 7);
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
		
		
		//Initialise motors
		BaseRegulatedMotor mSteering = new EV3LargeRegulatedMotor (MotorPort.A);
		BaseRegulatedMotor mMovement = new EV3LargeRegulatedMotor (MotorPort.B);

		//Initialise sensors
		EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S3);
		EV3UltrasonicSensor us = new EV3UltrasonicSensor(SensorPort.S4);

		//Connect to android
		connectAndroid();

		//Calibrate steering
		calibrateSteering calibrate = new calibrateSteering(mSteering);
		calibrate.calibrate();


		//Sample provider for ultrasonic sensor
		SampleProvider sp = us.getDistanceMode();


		double averageLightLevel;
		//Usual average light level for quick tests:
		//averageLightLevel = 0.3549999;

		//Calibrate the light level for black and 
		averageLightLevel = calibrateLightLevel(lightSensor);

		//Values for PID controller
		double errorLast = 0;
		double totalError = 0;

		//Set speed for movement motor
		mMovement.setSpeed(100);
		
		//Behaviour for the controller: the main driver class
		Behavior Controller = new PIDController(mSteering, mMovement, lightSensor, averageLightLevel, 
				navigator.getMode(), errorLast, totalError);

		//Behaviour to check if anything is in front of the ultrasonic sensor. If so, stop
		Behavior DistanceStop = new DistanceStop(mMovement, sp);

		
		//Behaviour for Going: the class that checks for the first QR code
		Behavior Going = new Going(in, connection, navigator);
	
		//Behaviour for Moving: the class that deals with QR codes after the first initial one
		Behavior Moving = new Moving(mMovement, mSteering, navigator.getMode(), navigator, in, connection);

		LCD.clear();
		//Creates the arbitrator with all the behaviours we need
		Arbitrator ab = new Arbitrator(new Behavior[] {Controller, DistanceStop, Going, Moving});
		
		//Manually clear the annoying message the arbitrator creates when being created:
		System.out.println("                          ");
		
		//Starts the arbitrator
		ab.go(); 
		us.close();
	}

	public static void connectAndroid() {
		buffer = new byte[MAX_READ];
		SocketAddress sa = new InetSocketAddress(IPaddress, port);
		try {
			connection.connect(sa, 1500); // Timeout possible
		} catch (Exception ex) {
			// This connection fail is just ignored - we were probably not trying to connect because there was no
			// Android device
			// Could be Timeout or just a normal IO exception
			LCD.drawString(ex.getMessage(), 0,6);
			connection = null;
		}
		if (connection != null) {
			try {
				in = new BufferedInputStream( connection.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				out = connection.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LCD.drawString("Connected", 0, 5);
		}
	}
	/**
	 *
	 * @return reads input stream binded to connection and returns message as a string
	 * @see reads buffer till 
	 * 
	 */
	public static String getAndroidQRMessage() {
		String message="";
		
		try {
			LCD.clear();
			int read = in.read(buffer, 0, MAX_READ);
			for (int index= 0 ; index < read ; index++) {						
				message = message + (char)buffer[index];
			}

			out.write("Reply:".getBytes(), 0, 6);
			out.write(buffer, 0, read);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	public static double calibrateLightLevel(EV3ColorSensor lightSensor) {
		double averageLightLevel;
		float [] level = new float [1];
		SampleProvider light = lightSensor.getRedMode();	
		int i = 0;
		float printout;
		double maxLightLevel;
		double minLightLevel;
		light.fetchSample(level, 0);
		minLightLevel = level[0];
		LCD.drawString("Black area reading",0,0);
		Button.ENTER.waitForPressAndRelease();
		while(i < NUMBER_OF_SAMPLES) {
			Delay.msDelay(DELAY_OF_SAMPLES_MS);
			light.fetchSample(level, 0);

			printout = level[0];
			if(printout < minLightLevel) {
				minLightLevel = printout;
			}
			LCD.clear();
			LCD.drawString("min: " + minLightLevel + "     ",0,1);
			LCD.drawString("Current: " + printout + "     ", 0, 4);
			LCD.drawInt((i+1), 5, 5);
			i++;
		}

		maxLightLevel = level[0];
		i = 0;
		LCD.drawString("White area reading",0,0);
		Button.ENTER.waitForPressAndRelease();
		while (i < NUMBER_OF_SAMPLES) {
			Delay.msDelay(DELAY_OF_SAMPLES_MS);
			light.fetchSample(level, 0);
			printout = level[0];
			if(printout > maxLightLevel) {
				maxLightLevel = printout;
			}
			LCD.drawString("max: " + maxLightLevel + "     ",0, 2);
			LCD.drawString("Current: " + printout + "     ", 0, 4);
			LCD.drawInt((i+1), 5, 5);
			i++;
		}
		averageLightLevel = ((maxLightLevel+minLightLevel)/2);
		return averageLightLevel;
	}


}
