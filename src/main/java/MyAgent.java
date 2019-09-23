import java.util.Random;
import java.util.ArrayList;
/**
 * Describe your basic strategy here.
 * @author <your Github username>
 *
 */
public class MyAgent extends Agent {
  /**
   * A random number generator to randomly decide where to place a token.
   */

  private Random random;
  private int mid;
  private ArrayList<Integer> give;
  private char[][] grid;

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
      int win = iCanWin();
      int block = theyCanWin();
      grid = myGame.getBoardMatrix();

      // always go for winning move
      if (win != -1) moveOnColumn(win);

      // try to block opponent from winning
      else if (block != -1) moveOnColumn(block);

      // go for middle
      else if (!myGame.getColumn(mid).getIsFull()) {
        moveOnColumn(mid);
      }
//      else if (!myGame.getRedPlayedFirst()) {
//          if (grid[0][mid - 1] == oppPiece()) moveOnColumn(mid + 1);
//          else if (grid[0][mid + 1] == oppPiece()) moveOnColumn(mid - 1);
//      }
      else {
          int rando = randomMove();
          int track = 0;
          boolean force = false;
          if(giveWin(rando)) {
              for (int i = 0; i < myGame.getColumnCount(); i++) {
                  if(myGame.getColumn(i).getIsFull()) track++;
              }
              if(track == myGame.getColumnCount()-1) {
                  force = true;
                  moveOnColumn(rando);
              }
              else while(!giveWin(rando)) rando = randomMove();
          }
          if (force == false) moveOnColumn(rando);
      }
  }
  public char oppPiece() {
      if(iAmRed) return 'Y';
      return 'R';
  }

  public char myPiece() {
      if(iAmRed) return 'R';
      return 'Y';
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
          if (a.gameWon() == oppPiece()) {
              return i;
          }
      }
      return -1;
  }

  public boolean giveWin(int col) {
      Connect4Game b = new Connect4Game(myGame);
      moveOnColumn(col, b);
      for(int i = 0; i < myGame.getColumnCount(); i++) {
          moveOnColumnOpp(i, b);
          if (b.gameWon() == oppPiece()) {
              return true;
          }
      }


      return false;
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

