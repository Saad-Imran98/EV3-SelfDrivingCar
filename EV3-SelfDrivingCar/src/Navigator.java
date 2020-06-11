import java.util.ArrayList;
import java.util.List;

import lejos.hardware.lcd.LCD;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
import lejos.robotics.pathfinding.Path;

public class Navigator {

	private String mode = "finding";
	private String destination;
	private String origin;
	private List<String> instructions;
	private ArrayList<Line> lines = new ArrayList<Line>();
	private Pose start;
	private Waypoint finish;

	public Waypoint getFinish() {
		return finish;
	}
	public void setFinish(Waypoint finish) {
		this.finish = finish;
	}
	public Navigator() {
		destination = "";
		origin = "";
		instructions = new ArrayList<String>();
		initializeLines();
	}
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public List<String> findPath() {
		try {
			Line[] avoidanceLines = new Line[20];

			avoidanceLines[0] = (new Line(0,1, 1,0));
			avoidanceLines[1] = (new Line(0,0, 1,1));

			avoidanceLines[2] = (new Line(0,1, 1,2));
			avoidanceLines[3] = (new Line(0,2, 1,1));

			avoidanceLines[4] = (new Line(0,2, 1,3));
			avoidanceLines[5] = (new Line(0,3, 1,2));

			avoidanceLines[6] = (new Line(1,3, 2,2));
			avoidanceLines[7] = (new Line(2,3, 1,2));

			avoidanceLines[8] = (new Line(1,0, 2,2));
			avoidanceLines[9] = (new Line(2,0, 1,2));

			avoidanceLines[10] = (new Line(2,0, 3,2));
			avoidanceLines[11] = (new Line(2,2, 3,0));

			avoidanceLines[12] = (new Line(2,3, 3,2));
			avoidanceLines[13] = (new Line(2,2, 3,3));

			avoidanceLines[14] = (new Line(3,2, 4,3));
			avoidanceLines[15] = (new Line(3,3, 4,2));

			avoidanceLines[16] = (new Line(3,2, 4,1));
			avoidanceLines[17] = (new Line(3,1, 4,2));

			avoidanceLines[18] = (new Line(3,1, 4,0));
			avoidanceLines[19] = (new Line(3,0, 4,1));






			Rectangle rectangle = new Rectangle(0,0,4,3);

			Line[] ln = new Line[lines.size()];
			lines.toArray(ln);
			LineMap linemap2 = new LineMap(avoidanceLines, rectangle);


			LineMap linemap = new LineMap(ln,rectangle);
			DijkstraPathFinder dj =  new DijkstraPathFinder(linemap);
			dj.setMap(lines);
			Path path = new Path();
			LCD.clear();
			
			path = dj.findRoute(start, finish, linemap2);
			float heading = start.getHeading();
			for(int i=0; i<path.size()-1; i++) {
				//IF heading 0 positive x axis
				if(heading == 0) {
					if(path.get(i).getY()==(path.get(i+1).getY())) {
						//go straight
						instructions.add("straight");
					} else if(path.get(i).getY()>path.get(i+1).getY()) {
						//turn right
						instructions.add("right");
						heading = 270;
					} else if(path.get(i).getY()<path.get(i+1).getY()) {
						instructions.add("left");
						//turn left
						heading = 90;
					}
				} 

				//IF heading 180 negative x axis
				if(heading == 180) {
					if(path.get(i).getY()==(path.get(i+1).getY())) {
						instructions.add("straight");
						//go straight
					} else if(path.get(i).getY()>path.get(i+1).getY()) {
						//turn left
						heading = 270;
						instructions.add("left");
					} else if(path.get(i).getY()<path.get(i+1).getY()) {
						//turn right
						heading = 90;
						instructions.add("right");
					}
				}

				//IF heading 90 positive y axis
				if(heading == 90) {
					if(path.get(i).getX()==(path.get(i+1).getX())) {
						instructions.add("straight");
						//go straight
					} else if(path.get(i).getX()>path.get(i+1).getX()) {
						//turn left
						heading = 180;
						instructions.add("left");
					} else if(path.get(i).getX()<path.get(i+1).getX()) {
						//turn right
						heading = 0;
						instructions.add("right");
					}
				} 

				//IF heading 270 negative y axis
				if(heading == 270) {
					if(path.get(i).getX()==(path.get(i+1).getX())) {
						//go straight
						instructions.add("straight");
					} else if(path.get(i).getX()>path.get(i+1).getX()) {
						//turn right
						instructions.add("right");
						heading = 180;
					} else if(path.get(i).getX()<path.get(i+1).getX()) {
						instructions.add("left");
						heading = 0;
						//turn left
					}
				}
			}






		} catch (DestinationUnreachableException e) {
			e.printStackTrace();
		}
		instructions.remove(0);

		return instructions;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public void initializeLines() {




		lines.add(new Line(0,0, 0,1));
		lines.add(new Line(0,1, 0,2));
		lines.add(new Line(0,2, 0,3));
		lines.add(new Line(1,0, 1,1));
		lines.add(new Line(1,1, 1,2));
		lines.add(new Line(1,2, 1,3));
		lines.add(new Line(2,0, 2,2));
		lines.add(new Line(2,2, 2,3));
		lines.add(new Line(3,0, 3,1));
		lines.add(new Line(3,1, 3,2));
		lines.add(new Line(3,2, 3,3));
		lines.add(new Line(4,0, 4,1));
		lines.add(new Line(4,1, 4,2));
		lines.add(new Line(4,2, 4,3));
		lines.add(new Line(0,0, 1,0));
		lines.add(new Line(0,1, 1,1));
		lines.add(new Line(0,2, 1,2));
		lines.add(new Line(0,3, 1,3));
		lines.add(new Line(1,0, 2,0));
		lines.add(new Line(1,2, 2,2));
		lines.add(new Line(1,3, 2,3));
		lines.add(new Line(2,0, 3,0));
		lines.add(new Line(2,2, 3,2));
		lines.add(new Line(2,3, 3,3));
		lines.add(new Line(3,0, 4,0));
		lines.add(new Line(3,1, 4,1));
		lines.add(new Line(3,2, 4,2));
		lines.add(new Line(3,3, 4,3));


	}
	public Pose getStart() {
		return start;
	}
	public void setStart(Pose start) {
		this.start = start;
	}

	public void setList(List<String> instructions) {
		this.instructions = instructions;
	}

	public List<String> getList() {
		return instructions;
	}







}
