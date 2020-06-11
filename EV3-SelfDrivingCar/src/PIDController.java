import lejos.hardware.motor.BaseRegulatedMotor;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;


public class PIDController implements Behavior{
	private BaseRegulatedMotor mSteering;
	private BaseRegulatedMotor mMovement;
	private EV3ColorSensor lightSensor;
	private double averageLightLevel;
	private String mode;
	private double errorLast;
	private double totalError;
	
	public PIDController(BaseRegulatedMotor mSteering, BaseRegulatedMotor mMovement, EV3ColorSensor lightSensor, 
			double averageLightLevel, String mode, double errorLast, double totalError) {
		this.mSteering = mSteering;
		this.mMovement = mMovement;
		this.lightSensor = lightSensor;
		this.averageLightLevel = averageLightLevel;
		this.mode = mode;
		this.errorLast = errorLast;
		this.totalError = totalError;
	}


		@Override
		public boolean takeControl() {
			//Flag for if it has arrived:
			return mode!="arrived";
		}

		@Override
		public void action() {

			float [] lightLevel = new float [1];
			SampleProvider light = lightSensor.getRedMode();
			light.fetchSample(lightLevel, 0);

			float current = lightLevel[0];

			double output;
			double error;
			
			double kp = 2.29;
			double ki = 1.1; 
			double kd = 100;
			
			mMovement.backward();
			light.fetchSample(lightLevel, 0);
			current = lightLevel[0];
			error = current - averageLightLevel;
			output = (kp * error) + kd * (error - errorLast) + (ki * totalError);
			mSteering.rotate((int) output);
			errorLast = error;
			totalError += error;

		}

		@Override
		public void suppress() {
		}

	}