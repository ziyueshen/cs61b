package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
public class GuitarHero {
    private static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static double [] doubleArray = new double[37];
    public static void main(String[] args) {
        for (int i = 0; i < 37; i++) {
            doubleArray[i] = 440.0 * Math.pow(2, (i - 24.0) / 12.0);
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyDex = keyboard.indexOf(key);
                if (keyDex != -1) {
                    GuitarString string = new GuitarString(doubleArray[keyDex]);
                    System.out.println(key);
                    System.out.println(keyDex);
                    System.out.println(doubleArray[keyDex]);
                    string.pluck();
                    for (int i = 0; i < 50000; i += 1) {
                        double sample = string.sample();

                        /* play the sample on standard audio */
                        StdAudio.play(sample);

                        /* advance the simulation of each guitar string by one step */
                        string.tic();
                    }
                }
            }

        }
    }
}

