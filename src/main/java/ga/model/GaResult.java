package ga.model;

import java.util.List;
import java.util.Map;

public class GaResult {
    public Double solution;
    public List<Integer> permutation;
    public List<SolutionStatistics> solutionStatistics;
    public Long totalGaTimeSecs;
    public Integer solutionIterationFound;
    public Long solutionTimeSecsFound;

    public GaResult(Double solution) {
        this.solution = solution;
    }

    public GaResult(Double solution, List<Integer> permutation, List<SolutionStatistics> solutionStatistics, Long totalGaTimeSecs, Integer solutionIterationFound, Long solutionTimeSecsFound) {
        this.solution = solution;
        this.permutation = permutation;
        this.solutionStatistics = solutionStatistics;
        this.totalGaTimeSecs = totalGaTimeSecs;
        this.solutionIterationFound = solutionIterationFound;
        this.solutionTimeSecsFound = solutionTimeSecsFound;
    }

    public List<String> getResults() {
        return List.of(
                solution.toString(),
                permutation.toString(),
                solutionIterationFound.toString(),
                solutionTimeSecsFound.toString(),
                totalGaTimeSecs.toString()
        );
    }
}
