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
	// The piece that the player is currently holding
	Piece activeP;
	
	// COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	// Sets the current color to white since white always starts
	int currentColor = WHITE;
	
	// BOOLEANS
	boolean canMove;
	boolean validSquare; 
	
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.black);
		// The program can detect the player's mouse movement or action
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setPieces();
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
		
		if(mouse.pressed) {
			// MOUSE BUTTON PRESSED 
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
					
					changePlayer();
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
	private void stimulate() {
		
		canMove = false;
		validSquare = false;
		
		// Reset the piece list in every loop
		// This is basically for restoring the removes piece during simulation
		copyPieces(pieces, simPieces);

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
			
			validSquare = true;
		}
		
	}
	private void changePlayer() {
		
		// If the currentColor is white then change it to Black
		if(currentColor == WHITE) {
			currentColor = BLACK;
		}
		// And if it's Black then change it to White
		else {
			currentColor = WHITE;
		}
		activeP = null;
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
			
			// Draw the active piece in the end so it won't be hidden by the board or the colored square
			activeP.draw(g2);
		}
		
		// STATUS MESSAGES
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if(currentColor == WHITE) {
			g2.drawString("White's turn", 840, 550);
		}
		else {
			g2.drawString("Black's turn", 840, 250);
		}
	}
	
}
