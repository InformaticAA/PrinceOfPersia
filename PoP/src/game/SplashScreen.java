package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {
    private static final long serialVersionUID = 1L;

    Image image;
    ImageIcon icon;

    boolean loaded = false;

    public SplashScreen(String path) {
        try {
            image = Toolkit.getDefaultToolkit().getImage(path);
            icon = new ImageIcon(image);
            setSize(icon.getIconWidth(), icon.getIconHeight());
            setLocationRelativeTo(null);
            setBackground(new Color(0, 255, 0, 0));
            loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSplashScreen(boolean visible) {
        setVisible(visible);
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}