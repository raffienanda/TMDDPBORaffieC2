package presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import model.TBenefit;
import model.TBenefitModel;
import view.IMenuView;
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

        // Tampilkan data awal di tabel
        loadTableData();

        // Tampilkan GUI
        this.view.display();
    }

    private void loadTableData() {
        // Minta data ke Model
        List<TBenefit> data = model.getAllBenefits();
        // Update View
        view.setTableData(data);
    }

    // Class Listener untuk Tombol Play
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
            
            System.out.println("Memulai Game untuk: " + username);
            view.close(); // Tutup menu
            
            // Masuk ke Game Presenter
            new GamePresenter(username, selectedTheme, model); 
        }
    }

    // Class Listener untuk Tombol Quit
    class QuitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}