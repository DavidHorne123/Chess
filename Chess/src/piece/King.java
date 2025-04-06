package piece;

import main.GamePanel;

public class King extends Piece{

	public King(int color, int col, int row) {
		super(color, col, row);
		// If the king's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-king");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-king");
		}
	}
	
	// Here we pass col and row of a square 
	// We check if this king can move to this square
	// If the king can move to the square return true else return false
	public boolean canMove(int targetCol, int targetRow) {
		
		// If targeted King is in the square
		if(isWithinBoard(targetCol, targetRow)) {
			// Precol and Prerow are the king's previous position
			// This is a function to get the difference of the two numbers 
			// and adds them to see if the answer is 1 or not
			if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || 
					Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
				// If the king can reach the square and the square is confirmed valid
				// Then it can move
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
		
			}
		}
		return false;
	}

}
