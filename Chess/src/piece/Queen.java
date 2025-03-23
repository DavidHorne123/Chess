package piece;

import main.GamePanel;

public class Queen extends Piece{

	public Queen(int color, int col, int row) {
		super(color, col, row);
		// If the queen's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-queen");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-queen");
		}
	}

}
