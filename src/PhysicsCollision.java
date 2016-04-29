/**
 * Created by Kshitiz Sharma on 28-Apr-16.
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

    public PhysicsCollision(int pane_x, int pane_y, double PADDLE_SPEED, int PADDLE_WIDTH, int PADDLE_HEIGHT, int BALL_HEIGHT, int BALL_WIDTH) {

//        if (pane_x==pane_y) {
//            this.pane_length = pane_x;
//        } else {
//            System.out.println("dimensions are different");
//        }

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
