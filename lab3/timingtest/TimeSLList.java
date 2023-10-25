package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        AList<Integer> calcOpCounts = new AList<>();
        timeGetLast(calcNs, calcTimes, calcOpCounts);
        printTimingTable(calcNs, calcTimes, calcOpCounts);
    }

    public static void timeGetLast(AList<Integer> calcNs, AList<Double> calcTimes, AList<Integer> calcOpCounts) {
        // TODO: YOUR CODE HERE
        for (int i = 1000; i <= 128000; i *= 2) {
            calcNs.addLast(i);
            SLList<Integer> storeNs = new SLList<>();
            SLList<Double> storeTimes = new SLList<>();
            double sumTime = 0;
            for (int j = 1; j <= i; j += 1) {   // build the SLList
                storeNs.addLast(1);
            }
            for (int k = 1; k <= 10000; k += 1) {   // time the SLList.getLast()
                Stopwatch sw = new Stopwatch();
                storeNs.getLast();
                double timeInSeconds = sw.elapsedTime();
                storeTimes.addLast(timeInSeconds);
                sumTime += storeTimes.getLast();
            }
            calcTimes.addLast(sumTime);
            calcOpCounts.addLast(10000);
        }
    }
}
