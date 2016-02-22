package game;

import javax.swing.JFrame;

public class GameStarter {
	
	public static void main(String[] args){
		
		JFrame window = new JFrame("Prince Of Persia");
		window.setContentPane(new Game());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
	}

}
