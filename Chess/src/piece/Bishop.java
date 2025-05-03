package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.BISHOP;
		
		// If the bishop's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-bishop");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-bishop");
		}
	}
	// Create canMove method
	public boolean canMove(int targetCol, int targetRow) {
		// check if it's within the board and not the samesquare
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// If you want to move diagonally the col difference and the row difference
			// Always need to be equal
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				// Check if the target square is valid
				if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}

}
