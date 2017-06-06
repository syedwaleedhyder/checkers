
package checkerss;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;



 //Red always starts the game. The application just opens a window that uses an object of type Checkers as its content pane.

public class Checkers extends JPanel {

    private JButton buttonReset;
    private JButton buttonLeave;
    private JLabel labelMessage;
    private JLabel labelDevelopedBy;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Checkers Game");
        Checkers checkers = new Checkers();
        frame.setContentPane(checkers);
        frame.pack();
        frame.setLocation(20, 20);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * The constructor Checkers --> Board --> buttons and label
     * adds and sets bounds of all the components (layout set manually)
     */
    public Checkers() {

        setLayout(null);
        setPreferredSize(new Dimension(500, 500));

        setBackground(new Color(50, 50, 50));  // Dark gray screen

        Board board = new Board();
        add(board);
        add(buttonReset);
        add(buttonLeave);
        add(labelMessage);
        add(labelDevelopedBy);

        board.setBounds(20, 20, 324, 324);
        buttonReset.setBounds(360, 120, 120, 30);
        buttonLeave.setBounds(360, 180, 120, 30);
        labelMessage.setBounds(0, 400, 350, 30);
        labelDevelopedBy.setBounds(10,480,450,30);

    } // end constructor

    public static class Applet extends JApplet {  //Size 500*500
        public void init() {
            setContentPane(new Checkers());
        }
    }

    // --------------------  Nested Classes -------------------------------

    /**
     * holds row/column of piece to be move AND square to which it is to be moved.
     */
    private static class CheckersMove {
        int fromRow, fromCol;  // Position of piece to be moved.
        int toRow, toCol;      // Square it is to move to.

        CheckersMove(int r1, int c1, int r2, int c2) { //constructor
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }

        boolean isJump() {
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }
    }  // end class CheckersMove.


    private class Board extends JPanel implements ActionListener, MouseListener {


        CheckersData board;  // data of checkers board. generates list of legal moves

        boolean gameInProgress; // used in reset game..

        int currentPlayer;      //  CheckersData.RED / CheckersData.BLACK.

        int selectedRow, selectedCol;

        CheckersMove[] legalMoves;


        /**
         *Create the board and
         * start the first game.
         */
        Board() { // constructor: buttons, labels, listener of mouse click, button click.
            setBackground(Color.BLACK);
            addMouseListener(this);
            buttonLeave = new JButton("Leave");
            buttonLeave.addActionListener(this);
            buttonReset = new JButton("Reset");
            buttonReset.addActionListener(this);
            labelMessage = new JLabel("", JLabel.CENTER);
            labelMessage.setFont(new Font("Serif", Font.BOLD, 14));
            labelMessage.setForeground(Color.green);
            labelDevelopedBy = new JLabel("Developed by: Hamna Moeeiz, Urvah Saikhani, Syed Waleed Hyder ", JLabel.LEFT);
            labelDevelopedBy.setFont(new Font("Serif", Font.BOLD, 14));
            labelDevelopedBy.setForeground(Color.cyan);
            board = new CheckersData();
            doReset();
        }


        /**
         * Respond to user's click on one of the two buttons.
         */
        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == buttonReset)
                doReset();
            else if (src == buttonLeave)
                doResign();
        }

        /**
         * Reset the game and initializes the pieces
         */
        void doReset() {
            if (gameInProgress) {
                labelMessage.setText("Finish the current game first!");
                return;
            }
            board.setUpGame();   // Set up the pieces.
            currentPlayer = CheckersData.RED;   // RED moves first.
            legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
            selectedRow = -1;   // RED has not yet selected a piece to move.
            labelMessage.setText("Red:  Make your move.");
            gameInProgress = true;
            buttonReset.setEnabled(true);
            buttonLeave.setEnabled(true);
            repaint();
        }

        void doResign() {
            if (gameInProgress == false) {
                labelMessage.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersData.RED)
                gameOver("RED resigns.  BLACK wins.");
            else
                gameOver("BLACK resigns.  RED wins.");
        }


        /**
         * The game ends.  The parameter, str, is displayed as a labelMessage
         * to the user.  The states of the buttons are adjusted so playes
         * can start a new game.  This method is called when the game
         * ends at any point in this class.
         */
        void gameOver(String str) {
            labelMessage.setText(str);
            buttonReset.setEnabled(true);
            buttonLeave.setEnabled(false);
            gameInProgress = false;
        }


        /**
         * This is called by mousePressed() when a player clicks on the
         * square in the specified row and col.  It has already been checked
         * that a game is, in fact, in progress.
         */
        void doClickSquare(int row, int col) {

         /* If the player clicked on one of the pieces that the player
          can move, mark this row and col as selected and return.  (This
          might change a previous selection.)  Reset the labelMessage, in
          case it was previously displaying an error labelMessage. */

            for (int i = 0; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED)
                        labelMessage.setText("RED:  Make your move.");
                    else
                        labelMessage.setText("BLACK:  Make your move.");
                    repaint();
                    return;
                }

         /* If no piece has been selected to be moved, the user must first
          select a piece.  Show an error labelMessage and return. */

            if (selectedRow < 0) {
                labelMessage.setText("Click the piece you want to move.");
                return;
            }

         /* If the user clicked on a squre where the selected piece can be
          legally moved, then make the move and return. */

            for (int i = 0; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                        && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                    doMakeMove(legalMoves[i]);
                    return;
                }

         /* If we get to this point, there is a piece selected, and the square where
          the user just clicked is not one where that piece can be legally moved.
          Show an error labelMessage. */

            labelMessage.setText("Click the square you want to move to.");

        }  // end doClickSquare()


        /**
         * This is called when the current player has chosen the specified
         * move.  Make the move, and then either end or continue the game
         * appropriately.
         */
        void doMakeMove(CheckersMove move) {

            board.makeMove(move);

         /* If the move was a jump, it's possible that the player has another
          jump.  Check for legal jumps starting from the square that the player
          just moved to.  If there are any, the player must jump.  The same
          player continues moving.
          */

            if (move.isJump()) {
                legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol);
                if (legalMoves != null) {
                    if (currentPlayer == CheckersData.RED)
                        labelMessage.setText("RED:  You must continue jumping.");
                    else
                        labelMessage.setText("BLACK:  You must continue jumping.");
                    selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                    selectedCol = move.toCol;
                    repaint();
                    return;
                }
            }

         /* The current player's turn is ended, so change to the other player.
          Get that player's legal moves.  If the player has no legal moves,
          then the game ends. */

            if (currentPlayer == CheckersData.RED) {
                currentPlayer = CheckersData.BLACK;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("BLACK has no moves.  RED wins.");
                else if (legalMoves[0].isJump())
                    labelMessage.setText("BLACK:  Make your move.  You must jump.");
                else
                    labelMessage.setText("BLACK:  Make your move.");
            } else {
                currentPlayer = CheckersData.RED;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("RED has no moves.  BLACK wins.");
                else if (legalMoves[0].isJump())
                    labelMessage.setText("RED:  Make your move.  You must jump.");
                else
                    labelMessage.setText("RED:  Make your move.");
            }

         /* Set selectedRow = -1 to record that the player has not yet selected
          a piece to move. */

            selectedRow = -1;

         /* As a courtesy to the user, if all legal moves use the same piece, then
          select that piece automatically so the use won't have to click on it
          to select it. */

            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow
                            || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                        sameStartSquare = false;
                        break;
                    }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                }
            }

         /* Make sure the board is redrawn in its new state. */

            repaint();

        }  // end doMakeMove();


        /**
         * Draw  checkerboard pattern in gray and lightGray.  Draw the
         * checkers.  If a game is in progress, highlight the legal moves.
         */
        public void paintComponent(Graphics g) {

         /* Draw a two-pixel black border around the edges of the canvas. */

            g.setColor(Color.black);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);

         /* Draw the squares of the checkerboard and the checkers. */

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2)
                        g.setColor(Color.LIGHT_GRAY);
                    else
                        g.setColor(Color.BLACK);
                    g.fillRect(2 + col * 40, 2 + row * 40, 40, 40);
                    switch (board.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.setColor(Color.RED);
                            g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
                            break;
                        case CheckersData.BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
                            break;
                        case CheckersData.RED_KING:
                            g.setColor(Color.RED);
                            g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 18 + col * 40, 25 + row * 40);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setColor(Color.BLACK);
                            g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 18 + col * 40, 25 + row * 40);
                            break;
                    }
                }
            }

         /* If a game is in progress, hilite the legal moves.   Note that legalMoves
          is never null while a game is in progress. */

            if (gameInProgress) {
               /* First, draw a 2-pixel cyan border around the pieces that can be moved. */
                g.setColor(Color.cyan);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.drawRect(2 + legalMoves[i].fromCol * 40, 2 + legalMoves[i].fromRow * 40, 39, 39);
                    g.drawRect(3 + legalMoves[i].fromCol * 40, 3 + legalMoves[i].fromRow * 40, 37, 37);
                }
               /* If a piece is selected for moving (i.e. if selectedRow >= 0), then
                draw a 2-pixel white border around that piece and draw green borders
                around each square that that piece can be moved to. */
                if (selectedRow >= 0) {
                    g.setColor(Color.white);
                    g.drawRect(2 + selectedCol * 40, 2 + selectedRow * 40, 39, 39);
                    g.drawRect(3 + selectedCol * 40, 3 + selectedRow * 40, 37, 37);
                    g.setColor(Color.green);
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                            g.drawRect(2 + legalMoves[i].toCol * 40, 2 + legalMoves[i].toRow * 40, 39, 39);
                            g.drawRect(3 + legalMoves[i].toCol * 40, 3 + legalMoves[i].toRow * 40, 37, 37);
                        }
                    }
                }
            }

        }  // end paintComponent()


        /**
         * Respond to a user click on the board.  If no game is in progress, show
         * an error labelMessage.  Otherwise, find the row and column that the user
         * clicked and call doClickSquare() to handle it.
         */
        public void mousePressed(MouseEvent evt) {
            if (gameInProgress == false)
                labelMessage.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (evt.getX() - 2) / 40;
                int row = (evt.getY() - 2) / 40;
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    doClickSquare(row, col);
            }
        }


        public void mouseReleased(MouseEvent evt) {
        }

        public void mouseClicked(MouseEvent evt) {
        }

        public void mouseEntered(MouseEvent evt) {
        }

        public void mouseExited(MouseEvent evt) {
        }


    }  // end class Board


    /**
     * An object of this class holds data about a game of checkers.
     * It knows what kind of piece is on each square of the checkerboard.
     * Note that RED moves "up" the board (i.e. row number decreases)
     * while BLACK moves "down" the board (i.e. row number increases).
     * Methods are provided to return lists of available legal moves.
     */
    private static class CheckersData {

        static final int
                EMPTY = 0,
                RED = 1,
                RED_KING = 2,
                BLACK = 3,
                BLACK_KING = 4;


        int[][] board;  // contents of row r, column c.

        CheckersData() {
            board = new int[8][8];
            setUpGame();
        }

        //row % 2 == col % 2
        void setUpGame() {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        if (row < 3)
                            board[row][col] = BLACK;
                        else if (row > 4)
                            board[row][col] = RED;
                        else
                            board[row][col] = EMPTY;
                    } else {
                        board[row][col] = EMPTY;
                    }
                }
            }
        }  //setUpGame()


        /**
         * Return the contents of the square in the specified row and column.
         */
        int pieceAt(int row, int col) {
            return board[row][col];
        }

        /**
         * Set the contents of the square in the specified row and column.
         * piece must be one of the constants EMPTY, RED, BLACK, RED_KING,
         * BLACK_KING.
         */
        void setPieceAt(int row, int col, int piece) {
            board[row][col] = piece;
        }


        /**
         * Make the specified move.  It is assumed that move
         * is non-null and that the move it represents is legal.
         */
        void makeMove(CheckersMove move) {
            makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }


        /**
         * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
         * assumed that this move is legal.  If the move is a jump, the
         * jumped piece is removed from the board.  If a piece moves
         * the last row on the opponent's side of the board, the
         * piece becomes a king.
         */
        void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = EMPTY;
            if (fromRow - toRow == 2 || fromRow - toRow == -2) {
                // The move is a jump.  Remove the jumped piece from the board.
                int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
                int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
                board[jumpRow][jumpCol] = EMPTY;
            }
            if (toRow == 0 && board[toRow][toCol] == RED)
                board[toRow][toCol] = RED_KING;
            if (toRow == 7 && board[toRow][toCol] == BLACK)
                board[toRow][toCol] = BLACK_KING;
        }

        /**
         * Return an array containing all the legal CheckersMoves
         * for the specfied player on the current board.  If the player
         * has no legal moves, null is returned.  The value of player
         * should be one of the constants RED or BLACK; if not, null
         * is returned.  If the returned value is non-null, it consists
         * entirely of jump moves or entirely of regular moves, since
         * if the player can jump, only jumps are legal moves.
         */
        CheckersMove[] getLegalMoves(int player) {

            if (player != RED && player != BLACK)
                return null;

            int playerKing;  // The constant representing a King belonging to player.
            if (player == RED)
                playerKing = RED_KING;
            else
                playerKing = BLACK_KING;

            ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();  // Moves will be stored in this list.

         /*  First, check for any possible jumps.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          jump in each of the four directions from that square.  If there is
          a legal jump in that direction, put it in the moves ArrayList.
          */

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2))
                            moves.add(new CheckersMove(row, col, row + 2, col + 2));
                        if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2))
                            moves.add(new CheckersMove(row, col, row - 2, col + 2));
                        if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2))
                            moves.add(new CheckersMove(row, col, row + 2, col - 2));
                        if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2))
                            moves.add(new CheckersMove(row, col, row - 2, col - 2));
                    }
                }
            }

         /*  If any jump moves were found, then the user must jump, so we don't
          add any regular moves.  However, if no jumps were found, check for
          any legal regualar moves.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          move in each of the four directions from that square.  If there is
          a legal move in that direction, put it in the moves ArrayList.
          */

            if (moves.size() == 0) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (board[row][col] == player || board[row][col] == playerKing) {
                            if (canMove(player, row, col, row + 1, col + 1))
                                moves.add(new CheckersMove(row, col, row + 1, col + 1));
                            if (canMove(player, row, col, row - 1, col + 1))
                                moves.add(new CheckersMove(row, col, row - 1, col + 1));
                            if (canMove(player, row, col, row + 1, col - 1))
                                moves.add(new CheckersMove(row, col, row + 1, col - 1));
                            if (canMove(player, row, col, row - 1, col - 1))
                                moves.add(new CheckersMove(row, col, row - 1, col - 1));
                        }
                    }
                }
            }

         /* If no legal moves have been found, return null.  Otherwise, create
          an array just big enough to hold all the legal moves, copy the
          legal moves from the ArrayList into the array, and return the array. */

            if (moves.size() == 0)
                return null;
            else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++)
                    moveArray[i] = moves.get(i);
                return moveArray;
            }

        }  // end getLegalMoves


        /**
         * Return a list of the legal jumps that the specified player can
         * make starting from the specified row and column.  If no such
         * jumps are possible, null is returned.  The logic is similar
         * to the logic of the getLegalMoves() method.
         */
        CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
            if (player != RED && player != BLACK)
                return null;
            int playerKing;  // The constant representing a King belonging to player.
            if (player == RED)
                playerKing = RED_KING;
            else
                playerKing = BLACK_KING;
            ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();  // The legal jumps will be stored in this list.
            if (board[row][col] == player || board[row][col] == playerKing) {
                if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2))
                    moves.add(new CheckersMove(row, col, row + 2, col + 2));
                if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2))
                    moves.add(new CheckersMove(row, col, row - 2, col + 2));
                if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2))
                    moves.add(new CheckersMove(row, col, row + 2, col - 2));
                if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2))
                    moves.add(new CheckersMove(row, col, row - 2, col - 2));
            }
            if (moves.size() == 0)
                return null;
            else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++)
                    moveArray[i] = moves.get(i);
                return moveArray;
            }
        }  // end getLegalMovesFrom()


        /**
         * This is called by the two previous methods to check whether the
         * player can legally jump from (r1,c1) to (r3,c3).  It is assumed
         * that the player has a piece at (r1,c1), that (r3,c3) is a position
         * that is 2 rows and 2 columns distant from (r1,c1) and that
         * (r2,c2) is the square between (r1,c1) and (r3,c3).
         */
        private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

            if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
                return false;  // (r3,c3) is off the board.

            if (board[r3][c3] != EMPTY)
                return false;  // (r3,c3) already contains a piece.

            if (player == RED) {
                if (board[r1][c1] == RED && r3 > r1)
                    return false;  // Regular red piece can only move  up.
                if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING)
                    return false;  // There is no black piece to jump.
                return true;  // The jump is legal.
            } else {
                if (board[r1][c1] == BLACK && r3 < r1)
                    return false;  // Regular black piece can only move downn.
                if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
                    return false;  // There is no red piece to jump.
                return true;  // The jump is legal.
            }

        }  // end canJump()


        /**
         * This is called by the getLegalMoves() method to determine whether
         * the player can legally move from (r1,c1) to (r2,c2).  It is
         * assumed that (r1,r2) contains one of the player's pieces and
         * that (r2,c2) is a neighboring square.
         */
        private boolean canMove(int player, int r1, int c1, int r2, int c2) {

            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
                return false;  // (r2,c2) is off the board.

            if (board[r2][c2] != EMPTY)
                return false;  // (r2,c2) already contains a piece.

            if (player == RED) {
                if (board[r1][c1] == RED && r2 > r1)
                    return false;  // Regualr red piece can only move down.
                return true;  // The move is legal.
            } else {
                if (board[r1][c1] == BLACK && r2 < r1)
                    return false;  // Regular black piece can only move up.
                return true;  // The move is legal.
            }

        }  // end canMove()


    } // end class CheckersData


} // end class Checkers

