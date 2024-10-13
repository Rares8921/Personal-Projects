package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private TargetDataLine line;
    private File audioFile;
    private volatile boolean running = false;

    // 44.1 kHz, 16-bit, stereo
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private TargetDataLine getStereoMixLine() throws LineUnavailableException {
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    // Windows stereo mix
                    if (mixerInfo.getName().contains("Stereo Mix")) {
                        TargetDataLine targetLine = (TargetDataLine) mixer.getLine(info);
                        targetLine.open(format);
                        return targetLine;
                    }
                }
            }
        }
        throw new LineUnavailableException("The stereo mix was not found!\n");
    }

    public void startRecording(String filePath) throws LineUnavailableException, IOException {
        audioFile = new File(filePath);
        line = getStereoMixLine();
        line.start();
        running = true;
        new Thread(() -> {
            if(!running) {
                System.gc(); // collect the thread
            } else {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile); // Audio writing
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        }).start();
    }

    public void stopRecording() {
        running = false;
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    public File getAudioFile() {
        return audioFile;
    }

}
