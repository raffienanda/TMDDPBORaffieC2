package view;

import javax.swing.*;
import java.awt.*;

public class StoryView extends JFrame {
    private JLabel lblImage;
    private JButton btnNext;
    private String[] imagePaths;
    private int currentIndex = 0;
    
    // Callback: Aksi yang dilakukan saat cerita selesai
    private Runnable onFinishAction;

    public StoryView(String[] images, Runnable onFinish) {
        this.imagePaths = images;
        this.onFinishAction = onFinish;
        
        setTitle("Prologue - The Alien's Journey");
        setSize(1024, 768); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // --- 1. Area Gambar ---
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBackground(Color.BLACK);
        lblImage.setOpaque(true); // Biar background hitam kalau gambar kekecilan
        add(lblImage, BorderLayout.CENTER);
        
        // --- 2. Tombol Lanjut ---
        btnNext = new JButton("LANJUT >>");
        btnNext.setFont(new Font("Arial", Font.BOLD, 16));
        btnNext.setBackground(Color.DARK_GRAY);
        btnNext.setForeground(Color.WHITE);
        btnNext.addActionListener(e -> nextImage());
        add(btnNext, BorderLayout.SOUTH);
        
        // Tampilkan gambar pertama
        showImage();
    }
    
    private void showImage() {
        if (currentIndex < imagePaths.length) {
            // Load gambar
            ImageIcon icon = new ImageIcon(imagePaths[currentIndex]);
            
            // Resize gambar biar pas di layar (Opsional, biar rapi)
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(1024, 768, Image.SCALE_SMOOTH);
            
            lblImage.setIcon(new ImageIcon(scaledImg));
        }
    }
    
    private void nextImage() {
        currentIndex++;
        
        // Cek apakah gambar sudah habis?
        if (currentIndex >= imagePaths.length) {
            // Kalau habis, tutup StoryView dan jalankan Game
            this.dispose();
            if (onFinishAction != null) {
                onFinishAction.run();
            }
        } else {
            // Kalau belum, lanjut gambar berikutnya
            showImage();
        }
    }
    
    public void display() {
        setVisible(true);
    }
}