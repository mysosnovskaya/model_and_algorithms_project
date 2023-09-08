import com.sun.net.httpserver.HttpServer;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private static final List<Double> ALPHA = List.of(1.5, 2.0, 2.5, 3.0);
    private static final List<Integer> JOBS_COUNT = List.of(50, 100);
    private static final List<Double> SINGLE_JOBS_PROBABILITY = List.of(0.3, 0.5, 0.7);
    private static final List<Double> SMALL_JOBS_PROBABILITY = List.of(0.0, 0.3, 0.5, 0.7, 1.0);

    private static final int EXAMPLES_COUNT = 30;
    private static final List<Integer> SMALL_JOBS_VOLUMES_1 = List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
    private static final List<Integer> SMALL_JOBS_VOLUMES_2 = List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
    private static final List<Integer> LARGE_JOBS_VOLUMES_1 = List.of(200, 275, 350, 425, 500, 575, 650, 725, 800, 875);
    private static final List<Integer> LARGE_JOBS_VOLUMES_2 = List.of(520, 540, 560, 580, 600, 620, 640, 660, 680, 700);
    private static final List<Integer> BLOCKS = List.of(2, 4, 6, 8, 10);

    private static final int ENERGY = 100_000;
    private static final int MAX_PROCESSORS = 2;

    private static double maxRes = 0.0;

    private static final Map<String, Map<String, Double>> expectedValues = new HashMap<>();
    private static final Map<String, Map<String, Double>> dispersions = new HashMap<>();
    private static final Map<String, Map<String, Double>> averageQuadraticDeviations = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> all2Proc = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> all1Proc = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> all1Minus2 = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> all1MinusLB = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> all2MinusLB = new HashMap<>();

    private static final Map<String, Map<String, List<Double>>> resortLast = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> resortRandom = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> resortReverse = new HashMap<>();

    private static final Map<String, Map<String, List<Double>>> noResortLast = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> noResortRandom = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> noResortReverse = new HashMap<>();

    private static final Map<String, Map<String, List<Double>>> LoLb = new HashMap<>();
    private static final Map<String, Map<String, List<Double>>> newLb = new HashMap<>();

    private static final Map<Integer, Double> a1solutions = new HashMap<>();
    private static final Map<Integer, Double> a2solutions = new HashMap<>();
    private static Integer counter = 0;

    public static void main(String[] args) throws IOException {
        run();
        if (true) {
            return;
        }
        Random random = new Random(260);
        for (double alpha : ALPHA) {
            for (int jobsCount : JOBS_COUNT) {
                for (double singleJobsProbability : SINGLE_JOBS_PROBABILITY) {
                    for (double smallJobsProbability : SMALL_JOBS_PROBABILITY) {
                        statistics(alpha, jobsCount, singleJobsProbability, smallJobsProbability, random, 0, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_1, "11");
//                        statistics(alpha, jobsCount, singleJobsProbability, smallJobsProbability, random, 0, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_2, "12");
//                        statistics(alpha, jobsCount, singleJobsProbability, smallJobsProbability, random, 0, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_1, "21");
//                        statistics(alpha, jobsCount, singleJobsProbability, smallJobsProbability, random, 0, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_2, "22");
                    }
  //                  statistics(alpha, jobsCount, singleJobsProbability, -1.0, random, 0);
                }
                for (double smallJobsProbability : SMALL_JOBS_PROBABILITY) {
                    for (int block : BLOCKS) {
//                        statistics(alpha, jobsCount, -1.0, smallJobsProbability, random, block, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_1, "11");
//                        statistics(alpha, jobsCount, -1.0, smallJobsProbability, random, block, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_2, "12");
//                        statistics(alpha, jobsCount, -1.0, smallJobsProbability, random, block, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_1, "21");
//                        statistics(alpha, jobsCount, -1.0, smallJobsProbability, random, block, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_2, "22");
                    }
                }
            }
        }
        System.out.println(maxRes);

        for (double alpha : ALPHA) {
            for (int jobsCount : JOBS_COUNT) {
                System.out.println("ALPHA = " + alpha);
                System.out.println("JOBS COUNT = " + jobsCount);
                String key = "" + alpha + "_" + jobsCount;

//                System.out.println("seria\t\t\twithSort\twithoutSort\tElb\t\tDlb\t\tSlb");
//                resortLast.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    double average = LoLb.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0);
//
//                    double dispersion = 0.0;
//                    for (Double solution : LoLb.get(key).get(e.getKey())) {
//                        dispersion += Math.pow(average - solution, 2);
//                    }
//                    dispersion = dispersion / LoLb.get(key).get(e.getKey()).size();
//
//                    String str = String.format(
//                            "%s\t\t%.2f | %d\t%.2f | %d\t%.2f\t%.2f\t%.2f",
//                            e.getKey(),
//                            resortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0),
//                            resortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).count(),
//                            noResortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0),
//                            noResortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).count(),
//                            average,
//                            dispersion,
//                            Math.sqrt(dispersion)
//                    );
//                    System.out.println(str);
////                    System.out.print(resortReverse.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0));
////                    System.out.print(" | " + resortReverse.get(key).get(e.getKey()).stream().filter(s -> s > 0).count());
////                    System.out.print("            ");
////                    System.out.print(resortRandom.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0));
////                    System.out.print(" | " + resortRandom.get(key).get(e.getKey()).stream().filter(s -> s > 0).count());
////                    System.out.print("            ");
////                    System.out.print(noResortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0));
////                    System.out.print(" | " + noResortLast.get(key).get(e.getKey()).stream().filter(s -> s > 0).count());
////                    System.out.print("            ");
////                    System.out.print(noResortReverse.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0));
////                    System.out.print(" | " + noResortReverse.get(key).get(e.getKey()).stream().filter(s -> s > 0).count());
////                    System.out.print("            ");
////                    System.out.print(noResortRandom.get(key).get(e.getKey()).stream().filter(s -> s > 0).mapToDouble(d -> d).average().orElse(0.0));
////                    System.out.print(" | " + noResortRandom.get(key).get(e.getKey()).stream().filter(s -> s > 0).count());
//                    System.out.println("            ");
//                });


//                System.out.println("seria\t\t\tavgAll2\t\tmax1\tmin1\tavgWeBetter\tavgWorse\tcountWeBetter");
////                System.out.println("               avgAll2                        max1                      min1                      avgWeBetter              avgWorse               countWeBetter");
//                all2Proc.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    String str = String.format(
//                            "%s\t\t%.2f\t\t%.2f\t%.2f\t%.2f\t\t%.2f\t\t%d",
//                            e.getKey(),
//                            all2Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).max().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).min().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).mapToDouble(d -> d).average().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().filter(d -> d > 0).mapToDouble(d -> d).average().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).count()
//                    );
//                    System.out.println(str);
//                    System.out.print(e.getKey());
//                    System.out.print("            ");
//                    System.out.print(all2Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).max().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).min().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().filter(d -> d > 0).mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).count());
//                    System.out.println("            ");
//                });
//
//                 System.out.println("                             avgAll2                        avgAll1                  avgAll1-avgAll2               avgAll1-LB              avgAll2-LB");
//                all2Proc.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    System.out.print(e.getKey());
//                    System.out.print("            ");
//                    System.out.print(all2Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).max().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1Minus2.get(key).get(e.getKey()).stream().mapToDouble(d -> d).min().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all1MinusLB.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.print("            ");
//                    System.out.print(all2MinusLB.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0));
//                    System.out.println("            ");
//                });
//                System.out.println("seria\t\t\tavgAll2\t\tavgAll1\t\tavg(All1-All2)\t\tavg(All1-LB)\tavg(All2-LB)");
//                all2Proc.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    String str = String.format(
//                            "%s\t\t%.2f\t\t%.2f\t\t%.2f\t\t\t\t%.2f\t\t\t%.2f",
//                            e.getKey(),
//                            all2Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0),
//                            all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).max().orElse(0.0),
//                            all1Minus2.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0),
//                            all1MinusLB.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0),
//                            all2MinusLB.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0)
//                    );
//                    System.out.println(str);
//                        });


//                System.out.println("                  E                        D                      S                      ");
//                expectedValues.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    System.out.println(e.getKey()
//                            + "     " + e.getValue()
//                            + "     " + dispersions.get(key).get(e.getKey())
//                            + "     " + averageQuadraticDeviations.get(key).get(e.getKey())
////                            + "     " + all2Proc.get(key).get(e.getKey())
//                    );
//                });
//                System.out.println("seria\t\t\t\tE\t\tD\t\tS\tEnewLB");
//                expectedValues.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    String str = String.format(
//                            "%s\t\t%.2f\t%.2f\t%.2f\t%.2f",
//                            e.getKey(),
//                            e.getValue(),
//                            dispersions.get(key).get(e.getKey()),
//                            averageQuadraticDeviations.get(key).get(e.getKey()),
//                            newLb.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0)
//                    );
//                    System.out.println(str);
//                        });
//                System.out.println("seria\t\t\t\tE\t\tD\t\tS\tEnewLB");
//                List<Double> averagesA1 = new ArrayList<>();
//                List<Double> dispersionsA1 = new ArrayList<>();
//                List<Double> averagesA2 = new ArrayList<>();
//                List<Double> dispersionsA2 = new ArrayList<>();
//                System.out.println();
//                System.out.println("single|small| Ea1  | Da1  | Ea2  | Da2");
//                System.out.println("-------------------------------------");
//                expectedValues.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                    double average = LoLb.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0);
//
//                    double dispersion = 0.0;
//                    for (Double solution : LoLb.get(key).get(e.getKey())) {
//                        dispersion += Math.pow(average - solution, 2);
//                    }
//                    dispersion = dispersion / LoLb.get(key).get(e.getKey()).size();
//                    String str = String.format(
//                            "%.1f   | %.1f | %.2f | %.2f | %.2f | %.2f ",
////                            "%.1f & %.1f & %.2f & %.2f & \\textbf{%.2f} & %.2f \\\\",
////                            "%d & %.1f & %.2f & %.2f & %.2f & %.2f \\\\",
//                            Double.parseDouble(e.getKey().split("_")[0]),
////                            Integer.parseInt(e.getKey().split("_")[3]),
//                            Double.parseDouble(e.getKey().split("_")[1]),
//                            e.getValue(),
//                            dispersions.get(key).get(e.getKey()),
//                            average,
//                            dispersion
//                    );
//                    System.out.println(str.replace(",", "."));
//                    averagesA1.add(e.getValue());
//                    dispersionsA1.add(dispersions.get(key).get(e.getKey()) );
//                    averagesA2.add(average);
//                    dispersionsA2.add(dispersion);
//                });
//                System.out.printf(
//                        "avg   |     | %.2f | %.2f | %.2f | %.2f ",
//                        averagesA1.stream().mapToDouble(d -> d).average().getAsDouble(),
//                        dispersionsA1.stream().mapToDouble(d -> d).average().getAsDouble(),
//                        averagesA2.stream().mapToDouble(d -> d).average().getAsDouble(),
//                        dispersionsA2.stream().mapToDouble(d -> d).average().getAsDouble()
//                        );
//                    System.out.println("seria\t\t\t\tE\t\tD\t\tS\tEnewLB");
//                    expectedValues.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
//                        double average = LoLb.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0);
//
//                        double dispersion = 0.0;
//                        for (Double solution : LoLb.get(key).get(e.getKey())) {
//                            dispersion += Math.pow(average - solution, 2);
//                        }
//                        dispersion = dispersion / LoLb.get(key).get(e.getKey()).size();
//                        String str = String.format(
//                                "%.1f & %.1f & %.2f & %.2f & \\textbf{%.2f} & %.2f \\\\",
//                                Double.parseDouble(e.getKey().split("_")[0]),
//                                Double.parseDouble(e.getKey().split("_")[1]),
//                                e.getValue(),
//                                dispersions.get(key).get(e.getKey()),
//                                average,
//                                dispersion
//                        );
//                        System.out.println(str.replace(",", "."));
//                    });

                List<Double> a1 = new ArrayList<>();
                List<Double> a2 = new ArrayList<>();
                List<Double> a3 = new ArrayList<>();
                List<Double> a4 = new ArrayList<>();
                List<Double> a5 = new ArrayList<>();
                List<Double> a6 = new ArrayList<>();
                List<Double> a7 = new ArrayList<>();
                System.out.println("single|small|  Ea2 |  E1  | MAXw | MAXb | AVGw | AVGb | COUNT");
                all2Proc.get(key).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
                    double a1v = all2Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0);
                    double a2v = all1Minus2.get(key).get(e.getKey()).stream().mapToDouble(d -> d).average().orElse(0.0);
                    double a3v = all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).max().orElse(0.0);
                    double a4v = all1Proc.get(key).get(e.getKey()).stream().mapToDouble(d -> d).min().orElse(0.0);
                    double a5v = all1Proc.get(key).get(e.getKey()).stream().filter(d -> d > 0).mapToDouble(d -> d).average().orElse(0.0);
                    double a6v = all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).mapToDouble(d -> d).average().orElse(0.0);
                    long a7v = all1Proc.get(key).get(e.getKey()).stream().filter(d -> d < 0).count();
                            String str = String.format(
                                    "%.1f   | %.1f | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %d ",
//                                    "%.1f & %.1f & %.2f & %.2f & %.2f & %.2f & %.2f & %.2f & %d \\\\",
                                    Double.parseDouble(e.getKey().split("_")[0]),
                                    Double.parseDouble(e.getKey().split("_")[1]),
                                    a1v, a2v, Math.max(a3v, 0.00), -a4v, a5v, -a6v, a7v
                            );
                            System.out.println(str.replace(",", "."));
                            a1.add(a1v);
                    a2.add(a2v);
                    a3.add(a3v);
                    a4.add(a4v);
                    a5.add(a5v);
                    a6.add(a6v);
                    a7.add((double) a7v);
                        });
//                System.out.println(
//                    a1.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a2.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a3.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a4.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a5.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a6.stream().mapToDouble(d -> d).average().getAsDouble() + " " +
//                            a7.stream().mapToDouble(d -> d).average().getAsDouble()
//                );
                System.out.println();
                System.out.println();
                System.out.println();

            }
        }

//        System.out.println("name a1 a2");
//        for (int i = 0; i < counter; i++) {
//            System.out.println(i + " " + a1solutions.get(i) + " " + a2solutions.get(i));
//        }
    }

    private static void statistics(double alpha, int jobsCount, double singleJobsProbability, double smallJobsProbability, Random random, int blocsCount,
                                    List<Integer> smallJobs, List<Integer> largeJobs, String seriaName) throws IOException {
        String key = "" + alpha + "_" + jobsCount;
        String keyKey = "" + singleJobsProbability + "_" + smallJobsProbability + "_" + seriaName + (blocsCount == 0 ? "" : "_" + blocsCount);
        if (!expectedValues.containsKey(key)) {
            expectedValues.put(key, new HashMap<>());
            dispersions.put(key, new HashMap<>());
            averageQuadraticDeviations.put(key, new HashMap<>());
        }
        List<Double> solutions = new LinkedList<>();
        for (int e = 0; e < EXAMPLES_COUNT; e++) {
            solutions.add(doAlgo(e, alpha, jobsCount, singleJobsProbability, smallJobsProbability, random, blocsCount, smallJobs, largeJobs, seriaName));
        }
        double average = solutions.stream().mapToDouble(d -> d).average().getAsDouble();

        double dispersion = 0.0;
        for (Double solution : solutions) {
            dispersion += Math.pow(average - solution, 2);
        }
        dispersion = dispersion / jobsCount;

//        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("result.txt", true))) {
//            bufferedWriter.write(buildSeriaName(alpha, jobsCount, singleJobsProbability, smallJobsProbability));
//            bufferedWriter.newLine();
//            bufferedWriter.write("" + average);
//            bufferedWriter.newLine();
//            bufferedWriter.write("" + dispersion);
//            bufferedWriter.newLine();
//        }
        expectedValues.get(key).put(keyKey, average);
        dispersions.get(key).put(keyKey, dispersion);
        averageQuadraticDeviations.get(key).put(keyKey, Math.sqrt(dispersion));
    }


    public static void run() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("jobs.txt")))) {
            String line = null;
            String ids = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] jobs = line.split("], \\[");
                List<Job> jobList = new ArrayList<>();
                for (int i = 0; i < jobs.length; i++) {
                    jobList.add(new Job(Integer.parseInt(jobs[i].split(", ")[0]), Integer.parseInt(jobs[i].split(", ")[1])));
                }
                double solution = apply(
                        jobList,
                        1,
                        1.5,
                        50,
                        0.5,
                        0.5,
                        0,
                        ""

                );
                System.out.println("update run_params set local_improvments_sol = " + solution + " where id > " + (Integer.parseInt(ids.split(" ")[0]) - 1) + " and id < " + (Integer.parseInt(ids.split(" ")[1]) + 1) + ";");
                ids = bufferedReader.readLine();
            }
        }
    }

    private static double doAlgo(int e, double alpha, int jobsCount, double singleJobsProbability, double smallJobsProbability, Random random, int blocsCount,
                                 List<Integer> smallJobs, List<Integer> largeJobs, String seriaName) throws IOException {
        List<Job> jobs = new LinkedList<>();
        if (blocsCount > 0) {
            List<Integer> volumes = new LinkedList<>();
            for (int i = 0; i < jobsCount; i++) {
                if (random.nextDouble() < smallJobsProbability) {
                    volumes.add(smallJobs.get(random.nextInt(smallJobs.size())));
                } else {
                    volumes.add(largeJobs.get(random.nextInt(largeJobs.size())));
                }
            }
            List<Integer> sortedValues = volumes.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < jobsCount; i+=blocsCount) {
                for (int j = 0; j < blocsCount - 1; j++) {
                    jobs.add(new Job(1, sortedValues.get(i) * 2));
                }
                jobs.add(new Job(2, sortedValues.get(i)));
            }
            if (jobs.size() % 2 != 0) {
                jobs.add(new Job(1, 0));
            }
        } else {
            for (int i = 0; i < jobsCount; i++) {
                int processorsCount;
                if (random.nextDouble() < singleJobsProbability) {
                    processorsCount = 1;
                } else {
                    processorsCount = 2;
                }

                int volume;
                if (smallJobsProbability == -1.0) {
                    throw new RuntimeException("ЭТА ОПЦИЯ НЕДОСТУПНА!!!!!!!!!!!");
//                    volume = BIG_SMALL_JOBS_VOLUMES.get(random.nextInt(BIG_SMALL_JOBS_VOLUMES.size()));
                } else {
                    if (random.nextDouble() < smallJobsProbability) {
                        volume = smallJobs.get(random.nextInt(smallJobs.size()));
                    } else {
                        volume = largeJobs.get(random.nextInt(largeJobs.size()));
                    }
                }

                jobs.add(new Job(processorsCount, volume));
            }
        }

//        jobs.clear();
//        jobs.addAll(List.of(
//                new Job(1, 20),
//                new Job(1, 100),
//                new Job(2, 50),
//                new Job(1, 350),
//                new Job(2, 200),
//                new Job(1, 424),
//                new Job(1, 650),
//                new Job(2, 350),
//                new Job(1, 724),
//                new Job(2, 800)
//        ));
        double res = apply(jobs, e, alpha, jobsCount, singleJobsProbability, smallJobsProbability, blocsCount, seriaName);
        if (res > maxRes) {
            maxRes = res;
        }
//        String key = "" + alpha + "_" + jobsCount;
//        String keyKey = "" + singleJobsProbability + "_" + smallJobsProbability + (blocsCount == 0 ? "" : "_" + blocsCount);
//        all2Proc.get(key).put(keyKey, applyAll2Proc(jobs, alpha, jobsCount));
        return res;
    }

    private static double apply(List<Job> jobs, int exampleNumber, double alpha, int jobsCount, double singleJobsProbability, double smallJobsProbability, int blocsCount,
                                String seriaName) throws IOException {
        List<Job> singleProcessorJobs = new LinkedList<>();
        for (Job job : jobs) {
            singleProcessorJobs.add(new Job(job.processorsCount, job.processorsCount == 1 ? job.volume / 2 : job.volume));
        }

        List<Job> sortedJobs = singleProcessorJobs.stream().sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());
        double totalSum = calculateTotalSum(sortedJobs, alpha, sortedJobs.size());
        double totalEnergy = calculateTotalEnergy(alpha, ENERGY / 2.0);
        double totalSumForDurationCalculation = Math.pow(totalSum, (1.0 / (alpha - 1.0)));
        for (int i = 0; i < sortedJobs.size(); i++) {
            Job job = sortedJobs.get(i);
            job.duration = calculateJobDuration(job, i + 1, totalSumForDurationCalculation, totalEnergy, alpha, sortedJobs.size());
        }

        // 6.5
        double lowerBound = Math.pow(totalSum, (alpha / (alpha - 1.0))) * totalEnergy;
        double myLowerBound = 0.0;
        for (int i = 0; i < sortedJobs.size(); i++) {
            myLowerBound = myLowerBound + sortedJobs.get(i).duration * (sortedJobs.size() - i);
        }

        List<Job> originalJobsWithDurations = calculateOriginalJobs(sortedJobs);
        var processors = assignJobsToProcessors(originalJobsWithDurations);
        double solution = calculateCSum(processors);

//        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("experiment/" + buildFileName(exampleNumber, alpha, jobsCount, singleJobsProbability, smallJobsProbability)))) {
//            bufferedWriter.write("" + solution / lowerBound);
//            bufferedWriter.newLine();
//            bufferedWriter.write("" + lowerBound);
//            bufferedWriter.newLine();
//            bufferedWriter.write("" + solution);
//            bufferedWriter.newLine();
//            for (Job job : originalJobsWithDurations) {
//                bufferedWriter.write(job.toString());
//                bufferedWriter.newLine();
//            }
//            bufferedWriter.newLine();
//            for (Processor processor : processors) {
//                bufferedWriter.write(processor.toString());
//                bufferedWriter.newLine();
//            }
//        }
        double all2 = applyAll2Proc(jobs, alpha, sortedJobs.size());
        double all1 = applyAll1Proc(jobs, alpha, sortedJobs.size());
        String key = "" + alpha + "_" + jobsCount;
        String keyKey = "" + singleJobsProbability + "_" + smallJobsProbability + "_" + seriaName + (blocsCount == 0 ? "" : "_" + blocsCount);
        if (!all2Proc.containsKey(key)) {
            all2Proc.put(key, new HashMap<>());
            all1Proc.put(key, new HashMap<>());
            resortLast.put(key, new HashMap<>());
            resortReverse.put(key, new HashMap<>());
            resortRandom.put(key, new HashMap<>());
            noResortLast.put(key, new HashMap<>());
            noResortReverse.put(key, new HashMap<>());
            noResortRandom.put(key, new HashMap<>());
            all1Minus2.put(key, new HashMap<>());
            all1MinusLB.put(key, new HashMap<>());
            all2MinusLB.put(key, new HashMap<>());
            LoLb.put(key, new HashMap<>());
            newLb.put(key, new HashMap<>());
        }
        if (!all2Proc.get(key).containsKey(keyKey)) {
            all2Proc.get(key).put(keyKey, new ArrayList<>());
            all1Proc.get(key).put(keyKey, new ArrayList<>());
            resortLast.get(key).put(keyKey, new ArrayList<>());
            resortReverse.get(key).put(keyKey, new ArrayList<>());
            resortRandom.get(key).put(keyKey, new ArrayList<>());
            noResortLast.get(key).put(keyKey, new ArrayList<>());
            noResortReverse.get(key).put(keyKey, new ArrayList<>());
            noResortRandom.get(key).put(keyKey, new ArrayList<>());
            all1Minus2.get(key).put(keyKey, new ArrayList<>());
            all1MinusLB.get(key).put(keyKey, new ArrayList<>());
            all2MinusLB.get(key).put(keyKey, new ArrayList<>());
            LoLb.get(key).put(keyKey, new ArrayList<>());
            newLb.get(key).put(keyKey, new ArrayList<>());
        }
        double next = doOptimizationToNextBlock(sortedJobs);
        double nextOdd = doOptimizationToNextOddBlock(originalJobsWithDurations);

        double localImprovements = doLocalImprovements(processors, sortedJobs);
        double bestFound = Math.min(Math.min(localImprovements, next), solution);
        a1solutions.put(counter,solution);
        a2solutions.put(counter, bestFound);
        counter++;

        if (bestFound - next > 0.001) {
            System.out.println("!!!!!!!!!!!!!!!!!!!! " + buildSeriaName(alpha, jobsCount, singleJobsProbability, smallJobsProbability));
            System.out.println(next + " vs " + bestFound);
        }

        all1Proc.get(key).get(keyKey).add((bestFound - all1) / all1 * 100);
        all2Proc.get(key).get(keyKey).add((bestFound - lowerBound) / lowerBound * 100);
        all1Minus2.get(key).get(keyKey).add((all1 - all2) / all2 * 100);
        all1MinusLB.get(key).get(keyKey).add((all1 - lowerBound) / lowerBound * 100);
        all2MinusLB.get(key).get(keyKey).add((all2 - lowerBound) / lowerBound * 100);

//        System.out.println(solution + "          " + all2 + "              " + all1);
//        double randomOptimization = doOptimizationRandomSort(sortedJobs);
//        double reverseOptimization = doOptimizationReverseSort(sortedJobs);
        resortLast.get(key).get(keyKey).add((solution - bestFound) / bestFound * 100);
//        resortReverse.get(key).get(keyKey).add((solution - reverseOptimization) / reverseOptimization * 100);
//        resortRandom.get(key).get(keyKey).add((solution - randomOptimization) / randomOptimization * 100);
//        System.out.println(solution + "          " + resortOptimization + "          " + randomOptimization + "          " + reverseOptimization);

//        double randomOptimizationNo = doOptimizationRandomWithoutReSort(originalJobsWithDurations);
//        double reverseOptimizationNo = doOptimizationReverseWithoutReSort(originalJobsWithDurations);
//        noResortLast.get(key).get(keyKey).add((solution - resortOptimizationNo) / resortOptimizationNo * 100);
        LoLb.get(key).get(keyKey).add((bestFound - lowerBound) / lowerBound * 100);
//        noResortReverse.get(key).get(keyKey).add((solution - reverseOptimizationNo) / reverseOptimizationNo * 100);
//        noResortRandom.get(key).get(keyKey).add((solution - randomOptimizationNo) / randomOptimizationNo * 100);
//        System.out.println(solution + "          " + resortOptimizationNo + "          " + randomOptimizationNo + "          " + reverseOptimizationNo);
//        System.out.println();
//        System.out.println();


        List<Job> twoProcessorJobs = new LinkedList<>();
        for (Job job : jobs) {
            twoProcessorJobs.add(new Job(job.processorsCount, job.processorsCount == 1 ? job.volume : job.volume * 2));
        }
        List<Job> sortedJobs2 = twoProcessorJobs.stream().sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());

        if (sortedJobs2.stream().filter(j -> j.processorsCount == 1).map(j -> j.volume).max(Integer::compareTo).orElse(0) <
                sortedJobs2.stream().filter(j -> j.processorsCount == 2).map(j -> j.volume).min(Integer::compareTo).orElse(0)) {

            double newLbValue = calculateNewLB(ENERGY, alpha, sortedJobs2);
            newLb.get(key).get(keyKey).add((solution - newLbValue) / newLbValue * 100);
        }
//        return bestFound;
        return (solution - lowerBound) / lowerBound * 100;
    }

    private static double calculateNewLB(double energy, double alpha, List<Job> jobs) {
        double coeff = Math.pow(energy, 1 / (1 - alpha)) / 2;
        double sum = 0.0;
        for (int i = 0; i < jobs.size(); i++) {
            sum += jobs.get(i).volume * Math.pow((jobs.size() - (i + 1) + 0.5 + 1.0 / jobs.get(i).processorsCount), (alpha - 1.0) / alpha);
        }
        return coeff * Math.pow(sum, alpha / (alpha - 1));
    }

    private static List<Job> calculateOriginalJobs(List<Job> sortedJobs) {
        List<Job> originalJobsWithDurations = new LinkedList<>();
        for (Job job : sortedJobs) {
            Job createdJob = new Job(job.processorsCount, job.processorsCount == 1 ? job.volume * 2 : job.volume);
            createdJob.duration = job.processorsCount == 1 ? job.duration * 2 : job.duration;
            originalJobsWithDurations.add(createdJob);
        }
        return originalJobsWithDurations;
    }

    private static List<Processor> assignJobsToProcessors(List<Job> originalJobsWithDurations) {
        List<Processor> processors = new LinkedList<>();
        for (int i = 0; i < MAX_PROCESSORS; i++) {
            processors.add(new Processor());
        }

        for (int i = 0; i < originalJobsWithDurations.size(); i++) {
            Job job = originalJobsWithDurations.get(i);
            if (job.processorsCount == 1) {
                int processorIndex = -1;
                double startTime = Double.MAX_VALUE;
                for (int j = 0; j < processors.size(); j++) {
                    double earliestTime = processors.get(j).whereCanAdd(job.duration);
                    if (earliestTime < startTime) {
                        startTime = earliestTime;
                        processorIndex = j;
                    }
                }
                processors.get(processorIndex).addInterval(startTime, job.duration);
                processors.get(processorIndex).assignedJobs.add(job);
            } else {
                double startTime = Double.MIN_VALUE;
                for (Processor processor : processors) {
                    double earliestTime = processor.getTheEarliestTime();
                    if (earliestTime > startTime) {
                        startTime = earliestTime;
                    }
                }
                for (Processor processor : processors) {
                    processor.addInterval(startTime, job.duration);
                    processor.assignedJobs.add(job);
                }
            }
        }

        for (Processor processor : processors) {
            processor.optimize();
        }
        return processors;
    }

    private static double calculateCSum(List<Processor> processors) {
        double solution = 0.0;
        for (int i = 0; i < processors.get(0).intervals.size(); i++) {
            solution += processors.get(0).intervals.get(i).getValue();
        }
        for (int i = 0; i < processors.get(1).assignedJobs.size(); i++) {
            if (processors.get(1).assignedJobs.get(i).processorsCount == 1) {
                solution += processors.get(1).intervals.get(i).getValue();
            }
        }
        return solution;
    }

    private static double doOptimizationToNextBlock(List<Job> sortedJobs) {
        List<List<Job>> blocks = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        Job job = null;
        int jobIndex = 0;
//        for (List<Job> block : blocks) {
//            if ((block.size() % 2) == 0) {
//                if (job == null) {
//                    job = block.get(block.size() - 2);
//                    for (int i = 0; i < block.size() - 2; i++) {
//                        newList.add(block.get(i));
//                    }
//                    jobIndex = newList.size();
//                    newList.add(block.get(block.size() - 1));
//                } else {
//                    newList.add(job);
//                    job = null;
//                    newList.addAll(block);
//                }
//            } else {
//                newList.addAll(block);
//            }
//        }
        for (List<Job> block : blocks) {
            if ((block.size() % 2) == 0) {
                if (job == null) {
                    job = block.get(block.size() - 2);
                    for (int i = 0; i < block.size() - 2; i++) {
                        newList.add(block.get(i));
                    }
                    jobIndex = newList.size();
                    newList.add(block.get(block.size() - 1));
                } else {
                    newList.add(job);
                    job = null;
                    newList.addAll(block);
                }
            } else {
                if (job != null && block.size() > 1) {
                    newList.add(job);
                    job = block.get(block.size() - 2);
                    for (int i = 0; i < block.size() - 2; i++) {
                        newList.add(block.get(i));
                    }
                    jobIndex = newList.size();
                    newList.add(block.get(block.size() - 1));
                } else {
                    newList.addAll(block);
                }
            }
        }
        if (job != null) {
            newList.add(jobIndex, job);
        }
        List<Job> jobs = calculateOriginalJobs(newList);
        int singleJobsCount = 0;
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).processorsCount == 2) {
                if (singleJobsCount > 2) {
                    Job job1 = jobs.remove(i-2);
                    Job job2 = jobs.remove(i-2);
                    jobs.add(i-2, job2);
                    jobs.add(i-1, job1);
                }
                singleJobsCount = 0;
            } else {
                singleJobsCount++;
            }
        }
        var processors = assignJobsToProcessors(jobs);
        double solLo = calculateCSum(processors);
        double last = doOptimizationWithReSortLast(newList);
        double random = doOptimizationRandomSort(newList);
        double reverse = doOptimizationReverseSort(newList);
//        if ((solLo - last) / last * 100 > 1) {
//            System.out.println("last : " + (solLo - last) / last * 100);
//        } else if ((solLo - random) / random * 100 > 1) {
//            System.out.println("random : " + (solLo - random) / random * 100);
//        } else if ((solLo - reverse) / reverse * 100 > 1) {
//            System.out.println("reverse : " + (solLo - reverse) / reverse * 100);
//        }
//        return Math.min(solLo, random);
        return solLo;
    }

    private static double doLocalImprovements(List<Processor> processors, List<Job> sortedJobs) {
        List<Block> blocks = getByObjectBlocks(processors);
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).jobsList.size() % 2 == 0 && blocks.get(i).jobsList.size() > 1 && i != blocks.size()-1) {
                Job job = blocks.get(i).jobsList.remove(blocks.get(i).jobsList.size() - 2);
                if (blocks.get(i + 1).empty > job.duration) {
                    blocks.get(i + 1).jobsList.add(blocks.get(i + 1).jobsList.size() - 1,job);
                    jobs.addAll(blocks.get(i).jobsList);
                    i++;
                    jobs.addAll(blocks.get(i).jobsList);
                } else {
                    if (blocks.subList(i+1, blocks.size()).stream().anyMatch(b -> b.jobsList.size() > 1)) {
//                        if (blocks.subList(i+1, blocks.size()).stream().anyMatch(b -> b.empty > job.duration)) {
                        blocks.get(i + 1).jobsList.add(blocks.get(i + 1).jobsList.size() > 1 ? blocks.get(i + 1).jobsList.size() - 2 : 0, job);
                    } else {
                        blocks.get(i).jobsList.add(blocks.get(i).jobsList.size() > 1 ? blocks.get(i).jobsList.size() - 1 : 0, job);
                    }
                    jobs.addAll(blocks.get(i).jobsList);
                }
            } else {
                jobs.addAll(blocks.get(i).jobsList);
            }
        }

        int singleJobsCount = 0;
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).processorsCount == 2) {
                if (singleJobsCount > 2) {
                    Job job1 = jobs.remove(i-2);
                    Job job2 = jobs.remove(i-2);
                    jobs.add(i-2, job2);
                    jobs.add(i-1, job1);
                }
                singleJobsCount = 0;
            } else {
                singleJobsCount++;
            }
        }


        List<List<Job>> blocksList = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        Job job = null;
        int jobIndex = 0;
        for (List<Job> block : blocksList) {
            if ((block.size() % 2) == 0) {
                if (job == null) {
                    job = block.get(block.size() - 2);
                    for (int i = 0; i < block.size() - 2; i++) {
                        newList.add(block.get(i));
                    }
                    jobIndex = newList.size();
                    newList.add(block.get(block.size() - 1));
                } else {
                    newList.add(job);
                    job = null;
                    newList.addAll(block);
                }
            } else {
                newList.addAll(block);
            }
        }
        if (job != null) {
            newList.add(jobIndex, job);
        }
        List<Job> jobsNext = calculateOriginalJobs(newList);

        var newProcessorsLI = assignJobsToProcessors(jobs);
        var newProcessorsNext = assignJobsToProcessors(jobsNext);
//        if (calculateCSum(newProcessorsLI) - calculateCSum(newProcessorsNext) > 0.0001) {
//            System.out.println(calculateCSum(newProcessorsLI));
//            System.out.println(calculateCSum(newProcessorsNext));
//            System.out.println("!");
//        }
        var solRand = doOptimizationRandomSort(jobs);
        var sol = calculateCSum(newProcessorsLI);
//        return Math.min(solRand, sol);

        return sol;
    }

    private static double doOptimizationToNextOddBlock(List<Job> sortedJobs) {
        List<List<Job>> blocks = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        Job job = null;
        int jobIndex = 0;
        for (List<Job> block : blocks) {
            if (block.size() % 2 == 0) {
                if (job == null) {
                    job = block.get(block.size() - 2);
                    for (int i = 0; i < block.size() - 2; i++) {
                        newList.add(block.get(i));
                    }
                    jobIndex = newList.size();
                    newList.add(block.get(block.size() - 1));
                } else {
                    newList.add(job);
                    job = null;
                    newList.addAll(block);
                }
            } else {
                newList.addAll(block);
            }
        }
        if (job != null) {
            newList.add(jobIndex, job);
        }
        List<Job> jobs = calculateOriginalJobs(newList);
        var processors = assignJobsToProcessors(jobs);
        return calculateCSum(processors);
    }

    private static double doOptimizationWithReSortLast(List<Job> sortedJobs) {
        List<List<Job>> blocks = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        for (List<Job> block : blocks) {
            if (block.size() % 2 == 0) {
                newList.add(block.get(block.size() - 2));
                for (int i = 0; i < block.size() - 2; i++) {
                    newList.add(block.get(i));
                }
                newList.add(block.get(block.size() - 1));
            } else {
                newList.addAll(block);
            }
        }
        List<Job> jobs = calculateOriginalJobs(newList);
        var processors = assignJobsToProcessors(jobs);
        return calculateCSum(processors);
    }

    private static double doOptimizationRandomSort(List<Job> sortedJobs) {
        List<List<Job>> blocks = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        for (List<Job> block : blocks) {
            if (block.size() % 2 == 0) {
                List<Job> tmp = new ArrayList<>(block.subList(0, block.size() - 1));
                Collections.shuffle(tmp);
                newList.addAll(tmp);
                newList.add(block.get(block.size() - 1));
            } else {
                newList.addAll(block);
            }
        }
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            List<Job> jobs = calculateOriginalJobs(newList);
            var processors = assignJobsToProcessors(jobs);
            double solution = calculateCSum(processors);
            if (solution < best) {
                best = solution;
            }
        }
        return best;
    }

    private static double doOptimizationReverseSort(List<Job> sortedJobs) {
        List<List<Job>> blocks = getByBlocks(sortedJobs);
        List<Job> newList = new ArrayList<>();
        for (List<Job> block : blocks) {
            if (block.size() % 2 == 0) {
                List<Job> tmp = new ArrayList<>(block.subList(0, block.size() - 1));
                Collections.reverse(tmp);
                newList.addAll(tmp);
                newList.add(block.get(block.size() - 1));
            } else {
                newList.addAll(block);
            }
        }
        List<Job> jobs = calculateOriginalJobs(newList);
        var processors = assignJobsToProcessors(jobs);
        return calculateCSum(processors);
    }

    private static List<List<Job>> getByBlocks(List<Job> jobs) {
        List<List<Job>> blocks = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            List<Job> singleJobs = new ArrayList<>();
            Job job = jobs.get(i);
            while (job.processorsCount != 2) {
                singleJobs.add(job);
                i++;
                if (i != jobs.size()) {
                    job = jobs.get(i);
                } else {
                    break;
                }
            }
            if (job.processorsCount == 2) {
                singleJobs.add(job);
            }
            blocks.add(singleJobs);
        }
        return blocks;
    }

    private static List<Block> getByObjectBlocks(List<Processor> processors) {
        List<Block> blocks = new ArrayList<>(processors.get(0).getBlocks());
        int i = 0;
        for (Block block : processors.get(1).getBlocks()) {
            blocks.get(i).getJobs().addAll(block.getJobs());
            List<Job> jobsList = new ArrayList<>(blocks.get(i).getJobs());
            var twoProcessorJobOptional = jobsList.stream().filter(j -> j.processorsCount == 2).findFirst();
            boolean isTwoProcessorJobFound = twoProcessorJobOptional.isPresent();
            if (isTwoProcessorJobFound) {
                jobsList.remove(twoProcessorJobOptional.get());
            }
            jobsList.sort((job, t1) -> {
                if (job.duration - t1.duration > 0) {
                    return 1;
                } else if (job.duration - t1.duration < 0) {
                    return -1;
                }
                return 0;
            });
            if (isTwoProcessorJobFound) {
                jobsList.add(twoProcessorJobOptional.get());
            }
            blocks.get(i).jobsList = jobsList;
            blocks.get(i).empty = Math.max(blocks.get(i).empty, block.empty);
            blocks.get(i).processorIndexMostEmpty = blocks.get(i).empty > block.empty ? 0 : 1;
            if (blocks.get(i).duration != block.duration) {
                System.out.println("ERROR!!!!!!!!!!!!!!!!!!!");
            }
            i++;
        }
        if (i < blocks.size()) {
            blocks.get(i).jobsList = new ArrayList<>(blocks.get(i).getJobs());
        }
        return blocks;
    }
//
//    private static double doOptimizationWithoutReSort(List<Job> sortedJobs) {
//        List<List<Job>> blocks = getByBlocks(sortedJobs);
//        List<Job> newList = new ArrayList<>();
//        for (List<Job> block : blocks) {
//            if (block.size() % 2 == 0) {
//                newList.add(block.get(block.size() - 2));
//                for (int i = 0; i < block.size() - 2; i++) {
//                    newList.add(block.get(i));
//                }
//                newList.add(block.get(block.size() - 1));
//            } else {
//                newList.addAll(block);
//            }
//        }
//        return assignJobsToProcessors(newList);
//    }
//
//    private static double doOptimizationRandomWithoutReSort(List<Job> sortedJobs) {
//        List<List<Job>> blocks = getByBlocks(sortedJobs);
//        List<Job> newList = new ArrayList<>();
//        for (List<Job> block : blocks) {
//            if (block.size() % 2 == 0) {
//                List<Job> ga_result.csv = new ArrayList<>(block.subList(0, block.size() - 1));
//                Collections.shuffle(ga_result.csv);
//                newList.addAll(ga_result.csv);
//                newList.add(block.get(block.size() - 1));
//            } else {
//                newList.addAll(block);
//            }
//        }
//        double best = Double.MAX_VALUE;
//        for (int i = 0; i < 10; i++) {
//            double solution = assignJobsToProcessors(newList);
//            if (solution < best) {
//                best = solution;
//            }
//        }
//        return best;
//    }
//
//    private static double doOptimizationReverseWithoutReSort(List<Job> sortedJobs) {
//        List<List<Job>> blocks = getByBlocks(sortedJobs);
//        List<Job> newList = new ArrayList<>();
//        for (List<Job> block : blocks) {
//            if (block.size() % 2 == 0) {
//                List<Job> ga_result.csv = new ArrayList<>(block.subList(0, block.size() - 1));
//                Collections.reverse(ga_result.csv);
//                newList.addAll(ga_result.csv);
//                newList.add(block.get(block.size() - 1));
//            } else {
//                newList.addAll(block);
//            }
//        }
//        return assignJobsToProcessors(newList);
//    }

    private static double applyAll2Proc(List<Job> jobs, double alpha, int jobsCount) {
        List<Job> singleProcessorJobs = new LinkedList<>();
        for (Job job : jobs) {
            singleProcessorJobs.add(new Job(job.processorsCount, job.processorsCount == 1 ? job.volume : job.volume * 2));
        }

        List<Job> sortedJobs = singleProcessorJobs.stream().sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());
        double totalSum = calculateTotalSum(sortedJobs, alpha, jobsCount);
        double totalEnergy = calculateTotalEnergy(alpha, ENERGY);
        double totalSumForDurationCalculation = Math.pow(totalSum, (1.0 / (alpha - 1.0)));
        for (int i = 0; i < jobsCount; i++) {
            Job job = sortedJobs.get(i);
            job.duration = calculateJobDuration(job, i + 1, totalSumForDurationCalculation, totalEnergy, alpha, jobsCount);
        }

        // 6.5
        return Math.pow(totalSum, (alpha / (alpha - 1.0))) * totalEnergy / 2;
    }

    private static double applyAll1Proc(List<Job> jobs, double alpha, int jobsCount) {
        List<Job> singleProcessorJobs = new LinkedList<>();
        for (Job job : jobs) {
            singleProcessorJobs.add(new Job(job.processorsCount, job.processorsCount == 1 ? job.volume : job.volume * 2));
        }

        List<Job> sortedJobs = singleProcessorJobs.stream().sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());
        double totalSum = calculateTotalSum1Proc(sortedJobs, alpha, jobsCount);
        for (int i = 0; i < jobsCount; i+=2) {
            Job job1 = sortedJobs.get(i);
            Job job2 = sortedJobs.get(i + 1);

            job1.duration = calculateJob1ProcDuration(job1, i + 1, totalSum, alpha, jobsCount);
            job2.duration = calculateJob1ProcDuration(job2, i + 1, totalSum, alpha, jobsCount);
        }

        List<Processor> processors = new LinkedList<>();
        for (int i = 0; i < MAX_PROCESSORS; i++) {
            processors.add(new Processor());
        }

        for (int i = 0; i < jobsCount; i+=2) {
            Job job1 = sortedJobs.get(i);
            double startTime1 = processors.get(0).whereCanAdd(job1.duration);
            processors.get(0).addInterval(startTime1, job1.duration);
            processors.get(0).assignedJobs.add(job1);

            Job job2 = sortedJobs.get(i+1);
            double startTime2 = processors.get(1).whereCanAdd(job2.duration);
            processors.get(1).addInterval(startTime2, job2.duration);
            processors.get(1).assignedJobs.add(job2);
        }

        double solution = 0.0;
        for (int i = 0; i < processors.get(0).intervals.size(); i++) {
            solution += processors.get(0).intervals.get(i).getValue();
            solution += processors.get(1).intervals.get(i).getValue();
        }
        return solution;
    }

    // 6.4
    private static double calculateJobDuration(Job job, int jobIndex, double totalSum, double totalEnergy, double alpha, int jobsCount) {
        return job.volume / Math.pow((jobsCount - jobIndex + 1), 1.0 / alpha) * totalSum * totalEnergy;
    }

    private static double calculateJob1ProcDuration(Job job, int jobIndex, double totalSum, double alpha, int jobsCount) {
        return Math.pow((jobsCount - jobIndex + 1), - 1.0 / alpha) * job.volume * totalSum;
    }

    private static double calculateTotalSum(List<Job> jobs, double alpha, int jobsCount) {
        double sum = 0.0;
        for (int i = 0; i < jobs.size(); i++) {
            sum += jobs.get(i).volume * Math.pow((jobsCount - (i + 1) + 1), (alpha - 1.0) / alpha);
        }
        return sum;
    }

    private static double calculateTotalSum1Proc(List<Job> jobs, double alpha, int jobsCount) {
        double sum = 0.0;
        for (int i = 0; i < jobs.size(); i+=2) {
            sum += (jobs.get(i).volume + jobs.get(i + 1).volume) * Math.pow((jobsCount - (i + 1) + 1), (alpha - 1.0) / alpha);
        }
        return Math.pow(sum / ENERGY, 1.0 / (alpha - 1));
    }

    private static double calculateTotalEnergy(double alpha, double energy) {
        return Math.pow(energy, (1.0 / (1.0 - alpha)));
    }

    private static String buildFileName(int exampleNumber, double alpha, int jobsCount, double singleJobsProbability, double smallJobsProbability) {
        return buildSeriaName(alpha, jobsCount, singleJobsProbability, smallJobsProbability) + "_" + exampleNumber + ".txt";
    }

    private static String buildSeriaName(double alpha, int jobsCount, double singleJobsProbability, double smallJobsProbability) {
        return alpha + "alpha_" + jobsCount + "jobs_" + singleJobsProbability  + "single_" + smallJobsProbability  + "small";
    }

    public static class Job {
        public final int processorsCount;
        public final int volume;
        public double duration;

        public Job(int processorsCount, int volume) {
            this.processorsCount = processorsCount;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return "[" + processorsCount + "; " + volume + "; " + duration + "]";
        }
    }

    public static class Processor {
        public List<Pair<Double, Double>> intervals = new LinkedList<>();
        public List<Job> assignedJobs = new LinkedList<>();

        public void addInterval(double start, double duration) {
            intervals.add(new Pair<>(start, start + duration));
            intervals = intervals.stream().sorted((i1, i2) -> (int) (i1.getKey() - i2.getKey())).collect(Collectors.toList());
        }

        public double getTheEarliestTime() {
            return intervals.stream().map(Pair::getValue).max(Double::compareTo).orElse(0.0);
        }

        public double whereCanAdd(double duration) {
            if (intervals.isEmpty()) {
                return 0.0;
            }
            if (intervals.size() == 1) {
                return intervals.get(0).getValue();
            }
            for (int i = 1; i < intervals.size(); i++) {
                if (intervals.get(i).getKey() - intervals.get(i - 1).getValue() >= duration) {
                    return intervals.get(i - 1).getValue();
                }
            }
            return getTheEarliestTime();
        }

        @Override
        public String toString() {
            return "\nJobs: " + assignedJobs.toString() + ";\nIntervals" + intervals.toString() + ";";
        }

        public List<Block> getBlocks() {
            List<Block> blocks = new ArrayList<>();
            Block block = new Block();
            for (int i = 0; i < assignedJobs.size(); i++) {
                block.getJobs().add(assignedJobs.get(i));
                if (assignedJobs.get(i).processorsCount == 2) {
                    block.duration = intervals.get(i).getValue();
                    block.empty = intervals.get(i).getKey() - (i > 0 ? intervals.get(i-1).getValue() : 0.0);
                    blocks.add(block);
                    block = new Block();
                }
            }
            if (block.getJobs().size() > 0 && !blocks.contains(block)) {
                blocks.add(block);
            }
            return blocks;
        }

        public void optimize() {
//            Map<Job, Integer> oldIndexes = new HashMap<>();
//            Map<Job, Integer> correctIndexes = new HashMap<>();
            Map<Integer, Job> correctIndexes2 = new HashMap<>();
            var jobs = assignedJobs.stream().filter(j -> j.processorsCount == 1).sorted((job, t1) -> {
                if (job.duration - t1.duration < 0) {
                    return -1;
                } else if (job.duration > t1.duration) {
                    return 1;
                }
                return 0;
            }).collect(Collectors.toList());

            for (int i = 0; i < assignedJobs.size(); i++) {
                var curJob = assignedJobs.get(i);
                if (curJob.processorsCount == 2) {
                    jobs.add(i, curJob);
                }
            }

            for (int i = 0; i < assignedJobs.size(); i++) {
                var curJob = assignedJobs.get(i);
                    correctIndexes2.put(jobs.indexOf(curJob), curJob);

            }

            List<Job> newAssignedJobs = new ArrayList<>();
            List<Pair<Double, Double>> newIntervals = new ArrayList<>();
            double startTime = 0.0;
            for (int i = 0; i < assignedJobs.size(); i++) {
                newAssignedJobs.add(correctIndexes2.get(i));
                if (assignedJobs.get(i).processorsCount == 1) {
                    newIntervals.add(new Pair<>(startTime, startTime + correctIndexes2.get(i).duration));
                    startTime += correctIndexes2.get(i).duration;
                } else {
                    newIntervals.add(intervals.get(i));
                    startTime = intervals.get(i).getValue();
                }
            }

            assignedJobs = newAssignedJobs;
            intervals = newIntervals;
        }
    }


}

class Block {
    private final Set<Main.Job> jobs = new HashSet<>();
    public List<Main.Job> jobsList;
    public double empty = 0.0;
    public double duration = 0.0;
    public int processorIndexMostEmpty = -1;

    public Set<Main.Job> getJobs() {
        return jobs;
    }
}
