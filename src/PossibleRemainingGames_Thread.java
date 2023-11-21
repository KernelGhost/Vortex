import java.util.concurrent.Callable;

public class PossibleRemainingGames_Thread implements Callable<Integer> {
	private String[][] game_board;
	private boolean turn;
	private int board_length;
	private int combination_length;
	private String user_symbol;
	private String vortex_symbol;
	
	public PossibleRemainingGames_Thread(String[][] new_game_board, boolean new_turn, int new_board_length, int new_combination_length, String new_user_symbol, String new_vortex_symbol) {
		this.game_board = new_game_board;
		this.turn = new_turn;
		this.board_length = new_board_length;
		this.combination_length = new_combination_length;
		this.user_symbol = new_user_symbol;
		this.vortex_symbol = new_vortex_symbol;
	}
	
	public Integer call() throws Exception {
		return PossibleRemainingGames(this.game_board, this.turn, this.board_length, this.combination_length, this.user_symbol, this.vortex_symbol);
	}
	
	// Calculates the number of possible remaining games given a game board.
	private int PossibleRemainingGames(String[][] game_board, boolean turn, int board_length, int combination_length, String user_symbol, String vortex_symbol) {
		int number_of_games = 0;
		String WinState = "";
		
		// If a move is legal, play that move.
		for (int y_counter = 0; y_counter < board_length; y_counter++) {
			for (int x_counter = 0; x_counter < board_length; x_counter++) {
				if (game_board[x_counter][y_counter].equals("")) {
					if (!turn) {
						game_board[x_counter][y_counter] = vortex_symbol;
					} else {
						game_board[x_counter][y_counter] = user_symbol;
					}
					
					// Check if the game is over.
					WinState = new GameplayStatus(board_length, combination_length, game_board).CheckStatus();
					
					if (!WinState.equals("")) {
						// If the game is over, increment the sum.
						number_of_games++;
					} else {
						// If the game is not over, recurse.
						number_of_games = number_of_games + PossibleRemainingGames(game_board, !turn, board_length, combination_length, user_symbol, vortex_symbol);
					}
					
					// Restore the game board state.
					game_board[x_counter][y_counter] = "";
				}
			}
		}
		
		return number_of_games;
	}
}
