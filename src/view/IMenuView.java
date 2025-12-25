package view;

import java.util.List;
import model.TBenefit;
import theme.GameTheme;
import java.awt.event.ActionListener;

/**
 * IMenuView: Interface (Kontrak) untuk Tampilan Menu Utama.
 * * Dalam pola MVP, Presenter tidak boleh mengakses class 'MenuView' secara langsung.
 * Presenter hanya boleh berbicara melalui Interface ini.
 * Tujuannya agar logika aplikasi (Presenter) terpisah dari teknis tampilan (Swing/JFrame).
 */
public interface IMenuView {
    
    /**
     * Meminta View untuk menampilkan data tabel High Score.
     * @param users List data user yang diambil dari database.
     */
    void setTableData(List<TBenefit> users);

    /**
     * Mengambil teks username yang diketik oleh user di input field.
     * @return String username.
     */
    String getUsernameInput();

    /**
     * Mengambil tema (Arena) yang dipilih user dari ComboBox.
     * @return Objek GameTheme yang dipilih.
     */
    GameTheme getSelectedTheme();

    /**
     * Mengambil nama file gambar avatar yang dipilih user.
     * @return String nama file (misal: "alien.png").
     */
    String getSelectedAvatar();
    
    // --- METHOD UNTUK MENGHUBUNGKAN TOMBOL DENGAN AKSI (LISTENER) ---
    
    /**
     * Pasang aksi ketika tombol "PLAY GAME" ditekan.
     */
    void addPlayListener(ActionListener listener);

    /**
     * Pasang aksi ketika tombol "QUIT" ditekan.
     */
    void addQuitListener(ActionListener listener);
    
    /**
     * Pasang aksi ketika tombol "SETTINGS" ditekan.
     * (Fitur Baru untuk pengaturan volume)
     */
    void addSettingsListener(ActionListener listener);
    
    // --- KONTROL WINDOW/JENDELA ---

    /**
     * Menutup jendela menu (biasanya dipanggil saat masuk ke game).
     */
    void close();

    /**
     * Menampilkan jendela menu ke layar.
     */
    void display();
}