package game;

import javax.swing.JFrame;

import framework.Loader;

public class GameStarter {
	
	public static void main(String[] args){
		SplashScreen s = new SplashScreen("resources/Sprites_400/Splash/splash.png");
		s.showSplashScreen(true);
		
		/* Loads every sprite in the game */
		Loader loader = new Loader(1000/14);
		loader.loadAllSprites();
		
		JFrame window = new JFrame("Prince Of Persia");
		window.setContentPane(new Game());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
