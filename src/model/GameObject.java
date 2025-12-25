package model;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * GameObject: Kelas dasar untuk semua objek di dalam game.
 * (Player, Musuh, Peluru, Batu, dll semua adalah GameObject)
 */
public class GameObject {
    private int x, y;
    private int width, height;
    private Color color;
    private String type; // "ALIEN" (Player), "HUMAN" (Bot), "OBSTACLE", "BULLET"

    public GameObject(int x, int y, int width, int height, Color color, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.type = type;
    }

    // Untuk deteksi tabrakan (Collision Detection)
    // Mengembalikan kotak (Rectangle) sesuai posisi dan ukuran objek.
    // Kotak ini nanti dipakai untuk cek .intersects() dengan objek lain.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getter & Setter
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Color getColor() { return color; }
    public String getType() { return type; }
}