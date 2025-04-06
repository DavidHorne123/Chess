package piece;

import main.GamePanel;

public class Knight extends Piece{

	public Knight(int color, int col, int row) {
		super(color, col, row);
		// If the knight's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-knight");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-knight");
		}
	}
	// Creating the canMove method
	// Then we check if the target square is reachable by the knight
	public boolean canMove(int targetCol, int targetRow) {
		
		// Checks if it's within the board
		if(isWithinBoard(targetCol, targetRow)) {
			// knight can move if its movement ratio of col and row is 1:2 or 2:1
			if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
				// Check if the destination square is valid or not
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
