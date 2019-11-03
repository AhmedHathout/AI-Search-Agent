package main;

import main.entities.*;

import java.util.*;

public class Main {

    // This is a hack :-) to count the number of nodes the search method returns just the node
    private static int numberOfExpandedNodes = 0;

    public static void main(String[] args) {
        String solutionString = solve("5,5;1,2;3,1;0,2,1,1,2,1,2,2,4,0,4,1;0,3,3,0,3,2,3,4,4,3",
                "AS1", true) + ";" + numberOfExpandedNodes;
        System.out.println(solutionString);

        String solutionString2 = solve("7,7;1,2;3,1;0,2,1,1,2,1,2,2,4,0,4,1;0,3,3,0,3,2,3,4,4,3,5,5,6,4,2,6",
                "AS2", false) + ";" + numberOfExpandedNodes;
        System.out.println(solutionString2);
        String solutionString3 = solve("7,7;3,5;1,2;2,3,1,5,3,6,2,6,4,0,4,1;3,4,2,5,4,6,2,6,2,1,5,5,6,4,2,4",
                "GR2", true);
        System.out.println(solutionString3);
    }

    public static String solve(String grid, String strategy, boolean visualise) {

        // This is just in case 2 different examples are run at the same time.
        numberOfExpandedNodes = 0;
        EndGame endGame = parseGridInput(grid);
        SearchTreeNode solution = genericSearchProcedure(endGame, strategy);

        if (solution == null) {
            return "There is no solution";
        }

        String solutionString = getActionSequence(solution).replaceFirst(",", "") + ";" + solution.getCostFromRoot();

        if (visualise) {
            solutionString = visualizeSolution(endGame, solution) + solutionString;
        }

        return solutionString + ";" + numberOfExpandedNodes;
    }

    private static SearchTreeNode genericSearchProcedure(GenericSearchProblem problem,
                                                         String strategy) {


        PriorityQueue<SearchTreeNode> nodeQueue;

        int maximumDepthForIterativeDeepening = -1;

        switch (strategy) {
            case "BF":
                nodeQueue = new PriorityQueue<>((node1, node2) -> node1.getCurrentDepth() - node2.getCurrentDepth());
                break;
            case "DF":
                nodeQueue = new PriorityQueue<>((node1, node2) -> node2.getCurrentDepth() - node1.getCurrentDepth());
                break;
            case "UC":
                nodeQueue = new PriorityQueue<SearchTreeNode>((node1, node2) -> {
                    return node1.getCostFromRoot() - node2.getCostFromRoot();
                });
                break;
            case "ID":
                nodeQueue = new PriorityQueue<>((node1, node2) -> node2.getCurrentDepth() - node1.getCurrentDepth());
                maximumDepthForIterativeDeepening = 0;
                break;
            case "GR1":
                nodeQueue = new PriorityQueue<SearchTreeNode>((node1, node2) -> {
                    return node1.heuristic(1) - node2.heuristic(1);
                });
                break;
            case "GR2":
                nodeQueue = new PriorityQueue<SearchTreeNode>((node1, node2) -> {
                    return node1.heuristic(2) - node2.heuristic(2);
                });
                break;

            case "AS1":
                nodeQueue = new PriorityQueue<SearchTreeNode>((node1, node2) -> {
                    return node1.AStarEvaluation(1) - node2.AStarEvaluation(1);
                });
                break;
            case "AS2":
                nodeQueue = new PriorityQueue<SearchTreeNode>((node1, node2) -> {
                    return node1.AStarEvaluation(2) - node2.AStarEvaluation(2);
                });
                break;

            default:
                throw new AssertionError("Unknow strategy: " + strategy);
        }

        SearchTreeNode root = new SearchTreeNode(problem.getInitialState(),
                null, null, 0);

        // This while loop is for iterative deepening
        while (true) {

            nodeQueue.add(root);
            Set<State> alreadyVisitedStates = new HashSet<>();
            while (true) {
                // If there are no nodes in the queue then there is no solution
                if (nodeQueue.isEmpty()) {
                    if (strategy.equals("ID")) {
                        break;
                    }

                    return null;
                }

                SearchTreeNode currentNode = nodeQueue.remove();

                // If the damage taken is more than 100, discard the node.
                if (currentNode.getCostFromRoot() >= 100) {
                    continue;
                }

                if (problem.goalTest(currentNode.getCurrentState())) {
                    return currentNode;
                }

                if (!alreadyVisitedStates.contains(currentNode.getCurrentState())) {
                    alreadyVisitedStates.add(currentNode.getCurrentState());

                    if (!strategy.equals("ID") ||
                            currentNode.getCurrentDepth() < maximumDepthForIterativeDeepening) {

                        List<SearchTreeNode> newNodes = problem.expand(currentNode);
                        numberOfExpandedNodes++;
                        nodeQueue.addAll(newNodes);
                    }
                }
            }

            maximumDepthForIterativeDeepening++;
        }
    }

    private static EndGame parseGridInput(String grid) {

        String[] splitGrid = grid.split(";");

        String[] gridDimensions = splitGrid[0].split(",");
        int gridLength = Integer.parseInt(gridDimensions[0]);
        int gridWidth = Integer.parseInt(gridDimensions[1]);

        String[] ironManLocationString = splitGrid[1].split(",");
        Location ironMan = new Location(Integer.parseInt(ironManLocationString[0]),
                                        Integer.parseInt(ironManLocationString[1]));

        String[] thanosLocationString = splitGrid[2].split(",");
        Location thanos = new Location(Integer.parseInt(thanosLocationString[0]),
                                       Integer.parseInt(thanosLocationString[1]));

        String[] inifintyStonesLocationString = splitGrid[3].split(",");
        Set<Location> infinityStoneSet = new HashSet<>();
        for (int i = 0; i < inifintyStonesLocationString.length; i +=2) {
            infinityStoneSet.add(new Location(Integer.parseInt(inifintyStonesLocationString[i]),
                                              Integer.parseInt(inifintyStonesLocationString[i + 1])));
        }

        String[] warriorsLocationString = splitGrid[4].split(",");
        Set<Location> warriorSet = new HashSet<>();
        for (int i = 0; i < warriorsLocationString.length; i +=2) {
            warriorSet.add(new Location(Integer.parseInt(warriorsLocationString[i]),
                    Integer.parseInt(warriorsLocationString[i + 1])));
        }

        return new EndGame(gridWidth, gridLength, thanos,
                ironMan, infinityStoneSet, warriorSet);
    }

    private static String visualizeOneNode(EndGame endGame, SearchTreeNode node) {

        String action = "";
        if (node.getLastAction() != null) {
            action = node.getLastAction().toString();
        }
        String[][] grid = new String[endGame.getGridLength()][endGame.getGridWidth()];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Location currentLocation = new Location(i, j);
                grid[i][j] = "";

                if (node.getCurrentState().getIronMan().equals(currentLocation)) {
                    grid[i][j] += "I";
                }

                if (endGame.getThanos().equals(currentLocation)) {
                    grid[i][j] += "T";
                }

                for (Location inifinityStone : node.getCurrentState().getInfinityStoneSet()) {
                    if (inifinityStone.equals(currentLocation)) {
                        grid[i][j] += "S";
                    }
                }

                for (Location warrior : node.getCurrentState().getWarriorSet()) {
                    if (warrior.equals(currentLocation)) {
                        grid[i][j] += "W";
                    }
                }
            }
        }

        String visualizeString = action + " " + node.getCostFromRoot() + "\n";
        for (String[] row : grid) {
            for (String cell : row) {
                if (cell == "") {
                    visualizeString += "-\t";
                }
                else {
                    visualizeString += cell + "\t";
                }
            }
            visualizeString += "\n";
        }

        return visualizeString;
    }

    private static String visualizeSolution(EndGame endGame, SearchTreeNode solution) {
        if (solution.getParentNode() != null) {
            return visualizeSolution(endGame, solution.getParentNode()) + visualizeOneNode(endGame, solution);
        }
        return visualizeOneNode(endGame, solution);
    }

    private static String getActionSequence(SearchTreeNode solution) {
        if (solution.getParentNode() != null) {
            return getActionSequence(solution.getParentNode()) + "," + solution.getLastAction().toString();
        }
        return "";
    }
}
