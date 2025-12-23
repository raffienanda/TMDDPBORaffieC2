package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.List;
import model.GameObject;
import theme.GameTheme;

public class GameView extends JFrame {
    private GamePanel canvas;

    // UPDATE CONSTRUCTOR: Terima avatarImgName
    public GameView(GameTheme theme, String avatarImgName) {
        setTitle("Game Arena - " + theme.getName());
        setSize(1024, 768); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Oper nama file ke panel
        canvas = new GamePanel(theme, avatarImgName);
        add(canvas);
    }

    public void render(List<GameObject> objects, int score, int ammo, int missed) {
        canvas.updateObjects(objects, score, ammo, missed);
        canvas.repaint(); 
    }

    public void addInputListener(KeyListener k) {
        this.addKeyListener(k);
    }

    // --- INNER CLASS: Area Menggambar ---
    private class GamePanel extends JPanel {
        private List<GameObject> objects;
        private GameTheme theme;
        private Image backgroundImage; 
        private Image playerImage; // <--- Image khusus Player
        private int score, ammo, missed;

        public GamePanel(GameTheme theme, String avatarImgName) {
            this.theme = theme;
            this.backgroundImage = theme.getBackgroundImage();
            
            // LOAD GAMBAR SESUAI PILIHAN
            try {
                // Coba load file yang dipilih
                ImageIcon icon = new ImageIcon("src/assets/images/" + avatarImgName);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    this.playerImage = icon.getImage();
                } else {
                    // Jika file tidak ketemu/gagal, pakai default
                    System.out.println("Gagal load: " + avatarImgName + ". Pakai default.");
                    this.playerImage = theme.getPlayerImage(); 
                }
            } catch (Exception e) {
                this.playerImage = theme.getPlayerImage();
            }
        }

        public void updateObjects(List<GameObject> objects, int s, int a, int m) {
            this.objects = objects;
            this.score = s;
            this.ammo = a;
            this.missed = m;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 

            // 1. GAMBAR BACKGROUND
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // 2. Gambar Status
            g.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Bayangan
            g.setColor(Color.BLACK);
            g.drawString("Skor: " + score, 12, 22);
            g.drawString("Peluru: " + ammo, 12, 42);
            g.drawString("Meleset: " + missed, 12, 62);
            
            // Teks Utama
            g.setColor(Color.WHITE);
            g.drawString("Skor: " + score, 10, 20);
            g.drawString("Peluru: " + ammo, 10, 40);
            g.drawString("Meleset: " + missed, 10, 60);

            // 3. Gambar Semua Objek
            if (objects != null) {
                for (GameObject obj : objects) {
                    Image img = null;

                    switch (obj.getType()) {
                        case "PLAYER":
                            img = this.playerImage; // <--- PAKAI IMAGE CUSTOM
                            break;
                        case "HUMAN": 
                            img = theme.getEnemyImage(); 
                            break;
                        case "OBSTACLE":
                            img = theme.getObstacleImage();
                            break;
                        case "EXPLOSION":
                            img = theme.getExplosionImage();
                            break;
                        default:
                            g.setColor(obj.getColor());
                            g.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                            continue; 
                    }

                    if (img != null) {
                        g.drawImage(img, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), null);
                    }
                }
            }
        }
    }
}