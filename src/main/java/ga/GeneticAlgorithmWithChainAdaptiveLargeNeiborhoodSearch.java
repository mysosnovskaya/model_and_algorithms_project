//package ga;
//
//import ga.model.AdaptiveEnum;
//import ga.model.GaJob;
//import ga.model.GaParams;
//import ga.model.GaResult;
//import ga.model.Genotype;
//import ga.model.ProblemInstance;
//import ga.model.SolutionStatistics;
//import ga.model.types.CrossoverType;
//import ga.model.types.MutationType;
//import ga.model.types.SelectionType;
//import javafx.util.Pair;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
//import static ga.FitnessCalculator.calculateFitness;
//import static ga.FitnessCalculator.calculateLowerBound;
//import static ga.GaRunner.LARGE_JOBS_VOLUMES_1;
//import static ga.GaRunner.SMALL_JOBS_VOLUMES_1;
//import static ga.GaRunner.generateProblemInstanceByParams;
//import static ga.model.types.CrossoverType.CX;
//import static ga.model.types.CrossoverType.NO;
//import static ga.model.types.CrossoverType.OPX;
//import static ga.model.types.CrossoverType.OX;
//import static ga.model.types.CrossoverType.OurCX;
//import static ga.model.types.CrossoverType.PMX;
//import static ga.model.types.SelectionType.RANKING;
//import static ga.model.types.SelectionType.TOURNEY_10;
//import static ga.model.types.SelectionType.TOURNEY_15;
//import static ga.model.types.SelectionType.TOURNEY_3;
//import static ga.model.types.SelectionType.TOURNEY_5;
//
//// 2302.0245910988792
//public class GeneticAlgorithmWithChainAdaptiveLargeNeiborhoodSearch {
//    // параметры ГА
//    private static int ITERATIONS_COUNT = 10000;
//    private static int FITNESS_CALCULATION = 15000 * 200;
//    private static int POPULATION_SIZE = 200; // 100, 200
//    private static int ELITES_COUNT = 2; // 0, 2, 98(198)
//    private static int FITNESS_CALCULATIONS_WITHOUT_IMPROVING_TO_RERUN = 1000 * 200;
//
//    private static double RANDOM_INITIAL_POPULATION_PROBABILITY = 0.2; // 0.2, 0.5, 0.8
//    private static int BEST_RANDOM_POPULATION_INIT_SIZE = 10; // 20, 50
//
//    private static SelectionType SELECTION_TYPE = RANKING;
//
////    private static double CROSSOVER_PROBABILITY = 0.8;
//    private static CrossoverType CROSSOVER_TYPE = OPX;
//
////    private static double MUTATION_PROBABILITY = 0.2;
//    private static MutationType MUTATION_TYPE = MutationType.SHIFT;
//    private static int MUTATION_WINDOW = 4;
//
//    private static final int LO_ITERATIONS_COUNT = 30;
//    private static final double LO_ALLOW_DEGRADATION_PROBABILITY = 0.0;
//
//    // необходимое для работы
//    // отсортированы от наименьшего к наибольшему по фитнес-функции
//    private static List<Genotype> population = new ArrayList<>();
//    private static final Comparator<Genotype> COMPARATOR = (genotype, t1) -> {
//        if (genotype.fitness < t1.fitness) {
//            return -1;
//        } else if (genotype.fitness > t1.fitness) {
//            return 1;
//        }
//        return 0;
//    };
//
//
//    private static final Map<SelectionType, Double> SELECTION_WEIGHTS = new HashMap<>(
//            Map.of(RANKING, 1.0, TOURNEY_3, 1.0, TOURNEY_5, 1.0, TOURNEY_10, 1.0, TOURNEY_15, 1.0)
//    );
//
//    private static final Map<AdaptiveEnum, Double> CROSSOVER_AND_MUTATIONS_WEIGHTS = new HashMap<>(
//            Map.of(AdaptiveEnum.OX_EXCHANGE, 0.0001, AdaptiveEnum.OX_SHIFT, 0.0001, AdaptiveEnum.OPX_EXCHANGE, 0.0001, AdaptiveEnum.OPX_SHIFT, 0.0001,
//                    AdaptiveEnum.OPX_NO, 0.0001, AdaptiveEnum.OX_NO, 0.0001, AdaptiveEnum.NO_EXCHANGE, 0.0001, AdaptiveEnum.NO_SHIFT, 0.0001)
//    );
//
//    private static double LAMBDA = 0.2;
//    private static boolean INCLUDE_ZERO = true;
//
//    public static void main(String[] args) {
//        long seed = 124; // 25214903830
//        Random random = new Random(seed);
////        for (int i = 0; i < 5; i++) {
//            double lowerBound = 0.0;
//            double solution = 1.0;
//            ProblemInstance problemInstance = null;
//            List<Integer> permutation = new ArrayList<>();
////        while (solution >= lowerBound) {
//            problemInstance =
////                    getProblemInstance();
//                    generateProblemInstanceByParams(0, 50, random, 0.5, SMALL_JOBS_VOLUMES_1,
//                            LARGE_JOBS_VOLUMES_1, 0.7, 1.5, 0, 100_000, "");
//            //    System.out.println("LOWER BOUND = " + calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, List.of()));
//            System.out.println("real LOWER BOUND = " + calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, List.of()));
//            var gaResult = run(problemInstance, random, true);
////            System.out.println("SOLUTION = " + gaResult.solution);
////        lowerBound = calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, gaResult.permutation);
////        System.out.println("LOWER BOUND = " + lowerBound);
//            solution = gaResult.solution;
//            permutation = gaResult.permutation;
////        }
//            List<GaJob> jobs = new ArrayList<>();
//            for (Integer index : permutation) {
//                jobs.add(problemInstance.jobs.get(index - 1));
//            }
//            System.out.println("real LOWER BOUND = " + calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, List.of()));
////        }
////        System.out.println(jobs);
//    }
//
//    public static GaResult runWithParams(ProblemInstance problemInstance, Random random, boolean printLog, GaParams gaParams) {
//        ITERATIONS_COUNT = gaParams.iterationsCount;
//        POPULATION_SIZE = gaParams.populationSize;
//        ELITES_COUNT = gaParams.elitesCount;
//        RANDOM_INITIAL_POPULATION_PROBABILITY = gaParams.randomInitialPopulationProbability;
//        BEST_RANDOM_POPULATION_INIT_SIZE = gaParams.bestRandomPopulationSize;
//        SELECTION_TYPE = gaParams.selectionType;
////        CROSSOVER_PROBABILITY = gaParams.crossoverProbability;
//        CROSSOVER_TYPE = gaParams.crossoverType;
////        MUTATION_PROBABILITY = gaParams.mutationProbability;
//        MUTATION_TYPE = gaParams.mutationType;
//        MUTATION_WINDOW = gaParams.mutationWindow;
//        LAMBDA = gaParams.lambda;
//        INCLUDE_ZERO = gaParams.includeZero;
//        return run(problemInstance, random, printLog);
//    }
//
//    public static GaResult run(ProblemInstance problemInstance, Random random, boolean printLog) {
//        population.clear();
//        Genotype globalBestFoundGenotype = new Genotype(new ArrayList<>(), Double.MAX_VALUE);
//        Genotype localBestFoundGenotype = new Genotype(new ArrayList<>(), Double.MAX_VALUE);
//        List<SolutionStatistics> solutionStatistics = new ArrayList<>();
//        long timeStart = System.currentTimeMillis();
//        long timeGlobalSolutionFound = 0;
//        int iterationGlobalSolutionFound = 0;
//        int iterationLocalSolutionFound = 0;
//        int fitnessCalculationsLocalSolutionFound = 0;
//        generateInitialPopulation(random, problemInstance);
//        population.sort(COMPARATOR);
//
//        int iterations = 0;
//        int fitnessCalculations = 0;
//
//        int opxShift = 0;
//        int oxShift = 0;
//        int opxExchange = 0;
//        int oxExchange = 0;
//        int opxNo = 0;
//        int oxNo = 0;
//        int noExchange = 0;
//        int noShift = 0;
////        int opx = 0;
////        int ox = 0;
////        int cx = 0;
////        int ourCx = 0;
////        int pmx = 0;
//        int localIterations = 0;
//        while (fitnessCalculations < FITNESS_CALCULATION) {
//            iterations++;
////        for (int i = 0; i < ITERATIONS_COUNT; i++) {
//
//            List<Genotype> currentPopulation = new ArrayList<>();
//            // наполнение лучшими генотипами из прошлой популяции
//            for (int j = 0; j < ELITES_COUNT; j++) {
//                currentPopulation.add(population.get(j));
//            }
//            int iterationsCount = (POPULATION_SIZE - currentPopulation.size()) / 2;
//            for (int j = 0; j < iterationsCount; j++) {
////                SELECTION_TYPE = chooseSelection(random, fitnessCalculations < FITNESS_CALCULATION * 0.05);
//                var adaptiveEnum = chooseCrossoverAndMutation(random, localIterations < FITNESS_CALCULATION * 0.0001);
//                localIterations++;
//                switch (adaptiveEnum) {
////                    case CX:
////                        CROSSOVER_TYPE = CX;
////                        break;
////                    case OX:
////                        CROSSOVER_TYPE = OX;
////                        break;
////                    case OPX:
////                        CROSSOVER_TYPE = OPX;
////                        break;
////                    case PMX:
////                        CROSSOVER_TYPE = PMX;
////                        break;
////                    case OurCX:
////                        CROSSOVER_TYPE = OurCX;
////                        break;
//                    case OX_SHIFT:
//                        CROSSOVER_TYPE = OX;
//                        MUTATION_TYPE = MutationType.SHIFT;
//                        break;
//                    case OPX_SHIFT:
//                        CROSSOVER_TYPE = OPX;
//                        MUTATION_TYPE = MutationType.SHIFT;
//                        break;
//                    case OX_EXCHANGE:
//                        CROSSOVER_TYPE = OX;
//                        MUTATION_TYPE = MutationType.EXCHANGE;
//                        break;
//                    case OPX_EXCHANGE:
//                        CROSSOVER_TYPE = OPX;
//                        MUTATION_TYPE = MutationType.EXCHANGE;
//                        break;
//                    case OX_NO:
//                        CROSSOVER_TYPE = OX;
//                        MUTATION_TYPE = MutationType.NO;
//                        break;
//                    case OPX_NO:
//                        CROSSOVER_TYPE = OPX;
//                        MUTATION_TYPE = MutationType.NO;
//                        break;
//                    case NO_SHIFT:
//                        CROSSOVER_TYPE = NO;
//                        MUTATION_TYPE = MutationType.SHIFT;
//                        break;
//                    case NO_EXCHANGE:
//                        CROSSOVER_TYPE = NO;
//                        MUTATION_TYPE = MutationType.EXCHANGE;
//                        break;
//                }
//                if (CROSSOVER_TYPE.equals(OPX) && MUTATION_TYPE.equals(MutationType.SHIFT)) {
//                    opxShift++;
//                } else if (CROSSOVER_TYPE.equals(OX) && MUTATION_TYPE.equals(MutationType.SHIFT)) {
//                    oxShift++;
//                } else if (CROSSOVER_TYPE.equals(OPX) && MUTATION_TYPE.equals(MutationType.EXCHANGE)) {
//                    opxExchange++;
//                } else if (CROSSOVER_TYPE.equals(OX) && MUTATION_TYPE.equals(MutationType.EXCHANGE)){
//                    oxExchange++;
//                } else if (CROSSOVER_TYPE.equals(NO) && MUTATION_TYPE.equals(MutationType.SHIFT)) {
//                    noShift++;
//                } else if (CROSSOVER_TYPE.equals(NO) && MUTATION_TYPE.equals(MutationType.EXCHANGE)) {
//                    noExchange++;
//                } else if (CROSSOVER_TYPE.equals(OX) && MUTATION_TYPE.equals(MutationType.NO)) {
//                    oxNo++;
//                } else if (CROSSOVER_TYPE.equals(OPX) && MUTATION_TYPE.equals(MutationType.NO)) {
//                    opxNo++;
//                }
////                if (CROSSOVER_TYPE.equals(OPX)) {
////                    opx++;
////                } else if (CROSSOVER_TYPE.equals(OX)) {
////                    ox++;
////                } else if (CROSSOVER_TYPE.equals(PMX)) {
////                    pmx++;
////                } else if (CROSSOVER_TYPE.equals(CX)) {
////                    cx++;
////                } else {
////                    ourCx++;
////                }
//
//                Genotype parent1 = doSelection(random);
//                Genotype parent2 = doSelection(random);
//                var childs = doCrossover(parent1, parent2, random, problemInstance.jobs.size());
//                var genotype1 = doMutation(childs.getKey(), random, problemInstance.jobs.size());
//                var genotype2 = doMutation(childs.getValue(), random, problemInstance.jobs.size());
//                genotype1.fitness = calculateFitness(
//                        genotype1.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
//                fitnessCalculations++;
//                genotype2.fitness = calculateFitness(
//                        genotype2.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
//                fitnessCalculations++;
//
//                    double w = 0.0;
//                    if (genotype1.fitness < globalBestFoundGenotype.fitness) {
//                        globalBestFoundGenotype = new Genotype(genotype1.permutation, genotype1.fitness);
//                        iterationGlobalSolutionFound = iterations;
//                        timeGlobalSolutionFound = System.currentTimeMillis() - timeStart;
//                        localBestFoundGenotype = new Genotype(genotype1.permutation, genotype1.fitness);
//                        iterationLocalSolutionFound = iterations;
//                        fitnessCalculationsLocalSolutionFound = fitnessCalculations;
//                        w = 1.0;
//                    } else if (genotype2.fitness < globalBestFoundGenotype.fitness) {
//                        globalBestFoundGenotype = new Genotype(genotype2.permutation, genotype2.fitness);
//                        iterationGlobalSolutionFound = iterations;
//                        timeGlobalSolutionFound = System.currentTimeMillis() - timeStart;
//                        localBestFoundGenotype = new Genotype(genotype2.permutation, genotype2.fitness);
//                        iterationLocalSolutionFound = iterations;
//                        fitnessCalculationsLocalSolutionFound = fitnessCalculations;
//                        w = 1.0;
//                    } else if (genotype1.fitness < localBestFoundGenotype.fitness) {
//                        localBestFoundGenotype = new Genotype(genotype1.permutation, genotype1.fitness);
//                        iterationLocalSolutionFound = iterations;
//                        fitnessCalculationsLocalSolutionFound = fitnessCalculations;
//                        w = 0.8;
//                    } else if (genotype2.fitness < localBestFoundGenotype.fitness) {
//                        localBestFoundGenotype = new Genotype(genotype2.permutation, genotype2.fitness);
//                        iterationLocalSolutionFound = iterations;
//                        fitnessCalculationsLocalSolutionFound = fitnessCalculations;
//                        w = 0.8;
////                } else if (childs.getKey().fitness > parent1.fitness && childs.getValue().fitness > parent1.fitness
////                            && childs.getKey().fitness > parent2.fitness && childs.getValue().fitness > parent2.fitness) {
////                        System.out.println("both better than both");
////                    w = 13;
//                    } else if ((genotype1.fitness < parent1.fitness && genotype1.fitness < parent2.fitness)
//                            || (genotype2.fitness < parent1.fitness && genotype2.fitness < parent2.fitness)) {
//                        w = 0.3;
//                    }
//                    if (INCLUDE_ZERO) {
//                        CROSSOVER_AND_MUTATIONS_WEIGHTS.put(adaptiveEnum, LAMBDA * CROSSOVER_AND_MUTATIONS_WEIGHTS.get(adaptiveEnum) + (1 - LAMBDA) * w);
//                    } else if (w != 0.0) {
//                        CROSSOVER_AND_MUTATIONS_WEIGHTS.put(adaptiveEnum, LAMBDA * CROSSOVER_AND_MUTATIONS_WEIGHTS.get(adaptiveEnum) + (1 - LAMBDA) * w);
//                    }
//
//                if (ELITES_COUNT == POPULATION_SIZE - 2) {
//                    List<Genotype> sorted = Stream.of(parent1, parent2, genotype1, genotype2).sorted((o1, o2) -> {
//                        if (o1.fitness - o2.fitness < 0) {
//                            return -1;
//                        } else if (o1.fitness > o2.fitness) {
//                            return 1;
//                        }
//                        return 0;
//                    }).collect(Collectors.toList());
//                    currentPopulation.add(sorted.get(0));
//                    currentPopulation.add(sorted.get(1));
//                } else {
//                    currentPopulation.add(genotype1);
//                    currentPopulation.add(genotype2);
//                }
//            }
//            population = currentPopulation;
//            population.sort(COMPARATOR);
//            if (iterations % 100 == 0) {
//                double avg = population.stream().map(g -> g.fitness).mapToDouble(f -> f).average().getAsDouble();
//                if (printLog) {
//                    System.out.println(iterations + ". bestFound: " + globalBestFoundGenotype.fitness + " localBest: " + localBestFoundGenotype.fitness + "; avg: " + avg);
//                }
//                solutionStatistics.add(
//                        new SolutionStatistics(iterations,
//                                population.get(0).fitness,
//                                avg,
//                                population.get(population.size() - 1).fitness,
//                                new HashMap<>(CROSSOVER_AND_MUTATIONS_WEIGHTS))
//                );
//            }
//            if (fitnessCalculations - fitnessCalculationsLocalSolutionFound > FITNESS_CALCULATIONS_WITHOUT_IMPROVING_TO_RERUN) {
//                for (int k = 0; k < population.size() * 0.1; k++) {
//                    Genotype g = doLocalOptimization(random, problemInstance, population.get(k));
//                    if (g.fitness < globalBestFoundGenotype.fitness) {
//                        globalBestFoundGenotype = g;
//                        iterationGlobalSolutionFound = iterations;
//                        timeGlobalSolutionFound = System.currentTimeMillis() - timeStart;
//                    }
//                }
//                localIterations = 0;
//                System.out.println("RERUN! Iteration " + iterations + " iterationLocalSolutionFound: " + iterationLocalSolutionFound + " localBestFoundGenotype: " + localBestFoundGenotype.fitness);
//                generateInitialPopulation(random, problemInstance);
//                population.sort(COMPARATOR);
//                iterationLocalSolutionFound = iterations + 1;
//                fitnessCalculationsLocalSolutionFound = fitnessCalculations + 1;
//                localBestFoundGenotype = population.get(0);
//                for (Map.Entry<AdaptiveEnum, Double> crossoverTypeDoubleEntry : CROSSOVER_AND_MUTATIONS_WEIGHTS.entrySet()) {
//                    crossoverTypeDoubleEntry.setValue(0.0001);
//                }
//            }
//        }
//
//        System.out.println("FINISH!");
//        System.out.println("oxShift: " + oxShift);
//        System.out.println("oxExchange: " + oxExchange);
//        System.out.println("opxShift: " + opxShift);
//        System.out.println("opxExchange: " + opxExchange);
//        System.out.println("oxNo: " + oxNo);
//        System.out.println("opxNo: " + opxNo);
//        System.out.println("noShift: " + noShift);
//        System.out.println("noExchange: " + noExchange);
//
//
////        System.out.println("best: " + bestFoundGenotype.fitness);
////        Genotype improvedGenotype = doLocalOptimization(random, problemInstance, bestFoundGenotype);
////        System.out.println("improved: " + improvedGenotype.fitness);
//        return new GaResult(globalBestFoundGenotype.fitness, globalBestFoundGenotype.permutation, solutionStatistics,
//                (System.currentTimeMillis() - timeStart) / 1000, iterationGlobalSolutionFound, timeGlobalSolutionFound / 1000);
//    }
//
//    private static void generateInitialPopulation(Random random, ProblemInstance instance) {
//        population.clear();
//        for (int i = 0; i < POPULATION_SIZE; i++) {
//            if (random.nextDouble() < RANDOM_INITIAL_POPULATION_PROBABILITY) {
//                population.add(generateInitialPopulationRandomly(random, instance.jobs.size()));
//            } else {
//                population.add(generateOneGenotypeBestRandom(random, instance));
//            }
//        }
//        for (Genotype genotype : population) {
//            genotype.fitness = calculateFitness(genotype.permutation, instance.jobs, instance.alpha, instance.energy);
//        }
//    }
//
//    private static Genotype generateInitialPopulationRandomly(Random random, int jobsCount) {
//        var jobsIndexes = IntStream.rangeClosed(1, jobsCount)
//                .boxed().collect(Collectors.toList());
//        Collections.shuffle(jobsIndexes, random);
//        return new Genotype(jobsIndexes);
//    }
//
//    private static Genotype generateOneGenotypeBestRandom(Random random, ProblemInstance problemInstance) {
//        List<Integer> permutation = new ArrayList<>();
//        var jobsIndexes = IntStream.rangeClosed(1, problemInstance.jobs.size()).boxed().collect(Collectors.toList());
//        Collections.shuffle(jobsIndexes, random);
//        for (int i = 0; i < BEST_RANDOM_POPULATION_INIT_SIZE; i++) {
//            permutation.add(jobsIndexes.remove(0));
//        }
//        while (!jobsIndexes.isEmpty()) {
//            int bestIndex = -1;
//            double bestFitness = Double.MAX_VALUE;
//
//            int jobIndex = jobsIndexes.remove(0);
//            for (int i = permutation.size(); i >= 0; i--) {
//                permutation.add(i, jobIndex);
//                double fitness = FitnessCalculator.calculateFitness(permutation,problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
//                if (fitness < bestFitness) {
//                    bestFitness = fitness;
//                    bestIndex = i;
//                }
//                permutation.remove(i);
//            }
//            permutation.add(bestIndex, jobIndex);
//        }
//        return new Genotype(permutation);
//    }
//
//    private static Genotype doSelection(Random random) {
//        switch (SELECTION_TYPE) {
//            case RANKING:
//                return doRankingSelection(random);
//            case TOURNEY_3:
//            case TOURNEY_5:
//            case TOURNEY_10:
//            case TOURNEY_15:
//                return doTourneySelection(random, SELECTION_TYPE.getTourneySize());
//            default:
//                throw new RuntimeException();
//        }
//    }
//
//    private static Genotype doRankingSelection(Random random) {
//        Map<Integer, Integer> genotypeIndexToRanks = new HashMap<>();
//        int currentRank = 0;
//        double currentFitness = Double.MAX_VALUE;
//        for (int i = population.size() - 1; i >= 0; i--) {
//            double fitness = population.get(i).fitness;
//            if (fitness < currentFitness) {
//                currentRank++;
//            }
//            genotypeIndexToRanks.put(i, currentRank);
//        }
//        int sumOfRanks = genotypeIndexToRanks.values().stream().mapToInt(i -> i).sum();
//        int randomInt = random.nextInt(sumOfRanks) + 1;
//        for (Map.Entry<Integer, Integer> entry : genotypeIndexToRanks.entrySet()) {
//            randomInt -= entry.getValue();
//            if (randomInt <= 0) {
//                return population.get(entry.getKey());
//            }
//        }
//        throw new RuntimeException("Can't do ranking selection");
//    }
//
//    private static Genotype doTourneySelection(Random random, int tourneySize) {
//        Collections.shuffle(population, random);
//        List<Genotype> tourney = population.subList(0, tourneySize);
//        return tourney.stream().min(COMPARATOR).get();
//    }
//
//    private static Pair<Genotype, Genotype> doCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
////        if (random.nextDouble() > CROSSOVER_PROBABILITY) {
////            return null;
////        }
//        switch (CROSSOVER_TYPE) {
//            case PMX:
//                return doPMXCrossover(parent1, parent2, random, jobsCount);
//            case OPX:
//                return doOPXCrossover(parent1, parent2, random, jobsCount);
//            case OX:
//                return doOXCrossover(parent1, parent2, random, jobsCount);
//            case CX:
//                return doCXCrossover(parent1, parent2, jobsCount, random);
//            case OurCX:
//                return doOurCXCrossover(parent1, parent2, jobsCount, random);
//            case NO:
//                return new Pair<>(parent1, parent2);
//            default:
//                throw new RuntimeException();
//        }
//    }
//
//    private static Pair<Genotype, Genotype> doPMXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
//        int randomIndex1 = random.nextInt(jobsCount);
//        int randomIndex2 = random.nextInt(jobsCount);
//        while (randomIndex1 == randomIndex2) {
//            randomIndex2 = random.nextInt(jobsCount);
//        }
//        if (randomIndex2 < randomIndex1) {
//            int tmp = randomIndex1;
//            randomIndex1 = randomIndex2;
//            randomIndex2 = tmp;
//        }
//        Integer[] child1 = new Integer[jobsCount];
//        Integer[] child2 = new Integer[jobsCount];
//        Set<Integer> child1Jobs = new HashSet<>();
//        Set<Integer> child2Jobs = new HashSet<>();
//        Map<Integer, Integer> replacementFrom2To1 = new HashMap<>();
//        Map<Integer, Integer> replacementFrom1To2 = new HashMap<>();
//        for (int i = randomIndex1; i < randomIndex2; i++) {
//            child1[i] = parent2.permutation.get(i);
//            child1Jobs.add(child1[i]);
//            child2[i] = parent1.permutation.get(i);
//            child2Jobs.add(child2[i]);
//
//            replacementFrom2To1.put(child1[i], child2[i]);
//            replacementFrom1To2.put(child2[i], child1[i]);
//        }
//
//        for (int i = 0; i < jobsCount; i++) {
//            if (child1[i] == null) {
//                if (!child1Jobs.contains(parent1.permutation.get(i))) {
//                    child1[i] = parent1.permutation.get(i);
//                } else {
//                    int gen =  replacementFrom2To1.get(parent1.permutation.get(i));
//                    while (child1Jobs.contains(gen)) {
//                        gen = replacementFrom2To1.get(gen);
//                    }
//                    child1[i] = gen;
//                }
//                child1Jobs.add(child1[i]);
//            }
//            if (child2[i] == null) {
//                if (!child2Jobs.contains(parent2.permutation.get(i))) {
//                    child2[i] = parent2.permutation.get(i);
//                } else {
//                    int gen =  replacementFrom1To2.get(parent2.permutation.get(i));
//                    while (child2Jobs.contains(gen)) {
//                        gen = replacementFrom1To2.get(gen);
//                    }
//                    child2[i] = gen;
//                }
//            }
//            child2Jobs.add(child2[i]);
//        }
//
//        if (child1Jobs.size() != child2Jobs.size() || child2Jobs.size() != jobsCount) {
//            throw new RuntimeException("Something went wrong in PMX crossover");
//        }
//        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
//    }
//
//    private static Pair<Genotype, Genotype> doOPXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
//        int randomIndex = random.nextInt(jobsCount);
//        Integer[] child1 = new Integer[jobsCount];
//        Integer[] child2 = new Integer[jobsCount];
//        Set<Integer> child1Jobs = new HashSet<>();
//        Set<Integer> child2Jobs = new HashSet<>();
//        for (int i = 0; i <= randomIndex; i++) {
//            child1[i] = parent1.permutation.get(i);
//            child1Jobs.add(child1[i]);
//            child2[i] = parent2.permutation.get(i);
//            child2Jobs.add(child2[i]);
//        }
//        int nextIndexChild1 = randomIndex + 1;
//        for (Integer jobIndex : parent2.permutation) {
//            if (!child1Jobs.contains(jobIndex)) {
//                child1[nextIndexChild1++] = jobIndex;
//                child1Jobs.add(jobIndex);
//            }
//        }
//        int nextIndexChild2 = randomIndex + 1;
//        for (Integer jobIndex : parent1.permutation) {
//            if (!child2Jobs.contains(jobIndex)) {
//                child2[nextIndexChild2++] = jobIndex;
//                child2Jobs.add(jobIndex);
//            }
//        }
//        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
//            throw new RuntimeException();
//        }
//        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
//    }
//
//    private static Pair<Genotype, Genotype> doOXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
//        int randomIndex1 = random.nextInt(jobsCount);
//        int randomIndex2 = random.nextInt(jobsCount);
//        while (randomIndex1 == randomIndex2) {
//            randomIndex2 = random.nextInt(jobsCount);
//        }
//        if (randomIndex2 < randomIndex1) {
//            int tmp = randomIndex1;
//            randomIndex1 = randomIndex2;
//            randomIndex2 = tmp;
//        }
//        Integer[] child1 = new Integer[jobsCount];
//        Integer[] child2 = new Integer[jobsCount];
//        Set<Integer> child1Jobs = new HashSet<>();
//        Set<Integer> child2Jobs = new HashSet<>();
//        for (int i = randomIndex1; i < randomIndex2; i++) {
//            child1[i] = parent2.permutation.get(i);
//            child1Jobs.add(child1[i]);
//            child2[i] = parent1.permutation.get(i);
//            child2Jobs.add(child2[i]);
//        }
//        List<Integer> parent1Jobs = new ArrayList<>();
//        List<Integer> parent2Jobs = new ArrayList<>();
//        for (int i = randomIndex2; i < jobsCount; i++) {
//            if (!child1Jobs.contains(parent1.permutation.get(i))) {
//                parent1Jobs.add(parent1.permutation.get(i));
//            }
//            if (!child2Jobs.contains(parent2.permutation.get(i))) {
//                parent2Jobs.add(parent2.permutation.get(i));
//            }
//        }
//        for (int i = 0; i < randomIndex2; i++) {
//            if (!child1Jobs.contains(parent1.permutation.get(i))) {
//                parent1Jobs.add(parent1.permutation.get(i));
//            }
//            if (!child2Jobs.contains(parent2.permutation.get(i))) {
//                parent2Jobs.add(parent2.permutation.get(i));
//            }
//        }
//        for (int i = randomIndex2; i < jobsCount; i++) {
//            child1[i] = parent1Jobs.remove(0);
//            child1Jobs.add(child1[i]);
//            child2[i] = parent2Jobs.remove(0);
//            child2Jobs.add(child2[i]);
//        }
//        for (int i = 0; i < randomIndex1; i++) {
//            child1[i] = parent1Jobs.remove(0);
//            child1Jobs.add(child1[i]);
//            child2[i] = parent2Jobs.remove(0);
//            child2Jobs.add(child2[i]);
//        }
//
//        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
//            throw new RuntimeException();
//        }
//
//        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
//    }
//
//
//    private static Pair<Genotype, Genotype> doCXCrossover(Genotype parent1, Genotype parent2, int jobsCount, Random random) {
//        Integer[] child1 = new Integer[jobsCount];
//        Integer[] child2 = new Integer[jobsCount];
//        Set<Integer> child1Jobs = new HashSet<>();
//        Set<Integer> child2Jobs = new HashSet<>();
//
//        int permutationIndex1 = 0;
//        int jobIndex1 = parent1.permutation.get(permutationIndex1);
//        while (!child1Jobs.contains(jobIndex1)) {
//            child1[permutationIndex1] = jobIndex1;
//            child1Jobs.add(jobIndex1);
//            jobIndex1 = parent2.permutation.get(permutationIndex1);
//            permutationIndex1 = parent1.permutation.lastIndexOf(jobIndex1);
//        }
//        for (int i = 0; i < jobsCount; i++) {
//            if (child1[i] == null) {
//                child1[i] = parent2.permutation.get(i);
//                child1Jobs.add(child1[i]);
//            }
//        }
//
//        int permutationIndex2 = 0;
//        int jobIndex2 = parent2.permutation.get(permutationIndex2);
//        while (!child2Jobs.contains(jobIndex2)) {
//            child2[permutationIndex2] = jobIndex2;
//            child2Jobs.add(jobIndex2);
//            jobIndex2 = parent1.permutation.get(permutationIndex2);
//            permutationIndex2 = parent2.permutation.lastIndexOf(jobIndex2);
//        }
//        for (int i = 0; i < jobsCount; i++) {
//            if (child2[i] == null) {
//                child2[i] = parent1.permutation.get(i);
//                child2Jobs.add(child2[i]);
//            }
//        }
//
//        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
//            throw new RuntimeException();
//        }
//
//        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
//    }
//
//    private static Pair<Genotype, Genotype> doOurCXCrossover(Genotype parent1, Genotype parent2, int jobsCount, Random random) {
//        Map<Integer, List<Integer>> cycles = new HashMap<>();
//        for (int i = 0; i < parent1.permutation.size(); i++) {
//            int id = i;
//            if (cycles.values().stream().flatMap(Collection::stream).noneMatch(ind -> ind == id)) {
//                List<Integer> cycle = new ArrayList<>();
//                int index = i;
//                while (!cycle.contains(index)) {
//                    cycle.add(index);
//                    int jobIndex = parent2.permutation.get(index);
//                    index = parent1.permutation.lastIndexOf(jobIndex);
//                }
//                cycles.put(i, cycle);
//            }
//        }
//
//        Integer[] child1 = new Integer[jobsCount];
//        Integer[] child2 = new Integer[jobsCount];
//
//        for (Integer startIndex : cycles.keySet()) {
//            Genotype p1, p2;
//            if (random.nextBoolean()) {
//                p1 = parent1;
//                p2 = parent2;
//            } else {
//                p1 = parent2;
//                p2 = parent1;
//            }
//
//            for (Integer i : cycles.get(startIndex)) {
//                child1[i] = p1.permutation.get(i);
//                child2[i] = p2.permutation.get(i);
//            }
//        }
//
//        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
//    }
//
//    private static Genotype doMutation(Genotype genotype, Random random, int jobsCount) {
////        if (random.nextDouble() > MUTATION_PROBABILITY) {
////            return genotype;
////        }
//        switch (MUTATION_TYPE) {
//            case EXCHANGE:
//                return doExchangeMutation(genotype, random, jobsCount);
//            case SHIFT:
//                return doShiftMutation(genotype, random, jobsCount);
//            case NO:
//                return genotype;
//            default:
//                throw new RuntimeException();
//        }
//    }
//
//    private static Genotype doExchangeMutation(Genotype genotype, Random random, int jobsCount) {
//        int randomIndex1 = random.nextInt(jobsCount);
//        int randomIndex2;
//        if (MUTATION_WINDOW < 1) {
//            randomIndex2 = random.nextInt(jobsCount);
//            while (randomIndex1 == randomIndex2) {
//                randomIndex2 = random.nextInt(jobsCount);
//            }
//        } else {
//        boolean shiftLeft = randomIndex1 != 0 && (randomIndex1 == jobsCount - 1 || random.nextBoolean());
//        randomIndex2 = shiftLeft
//                ? randomIndex1 - random.nextInt(randomIndex1 - Math.max(randomIndex1 - MUTATION_WINDOW, 0)) - 1
//                : random.nextInt(Math.min(randomIndex1 + MUTATION_WINDOW, jobsCount - 1) - randomIndex1) + 1 + randomIndex1;
//        }
//        if (randomIndex2 < randomIndex1) {
//            int tmp = randomIndex1;
//            randomIndex1 = randomIndex2;
//            randomIndex2 = tmp;
//        }
//
//        int tmp = genotype.permutation.remove(randomIndex2);
//        genotype.permutation.add(randomIndex2, genotype.permutation.get(randomIndex1));
//        genotype.permutation.remove(randomIndex1);
//        genotype.permutation.add(randomIndex1, tmp);
//        return genotype;
//    }
//
//    private static Genotype doShiftMutation(Genotype genotype, Random random, int jobsCount) {
//        int randomIndex1 = random.nextInt(jobsCount);
//        int randomIndex2;
//        if (MUTATION_WINDOW < 1) {
//            randomIndex2 = random.nextInt(jobsCount);
//            while (randomIndex1 == randomIndex2) {
//                randomIndex2 = random.nextInt(jobsCount);
//            }
//        } else {
//        boolean shiftLeft = randomIndex1 != 0 && (randomIndex1 == jobsCount - 1 || random.nextBoolean());
//        randomIndex2 = shiftLeft
//                ? randomIndex1 - random.nextInt(randomIndex1 - Math.max(randomIndex1 - MUTATION_WINDOW, 0)) - 1
//                : random.nextInt(Math.min(randomIndex1 + MUTATION_WINDOW, jobsCount - 1) - randomIndex1) + 1 + randomIndex1;
//        }
//        var job = genotype.permutation.remove(randomIndex1);
//        genotype.permutation.add(randomIndex2, job);
//        return genotype;
//    }
//
//    private static Genotype doLocalOptimization(Random random, ProblemInstance problemInstance, Genotype genotype) {
//        Genotype bestGenotype = genotype;
//        for (int i = 0; i < LO_ITERATIONS_COUNT; i++) {
//            Genotype mutatedGenotype = new Genotype(new ArrayList<>(genotype.permutation));
//            switch (MUTATION_TYPE) {
//                case NO:
//                case EXCHANGE:
//                    doExchangeMutation(mutatedGenotype, random, genotype.permutation.size());
//                    break;
//                case SHIFT:
//                    doShiftMutation(mutatedGenotype, random, genotype.permutation.size());
//                    break;
//                default:
//                    throw new RuntimeException();
//            }
//            mutatedGenotype.fitness = calculateFitness(mutatedGenotype.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
//            if (mutatedGenotype.fitness < bestGenotype.fitness || random.nextDouble() < LO_ALLOW_DEGRADATION_PROBABILITY) {
//                bestGenotype = mutatedGenotype;
//            }
//        }
//        return bestGenotype;
//    }
//
//    private static SelectionType chooseSelection(Random random, boolean chooseRandomly) {
//        if (chooseRandomly) {
//            var types = SelectionType.values();
//            return types[random.nextInt(types.length)];
//        }
//        double weightsSum = SELECTION_WEIGHTS.values().stream().mapToDouble(i -> i).sum();
//        double randomDouble = random.nextDouble();
//        for (Map.Entry<SelectionType, Double> entry : SELECTION_WEIGHTS.entrySet()) {
//            randomDouble -= (entry.getValue() / weightsSum);
//            if (randomDouble <= 0) {
//                return entry.getKey();
//            }
//        }
//        throw new RuntimeException();
//    }
//
////    private static CrossoverType chooseCrossover(Random random, boolean chooseRandomly) {
////        if (chooseRandomly) {
////            var types = Arrays.stream(CrossoverType.values()).filter(c -> !c.equals(NO)).collect(Collectors.toList());
////            return types.get(random.nextInt(types.size() - 1));
////        }
////        double weightsSum = CROSSOVER_WEIGHTS.values().stream().mapToDouble(i -> i).sum();
////        double randomDouble = random.nextDouble();
////        for (Map.Entry<CrossoverType, Double> entry : CROSSOVER_WEIGHTS.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList())) {
////            randomDouble -= (entry.getValue() / weightsSum);
////            if (randomDouble <= 0) {
////                return entry.getKey();
////            }
////        }
////        throw new RuntimeException();
////    }
//
//    private static AdaptiveEnum chooseCrossoverAndMutation(Random random, boolean chooseRandomly) {
//        if (chooseRandomly) {
////            var crossoverTypes = List.of(CrossoverType.OX, CrossoverType.OPX, NO);
////            var crossoverType = crossoverTypes.get(random.nextInt(crossoverTypes.size()));
////            var mutationTypes = List.of(MutationType.SHIFT, MutationType.EXCHANGE, MutationType.NO);
////            var mutationType = mutationTypes.get(random.nextInt(mutationTypes.size()));
//            var types = new ArrayList<>(CROSSOVER_AND_MUTATIONS_WEIGHTS.keySet());
//            return types.get(random.nextInt(types.size()));
////            if (crossoverType.equals(OPX) && MUTATION_TYPE.equals(MutationType.SHIFT)) {
////                return AdaptiveEnum.OPX_SHIFT;
////            } else if (crossoverType.equals(OX) && mutationType.equals(MutationType.SHIFT)) {
////                return AdaptiveEnum.OX_SHIFT;
////            } else if (crossoverType.equals(OPX) && mutationType.equals(MutationType.EXCHANGE)) {
////                return AdaptiveEnum.OPX_EXCHANGE;
////            } else if (crossoverType.equals(OX) && mutationType.equals(MutationType.EXCHANGE)){
////                return AdaptiveEnum.OX_EXCHANGE;
////            } else if (crossoverType.equals(NO) && mutationType.equals(MutationType.SHIFT)) {
////                return AdaptiveEnum.NO_SHIFT;
////            } else if (crossoverType.equals(NO) && mutationType.equals(MutationType.EXCHANGE)) {
////                return AdaptiveEnum.NO_EXCHANGE;
////            } else if (crossoverType.equals(OX) && mutationType.equals(MutationType.NO)) {
////                return AdaptiveEnum.OX_NO;
////            } else if (crossoverType.equals(OPX) && mutationType.equals(MutationType.NO)) {
////                return AdaptiveEnum.OPX_NO;
////            }
//        }
//        double weightsSum = CROSSOVER_AND_MUTATIONS_WEIGHTS.values().stream().mapToDouble(i -> i).sum();
//        double randomDouble = random.nextDouble();
//        for (Map.Entry<AdaptiveEnum, Double> entry : CROSSOVER_AND_MUTATIONS_WEIGHTS.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList())) {
//            randomDouble -= (entry.getValue() / weightsSum);
//            if (randomDouble <= 0) {
//                return entry.getKey();
//            }
//        }
//        throw new RuntimeException();
//    }
//}
