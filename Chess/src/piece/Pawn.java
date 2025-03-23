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

}
