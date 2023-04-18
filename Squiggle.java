/* *****************************************************************************
 *  Compilation:  javac Squiggle.java
 *  Execution:    java Squiggle
 *
 *  The Squiggle class inherits the Shape Abstract Data Type and defines the 
 *  getter methods that provide the central coordinate and the length, the 
 *  setter methods that can change these values, and the draw method, which
 *  draws the squiggle from its coordinates. It also defines methods that 
 *  calculate the coordinates of the points along the curves to be called 
 *  in the draw method. Its instance variables are the central x- and y-
 *  coordinate and the full length of all of the curves side by side.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public class Squiggle implements Shape {
    // instance variables
    private double x; // x coord
    private double y; // y coord
    private double len; // length of full curve

    // constructor
    public Squiggle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.len = radius;
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
        return len;
    }

    // get array of x-coordinates to draw (just center)
    public double[] getXCoords() {
        double[] coords = new double[51];
        for (int i = 0; i < 51; i++) {
            coords[i] = x - ((25.0 - i) / 25.0) * len;
        }
        return coords;
    }

    // get array of y-coordinates to draw (just center)
    public double[] getYCoords() {
        double[] xs = getXCoords();
        double[] coords = new double[51];
        for (int i = 0; i < 51; i++) {
            coords[i] = y - (1.0 / 25.0) * Math.sin(((xs[i] - (25.0 - i) / 25.0) / len));
        }
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
        len = newLen;
    }

    // draw the shape
    public void draw() {
        double[] xs = getXCoords();
        double[] ys = getYCoords();
        for (int i = 0; i < 49; i++) {
            StdDraw.line(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }
    }
}
