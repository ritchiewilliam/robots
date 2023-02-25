/**
 * This program is a game where the goal is to clear all robots that are chasing you
 * 
 * Author: William
 * Date Created: May 28, 2020
 * Last Modified: June 15, 2020
 * 
 */

package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JFrame;

import java.io.*;

public class Chase {

	static final int EMPTY = 0; // empty spot on grid
	static final int ROBOT = 1; // robot on grid
	static final int COLLISION = 10; // collision on grid
	static final int PLAYER = 100; // player on grid

	static final int SIZE = 21;// size of grid

	private int grid[][] = null;

	private ArrayList<int[]> robotPositions = new ArrayList<int[]>(); // position of the robots
	private ArrayList<Boolean> deadRobots = new ArrayList<Boolean>(); // list of robots, if true they are dead

	private int[] playerPosition = new int[2]; // position of the player
	private boolean death = false; // when player is dead will be true
	private boolean win = false; // when player clears all robots will be true

	private int score = 0; // score player gets for killing robots

	/**
	 * Used to pass grid over to GUI to paint icons.
	 * 
	 * @return the playing grid
	 */
	public int[][] getGrid() {
		return grid;
	}// end getGrid

	/**
	 * Used to pass player position to GUI to paint dead icon when died
	 * 
	 * @return the player position
	 */
	public int[] getPlayerPosition() {
		return playerPosition;
	}// end getPlayerPosition

	/**
	 * Used to pass to GUI to determine if dead
	 * 
	 * @return death
	 */
	public boolean isDeath() {
		return death;
	}// end isDeath

	/**
	 * Used to pass to GUI to repaint screen with robots when level is beat
	 * 
	 * @return win
	 */
	public boolean isWin() {
		return win;
	}// end isWin

	/**
	 * Used to set win in GUI
	 * 
	 * @return win
	 */
	public void setWin(boolean win) {
		this.win = win;
	}// end setWin

	public static void main(String[] args) {

		Chase chase = new Chase();
		GUI frame = new GUI(chase);// new Frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 720);
		frame.setVisible(true);
	}// end main

	/**
	 * Gets two random numbers to use as new position for player
	 */
	public void teleport() {

		grid[playerPosition[0]][playerPosition[1]] = 0;

		playerPosition[0] = (int) (SIZE * Math.random());
		playerPosition[1] = (int) (SIZE * Math.random());

		grid[playerPosition[0]][playerPosition[1]] += PLAYER;

	}// end teleport

	/**
	 * Determines which way to move based off of where user clicks.
	 * 
	 * @param x the x value of where the user clicked
	 * @param y the y value of where the user clicked
	 */
	public void playerMove(int x, int y) {

		double angle = 0;
		int adjacent = x - playerPosition[0];
		int opposite = playerPosition[1] - y;
		double ratio = (double) (opposite) / adjacent;

		int move_x = 0;// amount moved in x direction
		int move_y = 0;// amount moved in y direction

		grid[playerPosition[0]][playerPosition[1]] = EMPTY;// set original position of player to empty

		if (adjacent == 0 && opposite == 0) {

		}
		// quadrant 1 (top right)
		else if (adjacent >= 0 && opposite >= 0) {
			if (adjacent > 0) {
				angle = (Math.atan(ratio)) * 180 / Math.PI;
			} else {
				angle = 90;
			}

			// vertical if angle close to 90
			if (angle >= 67.5) {
				move_y = -1;
			}

			// go diagonal if angle close to 45
			else if (angle < 67.5 && angle >= 22.5) {
				move_x = 1;
				move_y = -1;
			}
			// go horizontal if angle close to 0
			else if (angle < 22.5) {
				move_x = 1;
			}
		}

		// quadrant 2 (top left)
		else if (adjacent < 0 && opposite > 0) {
			if (adjacent < 0) {
				angle = (Math.atan(Math.abs(ratio))) * 180 / Math.PI;
			} else {
				angle = 90;
			}

			// vertical if angle close to 90
			if (angle >= 67.5) {
				move_y = -1;
			}

			// go diagonal if angle close to 45
			else if (angle < 67.5 && angle >= 22.5) {
				move_x = -1;
				move_y = -1;
			}
			// go horizontal if angle close to 0
			else if (angle < 22.5) {
				move_x = -1;
			}
		}

		// quadrant 3 (bottom left)
		else if (adjacent <= 0 && opposite <= 0) {
			if (adjacent < 0) {
				angle = (Math.atan(ratio)) * 180 / Math.PI;
			} else {
				angle = 90;
			}
			// vertical if angle close to 90
			if (angle >= 67.5) {
				move_y = 1;
			}

			// go diagonal if angle close to 45
			else if (angle < 67.5 && angle >= 22.5) {
				move_x = -1;
				move_y = 1;

			}
			// horizontal if angle close to 0
			else if (angle < 22.5) {
				move_x = -1;
			}
		}
		// quadrant 4 (bottom right)
		else if (adjacent > 0 && opposite < 0) {

			angle = (Math.atan(Math.abs(ratio))) * 180 / Math.PI;
			// vertical if angle close to 90
			if (angle >= 67.5) {
				move_y = 1;
			}
			// go diagonal if angle close to 45
			else if (angle < 67.5 && angle >= 22.5) {
				move_x = 1;
				move_y = 1;
			}
			// horizontal if angle close to 0
			else if (angle < 22.5) {
				move_x = 1;
			}

		}
		// add movement to position of player
		playerPosition[0] += move_x;
		playerPosition[1] += move_y;

		// put player back on grid
		grid[playerPosition[0]][playerPosition[1]] += PLAYER;

	}// end playerMove

	/**
	 * Used to reset variables and start of a new game
	 */
	public void start() {
		robotPositions.clear();
		deadRobots.clear();
		death = false;

	}// end start

	/**
	 * Checks if player has won
	 */
	public void checkWin() {
		int robotDeaths = 0;
		// counts robots that have died
		for (int i = 0; i < deadRobots.size(); i++) {
			if (deadRobots.get(i)) {
				robotDeaths++;
			}
		}
		// if all the robots have died the player has won
		if (robotDeaths == deadRobots.size()) {
			win = true;
		}
		// System.out.println(deadRobots.size() + " " + robotDeaths);
	}// end checkWin

	/**
	 * Generates the robots and starts player in middle of the grid
	 */
	public void generateGrid(int lvl) {

		if (lvl == 1) {
			score = 0;
		}

		int robots = (int) (3 * (Math.random()) + (4 * lvl)); // amount of robots

		grid = new int[SIZE][SIZE];

		int centerPosn = SIZE / 2;

		for (int i = 0; i < robots; i++) {// loops through give robots random positions
			int[] positions = { (int) (SIZE * Math.random()), (int) (SIZE * Math.random()) };
			int x = positions[0];
			int y = positions[1];

			if (grid[x][y] == ROBOT) {// if the robot generated is on a spot with a robot loop once more to find
										// another spot
				i--;
			} else if (x == centerPosn && y == centerPosn) {// if a robot is generated on the players position loop
															// through again to find another spot
				i--;

			} else {// if everything is find
				robotPositions.add(positions); // add it to the arraylist managing the positions of the robots
				deadRobots.add(false);// set all of the robots to not dead
				grid[x][y] = ROBOT; // put the robots on the grid
			}
		}

		grid[centerPosn][centerPosn] = PLAYER;// put the player on the grid

		playerPosition[0] = centerPosn;// put the initial player position in a
		playerPosition[1] = centerPosn;

	}// end generateGrid

	/**
	 * Moves the robots based on where the player is
	 */
	public void robotMove() {

		for (int i = 0; i < robotPositions.size(); i++) {// loops through all the positions of robots
			int x = robotPositions.get(i)[0];
			int y = robotPositions.get(i)[1];

			if (!deadRobots.get(i)) {// if you aren't dead

				grid[x][y] -= ROBOT;// take away you previous position

				// up left
				if (x > playerPosition[0] && y > playerPosition[1]) {
					x--;
					y--;
					robotPositions.set(i, new int[] { x, y });
				}

				// up
				else if (x == playerPosition[0] && y > playerPosition[1]) {
					y--;
					robotPositions.set(i, new int[] { x, y });
				}
				// up right
				else if (x < playerPosition[0] && y > playerPosition[1]) {
					x++;
					y--;
					robotPositions.set(i, new int[] { x, y });
				}

				// right
				else if (x < playerPosition[0] && y == playerPosition[1]) {
					x++;
					robotPositions.set(i, new int[] { x, y });
				}

				// down right
				else if (x < playerPosition[0] && y < playerPosition[1]) {
					x++;
					y++;
					robotPositions.set(i, new int[] { x, y });
				}

				// down
				else if (x == playerPosition[0] && y < playerPosition[1]) {
					y++;
					robotPositions.set(i, new int[] { x, y });
				}

				// down left
				else if (x > playerPosition[0] && y < playerPosition[1]) {
					x--;
					y++;
					robotPositions.set(i, new int[] { x, y });
				}

				// left
				else if (x > playerPosition[0] && y == playerPosition[1]) {
					x--;
					robotPositions.set(i, new int[] { x, y });
				}

				grid[x][y] += ROBOT;// add the new position to the grid
			}
		}
	}// end robotMove

	/**
	 * Detects if there is a collision
	 */
	public void detectCollisions() {

		int x;
		int y;

		int xRobot;
		int yRobot;

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				// look for robot + robot or robot + debris collisions
				if (grid[i][j] == COLLISION) {// if its a collision do nothing

				} else if (grid[i][j] > ROBOT && grid[i][j] < PLAYER) {// if its not a robot, or player set it as a
																		// collision
					grid[i][j] = COLLISION;// set position to collision
					x = i;
					y = j;

					for (int k = 0; k < deadRobots.size(); k++) {// if your a robot in the position than set to dead
						xRobot = robotPositions.get(k)[0];
						yRobot = robotPositions.get(k)[1];
						if (x == xRobot && y == yRobot) {
							deadRobots.set(k, true);

						}
					}
				} else if (grid[i][j] > PLAYER) {// if there is a number in grid greater than player set the player dead
					death = true;
				}
			}
		}

	}// end detectCollisions

	/**
	 * prints grid for debug
	 */
	public void printGrid() {
		System.out.println();
		System.out.println();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				System.out.print(grid[i][j] + "  ");
			}
			System.out.println();
		}
	}// end printGrid

	/**
	 * updates score once level is complete
	 */
	public void updateScore() {
		score = calculateScore();
	}// end updateScore

	/**
	 * calculates score
	 * 
	 * @return returns robots killed from previous lvl + current amount killed
	 */
	public int calculateScore() {
		int deadCount = 0;
		for (int i = 0; i < deadRobots.size(); i++) {// find how many robots killed
			if (deadRobots.get(i)) {
				deadCount++;
			}
		}
		return score + deadCount;
	}// end calculateScore

	/**
	 * Reads scores from an existing highscore file
	 * 
	 * @param fileName the file path
	 * @return a hashmap with scores of players
	 */
	public HashMap<Integer, ArrayList<String>> readHighScoresFromFile(File fileName) throws Exception {
		if (fileName.exists()) {
		FileReader inputFile = new FileReader(fileName);
		BufferedReader br = new BufferedReader(inputFile);

		HashMap<Integer, ArrayList<String>> scoreMap = new HashMap<Integer, ArrayList<String>>();

		boolean endOfTxt = false;
		String input = "";
		String[] seperate;
		String allNames;
		String[] nameList;

		while (!endOfTxt) {

			input = br.readLine();

			if (input != null) {
				if (input.contains(":")) {

					ArrayList<String> names = new ArrayList<String>();
					seperate = input.split(": ");// split the input between the score and the names
					allNames = seperate[1].replace("[", "");// remove both the brackets created when its printed
					allNames = allNames.replace("]", "");
					nameList = allNames.split(", ");// split up each name

					for (int i = 0; i < nameList.length; i++) {
						names.add(nameList[i]);// put each name into an arraylist
					}
					scoreMap.put(Integer.parseInt(seperate[0]), names);// put the score and arraylist of names into
																		// hashmap
				}

			} else
				endOfTxt = true;
		}

		br.close();
		return scoreMap;
		}
		else {
			return null;
		}
	}// end readHighScoresFromFile

	/**
	 * Writes the highscore file
	 */
	public void writeHighScoresToFile(File fileName, String name) throws Exception {

		ArrayList<Integer> scores = new ArrayList<Integer>();
		HashMap<Integer, ArrayList<String>> scoreMap;
		ArrayList<String> names = new ArrayList<String>();
		names.add(name);

		// if a file already exists
		if (fileName.exists()) {
			scoreMap = readHighScoresFromFile(fileName);// get existing scores
			for (int key : scoreMap.keySet()) {
				if (key == score) {
					scoreMap.get(key).add(name);// if score achieved already in txt file place it in arraylist
				}
				scores.add(key);// add score to arraylist that will sort scores lowest to highest
			}

			if (scoreMap.get(score) == null) {// if the score doesn't exist

				scoreMap.put(score, names);// add score achieved along with name to hashmap
				scores.add(score);// add score to sorting arraylist
			}
			Collections.sort(scores);// sorts the scorts lowest to highest

		} else {// if the file doesn't exist
			scoreMap = new HashMap<Integer, ArrayList<String>>();

			scoreMap.put(score, names);
			scores.add(score);
		}

		PrintWriter outputFile = new PrintWriter(fileName);

		// prints out the scores from greatest to least
		outputFile.println("HIGH SCORES\n");
		for (int i = scoreMap.size() - 1; i > -1; i--) {
			outputFile.println(scores.get(i) + ": " + scoreMap.get(scores.get(i)));
		}

		outputFile.close();

	}// end writeHighScoresToFile


}// end Chase