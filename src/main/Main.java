package main;

import model.TBenefitModel;
import view.MenuView;
import presenter.MenuPresenter;

public class Main {
    public static void main(String[] args) {
        // Gunakan EventDispatchThread agar GUI aman dan responsif
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 1. Siapkan Model
                TBenefitModel model = new TBenefitModel();
                
                // 2. Siapkan View
                MenuView view = new MenuView();
                
                // 3. Siapkan Presenter (View dan Model dipertemukan di sini)
                new MenuPresenter(view, model);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}