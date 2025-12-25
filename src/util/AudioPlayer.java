package util;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {
    private Clip musicClip;
    
    // Default volume (100%)
    // Range: 0.0 (Mute) sampai 1.0 (Full)
    private float currentBgmVolume = 0.5f;
    private float currentSfxVolume = 1.0f;

    public void playMusic(String filePath) {
        stopMusic();

        try {
            File musicPath = new File(filePath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                musicClip = AudioSystem.getClip();
                musicClip.open(audioInput);
                
                // Set volume awal sesuai settingan
                setClipVolume(musicClip, currentBgmVolume);
                
                musicClip.loop(Clip.LOOP_CONTINUOUSLY); 
                musicClip.start();
            } else {
                System.out.println("File musik tidak ditemukan: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundEffect(String filePath) {
        try {
            File soundPath = new File(filePath);
            if (soundPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundPath);
                Clip sfxClip = AudioSystem.getClip(); 
                sfxClip.open(audioInput);
                
                // Set volume sesuai settingan SFX
                setClipVolume(sfxClip, currentSfxVolume);
                
                sfxClip.start(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            if (musicClip.isRunning()) musicClip.stop();
            musicClip.close();
        }
    }
    
    // --- FITUR VOLUME ---
    
    // Setter untuk BGM (Musik Latar)
    public void setBgmVolume(float volume) {
        this.currentBgmVolume = volume;
        if (musicClip != null && musicClip.isOpen()) {
            setClipVolume(musicClip, volume);
        }
    }

    // Setter untuk SFX (Efek Suara)
    public void setSfxVolume(float volume) {
        this.currentSfxVolume = volume;
    }
    
    public float getBgmVolume() { return currentBgmVolume; }
    public float getSfxVolume() { return currentSfxVolume; }

    // Helper: Mengubah float (0.0 - 1.0) menjadi Decibel
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
            // Rumus konversi Linear ke Logaritmik (Decibel)
            // Volume 0.0 -> Mute (-80dB)
            // Volume 1.0 -> Normal (0dB)
            float dB;
            if (volume <= 0.0f) {
                dB = -80.0f; // Mute total
            } else {
                dB = 20.0f * (float) Math.log10(volume);
            }
            
            // Pastikan tidak melebihi batas hardware
            if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
            if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();
            
            gainControl.setValue(dB);
        } catch (Exception e) {
            // Beberapa audio clip mungkin tidak support MASTER_GAIN
            System.err.println("Volume control not supported for this clip.");
        }
    }
}