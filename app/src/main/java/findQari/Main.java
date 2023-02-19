package findQari;

import java.io.File;

public class Main {


    public static void main(String[] args) throws Exception {


        VoiceComparator files = new VoiceComparator(
                new File("app/src/main/resources/StarWars1.wav"),
                new File("app/src/main/resources/StarWars1.wav"));

        files.checkSimilar(files.file1,files.file2);





    }

}
