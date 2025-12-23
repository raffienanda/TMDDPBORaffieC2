package view;

import javax.swing.*;
import java.awt.*;
import util.AudioPlayer; // 1. Import AudioPlayer

public class StoryView extends JFrame {
    private JLabel lblImage;
    private JButton btnNext;
    private String[] imagePaths;
    private int currentIndex = 0;
    
    private Runnable onFinishAction;
    private AudioPlayer audioPlayer; // 2. Variabel Audio

    public StoryView(String[] images, Runnable onFinish) {
        this.imagePaths = images;
        this.onFinishAction = onFinish;
        this.audioPlayer = new AudioPlayer(); // 3. Inisialisasi
        
        setTitle("Prologue - The Alien's Journey");
        setSize(1024, 768); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBackground(Color.BLACK);
        lblImage.setOpaque(true); 
        add(lblImage, BorderLayout.CENTER);
        
        btnNext = new JButton("LANJUT >>");
        btnNext.setFont(new Font("Arial", Font.BOLD, 16));
        btnNext.setBackground(Color.DARK_GRAY);
        btnNext.setForeground(Color.WHITE);
        btnNext.addActionListener(e -> nextImage());
        add(btnNext, BorderLayout.SOUTH);
        
        showImage();
        
        // 4. Mainkan Musik Story
        audioPlayer.playMusic("src/assets/audio/story_bgm.wav");
    }
    
    private void showImage() {
        if (currentIndex < imagePaths.length) {
            ImageIcon icon = new ImageIcon(imagePaths[currentIndex]);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(1024, 768, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaledImg));
        }
    }
    
    private void nextImage() {
        currentIndex++;
        
        if (currentIndex >= imagePaths.length) {
            // 5. Matikan Musik Story
            audioPlayer.stopMusic();
            
            this.dispose();
            if (onFinishAction != null) {
                onFinishAction.run();
            }
        } else {
            showImage();
        }
    }
    
    public void display() {
        setVisible(true);
    }
}