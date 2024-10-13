package sample;

import javafx.scene.media.AudioClip;

import java.util.Objects;

public class Sound {
    private final AudioClip soundEffect;

    public Sound(String filePath) {
        soundEffect = new AudioClip(Objects.requireNonNull(getClass().getResource(filePath)).toExternalForm());
    }

    public void playClip() {
        soundEffect.play();
    }
}