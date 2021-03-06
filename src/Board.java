

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Random;

/*
Board class is the main class for playing the game
and all the game updates from score updation to repainting of ball takes place in this class.
This class is used for the board of the game.
 */
public class Board extends JPanel implements ActionListener {


    private static final long serialVersionUID = 1L; //suppresses warning

    //Game constants
    public int threadtimeout = 3;
    public int frametimer = 5;
    public int WINDOW_X = 650;
    public int WINDOW_Y = 650;
    private int MAX_SCORE = 5;

    private int BALL_HEIGHT = 12;
    private int BALL_WIDTH = 12;
    private int PADDLE_WIDTH = 8;
    private int PADDLE_HEIGHT = 60;
    private double PADDLE_SPEED = 2;
    private double PADDLE_SPEED_ORIGINAL = 2;
    private double PADDLE_SPEED_AI = 2;

    private String message = "";

    private int player_1_score = 0;
    private int player_2_score = 0;
    private int player_3_score = 0;
    private int player_4_score = 0;
    private int player_1_3_score = 0;
    private int player_2_4_score = 0;

    private int pane_x; //the playable game area is smaller than the window
    private int pane_y;

    private boolean pause = true; //start paused for instruction screen

    private Rectangle2D paddleOne_one;
    private Rectangle2D paddleTwo_one;
    private Rectangle2D paddleOne_two;
    private Rectangle2D paddleTwo_two;

    private Timer timer;

    private double paddleOneY;
    private double paddleTwoX;
    private double paddleOneOppY;
    private double paddleTwoOppX;

    private boolean positiveYOne_one;
    private boolean positiveYOne_two;
    private boolean positiveXTwo_one;
    private boolean positiveXTwo_two;

    private boolean OneOneStop = true;
    private boolean OneTwoStop = true;
    private boolean TwoOneStop = true;
    private boolean TwoTwoStop = true;

    private int OneOnePlaying = 1;
    private int OneTwoPlaying = 1;
    private int TwoOnePlaying = 1;
    private int TwoTwoPlaying = 1;

    private String gameMode;
    private String difficulty;
    private int numberOfBalls;
    private String winMsg;

    private Ball ball1;
    private Ball ball2;

    private PhysicsCollision physics;

    private UDP UDPObject;

    private int playerIndex;

    //special keys
    public boolean Esc = false;
    public boolean Space = false;

    private ArrayList<Machine> playersList;

    //hand off key presses to the Paddles class.
    private Paddles paddle0;
    private Paddles paddle1;
    private Paddles paddle2;
    private Paddles paddle3;

    private Paddles paddle0a;
    private Paddles paddle1a;
    private Paddles paddle2a;
    private Paddles paddle3a;

    private boolean flag = false;

    private int numberOfPlayers;
    private int iterationPower;
    private Ball ball3;
    private int timerSpecialPower = 0;
    private boolean powerUsed = false;


    public AttributedString getAttributeString(String string) {
        Font font = new Font("Arial", Font.PLAIN, 40);
        AttributedString as1 = new AttributedString((string));
        as1.addAttribute(TextAttribute.FONT, font, 0, 1);
        as1.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, 1);
        return as1;
    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Space = false;
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                Esc = false;
            } else {
                if (!gameMode.equals("Multiplayer")) {
                    paddle0.keyReleased(e.getKeyCode());
                    paddle1.keyReleased(e.getKeyCode());
                    paddle2.keyReleased(e.getKeyCode());
                    paddle3.keyReleased(e.getKeyCode());
                } else {
                    if (playerIndex == 0) {
                        paddle0.keyReleased(e.getKeyCode());
                    }
                    if (playerIndex == 1) {
                        paddle1.keyReleased(e.getKeyCode());
                    }
                    if (playerIndex == 2) {
                        paddle2.keyReleased(e.getKeyCode());
                    }
                    if (playerIndex == 3) {
                        paddle3.keyReleased(e.getKeyCode());
                    }
                }
            }
            if (UDPObject != null)
                UDPObject.sendKeyEvent(e.getKeyCode(), "Released", playerIndex);
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Space = true;
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                Esc = true;
            } else {
                if (!gameMode.equals("Multiplayer")) {
                    paddle0.keyPressed(e.getKeyCode());
                    paddle1.keyPressed(e.getKeyCode());
                    paddle2.keyPressed(e.getKeyCode());
                    paddle3.keyPressed(e.getKeyCode());
                } else {
                    if (playerIndex == 0) {
                        paddle0.keyPressed(e.getKeyCode());
                    }
                    if (playerIndex == 1) {
                        paddle1.keyPressed(e.getKeyCode());
                    }
                    if (playerIndex == 2) {
                        paddle2.keyPressed(e.getKeyCode());
                    }
                    if (playerIndex == 3) {
                        paddle3.keyPressed(e.getKeyCode());
                    }
                }
            }
            if (UDPObject != null)
                UDPObject.sendKeyEvent(e.getKeyCode(), "Pressed", playerIndex);
        }
    }

    public Board(String mode, UDP UDPObject, String difficulty, int numberOfBalls) {
        paddle0 = new Paddles();
        paddle1 = new Paddles();
        paddle2 = new Paddles();
        paddle3 = new Paddles();


        this.difficulty = difficulty;
        this.numberOfBalls = numberOfBalls;
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        this.gameMode = mode;
        this.UDPObject = UDPObject;

        if (difficulty.equals("Easy")) {
            PADDLE_SPEED_AI = 1.5;
        } else if (difficulty.equals("Medium")) {
            PADDLE_SPEED_AI = 2;
        } else {
            PADDLE_SPEED_AI = 2.5;
        }

        ball1 = new Ball(1, BALL_HEIGHT, BALL_WIDTH, true, true, "ball");

        if (gameMode.equals("Single") && this.numberOfBalls == 2) {
            ball2 = new Ball(2, BALL_HEIGHT, BALL_WIDTH, false, false, "ball");
        } else {
            ball2 = null;
        }


        if (UDPObject != null) {
            playersList = UDPObject.getPlayerlist();
            numberOfPlayers = playersList.size();
            try {
                String ipMy = InetAddress.getLocalHost().getHostAddress();

                for (int i = 0; i < playersList.size(); i++) {
                    if (playersList.get(i).getIp().equals(ipMy)) {
                        playerIndex = i;
                        break;
                    }
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            if (numberOfPlayers == 2) {
                playersList.add(2, null);
                playersList.add(3, null);
            }
            if (numberOfPlayers == 3) {
                playersList.add(3, null);
            }
        }
        if ((numberOfPlayers == 2) || (mode.equals("2Player"))) {
            paddle0a = paddle0;
            paddle2a = paddle0;
            paddle1a = paddle1;
            paddle3a = paddle1;
        }
        if (numberOfPlayers == 3) {
            paddle0a = paddle0;
            paddle1a = paddle1;
            paddle2a = paddle2;

        }
        if (numberOfPlayers == 4) {
            paddle0a = paddle0;
            paddle1a = paddle1;
            paddle2a = paddle2;
            paddle3a = paddle3;

        }
        System.out.println("MyIndex : " + playerIndex);

//        if((numberOfPlayers==2) || (mode.equals("2Player"))){
//            paddle0a = paddle0;
//            paddle2a = paddle0;
//            paddle1a = paddle1;
//            paddle3a = paddle1;
//        }
//        if(numberOfPlayers==3){
//            paddle0a = paddle0;
//            paddle1a = paddle1;
//            paddle2a = paddle0;
//            paddle3a = paddle3;
//
//        }

        System.out.println("MyIndex " + playerIndex);
//
//        if((numberOfPlayers==2) || (mode.equals("2Player"))){
//            paddle0a = paddle0;
//            paddle2a = paddle0;
//            paddle1a = paddle1;
//            paddle3a = paddle1;
//        }
//        if(numberOfPlayers==3){
//            paddle0a = paddle0;
//            paddle1a = paddle1;
//            paddle2a = paddle0;
//            paddle3a = paddle3;
//
//        }

        //create polygons used in the game
//        paddleOne_one = new Rectangle(0,0,PADDLE_WIDTH, PADDLE_HEIGHT);
//        paddleOne_two = new Rectangle(0,0,PADDLE_WIDTH, PADDLE_HEIGHT);
//		paddleTwo_one = new Rectangle(0,0,PADDLE_HEIGHT, PADDLE_WIDTH);
//		paddleTwo_two = new Rectangle(0,0,PADDLE_HEIGHT, PADDLE_WIDTH);

        setDoubleBuffered(true);
        timer = new Timer(frametimer, this);
        timer.start();

    }

    public void Update_Dimensions() {

        pane_y = this.getHeight();
        pane_x = this.getWidth();

        resetBall("Begin Game");

        paddleOneY = pane_y / 2 - PADDLE_HEIGHT / 2;
        paddleOneOppY = pane_y / 2 - PADDLE_HEIGHT / 2;
        paddleTwoX = pane_x / 2 - PADDLE_HEIGHT / 2;
        paddleTwoOppX = pane_x / 2 - PADDLE_HEIGHT / 2;
    }

    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Arial", Font.PLAIN, 40);

        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        if (message.length() > 0) {

            g2d.drawString(message, pane_x / 2 - (60 + message.length() * 4), 120);
            if (pause == true) {
                g2d.drawString("Press SPACE to Start", pane_x / 2 - (140 + message.length() * 4), 220);

                g2d.drawString("Your controls are", pane_x / 2 - (115 + message.length() * 4), 220 + 220);
                if ((gameMode.equals("Single"))) {
                    g2d.drawString("Left-Right", pane_x / 2 - (60 + message.length() * 4), 220 + 270);
                } else if (gameMode.equals("2Player")) {
                    g2d.drawString("W/S-Left/Right", pane_x / 2 - (90 + message.length() * 4), 220 + 270);
                } else {
                    if ((playerIndex == 0) || (playerIndex == 2)) {
                        g2d.drawString("Left-Right", pane_x / 2 - (60 + message.length() * 4), 220 + 270);
                    } else if ((playerIndex == 1) || (playerIndex == 3)) {
                        g2d.drawString("W/S", pane_x / 2 - (10 + message.length() * 4), 220 + 270);
                    }
                }
            }
        }


        if (gameMode.equals("Single")) {
            AttributedString player_1_score_atrr = getAttributeString(Integer.toString(player_1_score));
            if (TwoTwoPlaying == 1) g2d.drawString(player_1_score_atrr.getIterator(), pane_x / 2, pane_y / 2 + 60);
            if (OneOnePlaying == 1) g2d.drawString(Integer.toString(player_2_score), pane_x / 2 - 60, pane_y / 2);
            if (TwoOnePlaying == 1) g2d.drawString(Integer.toString(player_3_score), pane_x / 2, pane_y / 2 - 60);
            if (OneTwoPlaying == 1) g2d.drawString(Integer.toString(player_4_score), pane_x / 2 + 60, pane_y / 2);
        } else if ((numberOfPlayers == 2) || (gameMode.equals("2Player"))) {
            if (numberOfPlayers == 2) {
                if (playerIndex == 1) {
                    AttributedString player_2_4_score_atrr = getAttributeString(Integer.toString(player_2_4_score));
                    g2d.drawString(player_2_4_score_atrr.getIterator(), pane_x / 2 - 60, pane_y / 2);
                    g2d.drawString(Integer.toString(player_1_3_score), pane_x / 2 + 60, pane_y / 2);
                } else {
                    AttributedString player_1_3_score_atrr = getAttributeString(Integer.toString(player_1_3_score));
                    g2d.drawString(player_1_3_score_atrr.getIterator(), pane_x / 2 + 60, pane_y / 2);
                    g2d.drawString(Integer.toString(player_2_4_score), pane_x / 2 - 60, pane_y / 2);
                }
            } else {
                g2d.drawString(Integer.toString(player_2_4_score), pane_x / 2 - 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_1_3_score), pane_x / 2 + 60, pane_y / 2);
            }
        } else if (gameMode.equals("Multiplayer")) {
            if (playerIndex == 0) {
                AttributedString player_1_score_atrr = getAttributeString(Integer.toString(player_1_score));
                g2d.drawString(player_1_score_atrr.getIterator(), pane_x / 2, pane_y / 2 + 60);
                g2d.drawString(Integer.toString(player_2_score), pane_x / 2 - 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_3_score), pane_x / 2, pane_y / 2 - 60);
                g2d.drawString(Integer.toString(player_4_score), pane_x / 2 + 60, pane_y / 2);
            }
            if (playerIndex == 1) {
                AttributedString player_2_score_atrr = getAttributeString(Integer.toString(player_2_score));
                g2d.drawString(player_2_score_atrr.getIterator(), pane_x / 2 - 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_1_score), pane_x / 2, pane_y / 2 + 60);
                g2d.drawString(Integer.toString(player_3_score), pane_x / 2, pane_y / 2 - 60);
                g2d.drawString(Integer.toString(player_4_score), pane_x / 2 + 60, pane_y / 2);
            }
            if (playerIndex == 2) {
                AttributedString player_3_score_atrr = getAttributeString(Integer.toString(player_3_score));
                g2d.drawString(player_3_score_atrr.getIterator(), pane_x / 2, pane_y / 2 - 60);
                g2d.drawString(Integer.toString(player_1_score), pane_x / 2, pane_y / 2 + 60);
                g2d.drawString(Integer.toString(player_2_score), pane_x / 2 - 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_4_score), pane_x / 2 + 60, pane_y / 2);
            }
            if (playerIndex == 3) {
                AttributedString player_4_score_atrr = getAttributeString(Integer.toString(player_4_score));
                g2d.drawString(player_4_score_atrr.getIterator(), pane_x / 2 + 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_1_score), pane_x / 2, pane_y / 2 + 60);
                g2d.drawString(Integer.toString(player_2_score), pane_x / 2 - 60, pane_y / 2);
                g2d.drawString(Integer.toString(player_3_score), pane_x / 2, pane_y / 2 - 60);
            }
        }

        if (winMsg != null) {
            g2d.drawString("   " + winMsg, pane_x / 2 - (140 + message.length() * 4), 220);
            Space = true;
            winMsg = null;
            resetScore();
        }

        paddleOne_one = new Rectangle2D.Double(0, paddleOneY, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddleOne_two = new Rectangle2D.Double(pane_x - PADDLE_WIDTH, paddleOneOppY, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddleTwo_one = new Rectangle2D.Double(paddleTwoX, 0, PADDLE_HEIGHT, PADDLE_WIDTH);
        paddleTwo_two = new Rectangle2D.Double(paddleTwoOppX, pane_y - PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_WIDTH);

        g2d.setColor(Color.WHITE);

        if (OneOnePlaying == 1) {
            g2d.fill(paddleOne_one);
            g2d.draw(paddleOne_one);
        }
        if (OneTwoPlaying == 1) {
            g2d.fill(paddleOne_two);
            g2d.draw(paddleOne_two);
        }
        if (TwoOnePlaying == 1) {
            g2d.fill(paddleTwo_one);
            g2d.draw(paddleTwo_one);
        }
        if (TwoTwoPlaying == 1) {
            g2d.fill(paddleTwo_two);
            g2d.draw(paddleTwo_two);
        }

        //update ball location
        ball1.drawBall(g2d);

        if (ball2 != null) {
            ball2.drawBall(g2d);
        }

        if (gameMode.equals("Single") || gameMode.equals("2Player")) {
            if (timerSpecialPower % 8000 == 1000) {
                ball3 = new Ball(3, BALL_HEIGHT, BALL_WIDTH, false, false, "SpecialSpeed");
                powerUsed = false;
                iterationPower = 0;
                ball3.updateBallPositions(pane_x / 2, pane_y / 2);
                ball3.updateBallSpeed(1.2, 0.9);
                ball3.updateBallVelocity(-1, -1);
            } else if (timerSpecialPower % 8000 == 3000 || powerUsed) {
                ball3 = null;
                revalidate();
            } else if (timerSpecialPower % 8000 == 4000) {
//                System.out.println("Red Ball");
                ball3 = new Ball(3, BALL_HEIGHT, BALL_WIDTH, false, false, "SpecialLife");
                powerUsed = false;
                iterationPower = 0;
                ball3.updateBallPositions(pane_x / 2, pane_y / 2);
                ball3.updateBallSpeed(1.2, 0.9);
                ball3.updateBallVelocity(-1, -1);
            } else if (timerSpecialPower % 8000 == 6000 || powerUsed) {
                ball3 = null;
                revalidate();
            }

            if (iterationPower == 800) {
                powerUsed = false;
                PADDLE_SPEED = PADDLE_SPEED_ORIGINAL;
                revalidate();
            }


            timerSpecialPower++;
            if (powerUsed) {
                iterationPower++;
            }

//            System.out.println(timerSpecialPower % 8000 + ":::" + iterationPower);
        }

        if (ball3 != null) {
            ball3.drawBall(g2d);
        }


        //g2d.draw(ball);
        Toolkit.getDefaultToolkit(
        ).sync();
        g.dispose();

    }

    private void resetBall(String msg) {
        message = msg;

        ball1.updateBallPositions(pane_x / 2 - 10, pane_y / 2);
        ball1.updateBallSpeed(1.2, 0.9);
        ball1.updateBallVelocity(1, 1);

        if (ball2 != null) {
            ball2.updateBallPositions(pane_x / 2 + 10, pane_y / 2);
            ball2.updateBallSpeed(1.2, 0.9);
            ball2.updateBallVelocity(-1, -1);
        }

    }

    private void resetScore() {
        player_1_score = 0;
        player_2_score = 0;
        player_3_score = 0;
        player_4_score = 0;
    }

    /**
     * Read inputs, update the game state, paint window.
     * Called by the Timer
     */
    public void actionPerformed(ActionEvent e) {
        //wait for message display
        if (message.length() > 0) {
            try {
                Thread.sleep(800);
                message = "";
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        JSONObject key_event;
        if (UDPObject != null) {
            key_event = UDPObject.getKeyEvent();
            if (key_event == null) {
//                System.out.println("null");
            } else {

                String event_type = key_event.getString("event_type");
                int key_event_code = key_event.getInt("key_event_code");
                int playerIndex_others = key_event.getInt("playerIndex");
                //System.out.println("player index others" + playerIndex_others);
                if (key_event_code == KeyEvent.VK_SPACE && event_type.equals("Pressed")) {
                    Space = true;
                } else if (key_event_code == KeyEvent.VK_SPACE && event_type.equals("Released")) {
                    Space = false;
                } else if (key_event_code == KeyEvent.VK_ESCAPE && event_type.equals("Pressed")) {
                    Esc = true;
                } else if (key_event_code == KeyEvent.VK_ESCAPE && event_type.equals("Released")) {
                    Esc = false;
                } else {
                    if (event_type.equals("Released")) {
                        if (playerIndex_others == 0) {
                            paddle0.keyReleased(key_event_code);
                        }
                        if (playerIndex_others == 1) {
                            paddle1.keyReleased(key_event_code);
                        }
                        if (playerIndex_others == 2) {
                            paddle2.keyReleased(key_event_code);
                        }
                        if (playerIndex_others == 3) {
                            paddle3.keyReleased(key_event_code);
                        }
                    } else {
                        if (playerIndex_others == 0) {
                            paddle0.keyPressed(key_event_code);
                        }
                        if (playerIndex_others == 1) {
                            paddle1.keyPressed(key_event_code);
                        }
                        if (playerIndex_others == 2) {
                            paddle2.keyPressed(key_event_code);
                        }
                        if (playerIndex_others == 3) {
                            paddle3.keyPressed(key_event_code);
                        }
                    }

                }

            }
            UDPObject.resetKeyEvent();
        }
        //check for pause

        if (Space == true) {
            //wait for debounce
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (!flag) {
                pause = !pause;
                flag = true;
            }

        } else {
            flag = false;
        }
        if (pause == true) {
            return;
        }


        //check for end game
        if (Esc == true) {
            resetScore();
            resetBall("Game Over");
            repaint();

            pause = true;

        }

        if (numberOfPlayers == 3) paddleAI_OneTwo();


        if (gameMode.equals("Multiplayer")) {
            playersList = UDPObject.getPlayerlist();
            if (numberOfPlayers == 2) {
                playersList.add(2, null);
                playersList.add(3, null);
            }
            if (numberOfPlayers == 3) {
                playersList.add(3, null);

            }

            if (gameMode.equals("Multiplayer")) {
                if (numberOfPlayers == 2) {
                    if (playersList.get(0) == null) {
                        paddle0a = null;
                        paddle2a = null;
                        paddleAI_Two();
                    } else if (playersList.get(1) == null) {
                        paddle1a = null;
                        paddle3a = null;
                        paddleAI_One();
                    }
                }
                for (int i = 0; i < numberOfPlayers; i++) {
                    if (playersList.get(i) == null) {
                        if (i == 0) {
                            paddleAI_TwoTwo();
                            paddle0a = null;
                        } else if (i == 1) {
                            paddleAI_OneOne();
                            paddle1a = null;
                        } else if (i == 2) {
                            paddleAI_TwoOne();
                            paddle2a = null;
                        } else if (i == 3) {
                            paddleAI_OneOne();
                            paddle3a = null;
                        }
                    }
                }
            }
        }

        //apply input from key
        InputApply(gameMode);


        //collision detection
        /*	The math here is generous to the players.
            Hitting the side of the paddle still results in a bounce.
        */


        collisionBall(ball1);

        if (gameMode.equals("Single") || gameMode.equals("2Player")) {
            if (ball2 != null) {
                collisionBall(ball2);
            }
            if (ball3 != null) {
                collisionBall(ball3);
            }
        }

        if (ball2 != null && (new PhysicsCollision(BALL_HEIGHT, BALL_WIDTH)).detectCollisionWithOtherBallAndUpdateParameters(ball1, ball2)) {
            ball1.updateBallPositions(ball1.getBallPositionX() + ball1.getBallSpeedX() * ball1.getBallVelocityX(), ball1.getBallPositionY() + ball1.getBallSpeedY() * ball1.getBallVelocityY());
            ball2.updateBallPositions(ball2.getBallPositionX() + ball2.getBallSpeedX() * ball2.getBallVelocityX(), ball2.getBallPositionY() + ball2.getBallSpeedY() * ball2.getBallVelocityY());
        }

        if (ball3 != null && (new PhysicsCollision(BALL_HEIGHT, BALL_WIDTH)).detectCollisionWithOtherBallAndUpdateParameters(ball1, ball3)) {
            ball1.updateBallPositions(ball1.getBallPositionX() + ball1.getBallSpeedX() * ball1.getBallVelocityX(), ball1.getBallPositionY() + ball1.getBallSpeedY() * ball1.getBallVelocityY());
            ball3.updateBallPositions(ball3.getBallPositionX() + ball3.getBallSpeedX() * ball3.getBallVelocityX(), ball3.getBallPositionY() + ball3.getBallSpeedY() * ball3.getBallVelocityY());
        }

        if (ball2 != null && ball3 != null&& (new PhysicsCollision(BALL_HEIGHT, BALL_WIDTH)).detectCollisionWithOtherBallAndUpdateParameters(ball3, ball2)) {
            ball1.updateBallPositions(ball3.getBallPositionX() + ball3.getBallSpeedX() * ball3.getBallVelocityX(), ball3.getBallPositionY() + ball3.getBallSpeedY() * ball3.getBallVelocityY());
            ball2.updateBallPositions(ball2.getBallPositionX() + ball2.getBallSpeedX() * ball2.getBallVelocityX(), ball2.getBallPositionY() + ball2.getBallSpeedY() * ball2.getBallVelocityY());
        }

        player_1_3_score = player_1_score + player_3_score;
        player_2_4_score = player_2_score + player_4_score;
        if ((numberOfPlayers == 2) || (gameMode.equals("2Player"))) {
            if (player_1_3_score >= 2 * MAX_SCORE) {
                winMsg = "Player 2 wins";
                resetScore();
            } else if (player_2_4_score >= 2 * MAX_SCORE) {
                winMsg = "Player 1 wins";
                resetScore();
            }
        } else {
            if (player_1_score >= MAX_SCORE) {
                TwoTwoPlaying = 0;
                TwoTwoStop = true;
                paddleTwoOppX = -100;
            }
            if (player_2_score >= MAX_SCORE) {
                OneOnePlaying = 0;
                OneOneStop = true;
                paddleOneY = -100;

            }
            if (player_3_score >= MAX_SCORE) {
                TwoOnePlaying = 0;
                TwoOneStop = true;
                paddleTwoX = -100;
            }
            if (player_4_score >= MAX_SCORE) {
                OneTwoPlaying = 0;
                OneTwoStop = true;
                paddleOneOppY = -100;
            }
            if ((OneOnePlaying + OneTwoPlaying + TwoOnePlaying + TwoTwoPlaying) == 1) {
                if (OneOnePlaying == 1) {
                    winMsg = "Player 2 wins";
                } else if (OneTwoPlaying == 1) {
                    winMsg = "Player 4 wins";
                } else if (TwoOnePlaying == 1) {
                    winMsg = "Player 3 wins";
                } else if (TwoTwoPlaying == 1) {
                    winMsg = "Player 1 wins";
                }
            }
        }

        repaint();


    }

    public void paddleAI_One() {
        paddleOneY=paddleOneOppY;
        if (paddleOneY + PADDLE_HEIGHT / 2 > ball1.ball_y) {
            if (paddleOneY > 0) {
                paddleOneY -= PADDLE_SPEED_AI;
                paddleOneOppY -= PADDLE_SPEED_AI;
            }
            positiveYOne_one = false;
            positiveYOne_two = false;
            OneOneStop = false;
            OneTwoStop = false;
        } else if (paddleOneY + PADDLE_HEIGHT / 2 == ball1.ball_y) {
            OneOneStop = true;
            OneTwoStop = true;
        } else {
            if (paddleOneY < pane_y - PADDLE_HEIGHT) {
                paddleOneY += PADDLE_SPEED_AI;
                paddleOneOppY += PADDLE_SPEED_AI;
            }
            positiveYOne_one = true;
            positiveYOne_two = true;
            OneOneStop = false;
            OneTwoStop = false;
        }
    }

    public void paddleAI_Two() {
        paddleTwoX=paddleTwoOppX;
        if (paddleTwoX + PADDLE_HEIGHT / 2 > ball1.ball_x) {
            if (paddleTwoX > 0) {
                paddleTwoX -= PADDLE_SPEED_AI;
                paddleTwoOppX -= PADDLE_SPEED_AI;
            }
            positiveXTwo_one = false;
            positiveXTwo_two = false;
            TwoOneStop = false;
            TwoTwoStop = false;
        } else if (paddleTwoX + PADDLE_HEIGHT / 2 == ball1.ball_x) {
            TwoOneStop = true;
            TwoTwoStop = true;
        } else {
            if (paddleTwoX < pane_x - PADDLE_HEIGHT) {
                paddleTwoX += PADDLE_SPEED_AI;
                paddleTwoOppX += PADDLE_SPEED_AI;
            }
            positiveXTwo_one = true;
            positiveXTwo_two = true;
            TwoOneStop = false;
            TwoTwoStop = false;
        }
    }

    public void paddleAI_OneOne() {

        //paddleOne_one AI
        if (ball2 == null) {
            if (paddleOneY + PADDLE_HEIGHT / 2 > ball1.ball_y) {
                if (paddleOneY > 0) {
                    paddleOneY -= PADDLE_SPEED_AI;
                }
                positiveYOne_one = false;
                OneOneStop = false;
            } else if (paddleOneY + PADDLE_HEIGHT / 2 == ball1.ball_y) {
                OneOneStop = true;
            } else {
                if (paddleOneY < pane_y - PADDLE_HEIGHT) {
                    paddleOneY += PADDLE_SPEED_AI;
                }
                positiveYOne_one = true;
                OneOneStop = false;
            }
        } else {

            double time1, time2;
            if (ball1.getBallPositiveX()) {
                time1 = (2 * pane_x - ball1.getBallPositionX() - BALL_HEIGHT / 2) / ball1.getBallSpeedX();
            } else {
                time1 = (ball1.getBallPositionX() + BALL_HEIGHT / 2) / ball1.getBallSpeedX();
            }
            if (ball2.getBallPositiveX()) {
                time2 = (2 * pane_x - ball2.getBallPositionX() - BALL_HEIGHT / 2) / ball2.getBallSpeedX();
            } else {
                time2 = (ball2.getBallPositionX() + BALL_HEIGHT / 2) / ball2.getBallSpeedX();
            }

            if (time1 < time2) {
                if (paddleOneY + PADDLE_HEIGHT / 2 > ball1.ball_y) {
                    if (paddleOneY > 0) {
                        paddleOneY -= PADDLE_SPEED_AI;
                    }
                    positiveYOne_one = false;
                    OneOneStop = false;
                } else if (paddleOneY + PADDLE_HEIGHT / 2 == ball1.ball_y) {
                    OneOneStop = true;
                } else {
                    if (paddleOneY < pane_y - PADDLE_HEIGHT) {
                        paddleOneY += PADDLE_SPEED_AI;
                    }
                    positiveYOne_one = true;
                    OneOneStop = false;
                }
            } else {
                if (paddleOneY + PADDLE_HEIGHT / 2 > ball2.ball_y) {
                    if (paddleOneY > 0) {
                        paddleOneY -= PADDLE_SPEED_AI;
                    }
                    positiveYOne_one = false;
                    OneOneStop = false;
                } else if (paddleOneY + PADDLE_HEIGHT / 2 == ball2.ball_y) {
                    OneOneStop = true;
                } else {
                    if (paddleOneY < pane_y - PADDLE_HEIGHT) {
                        paddleOneY += PADDLE_SPEED_AI;
                    }
                    positiveYOne_one = true;
                    OneOneStop = false;
                }
            }
        }


    }

    public void paddleAI_OneTwo() {

        //paddleOne_two AI
        if (ball2 == null) {
            if (paddleOneOppY + PADDLE_HEIGHT / 2 > ball1.ball_y) {
                if (paddleOneOppY > 0) {
                    paddleOneOppY -= PADDLE_SPEED_AI;
                }
                positiveYOne_two = false;
                OneTwoStop = false;
            } else if (paddleOneOppY + PADDLE_HEIGHT / 2 == ball1.ball_y) {
                OneTwoStop = true;
            } else {
                if (paddleOneOppY < pane_y - PADDLE_HEIGHT) {
                    paddleOneOppY += PADDLE_SPEED_AI;
                }
                positiveYOne_two = true;
                OneTwoStop = false;
            }
        } else {

            double time1, time2;
            if (!ball1.getBallPositiveX()) {
                time1 = (pane_x + ball1.getBallPositionX() + BALL_HEIGHT / 2) / ball1.getBallSpeedX();
            } else {
                time1 = (pane_x - ball1.getBallPositionX() - BALL_HEIGHT / 2) / ball1.getBallSpeedX();
            }
            if (!ball2.getBallPositiveX()) {
                time2 = (pane_x + ball2.getBallPositionX() + BALL_HEIGHT / 2) / ball2.getBallSpeedX();
            } else {
                time2 = (pane_x - ball2.getBallPositionX() - BALL_HEIGHT / 2) / ball2.getBallSpeedX();
            }

            if (time1 < time2) {
                if (paddleOneOppY + PADDLE_HEIGHT / 2 > ball1.ball_y) {
                    if (paddleOneOppY > 0) {
                        paddleOneOppY -= PADDLE_SPEED_AI;
                    }
                    positiveYOne_two = false;
                    OneTwoStop = false;
                } else if (paddleOneOppY + PADDLE_HEIGHT / 2 == ball1.ball_y) {
                    OneTwoStop = true;
                } else {
                    if (paddleOneOppY < pane_y - PADDLE_HEIGHT) {
                        paddleOneOppY += PADDLE_SPEED_AI;
                    }
                    positiveYOne_two = true;
                    OneTwoStop = false;
                }
            } else {
                if (paddleOneOppY + PADDLE_HEIGHT / 2 > ball2.ball_y) {
                    if (paddleOneOppY > 0) {
                        paddleOneOppY -= PADDLE_SPEED_AI;
                    }
                    positiveYOne_two = false;
                    OneTwoStop = false;
                } else if (paddleOneOppY + PADDLE_HEIGHT / 2 == ball2.ball_y) {
                    OneTwoStop = true;
                } else {
                    if (paddleOneOppY < pane_y - PADDLE_HEIGHT) {
                        paddleOneOppY += PADDLE_SPEED_AI;
                    }
                    positiveYOne_two = true;
                    OneTwoStop = false;
                }
            }
        }

    }

    public void paddleAI_TwoOne() {

        //paddleTwo_one AI
        if (ball2 == null) {
            if (paddleTwoX + PADDLE_HEIGHT / 2 > ball1.ball_x) {
                if (paddleTwoX > 0) {
                    paddleTwoX -= PADDLE_SPEED_AI;
                }
                positiveXTwo_one = false;
                TwoOneStop = false;
            } else if (paddleTwoX + PADDLE_HEIGHT / 2 == ball1.ball_x) {
                TwoOneStop = true;
            } else {
                if (paddleTwoX < pane_x - PADDLE_HEIGHT) {
                    paddleTwoX += PADDLE_SPEED_AI;
                }
                positiveXTwo_one = true;
                TwoOneStop = false;
            }

        } else {

            double time1, time2;
            if (ball1.getBallPositiveY()) {
                time1 = (2 * pane_y - ball1.getBallPositionY() - BALL_HEIGHT / 2) / ball1.getBallSpeedY();
            } else {
                time1 = (ball1.getBallPositionY() + BALL_HEIGHT / 2) / ball1.getBallSpeedY();
            }
            if (ball2.getBallPositiveY()) {
                time2 = (2 * pane_y - ball2.getBallPositionY() - BALL_HEIGHT / 2) / ball2.getBallSpeedY();
            } else {
                time2 = (ball2.getBallPositionY() + BALL_HEIGHT / 2) / ball2.getBallSpeedY();
            }

            if (time1 < time2) {
                if (paddleTwoX + PADDLE_HEIGHT / 2 > ball1.ball_x) {
                    if (paddleTwoX > 0) {
                        paddleTwoX -= PADDLE_SPEED_AI;
                    }
                    positiveXTwo_one = false;
                    TwoOneStop = false;
                } else if (paddleTwoX + PADDLE_HEIGHT / 2 == ball1.ball_x) {
                    TwoOneStop = true;
                } else {
                    if (paddleTwoX < pane_x - PADDLE_HEIGHT) {
                        paddleTwoX += PADDLE_SPEED_AI;
                    }
                    positiveXTwo_one = true;
                    TwoOneStop = false;
                }
            } else {
                if (paddleTwoX + PADDLE_HEIGHT / 2 > ball2.ball_x) {
                    if (paddleTwoX > 0) {
                        paddleTwoX -= PADDLE_SPEED_AI;
                    }
                    positiveXTwo_one = false;
                    TwoOneStop = false;
                } else if (paddleTwoX + PADDLE_HEIGHT / 2 == ball2.ball_x) {
                    TwoOneStop = true;
                } else {
                    if (paddleTwoX < pane_x - PADDLE_HEIGHT) {
                        paddleTwoX += PADDLE_SPEED_AI;
                    }
                    positiveXTwo_one = true;
                    TwoOneStop = false;
                }
            }

        }
    }

    public void paddleAI_TwoTwo() {

        //paddleTwo_Two AI
        if (ball2 == null) {
            if (paddleTwoOppX + PADDLE_HEIGHT / 2 > ball1.ball_x) {
                if (paddleTwoOppX > 0) {
                    paddleTwoX -= PADDLE_SPEED_AI;
                }
                positiveXTwo_two = false;
                TwoTwoStop = false;
            } else if (paddleTwoOppX + PADDLE_HEIGHT / 2 == ball1.ball_x) {
                TwoTwoStop = true;
            } else {
                if (paddleTwoOppX < pane_x - PADDLE_HEIGHT) {
                    paddleTwoOppX += PADDLE_SPEED_AI;
                }
                positiveXTwo_two = true;
                TwoTwoStop = false;
            }
        } else {

            double time1, time2;
            if (!ball1.getBallPositiveY()) {
                time1 = (pane_y + ball1.getBallPositionY() + BALL_HEIGHT / 2) / ball1.getBallSpeedY();
            } else {
                time1 = (pane_y - ball1.getBallPositionY() + BALL_HEIGHT / 2) / ball1.getBallSpeedY();
            }
            if (!ball2.getBallPositiveY()) {
                time2 = (pane_y + ball2.getBallPositionY() + BALL_HEIGHT / 2) / ball2.getBallSpeedY();
            } else {
                time2 = (pane_x - ball2.getBallPositionY() + BALL_HEIGHT / 2) / ball2.getBallSpeedY();
            }

            if (time1 < time2) {
                if (paddleTwoOppX + PADDLE_HEIGHT / 2 > ball1.ball_x) {
                    if (paddleTwoOppX > 0) {
                        paddleTwoX -= PADDLE_SPEED_AI;
                    }
                    positiveXTwo_two = false;
                    TwoTwoStop = false;
                } else if (paddleTwoOppX + PADDLE_HEIGHT / 2 == ball1.ball_x) {
                    TwoTwoStop = true;
                } else {
                    if (paddleTwoOppX < pane_x - PADDLE_HEIGHT) {
                        paddleTwoOppX += PADDLE_SPEED_AI;
                    }
                    positiveXTwo_two = true;
                    TwoTwoStop = false;
                }
            } else {
                if (paddleTwoOppX + PADDLE_HEIGHT / 2 > ball2.ball_x) {
                    if (paddleTwoOppX > 0) {
                        paddleTwoX -= PADDLE_SPEED_AI;
                    }
                    positiveXTwo_two = false;
                    TwoTwoStop = false;
                } else if (paddleTwoOppX + PADDLE_HEIGHT / 2 == ball2.ball_x) {
                    TwoTwoStop = true;
                } else {
                    if (paddleTwoOppX < pane_x - PADDLE_HEIGHT) {
                        paddleTwoOppX += PADDLE_SPEED_AI;
                    }
                    positiveXTwo_two = true;
                    TwoTwoStop = false;
                }
            }
        }
    }

    public void InputApply(String mode) {
        //single player game mode
        if (mode.equals("Single")) {
            if (paddle3.Left == true) {

                positiveXTwo_two = false;
                TwoTwoStop = false;

                if (paddleTwoOppX > 0) {
                    paddleTwoOppX -= PADDLE_SPEED;
                }
            } else if (paddle3.Right == true) {

                positiveXTwo_two = true;
                TwoTwoStop = false;

                if (paddleTwoOppX < pane_x - PADDLE_HEIGHT) {
                    paddleTwoOppX += PADDLE_SPEED;
                }
            } else {
                TwoTwoStop = true;
            }

            paddleAI_OneOne();
            paddleAI_OneTwo();
            paddleAI_TwoOne();

        }
        //game mode two player
        else {

            //Player 1
            if (paddle1a != null) {
                if ((paddle1a.S == true)) {

                    positiveYOne_one = true;
                    //positiveYOne_two = true;
                    OneOneStop = false;
                    //OneTwoStop = false;

                    if (paddleOneY < pane_y - PADDLE_HEIGHT) {
                        paddleOneY += PADDLE_SPEED;
                        //paddleOneOppY += PADDLE_SPEED;
                    }
                } else if ((paddle1a.W == true)) {

                    positiveYOne_one = false;
                    //positiveYOne_two = false;
                    OneOneStop = false;
                    //OneTwoStop = false;

                    if (paddleOneY > 0) {
                        paddleOneY -= PADDLE_SPEED;
                        //paddleOneOppY -= PADDLE_SPEED;
                    }
                } else {
                    OneOneStop = true;
                    //OneTwoStop = true;
                }
            }


            //Player 0
            if (paddle0a != null) {
                if (paddle0a.Left == true) {

                    //positiveXTwo_one = false;
                    positiveXTwo_two = false;
                    //TwoOneStop = false;
                    TwoTwoStop = false;

                    if (paddleTwoOppX > 0) {
                        //paddleTwoX -= PADDLE_SPEED;
                        paddleTwoOppX -= PADDLE_SPEED;
                    }
                } else if ((paddle0a.Right == true)) {

                    //positiveXTwo_one = true;
                    positiveXTwo_two = true;
                    //TwoOneStop = false;
                    TwoTwoStop = false;

                    if (paddleTwoOppX < pane_x - PADDLE_HEIGHT) {
                        //paddleTwoX += PADDLE_SPEED;
                        paddleTwoOppX += PADDLE_SPEED;
                    }
                } else {
                    //TwoOneStop = true;
                    TwoTwoStop = true;
                }
            }

            //Player 2
            if (paddle2a != null) {
                if (paddle2a.Left == true) {

                    positiveXTwo_one = false;
                    //positiveXTwo_two = false;
                    TwoOneStop = false;
                    //TwoTwoStop = false;

                    if (paddleTwoX > 0) {
                        paddleTwoX -= PADDLE_SPEED;
                        //paddleTwoOppX -= PADDLE_SPEED;
                    }
                } else if ((paddle2a.Right == true)) {

                    positiveXTwo_one = true;
                    //positiveXTwo_two = true;
                    TwoOneStop = false;
                    //TwoTwoStop = false;

                    if (paddleTwoX < pane_x - PADDLE_HEIGHT) {
                        paddleTwoX += PADDLE_SPEED;
                        //paddleTwoOppX += PADDLE_SPEED;
                    }
                } else {
                    TwoOneStop = true;
                    //TwoTwoStop = true;
                }
            }


            //Player 4
            if (paddle3a != null) {
                if ((paddle3a.S == true)) {

                    //positiveYOne_one = true;
                    positiveYOne_two = true;
                    //OneOneStop = false;
                    OneTwoStop = false;

                    if (paddleOneOppY < pane_y - PADDLE_HEIGHT) {
                        //paddleOneY += PADDLE_SPEED;
                        paddleOneOppY += PADDLE_SPEED;
                    }
                } else if ((paddle3a.W == true)) {

                    //positiveYOne_one = false;
                    positiveYOne_two = false;
                    //OneOneStop = false;
                    OneTwoStop = false;

                    if (paddleOneOppY > 0) {
                        //paddleOneY -= PADDLE_SPEED;
                        paddleOneOppY -= PADDLE_SPEED;
                    }
                } else {
                    //OneOneStop = true;
                    OneTwoStop = true;
                }
            }
        }
    }

    public void collisionBall(Ball ball) {

        physics = new PhysicsCollision(pane_x, pane_y, PADDLE_SPEED, PADDLE_WIDTH, PADDLE_HEIGHT, BALL_HEIGHT, BALL_WIDTH);

        double deltaSpeedX;
        double deltaSpeedY;
        double deltaVelocityX;
        double deltaVelocityY;

        double SpeedX;
        double SpeedY;

        double VelocityX;
        double VelocityY;

        double BallX;
        double BallY;

        double BallOldX = ball.getBallPositionX();
        double BallOldY = ball.getBallPositionY();

        boolean positiveBallX = ball.getBallPositiveX();
        boolean positiveBallY = ball.getBallPositiveY();


        //paddle one_one
        //paddle one_two
        //paddle two_one
        //paddle two_two

        //left wall
        //right wall
        //top wall
        // bottom wall

        //paddle one_one *****************************************************************************************************************

        if (physics.detectCollisionWithPaddleAndUpdateParameters(BallOldX, BallOldY, paddleOneY, OneOneStop, positiveYOne_one, positiveBallY)) {

            deltaSpeedX = physics.getDeltaSPEEDPERPENDICULAR();
            deltaSpeedY = physics.getDeltaSPEEDPARRALEL();
            deltaVelocityX = physics.getDeltaVELOCITYPERPENDICULAR();
            deltaVelocityY = physics.getDeltaVELOCITYPARALLEL();

            SpeedX = ball.getBallSpeedX() * (1 + deltaSpeedX);
            SpeedY = ball.getBallSpeedY() + deltaSpeedY;

            ball.updateBallPositions(PADDLE_WIDTH, BallOldY);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 1) {
//                    System.out.println("Paddle Collide");
                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_2_score);

                } else {
                    if (playersList.get(1) != null) {
                        JSONObject ballPosition = UDPObject.getPlayerScoreAndBall();
                        while (ballPosition == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            ballPosition = UDPObject.getPlayerScoreAndBall();
                        }


                        if (ballPosition != null) {
                            BallX = ballPosition.getDouble("b_x");
                            BallY = ballPosition.getDouble("b_y");
                            SpeedX = ballPosition.getDouble("B_X");
                            SpeedY = ballPosition.getDouble("B_Y");
                            deltaVelocityX = ballPosition.getDouble("v_x");
                            deltaVelocityY = ballPosition.getDouble("v_y");
                            player_2_score = ballPosition.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // paddle one_two ********************************************************************************************************************

        else if (physics.detectCollisionWithPaddleAndUpdateParameters(0 - BallOldX + pane_x - BALL_HEIGHT, BallOldY, paddleOneOppY, OneTwoStop, positiveYOne_two, positiveBallY)) {

            deltaSpeedX = physics.getDeltaSPEEDPERPENDICULAR();
            deltaSpeedY = physics.getDeltaSPEEDPARRALEL();
            deltaVelocityX = physics.getDeltaVELOCITYPERPENDICULAR();
            deltaVelocityY = physics.getDeltaVELOCITYPARALLEL();

            SpeedX = ball.getBallSpeedX() * (1 + deltaSpeedX);
            SpeedY = ball.getBallSpeedY() + deltaSpeedY;

            ball.updateBallPositions(pane_x - PADDLE_WIDTH - BALL_HEIGHT, BallOldY);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 3 || ((playerIndex == 1) && (numberOfPlayers == 2))) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_4_score);

                } else {
                    if (((numberOfPlayers == 4) && (playersList.get(3) != null)) || (numberOfPlayers == 2) && (playersList.get(1) != null)) {
                        JSONObject ballPosition = UDPObject.getPlayerScoreAndBall();
                        while (ballPosition == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            ballPosition = UDPObject.getPlayerScoreAndBall();
                        }

                        if (ballPosition != null) {
                            BallX = ballPosition.getDouble("b_x");
                            BallY = ballPosition.getDouble("b_y");
                            SpeedX = ballPosition.getDouble("B_X");
                            SpeedY = ballPosition.getDouble("B_Y");
                            deltaVelocityX = ballPosition.getDouble("v_x");
                            deltaVelocityY = ballPosition.getDouble("v_y");
                            player_4_score = ballPosition.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // paddle two_one ********************************************************************************************************************

        else if (physics.detectCollisionWithPaddleAndUpdateParameters(BallOldY, BallOldX, paddleTwoX, TwoOneStop, positiveXTwo_one, positiveBallX)) {

            deltaSpeedX = physics.getDeltaSPEEDPARRALEL();
            deltaSpeedY = physics.getDeltaSPEEDPERPENDICULAR();
            deltaVelocityX = physics.getDeltaVELOCITYPARALLEL();
            deltaVelocityY = physics.getDeltaVELOCITYPERPENDICULAR();

            SpeedY = ball.getBallSpeedX() * (1 + deltaSpeedY);
            SpeedX = ball.getBallSpeedY() + deltaSpeedX;

            ball.updateBallPositions(BallOldX, PADDLE_WIDTH);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 2 || ((playerIndex == 0) && (numberOfPlayers == 2))) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_3_score);

                } else {
                    if ((numberOfPlayers == 2) && (playersList.get(0) != null) || (numberOfPlayers != 2) && (playersList.get(2) != null)) {
                        JSONObject ballPosition = UDPObject.getPlayerScoreAndBall();
                        while (ballPosition == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            ballPosition = UDPObject.getPlayerScoreAndBall();
                        }

                        if (ballPosition != null) {
                            BallX = ballPosition.getDouble("b_x");
                            BallY = ballPosition.getDouble("b_y");
                            SpeedX = ballPosition.getDouble("B_X");
                            SpeedY = ballPosition.getDouble("B_Y");
                            deltaVelocityX = ballPosition.getDouble("v_x");
                            deltaVelocityY = ballPosition.getDouble("v_y");
                            player_3_score = ballPosition.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }

        }
        // paddle two_two ********************************************************************************************************************

        else if (physics.detectCollisionWithPaddleAndUpdateParameters(0 - BallOldY + pane_y - BALL_HEIGHT, BallOldX, paddleTwoOppX, TwoTwoStop, positiveXTwo_two, positiveBallX)) {

            deltaSpeedX = physics.getDeltaSPEEDPARRALEL();
            deltaSpeedY = physics.getDeltaSPEEDPERPENDICULAR();
            deltaVelocityX = physics.getDeltaVELOCITYPARALLEL();
            deltaVelocityY = physics.getDeltaVELOCITYPERPENDICULAR();

            ball.updateBallPositions(BallOldX, pane_y - PADDLE_WIDTH - BALL_HEIGHT);

            SpeedY = ball.getBallSpeedX() * (1 + deltaSpeedY);
            SpeedX = ball.getBallSpeedY() + deltaSpeedX;

            if (gameMode.equals("Single") || gameMode.equals("2Player")) {
                if (ball.getType().equals("SpecialSpeed")) {
                    ball.updateBallPositions(999, 999);
                    PADDLE_SPEED *= 2;
                    iterationPower = 0;
                    powerUsed = true;
                    return;
                } else if (ball.getType().equals("SpecialLife")) {
                    ball.updateBallPositions(999, 999);
                    player_1_score--;
                    iterationPower = 0;
                    powerUsed = true;
                    return;
                }
            }


            if (gameMode.equals("Multiplayer")) {

                if (playerIndex == 0) {
                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_1_score);
                } else {
                    if (playersList.get(0) != null) {
                        JSONObject ballPosition = UDPObject.getPlayerScoreAndBall();
                        while (ballPosition == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            ballPosition = UDPObject.getPlayerScoreAndBall();
                        }

                        if (ballPosition != null) {
                            BallX = ballPosition.getDouble("b_x");
                            BallY = ballPosition.getDouble("b_y");
                            SpeedX = ballPosition.getDouble("B_X");
                            SpeedY = ballPosition.getDouble("B_Y");
                            deltaVelocityX = ballPosition.getDouble("v_x");
                            deltaVelocityY = ballPosition.getDouble("v_y");
                            player_1_score = ballPosition.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // left wall ********************************************************************************************************************

        else if (physics.detectCollisionWithWallAndUpdateParameters(BallOldX)) {

            deltaSpeedX = physics.getDeltaSPEEDPERPENDICULAR();
            deltaSpeedY = physics.getDeltaSPEEDPARRALEL();
            deltaVelocityX = physics.getDeltaVELOCITYPERPENDICULAR();
            deltaVelocityY = physics.getDeltaVELOCITYPARALLEL();

            SpeedX = ball.getBallSpeedX() * (1 + deltaSpeedX);
            SpeedY = ball.getBallSpeedY() + deltaSpeedY;

            if (ball.getType().equals("SpecialLife") || ball.getType().equals("SpecialSpeed")) {

            } else {
                player_2_score++;
            }

            ball.updateBallPositions(PADDLE_WIDTH, BallOldY);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 1) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_2_score);

                } else {
                    if (playersList.get(1) != null) {
                        JSONObject score_and_balls = UDPObject.getPlayerScoreAndBall();
                        while (score_and_balls == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            score_and_balls = UDPObject.getPlayerScoreAndBall();
                        }

                        if (score_and_balls != null) {

                            BallX = score_and_balls.getDouble("b_x");
                            BallY = score_and_balls.getDouble("b_y");
                            SpeedX = score_and_balls.getDouble("B_X");
                            SpeedY = score_and_balls.getDouble("B_Y");
                            deltaVelocityX = score_and_balls.getDouble("v_x");
                            deltaVelocityY = score_and_balls.getDouble("v_y");
                            player_2_score = score_and_balls.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // right wall ********************************************************************************************************************

        else if (physics.detectCollisionWithWallAndUpdateParameters(0 - BallOldX + pane_x - BALL_HEIGHT)) {

            deltaSpeedX = physics.getDeltaSPEEDPERPENDICULAR();
            deltaSpeedY = physics.getDeltaSPEEDPARRALEL();
            deltaVelocityX = physics.getDeltaVELOCITYPERPENDICULAR();
            deltaVelocityY = physics.getDeltaVELOCITYPARALLEL();

            SpeedX = ball.getBallSpeedX() * (1 + deltaSpeedX);
            SpeedY = ball.getBallSpeedY() + deltaSpeedY;

            if (ball.getType().equals("SpecialLife") || ball.getType().equals("SpecialSpeed")) {

            } else {
                player_4_score++;
            }
            ball.updateBallPositions(pane_x - PADDLE_WIDTH - BALL_HEIGHT, BallOldY);


            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 3 || ((playerIndex == 1) && (numberOfPlayers == 2))) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_4_score);

                } else {
                    if (((numberOfPlayers == 4) && (playersList.get(3) != null)) || (numberOfPlayers == 2) && (playersList.get(1) != null)) {
                        JSONObject score_and_balls = UDPObject.getPlayerScoreAndBall();
                        while (score_and_balls == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            score_and_balls = UDPObject.getPlayerScoreAndBall();
                        }


                        if (score_and_balls != null) {

                            BallX = score_and_balls.getDouble("b_x");
                            BallY = score_and_balls.getDouble("b_y");
                            SpeedX = score_and_balls.getDouble("B_X");
                            SpeedY = score_and_balls.getDouble("B_Y");
                            deltaVelocityX = score_and_balls.getDouble("v_x");
                            deltaVelocityY = score_and_balls.getDouble("v_y");
                            player_4_score = score_and_balls.getInt("p_score");

                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // top wall ********************************************************************************************************************

        else if (physics.detectCollisionWithWallAndUpdateParameters(BallOldY)) {

            deltaSpeedX = physics.getDeltaSPEEDPARRALEL();
            deltaSpeedY = physics.getDeltaSPEEDPERPENDICULAR();
            deltaVelocityX = physics.getDeltaVELOCITYPARALLEL();
            deltaVelocityY = physics.getDeltaVELOCITYPERPENDICULAR();

            SpeedY = ball.getBallSpeedX() * (1 + deltaSpeedY);
            SpeedX = ball.getBallSpeedY() + deltaSpeedX;

            if (ball.getType().equals("SpecialLife") || ball.getType().equals("SpecialSpeed")) {

            } else {
                player_3_score++;
            }
            ball.updateBallPositions(BallOldX, PADDLE_WIDTH);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 2 || ((playerIndex == 0) && (numberOfPlayers == 2))) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_3_score);

                } else {
                    if ((numberOfPlayers == 2) && (playersList.get(0) != null) || (numberOfPlayers != 2) && (playersList.get(2) != null)) {
                        JSONObject score_and_balls = UDPObject.getPlayerScoreAndBall();
                        while (score_and_balls == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            score_and_balls = UDPObject.getPlayerScoreAndBall();
                        }

                        if (score_and_balls != null) {

                            BallX = score_and_balls.getDouble("b_x");
                            BallY = score_and_balls.getDouble("b_y");
                            SpeedX = score_and_balls.getDouble("B_X");
                            SpeedY = score_and_balls.getDouble("B_Y");
                            deltaVelocityX = score_and_balls.getDouble("v_x");
                            deltaVelocityY = score_and_balls.getDouble("v_y");
                            player_3_score = score_and_balls.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }
        }
        // bottom wall ********************************************************************************************************************

        else if (physics.detectCollisionWithWallAndUpdateParameters(0 - BallOldY + pane_y - BALL_HEIGHT)) {

            deltaSpeedX = physics.getDeltaSPEEDPARRALEL();
            deltaSpeedY = physics.getDeltaSPEEDPERPENDICULAR();
            deltaVelocityX = physics.getDeltaVELOCITYPARALLEL();
            deltaVelocityY = physics.getDeltaVELOCITYPERPENDICULAR();

            SpeedY = ball.getBallSpeedX() * (1 + deltaSpeedY);
            SpeedX = ball.getBallSpeedY() + deltaSpeedX;

            if (ball.getType().equals("SpecialLife") || ball.getType().equals("SpecialSpeed")) {

            } else {
                player_1_score++;
            }
            ball.updateBallPositions(BallOldX, pane_y - PADDLE_WIDTH - BALL_HEIGHT);

            if (gameMode.equals("Multiplayer")) {
                if (playerIndex == 0) {

                    UDPObject.sendBallAndScore(ball.ball_x, ball.ball_y, SpeedX, SpeedY, deltaVelocityX, deltaVelocityY, ball.id, player_1_score);

                } else {
                    if (playersList.get(0) != null) {
                        JSONObject score_and_balls = UDPObject.getPlayerScoreAndBall();
                        while (score_and_balls == null) {
                            try {
                                Thread.sleep(threadtimeout);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            score_and_balls = UDPObject.getPlayerScoreAndBall();
                        }

                        if (score_and_balls != null) {

                            BallX = score_and_balls.getDouble("b_x");
                            BallY = score_and_balls.getDouble("b_y");
                            SpeedX = score_and_balls.getDouble("B_X");
                            SpeedY = score_and_balls.getDouble("B_Y");
                            deltaVelocityX = score_and_balls.getDouble("v_x");
                            deltaVelocityY = score_and_balls.getDouble("v_y");
                            player_1_score = score_and_balls.getInt("p_score");
                            UDPObject.resetBallAndScore();

                            ball.updateBallPositions(BallX, BallY);
                        }
                    }
                }
            }

        } else {

            deltaVelocityX = 1;
            deltaVelocityY = 1;
            SpeedX = ball.getBallSpeedX();
            SpeedY = ball.getBallSpeedY();
        }


        if (deltaVelocityX == -1) {
            positiveBallX = !positiveBallX;
        } else if (deltaVelocityY == -1) {
            positiveBallY = !positiveBallY;
        }

        VelocityX = ball.getBallVelocityX() * deltaVelocityX;
        VelocityY = ball.getBallVelocityY() * deltaVelocityY;

        BallX = ball.getBallPositionX() + SpeedX * VelocityX;
        BallY = ball.getBallPositionY() + SpeedY * VelocityY;

        ball.updateBallVelocity(VelocityX, VelocityY);
        ball.updateBallSpeed(SpeedX, SpeedY);
        ball.updateBallPositiveBooleans(positiveBallX, positiveBallY);
        ball.updateBallPositions(BallX, BallY);


    }

}
