import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.navigation.Waypoint;
import lejos.utility.Delay;

public class SelectionMenu {

	private static Waypoint Prison = new Waypoint(3, (float) 2);
	private static Waypoint Library = new Waypoint((float) 0, 0.5);
	private static Waypoint Gym = new Waypoint(4, 3);
	private static Waypoint School = new Waypoint(2.5, (float) 0);
	private static Waypoint Pool = new Waypoint(4, (float) 1.5);

	public static void generateOptions() {
		LCD.clear();
		LCD.drawString("Where would you like to go?", 0, 0);
		LCD.drawString("Prison", 2, 2); 
		LCD.drawString("Library", 2, 3);
		LCD.drawString("Gym", 2, 4);
		LCD.drawString("School", 2, 5);
		LCD.drawString("Pool", 2, 6);
	}

	public Waypoint getSelection() {
		Sound.beep();
		generateOptions();
		LCD.drawString(">", 0, 2);
		LCD.drawString("<", 9, 2);

		int downCount = 0;

		while (!Button.ENTER.isDown() && (downCount < 5) && (downCount >= 0)) {
			if (Button.DOWN.isDown()) {
				downCount++;
				generateOptions();
				LCD.drawString(">", 0, 2+downCount);
				LCD.drawString("<", 9, 2+downCount);
				Delay.msDelay(500);
			}
			if (Button.UP.isDown() && (downCount < 5) && (downCount >= 0)) {
				downCount--;
				generateOptions();
				LCD.drawString(">", 0, 2+downCount);
				LCD.drawString("<", 9, 2+downCount);
				Delay.msDelay(500);

			}
		}

		LCD.clear();
		LCD.drawString("Selected:", 0, 0);

		if (downCount == 0) {
			LCD.drawString("Prison", 2, 2);
			Delay.msDelay(2000);
			return Prison;
		}
		if (downCount == 1) {
			LCD.drawString("Library", 2, 2);
			Delay.msDelay(2000);
			return Library;
		}
		if (downCount == 2) {
			LCD.drawString("Gym", 2, 2);
			Delay.msDelay(2000);
			return Gym;
		}
		if (downCount == 3) {
			LCD.drawString("School", 2, 2);
			Delay.msDelay(2000);
			return School;
		}
		if (downCount == 4) {
			LCD.drawString("Pool", 2, 2);
			Delay.msDelay(2000);
			return Pool;
		}

		return null;

	}
}
