package main.entities;

import java.util.List;
import java.util.Set;

public abstract class GenericSearchProblem {

    final Set<Action> actionSet;
    final State initialState;
    int totalCost;

    public GenericSearchProblem(Set<Action> actionSet, State initialState, int totalCost) {
        this.actionSet = actionSet;
        this.initialState = initialState;
        this.totalCost = totalCost;
    }

    public abstract List<SearchTreeNode> expand(SearchTreeNode currentNode);

    public abstract boolean goalTest(State currentState);

    public Set<Action> getActionSet() {
        return actionSet;
    }

    public State getInitialState() {
        return initialState;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }
}
