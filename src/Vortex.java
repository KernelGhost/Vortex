import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Vortex {
	// Settings
	private int board_size = 3;												// Grid length and width.
	private int win_length = 3;												// Winning sequence length.
	private Color user_colour = new Color(88, 214, 141);					// User's colour.
	private Color vortex_colour = new Color(41, 128, 185);					// Vortex's colour.
	private String username;												// User's username.
	private boolean find_remaining_games = false;							// Find number of remaining games?
	private String user_symbol = "X";										// User's symbol.
	private String vortex_symbol = "O";										// Vortex's symbol.
	
	// Game Variables
	private String[][] game_board = new String[board_size][board_size];		// Game board state.
	private boolean game_running;											// Is the game not over?
	private int vortex_score;												// Vortex's score.
	private int user_score;													// User's score.
	
	// Window Elements
	private About about_window;												// Instance of 'About'.
	private boolean about_open = false;										// 'About' window open?
	
	// GUI Elements
	private JFrame frmVortex;
	private JPanel panel;
	private JLabel lblTitle;
	private JLabel lblSubheading;
	private GridBox[] buttons = new GridBox[board_size * board_size];
	private JLabel lblVortex;
	private JLabel lblUser;
	private JLabel lblVortexScore;
	private JLabel lblUserScore;
	private JButton btnFirst;
	private JButton btnSecond;
	private JButton btnNewGame;
	private JButton btnResetScoreboard;
	private JLabel lblPossibilities;
	private JLabel lblAuthor;
	
	// Create object.
	public Vortex() {
		Initialise();
	}
	
	// Called when the user clicks on a cell on the game board.
	private void UserMove(int linear_index) {
		CartesianCell cartesian_user_move = CartesianCell.ToCartesian(linear_index, board_size);
		
		// Register the user's move only if the game is not over and the move is valid.
		if (game_running && (game_board[cartesian_user_move.GetX()][cartesian_user_move.GetY()].equals(""))) {
			game_board[cartesian_user_move.GetX()][cartesian_user_move.GetY()] = user_symbol;
			PlaceMoveUI(user_symbol, linear_index);
			
			// Calculate the remaining number of possible games.
			if (find_remaining_games) {
				UpdatePossibleGames(false);
			}
			
			// Check if the game was won/tied.
			game_running = !CheckWin();
			
			if (game_running) {
				// Respond with the computer's move.
				ComputerMove();
				
				// Check if the game was won/tied.
				game_running = !CheckWin();
			}
		}
	}
	
	// Check if a player has won or tied the game.
	private boolean CheckWin() {
		// Check if the game has ended.
		String WinState = new GameplayStatus(board_size, win_length, game_board).CheckStatus();
		
		if (!WinState.equals("")) {
			// Update possibilities label.
			if (find_remaining_games) {
				lblPossibilities.setText("Possible Games: 0");
			}
			
			if (WinState.equals(user_symbol)) {
				infoBox(username + " wins this round!", "Vortex", "User");
				user_score++;
			} else if (WinState.equals(vortex_symbol)) {
				infoBox("Vortex wins this round!", "Vortex", "Vortex");
				vortex_score++;
			} else if (WinState.equals("T")) {
				infoBox("It's a tie!", "Vortex", "Tie");
			}
			
			// Update user interface elements.
			lblVortexScore.setText(Integer.toString(vortex_score));
			lblUserScore.setText(Integer.toString(user_score));
			btnNewGame.setVisible(true);
			btnFirst.setVisible(false);
			btnSecond.setVisible(false);
			btnFirst.setEnabled(true);
			btnSecond.setEnabled(true);
			btnResetScoreboard.setEnabled(true);
		}
		
		return (!WinState.equals(""));
	}
	
	// Places a move on the GUI.
	private void PlaceMoveUI(String player_symbol, int index) {
		buttons[index].setText(player_symbol);
		if (player_symbol.equals(user_symbol)) {
			buttons[index].setForeground(user_colour);
		} else {
			buttons[index].setForeground(vortex_colour);
		}
	}
	
	// Plays the computer's move.
	private void ComputerMove() {
		List<Integer> legal_moves = new ArrayList<>();
		List<String[][]> legal_subsequent_game_states = new ArrayList<>();
		int[][] scores = new int[board_size][board_size];
		List<Integer> best_moves = new ArrayList<>();
		List<Future<Integer>> minimax_threads_results = null;
		CartesianCell best_move;
		int top_score = Integer.MIN_VALUE;
		int random_best_move_index = 0;
		
		// Initialise the scores array.
		for (int y_counter = 0; y_counter < board_size; y_counter++) {
			for (int x_counter = 0; x_counter < board_size; x_counter++) {
				scores[x_counter][y_counter] = Integer.MIN_VALUE;
			}
		}
		
		// Store all legal subsequent game states.
		for (int y_counter = 0; y_counter < board_size; y_counter++) {
			for (int x_counter = 0; x_counter < board_size; x_counter++) {
				if (game_board[x_counter][y_counter].equals("")) {
					
					// Store the move played
					legal_moves.add(new CartesianCell(x_counter, y_counter, board_size).GetLinearIndex());
					
					// Make a copy of the game state.
					String[][] newGameState = new String[board_size][];
					for (int copy_counter = 0; copy_counter < board_size; copy_counter++) {
						newGameState[copy_counter] = new String[board_size];
						System.arraycopy(game_board[copy_counter], 0, newGameState[copy_counter], 0, board_size);
			        }
					
					// Play the move on the copy of the game state.
					newGameState[x_counter][y_counter] = vortex_symbol;
					
					// Store the copy of the game state.
					legal_subsequent_game_states.add(newGameState);
				}
			}
		}
		
		// Prepare multi-threading variables.
		//ExecutorService executor_service = Executors.newCachedThreadPool();
		ExecutorService executor_service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
		
		// Obtain a score for each possible move at a depth of zero.
		for (int move_counter = 0; move_counter < legal_subsequent_game_states.size(); move_counter++) {			
			// Add the task.
			// Note: Objects (but not primitives) are added to ArrayLists by reference.
			// This is why distinct copies of each legal subsequent game state are required.
			tasks.add(new Minimax_Thread(legal_subsequent_game_states.get(move_counter), true, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, board_size, win_length, user_symbol, vortex_symbol));
		}
		
		// Run all tasks in parallel and wait for all tasks to complete.
		try {
			minimax_threads_results = executor_service.invokeAll(tasks);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while invoking the multithreaded minimax engine.\nVortex will now exit.", "Vortex", JOptionPane.ERROR_MESSAGE, null);
			System.exit(1);
		}
		
		// Make the ExecutorService stop accepting new tasks and shut down after all running threads finish their work.
		executor_service.shutdown();
		
		// Store results from the threads.
		for (int counter = 0; counter < legal_moves.size(); counter++) {
			try {
				scores[CartesianCell.ToCartesian(legal_moves.get(counter), board_size).GetX()][CartesianCell.ToCartesian(legal_moves.get(counter), board_size).GetY()] = minimax_threads_results.get(counter).get();
			} catch (InterruptedException | ExecutionException e) {
				//e.printStackTrace();
				JOptionPane.showMessageDialog(null, "An error occurred while running the multithreaded minimax engine.\nVortex will now exit.", "Vortex", JOptionPane.ERROR_MESSAGE, null);
				System.exit(1);
			}
		}
		
		// Vortex aims to maximise the score.
		for (int y_counter = 0; y_counter < board_size; y_counter++) {
			for (int x_counter = 0; x_counter < board_size; x_counter++) {
				if (scores[x_counter][y_counter] > top_score) {
					top_score = scores[x_counter][y_counter];
				}
			}
		}
		
		// Print the scores (debugging)
		/*
		for (int y_counter = 0; y_counter < board_size; y_counter++) {
			for (int x_counter = 0; x_counter < board_size; x_counter++) {
				if (x_counter == board_size - 1) {
					System.out.printf("%8d", scores[x_counter][y_counter]);
				} else {
					System.out.printf("%8d", scores[x_counter][y_counter]);
					System.out.print("\t" + " | " + "\t");
				}
			}
			System.out.println();
		}
		System.out.println("\n---------------- NEXT MOVE ----------------\n");
		*/
		
		// Find move(s) that result in the top score.
		for (int y_counter = 0; y_counter < board_size; y_counter++) {
			for (int x_counter = 0; x_counter < board_size; x_counter++) {
				if (scores[x_counter][y_counter] == top_score) {
					best_moves.add(new CartesianCell(x_counter, y_counter, board_size).GetLinearIndex());
				}
			}
		}
		
		// If there are multiple equally good moves, choose one randomly.
		random_best_move_index = ThreadLocalRandom.current().nextInt(0, best_moves.size()); // Note: Upper bound is exclusive.
		best_move = CartesianCell.ToCartesian(best_moves.get(random_best_move_index), board_size);
		
		// Play the chosen move.
		game_board[best_move.GetX()][best_move.GetY()] = vortex_symbol;
		PlaceMoveUI(vortex_symbol, best_move.GetLinearIndex());
		
		// Calculate the remaining number of possible games.
		if (find_remaining_games) {
			UpdatePossibleGames(true);
		}
	}
	
	private void infoBox(String infoMessage, String titleBar, String player) {
		ImageIcon icon = null;
		if (player.equals("User")) {
			icon = new ImageIcon(Vortex.class.getResource("resources/graphics/win.png"));
		} else if (player.equals("Vortex")) {
			icon = new ImageIcon(Vortex.class.getResource("resources/graphics/loss.png"));
		} else if (player.equals("Tie")) {
			icon = new ImageIcon(Vortex.class.getResource("resources/graphics/tie.png"));
		}
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE, icon);
    }
	
	private String GetUserName() {
		return System.getProperty("user.name");
	}
	
	private void ResetGame(boolean fullreset) {
		int ctrb = 0;
		int ctrx = 0;
		int ctry = 0;
		
		// Clear the game scores.
		if (!fullreset) {
			user_score = 0;
			vortex_score = 0;
			lblVortexScore.setText("0");
			lblUserScore.setText("0");
		} else {
			if (find_remaining_games) {
				lblPossibilities.setText("");
			}
			
			// Clear the board.
			for(ctrb = 0; ctrb < (board_size * board_size); ctrb++){
				buttons[ctrb].setText("");
			}
			
			// Initialise the 2D board array.
			for(ctrx = 0; ctrx < board_size; ctrx++){  
				for(ctry = 0; ctry < board_size; ctry++){ 
					game_board[ctrx][ctry] = "";
				}
			}
		}
	}
	
	private void UpdatePossibleGames(boolean user_plays_first) {
		// Prepare and execute calculation of number of possible remaining games.
		ExecutorService executor_service = Executors.newFixedThreadPool(1);
		PossibleRemainingGames_Thread possible_games = new PossibleRemainingGames_Thread(game_board, user_plays_first, board_size, win_length, user_symbol, vortex_symbol);
		Future<Integer> future_number_possible_games = executor_service.submit(possible_games);
		int number_possible_games = 0;
		try {
			// Display number of remaining possible games
			number_possible_games = future_number_possible_games.get();
			lblPossibilities.setText("Possible Games: " + Integer.toString(number_possible_games));
		} catch (InterruptedException | ExecutionException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while calculating the number of remaining possible games.", "Vortex", JOptionPane.ERROR_MESSAGE, null);
			lblPossibilities.setText("");
		}
	}
	
	private void StartGame(boolean user_plays_first) {
		// Set game running flag.
		game_running = true;
		
		// Update GUI elements.
		btnFirst.setEnabled(false);
		btnSecond.setEnabled(false);
		btnResetScoreboard.setEnabled(false);
		
		if (!user_plays_first) {
			// Computer plays a move.
			ComputerMove();
			
			// Check if the game was won/tied.
			game_running = !CheckWin();
		} else {
			// Calculate the remaining number of possible games.
			if (find_remaining_games) {
				UpdatePossibleGames(user_plays_first);
			}
		}
	}

	// Initialise the instance of Vortex.
	private void Initialise() {		
		// Prepare menu bar.
		JMenuBar menu_bar = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		JMenu help_menu = new JMenu("Help");
		
        // Menu items.
		JMenuItem menu_about = new JMenuItem("About");
		menu_about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!about_open) {
					about_open = true;
					about_window = new About();
					about_window.addWindowListener(new WindowAdapter() { 
					    @Override public void windowClosed(WindowEvent e) { 
					    	about_open = false;
					    }
					});
					about_window.setVisible(true);
				} else {
					about_window.requestFocus();
				}
			}
		});
		
		JMenuItem menu_exit = new JMenuItem("Exit");
		menu_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
        
        // Add about to help menu.
		help_menu.add(menu_about);
		
		// Add size and exit to file menu.
        file_menu.add(menu_exit);
        
        // Add file and help menus to menu bar.
        menu_bar.add(file_menu);
        menu_bar.add(help_menu);
		
        // Prepare JFrame.
		frmVortex = new JFrame();
		frmVortex.setResizable(false);
		frmVortex.setTitle("Vortex");
		frmVortex.setJMenuBar(menu_bar);
		frmVortex.setIconImage(Toolkit.getDefaultToolkit().getImage(Vortex.class.getResource("resources/graphics/icon.png")));
		if (board_size < 3) {
			frmVortex.getContentPane().setPreferredSize(new Dimension(508 + (board_size - 3) * 62, 280));
		} else {
			frmVortex.getContentPane().setPreferredSize(new Dimension(508 + (board_size - 3) * 62, 280 + (board_size - 3) * 62));
		}
		frmVortex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVortex.getContentPane().setLayout(null);
		frmVortex.pack();
		frmVortex.setLocationRelativeTo(null);
		
		// Scoreboard initialisation.
		username = GetUserName();
		panel = new JPanel();
		panel.setBorder(null);
		if (board_size >= 3) {
			panel.setBounds(220 + (board_size - 3) * 62, 130 + (board_size - 3) * 62, 267, 85);
		} else {
			panel.setBounds(220 + (board_size - 3) * 62, 130, 267, 85);
		}
		frmVortex.getContentPane().add(panel);
		panel.setLayout(null);
		
		lblVortex = new JLabel("Vortex");
		lblVortex.setHorizontalAlignment(SwingConstants.CENTER);
		lblVortex.setBounds(6, 29, 128, 16);
		panel.add(lblVortex);
		
		lblUser = new JLabel("User");
		lblUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblUser.setBounds(132, 29, 128, 16);
		lblUser.setText(username);
		panel.add(lblUser);
		
		lblVortexScore = new JLabel("0");
		lblVortexScore.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblVortexScore.setForeground(vortex_colour);
		lblVortexScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblVortexScore.setBounds(6, 46, 128, 39);
		panel.add(lblVortexScore);
		
		lblUserScore = new JLabel("0");
		lblUserScore.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblUserScore.setForeground(user_colour);
		lblUserScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore.setBounds(132, 46, 128, 39);
		panel.add(lblUserScore);
		
		btnResetScoreboard = new JButton("Reset Scoreboard");
		btnResetScoreboard.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		btnResetScoreboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResetGame(false);
			}
		});
		if (board_size < 3) {
			btnResetScoreboard.setBounds(220 + (board_size - 3) * 62, panel.getY() + panel.getHeight() + 5, 267, 35);
		} else {
			btnResetScoreboard.setBounds(220 + (board_size - 3) * 62, 92 + ((board_size - 1) * 62), 267, 35);
		}
		frmVortex.getContentPane().add(btnResetScoreboard);
		
		ResetGame(false);
		
		// Tic-Tac-Toe buttons.
		int bctr = 0;
		
		for(bctr = 0; bctr < (board_size * board_size); bctr++){ 
			buttons[bctr] = new GridBox(bctr);
			buttons[bctr].setHorizontalAlignment(SwingConstants.CENTER);
			buttons[bctr].setFont(new Font("Lucida Grande", Font.BOLD, 40));
			buttons[bctr].setBounds((25 + (62 * CartesianCell.ToCartesian(bctr, board_size).GetX())), (77 + (62 * CartesianCell.ToCartesian(bctr, board_size).GetY())), 50, 50);
			buttons[bctr].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			buttons[bctr].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// Changed from 'mouseClick', as this required the cursor to not move between being pressed and released to trigger the event.
					GridBox source = (GridBox) e.getSource();
					UserMove(source.GetIndex());
				}
			});
			frmVortex.getContentPane().add(buttons[bctr]);
		}
		
		// New game buttons.
		btnNewGame = new JButton("New Game");
		btnNewGame.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewGame.setVisible(false);
				btnFirst.setVisible(true);
				btnSecond.setVisible(true);
				ResetGame(true);
			}
		});
		btnNewGame.setBounds(220 + (board_size - 3) * 62, 77, 267, 50);
		frmVortex.getContentPane().add(btnNewGame);
		
		btnFirst = new JButton("Go First");
		btnFirst.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartGame(true);
			}
		});
		btnFirst.setBounds(220 + (board_size - 3) * 62, 77, 117, 50);
		btnFirst.setVisible(false);
		frmVortex.getContentPane().add(btnFirst);
		
		btnSecond = new JButton("Go Second");
		btnSecond.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		btnSecond.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartGame(false);
			}
		});
		btnSecond.setBounds(369 + (board_size - 3) * 62, 77, 117, 50);
		btnSecond.setVisible(false);
		frmVortex.getContentPane().add(btnSecond);
		
		// Other labels.
		lblTitle = new JLabel("VORTEX");
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 45));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(6, 0, 488 + (board_size - 3) * 62, 62);
		frmVortex.getContentPane().add(lblTitle);
		
		lblSubheading = new JLabel("A Minimax-Based Tic-Tac-Toe AI");
		lblSubheading.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubheading.setBounds(6, 52, 488 + (board_size - 3) * 62, 16);
		frmVortex.getContentPane().add(lblSubheading);
		
		lblPossibilities = new JLabel("");
		lblPossibilities.setBounds(25, 70 + (board_size * 62), 176, 16);
		frmVortex.getContentPane().add(lblPossibilities);
		
		lblAuthor = new JLabel("By Rohan Barar, 2019");
		lblAuthor.setHorizontalAlignment(SwingConstants.TRAILING);
		if (board_size < 3) {
			lblAuthor.setBounds(327 + (board_size - 3) * 62, btnResetScoreboard.getY() + btnResetScoreboard.getHeight() + 1, 160, 16);
		} else {
			lblAuthor.setBounds(327 + (board_size - 3) * 62, 256 + (board_size - 3) * 62, 160, 16);
		}
		frmVortex.getContentPane().add(lblAuthor);
		
		// Make visible.
		frmVortex.setVisible(true);
	}
}