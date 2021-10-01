
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 *
 * @author colegilbert 
 */
public class Solver {

    //this class houses all our search algorithms
    public static enum Algorithm {
        BREADTH_FIRST_SEARCH,
        DEPTH_FIRST_SEARCH,
        DEPTH_LIMITED_SEARCH,
        ITERATIVE_DEEPENING,
        BIDIRECTIONAL_SEARCH,
        UNIFORM_COST_SEARCH,
        GREEDY_BEST_FIRST_SEARCH,
        A_STAR
    }
    private int limit;        // Limit for depth-limited serach

    private boolean tracing;  // Turn on tracing for debugging
    private List<Boolean> visited; //i dont use this

    public Solver(int limit, boolean tracing) {
        this.limit = limit;
        this.tracing = tracing;
    }

    //this class returns the solution from a state
    //(list of the states it took to get from start state to goal state)
    public static class Solution {

        private Board.Direction[] dire;
        private State[] stat;
        private int totalSteps;

        public Solution(State solution) {

            //travels back thru parents
            Stack<State> path = new Stack<State>();
            path = solution.path();

            this.dire = new Board.Direction[path.size()];
            this.stat = new State[path.size()];
            this.totalSteps = 0;

            int i = 0;
            while (!path.empty()) {
                this.stat[i] = path.pop();
                if (i == 0) {
                    this.dire[i] = null;
                } else {
                    this.dire[i] = this.stat[i].getAction();
                }
                i++;
            }
            this.totalSteps = this.dire.length - 1; //must be -1, lose one in the front
        }

        public Solution(Board.Direction[] directions, State[] states, int steps) {
            this.dire = directions;
            this.stat = states;
            this.totalSteps = steps;
        }

        public Board.Direction[] moves() {  // Sequence of actions (moves)
            return this.dire;
        }

        public State[] states() {   // State sequence for the solution
            return this.stat;
        }

        public int numberSteps() {     // Number of moves in the solution
            return this.totalSteps;
        }
    }

    //PLEASE NOTE VARIABLE CONVENTIONS
    //node = start state
    //currentNode = current state
    //frontier = fontier of current state
    //CHECKED!
    public Solution breadthFirstSearch(Board start) {
        if (start == null) {
            return null;
        }

        LinkedList<State> queue = new LinkedList<State>();
        State node = State.find(start);

        queue.add(node);
        node.setVisited(true);

        while (!queue.isEmpty()) {
            State currentNode = queue.removeFirst();
            if (currentNode.isGoal()) {
                return new Solution(currentNode);
            }
            Board.Direction[] frontier = currentNode.getBoard().empty().validMoves();
            for (Board.Direction d : frontier) {
                State n = currentNode.next(d);
                if (n != null && !n.isVisited()) {
                    queue.addLast(n); //appends to the end
                    n.setParent(currentNode);
                    n.setVisited(true);
                    n.setAction(d);
                }
            }
        }
        return null; //(find goal, parent to parent to parent) put on stack, pop it off
    }

    //CHECKED!
    public Solution depthFirstSearch(Board start) {
        if (start == null) {
            return null;
        }

        Stack<State> stack = new Stack<State>();
        State node = State.find(start);
        stack.add(node);

        while (!stack.isEmpty()) {
            State currentNode = stack.pop();
            if (currentNode.isGoal()) {
                stack = new Stack<State>();
                return new Solution(currentNode);
            }
            if (!currentNode.isVisited()) {
                //System.out.print(element.data + " ");
                currentNode.setVisited(true);

                Board.Direction[] frontier = currentNode.getBoard().empty().validMoves();
                for (Board.Direction d : frontier) {
                    State n = currentNode.next(d);
                    if (n != null && !n.isVisited()) {
                        n.setParent(currentNode);
                        n.setAction(d);
                        stack.add(n);
                    }
                }
            }
        }
        return null;
    }

    //CHECKED!
    public Solution depthLimitedSearch(Board start, int limit) {
        if (start == null) {
            return null;
        }
        if (limit < 0) {
            return null;
        }

        Stack<State> stack = new Stack<State>();
        State node = State.find(start);
        stack.add(node);

        while (!stack.isEmpty()) {
            State currentNode = stack.pop();
            if (currentNode.isGoal()) {
                return new Solution(currentNode);
            }

            if (!currentNode.isVisited() //&& stack.size()-1 <= limit
                    ) {
                //System.out.print(element.data + " ");
                currentNode.setVisited(true);

                Board.Direction[] frontier = currentNode.getBoard().empty().validMoves();
                for (Board.Direction d : frontier) {
                    State n = currentNode.next(d);
                    if (n != null && !n.isVisited()) {
                        n.setParent(currentNode);
                        n.setAction(d);
                        if (n.numParents() <= limit) {
                            stack.add(n);
                        }
                    }
                }
            }
        }
        return null;
    }

    //CHECKED!
    public Solution iterativeDeepening(Board start) {
        if (start == null) {
            return null;
        }

        int limit2 = 0;

        Stack<State> stack = new Stack<State>();
        State node = State.find(start);
        stack.add(node);
        while (limit2 < 100) {

            //THIS NEXT CODE BLOCK IS THE SAME THING AS DLS
            //This is essentially a call to DLS, but I had problems when it wasn't set up like this
            //I know it is better to call the method, but it didnt work so I did this instead
            if (start == null) {
                return null;
            }
            if (limit < 0) {
                return null;
            }

            stack.add(node);

            while (!stack.isEmpty()) {
                State currentNode = stack.pop();
                if (currentNode.isGoal()) {
                    return new Solution(currentNode);
                }
                if (!currentNode.isVisited() && stack.size() - 1 <= limit2) {
                    //System.out.print(element.data + " ");
                    currentNode.setVisited(true);

                    Board.Direction[] frontier = currentNode.getBoard().empty().validMoves();
                    for (Board.Direction d : frontier) {
                        State n = currentNode.next(d);
                        if (n != null && !n.isVisited()) {
                            n.setParent(currentNode);
                            n.setAction(d);
                            stack.add(n);
                        }
                    }
                }
            }
            limit2++;
        }
        return null;
    }

//    public Solution bidirectionalSearch(Board start) {
//
//    }
    //CHECKED!
    public Solution uniformCostSearch(Board start) {
        PriorityQueue<State> priorityQ = new PriorityQueue<State>(new Comparator<State>() {
            @Override
            public int compare(State s1, State s2) {
                return (int) (s1.getCost() - s2.getCost());
            }
        });
        State node = State.find(start);

        priorityQ.add(node);
        node.setVisited(true);
        node.setCost(0.0);

        return search(priorityQ);
    }

    //CHECKED!
    public Solution greedyBestFirstSearch(Board start) {
        PriorityQueue<State> priorityQ = new PriorityQueue<State>(new Comparator<State>() {
            @Override
            public int compare(State s1, State s2) {
                return (int) (s1.getBoard().heuristic() - s2.getBoard().heuristic());
            }
        });
        State node = State.find(start);

        priorityQ.add(node);
        node.setVisited(true);
        node.setCost(0.0);

        return search(priorityQ);
    }

    //CHECKED!
    public Solution aStar(Board start) {
        PriorityQueue<State> priorityQ = new PriorityQueue<State>(new Comparator<State>() {
            @Override
            public int compare(State s1, State s2) {
                return (int) ((s1.getCost() + s1.getBoard().heuristic()) - (s2.getCost() + s2.getBoard().heuristic()));
            }
        });
        State node = State.find(start);

        priorityQ.add(node);
        node.setVisited(true);
        node.setCost(0.0);

        return search(priorityQ);
    }

    //generic search method to go through the priority queue of states
    private Solution search(PriorityQueue<State> priorityQ) {
        while (!priorityQ.isEmpty()) {
            State currentNode = priorityQ.remove();
            if (currentNode.isGoal()) {
                return new Solution(currentNode);
            }

            Board.Direction[] frontier = currentNode.getBoard().empty().validMoves();
            for (Board.Direction d : frontier) {
                State n = currentNode.next(d);

                double nCost = currentNode.getCost() + n.getBoard().cost(d);
                if (n.getCost() < 0 || n.getCost() > nCost) {
                    n.setCost(nCost);
                }

                if (n != null && !n.isVisited()) {
                    priorityQ.add(n);
                    n.setParent(currentNode);
                    n.setVisited(true);
                    n.setAction(d);
                }
            }
        }
        return null;
    }

    public Solution solve(Algorithm algorithm, Board start) {
        switch (algorithm) {
            case BREADTH_FIRST_SEARCH:
                return breadthFirstSearch(start);
            case DEPTH_FIRST_SEARCH:
                return depthFirstSearch(start);
            case DEPTH_LIMITED_SEARCH:
                return depthLimitedSearch(start, this.limit);
            case ITERATIVE_DEEPENING:
                return iterativeDeepening(start);
            case BIDIRECTIONAL_SEARCH:
            //return bidirectionalSearch(start);
            case UNIFORM_COST_SEARCH:
                return uniformCostSearch(start);
            case GREEDY_BEST_FIRST_SEARCH:
                return greedyBestFirstSearch(start);
            case A_STAR:
                return aStar(start);
            default:
                return null;
        }
    }
}
