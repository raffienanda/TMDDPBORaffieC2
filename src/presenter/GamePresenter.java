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
    
    // --- TAMBAHAN BARU: Variabel Ledakan ---
    private boolean isExploding = false; // Status apakah sedang meledak
    private int explosionTimer = 0;      // Timer durasi ledakan
    private GameObject explosionObj;     // Objek visual ledakan
    // ---------------------------------------
    
    private boolean up, down, left, right;
    private int spawnTimer = 0;
    private Random random = new Random();

    public GamePresenter(String username, GameTheme theme, TBenefitModel dbModel) {
        this.dbModel = dbModel;
        this.view = new GameView(theme);
        this.view.addInputListener(this);

        // Setup Player
        player = new GameObject(487, 290, 40, 40, Color.BLUE, "PLAYER");

        if (dbModel.isUsernameExist(username)) {
            this.playerStats = new TBenefit(username, 0, 0, 0); 
        } else {
            this.playerStats = new TBenefit(username, 0, 0, 0);
        }

        generateObstacles();

        this.view.setVisible(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void generateObstacles() {
        obstacles.clear(); 
        int count = 5 + random.nextInt(4); 
        int attempts = 0; 
        
        while (obstacles.size() < count && attempts < 100) {
            int ox = random.nextInt(900);
            int oy = random.nextInt(500); 
            
            boolean safeFromPlayer = Math.abs(ox - player.getX()) > 60 || Math.abs(oy - player.getY()) > 60;
            boolean safeFromRocks = true;
            for (GameObject existingRock : obstacles) {
                if (Math.abs(ox - existingRock.getX()) < 60 && Math.abs(oy - existingRock.getY()) < 60) {
                    safeFromRocks = false;
                    break;
                }
            }
            
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
        // --- TAMBAHAN BARU: LOGIKA SAAT MELEDAK ---
        if (isExploding) {
            explosionTimer++;
            // Tunggu sekitar 60 frame (1 detik) sebelum Game Over beneran
            if (explosionTimer > 60) {
                finishGame(); // Panggil method baru untuk menutup game
            }
            return; // STOP UPDATE LAINNYA (Player gak bisa gerak, musuh diem)
        }
        // ------------------------------------------

        // 1. Gerakan Player
        int speed = 5;
        if (up && player.getY() > 0) player.setY(player.getY() - speed);
        if (down && player.getY() < 700) player.setY(player.getY() + speed);
        if (left && player.getX() > 0) player.setX(player.getX() - speed);
        if (right && player.getX() < 970) player.setX(player.getX() + speed);

        // 2. Spawn Alien
        spawnTimer++;
        if (spawnTimer > 100) { 
            int ax = random.nextInt(950);
            aliens.add(new GameObject(ax, 768, 40, 40, Color.RED, "HUMAN"));
            spawnTimer = 0;
        }

        // 3. Gerakan Alien
        for (GameObject alien : aliens) {
            alien.setY(alien.getY() - 1); 
            if (random.nextInt(100) < 2) { 
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
        GameObject bullet = new GameObject(bx, by, 10, 10, c, type);
        bullets.add(bullet);
    }
    
    private void playerShoot() {
        // Cek isExploding biar mayat gak bisa nembak
        if (playerStats.getSisaPeluru() > 0 && !isExploding) {
            shootBullet(player, "PLAYER_BULLET");
            playerStats.setSisaPeluru(playerStats.getSisaPeluru() - 1);
        } 
    }

    private void updateBullets() {
        Iterator<GameObject> it = bullets.iterator();
        while (it.hasNext()) {
            GameObject b = it.next();
            if (b.getType().equals("PLAYER_BULLET")) {
                b.setY(b.getY() + 7); 
            } else {
                b.setY(b.getY() - 5); 
            }

            if (b.getY() < 0 || b.getY() > 768) {
                if (b.getType().equals("ENEMY_BULLET")) {
                    playerStats.setPeluruMeleset(playerStats.getPeluruMeleset() + 1);
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

            // Cek Kena Batu
            for (GameObject rock : obstacles) {
                if (b.getBounds().intersects(rock.getBounds())) {
                    hit = true; 
                    break;
                }
            }

            // Cek Kena Player 
            if (!hit && b.getType().equals("ENEMY_BULLET")) {
                if (b.getBounds().intersects(player.getBounds())) {
                    // --- UBAH DI SINI: JANGAN LANGSUNG GAMEOVER ---
                    triggerExplosion(); 
                    hit = true; 
                    // ----------------------------------------------
                }
            }

            // Cek Kena Alien 
            if (!hit && b.getType().equals("PLAYER_BULLET")) {
                Iterator<GameObject> itAlien = aliens.iterator();
                while (itAlien.hasNext()) {
                    GameObject a = itAlien.next();
                    if (b.getBounds().intersects(a.getBounds())) {
                        playerStats.setSkor(playerStats.getSkor() + 10);
                        itAlien.remove(); 
                        hit = true;
                        break;
                    }
                }
            }

            if (hit) itBullet.remove();
        }
    }

    // --- METHOD BARU: MEMICU LEDAKAN ---
    private void triggerExplosion() {
        isExploding = true;
        // Buat objek ledakan tepat di posisi player
        // Ukuran 60x60 biar agak lebih gede dari player
        explosionObj = new GameObject(player.getX()-10, player.getY()-10, 60, 60, Color.ORANGE, "EXPLOSION");
        
        // Pindahkan player jauh keluar layar biar gak kelihatan
        player.setX(-1000); 
    }

    // --- METHOD BARU: MENYELESAIKAN GAME (PISAH DARI GAMEOVER) ---
    private void finishGame() {
        isRunning = false;
        JOptionPane.showMessageDialog(view, "GAME OVER!\nSkor: " + playerStats.getSkor());
        
        dbModel.updateOrInsert(playerStats);
        
        view.dispose();
        
        System.out.println("Kembali ke Lobby...");
        MenuView menu = new MenuView();          
        new MenuPresenter(menu, dbModel);        
    }

    private void draw() {
        List<GameObject> allObjs = new ArrayList<>();
        
        // Kalau belum meledak, gambar player. Kalau meledak, gambar ledakan.
        if (!isExploding) {
            allObjs.add(player);
        } else {
            if (explosionObj != null) {
                allObjs.add(explosionObj);
            }
        }
        
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
        // Kalau lagi meledak, input dimatikan
        if (isExploding) return; 

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
        
        if (code == KeyEvent.VK_Z) {
            playerShoot();
        }
        
        if (code == KeyEvent.VK_SPACE) {
             finishGame(); // Quit manual
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