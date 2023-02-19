package findQari;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class VoiceComparator {

    public VoiceComparator(File file1, File file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public File file1;
     public File file2;
    private static final int NUM_THREADS = 4;


    public void checkSimilar(File file1 , File file2) {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try (
                AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(file1);
                AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(file2)) {

            // Submit tasks to resample and convert the audio files concurrently
            Future<AudioInputStream> resampledStream1Future = executor.submit(
                    () -> AudioUtils.resample(audioStream1, 44100.0f));
            Future<AudioInputStream> resampledStream2Future = executor.submit(
                    () -> AudioUtils.resample(audioStream2, 44100.0f));
            Future<AudioInputStream> convertedStream1Future = executor.submit(
                    () -> AudioUtils.convertChannels(resampledStream1Future.get(), 1));
            Future<AudioInputStream> convertedStream2Future = executor.submit(
                    () -> AudioUtils.convertChannels(resampledStream2Future.get(), 1));

            // Wait for the tasks to complete and get the resulting audio data as byte arrays
            byte[] audioData1 = convertedStream1Future.get()
                    .readAllBytes();
            byte[] audioData2 = convertedStream2Future.get()
                    .readAllBytes();

            // Compute the cross-correlation of the two signals using parallel streams
            double[] xcorr = IntStream.range(0, audioData1.length + audioData2.length - 1)
                    .parallel()
                    .mapToDouble(i -> {
                        double sum = 0;
                        for (int j = Math.max(0, i - audioData2.length + 1); j <= Math.min(i, audioData1.length - 1); j++) {
                            sum += audioData1[j] * audioData2[i - j];
                        }
                        return sum;
                    })
                    .toArray();

            // Find the maximum correlation value and the lag at which it occurs
            double maxCorr = Double.NEGATIVE_INFINITY;
            int maxLag = 0;
            for (int i = 0; i < xcorr.length; i++) {
                if (xcorr[i] > maxCorr) {
                    maxCorr = xcorr[i];
                    maxLag = i;
                }
            }

            // Print the results
            System.out.println("Maximum correlation: " + maxCorr);
            System.out.println("Time lag: " + maxLag);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


}

