package piece;

import main.GamePanel;

public class Bishop extends Piece{

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		// If the bishop's color is White use the white image
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-bishop");
		}
		// but if it's Black use the black image
		else {
			image = getImage("/piece/b-bishop");
		}
	}

}
