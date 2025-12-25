package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.List;
import model.GameObject;
import theme.GameTheme;

/**
 * GameView: Kelas ini bertugas menangani tampilan visual (GUI) permainan.
 * Kelas ini merupakan "Wadah" (Container) utama berbentuk Window/Jendela.
 * Menggunakan pola MVP: View ini pasif, hanya menerima perintah "render" dari Presenter.
 */
public class GameView extends JFrame {
    private GamePanel canvas; // Panel khusus untuk menggambar (Canvas)

    // UPDATE CONSTRUCTOR: Terima avatarImgName
    public GameView(GameTheme theme, String avatarImgName) {
        // --- 1. SETUP WINDOW ---
        setTitle("Game Arena - " + theme.getName()); // Judul window sesuai tema
        setSize(1024, 768);                          // Ukuran window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Tombol X untuk tutup aplikasi
        setLocationRelativeTo(null);                 // Posisi tengah layar
        setResizable(false);                         // Ukuran tidak bisa diubah user

        // --- 2. SETUP KANVAS GAMBAR ---
        // Oper tema dan nama avatar ke panel untuk diload gambarnya
        canvas = new GamePanel(theme, avatarImgName);
        add(canvas); // Tempelkan kanvas ke dalam window
    }

    /**
     * Method ini dipanggil terus-menerus oleh Game Loop di Presenter (60x detik).
     * Tugasnya menerima data terbaru (posisi objek, skor, dll) lalu menyuruh kanvas menggambar ulang.
     */
    public void render(List<GameObject> objects, int score, int ammo, int missed) {
        canvas.updateObjects(objects, score, ammo, missed); // Update data di kanvas
        canvas.repaint(); // Memaksa komputer memanggil paintComponent() segera
    }

    /**
     * Menghubungkan input keyboard dari user ke Presenter.
     * View tidak mengolah input, hanya meneruskannya.
     */
    public void addInputListener(KeyListener k) {
        this.addKeyListener(k);
    }

    // --- INNER CLASS: Area Menggambar ---
    /**
     * GamePanel: Area kerja sebenarnya. Di sinilah gambar pixel demi pixel dilukis.
     * Menggunakan JPanel dan meng-override method paintComponent.
     */
    private class GamePanel extends JPanel {
        // Data yang perlu digambar (Disuplai oleh render())
        private List<GameObject> objects;
        private GameTheme theme;
        
        // Aset Gambar
        private Image backgroundImage; 
        private Image playerImage; // Image khusus Player (bisa custom)
        
        // Data Status (HUD)
        private int score, ammo, missed;

        public GamePanel(GameTheme theme, String avatarImgName) {
            this.theme = theme;
            this.backgroundImage = theme.getBackgroundImage();
            
            // --- LOAD GAMBAR PLAYER ---
            // Logika: Coba load gambar pilihan user. Jika gagal, pakai default tema.
            try {
                // Coba load file dari folder src/assets/images/
                ImageIcon icon = new ImageIcon("src/assets/images/" + avatarImgName);
                
                // Cek apakah gambar berhasil dimuat
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    this.playerImage = icon.getImage();
                } else {
                    // Jika file tidak ketemu/rusak, log error dan pakai default
                    System.out.println("Gagal load: " + avatarImgName + ". Pakai default.");
                    this.playerImage = theme.getPlayerImage(); 
                }
            } catch (Exception e) {
                // Safety net jika terjadi error lain
                this.playerImage = theme.getPlayerImage();
            }
        }

        // Menerima update data dari GameView.render()
        public void updateObjects(List<GameObject> objects, int s, int a, int m) {
            this.objects = objects;
            this.score = s;
            this.ammo = a;
            this.missed = m;
        }

        /**
         * METHOD UTAMA PENGGAMBARAN
         * Method ini dipanggil otomatis oleh Java saat repaint().
         * Di sini kita melukis layer demi layer (Background -> Objek -> UI).
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Bersihkan sisa gambar frame sebelumnya

            // LAYER 1: GAMBAR BACKGROUND
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback jika tidak ada gambar background (Layar Hitam)
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // LAYER 2: GAMBAR STATUS (HUD - Heads Up Display)
            g.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Trik membuat efek bayangan (Shadow) pada teks agar terbaca di background terang
            g.setColor(Color.BLACK); // Warna bayangan
            g.drawString("Skor: " + score, 12, 22);    // Geser dikit (+2px)
            g.drawString("Peluru: " + ammo, 12, 42);
            g.drawString("Meleset: " + missed, 12, 62);
            
            // Teks Utama (Warna Putih)
            g.setColor(Color.WHITE);
            g.drawString("Skor: " + score, 10, 20);    // Posisi asli
            g.drawString("Peluru: " + ammo, 10, 40);
            g.drawString("Meleset: " + missed, 10, 60);

            // LAYER 3: GAMBAR SEMUA OBJEK GAME
            // Loop semua objek (Player, Musuh, Peluru, Batu) dan gambar sesuai posisinya
            if (objects != null) {
                for (GameObject obj : objects) {
                    Image img = null;

                    // Tentukan gambar berdasarkan tipe objek
                    switch (obj.getType()) {
                        case "PLAYER":
                            img = this.playerImage; // Gunakan gambar yang sudah di-load di konstruktor
                            break;
                        case "HUMAN": 
                            img = theme.getEnemyImage(); // Ambil dari tema
                            break;
                        case "OBSTACLE":
                            img = theme.getObstacleImage();
                            break;
                        case "EXPLOSION":
                            img = theme.getExplosionImage();
                            break;
                        default:
                            // Jika tipe objek tidak punya gambar (misal peluru sederhana), gambar kotak warna
                            g.setColor(obj.getColor());
                            g.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                            continue; // Lanjut ke objek berikutnya
                    }

                    // Jika ada gambarnya, lukis gambar tersebut (draw image)
                    if (img != null) {
                        g.drawImage(img, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), null);
                    }
                }
            }
        }
    }
}