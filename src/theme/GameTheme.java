package theme;

import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;

public interface GameTheme {
    String getName();
    Color getBackgroundColor();
    
    // Tambahan method untuk ambil gambar
    default Image getPlayerImage() {
        return new ImageIcon("src/assets/alien.png").getImage();
    }
    
    default Image getEnemyImage() {
        return new ImageIcon("src/assets/human.png").getImage();
    }
    
    default Image getObstacleImage() {
        return new ImageIcon("src/assets/rock.png").getImage();
    }
}