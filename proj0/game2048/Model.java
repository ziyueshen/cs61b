package game2048;

import java.util.Formatter;
import java.util.Observable;
import java.util.ArrayList;
import java.util.List;

/** The state of a game of 2048.
 *  @author Ziyue SHen
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // now we are in the Class Model
        // defining method within method is not allowed in Java
        ArrayList<Boolean> scoreList = new ArrayList<>();   // keep track of change, need to import
        score = forEachColumn(board, scoreList);
        for (Boolean value : scoreList) {
            if (value == true) {
                changed = true;
            }
        }
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** return the score from each move of a tile*/
    public int forOneTile(Tile t, Board board, ArrayList scorelist, ArrayList listMerge) {
        int c = t.col();
        int r = t.row() + 1;    // the nearest upper tile
        int ttl = board.size();
        int prevMerge = 100;
        if (listMerge.size() > 0) {
            prevMerge = (int) listMerge.get(listMerge.size() - 1);  //the tile last merged in this column
        }
        while (tile(c, r) == null && r < ttl - 1) {
            r += 1;
        }
        if (tile(c, r) == null) {
            board.move(c, r, t);
            scorelist.add(true);
            return 0;
        } else if (tile(c, r).value() == t.value() && r < prevMerge) {  // can't merge 2 times
            board.move(c, r, t);
            scorelist.add(true);
            listMerge.add(r);
            return  t.value() * 2; // if merged, return score
        } else {
            board.move(c, r - 1, t);
            if (r - 1 > t.row()) {
                scorelist.add(true);
            }
            return 0;
        }
    }

    /** process every tile in a column 'c', start from the 2nd row top down */
    public int forOneColumn(int c, Board board, ArrayList scorelist) {
        int res = 0; // for score
        int ttl = board.size();
        ArrayList<Integer> listMerge = new ArrayList<>();  //keep track of merged tile
        for (int i = ttl - 2; i >= 0; i--) {
            if (tile(c, i) == null) {
                continue;
            }
            Tile currentTile = tile(c, i);   // have to deal with null
            res += forOneTile(currentTile, board, scorelist, listMerge);
        }
        return res;
    }

    /** process every column */
    public int forEachColumn(Board board, ArrayList scorelist) {
        int res = 0; // for score
        int ttl = board.size();
        for  (int i = ttl - 1; i >= 0; i--) {
            res += forOneColumn(i, board, scorelist);
        }
        return res;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        int c = 0;
        while (c < b.size()) {
            int r = 0;
            while (r < b.size()) {
                if (b.tile(c, r) == null) {
                    return true;
                }
                r += 1;
            }
            c += 1;
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int c = 0;
        while (c < b.size()) {
            int r = 0;
            while (r < b.size()) {
                Tile current_tile = b.tile(c, r);
                r += 1;
                if (current_tile == null) {
                    continue;
                }
                if (current_tile.value() == MAX_PIECE) {
                    return true;
                }
            }
            c += 1;
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if (emptySpaceExists(b)) {
            return true;
        }
        // compare every tile with the right one and the upper one
        else {
            int c = 0;
            while (c < b.size()) {
                int r = 0;
                while (r < b.size()) {
                    Tile current_tile = b.tile(c, r);
                    if (c + 1 < b.size()) {  //right edge
                        Tile right_tile = b.tile(c + 1, r);
                        if (current_tile.value() == right_tile.value()) {
                            return true;
                        }
                    }
                    if (r + 1 < b.size()) {
                        Tile upper_tile = b.tile(c, r + 1);
                        if (current_tile.value() == upper_tile.value()) {
                            return true;
                        }
                    }
                    r += 1;
                }
            c += 1;
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
