package piece;

import main.GamePanel;

public class Pawn extends Piece{

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		// If the pawn's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-pawn");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-pawn");
		}
		
	}
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow) == false) {
			
			// Define the move value based on its color
			int moveValue;
			// If the color is white, it's going up
			if(color == GamePanel.WHITE) {
				// so the moveValue is -1
				moveValue = -1;
			}
			else {
				// if it's black, it's going down
				// so the moveValue is +1
				moveValue = 1;
			}
			
			// Check the hitting piece
			hittingP = getHittingP(targetCol,targetRow);
			
			// 1 square movement
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			// 2 squares movement
			// We multiply the moveValue by 2 because it's moving by 2 squares
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false &&
					pieceIsOnStraightLine(targetCol,targetRow) == false) {
				return true;
			}
			// Diagonal movement & Capture ( if a piece is on a square diagonally in front of it)
			// When a pawn can capture a piece it moves to the left or right by 1 square
			// so the col difference needs to be 1
			// and the targetRow needs to be preRow +1 or -1(moveValue)
			// if the piece color is not the same color then it can move and capture it
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
					hittingP.color != color) {
				return true;
			}
			// En Passant
			
			
		}
		return false;
	}

}
