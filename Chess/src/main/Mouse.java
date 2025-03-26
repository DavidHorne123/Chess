package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Mouse extends MouseAdapter{
	
	public int x, y;
	public boolean pressed;
	
	// If the player pressed their mouse button set pressed to true
	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
		
	}
	// If the player releases their mouse button set pressed to false
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
		
	}
	// Mouse dragged can track the player's movement
	// The e allows us to get the player's mouse current position
	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	// MouseMoved can track the player's movement
	// The e allows us to get the player's mouse current position
	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

}

