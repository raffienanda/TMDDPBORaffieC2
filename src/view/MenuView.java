package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.TBenefit;
import theme.*;

public class MenuView extends JFrame implements IMenuView {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername;
    private JComboBox<GameTheme> comboTheme; // Dropdown tema
    private JButton btnPlay, btnQuit;

    public MenuView() {
        setTitle("Hide and Seek The Challenge");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Judul
        JLabel lblTitle = new JLabel("HIDE AND SEEK THE CHALLENGE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Panel Tengah (Input & Tabel)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Input Username
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Username: "));
        txtUsername = new JTextField(15);
        inputPanel.add(txtUsername);
        centerPanel.add(inputPanel);
        
        // Pilihan Tema (Fitur Tambahan)
        JPanel themePanel = new JPanel(new FlowLayout());
        themePanel.add(new JLabel("Pilih Tema: "));
        GameTheme[] themes = { new DefaultTheme(), new DarkTheme() };
        comboTheme = new JComboBox<>(themes);
        // Custom renderer agar yang tampil di combo box adalah nama temanya
        comboTheme.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GameTheme) {
                    setText(((GameTheme) value).getName());
                }
                return this;
            }
        });
        themePanel.add(comboTheme);
        centerPanel.add(themePanel);

        // Tabel Data (TBenefit)
        String[] columns = {"Username", "Skor", "Peluru Meleset", "Sisa Peluru"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Tombol Bawah
        JPanel buttonPanel = new JPanel();
        btnPlay = new JButton("Play");
        btnQuit = new JButton("Quit");
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnQuit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setTableData(List<TBenefit> users) {
        tableModel.setRowCount(0); // Reset tabel
        for (TBenefit user : users) {
            Object[] row = {
                user.getUsername(),
                user.getSkor(),
                user.getPeluruMeleset(),
                user.getSisaPeluru()
            };
            tableModel.addRow(row);
        }
    }

    @Override
    public String getUsernameInput() {
        return txtUsername.getText().trim();
    }

    @Override
    public GameTheme getSelectedTheme() {
        return (GameTheme) comboTheme.getSelectedItem();
    }

    @Override
    public void addPlayListener(java.awt.event.ActionListener listener) {
        btnPlay.addActionListener(listener);
    }

    @Override
    public void addQuitListener(java.awt.event.ActionListener listener) {
        btnQuit.addActionListener(listener);
    }

    @Override
    public void close() {
        this.dispose(); // Menutup jendela menu
    }
    
    // show() sudah ada bawaan dari JFrame, tapi kita override untuk jelas di interface
    @Override 
    public void display() {
        this.setVisible(true);
    }
}