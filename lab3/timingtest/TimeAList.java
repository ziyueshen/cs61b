package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        AList<Integer> calcNs = new AList<>();
        AList<Double> calcTimes = new AList<>();
        AList<Integer> storeOpCounts = calcNs;
        timeAListConstruction(calcNs, calcTimes);
        printTimingTable(calcNs, calcTimes, storeOpCounts);
    }

    public static void timeAListConstruction(AList<Integer> calcNs, AList<Double> calcTimes) {
        // TODO: YOUR CODE HERE
        for (int i = 1000; i <= 128000; i *= 2) {
            calcNs.addLast(i);
            AList<Integer> storeNs = new AList<>();
            AList<Double> storeTimes = new AList<>();
            double sumTime = 0;
            for (int j = 1; j <= i; j += 1) {
                Stopwatch sw = new Stopwatch();
                storeNs.addLast(1);
                double timeInSeconds = sw.elapsedTime();
                storeTimes.addLast(timeInSeconds);
                sumTime += storeTimes.getLast();
            }
            calcTimes.addLast(sumTime);
        }
    }
}
