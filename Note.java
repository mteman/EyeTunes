/* *****************************************************************************
 *  Compilation:  javac Note.java
 *  Execution:    java Note
 *
 *  The Note class represents individual notes in a song, with the instance
 *  variables program (program number, determines color, shape, and quadrant),
 *  channel (channel number, determines percussion), pitch (pitch of the note,
 *  determines position and brightness), and velocity (volume, determines size).
 *  Note has four getter methods that return the values of these fields.
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

// Note data structure for ArrayList
public class Note {
    // instance variables
    private static int program; // determines color, shape, quadrant
    private static int channel; // determines percussion
    private static int pitch; // determines position, brightness
    private static int velocity; // determines size

    public Note(int prog, int chan, int p, int vel) {
        program = prog;
        channel = chan;
        pitch = p;
        velocity = vel;
    }

    public int getProg() {
        return program;
    }

    public int getChan() {
        return channel;
    }
    
    public int getPitch() {
        return pitch;
    }

    public int getVel() {
        return velocity;
    }

    public static void main(String[] args) {
        
    }
}
