/* *****************************************************************************
 *  Compilation:  javac Sawtooth.java
 *  Execution:    java Sawtooth
 *
 *  The Sawtooth class inherits the Shape Abstract Data Type and defines the 
 *  getter methods that provide the central coordinate and the length, the 
 *  setter methods that can change these values, and the draw method, which
 *  draws the sawtooth from its coordinates. It also defines methods that 
 *  calculate the coordinates of the points along the perimeter of the
 *  triangles to be called in the draw method. Its instance variables are 
 *  the central x- and y-coordinate, the quarter length (individual lengths of
 *  the triangles), and a factor of sqrt(3)/2, which is useful in calculating
 *  the heights of the triangles.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class Sawtooth implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double quartLen; // quarter of the length
    // constants
    private final double FACTOR = Math.sqrt(3) / 2;

    // constructor
    public Sawtooth(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.quartLen = radius / 4;
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
        return quartLen * 4;
    }

    // triangle 1 
        // (x - 2 * quartLen, y), 
        // (x - ((3.0) / 2) * quartLen, y + FACTOR * quartLen), 
        // (x - quartLen, y)
    // triangle 2 
        // (x - quartLen, y), 
        // (x - ((1.0) / 2) * quartLen, y + FACTOR * quartLen), 
        // (x, y)
    // triangle 3 
        // (x, y), 
        // (x + ((1.0) / 2) * quartLen, y + FACTOR * quartLen), 
        // (x + quartLen, y)
    // triangle 4 
        // (x + quartLen, y), 
        // (x + ((3.0) / 2) * quartLen, y + FACTOR * quartLen), 
        // (x + 2 * quartLen, y)
    // get array of x-coordinates to draw
    public double[] getXCoords() {
        double[] coords = {x - 2 * quartLen, x - ((3.0) / 2) * quartLen, 
            x - quartLen, x - quartLen, x - ((1.0) / 2) * quartLen, x, 
            x, x + ((1.0) / 2) * quartLen, x + quartLen, x + quartLen, 
            x + ((3.0) / 2) * quartLen, x + 2 * quartLen};
        return coords;
    }

    // get array of y-coordinates to draw
    public double[] getYCoords() {
        double[] coords = {y, y + FACTOR * quartLen, y, y, 
            y + FACTOR * quartLen, y, y, y + FACTOR * quartLen, 
            y, y, y + FACTOR * quartLen, y};
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
        quartLen = newLen / 4;
    }

    // draw the shape
    public void draw() {
        double[] xs = getXCoords();
        double[] ys = getYCoords();
        StdDraw.filledPolygon(xs, ys);
    }
}