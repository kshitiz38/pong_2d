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

        setSize(board.WINDOW_X, board.WINDOW_Y+23);

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

        setSize(board.WINDOW_X, board.WINDOW_Y+23);

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