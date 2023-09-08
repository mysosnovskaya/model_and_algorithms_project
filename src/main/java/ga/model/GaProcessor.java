package ga.model;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GaProcessor {
    public List<Pair<Double, Double>> intervals = new LinkedList<>();
    public List<GaJob> assignedJobs = new LinkedList<>();

    public void addInterval(double start, double duration) {
        intervals.add(new Pair<>(start, start + duration));
        intervals = intervals.stream().sorted(Comparator.comparing(Pair::getKey)).collect(Collectors.toList());
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
}
