package presenter;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // Import MouseAdapter
import java.awt.event.MouseEvent; // Import MouseEvent
import java.util.List;
import javax.swing.*;

import model.TBenefit;
import model.TBenefitModel;
import view.IMenuView;
import view.MenuView; // Perlu import MenuView untuk casting (akses getTable)
import view.StoryView;
import theme.GameTheme;
import util.AudioPlayer;

public class MenuPresenter {
    private IMenuView view;
    private TBenefitModel model;
    private AudioPlayer audioPlayer;

    public MenuPresenter(IMenuView view, TBenefitModel model) {
        this.view = view;
        this.model = model;
        this.audioPlayer = new AudioPlayer();

        // Pasang Listener Tombol
        this.view.addPlayListener(new PlayAction());
        this.view.addQuitListener(new QuitAction());
        this.view.addSettingsListener(new SettingsAction());

        // Load Data Awal
        loadTableData();
        this.view.display();
        
        // Putar Musik Menu
        audioPlayer.playMusic("src/assets/audio/menu_bgm.wav");

        // --- FITUR BARU: KLIK TABEL -> AUTO ISI USERNAME ---
        if (view instanceof MenuView) { // Cek apakah view adalah instance MenuView
            MenuView v = (MenuView) view;
            
            // Pasang Mouse Listener ke Tabel
            v.getTable().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = v.getTable().getSelectedRow();
                    if (row != -1) {
                        // Ambil nama dari kolom ke-0 (Username)
                        String selectedName = v.getTable().getModel().getValueAt(row, 0).toString();
                        // Isi ke text field
                        v.setUsername(selectedName);
                    }
                }
            });
        }
        // --------------------------------------------------
    }

    private void loadTableData() {
        List<TBenefit> data = model.getAllBenefits();
        view.setTableData(data);
    }
    
    // Class Listener untuk Settings (Volume)
    class SettingsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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

    // Class Listener untuk Play Game
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
            
            // Gambar cerita
            String[] storyImages = {
                "src/assets/images/Family_Scene.jpg",
                "src/assets/images/HumanAttack_Scene.jpg",
                "src/assets/images/AlienAttack_Scene.jpg"
            };
            
            String storyAudio = "src/assets/audio/story_bgm.wav"; 

            // Simpan volume saat ini
            float finalBgmVol = audioPlayer.getBgmVolume();
            float finalSfxVol = audioPlayer.getSfxVolume();

            // Mulai Story -> Lanjut Game
            new StoryView(storyImages, storyAudio, () -> {
                System.out.println("Cerita selesai. Memulai Game...");
                
                GamePresenter game = new GamePresenter(username, selectedTheme, model, selectedAvatar);
                game.setVolumes(finalBgmVol, finalSfxVol); 
                
            }).display();
        }
    }

    // Class Listener untuk Keluar
    class QuitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}