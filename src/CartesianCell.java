public class CartesianCell {
    private int x_coordinate;
    private int y_coordinate;
    private int board_length;
    
    // Initialise the CartesianCell instance.
    public CartesianCell(int new_x_coordinate, int new_y_coordinate, int new_board_length) {
        this.x_coordinate = new_x_coordinate;
        this.y_coordinate = new_y_coordinate;
        this.board_length = new_board_length;
    }
    
    // Return the X coordinate.
    public int GetX() {
		return this.x_coordinate;
    }
    
    // Return the Y coordinate.
    public int GetY() {
		return this.y_coordinate;
    }
    
    // Return the board size.
    public int GetBoardSize() {
		return this.board_length;
    }
    
    // Returns an integer representing the game cell position.
    // Counting is performed from left to right and from top to bottom.
    public int GetLinearIndex() {
		return (this.board_length * this.GetY()) + this.GetX();
	}
    
    // Converts a linear index to a set of x and y coordinates.
    // Note: Method is static as it should belong to the class rather than instances of the class.
    public static CartesianCell ToCartesian(int linear_index, int board_length) {
 		int new_x_coordinate = (linear_index % board_length);
 		int new_y_coordinate = (linear_index - new_x_coordinate) / board_length;
 		return new CartesianCell(new_x_coordinate, new_y_coordinate, board_length);
 	}
}
