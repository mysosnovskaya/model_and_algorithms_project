package ga.model;

public class GaJob {
    public final int processorsCount;
    public final int volume;
    public double duration = 0.0;

    public GaJob(int processorsCount, int volume) {
        this.processorsCount = processorsCount;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "[" + processorsCount + ", " + volume + "]";
    }
}
