package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;

public class Piece {

		public BufferedImage image;
		public int x, y;
		public int col, row, preCol, preRow;
		public int color;
		
		public Piece(int color, int col, int row) {
			
			this.color = color;
			this.col = col;
			this.row = row;
			x = getX(col);
			y = getY(row);
			preCol = col;
			preRow = row;
		}
		// This method gets image path
		public BufferedImage getImage(String imagePath) {
			
			BufferedImage image = null;
			
			try {
				// Based on the path get image
				image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
				
			}catch(IOException e) {
				e.printStackTrace();
			}
			return image;
		}
		public int getX(int col) {
			return col * Board.SQUARE_SIZE;
		}
		public int getY(int row) {
			return row * Board.SQUARE_SIZE;
		}
		// Added Half square size to x to detect its col based on the center point of the piece
		public int getCol(int x) {
			return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
		// Added Half square size to y to detect its row based on the center point of the piece
		}
		public int getRow(int y) {
			return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
		}
		public void updatePosition() {
			
			// We update its X and Y based on its current col and row
			// This adjusts its position and places it at the center of the square
			x = getX(col);
			y = getY(row);
			// We update the previous col and previous row since the move has been confirmed
			// And the piece has moved to a new square
			preCol = getCol(x);
			preRow = getRow(y);
		}
		public boolean canMove(int targetCol, int targetRow) {
			return false;
		}
		public void draw(Graphics2D g2) {
			// This draws the image and the x and y and width and height
			g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
		}
}
