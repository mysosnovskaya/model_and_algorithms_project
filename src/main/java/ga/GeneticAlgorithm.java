package ga;

import ga.model.GaJob;
import ga.model.GaParams;
import ga.model.GaResult;
import ga.model.Genotype;
import ga.model.ProblemInstance;
import ga.model.SolutionStatistics;
import ga.model.types.CrossoverType;
import ga.model.types.MutationType;
import ga.model.types.SelectionType;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ga.FitnessCalculator.calculateFitness;
import static ga.FitnessCalculator.calculateLowerBound;
import static ga.GaRunner.LARGE_JOBS_VOLUMES_1;
import static ga.GaRunner.SMALL_JOBS_VOLUMES_1;
import static ga.GaRunner.generateProblemInstanceByParams;

/*
- Выбирать не все перестановки
- Нарисовать графики
- посмотреть в разрезе конфигураций (фиксируем конфигурацию ГА и смотрим мин, макс, среднее, пробуем что-то понять)
- применять локальную оптимизацию решений (на процессорах). взять топ лучших в конце или каждое решение
- добавить перезапуск: если в течение заданного кол-ва итераций (для начала 100) не произошло улучшение, то перезапуск (популяция с нуля строится)
- Сколько улучшений рандомизированым поиском было

- TODO: как раньше делали, сортировать и  менять местами последние однопроцессорные в каждом блоке
   Подумать над другими реализациями локального поиска
+ TODO: какая мутация?
    Добавить случайный локальный поиск с мутацией: делаем 30 раз и переходим тогда, когда лучше - в самом конце популяции (флаг : разрешать переходить в ухудшающую точку, вероятность)
 */
public class GeneticAlgorithm {
    // параметры ГА
    private static int ITERATIONS_COUNT = 10000;
    private static int FITNESS_CALCULATION = 15000 * 200;
    private static int POPULATION_SIZE = 200; // 100, 200
    private static int ELITES_COUNT = 2; // 0, 2, 98(198)
    private static int ITERATIONS_WITHOUT_IMPROVING_TO_RERUN = 1000 * 200;

    private static int FITNESS_CALCULATIONS_WITHOUT_IMPROVING_TO_RERUN = 1000 * 200;

    private static double RANDOM_INITIAL_POPULATION_PROBABILITY = 0.2; // 0.2, 0.5, 0.8
    private static int BEST_RANDOM_POPULATION_INIT_SIZE = 10; // 20, 50

    private static SelectionType SELECTION_TYPE = SelectionType.RANKING;

    private static double CROSSOVER_PROBABILITY = 0.8;
    private static CrossoverType CROSSOVER_TYPE = CrossoverType._1PX;

    private static double MUTATION_PROBABILITY = 0.2;
    private static MutationType MUTATION_TYPE = MutationType.SHIFT;
    private static int MUTATION_WINDOW = 4;

    private static final int LO_ITERATIONS_COUNT = 30;
    private static final double LO_ALLOW_DEGRADATION_PROBABILITY = 0.0;

    // необходимое для работы
    // отсортированы от наименьшего к наибольшему по фитнес-функции
    private static List<Genotype> population = new ArrayList<>();
    private static final Comparator<Genotype> COMPARATOR = (genotype, t1) -> {
        if (genotype.fitness < t1.fitness) {
            return -1;
        } else if (genotype.fitness > t1.fitness) {
            return 1;
        }
        return 0;
    };

    public static void main(String[] args) {
        long seed = 124; // 25214903830
        Random random = new Random(seed);
        double lowerBound = 0.0;
        double solution = 1.0;
        ProblemInstance problemInstance = null;
        List<Integer> permutation = new ArrayList<>();
//        while (solution >= lowerBound) {
            problemInstance =
//                    getProblemInstance();
                    generateProblemInstanceByParams(0, 50, random, 0.5, SMALL_JOBS_VOLUMES_1,
                            LARGE_JOBS_VOLUMES_1, 0.7, 1.5, 0, 100_000, "");
        //    System.out.println("LOWER BOUND = " + calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, List.of()));
            var gaResult = run(problemInstance, random, true);
//            System.out.println("SOLUTION = " + gaResult.solution);
            lowerBound = calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, gaResult.permutation);
//            System.out.println("LOWER BOUND = " + lowerBound);
//            System.out.println("real LOWER BOUND = " + calculateLowerBound(problemInstance.jobs, problemInstance.alpha, problemInstance.energy, List.of()));
            solution = gaResult.solution;
            permutation = gaResult.permutation;
//        }
        List<GaJob> jobs = new ArrayList<>();
        for (Integer index : permutation) {
            jobs.add(problemInstance.jobs.get(index - 1));
        }
        System.out.println(jobs);
    }

    public static GaResult runWithParams(ProblemInstance problemInstance, Random random, boolean printLog, GaParams gaParams) {
        ITERATIONS_COUNT = gaParams.iterationsCount;
        POPULATION_SIZE = gaParams.populationSize;
        ELITES_COUNT = gaParams.elitesCount;
        RANDOM_INITIAL_POPULATION_PROBABILITY = gaParams.randomInitialPopulationProbability;
        BEST_RANDOM_POPULATION_INIT_SIZE = gaParams.bestRandomPopulationSize;
        SELECTION_TYPE = gaParams.selectionType;
        CROSSOVER_PROBABILITY = gaParams.crossoverProbability;
        CROSSOVER_TYPE = gaParams.crossoverType;
        MUTATION_PROBABILITY = gaParams.mutationProbability;
        MUTATION_TYPE = gaParams.mutationType;
        MUTATION_WINDOW = gaParams.mutationWindow;
        return run(problemInstance, random, printLog);
    }

    public static GaResult run(ProblemInstance problemInstance, Random random, boolean printLog) {
        population.clear();
        Genotype globalBestFoundGenotype = new Genotype(new ArrayList<>(), Double.MAX_VALUE);
        Genotype localBestFoundGenotype = new Genotype(new ArrayList<>(), Double.MAX_VALUE);
        List<SolutionStatistics> solutionStatistics = new ArrayList<>();
        long timeStart = System.currentTimeMillis();
        long timeGlobalSolutionFound = 0;
        int iterationGlobalSolutionFound = 0;
        int iterationLocalSolutionFound = 0;
        int fitnessCalculationsLocalSolutionFound = 0;
        generateInitialPopulation(random, problemInstance);
        population.sort(COMPARATOR);

        int iterations = 0;
        int fitnessCalculations = 0;
        while (fitnessCalculations < FITNESS_CALCULATION) {
            iterations++;
//        for (int i = 0; i < ITERATIONS_COUNT; i++) {

            List<Genotype> currentPopulation = new ArrayList<>();
            // наполнение лучшими генотипами из прошлой популяции
            for (int j = 0; j < ELITES_COUNT; j++) {
                currentPopulation.add(population.get(j));
            }
            int iterationsCount = (POPULATION_SIZE - currentPopulation.size()) / 2;
            for (int j = 0; j < iterationsCount; j++) {
                Genotype parent1 = doSelection(random);
                Genotype parent2 = doSelection(random);
                var childs = doCrossover(parent1, parent2, random, problemInstance.jobs.size());
                var genotype1 = doMutation(childs.getKey(), random, problemInstance.jobs.size());
                var genotype2 = doMutation(childs.getValue(), random, problemInstance.jobs.size());
                genotype1.fitness = calculateFitness(
                        genotype1.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
                fitnessCalculations++;
                genotype2.fitness = calculateFitness(
                        genotype2.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
                fitnessCalculations++;
                currentPopulation.add(genotype1);
                currentPopulation.add(genotype2);
            }
            population = currentPopulation;
            population.sort(COMPARATOR);
            Genotype best = population.get(0);
            if (globalBestFoundGenotype.fitness > best.fitness) {
                globalBestFoundGenotype = new Genotype(best.permutation, best.fitness);
                iterationGlobalSolutionFound = iterations;
                timeGlobalSolutionFound = System.currentTimeMillis() - timeStart;
            }
            if (localBestFoundGenotype.fitness > best.fitness) {
                localBestFoundGenotype = new Genotype(best.permutation, best.fitness);
                iterationLocalSolutionFound = iterations;
                fitnessCalculationsLocalSolutionFound = fitnessCalculations;
            }
            if (iterations % 100 == 0) {
                if (printLog) {
                    System.out.println(iterations + ". bestFound: " + globalBestFoundGenotype.fitness + " population size: " + new HashSet<>(population).size());
                }
                solutionStatistics.add(
                        new SolutionStatistics(iterations,
                                population.get(0).fitness,
                                population.stream().map(g -> g.fitness).mapToDouble(f -> f).average().getAsDouble(),
                                population.get(population.size() - 1).fitness,
                                new HashMap<>())
                );
            }
            if (fitnessCalculations - fitnessCalculationsLocalSolutionFound > FITNESS_CALCULATIONS_WITHOUT_IMPROVING_TO_RERUN) {
//                for (int k = 0; k < population.size() * 0.1; k++) {
//                    Genotype g = doLocalOptimization(random, problemInstance, population.get(k));
//                    if (g.fitness < globalBestFoundGenotype.fitness) {
//                        globalBestFoundGenotype = g;
//                        iterationGlobalSolutionFound = iterations;
//                        timeGlobalSolutionFound = System.currentTimeMillis() - timeStart;
//                    }
//                }
                System.out.println("RERUN! Iteration " + iterations + " iterationLocalSolutionFound: " + iterationLocalSolutionFound + " localBestFoundGenotype: " + localBestFoundGenotype.fitness);
                generateInitialPopulation(random, problemInstance);
                population.sort(COMPARATOR);
                iterationLocalSolutionFound = iterations + 1;
                fitnessCalculationsLocalSolutionFound = fitnessCalculations + 1;
                localBestFoundGenotype = population.get(0);
            }
        }
//        System.out.println("best: " + bestFoundGenotype.fitness);
//        Genotype improvedGenotype = doLocalOptimization(random, problemInstance, bestFoundGenotype);
//        System.out.println("improved: " + improvedGenotype.fitness);
        return new GaResult(globalBestFoundGenotype.fitness, globalBestFoundGenotype.permutation, solutionStatistics,
                (System.currentTimeMillis() - timeStart) / 1000, iterationGlobalSolutionFound, timeGlobalSolutionFound / 1000);
    }

    private static void generateInitialPopulation(Random random, ProblemInstance instance) {
        population.clear();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (random.nextDouble() < RANDOM_INITIAL_POPULATION_PROBABILITY) {
                population.add(generateInitialPopulationRandomly(random, instance.jobs.size()));
            } else {
                population.add(generateOneGenotypeBestRandom(random, instance));
            }
        }
        for (Genotype genotype : population) {
            genotype.fitness = calculateFitness(genotype.permutation, instance.jobs, instance.alpha, instance.energy);
        }
    }

    private static Genotype generateInitialPopulationRandomly(Random random, int jobsCount) {
        var jobsIndexes = IntStream.rangeClosed(1, jobsCount)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(jobsIndexes, random);
        return new Genotype(jobsIndexes);
    }

    private static Genotype generateOneGenotypeBestRandom(Random random, ProblemInstance problemInstance) {
        List<Integer> permutation = new ArrayList<>();
        var jobsIndexes = IntStream.rangeClosed(1, problemInstance.jobs.size()).boxed().collect(Collectors.toList());
        Collections.shuffle(jobsIndexes, random);
        for (int i = 0; i < BEST_RANDOM_POPULATION_INIT_SIZE; i++) {
            permutation.add(jobsIndexes.remove(0));
        }
        while (!jobsIndexes.isEmpty()) {
            int bestIndex = -1;
            double bestFitness = Double.MAX_VALUE;

            int jobIndex = jobsIndexes.remove(0);
            for (int i = permutation.size(); i >= 0; i--) {
                permutation.add(i, jobIndex);
                double fitness = FitnessCalculator.calculateFitness(permutation,problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestIndex = i;
                }
                permutation.remove(i);
            }
            permutation.add(bestIndex, jobIndex);
        }
        return new Genotype(permutation);
    }

    private static Genotype doSelection(Random random) {
        switch (SELECTION_TYPE) {
            case RANKING:
                return doRankingSelection(random);
            case TOURNEY_3:
            case TOURNEY_5:
            case TOURNEY_10:
            case TOURNEY_15:
                return doTourneySelection(random, SELECTION_TYPE.getTourneySize());
            default:
                throw new RuntimeException();
        }
    }

    private static Genotype doRankingSelection(Random random) {
        Map<Integer, Integer> genotypeIndexToRanks = new HashMap<>();
        int currentRank = 0;
        double currentFitness = Double.MAX_VALUE;
        for (int i = population.size() - 1; i >= 0; i--) {
            double fitness = population.get(i).fitness;
            if (fitness < currentFitness) {
                currentRank++;
            }
            genotypeIndexToRanks.put(i, currentRank);
        }
        int sumOfRanks = genotypeIndexToRanks.values().stream().mapToInt(i -> i).sum();
        int randomInt = random.nextInt(sumOfRanks) + 1;
        for (Map.Entry<Integer, Integer> entry : genotypeIndexToRanks.entrySet()) {
            randomInt -= entry.getValue();
            if (randomInt <= 0) {
                return population.get(entry.getKey());
            }
        }
        throw new RuntimeException("Can't do ranking selection");
    }

    private static Genotype doTourneySelection(Random random, int tourneySize) {
        Collections.shuffle(population, random);
        List<Genotype> tourney = population.subList(0, tourneySize);
        return tourney.stream().min(COMPARATOR).get();
    }

    private static Pair<Genotype, Genotype> doCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
        if (random.nextDouble() > CROSSOVER_PROBABILITY) {
            return new Pair<>(parent1, parent2);
        }
        switch (CROSSOVER_TYPE) {
            case PMX:
                return doPMXCrossover(parent1, parent2, random, jobsCount);
            case _1PX:
                return doOPXCrossover(parent1, parent2, random, jobsCount);
            case OX:
                return doOXCrossover(parent1, parent2, random, jobsCount);
            case CX:
                return doCXCrossover(parent1, parent2, jobsCount, random);
            case OurCX:
                return doOurCXCrossover(parent1, parent2, jobsCount, random);
            default:
                throw new RuntimeException();
        }
    }

    private static Pair<Genotype, Genotype> doPMXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
        int randomIndex1 = random.nextInt(jobsCount);
        int randomIndex2 = random.nextInt(jobsCount);
        while (randomIndex1 == randomIndex2) {
            randomIndex2 = random.nextInt(jobsCount);
        }
        if (randomIndex2 < randomIndex1) {
            int tmp = randomIndex1;
            randomIndex1 = randomIndex2;
            randomIndex2 = tmp;
        }
        Integer[] child1 = new Integer[jobsCount];
        Integer[] child2 = new Integer[jobsCount];
        Set<Integer> child1Jobs = new HashSet<>();
        Set<Integer> child2Jobs = new HashSet<>();
        Map<Integer, Integer> replacementFrom2To1 = new HashMap<>();
        Map<Integer, Integer> replacementFrom1To2 = new HashMap<>();
        for (int i = randomIndex1; i < randomIndex2; i++) {
            child1[i] = parent2.permutation.get(i);
            child1Jobs.add(child1[i]);
            child2[i] = parent1.permutation.get(i);
            child2Jobs.add(child2[i]);

            replacementFrom2To1.put(child1[i], child2[i]);
            replacementFrom1To2.put(child2[i], child1[i]);
        }

        for (int i = 0; i < jobsCount; i++) {
            if (child1[i] == null) {
                if (!child1Jobs.contains(parent1.permutation.get(i))) {
                    child1[i] = parent1.permutation.get(i);
                } else {
                    int gen =  replacementFrom2To1.get(parent1.permutation.get(i));
                    while (child1Jobs.contains(gen)) {
                        gen = replacementFrom2To1.get(gen);
                    }
                    child1[i] = gen;
                }
                child1Jobs.add(child1[i]);
            }
            if (child2[i] == null) {
                if (!child2Jobs.contains(parent2.permutation.get(i))) {
                    child2[i] = parent2.permutation.get(i);
                } else {
                    int gen =  replacementFrom1To2.get(parent2.permutation.get(i));
                    while (child2Jobs.contains(gen)) {
                        gen = replacementFrom1To2.get(gen);
                    }
                    child2[i] = gen;
                }
            }
            child2Jobs.add(child2[i]);
        }

        if (child1Jobs.size() != child2Jobs.size() || child2Jobs.size() != jobsCount) {
            throw new RuntimeException("Something went wrong in PMX crossover");
        }
        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
    }

    private static Pair<Genotype, Genotype> doOPXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
        int randomIndex = random.nextInt(jobsCount);
        Integer[] child1 = new Integer[jobsCount];
        Integer[] child2 = new Integer[jobsCount];
        Set<Integer> child1Jobs = new HashSet<>();
        Set<Integer> child2Jobs = new HashSet<>();
        for (int i = 0; i <= randomIndex; i++) {
            child1[i] = parent1.permutation.get(i);
            child1Jobs.add(child1[i]);
            child2[i] = parent2.permutation.get(i);
            child2Jobs.add(child2[i]);
        }
        int nextIndexChild1 = randomIndex + 1;
        for (Integer jobIndex : parent2.permutation) {
            if (!child1Jobs.contains(jobIndex)) {
                child1[nextIndexChild1++] = jobIndex;
                child1Jobs.add(jobIndex);
            }
        }
        int nextIndexChild2 = randomIndex + 1;
        for (Integer jobIndex : parent1.permutation) {
            if (!child2Jobs.contains(jobIndex)) {
                child2[nextIndexChild2++] = jobIndex;
                child2Jobs.add(jobIndex);
            }
        }
        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
            throw new RuntimeException();
        }
        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
    }

    private static Pair<Genotype, Genotype> doOXCrossover(Genotype parent1, Genotype parent2, Random random, int jobsCount) {
        int randomIndex1 = random.nextInt(jobsCount);
        int randomIndex2 = random.nextInt(jobsCount);
        while (randomIndex1 == randomIndex2) {
            randomIndex2 = random.nextInt(jobsCount);
        }
        if (randomIndex2 < randomIndex1) {
            int tmp = randomIndex1;
            randomIndex1 = randomIndex2;
            randomIndex2 = tmp;
        }
        Integer[] child1 = new Integer[jobsCount];
        Integer[] child2 = new Integer[jobsCount];
        Set<Integer> child1Jobs = new HashSet<>();
        Set<Integer> child2Jobs = new HashSet<>();
        for (int i = randomIndex1; i < randomIndex2; i++) {
            child1[i] = parent2.permutation.get(i);
            child1Jobs.add(child1[i]);
            child2[i] = parent1.permutation.get(i);
            child2Jobs.add(child2[i]);
        }
        List<Integer> parent1Jobs = new ArrayList<>();
        List<Integer> parent2Jobs = new ArrayList<>();
        for (int i = randomIndex2; i < jobsCount; i++) {
            if (!child1Jobs.contains(parent1.permutation.get(i))) {
                parent1Jobs.add(parent1.permutation.get(i));
            }
            if (!child2Jobs.contains(parent2.permutation.get(i))) {
                parent2Jobs.add(parent2.permutation.get(i));
            }
        }
        for (int i = 0; i < randomIndex2; i++) {
            if (!child1Jobs.contains(parent1.permutation.get(i))) {
                parent1Jobs.add(parent1.permutation.get(i));
            }
            if (!child2Jobs.contains(parent2.permutation.get(i))) {
                parent2Jobs.add(parent2.permutation.get(i));
            }
        }
        for (int i = randomIndex2; i < jobsCount; i++) {
            child1[i] = parent1Jobs.remove(0);
            child1Jobs.add(child1[i]);
            child2[i] = parent2Jobs.remove(0);
            child2Jobs.add(child2[i]);
        }
        for (int i = 0; i < randomIndex1; i++) {
            child1[i] = parent1Jobs.remove(0);
            child1Jobs.add(child1[i]);
            child2[i] = parent2Jobs.remove(0);
            child2Jobs.add(child2[i]);
        }

        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
            throw new RuntimeException();
        }

        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
    }


    private static Pair<Genotype, Genotype> doCXCrossover(Genotype parent1, Genotype parent2, int jobsCount, Random random) {
        Integer[] child1 = new Integer[jobsCount];
        Integer[] child2 = new Integer[jobsCount];
        Set<Integer> child1Jobs = new HashSet<>();
        Set<Integer> child2Jobs = new HashSet<>();

        int permutationIndex1 = 0;
        int jobIndex1 = parent1.permutation.get(permutationIndex1);
        while (!child1Jobs.contains(jobIndex1)) {
            child1[permutationIndex1] = jobIndex1;
            child1Jobs.add(jobIndex1);
            jobIndex1 = parent2.permutation.get(permutationIndex1);
            permutationIndex1 = parent1.permutation.lastIndexOf(jobIndex1);
        }
        for (int i = 0; i < jobsCount; i++) {
            if (child1[i] == null) {
                child1[i] = parent2.permutation.get(i);
                child1Jobs.add(child1[i]);
            }
        }

        int permutationIndex2 = 0;
        int jobIndex2 = parent2.permutation.get(permutationIndex2);
        while (!child2Jobs.contains(jobIndex2)) {
            child2[permutationIndex2] = jobIndex2;
            child2Jobs.add(jobIndex2);
            jobIndex2 = parent1.permutation.get(permutationIndex2);
            permutationIndex2 = parent2.permutation.lastIndexOf(jobIndex2);
        }
        for (int i = 0; i < jobsCount; i++) {
            if (child2[i] == null) {
                child2[i] = parent1.permutation.get(i);
                child2Jobs.add(child2[i]);
            }
        }

        if (child1Jobs.size() != jobsCount || child2Jobs.size() != jobsCount) {
            throw new RuntimeException();
        }

        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
    }

    private static Pair<Genotype, Genotype> doOurCXCrossover(Genotype parent1, Genotype parent2, int jobsCount, Random random) {
        Map<Integer, List<Integer>> cycles = new HashMap<>();
        for (int i = 0; i < parent1.permutation.size(); i++) {
            int id = i;
            if (cycles.values().stream().flatMap(Collection::stream).noneMatch(ind -> ind == id)) {
                List<Integer> cycle = new ArrayList<>();
                int index = i;
                while (!cycle.contains(index)) {
                    cycle.add(index);
                    int jobIndex = parent2.permutation.get(index);
                    index = parent1.permutation.lastIndexOf(jobIndex);
                }
                cycles.put(i, cycle);
            }
        }

        Integer[] child1 = new Integer[jobsCount];
        Integer[] child2 = new Integer[jobsCount];

        for (Integer startIndex : cycles.keySet()) {
            Genotype p1, p2;
            if (random.nextBoolean()) {
                p1 = parent1;
                p2 = parent2;
            } else {
                p1 = parent2;
                p2 = parent1;
            }

            for (Integer i : cycles.get(startIndex)) {
                child1[i] = p1.permutation.get(i);
                child2[i] = p2.permutation.get(i);
            }
        }

        return new Pair<>(new Genotype(Arrays.asList(child1)), new Genotype(Arrays.asList(child2)));
    }

    private static Genotype doMutation(Genotype genotype, Random random, int jobsCount) {
        if (random.nextDouble() > MUTATION_PROBABILITY) {
            return genotype;
        }
        switch (MUTATION_TYPE) {
            case EXCHANGE:
                return doExchangeMutation(genotype, random, jobsCount);
            case SHIFT:
                return doShiftMutation(genotype, random, jobsCount);
            default:
                throw new RuntimeException();
        }
    }

    private static Genotype doExchangeMutation(Genotype genotype, Random random, int jobsCount) {
        int randomIndex1 = random.nextInt(jobsCount);
        int randomIndex2;
        if (MUTATION_WINDOW < 1) {
            randomIndex2 = random.nextInt(jobsCount);
            while (randomIndex1 == randomIndex2) {
                randomIndex2 = random.nextInt(jobsCount);
            }
        } else {
        boolean shiftLeft = randomIndex1 != 0 && (randomIndex1 == jobsCount - 1 || random.nextBoolean());
        randomIndex2 = shiftLeft
                ? randomIndex1 - random.nextInt(randomIndex1 - Math.max(randomIndex1 - MUTATION_WINDOW, 0)) - 1
                : random.nextInt(Math.min(randomIndex1 + MUTATION_WINDOW, jobsCount - 1) - randomIndex1) + 1 + randomIndex1;
        }
        if (randomIndex2 < randomIndex1) {
            int tmp = randomIndex1;
            randomIndex1 = randomIndex2;
            randomIndex2 = tmp;
        }

        int tmp = genotype.permutation.remove(randomIndex2);
        genotype.permutation.add(randomIndex2, genotype.permutation.get(randomIndex1));
        genotype.permutation.remove(randomIndex1);
        genotype.permutation.add(randomIndex1, tmp);
        return genotype;
    }

    private static Genotype doShiftMutation(Genotype genotype, Random random, int jobsCount) {
        int randomIndex1 = random.nextInt(jobsCount);
        int randomIndex2;
        if (MUTATION_WINDOW < 1) {
            randomIndex2 = random.nextInt(jobsCount);
            while (randomIndex1 == randomIndex2) {
                randomIndex2 = random.nextInt(jobsCount);
            }
        } else {
        boolean shiftLeft = randomIndex1 != 0 && (randomIndex1 == jobsCount - 1 || random.nextBoolean());
        randomIndex2 = shiftLeft
                ? randomIndex1 - random.nextInt(randomIndex1 - Math.max(randomIndex1 - MUTATION_WINDOW, 0)) - 1
                : random.nextInt(Math.min(randomIndex1 + MUTATION_WINDOW, jobsCount - 1) - randomIndex1) + 1 + randomIndex1;
        }
        var job = genotype.permutation.remove(randomIndex1);
        genotype.permutation.add(randomIndex2, job);
        return genotype;
    }

    private static Genotype doLocalOptimization(Random random, ProblemInstance problemInstance, Genotype genotype) {
        Genotype bestGenotype = genotype;
        for (int i = 0; i < LO_ITERATIONS_COUNT; i++) {
            Genotype mutatedGenotype = new Genotype(new ArrayList<>(genotype.permutation));
            switch (MUTATION_TYPE) {
                case EXCHANGE:
                    doExchangeMutation(mutatedGenotype, random, genotype.permutation.size());
                    break;
                case SHIFT:
                    doShiftMutation(mutatedGenotype, random, genotype.permutation.size());
                    break;
                default:
                    throw new RuntimeException();
            }
            mutatedGenotype.fitness = calculateFitness(mutatedGenotype.permutation, problemInstance.jobs, problemInstance.alpha, problemInstance.energy);
            if (mutatedGenotype.fitness < bestGenotype.fitness || random.nextDouble() < LO_ALLOW_DEGRADATION_PROBABILITY) {
                bestGenotype = mutatedGenotype;
            }
        }
        return bestGenotype;
    }
}
