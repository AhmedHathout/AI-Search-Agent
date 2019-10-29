package main.entities;

import java.util.Objects;

public class SearchTreeNode {

    private final State currentState;
    private final SearchTreeNode parentNode;
    private final Action lastAction;
    private final int currentDepth;
    private int costFromRoot;

    public SearchTreeNode(State currentState, SearchTreeNode parentNode,
                          Action lastAction, int currentDepth) {

        this.currentState = currentState;
        this.parentNode = parentNode;
        this.lastAction = lastAction;
        this.currentDepth = currentDepth;
        this.costFromRoot = 0;
    }

    /**
     * Calculates a rough estimate of the cost from this node to the goal.
     * To be admissible, it returns the minimum remaining damage that is to be taken.
     * That is, the number of the remaining stones * the damage taken to collect the stone
     * plus the damage taken from Thanos before the snap action.
     * The second heuristic function return the same number as the first one but without the
     * damage taken from Thanos
     * If it is a goal node then it returns 0 instead
     */
    public int heuristic(int i) {
        if (getCurrentState().isSnapped()) {
            return 0;
        }
        if (i == 1) {
            return getCurrentState().getInfinityStoneSet().size() * 3 + 5;
        }
        return getCurrentState().getInfinityStoneSet().size() * 3;
    }

    /**
     * @return the heuristic value plus the cost from the root but returns 0 if it is a goal
     */
    public int AStarEvaluation(int i) {
        if (getCurrentState().isSnapped()) {
            return 0;
        }
        return heuristic(i) + getCostFromRoot();
    }
    public State getCurrentState() {
        return currentState;
    }

    public SearchTreeNode getParentNode() {
        return parentNode;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public int getCostFromRoot() {
        return costFromRoot;
    }

    public void setCostFromRoot(int costFromRoot) {
        this.costFromRoot = costFromRoot;
    }

    @Override
    public String toString() {
        return lastAction.toString() + getCurrentState().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchTreeNode that = (SearchTreeNode) o;
        return currentDepth == that.currentDepth &&
                costFromRoot == that.costFromRoot &&
                currentState.equals(that.currentState) &&
                Objects.equals(parentNode, that.parentNode) &&
                Objects.equals(lastAction, that.lastAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentState, parentNode, lastAction, currentDepth, costFromRoot);
    }
}
