package main.entities;

import java.util.List;
import java.util.Set;

public abstract class GenericSearchProblem {

    private final Set<Action> actionSet;
    private final State initialState;

    public GenericSearchProblem(Set<Action> actionSet, State initialState/*, int totalCost*/) {
        this.actionSet = actionSet;
        this.initialState = initialState;
    }

    public abstract List<SearchTreeNode> expand(SearchTreeNode currentNode);

    public abstract boolean goalTest(State currentState);

    public Set<Action> getActionSet() {
        return actionSet;
    }

    public State getInitialState() {
        return initialState;
    }
}
