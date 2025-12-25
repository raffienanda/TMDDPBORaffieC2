package main;

import model.TBenefitModel;
import view.MenuView;
import presenter.MenuPresenter;

/**
 * Main Class: Titik masuk (Entry Point) aplikasi.
 */
public class Main {
    public static void main(String[] args) {
        // Gunakan EventDispatchThread agar GUI aman dan responsif
        // (Swing tidak thread-safe, jadi update GUI harus di thread khusus ini)
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 1. Siapkan Model
                // (Bertugas menangani data dan koneksi database)
                TBenefitModel model = new TBenefitModel();
                
                // 2. Siapkan View
                // (Tampilan awal menu utama)
                MenuView view = new MenuView();
                
                // 3. Siapkan Presenter (View dan Model dipertemukan di sini)
                // (Presenter akan mengontrol logika interaksi antara View dan Model)
                new MenuPresenter(view, model);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}