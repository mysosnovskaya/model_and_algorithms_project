package ga;

import ga.model.GaJob;
import ga.model.GaParams;
import ga.model.GaProcessor;
import ga.model.GaResult;
import ga.model.ProblemInstance;
import ga.model.types.CrossoverType;
import ga.model.types.MutationType;
import ga.model.types.SelectionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ga.FitnessCalculator.calculateLowerBound;
import static ga.model.types.CrossoverType._1PX;

public class GaRunner {
    private static final File RESULT_FILE = new File("adaptive_optimized.csv");
    private static final File AVERAGE_SOLUTION_FILE = new File("adaptive_optimized_avg_solutions.csv");
    private static Integer ID = 1;

    private static final List<Double> ALPHA = List.of(1.5/*, 2.0, 2.5, 3.0*/);
    private static final List<Integer> JOBS_COUNT = List.of(50/*, 100*/);
    private static final List<Double> SINGLE_JOBS_PROBABILITY = List.of(0.5/*0.3, 0.5, 0.7*/);
    private static final List<Double> SMALL_JOBS_PROBABILITY = List.of(0.5/*0.0, 0.3, 0.5, 0.7, 1.0*/);

    private static final int EXAMPLES_COUNT = 30;
    public static final List<Integer> SMALL_JOBS_VOLUMES_1 = List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
    public static final List<Integer> SMALL_JOBS_VOLUMES_2 = List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
    public static final List<Integer> LARGE_JOBS_VOLUMES_1 = List.of(200, 275, 350, 425, 500, 575, 650, 725, 800, 875);
    public static final List<Integer> LARGE_JOBS_VOLUMES_2 = List.of(520, 540, 560, 580, 600, 620, 640, 660, 680, 700);
    private static final List<Integer> BLOCKS = List.of(2/*, 4, 6, 8, 10*/);

    private static final int ENERGY = 100_000;

    private final static int RANDOM_SEED = 578395;
    private static final Random RANDOM = new Random(RANDOM_SEED);

    public static void main(String[] args) throws IOException {
        var listOfListJobs = read("jobs.txt");
        var listOfListBlockJobs = read("jobs_blocks.txt");

//        GeneticAlgorithm.main(null); // для разогрева
        for (double alpha : ALPHA) {
            for (int jobsCount : JOBS_COUNT) {
                for (double singleJobsProbability : SINGLE_JOBS_PROBABILITY) {
                    for (double smallJobsProbability : SMALL_JOBS_PROBABILITY) {
                        for (int e = 0; e < EXAMPLES_COUNT; e++) {
                            ProblemInstance instance =
//                                    new ProblemInstance(new ArrayList<>());
//                                    generateProblemInstanceByParams(0, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_1, singleJobsProbability, alpha, e, ENERGY, "11");
//                                    generateProblemInstanceByParams(0, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_2, singleJobsProbability, alpha, e, ENERGY, "12");
//                                    generateProblemInstanceByParams(0, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_1, singleJobsProbability, alpha, e, ENERGY, "21");
//                                    generateProblemInstanceByParams(0, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_2, singleJobsProbability, alpha, e, ENERGY, "22");
                                    generateProblemInstanceWithJobs(listOfListJobs.get(e), 0, 0.5, 0.5, 1.5, e, ENERGY, "11");
                            if (!instance.jobs.isEmpty()) {
                                System.out.println("ALPHA = " + alpha + "; JOBS_COUNT = " + jobsCount + "; SINGLE_JOBS_PROBABILITY = " + singleJobsProbability + "; SMALL_JOBS_PROBABILITY = " + smallJobsProbability + "; e = " + e);
                                runGa(instance);
                            }
                        }
                    }
                }
                for (double smallJobsProbability : SMALL_JOBS_PROBABILITY) {
                    for (int block : BLOCKS) {
                        for (int e = 0; e < EXAMPLES_COUNT; e++) {
                            ProblemInstance instance =
                                new ProblemInstance(new ArrayList<>());
//                                    generateProblemInstanceByParams(block, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_1, -1.0, alpha, e, ENERGY, "11");
//                                    generateProblemInstanceByParams(block, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_1, LARGE_JOBS_VOLUMES_2, -1.0, alpha, e, ENERGY, "12");
//                                    generateProblemInstanceByParams(block, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_1, -1.0, alpha, e, ENERGY, "21");
//                                    generateProblemInstanceByParams(block, jobsCount, RANDOM, smallJobsProbability, SMALL_JOBS_VOLUMES_2, LARGE_JOBS_VOLUMES_2, -1.0, alpha, e, ENERGY, "22");
//                                    generateProblemInstanceWithJobs(listOfListBlockJobs.get(e), 0, 0.5, 0.5, 1.5, e, ENERGY, "11");
                            if (!instance.jobs.isEmpty()) {
                                System.out.println("ALPHA = " + alpha + "; JOBS_COUNT = " + jobsCount + "; BLOCK_SIZE = " + block + "; SMALL_JOBS_PROBABILITY = " + smallJobsProbability + "; e = " + e);
//                                runGa(instance);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void runGa(ProblemInstance problemInstance) throws IOException {
        double lowerBound = calculateLowerBound(problemInstance.jobs, problemInstance.alpha, ENERGY, List.of());

        for (Integer populationSize : List.of(200)) {
                for (Double randomInitialPopulationProbability : List.of(0.2/*, 0.5, 0.8*/)) {
                    for (Double bestRandomPopulationInitSize : List.of(problemInstance.jobs.size() * 0.2)) {
                        for (Integer elitesCount : List.of(2)) { // 0 ????????
//                            for (Double lambda : List.of(/*0.2, 0.5, */0.8)) {
//                                for (Boolean includeZero : List.of(true)) {
                                    for (CrossoverType crossoverType : List.of(_1PX)) {
                                   //     System.out.println("ELITES_COUNT = " + elitesCount + "; LAMBDA = " + lambda + "; INCLUDE_ZERO = " + includeZero);
                                        GaParams gaParams = new GaParams(10000, populationSize, elitesCount, randomInitialPopulationProbability,
                                                bestRandomPopulationInitSize.intValue(), SelectionType.RANKING, 0.8, crossoverType, 0.2, MutationType.SHIFT, -1, 0.0, true);
                                        GaResult gaResult = GeneticAlgorithmOptimizedCrossoversWithAdaptiveLargeNeiborhoodSearch.runWithParams(problemInstance, RANDOM, true, gaParams);
                                        Integer id = ID++;
                                        List<String> results = new ArrayList<>();
                                        results.add(id.toString());
                                        results.addAll(problemInstance.getParams());
                                        results.add(Double.toString(lowerBound));
                                        results.addAll(gaResult.getResults());
                                        results.addAll(gaParams.getParams());

                                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_FILE, true))) {
                                            bufferedWriter.newLine();
                                            bufferedWriter.write(String.join(";", results));
                                        }

                                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(AVERAGE_SOLUTION_FILE, true))) {
                                            for (var entry : gaResult.solutionStatistics) {
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(id + ";" + entry.iteration + ";" + entry.min + ";" + entry.avg + ";" + entry.max
                                                                + ";"
                                                + entry.crossoverBytes.get(CrossoverType.PMX) + ";" + entry.crossoverBytes.get(CrossoverType.CX) + ";"
                                                + entry.crossoverBytes.get(CrossoverType.OX) + ";" + entry.crossoverBytes.get(CrossoverType._1PX) + ";"
                                                + entry.crossoverBytes.get(CrossoverType.OurCX)
//                                                                + entry.crossoverBytes.get(AdaptiveEnum.OPX_EXCHANGE) + ";" + entry.crossoverBytes.get(AdaptiveEnum.OX_EXCHANGE) + ";"
//                                                                + entry.crossoverBytes.get(AdaptiveEnum.OPX_SHIFT) + ";" + entry.crossoverBytes.get(AdaptiveEnum.OX_SHIFT) + ";"
//                                                + entry.crossoverBytes.get(AdaptiveEnum.OPX_NO) + ";" + entry.crossoverBytes.get(AdaptiveEnum.OX_NO) + ";"
//                                                + entry.crossoverBytes.get(AdaptiveEnum.NO_SHIFT) + ";" + entry.crossoverBytes.get(AdaptiveEnum.NO_EXCHANGE)
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
//                }
//        }
    }

    private static List<List<GaJob>> read(String filename) throws IOException {
        List<List<GaJob>> problemInstances = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line = null;
            String ids = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] jobs = line.split("], \\[");
                List<GaJob> jobList = new ArrayList<>();
                for (int i = 0; i < jobs.length; i++) {
                    jobList.add(new GaJob(Integer.parseInt(jobs[i].split(", ")[0]), Integer.parseInt(jobs[i].split(", ")[1])));
                }
                ids = bufferedReader.readLine();
                problemInstances.add(jobList);
            }
        }
        return problemInstances;
    }

    public static ProblemInstance getProblemInstance() {
        return new ProblemInstance(List.of(
                new GaJob(1, 10),
                new GaJob(1, 100),
                new GaJob(2, 70),
                new GaJob(2, 80),
                new GaJob(2, 100),
                new GaJob(1, 875),
                new GaJob(1, 800),
                new GaJob(1, 30),
                new GaJob(2, 500),
                new GaJob(2, 500)
//                new GaJob(1, 200),
//                new GaJob(1, 575),
//                new GaJob(2, 650),
//                new GaJob(1, 800),
//                new GaJob(1, 60),
//                new GaJob(1, 80),
//                new GaJob(1, 425),
//                new GaJob(1, 275),
//                new GaJob(2, 70),
//                new GaJob(2, 100),
//                new GaJob(1, 425),
//                new GaJob(1, 20),
//                new GaJob(1, 30),
//                new GaJob(2, 725),
//                new GaJob(1, 30),
//                new GaJob(1, 50),
//                new GaJob(1, 200),
//                new GaJob(1, 20),
//                new GaJob(1, 350),
//                new GaJob(2, 350),
//                new GaJob(1, 575),
//                new GaJob(1, 10),
//                new GaJob(2, 425),
//                new GaJob(2, 425),
//                new GaJob(2, 500),
//                new GaJob(1, 425),
//                new GaJob(1, 500),
//                new GaJob(2, 500),
//                new GaJob(2, 650),
//                new GaJob(2, 500),
//                new GaJob(2, 70),
//                new GaJob(2, 20),
//                new GaJob(2, 575),
//                new GaJob(2, 90),
//                new GaJob(1, 100),
//                new GaJob(1, 50),
//                new GaJob(1, 20),
//                new GaJob(2, 350),
//                new GaJob(2, 60),
//                new GaJob(1, 875),
//                new GaJob(1, 80),
//                new GaJob(1, 575),
//                new GaJob(1, 90),
//                new GaJob(2, 350),
//                new GaJob(1, 80),
//                new GaJob(2, 800),
//                new GaJob(1, 10),
//                new GaJob(1, 275),
//                new GaJob(1, 275),
//                new GaJob(2, 275)
        ));
    }
    
    public static ProblemInstance generateProblemInstanceByParams(int blocsCount, int jobsCount, Random random,
                                                                   double smallJobsProbability, List<Integer> smallJobs,
                                                                   List<Integer> largeJobs, double singleJobsProbability,
                                                                  double alpha, int exampleNumber, int energy, String seriesName) {
        List<GaJob> jobs = new LinkedList<>();
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
                    jobs.add(new GaJob(1, sortedValues.get(i) * 2));
                }
                jobs.add(new GaJob(2, sortedValues.get(i)));
            }
            if (jobs.size() % 2 != 0) {
                jobs.add(new GaJob(1, 0));
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

                jobs.add(new GaJob(processorsCount, volume));
            }
        }
        return new ProblemInstance(jobs, alpha, singleJobsProbability, smallJobsProbability, exampleNumber, blocsCount, seriesName, energy);
    }

    public static ProblemInstance generateProblemInstanceWithJobs(List<GaJob> jobs, int blocsCount,
                                                                  double smallJobsProbability,
                                                                  double singleJobsProbability,
                                                                  double alpha, int exampleNumber, int energy, String seriesName) {
        return new ProblemInstance(jobs, alpha, singleJobsProbability, smallJobsProbability, exampleNumber, blocsCount, seriesName, energy);
    }


    private static double applyAll1Proc(List<GaJob> jobs, double alpha, int jobsCount) {
        List<GaJob> singleProcessorJobs = new LinkedList<>();
        for (GaJob job : jobs) {
            singleProcessorJobs.add(new GaJob(job.processorsCount, job.processorsCount == 1 ? job.volume : job.volume * 2));
        }

        List<GaJob> sortedJobs = singleProcessorJobs.stream().sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());
        double totalSum = calculateTotalSum1Proc(sortedJobs, alpha, jobsCount);
        for (int i = 0; i < jobsCount; i+=2) {
            GaJob job1 = sortedJobs.get(i);
            GaJob job2 = sortedJobs.get(i + 1);

            job1.duration = calculateJob1ProcDuration(job1, i + 1, totalSum, alpha, jobsCount);
            job2.duration = calculateJob1ProcDuration(job2, i + 1, totalSum, alpha, jobsCount);
        }

        List<GaProcessor> processors = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            processors.add(new GaProcessor());
        }

        for (int i = 0; i < jobsCount; i+=2) {
            GaJob job1 = sortedJobs.get(i);
            double startTime1 = processors.get(0).whereCanAdd(job1.duration);
            processors.get(0).addInterval(startTime1, job1.duration);
            processors.get(0).assignedJobs.add(job1);

            GaJob job2 = sortedJobs.get(i+1);
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

    private static double calculateJob1ProcDuration(GaJob job, int jobIndex, double totalSum, double alpha, int jobsCount) {
        return Math.pow((jobsCount - jobIndex + 1), - 1.0 / alpha) * job.volume * totalSum;
    }

    private static double calculateTotalSum1Proc(List<GaJob> jobs, double alpha, int jobsCount) {
        double sum = 0.0;
        for (int i = 0; i < jobs.size(); i+=2) {
            sum += (jobs.get(i).volume + jobs.get(i + 1).volume) * Math.pow((jobsCount - (i + 1) + 1), (alpha - 1.0) / alpha);
        }
        return Math.pow(sum / ENERGY, 1.0 / (alpha - 1));
    }
}
