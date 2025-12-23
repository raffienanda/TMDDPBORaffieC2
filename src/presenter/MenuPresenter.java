package presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import model.TBenefit;
import model.TBenefitModel;
import view.IMenuView;
import view.StoryView;
import theme.GameTheme;
import util.AudioPlayer; // 1. Import AudioPlayer

public class MenuPresenter {
    private IMenuView view;
    private TBenefitModel model;
    private AudioPlayer audioPlayer; // 2. Variabel Audio

    public MenuPresenter(IMenuView view, TBenefitModel model) {
        this.view = view;
        this.model = model;
        this.audioPlayer = new AudioPlayer(); // 3. Inisialisasi

        this.view.addPlayListener(new PlayAction());
        this.view.addQuitListener(new QuitAction());

        loadTableData();
        this.view.display();
        
        // 4. Mainkan Musik Menu
        audioPlayer.playMusic("src/assets/sounds/menu_bgm.wav");
    }

    private void loadTableData() {
        List<TBenefit> data = model.getAllBenefits();
        view.setTableData(data);
    }

    class PlayAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsernameInput();
            GameTheme selectedTheme = view.getSelectedTheme();
            String selectedAvatar = view.getSelectedAvatar(); // Fitur Avatar Tetap Ada

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username tidak boleh kosong!");
                return;
            }
            
            // 5. Matikan Musik Menu sebelum pindah
            audioPlayer.stopMusic();
            
            view.close(); 
            
            String[] storyImages = {
                "src/assets/images/Family_Scene.jpg",
                "src/assets/images/HumanAttack_Scene.jpg",
                "src/assets/images/AlienAttack_Scene.jpg"
            };

            new StoryView(storyImages, () -> {
                System.out.println("Cerita selesai. Memulai Game untuk: " + username);
                // Masuk ke Game (Audio Game akan mulai di dalam GamePresenter)
                new GamePresenter(username, selectedTheme, model, selectedAvatar); 
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