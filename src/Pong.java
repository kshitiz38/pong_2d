/*
 * Pong for the Java 2D API
 * Written by Nick Carneiro.
 * http://trillworks.com/nick
 *
 * 2011 No rights reserved.
 * No attribution required.
 *
 *
 */

import javax.swing.JFrame;
import java.awt.*;

/*
Pong class provides user with screen to play the game and brings up the game arena.
 */
public class Pong extends JFrame {

    //suppresses warning
    private static final long serialVersionUID = 1L;
    UDP udp;
    //creates the window and instantiates the game board
    public Pong(UDP udp) {


        this.udp = udp;
        Board board = new Board("Multiplayer", udp, "Hard", 1);
        add(board);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        if (width<=1400) setSize(board.WINDOW_X, board.WINDOW_Y+23);
        else setSize(board.WINDOW_X, board.WINDOW_Y+29);

        setLocationRelativeTo(null);
        setTitle("Pong Game 2D");

        setResizable(false);
        setVisible(true);

        //tell the board to update it's game state variables
        //to account for the setSize parameters above.
        board.Update_Dimensions();

    }

    public Pong(String gameMode, String difficulty, int balls) {

        Board board = new Board(gameMode, udp, difficulty, balls);
        add(board);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        if (width<=1400) setSize(board.WINDOW_X, board.WINDOW_Y+23);
        else setSize(board.WINDOW_X, board.WINDOW_Y+29);

        setLocationRelativeTo(null);
        setTitle("Pong Game 2D");

        setResizable(false);
        setVisible(true);

        //tell the board to update it's game state variables
        //to account for the setSize parameters above.
        board.Update_Dimensions();
    }

//    public Pong(UDP udp) {
//
//    }

//    public static void main(String[] args) {
//
//        new Pong();
//    }
}