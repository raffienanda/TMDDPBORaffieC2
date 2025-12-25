package view;

import javax.swing.*;
import java.awt.*;
import util.AudioPlayer;

/**
 * StoryView: Kelas untuk menampilkan prolog/cerita sebelum game dimulai.
 * Konsepnya sederhana: Menampilkan "Slideshow" gambar satu per satu.
 * Ketika gambar habis, window ini tertutup dan game utama dimulai.
 */
public class StoryView extends JFrame {
    // Komponen GUI
    private JLabel lblImage;    // Tempat menaruh gambar cerita
    private JButton btnNext;    // Tombol untuk lanjut ke slide berikutnya
    
    // Data Cerita
    private String[] imagePaths; // Daftar lokasi file gambar cerita
    private int currentIndex = 0; // Penanda kita sedang di halaman ke berapa
    
    // Logika Alur
    private Runnable onFinishAction; // Aksi yang dijalankan saat cerita selesai (Callback)
    private AudioPlayer audioPlayer; // Pemutar musik latar

    /**
     * Constructor StoryView
     * @param images Array string berisi path gambar-gambar cerita.
     * @param audioPath Path file musik background untuk cerita.
     * @param onFinish Fungsi/Codingan yang mau dijalankan setelah cerita tamat (biasanya Start Game).
     */
    // UBAH Constructor: Terima String audioPath (bukan String[])
    public StoryView(String[] images, String audioPath, Runnable onFinish) {
        this.imagePaths = images;
        this.onFinishAction = onFinish;
        this.audioPlayer = new AudioPlayer();
        
        // --- SETUP WINDOW ---
        setTitle("Prologue - The Alien's Journey");
        setSize(1024, 768); 
        setLocationRelativeTo(null); // Tengah layar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Layout: Gambar di Tengah, Tombol di Bawah
        
        // --- 1. SETUP LABEL GAMBAR ---
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBackground(Color.BLACK);
        lblImage.setOpaque(true); // Agar background hitam terlihat
        add(lblImage, BorderLayout.CENTER);
        
        // --- 2. SETUP TOMBOL NEXT ---
        btnNext = new JButton("LANJUT >>");
        btnNext.setFont(new Font("Arial", Font.BOLD, 16));
        btnNext.setBackground(Color.DARK_GRAY);
        btnNext.setForeground(Color.WHITE);
        
        // Lambda Expression: Saat tombol diklik, panggil fungsi nextImage()
        btnNext.addActionListener(e -> nextImage());
        add(btnNext, BorderLayout.SOUTH);
        
        // Tampilkan gambar pertama
        showImage();
        
        // --- 3. MAINKAN MUSIK ---
        // Mainkan musik SATU KALI saja di awal, dan biarkan mengalun sampai cerita selesai
        if (audioPath != null) {
            audioPlayer.playMusic(audioPath);
        }
    }
    
    /**
     * Menampilkan gambar sesuai index saat ini (currentIndex).
     * Gambar akan di-scale (diubah ukurannya) agar pas dengan layar.
     */
    private void showImage() {
        if (currentIndex < imagePaths.length) {
            // Load gambar dari file
            ImageIcon icon = new ImageIcon(imagePaths[currentIndex]);
            
            // Proses Resize Gambar agar pas di window (1024x768)
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(1024, 768, Image.SCALE_SMOOTH);
            
            // Pasang gambar ke Label
            lblImage.setIcon(new ImageIcon(scaledImg));
            
            // CATATAN: Dulu ada logika playMusic disini, sekarang dihapus 
            // agar lagu tidak ter-reset/mengulang dari awal setiap ganti gambar.
        }
    }
    
    /**
     * Logika untuk pindah ke slide berikutnya.
     * Jika gambar sudah habis, maka cerita selesai.
     */
    private void nextImage() {
        currentIndex++; // Naikkan halaman
        
        // Cek apakah sudah melebihi jumlah gambar?
        if (currentIndex >= imagePaths.length) {
            // --- CERITA SELESAI ---
            
            // 1. Matikan musik cerita
            audioPlayer.stopMusic();
            
            // 2. Tutup jendela cerita
            this.dispose();
            
            // 3. Jalankan aksi selanjutnya (Mulai Game)
            // onFinishAction dikirim dari MenuPresenter
            if (onFinishAction != null) {
                onFinishAction.run();
            }
        } else {
            // --- BELUM SELESAI ---
            // Tampilkan gambar berikutnya
            showImage();
        }
    }
    
    /**
     * Menampilkan jendela ini ke layar.
     */
    public void display() {
        setVisible(true);
    }
}