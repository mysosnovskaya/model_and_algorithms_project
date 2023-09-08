package ga.model.types;

public enum SelectionType {
    RANKING(null), TOURNEY_3(3), TOURNEY_5(5), TOURNEY_10(10), TOURNEY_15(15);
    private final Integer tourneySize;

    SelectionType(Integer tourneySize) {
        this.tourneySize = tourneySize;
    }

    public Integer getTourneySize() {
        return tourneySize;
    }
}
