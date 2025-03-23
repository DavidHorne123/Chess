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

}
