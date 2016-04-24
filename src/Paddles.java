
import java.awt.event.KeyEvent;
/**
 * Handles keypress events by changing public boolean values
 * read by the Board class
 * @author Nick
 *
 */
public class Paddles {
    public boolean Left = false;
    public boolean Right = false;
    public boolean W = false;
    public boolean S = false;
    public boolean Esc = false;
    public boolean Space = false;





	/* I use boolean values to keep track of keypresses because
	 * holding down two keys simultaneously results in the latest
	 * keypress blocking out the first one. Paddle movement is also
	 * smoother this way.
	 * true = pressed, false = not pressed
	 */

    public void keyPressed(int event_code) {

        int key =event_code;
        //int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT) {
            Right = true;
        }
        if (key == KeyEvent.VK_LEFT) {
            Left = true;
        }
        if (key == KeyEvent.VK_W) {
            W = true;
        }
        if (key == KeyEvent.VK_S) {
            S = true;
        }

        if(key == KeyEvent.VK_ESCAPE){
            Esc = true;
        }

        if(key == KeyEvent.VK_SPACE){
            Space = true;
        }
    }



    public void keyReleased(int event_code) {

        int key=event_code;
        //int key = e.getKeyCode();
        if(key == KeyEvent.VK_RIGHT){
            Right = false;
        }
        if(key == KeyEvent.VK_LEFT){
            Left = false;
        }
        if(key == KeyEvent.VK_W){
            W = false;
        }
        if(key == KeyEvent.VK_S){
            S = false;
        }
        if(key == KeyEvent.VK_ESCAPE){
            Esc = false;
        }

        if(key == KeyEvent.VK_SPACE){
            Space = false;
        }

    }


}
