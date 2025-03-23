package main;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		

		JFrame window = new JFrame("Chess"); // creates JFrame
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // stops program when closed
		window.setResizable(false);  		// We set Resizable to false so you cant resize the window
		
		// Add GamePanel to the window
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack(); // By packing like this the window adjusts its size to this GamePanel
		
		window.setLocationRelativeTo(null); 		// The window will show up at the center of your monitor
		window.setVisible(true);
		
		gp.launchGame();
	}

}
