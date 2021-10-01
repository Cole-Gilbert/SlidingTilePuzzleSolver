
import static java.util.Arrays.sort;
import java.util.List;
import java.util.Stack;

/**
 *
 * * @author colegilbert Mr. Paige Artificial Intelligence H 9/11/2020
 */
public class State {
    //Essentially the node class of the sliding tile project
    //represents the current step you are on (board, actions, etc.)

    private static int numberStates = 0;    // Total number of states created
    private static int statesExpanded = 0;  // Number of states expanded (calls to next)
    private static HashMap<Board, State> hashmap = new HashMap<Board, State>();
    private Stack<State> path;
    private boolean visited; //only used when necessary

    private final int id;                   // A unique id assigned to each state (debugging)
    private final Board board; // Board configuration associated with this state
    private Board.Direction action;
    private State parent;                   // Predecessor along path from start to this state.
    private double cost; //cost to get from start state to this state
    private int numParent;
    //pass in a comparator to compare cost
    //pass in comparator to compare heuristic
    //pass in a comparaor to compare h and c

    private State(Board board) {
        this.id = State.numberStates++;
        this.board = board;
        this.cost = -1;
    }

    // Return the one and only state representing this
    // board configuration.  Create a one if nececessary.
    public static State find(Board board) {
        if (board != null) {
            if (hashmap.contains(board) == false) {
                State newState = new State(board);
                hashmap.add(board, newState);
                numberStates++;
            }
            return hashmap.find(board);
        }
        return null;
    }

    public int id() {
        return this.id;
    }

    public Board getBoard() {
        return this.board;
    }

    public State parent() {
        return this.parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }

    public void setVisited(boolean b) {
        visited = b;
    }

    public boolean isVisited() {
        return visited;
    }

    //returns true if the current state is a goal state
    public boolean isGoal() {
        int[] goal = this.board.getTiles().clone();
        sort(goal);
        for (int i = 0; i < this.board.getTiles().length; i++) {
            if (this.board.getTiles()[i] != goal[i]) {
                return false;
            }
        }
        return true;
    }

    // Legal actions for this state.
    public Board.Direction[] actions() {
        return this.board.empty().validMoves();
    }

    public void setAction(Board.Direction action) {
        this.action = action;
    }

    public Board.Direction getAction() {
        return this.action;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return this.cost;
    }

    //gets the next state, via the direction given to move
    public State next(Board.Direction action) {
        statesExpanded++;
        // State transition function.
        Board board2 = new Board(this.board, action);
        State state2 = this.find(board2);
        return state2;
    }

    //auxillary method for public path
    //essentially a recursive while loop, goes thru parents until null
    private void path(State s) {
        if (s != null) {
            numParent++;
            path.push(s);
            path(s.parent);
        }
    }

    //auxillary method for numParents()
    //same thing as path, but doesn't push states onto the parent stack
    private int parentPath(State s) {
        if (s != null) {
            numParent++;
            parentPath(s.parent);
        }
        return numParent;
    }

    // The path from the start state to this state.
    public Stack<State> path() {
        this.path = new Stack<State>();
        State current = this;
        path(current);
        return this.path;
    }

    //returns the number of parents that a given state has
    //used for DLS
    public int numParents() {
        numParent = 0;
        return parentPath(this);
    }

    //returns true if the two states are equal
    public boolean equals(State other) {
        if (other != null) {
            if (this.board.equals(other.board)) {
                if (this.parent.board.equals(other.parent.board)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof State && this.equals((State) other);
    }

    @Override
    public int hashCode() {
        long hash = 3;
        hash = 7 * hash + this.board.hashCode();
        hash = 7 * hash + this.parent.board.hashCode();
        hash /= 751;
        hash = 29 * hash * (numberStates + statesExpanded);
        return (int) hash;
    }

    @Override
    public String toString() {
        String s = new String();
        s += "State # " + this.id;
        //s += "\n State A " + this.action;
        s += "\n State B: \n" + this.board;
        return s; // Good enough!
    }

    public static int numberStates() {
        return State.numberStates;
    }

    public static int statesExpanded() {
        return State.statesExpanded;
    }

}
