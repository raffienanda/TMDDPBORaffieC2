package view;

import java.util.List;
import model.TBenefit;
import theme.GameTheme;
import java.awt.event.ActionListener;

public interface IMenuView {
    void setTableData(List<TBenefit> users);
    String getUsernameInput();
    GameTheme getSelectedTheme();
    String getSelectedAvatar();
    
    void addPlayListener(ActionListener listener);
    void addQuitListener(ActionListener listener);
    
    // --- BARU ---
    void addSettingsListener(ActionListener listener);
    
    void close();
    void display();
}