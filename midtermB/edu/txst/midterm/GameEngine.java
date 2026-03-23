package edu.txst.midterm;

/**
 * The GameEngine class is responsible for the core logic of the maze game.
 * It tracks the player's position, the exit, steps taken, and coins collected.
 */
public class GameEngine {
	private Board board;
	private int playerRow;
	private int playerCol;
	private int exitRow;
	private int exitCol;
	private int steps;
	private int coins;

	// Cell Type Constants
	private static final int FLOOR = 0;
	private static final int WALL = 1;
	private static final int COIN = 2;
	private static final int EXIT = 5;
	private static final int PLAYER = 6;

	/**
	 * Constructor for GameEngine.
	 * 
	 * @param board The game board to be used for this level.
	 */
	public GameEngine(Board board) {
		this.board = board;
		this.steps = 0;
		this.coins = 0;
		findPlayer();
		findExit();
	}

	/**
	 * Checks if the player has reached the exit.
	 * 
	 * @return True if the player's position matches the exit position, false otherwise.
	 */
	public boolean playerWins() {
		return playerRow == exitRow && playerCol == exitCol;
	}

	/**
	 * Gets the current number of steps taken by the player.
	 * 
	 * @return Number of steps.
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Gets the current number of coins collected by the player.
	 * 
	 * @return Number of coins.
	 */
	public int getCoins() {
		return coins;
	}

	/**
	 * Locates the initial position of the player on the board.
	 */
	private void findPlayer() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == PLAYER) {
					playerRow = r;
					playerCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Locates the exit position on the board.
	 */
	private void findExit() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == EXIT) {
					exitRow = r;
					exitCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Attempts to move the player by the given delta row and col.
	 * Updates step count along with coins if the player picks one up.
	 * 
	 * @param dRow Change in row (-1, 0, 1)
	 * @param dCol Change in column (-1, 0, 1)
	 */
	public void movePlayer(int dRow, int dCol) {
		int targetRow = playerRow + dRow;
		int targetCol = playerCol + dCol;
		int targetCell = board.getCell(targetRow, targetCol);

		// Check for Walls or Out of Bounds
		if (targetCell == WALL || targetCell == -1) {
			return; // Movement blocked
		}

		if (targetCell == COIN) {
			coins++;
		}
		
		steps++;

		// Move the Player
		// Current position becomes Floor (or Goal if player was standing on one)
		// Note: For simplicity, this engine assumes player replaces the cell.
		// If you want "Player on Goal", you'd add a 6th constant.
		board.setCell(playerRow, playerCol, FLOOR);

		playerRow = targetRow;
		playerCol = targetCol;
		board.setCell(playerRow, playerCol, PLAYER);

	}
}
