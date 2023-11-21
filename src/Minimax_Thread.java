import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

public class Minimax_Thread implements Callable<Integer> {
	private String[][] game_board;
	private boolean turn;
	private int tree_depth;
	private int alpha;
	private int beta;
	private int board_length;
	private int combination_length;
	private String user_symbol;
	private String vortex_symbol;
	
	public Minimax_Thread(String[][] new_game_board, boolean new_turn, int new_tree_depth, int new_alpha, int new_beta, int new_board_length, int new_combination_length, String new_user_symbol, String new_vortex_symbol) {
        this.game_board = new_game_board;
        this.turn = new_turn;
        this.tree_depth = new_tree_depth;
        this.alpha = new_alpha;
        this.beta = new_beta;
        this.board_length = new_board_length;
        this.combination_length = new_combination_length;
        this.user_symbol = new_user_symbol;
        this.vortex_symbol = new_vortex_symbol;
	}
    
    public Integer call() throws Exception {
        return Minimax(this.game_board, this.turn, this.tree_depth, this.alpha, this.beta, this.board_length, this.combination_length, this.user_symbol, this.vortex_symbol);
	}
    
    private int Minimax(String[][] game_board, boolean turn, int tree_depth, int alpha, int beta, int board_length, int combination_length, String user_symbol, String vortex_symbol) {
		int score = 0;
		String WinState = "";
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		// Check if the game has ended.
		WinState = new GameplayStatus(board_length, combination_length, game_board).CheckStatus();
		
		// If the game has ended, return the score.
		// Scores are penalised based on the number of steps (tree depth).
		if (!WinState.equals("")) {
			
			// Vortex is the maximising agent.
			score = ((board_length * board_length) + 1) - tree_depth;
			
			if (WinState.equals(user_symbol)) {
				// User is the minimising agent.
				score = -1 * score;
			} else if (WinState.equals("T")) {
				// Ties are considered neutral.
				score = 0;
			}
			
			return score;
		}
		
		// If the game is not over, continue expanding the game tree.
		for (int y_counter = 0; y_counter < board_length; y_counter++) { 
			for (int x_counter = 0; x_counter < board_length; x_counter++) {
				if (game_board[x_counter][y_counter].equals("")) {
					
					// Place the appropriate symbol depending on who's turn it would be.
					if (turn) {
						// User.
						game_board[x_counter][y_counter] = user_symbol;
					} else {
						// Vortex.
						game_board[x_counter][y_counter] = vortex_symbol;
					}
					
					// Recurse
					score = Minimax(game_board, !turn, tree_depth + 1, alpha, beta, board_length, combination_length, user_symbol, vortex_symbol);
					
					// Add the score to the growing list.
					list.add(score);
					
					// Backtrack the game state.
					game_board[x_counter][y_counter] = "";
					
					// Update alpha and beta values for pruning.
					if (turn) {
						// User will aim to minimise score.
						beta = Math.min(beta, Collections.min(list));
					} else {
						// Vortex will aim to maximise score.
						alpha = Math.max(alpha, Collections.max(list));
					}
					
					// Perform alpha-beta pruning.
					if (beta <= alpha) {
						// Do not bother evaluating further and submit the current value upward.
						return turn ? Collections.min(list) : Collections.max(list);
					}
				}
			}
		}
		
		// User will aim to minimise score, so return the smallest item.
		// Vortex will aim to maximise score, so return the largest item.
		return turn ? Collections.min(list) : Collections.max(list);
	}
}
