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

}
