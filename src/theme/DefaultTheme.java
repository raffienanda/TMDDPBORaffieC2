package theme;

import java.awt.Image;
import javax.swing.ImageIcon;

public class DefaultTheme implements GameTheme {
    @Override
    public String getName() {
        return "Alien Hometown";
    }

    @Override
    public Image getBackgroundImage() {
        // Pastikan kamu punya file ini atau ganti namanya
        return new ImageIcon("src/assets/images/AlienHometown_bg.png").getImage(); 
    }
}