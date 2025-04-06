package piece;

import main.GamePanel;

public class Rook extends Piece{

	public Rook(int color, int col, int row) {
		super(color, col, row);
		// If the Rook's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-rook");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-rook");
		}
	}
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Rook can move as long as either its col or row is the same
			if(targetCol == preCol || targetRow == preRow) {
				// We check if the targeted square is valid or not
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
	

}
