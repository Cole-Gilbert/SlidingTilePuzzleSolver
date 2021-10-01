/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rpaige
 * @author colegilbert
 */
import java.util.ArrayList;

public class Puzzle {

    public static void printSolution(Solver.Solution solution, boolean verbose) {
        Board.Direction[] moves = solution.moves();
        State[] states = solution.states();

        // I am assuming that these two arrays are the same length.
        // The state corresponding to the starting board configuration
        // is in states[0]; there is no corresponding action to get
        // into the starting state so moves[0] is null.  For i > 0
        // moves[i] is the action required to transition from
        // states[i-1] to states[i].
        // Do not feel compelled to be bound to this representation of
        // the solution ... but you will need to change the code below
        // if you do change it.
        for (int i = 0; i < states.length; i++) {
            Board.Direction move = moves[i];
            State state = states[i];
            if (move != null) {
                System.out.println(move);
            }
            if (verbose) {
                System.out.println();
                System.out.print(state.getBoard());
                System.out.println();
            }
        }
    }

    private static boolean isOption(String arg) {
        return arg.length() > 0 && arg.charAt(0) == '-';
    }

    //read over cases to determine which algorithms to use to solve the sliding tile puzzle. Furthermore the general
    //procedure is -parameter value (ex -length
    public static void main(String[] args) {
        ArrayList<Integer> values = new ArrayList<>();
        Solver.Algorithm algorithm = Solver.Algorithm.A_STAR;
        boolean verbose = false;
        boolean tracing = false;
        boolean statistics = true;
        boolean errors = false;
        String option = null;
        int rows = 4;
        int columns = 4;
        int limit = 10;

        for (String arg : args) {
            if (option != null && isOption(arg)) {
                System.out.println("Missing value for " + option);
                option = null;
                errors = true;
            }

            switch (arg.toLowerCase()) {
                case "-n":
                case "-c":
                case "-r":
                case "-l":
                case "-size":
                case "-rows":
                case "-cols":
                case "-columns":
                case "-limit":
                    option = arg;
                    continue;

                case "-v":
                case "-verbose":
                    verbose = true;
                    continue;

                case "+v":
                case "-noverbose":
                    verbose = false;
                    continue;

                case "-t":
                case "-tracing":
                    tracing = true;
                    continue;

                case "+t":
                case "-notracing":
                    tracing = false;
                    continue;

                case "-s":
                case "-stats":
                case "-statistics":
                    statistics = true;
                    continue;

                case "+s":
                case "-nostats":
                case "-nostatistics":
                    statistics = false;
                    continue;

                case "-bfs":
                case "-breadth_first_search":
                    algorithm = Solver.Algorithm.BREADTH_FIRST_SEARCH;
                    continue;

                case "-dfs":
                case "-depth_first_search":
                    algorithm = Solver.Algorithm.DEPTH_FIRST_SEARCH;
                    continue;

                case "-dls":
                case "-depth_limited_search":
                    algorithm = Solver.Algorithm.DEPTH_LIMITED_SEARCH;
                    continue;

                case "-id":
                case "-iterative_deepening":
                    algorithm = Solver.Algorithm.ITERATIVE_DEEPENING;
                    continue;

                case "-bds":
                case "-bidirectional_search":
                    algorithm = Solver.Algorithm.BIDIRECTIONAL_SEARCH;
                    continue;

                case "-ucs":
                case "-uniform_cost_search":
                    algorithm = Solver.Algorithm.UNIFORM_COST_SEARCH;
                    continue;

                case "-gbfs":
                case "-greedy":
                case "-greedy_best_first_search":
                    algorithm = Solver.Algorithm.GREEDY_BEST_FIRST_SEARCH;
                    continue;

                case "-a":
                case "-a*":
                case "a_star":
                    algorithm = Solver.Algorithm.A_STAR;
                    continue;

                default:
                    if (isOption(arg)) {
                        System.out.println("Invalid option: " + arg);
                        errors = true;
                        continue;
                    }
            }

            int value = 0;
            try {
                value = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                if (option != null) {
                    System.out.println("Invalid value for option " + option + ": " + arg);
                } else {
                    System.out.println("Invalid tile: " + arg);
                }
                errors = true;
                continue;
            }

            if (option == null) {
                values.add(value);

            } else {
                switch (option) {
                    case "-n":
                    case "-size":
                        rows = value;
                        columns = value;
                        break;

                    case "-r":
                    case "-rows":
                        rows = value;
                        break;

                    case "-c":
                    case "-cols":
                    case "-columns":
                        columns = value;
                        break;

                    case "-l":
                    case "-limit":
                        limit = value;
                        break;
                }
                option = null;
            }
        }

        if (option != null) {
            System.out.println("Missing value for " + option);
            errors = true;
        }

        if (errors) {
            return;
        }

        int[] tiles = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            tiles[i] = values.get(i);
        }

        Board board = new Board(rows, columns, tiles);
        Solver solver = new Solver(limit, tracing);
        Solver.Solution solution = solver.solve(algorithm, board);

        if (solution != null) {
            printSolution(solution, verbose);
        } else {
            System.out.println("No solution");
        }

        if (statistics) {
            System.out.println();
            if (solution != null) {
                System.out.println("Solution steps: " + solution.numberSteps());
            }
            System.out.println("States created: " + State.numberStates());
            System.out.println("States expanded: " + State.statesExpanded());
        }
    }
}
