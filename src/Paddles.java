
import java.awt.event.KeyEvent;
/**
 * Handles keypress events by changing public boolean values
 * read by the Board class
 * @author Nick
 *
 */
/*
Paddles class is for the instantiation of a single paddle used in the game and includes variou key listeners on it.
 */
public class Paddles {
    public int playerIndex;
    public boolean Left = false;
    public boolean Right = false;
    public boolean Up = false;
    public boolean Down = false;
    public boolean W = false;
    public boolean S = false;
    public boolean A = false;
    public boolean D = false;
    public boolean Esc = false;
    public boolean Space = false;





	/* I use boolean values to keep track of keypresses because
	 * holding down two keys simultaneously results in the latest
	 * keypress blocking out the first one. Paddle movement is also
	 * smoother this way.
	 * true = pressed, false = not pressed
	 */

    public void keyPressed(int event_code) {

        int key = event_code;
        //int key = e.getKeyCode();

        this.playerIndex = playerIndex;

        if (key == KeyEvent.VK_RIGHT) {
            Right = true;
        }
        if (key == KeyEvent.VK_LEFT) {
            Left = true;
        }
        if (key == KeyEvent.VK_UP) {
            Up = true;
        }
        if (key == KeyEvent.VK_DOWN) {
            Down = true;
        }
        if (key == KeyEvent.VK_W) {
            W = true;
        }
        if (key == KeyEvent.VK_S) {
            S = true;
        }
        if (key == KeyEvent.VK_A) {
            A = true;
        }
        if (key == KeyEvent.VK_D) {
            D = true;
        }

        if(key == KeyEvent.VK_ESCAPE){
            Esc = true;
        }

        if(key == KeyEvent.VK_SPACE){
            Space = true;
        }
    }



    public void keyReleased(int event_code) {

        this.playerIndex = playerIndex;

        int key=event_code;
        //int key = e.getKeyCode();
        if(key == KeyEvent.VK_RIGHT){
            Right = false;
        }
        if(key == KeyEvent.VK_LEFT){
            Left = false;
        }
        if (key == KeyEvent.VK_UP) {
            Up = true;
        }
        if (key == KeyEvent.VK_DOWN) {
            Down = true;
        }
        if(key == KeyEvent.VK_W){
            W = false;
        }
        if(key == KeyEvent.VK_S){
            S = false;
        }
        if (key == KeyEvent.VK_A) {
            A = true;
        }
        if (key == KeyEvent.VK_D) {
            D = true;
        }
        if(key == KeyEvent.VK_ESCAPE){
            Esc = false;
        }

        if(key == KeyEvent.VK_SPACE){
            Space = false;
        }

    }


}
