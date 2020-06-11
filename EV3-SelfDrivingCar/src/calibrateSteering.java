import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;

public class calibrateSteering {

	private static int tachohalf;
	private BaseRegulatedMotor mSteering;

	public calibrateSteering(BaseRegulatedMotor mSteering) {
		this.mSteering = mSteering;
	}
	
	
	public int getTachoHalf() {
		return tachohalf;
	}
	
	public void calibrate() {
		mSteering.setStallThreshold(100, 100);
		mSteering.forward();
		while(!mSteering.isStalled()) {
		}
		mSteering.resetTachoCount();

		mSteering.backward();
		while(!mSteering.isStalled()) {		}
		int tacho = mSteering.getTachoCount();
		tachohalf = tacho/2;
		mSteering.rotate(-tachohalf);
		if (tachohalf<0) {
			tachohalf = tachohalf*-1;
		}
	}
	


}
