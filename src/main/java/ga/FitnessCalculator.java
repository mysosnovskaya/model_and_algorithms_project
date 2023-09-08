package ga;

import ga.model.GaJob;
import ga.model.GaProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FitnessCalculator {
    public static double calculateFitness(List<Integer> permutation, List<GaJob> jobs, Double ALPHA, Integer ENERGY) {
        Map<Integer, GaJob> singleProcessorJobs = new HashMap<>();
        for (int i = 0; i < jobs.size(); i++) {
            var job = jobs.get(i);
            singleProcessorJobs.put(i+1, new GaJob(job.processorsCount, job.processorsCount == 1 ? job.volume / 2 : job.volume));
        }

        List<GaJob> sortedJobs = new ArrayList<>();
        for (Integer index : permutation) {
            sortedJobs.add(singleProcessorJobs.get(index));
        }
        double totalSum = calculateTotalSum(sortedJobs, ALPHA, sortedJobs.size());
        double totalEnergy = calculateTotalEnergy(ALPHA, ENERGY / 2.0);
        double totalSumForDurationCalculation = Math.pow(totalSum, (1.0 / (ALPHA - 1.0)));
        for (int i = 0; i < sortedJobs.size(); i++) {
            GaJob job = sortedJobs.get(i);
            job.duration = calculateJobDuration(job, i + 1, totalSumForDurationCalculation, totalEnergy, ALPHA, sortedJobs.size());
        }

        List<GaJob> originalJobsWithDurations = calculateOriginalJobs(sortedJobs);
        var processors = assignJobsToProcessors(originalJobsWithDurations);

        return calculateCSum(processors);
    }

    // 8 6 1 3 10 2 4 7 9 5 - lb 10.03
    // 35/1 50/1 70/2 70/2 175/1 275/2 287/1 800/2
    // 40/1      70/2 70/2       275/2 362/1 800/2

    // 10 3 9 4 7 8 1 2 6 5 - sol
    // 70/2 70/2 362/1      275/2 35/1 40/1 800/2
    // 70/2 70/2 287/1 50/1 275/2 175/1     800/2
    public static double calculateLowerBound(List<GaJob> jobs, Double ALPHA, Integer ENERGY, List<Integer> permutation) {
        Map<Integer, GaJob> singleProcessorJobs = new HashMap<>();
        for (int i = 0; i < jobs.size(); i++) {
            var job = jobs.get(i);
            singleProcessorJobs.put(i+1, new GaJob(job.processorsCount, job.processorsCount == 1 ? job.volume / 2 : job.volume));
        }
        List<GaJob> sortedJobs = new ArrayList<>();

        if (permutation.isEmpty()) {
            sortedJobs = singleProcessorJobs.values().stream()
                    .sorted(Comparator.comparingInt(j -> j.volume)).collect(Collectors.toList());
        } else {
            for (Integer index : permutation) {
                sortedJobs.add(singleProcessorJobs.get(index));
            }
        }

        double totalSum = calculateTotalSum(sortedJobs, ALPHA, sortedJobs.size());
        double totalEnergy = calculateTotalEnergy(ALPHA, ENERGY / 2.0);
        double totalSumForDurationCalculation = Math.pow(totalSum, (1.0 / (ALPHA - 1.0)));

        for (int i = 0; i < sortedJobs.size(); i++) {
            GaJob job = sortedJobs.get(i);
            job.duration = calculateJobDuration(job, i + 1, totalSumForDurationCalculation, totalEnergy, ALPHA, sortedJobs.size());
        }

        return Math.pow(totalSum, (ALPHA / (ALPHA - 1.0))) * totalEnergy;
    }

    private static List<GaJob> calculateOriginalJobs(List<GaJob> sortedJobs) {
        List<GaJob> originalJobsWithDurations = new LinkedList<>();
        for (GaJob job : sortedJobs) {
            GaJob createdJob = new GaJob(job.processorsCount, job.processorsCount == 1 ? job.volume * 2 : job.volume);
            createdJob.duration = job.processorsCount == 1 ? job.duration * 2 : job.duration;
            originalJobsWithDurations.add(createdJob);
        }
        return originalJobsWithDurations;
    }

    private static List<GaProcessor> assignJobsToProcessors(List<GaJob> originalJobsWithDurations) {
        if (originalJobsWithDurations.get(0).processorsCount == 1) {
            originalJobsWithDurations.add(0, new GaJob(2, 0));
        }
        Map<GaJob, List<GaJob>> jobs = new HashMap<>(); // 2proc -> list of 1proc
        List<GaJob> twoProcJobs = new ArrayList<>();
        GaJob last2ProcJob = null;
        for (GaJob originalJobsWithDuration : originalJobsWithDurations) {
            if (originalJobsWithDuration.processorsCount == 2) {
                jobs.put(originalJobsWithDuration, new ArrayList<>());
                last2ProcJob = originalJobsWithDuration;
                twoProcJobs.add(last2ProcJob);
            } else {
                jobs.get(last2ProcJob).add(originalJobsWithDuration);
            }
        }

        List<GaJob> gaJobs = new ArrayList<>();
        for (GaJob twoProcJob : twoProcJobs) {
            gaJobs.add(twoProcJob);
            List<GaJob> singleJobs = jobs.get(twoProcJob);
            singleJobs.sort((job, t1) -> {
                if (job.duration - t1.duration < 0) {
                    return -1;
                } else if (job.duration > t1.duration) {
                    return 1;
                }
                return 0;
            });
            if (singleJobs.size() > 2) {
                var lastJob = singleJobs.remove(singleJobs.size() - 1);
                singleJobs.add(singleJobs.size() - 1, lastJob);
            }
            gaJobs.addAll(singleJobs);
        }
        if (gaJobs.get(0).processorsCount == 2 && gaJobs.get(0).duration == 0.0) {
            gaJobs.remove(0);
        }


        List<GaProcessor> processors = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            processors.add(new GaProcessor());
        }

        for (int i = 0; i < gaJobs.size(); i++) {
            GaJob job = gaJobs.get(i);
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
                for (GaProcessor processor : processors) {
                    double earliestTime = processor.getTheEarliestTime();
                    if (earliestTime > startTime) {
                        startTime = earliestTime;
                    }
                }
                for (GaProcessor processor : processors) {
                    processor.addInterval(startTime, job.duration);
                    processor.assignedJobs.add(job);
                }
            }
        }

        return processors;
    }

    private static double calculateCSum(List<GaProcessor> processors) {
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

    private static double calculateJobDuration(GaJob job, int jobIndex, double totalSum, double totalEnergy, double alpha, int jobsCount) {
        return job.volume / Math.pow((jobsCount - jobIndex + 1), 1.0 / alpha) * totalSum * totalEnergy;
    }

    private static double calculateTotalSum(List<GaJob> jobs, double alpha, int jobsCount) {
        double sum = 0.0;
        for (int i = 0; i < jobs.size(); i++) {
            sum += jobs.get(i).volume * Math.pow((jobsCount - (i + 1) + 1), (alpha - 1.0) / alpha);
        }
        return sum;
    }

    private static double calculateTotalEnergy(double alpha, double energy) {
        return Math.pow(energy, (1.0 / (1.0 - alpha)));
    }
}
