import lejos.hardware.Sound;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;


public class DistanceStop implements Behavior{
	private SampleProvider sp;
	private float[] samples = new float[1];
	private BaseRegulatedMotor mMovement;

	public DistanceStop(BaseRegulatedMotor mMovement, SampleProvider sp) {
		this.mMovement = mMovement;
		this.sp = sp;
	}

	public boolean takeControl() {
		sp.fetchSample(samples, 0);
		//If there is anything near, return true
		return(samples[0] < 0.1f);
	}
	
	public void action() {
		float[] samples = new float[1];
		sp.fetchSample(samples, 0);

		mMovement.stop();
		
		//To simulate a car horn:
		Sound.beep();
		mMovement.backward();
	}

	public void suppress() { 
	}


}
