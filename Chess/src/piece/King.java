package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece{

	public King(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.KING;
		
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
			
			// CASTLING
			if(moved == false) {
				
				// Right castling
				if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol,targetRow) == false) {
					// We scan simPices
					for(Piece piece : GamePanel.simPieces) {
						// And if there is a piece that is 3 squares to the right, on the same row,
						// and hasn't moved
						// If it hasn't moved this means this is rook
						if(piece.col == preCol+3 && piece.row == preRow && piece.moved == false) {
							// We get this piece as the castlingP 
							GamePanel.castlingP = piece;
							return true;
						}
					}
				}
				
				
				// Left castling
				if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol,targetRow) == false) {
					Piece p[] = new Piece[2];
					// We scan simPieces
					for(Piece piece : GamePanel.simPieces) {
						// If there is a piece with col-3 and the same row
						if(piece.col == preCol-3 && piece.row == targetRow) {
							// Then put the piece in slot 0
							p[0] = piece;
						}
						// If there is a pice with col-4 and the same square
						if(piece.col == preCol-4 && piece.row == targetRow) {
							// Then put it in slot 1
							p[1] = piece;
						}
						
						if(p[0] == null && p[1] != null && p[1].moved == false) {
							GamePanel.castlingP = p[1];
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}

}
