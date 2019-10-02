package main.entities;


import java.util.Objects;

/**
 * Represents the action that Iron Man is allowed to perform
 */
public class Action {

    final String name;
    final int cost;

    public Action(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return cost == action.cost &&
                name.equals(action.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
