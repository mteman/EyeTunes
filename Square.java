/* *****************************************************************************
 *  Compilation:  javac Square.java
 *  Execution:    java Square
 *
 *  The Square class inherits the Shape Abstract Data Type and defines the 
 *  getter methods that provide the central coordinate and the length, the 
 *  setter methods that can change these values, and the draw method, which
 *  draws the square from its coordinates. It also defines methods that
 *  calculate the coordinates of the perimeter of the shape to be called in
 *  the draw method. Its instance variables are the central x- and y-
 *  coordinate and the half length.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class Square implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double halfLength; // half length

    // constructor
    public Square(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.halfLength = radius / 2;
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

    // get halflength (halfwidth and halfheight)
    public double getLen() {
        return halfLength;
    }

    // get array of x-coordinates to draw (clockwise bottom left)
    public double[] getXCoords() {
        double[] coords = {x - halfLength, x - halfLength, x + halfLength, x + halfLength};
        return coords;
    }

    // get array of y-coordinates to draw (clockwise bottom left)
    public double[] getYCoords() {
        double[] coords = {y - halfLength, y + halfLength, y + halfLength, y - halfLength};
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

    // set halflength (halfwidth and halfheight)
    public void setLen(double newLen) {
        halfLength = newLen / 2;
    }

    // draw the shape
    public void draw() {
        StdDraw.filledPolygon(getXCoords(), getYCoords());
    }
}
