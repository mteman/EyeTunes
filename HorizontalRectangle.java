/* *****************************************************************************
 *  Compilation:  javac HorizontalRectangle.java
 *  Execution:    java HorizontalRectangle
 *
 *  The HorizontalRectangle class inherits the Shape Abstract Data Type and 
 *  defines the getter methods that provide the central coordinate and the length, 
 *  the setter methods that can change these values, and the draw method, which
 *  draws the horizontal rectangle from its coordinates. It also defines methods 
 *  that calculate the half height and the coordinates of the perimeter of the 
 *  shape to be called in the draw method. Its instance variables are the central x- and y-
 *  coordinate, the half length, and the half height.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class HorizontalRectangle implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double halfLength; // half length
    private double halfHeight; // half height

    // constructor
    public HorizontalRectangle(double x, double y, double radius) {
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

    // get array of x-coordinates to draw (clockwise bottom left)
    public double[] getXCoords() {
        double[] coords = {x - halfLength, x - halfLength, x + halfLength, x + halfLength};
        return coords;
    }

    // get array of y-coordinates to draw (clockwise bottom left)
    public double[] getYCoords() {
        double[] coords = {y - halfHeight, y + halfHeight, y + halfHeight, y - halfHeight};
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
