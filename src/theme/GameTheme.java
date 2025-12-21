package theme;

import java.awt.Image;
import javax.swing.ImageIcon;

public interface GameTheme {
    String getName();
    
    // GANTI: Dari Color ke Image
    Image getBackgroundImage(); 
    
    // Method gambar aset lain
    default Image getPlayerImage() {
        return new ImageIcon("src/assets/images/alien.png").getImage();
    }
    
    default Image getEnemyImage() {
        return new ImageIcon("src/assets/images/human.png").getImage();
    }
    
    default Image getObstacleImage() {
        return new ImageIcon("src/assets/images/rock.png").getImage();
    }
}