package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.List;
import model.GameObject;
import theme.GameTheme;

public class GameView extends JFrame {
    private GamePanel canvas;

    public GameView(GameTheme theme) {
        setTitle("Game Arena - " + theme.getName());
        setSize(1024, 768); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Buat canvas tempat menggambar
        canvas = new GamePanel(theme);
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
        private Image backgroundImage; // Simpan gambar background
        private int score, ammo, missed;

        public GamePanel(GameTheme theme) {
            this.theme = theme;
            
            // LOAD GAMBAR BACKGROUND DI SINI
            this.backgroundImage = theme.getBackgroundImage();
        }

        public void updateObjects(List<GameObject> objects, int s, int a, int m) {
            this.objects = objects;
            this.score = s;
            this.ammo = a;
            this.missed = m;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Bersihkan layar

            // 1. GAMBAR BACKGROUND (Paling Bawah)
            if (backgroundImage != null) {
                // Gambar full satu layar
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback kalau gambar gagal load: Pakai warna hitam
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // 2. Gambar Status (Skor/Peluru)
            // Kasih outline putih sedikit biar kebaca kalau background gelap
            g.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Bayangan teks (biar jelas)
            g.setColor(Color.BLACK);
            g.drawString("Skor: " + score, 12, 22);
            g.drawString("Peluru: " + ammo, 12, 42);
            g.drawString("Meleset: " + missed, 12, 62);
            
            // Teks Utama (Putih)
            g.setColor(Color.WHITE);
            g.drawString("Skor: " + score, 10, 20);
            g.drawString("Peluru: " + ammo, 10, 40);
            g.drawString("Meleset: " + missed, 10, 60);

            // 3. Gambar Semua Objek (Player, Alien, dll)
            if (objects != null) {
                for (GameObject obj : objects) {
                    Image img = null;

                    switch (obj.getType()) {
                        case "PLAYER":
                            img = theme.getPlayerImage();
                            break;
                        case "HUMAN": 
                            img = theme.getEnemyImage(); 
                            break;
                        case "OBSTACLE":
                            img = theme.getObstacleImage();
                            break;
                        default:
                            // Objek tanpa gambar (misal peluru)
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