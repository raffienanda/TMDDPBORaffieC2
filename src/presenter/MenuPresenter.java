package presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import model.TBenefit;
import model.TBenefitModel;
import view.IMenuView;
import view.StoryView; // <--- Jangan lupa import ini
import theme.GameTheme;

public class MenuPresenter {
    private IMenuView view;
    private TBenefitModel model;

    public MenuPresenter(IMenuView view, TBenefitModel model) {
        this.view = view;
        this.model = model;

        // Pasang Event Listener ke Tombol di View
        this.view.addPlayListener(new PlayAction());
        this.view.addQuitListener(new QuitAction());

        loadTableData();
        this.view.display();
    }

    private void loadTableData() {
        List<TBenefit> data = model.getAllBenefits();
        view.setTableData(data);
    }

    // --- CLASS LISTENER TOMBOL PLAY (DIMODIFIKASI) ---
    class PlayAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsernameInput();
            GameTheme selectedTheme = view.getSelectedTheme();

            // Validasi Input
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username tidak boleh kosong!");
                return;
            }
            
            // 1. Tutup Menu Dulu
            view.close(); 
            
            // 2. Siapkan Gambar Cerita
            String[] storyImages = {
                "src/assets/images/Family_Scene.jpg",
                "src/assets/images/HumanAttack_Scene.jpg",
                "src/assets/images/AlienAttack_Scene.jpg"
            };

            // 3. Buka Story View
            new StoryView(storyImages, () -> {
                
                System.out.println("Cerita selesai. Memulai Game untuk: " + username);
                new GamePresenter(username, selectedTheme, model); 
                
            }).display();
        }
    }

    class QuitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}