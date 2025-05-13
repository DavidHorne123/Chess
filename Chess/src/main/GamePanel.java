package main;

import java.util.ArrayList;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
	
	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	// PIECES
	// Both ArrayLists contain the pieces currently on the board
	// The pieces array works as a backup list in case we want to reset the changes that 
	// the player made
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();	
	ArrayList<Piece> promoPieces = new ArrayList<>();
	// The piece that the player is currently holding
	Piece activeP, checkingP;
	public static Piece castlingP;
	
	// COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	// Sets the current color to white since white always starts
	int currentColor = WHITE;
	
	// BOOLEANS
	boolean canMove;
	boolean validSquare; 
	boolean promotion;
	boolean gameover;
	boolean stalemate;
	
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.black);
		// The program can detect the player's mouse movement or action
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setPieces();
		//testPromotion();
		// pass the pieces as the source
		// pass the simPieces as the target
		copyPieces(pieces, simPieces);
	}
	public void launchGame() {
		gameThread = new Thread(this);
		// stating a thread means calling this run method
		gameThread.start();
	}
	public void setPieces() {
		
		//White team
		// COLOR col, and row
		pieces.add(new Pawn(WHITE,0,6));
		pieces.add(new Pawn(WHITE,1,6));
		pieces.add(new Pawn(WHITE,2,6));
		pieces.add(new Pawn(WHITE,3,6));
		pieces.add(new Pawn(WHITE,4,6));
		pieces.add(new Pawn(WHITE,5,6));
		pieces.add(new Pawn(WHITE,6,6));
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new Rook(WHITE,0,7));
		pieces.add(new Rook(WHITE,7,7));
		pieces.add(new Knight(WHITE,1,7));
		pieces.add(new Knight(WHITE,6,7));
		pieces.add(new Bishop(WHITE,2,7));
		pieces.add(new Bishop(WHITE,5,7));
		pieces.add(new Queen(WHITE,3,7));
		pieces.add(new King(WHITE,4,7));
		
		//Black team
		pieces.add(new Pawn(BLACK,0,1));
		pieces.add(new Pawn(BLACK,1,1));
		pieces.add(new Pawn(BLACK,2,1));
		pieces.add(new Pawn(BLACK,3,1));
		pieces.add(new Pawn(BLACK,4,1));
		pieces.add(new Pawn(BLACK,5,1));
		pieces.add(new Pawn(BLACK,6,1));
		pieces.add(new Pawn(BLACK,7,1));
		pieces.add(new Rook(BLACK,0,0));
		pieces.add(new Rook(BLACK,7,0));
		pieces.add(new Knight(BLACK,1,0));
		pieces.add(new Knight(BLACK,6,0));
		pieces.add(new Bishop(BLACK,2,0));
		pieces.add(new Bishop(BLACK,5,0));
		pieces.add(new Queen(BLACK,3,0));
		pieces.add(new King(BLACK,4,0));
	}
	public void testPromotion() {
		pieces.add(new Pawn(WHITE, 0, 4));
		pieces.add(new Pawn(BLACK,5,4));
	}
	// This method receives two lists
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		
		// At first we clear this target list
		target.clear();
		// Then add everything in the source list to 
		// The target list
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		
		// GAME LOOP
		// The game loop is a sequence of processes that run
		// continuously as along as the game is runing
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		// Here we use System.nanoTime() to measure the elapsed time and call
		// update and repaint methods once every 1/60 of a second
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
		
	}
	
	private void update() {
		
		if(promotion) {
			promoting();
		}
		else if(gameover == false && stalemate == false){
			// MOUSE BUTTON PRESSED 
			if(mouse.pressed) {
				// check if activeP is null
				// Which means the player is not holding a piece
				if(activeP == null) {
					// We scan the simPieces arrayList
					for(Piece piece : simPieces) {
						// If one of these pieces has the same color as the current color
						if(piece.color == currentColor &&
								// And also same col as this mouse 
								// We get the col by dividing the mouse current x by the square size
								piece.col == mouse.x/Board.SQUARE_SIZE &&
								piece.row == mouse.y/Board.SQUARE_SIZE){
							// If one of these pieces has the same color and the same
							// col and the same row 
							// then that means the player's mouse is on this piece
							// So the player can pick it up 
							activeP = piece;
						}
					}
				}
				else {
					// If the player is holding a piece, stimulate the move
					stimulate();
				}
			}
			
			// MOUSE BUTTON RELEASED
			// If the player releases the mouse button
			if(mouse.pressed == false) {
				
				// When they are holding a piece
				if(activeP != null) {
					// If validSquare is true, then we update the position
					if(validSquare) {
						
						// Move confirmed
						
						// Update the piece list in case a piece has been captured and removed
						// during the simulation
						// passing the simPieces as the source and pieces as the target
						copyPieces(simPieces, pieces);
				
						
						// Calling the updatePosition method
						// And adjust its position
						activeP.updatePosition();
						if(castlingP != null) {
							castlingP.updatePosition();
						}
						
						if(isKingInCheck() && isCheckmate()) {
							gameover = true;
						}
						else if(isStalemate() && isKingInCheck() == false) {
							stalemate = true;
						}
						else { // The game is still going on
							if(canPromote()) {
								promotion = true;
							}
							else {
								changePlayer();
							}
						}	
					}	
					else {
						// The move is not valid so reset everything
						copyPieces(pieces, simPieces);
						// Make activeP null since the player released the piece
						activeP.resetPosition();
						activeP = null;
					}
				}
			}
		}
	}
	private void stimulate() {
		
		canMove = false;
		validSquare = false;
		
		// Reset the piece list in every loop
		// This is basically for restoring the removes piece during simulation
		copyPieces(pieces, simPieces);
		
		// Reset the castling piece's position
		if(castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
					castlingP = null;
		}

		// If a piece is being held, update its position
		// Here we update the activeP's x and y based on the player's mouse position
		// subtract half square size from the x and y
		activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
		// The color of the square that the active P is currently on changes
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		// Check if the piece is hovering over a reachable square
		if(activeP.canMove(activeP.col, activeP.row)) {
			
			canMove = true;
			
			// If hitting a piece, remove it from the list
			if(activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			checkCastling();
			
			if(isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
				validSquare = true;
			}
		}
	}
	private boolean opponentCanCaptureKing() {
		
		// We get the current color's king and pass false
		Piece king = getKing(false);
		
		// Scan the list and check if there is a piece that can move to the king's square
		for(Piece piece : simPieces) {
			if(piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}
		return false;
	}
	private boolean isIllegal(Piece king) {
	    // Check if the piece passed in is a king
	    if (king.type == Type.KING) {
	        // Iterate through all simulated pieces on the board
	        for (Piece piece : simPieces) {
	            // Check if there is a piece that is not king and has a different color 
	        	// and can move to the square where the king is trying to move to
	            if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {

	        
	                return true;
	            }
	        }
	    }
	    // If no opposing piece can capture the king, the move is legal
	    return false;
	}
	private boolean isKingInCheck() {
		
		// We want to get the opponent king so pass true here
		Piece king = getKing(true);
		
		// We check if the active piece can move to the square where the opponent king is
		if(activeP.canMove(king.col, king.row)) {
			// And if it can, this piece is checking the king
			checkingP = activeP;
			// This means the king is in check
			return true;
		}
		else {
			checkingP = null;
		}
		
		return false;
	}
	// This method finds the King in simPieces and returns it
	private Piece getKing(boolean opponent) {
		
		Piece king = null;
		
		// Finds the king in simPieces
		for(Piece piece : simPieces) {
			// If opponent parameter is true return the opponent king
			if(opponent) {
				if(piece.type == Type.KING && piece.color != currentColor) {
					king = piece;
				}
			}
			// and if this is false return your own king
			else {
				if(piece.type == Type.KING && piece.color == currentColor) {
					king = piece;
				}
			}
		}
		return king;
	}
	private boolean isCheckmate() {
		
		// Get the opponent king
		Piece king = getKing(true);
		
		// Call the kingCanMove method and pass the opponent king
		if(kingCanMove(king)) {
			return false;
		}
		else {
			// Check if you can block the attack with your piece
			
			// Check the position of the checking piece and the king in check
			// We get the col difference between checkingP and the king
			int colDiff = Math.abs(checkingP.col - king.col);
			// We get the row difference between checkingP and the king
			int rowDiff = Math.abs(checkingP.row - king.row);
			
			if(colDiff == 0) {
				// The checking piece is attacking vertically
				if(checkingP.row < king.row) {
					// The checking piece is above the king
					for(int row = checkingP.row; row < king.row; row++) {
						// We scan simPieces
						for(Piece piece : simPieces) {
							// if we find a piece then this is not checkmate yet
							if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					// The checking piece is below the king
					for(int row = checkingP.row; row > king.row; row--) {
						// We scan simPieces
						for(Piece piece : simPieces) {
							// if we find a piece then this is not checkmate yet
							if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}
			else if(rowDiff == 0) {
				// The checking piece is attacking horizontally
				if(checkingP.col < king.col) {
					// The checking piece is to the left
					for(int col = checkingP.col; col < king.col; col++) {
						// We scan simPieces
						for(Piece piece : simPieces) {
							// if we find a piece then this is not checkmate yet
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.col > king.col) {
					// The checking piece is to the right
					for(int col = checkingP.col; col > king.col; col--) {
						// We scan simPieces
						for(Piece piece : simPieces) {
							// if we find a piece then this is not checkmate yet
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}
			else if(colDiff == rowDiff) {
				// The checking piece is attacking diagonally
				if(checkingP.row < king.row) {
					// The checking piece is above the king
					if(checkingP.col < king.col) {
						// The checking piece is in the upper left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
							for(Piece piece : simPieces) {
								// If there is a piece that is not the king and not the same color
								// and can move to this location
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						// The checking piece is in the upper right
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col--, row++){
							for(Piece piece : simPieces) {
								// If there is a piece that is not the king and not the same color
								// and can move to this location
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					// The checking piece is below the king
					if(checkingP.col < king.col) {
						// The checking piece is in the lower left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
							for(Piece piece : simPieces) {
								// If there is a piece that is not the king and not the same color
								// and can move to this location
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						// The checking piece is in the lower right
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
							for(Piece piece : simPieces) {
								// If there is a piece that is not the king and not the same color
								// and can move to this location
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}
			else {
				// The checking piece is Knight
			}
		}
		
			
		return true;
	}
	private boolean kingCanMove(Piece king) {
		
		// Simulate if there is any square where the king can move to
		if(isValidMove(king, -1, -1)) {return true;}
		if(isValidMove(king, 0, -1)) {return true;}
		if(isValidMove(king, 1, -1)) {return true;}
		if(isValidMove(king, -1, 0)) {return true;}
		if(isValidMove(king, 1, 0)) {return true;}
		if(isValidMove(king, -1, 1)) {return true;}
		if(isValidMove(king, 0, 1)) {return true;}
		if(isValidMove(king, 1, 1)) {return true;}
		
		return false;
		
	}
	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
		
		boolean isValidMove = false;
		
		// Update the king's position for a second
		// Check if this is a safe spot or not
		king.col += colPlus;
		king.row += rowPlus;
		
		// We check if this king can move to this square
		if(king.canMove(king.col, king.row)) {
			
			// If it can check if it's hitting any piece
			if(king.hittingP != null) {
				// And if it is hitting a piece, remove it from the list
				simPieces.remove(king.hittingP.getIndex());
			}
			// Then check if the move is illegal or not
			// if the king can move to this square and the move is not illegal
			if(isIllegal(king) == false) {
				// Then this is a safe spot
				isValidMove = true;
			}
		}
		// Reset the king's position and restore the removed piece
		king.resetPosition();
		copyPieces(pieces, simPieces);
		
		return isValidMove;
	}
	private boolean isStalemate() {
		
		int count = 0;
		// Count the number of pieces
		for(Piece piece : simPieces) {
			if(piece.color != currentColor) {
				count++;
			}
		}
		
		// If only one piece (the king) is left
		if(count == 1) {
			// Call the kingCanMove and pass the opponent king
			if(kingCanMove(getKing(true)) == false) {
				return false;
			}
		}
		
		return false;
	}
	private void checkCastling() {
		
		if(castlingP != null) {
			// The castlingP's call is 0
			if(castlingP.col == 0) {
				// Which means it's the rook on the left
				// and in this case the rook moves by 3 squares
				castlingP.col += 3;
			}
			// If the col is 7, then it's the rook on the right
			else if(castlingP.col == 7) {
				// and it moves two squares
				castlingP.col -= 2;
			}
			// Update castlingP.x based on the col
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	private void changePlayer() {
		
		// If the currentColor is white then change it to Black
		if(currentColor == WHITE) {
			currentColor = BLACK;
			// Reset black's two stepped status
			for(Piece piece : pieces) {
				if(piece.color == BLACK) {
					// Disable all the twoStepped pieces to false
					piece.twoStepped = false;
				}
			}
		}
		// And if it's Black then change it to White
		else {
			currentColor = WHITE;
			// Reset white's two stepped status
			for(Piece piece : pieces) {
				if(piece.color == WHITE) {
					// Disable all the twoStepped pieces to false
					piece.twoStepped = false;
				}
			}
		}
		activeP = null;
	}
	private boolean canPromote() {
		
		if(activeP.type == Type.PAWN) {
			// If it's a white piece, check if its row is 0
			// If it's a black piece, check if its row is 7
			if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor,9,2));
				promoPieces.add(new Knight(currentColor,9,3));
				promoPieces.add(new Bishop(currentColor,9,4));
				promoPieces.add(new Queen(currentColor,9,5));
				return true;
			}
		}
		
		return false;
	}
	private void promoting() {
		// Check if mouse is pressed
		if(mouse.pressed) {
			for(Piece piece : promoPieces) {
				// If there is a piece that has the same column and row
				// as the mouse col and row
				// that means the mouse is on one of those pieces
				if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					// Using switch case to check the piece type
					switch(piece.type) {
					case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
					case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
					case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
					case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
					default: break;
					}
					// Then remove the pawn from the list
					simPieces.remove(activeP.getIndex());
					// Update the backup list too
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					// call the changePlayer function
					changePlayer();
				}
			}
		}
		
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		// BOARD
		board.draw(g2);
		
		// PIECES
		// enhanced for loop
		for(Piece p : simPieces) {
			p.draw(g2);
		}
		// If activeP is not null
		if(activeP != null) {
			if(canMove) {
				// If move is illegal or the opponent can capture the king
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					// Draw a rectangle
					// X is col 
					// Y is row
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					// First we set the color on graphics 2D
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					// Draw a rectangle
					// X is col 
					// Y is row
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}
			
			// Draw the active piece in the end so it won't be hidden by the board or the colored square
			activeP.draw(g2);
		}
		
		// STATUS MESSAGES
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if(promotion) {
			g2.drawString("Promote to:", 840, 150);
			// Scan the promoPieces list and draw the image one by one
			for(Piece piece: promoPieces) {
				// Get the X and Y coordinates and the image size
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
						Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's turn", 840, 550);
				// If checkingP is not null and the color is Black
				if(checkingP != null && checkingP.color == BLACK) {
					// Display this text in red
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 650);
					g2.drawString("is in check!", 840, 700);
				}
			}
			else {
				g2.drawString("Black's turn", 840, 250);
				if(checkingP != null && checkingP.color == WHITE) {
					// Display this text in red
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 100);
					g2.drawString("is in check", 840, 150);
				}
			}
		}
		
		if(gameover) {
			String s = "";
			if(currentColor == WHITE) {
				s = "White Wins";
			}
			else {
				s = "Black Wins";
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.green);
			g2.drawString(s, 200, 420);
		}
		if(stalemate) {
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.lightGray);
			g2.drawString("Stalemate", 200, 420);
		}
	}
}
