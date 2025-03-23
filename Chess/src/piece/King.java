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

}
