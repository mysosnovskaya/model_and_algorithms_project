package ga.model;

import ga.model.types.CrossoverType;
import ga.model.types.MutationType;
import ga.model.types.SelectionType;

import java.util.List;

public class GaParams {
    public Integer iterationsCount;
    public Integer populationSize;
    public Integer elitesCount;
    public Double randomInitialPopulationProbability;
    public Integer bestRandomPopulationSize;
    public SelectionType selectionType;
    public Double crossoverProbability;
    public CrossoverType crossoverType;
    public Double mutationProbability;
    public MutationType mutationType;
    public Integer mutationWindow;

    public Double lambda;

    public Boolean includeZero;

    public GaParams(Integer iterationsCount, Integer populationSize, Integer elitesCount, Double randomInitialPopulationProbability,
                    Integer bestRandomPopulationSize, SelectionType selectionType, Double crossoverProbability, CrossoverType crossoverType,
                    Double mutationProbability, MutationType mutationType, Integer mutationWindow, Double lambda, Boolean includeZero) {
        this.iterationsCount = iterationsCount;
        this.populationSize = populationSize;
        this.elitesCount = elitesCount;
        this.randomInitialPopulationProbability = randomInitialPopulationProbability;
        this.bestRandomPopulationSize = bestRandomPopulationSize;
        this.selectionType = selectionType;
        this.crossoverProbability = crossoverProbability;
        this.crossoverType = crossoverType;
        this.mutationProbability = mutationProbability;
        this.mutationType = mutationType;
        this.mutationWindow = mutationWindow;
        this.lambda = lambda;
        this.includeZero = includeZero;
    }

    public List<String> getParams() {
        return List.of(
                iterationsCount.toString(),
                populationSize.toString(),
                elitesCount.toString(),
                randomInitialPopulationProbability.toString(),
                bestRandomPopulationSize.toString(),
                selectionType.name(),
                crossoverProbability.toString(),
                crossoverType.name(),
                mutationProbability.toString(),
                mutationType.name(),
                mutationWindow.toString(),
                lambda.toString(),
                includeZero.toString()
        );
    }
}
