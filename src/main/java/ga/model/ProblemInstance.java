package ga.model;

import java.util.List;

public class ProblemInstance {
    public List<GaJob> jobs;
    public Double alpha;

    public Double single;
    public Double small;
    public Integer exampleNumber;
    public Integer blocks;
    public String seriesName;
    public Integer energy;

    public ProblemInstance(List<GaJob> jobs) {
        this.jobs = jobs;
    }

    public ProblemInstance(List<GaJob> jobs, Double alpha, Double single, Double small,
                           Integer exampleNumber, Integer blocks, String seriesName, Integer energy) {
        this.jobs = jobs;
        this.alpha = alpha;
        this.single = single;
        this.small = small;
        this.exampleNumber = exampleNumber;
        this.blocks = blocks;
        this.seriesName = seriesName;
        this.energy = energy;
    }

    public List<String> getParams() {
        return List.of(
                alpha.toString(),
                jobs.size() + "",
                single.toString(),
                small.toString(),
                exampleNumber.toString(),
                blocks.toString(),
                seriesName,
                energy.toString(),
                jobs.toString()
        );
    }
}
