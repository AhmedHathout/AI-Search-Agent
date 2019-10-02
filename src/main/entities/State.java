package main.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class State {

    final Location ironMan;
    final Set<Location> infinityStoneSet;
    final Set<Location> warriorSet;
    boolean snapped;

    public State(Location ironMan, Set<Location> infinityStoneSet,
                 Set<Location> warriorSet, boolean snapped) {

        this.ironMan = ironMan;
        this.infinityStoneSet = infinityStoneSet;
        this.warriorSet = warriorSet;
        this.snapped = snapped;
    }

    public Location getIronMan() {
        return ironMan;
    }

    public Set<Location> getInfinityStoneSet() {
        return infinityStoneSet;
    }

    public Set<Location> getWarriorSet() {
        return warriorSet;
    }

    public boolean isSnapped() {
        return snapped;
    }

    public void setSnapped(boolean snapped) {
        this.snapped = snapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return snapped == state.snapped &&
                Objects.equals(ironMan, state.ironMan) &&
                Objects.equals(infinityStoneSet, state.infinityStoneSet) &&
                Objects.equals(warriorSet, state.warriorSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ironMan, infinityStoneSet, warriorSet, snapped);
    }
}
