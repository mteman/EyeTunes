/* *****************************************************************************
 *  Compilation:  javac RightDiagonalRectangle.java
 *  Execution:    java RightDiagonalRectangle
 *
 *  The RightDiagonalRectangle class inherits the Shape Abstract Data Type and 
 *  defines the getter methods that provide the central coordinate and the 
 *  length, the setter methods that can change these values, and the draw 
 *  method, which draws the right diagonal rectangle from its coordinates. It 
 *  also defines methods that calculate the half height and the coordinates of 
 *  the perimeter of the shape to be called in the draw method. Its instance 
 *  variables are the central x- and y-coordinate, the half length, the half 
 *  height, and the angle of rotation.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class RightDiagonalRectangle implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double halfLength; // half length
    private double halfHeight; // half height
    // constants
    private final int ANGLE = 45;

    // constructor
    public RightDiagonalRectangle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.halfLength = radius / 2;
        this.halfHeight = radius / 4;
    }

    // getters
    // get x coordinate - center
    public double getX() {
        return x;
    }

    // get y coordinate - center
    public double getY() {
        return y;
    }

    // get halflength
    public double getLen() {
        return halfLength;
    }

    // get halfheight
    public double getHeight() {
        return halfHeight;
    }

    // Returns four points in clockwise order starting from the top left.
        // (x + r1x, y + r1y),
        // (x + r2x, y + r2y),
        // (x - r1x, y - r1y),
        // (x - r2x, y - r2y);
    // get array of x-coordinates to draw (clockwise bottom left)
    public double[] getXCoords() {
        double c = Math.cos(ANGLE);
        double s = Math.sin(ANGLE);
        
        double r1x = -halfLength * c - halfHeight * s;
        double r2x =  halfLength * c - halfHeight * s;

        double[] coords = {x + r1x, x + r2x, x - r1x, x - r2x};
        return coords;
    }

    // get array of y-coordinates to draw (clockwise bottom left)
    public double[] getYCoords() {
        double c = Math.cos(ANGLE);
        double s = Math.sin(ANGLE);
        
        double r1y = -halfLength * s + halfHeight * c;
        double r2y =  halfLength * s + halfHeight * c;

        double[] coords = {y + r1y, y + r2y, y - r1y, y - r2y};
        return coords;
    }

    // setters
    // set x coordinate - center
    public void setX(double newX) {
        x = newX;
    }

    // set y coordinate - center
    public void setY(double newY) {
        y = newY;
    }

    // set halflength
    public void setLen(double newLen) {
        halfLength = newLen / 4;
    }

    // set halfheight
    public void setHeight(double newHeight) {
        halfHeight = newHeight / 4;
    }

    // draw the shape
    public void draw() {
        StdDraw.filledPolygon(getXCoords(), getYCoords());
    }
    
}
