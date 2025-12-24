package presenter;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

import model.TBenefit;
import model.TBenefitModel;
import view.IMenuView;
import view.StoryView;
import theme.GameTheme;
import util.AudioPlayer;

public class MenuPresenter {
    // ... (Bagian atas Class & Constructor TETAP SAMA) ...
    // Copy paste saja bagian class SettingsAction dan lainnya dari kode sebelumnya
    private IMenuView view;
    private TBenefitModel model;
    private AudioPlayer audioPlayer;

    public MenuPresenter(IMenuView view, TBenefitModel model) {
        this.view = view;
        this.model = model;
        this.audioPlayer = new AudioPlayer();

        this.view.addPlayListener(new PlayAction());
        this.view.addQuitListener(new QuitAction());
        this.view.addSettingsListener(new SettingsAction()); // Listener Setting

        loadTableData();
        this.view.display();
        
        audioPlayer.playMusic("src/assets/audio/menu_bgm.wav");
    }

    private void loadTableData() {
        List<TBenefit> data = model.getAllBenefits();
        view.setTableData(data);
    }
    
    // ... (Class SettingsAction TETAP SAMA) ...
    class SettingsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
             // ... (Logika slider volume sama seperti sebelumnya) ...
            JPanel panel = new JPanel(new GridLayout(4, 1));
            JSlider bgmSlider = new JSlider(0, 100, (int)(audioPlayer.getBgmVolume() * 100));
            JSlider sfxSlider = new JSlider(0, 100, (int)(audioPlayer.getSfxVolume() * 100));
            
            bgmSlider.addChangeListener(e1 -> audioPlayer.setBgmVolume(bgmSlider.getValue() / 100.0f));
            sfxSlider.addChangeListener(e1 -> audioPlayer.setSfxVolume(sfxSlider.getValue() / 100.0f));

            panel.add(new JLabel("Volume Musik (BGM):"));
            panel.add(bgmSlider);
            panel.add(new JLabel("Volume Efek (SFX):"));
            panel.add(sfxSlider);

            JOptionPane.showMessageDialog((Component) view, panel, "Pengaturan Suara", JOptionPane.PLAIN_MESSAGE);
        }
    }

    class PlayAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsernameInput();
            GameTheme selectedTheme = view.getSelectedTheme();
            String selectedAvatar = view.getSelectedAvatar();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username tidak boleh kosong!");
                return;
            }
            
            audioPlayer.stopMusic();
            view.close(); 
            
            String[] storyImages = {
                "src/assets/images/Family_Scene.jpg",
                "src/assets/images/HumanAttack_Scene.jpg",
                "src/assets/images/AlienAttack_Scene.jpg"
            };
            
            // UBAH: Hanya pakai 1 file lagu untuk Story
            // Pastikan kamu punya file ini atau ganti dengan "menu_bgm.wav" kalau mau tes
            String storyAudio = "src/assets/audio/story_bgm.wav"; 

            // Ambil volume terakhir
            float finalBgmVol = audioPlayer.getBgmVolume();
            float finalSfxVol = audioPlayer.getSfxVolume();

            // Panggil StoryView dengan 1 string audio
            new StoryView(storyImages, storyAudio, () -> {
                System.out.println("Cerita selesai. Memulai Game...");
                
                GamePresenter game = new GamePresenter(username, selectedTheme, model, selectedAvatar);
                game.setVolumes(finalBgmVol, finalSfxVol); 
                
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