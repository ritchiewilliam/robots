package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GUI extends JFrame {

	// All in game
	private JPanel playingArea; // the grid where you actually play
	private JButton[][] buttons; // an array of buttons that goes in the playing
									// area
	private JButton teleport; // button that lets you teleport

	ClassLoader url;// to load images in jar file
	private Icon robot; // icon of robot to put on buttons
	private Icon player;// icon of player to put on buttons
	private Icon explosion;// icon of explosion to put on buttons
	private Icon dead;

	private JPanel tp; // panel that holds tp button
	private JPanel menu; // panel that holds information
	private JLabel info;// info that goes into menu panel

	// All in start page
	private JPanel top = new JPanel();
	private JLabel title = null;
	private JPanel middle = new JPanel();
	private JPanel bottom = new JPanel();

	private JTextArea rules;// explains rules

	private JTextArea highScores; // display high scores

	private JButton start;// button to start game
	private JButton exit;// button to close game

	private JOptionPane deathWindow; // popup window after you die

	// For choosing file for highscores
	private JFrame file;
	File fileName;
	private JFileChooser saveFile;
	private String name;

	// determines what to do with button clicks and other events
	private ButtonGridHandler buttonGridHandler = new ButtonGridHandler();

	// Game Data
	private int teleports = 0;
	private int lvl = 0;

	private Chase chase = null;

	/**
	 * The GUI
	 */
	public GUI(Chase chase) {
		super("Robot Game");// title
		this.chase = chase;// main

		setLayout(new BorderLayout());

		title = new JLabel("Robots");// titles of game
		title.setFont(new Font("Impact", Font.PLAIN, 35));
		top.add(title);

		// txt box telling you rules
		rules = new JTextArea(
				"\nHow to Play:"
				+ "\n\nDon't let the robots get you. To move, click on any of the buttons on the grid. Everytime you move, the\nrobots move towards you. To get rid of them you can make them crash into each other, or a previous\ncrash. If you need to you can teleport, but be careful using them as you have limited teleports and they\nmay land you on a robot. Once all the robots are gone, tap any button to move to the next level."
				+ "\n\nRules:"
				+ "\nIf a robot catches you, or you step on a junk pile, you die."
				+ "\nIf a robot runs into another robot, they die and turn into a junk pile."
				+ "\nIf a robot runs into a junk pile, it dies."
				+ "\n\nControls: "
				+ "\nTo move, click on one of the buttons in the general direction you want to move."
				+ "\nTeleport with the teleport button at the bottom of the screen.\n\n");
		rules.setEditable(false); // make sure you can't edit when running

		highScores = new JTextArea();
		highScores.setRows(7);
		highScores.setColumns(54);
		highScores.setEditable(false);

		// set the icons that will be on the buttons

		// robot = new ImageIcon(".//res//robot.png");
		// player = new ImageIcon(".//res//smile.png");
		// explosion = new ImageIcon(".//res//explosion.png");
		robot = new ImageIcon(getClass().getClassLoader().getResource(
				"robot.png"));
		player = new ImageIcon(getClass().getClassLoader().getResource(
				"smile.png"));
		explosion = new ImageIcon(getClass().getClassLoader().getResource(
				"explosion.png"));

		middle.add(rules);
		middle.add(highScores);

		// edit buttons
		start = new JButton("Start");
		exit = new JButton("Exit");
		start.setSize(100, 50);
		exit.setSize(100, 50);
		start.setFont(new Font("Impact", Font.PLAIN, 20));
		exit.setFont(new Font("Impact", Font.PLAIN, 20));

		startPage();// starting page

	}// end GUI

	/**
	 * The starting page
	 */
	public void startPage() {

		printHighScores();

		// add buttons to panel
		bottom.add(start);
		bottom.add(exit);

		// add panels to frame
		add(top, BorderLayout.PAGE_START);
		add(middle, BorderLayout.CENTER);
		add(bottom, BorderLayout.PAGE_END);

		// give buttons an actionlistener
		start.addActionListener(new StartButtonHandler());
		exit.addActionListener(new ExitButtonHandler());
	}// end startPage

	/**
	 * Where the game is played
	 */
	public void gameLayout() {
		setLayout(new BorderLayout());

		// playing area (middle)
		playingArea = new JPanel();
		playingArea.setLayout(new GridLayout(Chase.SIZE, Chase.SIZE));
		buttons = new JButton[Chase.SIZE][Chase.SIZE];

		// menu panel (top)
		menu = new JPanel();
		info = new JLabel("");
		info.setFont(new Font("San Serif", Font.PLAIN, 25));
		menu.add(info);

		// teleport button (bottom)
		tp = new JPanel();
		teleport = new JButton("Teleport");
		teleport.addActionListener(new TeleportButtonHandler());
		teleport.setSize(250, 400);
		teleport.setFont(new Font("San Serif", Font.PLAIN, 15));
		tp.add(teleport);

		// add panels to frame
		add(menu, BorderLayout.PAGE_START);
		add(tp, BorderLayout.PAGE_END);
		add(playingArea, BorderLayout.CENTER);

		// give buttons action listener
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[0].length; j++) {
				buttons[i][j] = new JButton("");
				buttons[i][j].addActionListener(buttonGridHandler);
			}
		}
	}// end gameLayout

	/**
	 * Updates highscores on menu page
	 */
	public void printHighScores() {

		String scoreStr = new String();
		try {
			if (fileName.exists()) {
				
				FileReader inputFile = new FileReader(fileName);
				BufferedReader br = new BufferedReader(inputFile);

				boolean endOfTxt = false;
				String input = "";
				for (int i = 0; i < 8 && !endOfTxt; i++) {

					input = br.readLine();

					if (input != null) {
						scoreStr = scoreStr + input + "\n";
					} else {

						endOfTxt = true;
					}
				}
				highScores.setText(scoreStr);
				br.close();
			}
				
			}
			catch (Exception e) {
				highScores.setText("Highscores will show up here once you load or create a new highscore file after the game.");
			}
			 
	}// end printHighScores

	/**
	 * Popup screen for when you die
	 * 
	 * @return returns 0 if you press yes, 1 if you press no
	 */
	public int deathScreen() {

		int playAgain = deathWindow.showConfirmDialog(null,
				"You died! Would you like to record the score?", "You Died!",
				deathWindow.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		return playAgain;
	}// end deathScreen

	/**
	 * Prompts used to input name
	 * 
	 * @return user's name
	 */
	public String recordHighScore() {

		String name = JOptionPane
				.showInputDialog("Please enter your name to record the score: ");

		return name;
	}// end recordHighScore

	/**
	 * Opens file manager to create or save highscores to a file
	 */
	public void save() throws Exception {

		if (fileName == null) {
			file = new JFrame();

			saveFile = new JFileChooser();
			saveFile.setDialogTitle("Save Highscore File");

			if (saveFile.showSaveDialog(file) == JFileChooser.APPROVE_OPTION) {
				fileName = saveFile.getSelectedFile();
				chase.writeHighScoresToFile(fileName, name);
			}
		} else {
			chase.writeHighScoresToFile(fileName, name);
		}

	}// end save

	/**
	 * Determines what icon to put on button
	 */
	public void determineIcon() {
		menu.removeAll();// remove info from top

		int score = chase.calculateScore();

		int[][] grid = chase.getGrid();
		for (int i = 0; i < grid.length; i++) {// loop through grid and replace icons
			for (int j = 0; j < grid[0].length; j++) {

				if (grid[i][j] == Chase.ROBOT) {
					buttons[i][j].setIcon(robot);
				}

				else if (grid[i][j] == Chase.COLLISION) {
					buttons[i][j].setIcon(explosion);
				}

				else if (grid[i][j] == Chase.PLAYER) {
					buttons[i][j].setIcon(player);
				}

				else {
					buttons[i][j].setIcon(null);
				}

				buttons[i][j].setBackground(Color.white);
				playingArea.add(buttons[i][j]);

			}
		}
		// update info on labels
		info = new JLabel("Teleports: " + teleports + "   Level: " + lvl
				+ "   Score: " + score);
		info.setFont(new Font("San Serif", Font.PLAIN, 25));
		menu.add(info);
		menu.updateUI();

	}// end determineIcon

	/**
	 * Initializes some of the data that is in the GUI
	 */
	public void initGameData() {

		if (!chase.isWin()) {// if you lost reset tps and lvl
			lvl = 1;
			teleports = 3;
		}

		chase.generateGrid(lvl); // generate the grid

	}// end initGameData

	/**
	 * resets game once you die or win
	 */
	public void reset() throws Exception {

		if (chase.isDeath()) {// if you die
			// new icon for player
			// dead = new ImageIcon(".//res//dead.png");
			dead = new ImageIcon(getClass().getClassLoader().getResource(
					"dead.png"));
			int[] playerPosition = chase.getPlayerPosition();
			buttons[playerPosition[0]][playerPosition[1]].setIcon(dead);

			// update to final score
			chase.updateScore();

			int record = deathScreen();// put popup on screen

			if (record == 0) {// if they want to record score
				name = recordHighScore();
				if (name != null) {
					save();

				}
			}

			// resets to menu
			getContentPane().removeAll();
			getContentPane().repaint();
			startPage();
		}

		if (chase.isWin()) {// if you won

			// update values for new game
			chase.updateScore();
			chase.start();
			teleports++;
			lvl++;
			initGameData();
			chase.setWin(false);
			determineIcon();

		}
	}// end reset

	/**
	 * The event handler for the start button
	 */
	private class StartButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// repaint to playing screen and initialize variables for game
			getContentPane().removeAll();
			getContentPane().repaint();
			gameLayout();
			chase.start();
			initGameData();
			determineIcon();
			// chase.printGrid();
		}// end actionPerformed
	}// end StartButtonHandler

	/**
	 * The event handler for the exit button
	 */
	private class ExitButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}// end ExitButtonHandler

	/**
	 * The event handler for the teleport button
	 */
	private class TeleportButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (teleports > 0) {// teleport only if there are any left
				chase.teleport();
				teleports--; // take away a teleport
			}
			chase.robotMove();// move robots and detect for collisions
			chase.detectCollisions();
			determineIcon(); // repaint screen with new icons
			// chase.printGrid();
			try {
				reset(); // reset only if died or win
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}// end TeleportButtonHandler

	/**
	 * The event handler for the grid buttons
	 */
	private class ButtonGridHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			boolean buttonFound = false;
			int x = 0; // x value of button clicked
			int y = 0; // y value of button clicked

			// look for a button pressed in grid
			for (int i = 0; i < buttons.length || !buttonFound; i++) {
				for (int j = 0; j < buttons[0].length || !buttonFound; j++) {
					try {
						if (e.getSource() == buttons[i][j]) {
							buttonFound = true;// once button is found leave the
												// loop
							x = i;
							y = j;
						}
					} catch (ArrayIndexOutOfBoundsException event) {
						buttonFound = true;
					}
				}
			}

			if (buttonFound) {// if a button click is found
				chase.checkWin(); // check if player has won
				chase.playerMove(x, y);// move player towards button click
				chase.robotMove();// move the robots
				chase.detectCollisions(); // detect the collisions
				// chase.printGrid();

				determineIcon(); // repaint screen

				// System.out.println(x + " " + y);
			}

			try {
				reset(); // reset only if died or win
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

	}// end ButtonGridHandler

}// end GUI