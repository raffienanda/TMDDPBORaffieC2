package theme;

import java.awt.Color;

public interface GameTheme {
    String getName();           // Nama tema (misal: "Default", "Space")
    Color getBackgroundColor(); // Warna latar belakang saat main
    // Nanti bisa ditambah: getPlayerImage(), getAlienImage(), dll.
}