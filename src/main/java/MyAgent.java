import java.util.Random;
import java.util.Arrays;

/**
 * block wins + go for wins
 * Each move has a score based on how many 2 in a rows, 3 in a rows or blocks are made
 * 2 piece blocks = 3 pts
 * 2 in a row = 2 pts
 * 3 in a row = 4 pts
 * gives opp 3 in a row = -2 pts
 * forbidden columns (throwWin + giveWin) = -5
 *
 * move in slot with highest score (MAKE SURE SEARCHING FOR HIGHEST starting from CENTER
 * break ties --> whichever piece is closer to center
 *
 * @author <tortleX3>
 *
 */
public class MyAgent extends Agent {
  /**
   * A random number generator to randomly decide where to place a token.
   */

  private Random random;
  private int mid;
  private int[] points;
  private int cntr;

  // tracks where the lowest to start strategy is

  /**
   * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
   *
   * @param game The game the agent will be playing.
   * @param iAmRed True if the agent is Red, False if the agent is Yellow.
   */

  public MyAgent(Connect4Game game, boolean iAmRed) {
    super(game, iAmRed);
    random = new Random();
    mid = game.getColumnCount()/2;
    cntr = 2;
  }

  /**
   * The move method is run every time it is this agent's turn in the game. You may assume that
   * when move() is called, the game has at least one open slot for a token, and the game has not
   * already been won.
   *
   * <p>By the end of the move method, the agent should have placed one token into the game at some
   * point.</p>
   *
   * <p>After the move() method is called, the game engine will check to make sure the move was
   * valid. A move might be invalid if:
   * - No token was place into the game.
   * - More than one token was placed into the game.
   * - A previous token was removed from the game.
   * - The color of a previous token was changed.
   * - There are empty spaces below where the token was placed.</p>
   *
   * <p>If an invalid move is made, the game engine will announce it and the game will be ended.</p>
   *
   */
  public void move() {
      Arrays.fill(points, 0);
      int win = iCanWin();
      int block = theyCanWin();
      char[][] ref = myGame.getBoardMatrix();
      points = new int[myGame.getColumnCount()];
      boolean blocked = false;


      // always go for winning move
      if (win != -1) moveOnColumn(win);

      // try to block opponent from winning
      else if (block != -1) moveOnColumn(block);

      // go for middle first 2 moves
      else if (cntr > 0) {
          moveOnColumn(mid);
          cntr--;
      }

      // scoring system!
      else {
          for (int i = 0; i < myGame.getColumnCount(); i++) {
              points[i] = horizontalScore(i, ref) + diagonalScore(i, ref) + horizontalBlock(i, ref) + diagonalBlock(i, ref);
          }
      }
  }
  // if adding piece makes a 2 in a row
  public int horizontalScore(int col, char[][] grid) {
      int r = getLowestEmptyIndex(myGame.getColumn(col));
      int track = 0;
      int c = col-3;

      while (c < col) {
          for (int i = 0; i <= 3; i++) {
              if (valid(r, c + i)) {
                  // if one of opponent's pieces is there then start on the column to the left of it
                  if (grid[r][c + i] == oppPlayer()) {
                      c += i;
                      break;
                  }
                  if (grid[r][c + i] == 'B') track++;
                  if (grid[r][c + i] == myPiece()) track += 5;
              }
              // if start column is not valid then don't count it
              else break;

              // 1 of my piece and 3 blanks
              if (track == 8) return 2;
              // 2 of my pieces and 2 blanks
              if (track == 12) return 4;
              c++;
              track = 0;
          }
      }
      return 0;
  }

  public int diagonalScore(int col, char[][] grid) {
      int r = getLowestEmptyIndex(myGame.getColumn(col));
      int track = 0;
      int c = col-3;

      while (c < col) {
          for (int i = 0; i <= 3; i++) {
              if (valid(r, c + i)) {
                  // if one of opponent's pieces is there then start on the column to the left of it
                  if (grid[r][c + i] == oppPlayer()) {
                      c += i;
                      break;
                  }
                  if (grid[r][c + i] == 'B') track++;
                  if (grid[r][c + i] == myPiece()) track += 5;
              }
              // if start column is not valid then don't count it
              else break;

              // 1 of my piece and 3 blanks
              if (track == 8) return 2;
              // 2 of my pieces and 2 blanks
              if (track == 12) return 4;
              c++;
              track = 0;
          }
      }
      return 0;
  }

  /**
   * Drops a token into a particular column so that it will fall to the bottom of the column.
   * If the column is already full, nothing will change.
   *
   * @param columnNumber The column into which to drop the token.
   */
  public void moveOnColumn(int columnNumber) {
    // Find the top empty slot in the column
    // If the column is full, lowestEmptySlot will be -1
    int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));
    // if the column is not full
    if (lowestEmptySlotIndex > -1) {
      // get the slot in this column at this index
      Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);
      // If the current agent is the Red player...
      if (iAmRed) {
        lowestEmptySlot.addRed(); // Place a red token into the empty slot
      } else {
        lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
      }
    }
  }

  public char myPiece() {
      if(iAmRed) return 'R';
      return 'Y';
  }

  public char oppPlayer() {
      if (iAmRed) return 'Y';
      return 'R';
  }

  /**
   * Returns the index of the top empty slot in a particular column.
   *
   * @param column The column to check.
   * @return
   *      the index of the top empty slot in a particular column;
   *      -1 if the column is already full.
   */
  public int getLowestEmptyIndex(Connect4Column column) {
    int lowestEmptySlot = -1;
    for  (int i = 0; i < column.getRowCount(); i++) {
      if (!column.getSlot(i).getIsFilled()) {
        lowestEmptySlot = i;
      }
    }
    return lowestEmptySlot;
  }

  /**
   * Returns a random valid move. If your agent doesn't know what to do, making a random move
   * can allow the game to go on anyway.
   *
   * @return a random valid move.
   */
  public int randomMove() {
    int i = random.nextInt(myGame.getColumnCount());
    while (getLowestEmptyIndex(myGame.getColumn(i)) == -1) {
      i = random.nextInt(myGame.getColumnCount());
    }
    return i;
  }

  /**
   * Returns the column that would allow the agent to win.
   *
   * <p>You might want your agent to check to see if it has a winning move available to it so that
   * it can go ahead and make that move. Implement this method to return what column would
   * allow the agent to win.</p>
   *
   * @return the column that would allow the agent to win.
   */

  /*
Board indexes
  0
  1
  2
  3
  4
  5
    0 1 2 3 4 5 6

  */
  public int iCanWin() {
      Connect4Game a = new Connect4Game(myGame);
      for (int i = 0; i < myGame.getColumnCount(); i++) {
          a = new Connect4Game(myGame);
          moveOnColumn(i, a);
          if (!iAmRed && a.gameWon() == 'Y') {
              return i;
          }
          if (iAmRed && a.gameWon() == 'R') {
              return i;
          }
      }
      return -1;
  }

  // check if column and row are valid indexes
  public static boolean valid(int r, int c) {
      if (r < 0 || r >= myGame.getRowCount() || c < 0 || c >= myGame.getColumnCount()) return false;
      return true;
  }



  /**
   * Returns the column that would allow the opponent to win.
   *
   * <p>You might want your agent to check to see if the opponent would have any winning moves
   * available so your agent can block them. Implement this method to return what column should
   * be blocked to prevent the opponent from winning.</p>
   *
   * @return the column that would allow the opponent to win.
   */
  public int theyCanWin() {
      Connect4Game a = new Connect4Game(myGame);
      for (int i = 0; i < myGame.getColumnCount(); i++) {
          a = new Connect4Game(myGame);
          moveOnColumnOpp(i, a);
          if (!iAmRed && a.gameWon() == 'R') {
              return i;
          }
          if (iAmRed && a.gameWon() == 'Y') {
              return i;
          }
      }
      return -1;
  }

  public boolean giveWin(int col) {
      Connect4Game b = new Connect4Game(myGame);
      moveOnColumn(col, b);
      moveOnColumnOpp(col, b);
      if ((b.gameWon() == 'Y' && iAmRed) || (b.gameWon() == 'R' && !iAmRed)) {
          return true;
      }
      return false;
  }

  public int throwWin(int col) {
      Connect4Game g = new Connect4Game(myGame);

      // if I force them to block but doesn't benefit me
      moveOnColumn(col, g);
      moveOnColumn(col, g);
      if((iAmRed && g.gameWon() == 'R') || (!iAmRed && g.gameWon() == 'Y')) return -5;

      return 0;
}

  /**
   * Returns the name of this agent.
   *
   * @return the agent's name
   */
  public String getName() {
    return "My Agent";
  }

    public void moveOnColumn(int columnNumber, Connect4Game game) {
        // Find the top empty slot in the column
        // If the column is full, lowestEmptySlot will be -1
        int lowestEmptySlotIndex = getLowestEmptyIndex(game.getColumn(columnNumber));
        // if the column is not full
        if (lowestEmptySlotIndex > -1) {
            // get the slot in this column at this index
            Connect4Slot lowestEmptySlot = game.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);
            // If the current agent is the Red player...
            if (iAmRed) {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            } else {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    public void moveOnColumnOpp(int columnNumber, Connect4Game game) {
        // Find the top empty slot in the column
        // If the column is full, lowestEmptySlot will be -1
        int lowestEmptySlotIndex = getLowestEmptyIndex(game.getColumn(columnNumber));
        // if the column is not full
        if (lowestEmptySlotIndex > -1) {
            // get the slot in this column at this index
            Connect4Slot lowestEmptySlot = game.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);
            // If the current agent is the Red player...
            if (iAmRed) {
                lowestEmptySlot.addYellow(); // Place a red token into the empty slot
            } else {
                lowestEmptySlot.addRed(); // Place a yellow token into the empty slot
            }
        }
    }


}

