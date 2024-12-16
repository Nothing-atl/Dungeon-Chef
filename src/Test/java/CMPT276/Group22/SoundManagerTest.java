package CMPT276.Group22;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.lang.reflect.Field;

public class SoundManagerTest {
    private SoundManager soundManager;

    @Before
    public void setUp() {
        soundManager = SoundManager.getInstance();
    }

    @After
    public void tearDown() {
        resetSingleton();
    }

    private void resetSingleton() {
        try {
            Field instance = SoundManager.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSingletonInstance() {
        SoundManager instance1 = SoundManager.getInstance();
        SoundManager instance2 = SoundManager.getInstance();
        assertSame("Should return same instance", instance1, instance2);
    }

    @Test
    public void testMuteFunctionality() {
        soundManager.toggleMute();
        // Test playing sound while muted
        soundManager.playSound("collect_ingredient");
        soundManager.playBackgroundMusic("menu_music");
        
        soundManager.toggleMute();
        // Test playing sound while unmuted
        soundManager.playSound("collect_ingredient");
        soundManager.playBackgroundMusic("menu_music");
    }

    @Test
    public void testVolumeControl() {
        // Test volume bounds
        soundManager.setVolume(2.0f); // Above max
        soundManager.setVolume(-1.0f); // Below min
        soundManager.setVolume(0.5f); // Normal
    }

    @Test
    public void testBackgroundMusicControl() {
        // Test null music case
        soundManager.pauseBackgroundMusic();
        soundManager.resumeBackgroundMusic();
        soundManager.stopBackgroundMusic();

        // Test with actual music
        soundManager.playBackgroundMusic("menu_music");
        soundManager.pauseBackgroundMusic();
        soundManager.resumeBackgroundMusic();
        soundManager.stopBackgroundMusic();
    }

    @Test 
    public void testSoundPlayback() {
        // Test regular and collection sounds
        soundManager.playSound("collect_ingredient");
        soundManager.playSound("collect_bonus");
        soundManager.playSound("button_click");
        
        // Test invalid sound
        soundManager.playSound("invalid_sound");
    }

    @Test
    public void testCleanup() {
        soundManager.cleanup();
        soundManager.playBackgroundMusic("menu_music"); // Test after cleanup
    }
}