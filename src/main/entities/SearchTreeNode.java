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
