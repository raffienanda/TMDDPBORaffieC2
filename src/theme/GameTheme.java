package theme;

import java.awt.Image;
import javax.swing.ImageIcon;

public interface GameTheme {
    String getName();
    Image getBackgroundImage(); 
    
    default Image getPlayerImage() {
        return new ImageIcon("src/assets/images/alien.png").getImage();
    }
    
    default Image getEnemyImage() {
        return new ImageIcon("src/assets/images/human.png").getImage();
    }
    
    default Image getObstacleImage() {
        return new ImageIcon("src/assets/images/rock.png").getImage();
    }

    // --- TAMBAHAN BARU ---
    default Image getExplosionImage() {
        return new ImageIcon("src/assets/images/explosion.png").getImage();
    }
}