import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.subsumption.Behavior;

public class Going implements Behavior{
	private Navigator navigator = MainControl.navigator;
	private BufferedInputStream in;
	public Socket connection;
	private List<String> list;
	


	public Going(BufferedInputStream in, Socket connection, Navigator navigator){
		this.in = in;
		this.connection = connection;
		this.navigator = navigator;
	}
	
	@Override
	public boolean takeControl() {
		if(navigator.getMode().equals("finding") && connection != null){
			try {
				if (in.available() > 0) {
					return true;
				}
				return false;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	@Override
	public void action() {
		navigator.setMode("going");
		//GET QR
		String message = MainControl.getAndroidQRMessage();

		//x,y,heading format
		message = message.substring(4);
		String[] split = message.split(",");

		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		int heading = Integer.parseInt(split[2]);

		Pose pose = new Pose(x,y,heading);
		navigator.setStart(pose);

		//SELECTING DESTINATION using class SelectionMenu
		SelectionMenu selectionMenu = new SelectionMenu();
		Waypoint finish = selectionMenu.getSelection();
		navigator.setFinish(finish);
		navigator.findPath();
		navigator.setMode("moving");
	}

	@Override
	public void suppress() {
	}
	
	public List<String> getList() {
		return list;
	}
	
	public void setList(List<String> list) {
		this.list = list;
	}

}
