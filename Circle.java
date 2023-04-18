/* *****************************************************************************
 *  Compilation:  javac Circle.java
 *  Execution:    java Circle
 *
 *  The Circle class inherits the Shape Abstract Data Type and defines the 
 *  getter methods that provide the central coordinate and the length, the 
 *  setter methods that can change these values, and the draw method, which
 *  draws the circle from its coordinate and radius. It also defines methods 
 *  that recalculate the coordinate of the center of the shape to be called 
 *  in the draw method. Its instance variables are the central x- and y-
 *  coordinate and the radius (half length).
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class Circle implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double radius; // half length (actual radius)

    // constructor
    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius / 2;
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

    // get radius
    public double getLen() {
        return radius * 2;
    }

    // get array of x-coordinates to draw (just center)
    public double[] getXCoords() {
        double[] coords = {x};
        return coords;
    }

    // get array of y-coordinates to draw (just center)
    public double[] getYCoords() {
        double[] coords = {y};
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

    // set radius
    public void setLen(double newLen) {
        radius = newLen / 2;
    }

    // draw the shape
    public void draw() {
        StdDraw.filledCircle(x, y, radius);
    }
}
