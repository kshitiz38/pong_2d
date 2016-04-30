/**
 * Created by Kshitiz Sharma on 28-Apr-16.
 */
/*
Physics class includes the equations for the detection of the collision of the ball with a paddle, a wall and with another wall
It is used in Board.java for implementation of the collision stuff.
 */
public class PhysicsCollision {

    int PADDLE_WIDTH;
    int PADDLE_HEIGHT;
    int BALL_HEIGHT;
    int BALL_WIDTH;

    int pane_length;

    double PADDLE_SPEED;
    double aRestitution = 0.002;
    double eRestitution = 0.002;

    double deltaSPEEDPERPENDICULAR = 0;
    double deltaSPEEDPARALLEL = 0;

    double deltaVELOCITYPERPENDICULAR = 1;
    double deltaVELOCITYPARALLEL = 1;

    public PhysicsCollision(int BALL_HEIGHT, int BALL_WIDTH) {
        this.BALL_HEIGHT = BALL_HEIGHT;
        this.BALL_WIDTH = BALL_WIDTH;
    }

    public PhysicsCollision(int pane_x, int pane_y, double PADDLE_SPEED, int PADDLE_WIDTH, int PADDLE_HEIGHT, int BALL_HEIGHT, int BALL_WIDTH) {

        if (pane_x==pane_y) {
            this.pane_length = pane_x;
        } else {
//            System.out.println("dimensions are different"+(pane_x-pane_y));
        }

        this.PADDLE_WIDTH = PADDLE_WIDTH;
        this.PADDLE_HEIGHT = PADDLE_HEIGHT;
        this.PADDLE_SPEED = PADDLE_SPEED;

        this.BALL_HEIGHT = BALL_HEIGHT;
        this.BALL_WIDTH = BALL_WIDTH;
    }

    public boolean detectCollisionWithPaddleAndUpdateParameters(double ballPositionPerpendicular, double ballPositionParallel, double paddlePositionParallel, boolean paddleIsNotMoving, boolean paddlePositiveDirection, boolean ballPositiveDirection) {

        if (ballPositionPerpendicular < PADDLE_WIDTH && (ballPositionParallel + BALL_HEIGHT > paddlePositionParallel && ballPositionParallel < paddlePositionParallel + PADDLE_HEIGHT)) {

            deltaVELOCITYPERPENDICULAR = -1;
            deltaSPEEDPERPENDICULAR = (eRestitution * (Math.abs(paddlePositionParallel + PADDLE_HEIGHT / 2 - ballPositionParallel)));

            if (paddleIsNotMoving) {}
            else if (paddlePositiveDirection == ballPositiveDirection) {

                deltaSPEEDPARALLEL = (PADDLE_SPEED) * (aRestitution * Math.abs(paddlePositionParallel + PADDLE_HEIGHT / 2 - ballPositionParallel));
                deltaVELOCITYPARALLEL = 1;

            } else {
                deltaSPEEDPARALLEL = (PADDLE_SPEED) * (aRestitution * Math.abs(paddlePositionParallel + PADDLE_HEIGHT / 2 - ballPositionParallel));
                deltaVELOCITYPARALLEL = -1;
            }

            return true;
        }

        return false;
    }

    public boolean detectCollisionWithWallAndUpdateParameters(double ballPositionPerpendicular) {
        if (ballPositionPerpendicular < 0) {

            deltaVELOCITYPERPENDICULAR = -1;

            return true;
        }

        return false;
    }

    public boolean detectCollisionWithOtherBallAndUpdateParameters(Ball ball1, Ball ball2) {


        if ( Math.sqrt(Math.pow(ball1.getBallPositionX() - ball2.getBallPositionX(),2) + Math.pow(ball1.getBallPositionY() - ball2.getBallPositionY(),2))<= BALL_HEIGHT) {

            double ball1VX = ball1.getBallVelocityX();
            double ball1VY = ball2.getBallVelocityY();
            double ball2VX = ball2.getBallVelocityX();
            double ball2VY = ball2.getBallVelocityY();

            double ball1SX = ball1.getBallSpeedX();
            double ball1SY = ball1.getBallSpeedY();
            double ball2SX = ball2.getBallSpeedX();
            double ball2SY = ball2.getBallSpeedY();

            if(ball1VX * ball2VX * ball1VY * ball2VY==-1) {
                if (ball1VX*ball2VX==-1) {
                    ball1.updateBallVelocity(ball1VX*-1, ball1VY);
                    ball2.updateBallVelocity(ball2VX*-1, ball2VY);
                } else if (ball1VY*ball2VY==-1) {
                    ball1.updateBallVelocity(ball1VX, ball1VY*-1);
                    ball2.updateBallVelocity(ball2VX, ball2VY*-1);
                }
            } else if (ball1VX*ball2VX==-1 && ball1VY*ball2VY==-1) {
                ball1.updateBallVelocity(ball1VX*-1, ball1VY*-1);
                ball2.updateBallVelocity(ball2VX*-1, ball2VY*-1);
            } else {
                if (ball1SX > ball2SX && ball1SY > ball2SX) {
                    ball1.updateBallSpeed(ball2SX,ball2SY);
                    ball2.updateBallSpeed(ball1SX, ball1SY);
                } else if (ball1SX > ball2SX && ball1SY < ball2SY ) {
                    ball1.updateBallSpeed(ball2SX, ball1SY);
                    ball2.updateBallSpeed(ball1SX, ball2SY);
                } else if (ball1SX < ball2SX && ball1SY > ball2SY) {
                    ball1.updateBallSpeed(ball1SX, ball2SY);
                    ball2.updateBallSpeed(ball2SX, ball1SY);
                } else if (ball1SX < ball1SY && ball1SY < ball2SY) {
                    ball1.updateBallSpeed(ball2SX, ball2SY);
                    ball2.updateBallSpeed(ball1SX, ball1SY);
                }
            }

            return true;
        }

        return false;
    }

    public double getDeltaSPEEDPERPENDICULAR() {
        double temp = this.deltaSPEEDPERPENDICULAR;
        this.deltaSPEEDPERPENDICULAR = 0;
        return temp;
    }
    public double getDeltaSPEEDPARRALEL() {
        double temp = this.deltaSPEEDPARALLEL;
        this.deltaSPEEDPARALLEL = 0;
        return temp;
    }
    public double getDeltaVELOCITYPERPENDICULAR() {
        double temp = this.deltaVELOCITYPERPENDICULAR;
        this.deltaVELOCITYPERPENDICULAR = 1;
        return temp;
    }
    public double getDeltaVELOCITYPARALLEL() {
        double temp = this.deltaVELOCITYPARALLEL;
        this.deltaVELOCITYPARALLEL = 1;
        return temp;
    }



}
