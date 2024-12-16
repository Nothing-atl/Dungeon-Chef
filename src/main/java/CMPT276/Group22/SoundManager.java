package CMPT276.Group22;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound effects and background music for the game.
 * Provides methods to play, pause, and stop sounds as needed.
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> clips;
    private Clip currentMusic;
    private float volume = 1.0f;
    private boolean isMuted = false;

    private SoundManager() {
        clips = new HashMap<>();
        initializeSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void initializeSounds() {
        try {
            // Load all sounds
            loadSound("collect_ingredient", "/sounds/collect_ingredient.wav");
            loadSound("collect_bonus", "/sounds/collect_bonus.wav");
            loadSound("button_click", "/sounds/button_click.wav");
            loadSound("menu_music", "/sounds/menu_music.wav");
            loadSound("fire_music", "/sounds/fire_music.wav");
            loadSound("ice_music", "/sounds/ice_music.wav");
            loadSound("earth_music", "/sounds/earth_music.wav");
            loadSound("win", "/sounds/win.wav");
            loadSound("game_over", "/sounds/game_over.wav");
            loadSound("enemy_hit", "/sounds/enemy_hit.wav");
            loadSound("bonus_spawned", "/sounds/bonus_spawned.wav"); 
        } catch (Exception e) {
            System.err.println("Error initializing sounds: " + e.getMessage());
        }
    }

    private void loadSound(String name, String path) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("Could not find sound file: " + path);
                return;
            }
            
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            clips.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound " + name + ": " + e.getMessage());
        }
    }

    public void playSound(String name) {
        if (isMuted) return;
        
        // System.out.println("Attempting to play sound: " + name);
        Clip clip = clips.get(name);
        
        if (clip != null) {
            // For collection sounds, don't stop previous sound
            if (name.equals("collect_ingredient") || name.equals("collect_bonus")) {
                // Just start from beginning, even if currently playing
                clip.setFramePosition(0);
                clip.start();
            } else {
                // For other sounds, stop current and replay
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            }
        } else {
            System.out.println("Sound clip not found: " + name);
        }
    }

    public void playBackgroundMusic(String name) {
        if (isMuted) return;
        
        stopBackgroundMusic();
        
        Clip music = clips.get(name);
        if (music != null) {
            music.setFramePosition(0);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusic = music;
        }
    }

    public void stopBackgroundMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
            currentMusic.setFramePosition(0);
        }
    }

    public void pauseBackgroundMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
        }
    }

    public void resumeBackgroundMusic() {
        if (currentMusic != null && !currentMusic.isRunning() && !isMuted) {
            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.min(1.0f, Math.max(0.0f, volume));
        for (Clip clip : clips.values()) {
            if (clip != null) {
                FloatControl gainControl = 
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else if (currentMusic != null) {
            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void cleanup() {
        stopBackgroundMusic();
        for (Clip clip : clips.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        clips.clear();
    }
}