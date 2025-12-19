package theme;

import java.awt.Color;

public class DefaultTheme implements GameTheme {
    @Override
    public String getName() {
        return "Default (Clean White)";
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE; // Sesuai contoh di dokumen PDF
    }
}