package edu.txst.midterm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * The MazeGUI class provides the graphical user interface for the Maze Game.
 * It manages the main window, renders the board, and processes keyboard inputs.
 */
public class MazeGUI extends JFrame {
	private Board originalBoard;
	private Board currentBoard;
	private GameEngine engine;
	private GamePanel gamePanel;
	private InfoPanel infoPanel;
	private JMenuItem resetItem;

	/**
	 * Constructor that sets up the GUI elements, handles keyboard input, and initializes components.
	 */
	public MazeGUI() {
		setTitle("16-Bit Maze");
		setSize(640, 480); // Adjusted for 10x5 grid with scaling
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		initMenu();

		infoPanel = new InfoPanel();
		gamePanel = new GamePanel();
		add(infoPanel, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);

		// Handle Keyboard Input
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (engine == null)
					return;

				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP -> engine.movePlayer(-1, 0);
					case KeyEvent.VK_DOWN -> engine.movePlayer(1, 0);
					case KeyEvent.VK_LEFT -> engine.movePlayer(0, -1);
					case KeyEvent.VK_RIGHT -> engine.movePlayer(0, 1);
				}
				
				infoPanel.setInfoSteps(engine.getSteps());
				infoPanel.setInfoCoins(engine.getCoins());
				
				gamePanel.repaint();

				// Check for victory
				if (engine.playerWins()) {
					JOptionPane.showMessageDialog(MazeGUI.this,
							"Congratulations! You found the exit.\nYou got "
									+ (infoPanel.getInfoSteps() * -1 + infoPanel.getInfoCoins() * 5)
									+ " points.",
							"Level Complete", JOptionPane.INFORMATION_MESSAGE);

					// Optional: Disable engine to prevent movement after win
					engine = null;
					resetItem.setEnabled(false);
				}
			}
		});
	}

	/**
	 * Initializes the top menu bar, including options to Open files and Reset the game.
	 */
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem openItem = new JMenuItem("Open");
		resetItem = new JMenuItem("Reset");
		resetItem.setEnabled(false); // Disabled by default

		openItem.addActionListener(e -> openFile());
		resetItem.addActionListener(e -> resetGame());

		gameMenu.add(openItem);
		gameMenu.add(resetItem);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * Opens a file chooser to load a new maze level from a CSV file.
	 */
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			CSVBoardLoader loader = new CSVBoardLoader();

			// Load and Store
			originalBoard = loader.load(selectedFile.getAbsolutePath());
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);

			resetItem.setEnabled(true);
			gamePanel.setBoard(currentBoard);
			infoPanel.setInfoSteps(engine.getSteps());
			infoPanel.setInfoCoins(engine.getCoins());
			gamePanel.repaint();
		}
	}

	/**
	 * Resets the current level back to its original state.
	 */
	private void resetGame() {
		if (originalBoard != null) {
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);
			gamePanel.setBoard(currentBoard);
			infoPanel.setInfoSteps(engine.getSteps());
			infoPanel.setInfoCoins(engine.getCoins());
			gamePanel.repaint();
		}
	}

	/**
	 * InfoPanel is a custom JPanel that displays the step and coin counters below the menu.
	 */
	private class InfoPanel extends JPanel {
		private JLabel infoSteps;
		private JLabel infoCoins;

		/**
		 * Constructor that initializes the labels for steps and coins.
		 */
		public InfoPanel() {
			this.setLayout(new FlowLayout());
			this.add(new JLabel("Steps: "));
			// infoRemainingSteps is a label which value can be changed using its method called
			// setText
			infoSteps = new JLabel("0");
			this.add(infoSteps);
			this.add(new JLabel("Coins: "));
			// infoCoins is a label which value can be changed using its method called setText
			infoCoins = new JLabel("0");
			this.add(infoCoins);
		}

		/**
		 * Updates the displayed step count.
		 * 
		 * @param remainingSteps The number of steps to display.
		 */
		public void setInfoSteps(int remainingSteps) {
			this.infoSteps.setText(Integer.toString(remainingSteps));
		}

		/**
		 * Retrieves the current number of steps from the label.
		 * 
		 * @return The current steps shown.
		 */
		public int getInfoSteps() {
			return Integer.parseInt(this.infoSteps.getText());
		}

		/**
		 * Updates the displayed coin count.
		 * 
		 * @param infoCoins The number of coins to display.
		 */
		public void setInfoCoins(int infoCoins) {
			this.infoCoins.setText(Integer.toString(infoCoins));
		}

		/**
		 * Retrieves the current number of coins from the label.
		 * 
		 * @return The current coins shown.
		 */
		public int getInfoCoins() {
			return Integer.parseInt(this.infoCoins.getText());
		}
	}

	/**
	 * GamePanel is a custom JPanel that renders the maze board visually on the screen.
	 */
	private class GamePanel extends JPanel {
		private Board board;
		private final int TILE_SIZE = 64; // Scale up for visibility

		/**
		 * Sets the board to be rendered.
		 * 
		 * @param board The current maze board.
		 */
		public void setBoard(Board board) {
			this.board = board;
		}

		/**
		 * Overrides the paintComponent to draw the tiles of the game board.
		 * 
		 * @param g The Graphics object used for drawing.
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (board == null)
				return;

			for (int r = 0; r < 6; r++) {
				for (int c = 0; c < 10; c++) {
					int cell = board.getCell(r, c);
					drawTile(g, cell, c * TILE_SIZE, r * TILE_SIZE);
				}
			}
		}

		/**
		 * Helper method to draw a single tile on the grid.
		 * 
		 * @param g    The Graphics context.
		 * @param type The type of the cell (floor, wall, coin, exit, player).
		 * @param x    The x-coordinate.
		 * @param y    The y-coordinate.
		 */
		private void drawTile(Graphics g, int type, int x, int y) {
			// Placeholder colors until you link the sprite loading logic
			switch (type) {
				case 0 -> g.setColor(Color.LIGHT_GRAY); // Floor
				case 1 -> g.setColor(Color.DARK_GRAY); // Wall
				case 2 -> g.setColor(Color.YELLOW); // Coin
				case 5 -> g.setColor(Color.MAGENTA); // Exit
				case 6 -> g.setColor(Color.BLUE); // Player
				default -> g.setColor(Color.BLACK);
			}
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			g.setColor(Color.WHITE);
			g.drawRect(x, y, TILE_SIZE, TILE_SIZE); // Grid lines
		}
	}

	/**
	 * The main method to launch the application.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MazeGUI().setVisible(true));
	}
}
