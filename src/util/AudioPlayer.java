package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;

    public void playMusic(String filePath) {
        // Hentikan musik sebelumnya jika ada
        stopMusic();

        try {
            File musicPath = new File(filePath);

            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                
                // Putar terus menerus (looping)
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
                clip.start();
            } else {
                System.out.println("File audio tidak ditemukan: " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
    }
}