package main.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EndGame extends GenericSearchProblem {

    private Location thanos;
    private int gridWidth;
    private int gridLength;

    public EndGame(int gridWidth, int gridLength,
                   Location thanos, Location ironMan,
                   Set<Location> infinityStoneSet, Set<Location> warriorSet) {

        // Had to create everything in super() because it must be the first
        // line in the constructor
        super(new HashSet<>(),
              new State(ironMan, infinityStoneSet, warriorSet, false));


        getActionSet().add(new Action("up", 0));
        getActionSet().add(new Action("down", 0));
        getActionSet().add(new Action("left", 0));
        getActionSet().add(new Action("right", 0));
        getActionSet().add(new Action("collect", 3));
        getActionSet().add(new Action("kill", 2));
        getActionSet().add(new Action("snap", 0));

        this.thanos = thanos;

        this.gridWidth = gridWidth;
        this.gridLength = gridLength;
    }

    @Override
    public List<SearchTreeNode> expand(SearchTreeNode currentNode) {

        List<SearchTreeNode> newNodeList = new ArrayList<>();

        Location ironMan = currentNode.getCurrentState().getIronMan();
        Set<Location> infinityStoneSet = currentNode.getCurrentState().getInfinityStoneSet();
        Set<Location> warriorSet = currentNode.getCurrentState().getWarriorSet();

        // If there is an infinity stone on the same cell. Iron Man should not move but can only kill or collect.
        if (infinityStoneSet
                .stream()
                .noneMatch(infinityStone -> infinityStone.equals(ironMan))) {

            if (ironMan.getY() < getGridWidth() - 1) {
                State newState = new State(ironMan.moveRight(),
                        infinityStoneSet,
                        warriorSet,
                        false);

                SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                        getActionByName("right"), currentNode.getCurrentDepth() + 1);

                newNode.setCostFromRoot(computeTotalCost(newNode));

                if (permissibleCell(newNode)) {
                    newNodeList.add(newNode);
                }
            }

            if (ironMan.getY() > 0) {
                State newState = new State(ironMan.moveLeft(),
                        infinityStoneSet,
                        warriorSet,
                        false);

                SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                        getActionByName("left"), currentNode.getCurrentDepth() + 1);

                newNode.setCostFromRoot(computeTotalCost(newNode));

                if (permissibleCell(newNode)) {
                    newNodeList.add(newNode);
                }
            }

            if (ironMan.getX() < getGridLength() - 1) {
                State newState = new State(ironMan.moveDown(),
                        infinityStoneSet,
                        warriorSet,
                        false);

                SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                        getActionByName("down"), currentNode.getCurrentDepth() + 1);

                newNode.setCostFromRoot(computeTotalCost(newNode));

                if (permissibleCell(newNode)) {
                    newNodeList.add(newNode);
                }
            }

            if (ironMan.getX() > 0) {
                State newState = new State(ironMan.moveUp(),
                        infinityStoneSet,
                        warriorSet,
                        false);

                SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                        getActionByName("up"), currentNode.getCurrentDepth() + 1);

                newNode.setCostFromRoot(computeTotalCost(newNode));

                if (permissibleCell(newNode)) {
                    newNodeList.add(newNode);
                }
            }
        }

        // Kill an adjacent warrior
        for (Location warrior : warriorSet) {
            if (warrior.isAdjacent(ironMan)) {
                Set<Location> killedWarriorSet = new HashSet<>(warriorSet);
                killedWarriorSet.remove(warrior);

                State newState = new State(ironMan, infinityStoneSet,
                        killedWarriorSet, false);

                SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                        getActionByName("kill"), currentNode.getCurrentDepth() + 1);
                newNode.setCostFromRoot(computeTotalCost(newNode));

                newNodeList.add(newNode);

            }
        }

        if (infinityStoneSet
                .stream()
                .anyMatch(infinityStone -> infinityStone.equals(ironMan))) {

            // Remove the infinity stone that has the same location as Iron Man
            Set<Location> newInfinityStoneSet = new HashSet<>(infinityStoneSet);
            newInfinityStoneSet.remove(ironMan);

            State newState = new State(ironMan,
                    newInfinityStoneSet,
                    warriorSet,
                    false);

            SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                    getActionByName("collect"), currentNode.getCurrentDepth() + 1);

            newNode.setCostFromRoot(computeTotalCost(newNode));
            newNodeList.add(newNode);
        }

        // If location of Iron Man and Thanos is the same and there are no more infinity stones, snap
        if (ironMan.equals(getThanos()) && infinityStoneSet.isEmpty()) {
            State newState = new State(ironMan,
                    infinityStoneSet,
                    warriorSet,
                    true);

            SearchTreeNode newNode = new SearchTreeNode(newState, currentNode,
                    getActionByName("snap"), currentNode.getCurrentDepth() + 1);
            newNode.setCostFromRoot(computeTotalCost(newNode));

            newNodeList.add(newNode);
        }

        return newNodeList;
    }

    @Override
    public boolean goalTest(State currentState) {
        return currentState.isSnapped();
    }

    private int computeTotalCost(SearchTreeNode newNode) {

        int previousCost = newNode.getParentNode().getCostFromRoot();

        Action lastAction = newNode.getLastAction();

        // If the action is snap, there is no damage taken
        if (lastAction.getName().equals("snap")) {
            return previousCost;
        }

        // Kill does 2 units of damage. There is also the damage taken by the remaining adjacent enemies
        return previousCost + lastAction.getCost() + damageTakenFromEnemies(newNode);
    }

    /**
     * Checks whether Iron Man can be in this cell or not. Iron Man can not be in the same
     * cell a warrior is in. He can be in the same cell with thanos only if he collected
     * all the infinity stones.
     *
     * @param node The new node that resutled from the expansion of the current node.
     * @return true if Iron Man can be in that cell, false otherwise
     */
    private boolean permissibleCell(SearchTreeNode node) {
        Location ironMan = node.getCurrentState().getIronMan();
        return (node.getCurrentState().getWarriorSet()
                                        .stream()
                                        .noneMatch(warrior -> warrior.equals(ironMan)) &&
                (!ironMan.equals(this.getThanos()) || node.getCurrentState().getInfinityStoneSet().size() == 0));
    }

    private Action getActionByName(String name) {
        return getActionSet()
                .stream()
                .filter(action -> action.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new AssertionError("Action name unknown"));
    }

    private int damageTakenFromEnemies(SearchTreeNode node) {
        int damageTakenFromCurrentCell = 0;

        Location ironMan = node.getCurrentState().getIronMan();
        Set<Location> warriorSet = node.getCurrentState().getWarriorSet();

        // Check if the enemy is in the above row
        if (warriorSet
                .stream()
                .anyMatch(warrior -> warrior.getX() == ironMan.getX() - 1 && warrior.getY() == ironMan.getY())) {

            damageTakenFromCurrentCell++;
        }

        // Check if the enemy is in the below row
        if (warriorSet
                .stream()
                .anyMatch(warrior -> warrior.getX() == ironMan.getX() + 1 && warrior.getY() == ironMan.getY())) {

            damageTakenFromCurrentCell++;

        }

        // Check if the enemy is in the cell to the left
        if (warriorSet
                .stream()
                .anyMatch(warrior -> warrior.getY() == ironMan.getY() - 1 && warrior.getX() == ironMan.getX())) {

            damageTakenFromCurrentCell++;

        }

        if (warriorSet
                .stream()
                .anyMatch(warrior -> warrior.getY() == ironMan.getY() + 1 && warrior.getX() == ironMan.getX())) {

            damageTakenFromCurrentCell++;

        }

        // Check if thanos is in an adjacent cell
        if ((ironMan.getX() + 1 == getThanos().getX() && ironMan.getY() == getThanos().getY()) ||
            (ironMan.getX() - 1 == getThanos().getX() && ironMan.getY() == getThanos().getY()) ||
            (ironMan.getY() + 1 == getThanos().getY() && ironMan.getX() == getThanos().getX()) ||
            (ironMan.getY() - 1 == getThanos().getY() && ironMan.getX() == getThanos().getX()) ||
            (ironMan.getY() == getThanos().getY() && ironMan.getX() == getThanos().getX())){

            damageTakenFromCurrentCell += 5;
        }

        return damageTakenFromCurrentCell;
    }

    public Location getThanos() {
        return thanos;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridLength() {
        return gridLength;
    }
}
