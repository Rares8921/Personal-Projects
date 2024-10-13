package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Metronome extends Thread {
    private static AtomicBoolean keepRunning;
    public static double bpm = 60;
    private static MediaPlayer mediaPlayer;

    public boolean isRunning() {
        return keepRunning.get();
    }

    public Metronome()  {
        Media media = new Media(String.valueOf(Objects.requireNonNull(getClass().getResource("/audio/metronomeBeep.mp3"))));
        mediaPlayer = new MediaPlayer(media);
        keepRunning = new AtomicBoolean(false);
    }

    public void end()  {
        keepRunning.set(false);
        Thread.currentThread().interrupt();
        mediaPlayer.stop();
    }

    @Override
    public void run() {
        keepRunning.set(true);
        while(keepRunning.get()) {
            try  {
                Thread.sleep((long)(1000 * (60.0 / bpm)));
                Media media = new Media(String.valueOf(Objects.requireNonNull(getClass().getResource("/Audio/metronomeBeep.mp3"))));
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(Controller.applicationVolume);
                mediaPlayer.play();
            } catch(InterruptedException e)  {
                e.printStackTrace(System.out);
            }
        }
    }
}
