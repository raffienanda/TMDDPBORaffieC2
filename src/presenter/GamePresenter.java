package presenter;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import model.GameObject;
import model.TBenefit;
import model.TBenefitModel;
import theme.GameTheme;
import view.GameView;
import view.MenuView;

public class GamePresenter implements Runnable, KeyListener {
    private GameView view;
    private TBenefitModel dbModel;
    private TBenefit playerStats;
    private Thread gameThread;
    private boolean isRunning = true;

    // Objek Game
    private GameObject player;
    private List<GameObject> aliens = new ArrayList<>();
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> obstacles = new ArrayList<>();
    
    // Variabel Bantu
    private boolean up, down, left, right;
    private int spawnTimer = 0;      // Timer untuk spawn alien
    private Random random = new Random();

    public GamePresenter(String username, GameTheme theme, TBenefitModel dbModel) {
        this.dbModel = dbModel;
        this.view = new GameView(theme);
        this.view.addInputListener(this);

        // 1. Setup Player (Tengah)
        player = new GameObject(497, 600, 70, 70, Color.BLUE, "PLAYER");

        // 2. Setup Data Pemain (Reset Skor & Meleset, Sisa Peluru diambil dari DB)
        if (dbModel.isUsernameExist(username)) {
            // Ambil sisa peluru terakhir dari DB (implementasi ini perlu query select spesifik, 
            // tapi untuk simplifikasi kita anggap load data di sini)
            // Asumsi: Method getBenefitByUsername ada di Model (atau pakai default dulu)
            this.playerStats = new TBenefit(username, 0, 0, 0); // Awal main peluru 0 dulu sesuai aturan tantangan
        } else {
            this.playerStats = new TBenefit(username, 0, 0, 0);
        }

        // 3. Generate Batu Pelindung (Acak)
        generateObstacles();

        this.view.setVisible(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void generateObstacles() {
        obstacles.clear(); // Pastikan list bersih dulu
        int count = 5 + random.nextInt(4); // Mau bikin 5-8 batu
        int attempts = 0; // Penjaga biar gak infinite loop kalau layar penuh
        
        while (obstacles.size() < count && attempts < 100) {
            int ox = random.nextInt(900);
            int oy = random.nextInt(500); 
            
            // 1. Cek apakah menimpa Player?
            // (Jarak X > 50 ATAU Jarak Y > 50 berarti aman, tidak kena)
            boolean safeFromPlayer = Math.abs(ox - player.getX()) > 60 || Math.abs(oy - player.getY()) > 60;
            
            // 2. Cek apakah menimpa Batu Lain?
            boolean safeFromRocks = true;
            for (GameObject existingRock : obstacles) {
                // Kalau jarak X dekat DAN jarak Y dekat, berarti numpuk
                if (Math.abs(ox - existingRock.getX()) < 60 && Math.abs(oy - existingRock.getY()) < 60) {
                    safeFromRocks = false;
                    break;
                }
            }
            
            // Kalau aman dari Player DAN aman dari Batu lain, baru tambahkan
            if (safeFromPlayer && safeFromRocks) {
                obstacles.add(new GameObject(ox, oy, 50, 50, Color.GRAY, "OBSTACLE"));
            }
            
            attempts++;
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (isRunning) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                draw();
                delta--;
            }
        }
    }

    private void update() {
        // 1. Gerakan Player
        int speed = 5;
        if (up && player.getY() > 0) player.setY(player.getY() - speed);
        if (down && player.getY() < 708) player.setY(player.getY() + speed);
        if (left && player.getX() > 0) player.setX(player.getX() - speed);
        if (right && player.getX() < 984) player.setX(player.getX() + speed);

        // 2. Spawn Alien (Dari Bawah)
        spawnTimer++;
        if (spawnTimer > 100) { // Setiap ~1.5 detik
            int ax = random.nextInt(974);
            // Alien muncul di Y=550 (Bawah) bergerak ke atas/random
            aliens.add(new GameObject(ax, 718, 40, 40, Color.RED, "HUMAN"));
            spawnTimer = 0;
        }

        // 3. Gerakan Alien (Naik perlahan)
        for (GameObject alien : aliens) {
            alien.setY(alien.getY() - 1); // Bergerak ke atas
            
            // Alien Menembak Acak ke arah Player
            if (random.nextInt(100) < 2) { // 2% chance per frame
                shootBullet(alien, "ENEMY_BULLET");
            }
        }

        // 4. Update Peluru
        updateBullets();
        
        // 5. Cek Tabrakan
        checkCollisions();
    }

    private void shootBullet(GameObject shooter, String type) {
        int bx = shooter.getX() + shooter.getWidth() / 2;
        int by = shooter.getY();
        Color c = type.equals("PLAYER_BULLET") ? Color.YELLOW : Color.MAGENTA;
        
        // Arah peluru
        // Jika player nembak -> ke bawah/arah alien (disimpulkan ke bawah/ke arah alien terdekat)
        // Jika alien nembak -> ke atas/arah player
        GameObject bullet = new GameObject(bx, by, 10, 10, c, type);
        bullets.add(bullet);
    }
    
    private void playerShoot() {
        if (playerStats.getSisaPeluru() > 0) {
            shootBullet(player, "PLAYER_BULLET");
            playerStats.setSisaPeluru(playerStats.getSisaPeluru() - 1);
        } 
    }

    private void updateBullets() {
        Iterator<GameObject> it = bullets.iterator();
        while (it.hasNext()) {
            GameObject b = it.next();
            
            // Gerakan Peluru
            if (b.getType().equals("PLAYER_BULLET")) {
                b.setY(b.getY() + 7); // Player nembak ke bawah (asumsi alien di bawah)
            } else {
                // Alien nembak ke arah player (simplifikasi: lurus ke atas dulu biar mudah)
                b.setY(b.getY() - 5); 
            }

            // Hapus jika keluar layar
            if (b.getY() < 0 || b.getY() > 768) {
                if (b.getType().equals("ENEMY_BULLET")) {
                    // Jika peluru alien keluar layar -> Player dapat bonus peluru
                    playerStats.setPeluruMeleset(playerStats.getPeluruMeleset() + 1);
                    // Mekanisme Bonus: Tiap 1 peluru meleset -> Tambah 1 peluru pemain
                    playerStats.setSisaPeluru(playerStats.getSisaPeluru() + 1);
                }
                it.remove();
            }
        }
    }

    private void checkCollisions() {
        Iterator<GameObject> itBullet = bullets.iterator();
        while (itBullet.hasNext()) {
            GameObject b = itBullet.next();
            boolean hit = false;

            // Cek Kena Batu (Obstacle)
            for (GameObject rock : obstacles) {
                if (b.getBounds().intersects(rock.getBounds())) {
                    hit = true; // Peluru hancur kena batu
                    break;
                }
            }

            // Cek Kena Player (Game Over)
            if (!hit && b.getType().equals("ENEMY_BULLET")) {
                if (b.getBounds().intersects(player.getBounds())) {
                    gameOver();
                    return;
                }
            }

            // Cek Kena Alien (Skor Nambah)
            if (!hit && b.getType().equals("PLAYER_BULLET")) {
                Iterator<GameObject> itAlien = aliens.iterator();
                while (itAlien.hasNext()) {
                    GameObject a = itAlien.next();
                    if (b.getBounds().intersects(a.getBounds())) {
                        playerStats.setSkor(playerStats.getSkor() + 10);
                        itAlien.remove(); // Alien mati
                        hit = true;
                        break;
                    }
                }
            }

            if (hit) itBullet.remove();
        }
    }

    private void gameOver() {
        isRunning = false;
        JOptionPane.showMessageDialog(view, "GAME OVER!\nSkor: " + playerStats.getSkor());
        
        // 1. Simpan Data ke Database
        dbModel.updateOrInsert(playerStats);
        
        // 2. Tutup Jendela Game
        view.dispose();
        
        // 3. --- LOGIKA BALIK KE LOBBY ---
        System.out.println("Kembali ke Lobby...");
        MenuView menu = new MenuView();          // Bikin tampilan menu baru
        new MenuPresenter(menu, dbModel);        // Jalankan logika menu
    }

    private void draw() {
        List<GameObject> allObjs = new ArrayList<>();
        allObjs.add(player);
        allObjs.addAll(obstacles);
        allObjs.addAll(aliens);
        allObjs.addAll(bullets);
        
        view.render(allObjs, playerStats.getSkor(), playerStats.getSisaPeluru(), playerStats.getPeluruMeleset());
    }

    // Input Handling
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
        
        // Nembak pakai Spasi (jika peluru ada)
        // Atau pakai tombol lain misal Z, karena Spasi di PDF buat Pause/Quit
        if (code == KeyEvent.VK_Z) {
            playerShoot();
        }
        
        // Tombol Spasi: Pause/Quit ke Menu
        if (code == KeyEvent.VK_SPACE) {
             gameOver(); // Sementara anggap quit = save & exit
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = false;
    }
}