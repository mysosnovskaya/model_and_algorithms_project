package ga.model;

import ga.model.types.CrossoverType;

import java.util.Map;

public class SolutionStatistics {
    public Integer iteration;
    public Double min;
    public Double avg;
    public Double max;
    public Map<CrossoverType, Double> crossoverBytes;

    public SolutionStatistics(Integer iteration, Double min, Double avg, Double max, Map<CrossoverType, Double> crossoverBytes) {
        this.iteration = iteration;
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.crossoverBytes = crossoverBytes;
    }
}

