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
        setSize(1024, 768); // Ukuran arena
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Buat canvas tempat menggambar
        canvas = new GamePanel(theme);
        add(canvas);
    }

    // Method agar Presenter bisa minta update tampilan
    public void render(List<GameObject> objects, int score, int ammo, int missed) {
        canvas.updateObjects(objects, score, ammo, missed);
        canvas.repaint(); // Ini akan memicu paintComponent di bawah
    }

    // Method untuk menyambungkan Keyboard Input
    public void addInputListener(KeyListener k) {
        this.addKeyListener(k);
    }

    // --- INNER CLASS: Area Menggambar ---
    private class GamePanel extends JPanel {
        private List<GameObject> objects;
        private GameTheme theme;
        private int score, ammo, missed;

        public GamePanel(GameTheme theme) {
            this.theme = theme;
            this.setBackground(theme.getBackgroundColor());
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

            // 1. Gambar Status (Skor/Peluru)
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Skor: " + score, 10, 20);
            g.drawString("Peluru: " + ammo, 10, 40);
            g.drawString("Meleset: " + missed, 10, 60);

            // 2. Gambar Semua Objek (Player, Alien, dll)
            if (objects != null) {
                for (GameObject obj : objects) {
                    Image img = null;

                    // Cek tipe objek untuk menentukan gambar mana yang dipakai
                    switch (obj.getType()) {
                        case "PLAYER":
                            // Player (Alien) tetap pakai getPlayerImage
                            img = theme.getPlayerImage();
                            break;

                        case "HUMAN": // <-- GANTI CASE INI DARI "ALIEN" JADI "HUMAN"
                            // Musuh (Manusia) pakai gambar musuh
                            img = theme.getEnemyImage(); // Sesuaikan nama method di interface tadi
                            break;

                        case "OBSTACLE":
                            img = theme.getObstacleImage();
                            break;
                        default:
                            // Untuk peluru, kita tetap pakai kotak warna saja biar performa ringan
                            g.setColor(obj.getColor());
                            g.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                            continue; // Skip drawImage di bawah
                    }

                    if (img != null) {
                        g.drawImage(img, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), null);
                    }
                }
            }
        }
    }
}