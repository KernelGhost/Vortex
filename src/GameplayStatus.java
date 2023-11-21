public class GameplayStatus {
	private int board_length;
	private int combination_length;
	private String[][] game_board;
	
	public GameplayStatus(int new_board_length, int new_combination_length, String[][] new_game_board) {
		this.board_length = new_board_length;
		this.combination_length = new_combination_length;
		this.game_board = new_game_board;
	}
	
	// Checks whether a game has ended.
	// Returns the winning player's symbol.
	// Returns "T" if the game was tied.
	// Returns "" if the game is not over.
	public String CheckStatus() {
  		String[] symbol_sequence = new String[this.combination_length];
  		String game_result = "";
  		boolean tie = true;
  		
  		// Horizontal & vertical checks.
  		for (int orientation_counter = 0; orientation_counter < 2; orientation_counter++) {
  			for (int counter_1 = 0; counter_1 < this.board_length; counter_1++) {
  				for (int counter_2 = this.combination_length - 1; counter_2 < this.board_length; counter_2++) {
  					for (int combination_counter = 0; combination_counter < this.combination_length; combination_counter++) {
  						if (orientation_counter == 0) {
  							symbol_sequence[combination_counter] = this.game_board[counter_2 - combination_counter][counter_1];
  						} else {
  							symbol_sequence[combination_counter] = this.game_board[counter_1][counter_2 - combination_counter];
  						}
  					}
  					
  					// Check if the combination is a winning sequence.
  					game_result = CheckWinningSequence(symbol_sequence);
  					if (!game_result.equals("")) {
  						return game_result;
  					}
  				}
  			}
  		}
  		
  		// Diagonal checks (/).
  		for (int counter_1 = 0; counter_1 < this.board_length - this.combination_length + 1; counter_1++) {
  			for (int counter_2 = this.combination_length - 1; counter_2 < this.board_length; counter_2++) {
  				for (int combination_counter = 0; combination_counter < this.combination_length; combination_counter++) {
  					symbol_sequence[combination_counter] = this.game_board[counter_2 - combination_counter][counter_1 + combination_counter];
  				}
  				
  				// Check if the combination is a winning sequence.
  				game_result = CheckWinningSequence(symbol_sequence);
  				if (!game_result.equals("")) {
  					return game_result;
  				}
  			}
  		}
  		
  		// Diagonal checks (\).
  		for (int counter_1 = 0; counter_1 < this.board_length - this.combination_length + 1; counter_1++) {
  			for (int counter_2 = 0; counter_2 < this.board_length - this.combination_length + 1; counter_2++) {
  				for (int combination_counter = 0; combination_counter < this.combination_length; combination_counter++) {
  					symbol_sequence[combination_counter] = this.game_board[counter_2 + combination_counter][counter_1 + combination_counter];
  				}
  				
  				// Check if the combination is a winning sequence.
  				game_result = CheckWinningSequence(symbol_sequence);
  				if (!game_result.equals("")) {
  					return game_result;
  				}
  			}
  		}
  		
  		// Tie check.
  		for (int counter_1 = 0; counter_1 < this.board_length; counter_1++) {
  			for (int counter_2 = 0; counter_2 < this.board_length; counter_2++) {
  				if (this.game_board[counter_1][counter_2].equals("")) {
  					tie = false;
  				}
  			}
  		}
  		
  		if (tie) {
  			return "T";
  		}
  		
  		// Will only run if result is "" as all other results would have been returned earlier.
  		return game_result;
  	}
  	
    // Checks if a string array contains a winning sequence (e.g. ["X", "X", "X"]).
	// Returns the symbol of the winning player if the sequence is a winning sequence.
	// Returns "" if the sequence is not a winning sequence.
  	private String CheckWinningSequence(String[] symbol_sequence) {
  		
  		// If the first symbol is empty, set the flag to false.
  		boolean winning_sequence = (!symbol_sequence[0].equals(""));
  		
  		if (winning_sequence) {
  			// Check all elements in the string array are identical.
  			for (int counter = 1; counter < symbol_sequence.length; counter++) {
  				if (!symbol_sequence[0].equals(symbol_sequence[counter])) {
  					winning_sequence = false;
  					break;
  				}
  			}
  		}
  		
  		return winning_sequence ? symbol_sequence[0] : "";
  	}
}
