package findQari;

import javax.sound.sampled.*;

public class AudioUtils {
    public static AudioInputStream resample(AudioInputStream inputAudioStream, float targetSampleRate)
            throws UnsupportedAudioFileException, UnsupportedAudioFileException {
        AudioFormat inputFormat = inputAudioStream.getFormat();
        float sourceSampleRate = inputFormat.getSampleRate();
        int sourceChannels = inputFormat.getChannels();

        AudioFormat targetFormat = new AudioFormat(inputFormat.getEncoding(), targetSampleRate, inputFormat.getSampleSizeInBits(), sourceChannels,
                inputFormat.getFrameSize(), inputFormat.getFrameRate(), inputFormat.isBigEndian());

        return AudioSystem.getAudioInputStream(targetFormat, inputAudioStream);
    }

    public static AudioInputStream convertChannels(AudioInputStream inputAudioStream, int targetChannels)
            throws UnsupportedAudioFileException, UnsupportedAudioFileException {
        AudioFormat inputFormat = inputAudioStream.getFormat();
        int sourceChannels = inputFormat.getChannels();

        AudioFormat targetFormat = new AudioFormat(inputFormat.getEncoding(), inputFormat.getSampleRate(), inputFormat.getSampleSizeInBits(), targetChannels,
                inputFormat.getFrameSize() / sourceChannels * targetChannels, inputFormat.getFrameRate(), inputFormat.isBigEndian());

        return AudioSystem.getAudioInputStream(targetFormat, inputAudioStream);
    }
}

