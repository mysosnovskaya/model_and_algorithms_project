package ga.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Genotype {
    public List<Integer> permutation = new ArrayList<>();
    public double fitness = 0.0;

    public Genotype(List<Integer> permutation) {
        this.permutation.addAll(permutation);
    }

    public Genotype(List<Integer> permutation, double fitness) {
        this.permutation = permutation;
        this.fitness = fitness;
    }


    @Override
    public int hashCode() {
        return Objects.hash(permutation, fitness);
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(permutation, ((Genotype) obj).permutation) && Objects.equals(fitness, ((Genotype) obj).fitness);
    }
}
