package theme;

import java.awt.Image;
import javax.swing.ImageIcon;

public class DarkTheme implements GameTheme {
    @Override
    public String getName() {
        return "Alien Burning Homeland";
    }

    @Override
    public Image getBackgroundImage() {
        // Pastikan kamu punya file ini atau ganti namanya
        return new ImageIcon("src/assets/images/BurningHomeland_bg.png").getImage(); 
    }
}