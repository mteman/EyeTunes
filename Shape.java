/* *****************************************************************************
 *  Compilation:  javac Shape.java
 *  Execution:    java Shape
 *
 *  The Shape Abstract Data Type defines the main coordinates and dimensions of
 *  the individual visuals. Subclasses inherit getter methods that provide the
 *  central coordinate and the length, setter methods that can change these
 *  values, and the draw method, which draws the polygon from its coordinates.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

public interface Shape {
    // get coordinates
    public double getX();
    public double getY();
    public double getLen();
    public double[] getXCoords();
    public double[] getYCoords();

    // set coordinates
    public void setX(double newX);
    public void setY(double newY);
    public void setLen(double newLen);

    // draw the shape
    public void draw();
}
