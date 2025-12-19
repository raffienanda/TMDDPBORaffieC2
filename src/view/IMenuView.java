package view;

import java.util.List;
import model.TBenefit;
import theme.GameTheme;

public interface IMenuView {
    // Untuk mengisi data ke tabel
    void setTableData(List<TBenefit> users);
    
    // Untuk mengambil inputan user
    String getUsernameInput();
    GameTheme getSelectedTheme();
    
    // Untuk tombol
    void addPlayListener(java.awt.event.ActionListener listener);
    void addQuitListener(java.awt.event.ActionListener listener);
    
    // Menutup menu saat game mulai
    void close();
    void display();
}