package theme;

import java.awt.Color;

public class DarkTheme implements GameTheme {
    @Override
    public String getName() {
        return "Dark Space";
    }

    @Override
    public Color getBackgroundColor() {
        return Color.BLACK; 
    }
}