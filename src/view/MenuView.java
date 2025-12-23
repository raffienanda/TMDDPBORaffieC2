package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import model.TBenefit;
import theme.*;

public class MenuView extends JFrame implements IMenuView {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername;
    private JComboBox<GameTheme> comboTheme;
    private JComboBox<String> comboAvatar; 
    private JButton btnPlay, btnQuit;
    private Image backgroundImage;

    public MenuView() {
        setTitle("Hide and Seek The Challenge");
        setSize(500, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Load Gambar Background
        backgroundImage = new ImageIcon("src/assets/images/menu_bg.png").getImage();

        // 2. Custom Panel untuk Background Image
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(mainPanel);

        // --- JUDUL DENGAN OUTLINE ---
        OutlineLabel lblTitle = new OutlineLabel("HIDE AND SEEK THE CHALLENGE");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 26)); 
        lblTitle.setForeground(Color.WHITE);   
        lblTitle.setOutlineColor(Color.BLACK); 
        lblTitle.setOutlineThickness(3.0f);    
        
        // --- (BARU) SUBTITLE ---
        // Ganti tulisan ini sesuai keinginanmu
        OutlineLabel lblSubtitle = new OutlineLabel("Revenge Of Imroatus"); 
        lblSubtitle.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        lblSubtitle.setForeground(Color.WHITE); // Saya beri warna kuning agar kontras
        lblSubtitle.setOutlineColor(Color.BLACK); 
        lblSubtitle.setOutlineThickness(2.0f);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle, BorderLayout.CENTER);  // Judul di Tengah
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH); // Subtitle di Bawahnya
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(titlePanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (INPUT & TABEL) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Input Username
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);
        
        OutlineLabel lblUser = new OutlineLabel("Username: ");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setOutlineColor(Color.BLACK);
        lblUser.setOutlineThickness(2.0f);
        inputPanel.add(lblUser);
        
        txtUsername = new JTextField(15);
        inputPanel.add(txtUsername);
        centerPanel.add(inputPanel);
        
        // 2. Pilihan Tema
        JPanel themePanel = new JPanel(new FlowLayout());
        themePanel.setOpaque(false);
        
        OutlineLabel lblTheme = new OutlineLabel("Pilih Arena: ");
        lblTheme.setFont(new Font("Arial", Font.BOLD, 14));
        lblTheme.setForeground(Color.WHITE);
        lblTheme.setOutlineColor(Color.BLACK);
        lblTheme.setOutlineThickness(2.0f);
        themePanel.add(lblTheme);
        
        GameTheme[] themes = { new DefaultTheme(), new DarkTheme() };
        comboTheme = new JComboBox<>(themes);
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

        // 3. Pilihan Avatar (UFO)
        JPanel avatarPanel = new JPanel(new FlowLayout());
        avatarPanel.setOpaque(false);

        OutlineLabel lblAvatar = new OutlineLabel("Pilih UFO: ");
        lblAvatar.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setOutlineColor(Color.BLACK);
        lblAvatar.setOutlineThickness(2.0f);
        avatarPanel.add(lblAvatar);

        String[] avatars = { "Biru (Default)", "Merah", "Hijau" };
        comboAvatar = new JComboBox<>(avatars);
        avatarPanel.add(comboAvatar);
        centerPanel.add(avatarPanel);

        // --- TABEL CUSTOM ---
        String[] columns = {"Username", "Skor", "Meleset", "Sisa Peluru"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        table.setShowGrid(false); 
        table.setRowHeight(30);   
        
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JComponent) ((JComponent)c).setOpaque(true);
                
                if (isSelected) {
                    c.setBackground(new Color(255, 255, 255, 60)); 
                } else {
                    c.setBackground(new Color(0, 0, 0, 180)); 
                }
                
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Arial", Font.BOLD, 12));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 80)));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(0, 0, 0, 230)); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.getViewport().setBackground(new Color(0,0,0,0)); 
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); 

        JPanel tableWrapper = new JPanel();
        tableWrapper.setOpaque(false);
        tableWrapper.add(scrollPane);
        centerPanel.add(tableWrapper);

        add(centerPanel, BorderLayout.CENTER);

        // --- TOMBOL BAWAH ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); 
        
        btnPlay = new JButton("PLAY GAME");
        btnQuit = new JButton("QUIT");
        
        btnPlay.setFont(new Font("Arial", Font.BOLD, 14));
        btnPlay.setBackground(new Color(34, 139, 34)); 
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFocusPainted(false);
        
        btnQuit.setFont(new Font("Arial", Font.BOLD, 14));
        btnQuit.setBackground(new Color(178, 34, 34)); 
        btnQuit.setForeground(Color.WHITE);
        btnQuit.setFocusPainted(false);

        buttonPanel.add(btnPlay);
        buttonPanel.add(btnQuit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- IMPLEMENTASI METHOD DARI INTERFACE ---

    @Override
    public void setTableData(List<TBenefit> users) {
        tableModel.setRowCount(0); 
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
    public String getSelectedAvatar() {
        String choice = (String) comboAvatar.getSelectedItem();
        if ("Merah".equals(choice)) return "alien_red.png";
        if ("Hijau".equals(choice)) return "alien_green.png";
        return "alien.png"; 
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
        this.dispose(); 
    }
    
    @Override 
    public void display() {
        this.setVisible(true);
    }
}

// CLASS HELPER LABEL OUTLINE (TIDAK BERUBAH)
class OutlineLabel extends JLabel {
    private Color outlineColor = Color.BLACK;
    private float strokeThickness = 3f;

    public OutlineLabel(String text) {
        super(text);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setOutlineColor(Color c) {
        this.outlineColor = c;
    }
    
    public void setOutlineThickness(float f) {
        this.strokeThickness = f;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        String text = getText();
        if (text == null || text.isEmpty()) { super.paintComponent(g); return; }
        Font font = getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);
        Rectangle2D bounds = textLayout.getBounds();
        double x = (getWidth() - bounds.getWidth()) / 2;
        double y = (getHeight() - bounds.getHeight()) / 2 + textLayout.getAscent();
        java.awt.Shape shape = textLayout.getOutline(AffineTransform.getTranslateInstance(x, y));
        g2.setColor(outlineColor);
        g2.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(shape);
        g2.setColor(getForeground());
        g2.fill(shape);
        g2.dispose();
    }
}