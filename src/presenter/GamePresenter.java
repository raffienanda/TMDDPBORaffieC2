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

    // --- OBJEK GAME (UPDATE PENAMAAN) ---
    private GameObject player; // Player = Alien
    private List<GameObject> humans = new ArrayList<>(); // Bot = Manusia
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> obstacles = new ArrayList<>();
    
    // Variabel Ledakan
    private boolean isExploding = false; 
    private int explosionTimer = 0;      
    private GameObject explosionObj;     
    
    private boolean up, down, left, right;
    private int spawnTimer = 0;      
    private Random random = new Random();

    public GamePresenter(String username, GameTheme theme, TBenefitModel dbModel) {
        this.dbModel = dbModel;
        this.view = new GameView(theme);
        this.view.addInputListener(this);

        // 1. Setup Player (Alien)
        player = new GameObject(487, 290, 40, 40, Color.BLUE, "PLAYER");

        // 2. Setup Data Pemain
        if (dbModel.isUsernameExist(username)) {
            this.playerStats = new TBenefit(username, 0, 0, 0); 
        } else {
            this.playerStats = new TBenefit(username, 0, 0, 0);
        }

        // 3. Generate Batu
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
        if (isExploding) {
            explosionTimer++;
            if (explosionTimer > 60) {
                finishGame(); 
            }
            return; 
        }

        // 1. Gerakan Player (Alien)
        int speed = 5;
        if (up && player.getY() > 0) player.setY(player.getY() - speed);
        if (down && player.getY() < 700) player.setY(player.getY() + speed);
        if (left && player.getX() > 0) player.setX(player.getX() - speed);
        if (right && player.getX() < 970) player.setX(player.getX() + speed);

        // 2. Spawn Manusia (Bot) dari Bawah
        spawnTimer++;
        if (spawnTimer > 100) { 
            int hx = random.nextInt(950);
            humans.add(new GameObject(hx, 768, 40, 40, Color.RED, "HUMAN"));
            spawnTimer = 0;
        }

        // 3. Gerakan Manusia (Zig-Zag)
        for (GameObject human : humans) {
            human.setY(human.getY() - 1); // Jalan ke atas
            
            // Logika Zig-Zag
            int zigzag = (int) (Math.sin(human.getY() * 0.05) * 3);
            int newX = human.getX() + zigzag;
            
            if (newX > 0 && newX < 970) {
                human.setX(newX);
            }
            
            // Manusia Nembak
            if (random.nextInt(100) < 2) { 
                shootBullet(human, "ENEMY_BULLET");
            }
        }

        updateBullets();
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
                    // Jika peluru musuh (Manusia) keluar layar, Player dapat bonus
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

            // Cek Peluru Manusia Kena Player (Alien)
            if (!hit && b.getType().equals("ENEMY_BULLET")) {
                if (b.getBounds().intersects(player.getBounds())) {
                    triggerExplosion(); 
                    hit = true; 
                }
            }

            // Cek Peluru Player Kena Manusia
            if (!hit && b.getType().equals("PLAYER_BULLET")) {
                Iterator<GameObject> itHuman = humans.iterator(); // Ganti nama iterator
                while (itHuman.hasNext()) {
                    GameObject human = itHuman.next(); // Ganti nama variabel
                    if (b.getBounds().intersects(human.getBounds())) {
                        playerStats.setSkor(playerStats.getSkor() + 10);
                        itHuman.remove(); // Manusia mati
                        hit = true;
                        break;
                    }
                }
            }

            if (hit) itBullet.remove();
        }
    }

    private void triggerExplosion() {
        isExploding = true;
        explosionObj = new GameObject(player.getX()-10, player.getY()-10, 60, 60, Color.ORANGE, "EXPLOSION");
        player.setX(-1000); 
    }

    private void finishGame() {
        isRunning = false;
        JOptionPane.showMessageDialog(view, "GAME OVER!\nSkor Akhir: " + playerStats.getSkor());
        
        dbModel.updateOrInsert(playerStats);
        
        view.dispose();
        
        System.out.println("Kembali ke Lobby...");
        MenuView menu = new MenuView();          
        new MenuPresenter(menu, dbModel);        
    }

    private void draw() {
        List<GameObject> allObjs = new ArrayList<>();
        
        if (!isExploding) {
            allObjs.add(player);
        } else {
            if (explosionObj != null) {
                allObjs.add(explosionObj);
            }
        }
        
        allObjs.addAll(obstacles);
        allObjs.addAll(humans); // Add list humans
        allObjs.addAll(bullets);
        
        view.render(allObjs, playerStats.getSkor(), playerStats.getSisaPeluru(), playerStats.getPeluruMeleset());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
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
             finishGame(); 
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