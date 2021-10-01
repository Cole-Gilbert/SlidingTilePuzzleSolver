
import static java.util.Arrays.sort;

/**
 *
 * @author colegilbert Mr. Paige Artificial Intelligence H 9/11/2020
 */
public class Board {

    // An IMMUTABLE class to represent a configuration of the sliding tile puzzle.
    private int width;            // Number of squares wide //columns
    private int height;           // Number of squares tall //rows
    private int[] tiles;
    private Position empty;       // The location of the empty square

    //different static types for the actions of tiles in certain generalized conditions
    ///tile in the middle
    public static Direction[] bNotEdge = new Direction[]{Direction.LEFT, Direction.RIGHT, Direction.DOWN, Direction.UP};

    //tile on left column
    public static Direction[] bLeft = new Direction[]{Direction.RIGHT, Direction.DOWN, Direction.UP};
    //tile on right column
    public static Direction[] bRight = new Direction[]{Direction.LEFT, Direction.DOWN, Direction.UP};

    //tile on top row
    public static Direction[] bTop = new Direction[]{Direction.LEFT, Direction.DOWN, Direction.RIGHT};
    //tile on bottom row
    public static Direction[] bBottom = new Direction[]{Direction.LEFT, Direction.UP, Direction.RIGHT};

    //tile in top left corner
    public static Direction[] bTopLeft = new Direction[]{Direction.DOWN, Direction.RIGHT};
    //tile in top right corner
    public static Direction[] bTopRight = new Direction[]{Direction.LEFT, Direction.DOWN};

    //tile in bottom left corner
    public static Direction[] bBottomLeft = new Direction[]{Direction.UP, Direction.RIGHT};
    //tile in bottom right corner
    public static Direction[] bBottomRight = new Direction[]{Direction.LEFT, Direction.UP};

    public static enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public int[] getTiles() {
        return tiles;
    }

    public class Position {

        // A class to represent the position of a tile in the puzzle.
        // index in the range 0 .. width*height-1.'
        private int index;

        public Position(int row, int column) {
            if (row < 0 || row >= Board.this.height) {
                throw new IllegalArgumentException("Row = " + row);
            } else if (column < 0 || column >= Board.this.width) {
                throw new IllegalArgumentException("Column = " + column);
            }
            this.index = row * Board.this.width + column;
        }

        public Position(int index) {
            if (index < 0 || index > Board.this.tiles.length) {
                throw new IllegalArgumentException("Index = " + index);
            }
            this.index = index;
        }

        public Position(Position p) {
            this.index = p.index();
        }

        //converts index to row
        public int row() {
            return this.index / Board.this.width;
        }

        //converts index to column
        public int column() {
            return this.index % Board.this.width;
        }

        public int index() {
            return this.index;
        }

        //checks if the tile is at the top
        public boolean atTop() {
            if (this.row() == 0) {
                return true;
            } else {
                return false;
            }
        }

        //checks if the tile is at the bottom
        public boolean atBottom() {
            if (this.row() == Board.this.height - 1) {
                return true;
            } else {
                return false;
            }
        }

        //checks if the tile is on the left edge
        public boolean atLeft() {
            if (this.column() == 0) {
                return true;
            } else {
                return false;
            }
        }

        //checks if the tile is on the right edge
        public boolean atRight() {
            if (this.column() == Board.this.width - 1) {
                return true;
            } else {
                return false;
            }
        }

        //moves tile up one
        public Position up() {
            return new Position(row() - 1, column());

        }

        //moves tile down one
        public Position down() {
            return new Position(row() + 1, column());

        }

        //moves tiles left one
        public Position left() {
            return new Position(row(), column() - 1);

        }

        //moves tiles right one
        public Position right() {
            return new Position(row(), column() + 1);

        }

        //moves the tiles in the direction specified
        public Position move(Direction direction) {
            switch (direction) {
                case UP:
                    return this.up();
                case DOWN:
                    return this.down();
                case LEFT:
                    return this.left();
                case RIGHT:
                    return this.right();
                default:
                    return null;
            }
        }

        public Direction[] validMoves() {
            // An array of the valid directions in which you can
            // move from the current position (i.e., can't move UP
            // when you are on the top row).
            Direction[] d = bNotEdge; //not edge
            if (this.atLeft()) {
                d = bLeft; //leftmost column
            }
            if (this.atRight()) {
                d = bRight; //rightmost column
            }
            if (this.atTop()) {
                d = bTop; // top row
            }
            if (this.atBottom()) {
                d = bBottom; // bottom row
            }
            if (this.atTop() && this.atLeft()) {
                d = bTopLeft; //top left corner
            }
            if (this.atTop() && this.atRight()) {
                d = bTopRight; //top right corner
            }
            if (this.atBottom() && this.atLeft()) {
                d = bBottomLeft; //bottom left corner
            }
            if (this.atBottom() && this.atRight()) {
                d = bBottomRight; //bottom right corner
            }
            return d;
        }

        //returns true if the two positions are equal
        public boolean equals(Position other) {
            if (other != null) {
                if (row() == other.row() && column() == other.column()) {
                    return true;
                } else {
                    return false; //compile time error w/out this
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Position && this.equals((Position) other);
        }

        @Override
        public String toString() {
            return "(" + this.row() + "," + this.column() + ")";
        }
    }

    public Board(int size, int[] tiles) {
        //new square board with size as width and height and tiles array
        this(size, size, tiles);
    }

    public Board(int width, int height, int[] tiles) {
        //new board with specified height, width, and tiles array
        if (height < 0) {
            throw new IllegalArgumentException("Height = " + height);
        } else if (width < 0) {
            throw new IllegalArgumentException("Width = " + width);
        } else if (tiles.length != (width * height)) {
            throw new IllegalArgumentException("Length = " + tiles.length);
        } else if (!isValid(tiles)) {
            throw new IllegalArgumentException("Tiles = " + tiles);
        }
        // Make sure to make a copy of the tiles array.
        this.width = width;
        this.height = height;
        this.tiles = new int[width * height];

        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i] = tiles[i];
            if (tiles[i] == 0) {
                this.empty = new Position(i);
            }
        }
    }

    public Board(Board board) {
        //new board that is equal to the board passed in
        this(board.width, board.height, board.tiles);
    }

    public Board(Board board, Direction direction) {
        // New board with the empt square moved in the indicated direction.
        this(board);
        Position position = this.empty.move(direction);
        this.swap(this.empty, position);
        this.empty = position;
    }

    //finds the position of a certain value in a given board
    private Position find(int data) {
        for (int i = 0; i < this.tiles.length; i++) {
            if (data == this.tiles[i]) {
                return new Position(i);
            }
        }
        return null;
    }

    //returns the manhattan distance from one tile [find(data)] to the other [r,c]
    private double manhattanDistance(int data, int r, int c) {
        Position p1 = this.find(data);
        double md = 0;
        md += Math.abs((double) (p1.row() - r));
        md += Math.abs((double) (p1.column() - c));
        return md;
    }

    //evaluates how good a board is (manhattan distance from all tiles to where they should be
    public double heuristic() {
        int[] tilesC = this.tiles.clone();
        double h = 0;
        sort(tilesC);
        for (int i = 0; i < width; i++) { //rows
            for (int j = 0; j < height; j++) { //columns
                h += this.manhattanDistance(this.get(i, j), i, j);
            }
        }
        return h;
    }

    //evaluates the cost of an action
    public double cost(Board.Direction action) {
        return 1.0; //cost is constant
    }

    public int size() {
        return this.height * this.width;
    }

    public int get(Position position) {
        // The value of the tile at the specified position.
        return get(position.index());
    }

    public int get(int row, int column) {
        // The value of the tile at the specified position.
        return get(new Position(row, column).index()); //rows columns to array
    }

    private int get(int index) {
        return this.tiles[index];
    }

    //swaps two positions
    private void swap(Position a, Position b) {
        int i = a.index();
        int j = b.index();
        int temp = this.tiles[i];
        this.tiles[i] = this.tiles[j];
        this.tiles[j] = temp;
    }

    public Position empty() {
        // Returns the position of the empty square.
        //Position p = new Position((int) (this.tiles.length / height), (int) (this.tiles.length % width)); //useful later
        return this.empty;
    }

    //returns true if the board is solved
    public boolean isSolved() {
        boolean yesNo;
        for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i] != i) {
                return false;
            }
        }
        return true;
    }

    //checks validity of board
    public static boolean isValid(int[] tiles) {
        // Is this array a permuation of the numbers 0 .. tiles.length-1 ?
        //valid permutation is just making sure that all the numbers from 0-15 exist (on a 4x4)
        int[] cTiles = tiles.clone(); //copy of tiles
        sort(cTiles);
        for (int i = 0; i < tiles.length; i++) {
            if (cTiles[i] != i) {
                return false;
            }
        }
        return true;
    }

    //this method returns true if the two boards are identical
    public boolean equals(Board other) {
        if (this.width == other.width && this.height == other.height) {
            for (int i = 0; i < tiles.length; i++) {
                if (this.tiles[i] != other.tiles[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Board && this.equals((Board) other);
    }

    @Override
    public int hashCode() {
        // Yes, you do need to worry about this method !!!
        // Recall, if x.equals(y) then x.hashCode() == y.hashCode()
        long hash = 5;
        hash = 19 * hash + this.height;
        hash = 19 * hash + this.width;
        hash /= 751; //making this smaller for the next step
        for (int i = 0; i < this.tiles.length; i++) {
            hash = 7 * hash + this.tiles[i];
        }
        return (int) hash;
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = 0; row < this.height; row++) {
            if (row > 0) {
                result += rowSeparator();
            }
            String separator = "";
            for (int col = 0; col < this.width; col++) {
                int tile = this.get(row, col);
                result += separator;
                if (tile > 0) {
                    result += String.format(" %2d ", this.get(row, col));
                } else {
                    result += "    ";
                }
                separator = "|";
            }
            result += "\n"; // next line
        }
        return result;
    }

    private String rowSeparator() {
        String result = "";
        String separator = "";
        for (int col = 0; col < this.width; col++) {
            result += separator;
            result += "----";
            separator = "+";
        }
        result += "\n";
        return result;
    }

    public static void main(String[] args) {
        int width = 4;
        int height = 4;
        int size = width * height;
        int[] tiles = new int[16];
        for (int i = 0; i < size; i++) {
            try {
                tiles[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid tile: " + args[i]);
            }
        }

        Board board = new Board(width, height, tiles);
        System.out.println(board);
        for (int i = size; i < args.length; i++) {
            Direction direction;
            try {
                direction = Direction.valueOf(args[i].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid direction: " + args[i]);
                continue;
            }
            board = new Board(board, direction);
            System.out.println();
            System.out.println(board);
        }
    }
}
