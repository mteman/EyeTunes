/* *****************************************************************************
 *  Compilation:  javac CombineClasses.java
 *  Execution: java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses [-p] 
 *  [<midifile.mid>]
 *  
 *  CombineClasses reads data from MIDI (Musical Instrument Digital Interface)
 *  input, fetches the associated visual characteristics from the database
 *  initialized by a CreateDB object, instantiates Shape subclass objects that
 *  correspond with these visual characteristics, draws the Shapes, and plays
 *  the MIDI sounds using the MIDI sequencer. CombineClasses has instance fields
 *  midiMessageQueue (a LinkedBlockingDeque of MidiMessages that queues the
 *  MidiMessages produced by the MIDI transmitter), sequencer (the Java MIDI
 *  Sequencer), db (a CreateDB object that initializes the database), currProg
 *  (an int that tracks the current program number, or instrument), and notes 
 *  (an ArrayList that stores all of the currently playing notes as Note objects).
 * 
 *  MIDI message reading and playing adapted from MidiSource.java by Alan Kaplan
 *  and Nico Toy, written for Princeton University's COS 126 course.
 *  https://www.cs.princeton.edu/courses/archive/spr23/cos126/static/assignments/guitar/guitar.zip
 *
 *  By Morgan Teman
 **************************************************************************** */

import javax.sound.midi.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.Color;

public final class CombineClasses {

    /* 
    *
    *
    *       INSTANCE VARIABLES
    *
    *
    */

    // keep track if source is "live" controller or static file
    private static final int MIDI_CONTROLLER = 0;
    private static final int MIDI_FILE = 1;
    private int sourceType;

    // queue for midi messages produced by MIDI transmitter (keyboard controller or sequencer)
    private LinkedBlockingDeque<MidiMessage> midiMessageQueue;
    private MidiDevice    device;       // hardware keyboard controller
    private Sequencer     sequencer;    // Java MIDI sequencer

    private boolean verbose = false;    // indicates if MidiSource should print information
                                        // about MidiMessages to stdout as messages are
                                        // produced

    private boolean playSynth = false;  // indicates if MidiSource should play notes using
                                        // default Java Synthesizer as messages are 
                                        // produced


    private static CreateDB db; // database or programvisuals and colornotes
    private static int currProg; // current program number (for adding to hashmaps)
    // ARRAYLIST INSPIRED BY https://github.com/wizardwalk/midi-animator 
    private static List<Note> notes; // ArrayList of notes (prog, chan, note, vel)

    // constants
    private static final int MIDI_END_OF_TRACK = 47; // MetaMessage end of track event
    private static final double MAXNUM = 128.0; // max number of 127 velocities or pitches

    // short message field names for helper print method
    // adapted from MidiSource.java
    private static final HashMap<Integer, String> SM_FIELDS = 
    CombineClasses.setShortMessageFields();
    private static HashMap<Integer, String> setShortMessageFields() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        Field[] declaredFields = ShortMessage.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    map.put(field.getInt(null), field.getName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return map;
    }

    /**
     * Helper method - prints a MidiMessage
     * adapted from MidiSource.java, updated to print information pertinent to
     * EyeTunes. Useful for examining MIDI file contents before running
     */
    private static void print(MidiMessage message) {
        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            // some controllers continuously output 240 
            if (shortMessage.getCommand() != 240) { 
                System.out.print("ShortMessage: ");
                System.out.print(" Command: " + SM_FIELDS.get(shortMessage.getCommand()) +
                                 " (" + shortMessage.getCommand() + ") ");
                System.out.print(" Channel: " + getChannel(shortMessage));
                if (shortMessage.getCommand() ==  ShortMessage.NOTE_ON) {
                    int pitch = getPitch(shortMessage);
                    int velocity = getVelocity(shortMessage);
                    System.out.print(" Pitch:   " + pitch);
                    System.out.print(" Velocity: " + velocity);
                }
                else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                    System.out.print(" Pitch:   " + getPitch(shortMessage));
                    System.out.print(" Velocity: " + getVelocity(shortMessage));
                }
                else if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
                    System.out.print(" Number:   " + shortMessage.getData1());
                    System.out.print(" Data2:    " + shortMessage.getData2());
                } else if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                    // https://www.songstuff.com/recording/article/midi_message_format/
                    int chan = shortMessage.getData2(); // channel number
                    int prog = shortMessage.getData1(); // program number
                    System.out.println("Channel: " + chan);
                    System.out.println("Program: " + prog);
                } else {
                    System.out.print(" Data1:    " + shortMessage.getData1());
                    System.out.print(" Data2:    " + shortMessage.getData2());
                }
                System.out.println();
            }
            else if (message instanceof SysexMessage) {
                System.out.println("SysexMessage");
            }
            else if (message instanceof MetaMessage) {
                System.out.print("MetaMessage: ");
                MetaMessage metaMessage = (MetaMessage) message;
                System.out.println(metaMessage.getType());                  
            }
        }
    }

    
    /* 
    *
    *
    *       PRIVATE CLASSES
    *
    *
    */

    /**
     * Private helper class that receives MidiMessages from MIDI Keyboard, 
     * and adds each MIDI message received to a MidiMessage queue. As messages
     * are added to the MidiMessage queue, also updates notes ArrayList for
     * graphics generation. Optionally (1) prints messages to terminal and 
     * (2) plays messages using Java Synthesizer. Adapted from MidiSource.java
     * and updated with graphics generation data structures.
     */
    private class MidiKeyboardControllerReceiver implements Receiver {
        private boolean       verbose   = false; // default - do not print message
        private boolean       playSynth = false; // default - do not play synthesizer
        private Synthesizer   synth     = null;  // default Java Synthesizer
        private MidiChannel[] channels  = null;  // defaul - Java Sythesizer channels
        public MidiKeyboardControllerReceiver(boolean verbose, boolean playSynth) {
            midiMessageQueue = new LinkedBlockingDeque<MidiMessage>();
            this.verbose   = verbose;
            this.playSynth = playSynth;

            // if this Receiver needs to play notes, set up channels
            if (playSynth) {
                try {
                    synth = MidiSystem.getSynthesizer();
                }
                catch (MidiUnavailableException e) { 
                    e.printStackTrace();
                    System.exit(1);
                }
                try {
                    synth.open();
                }
                catch (MidiUnavailableException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                channels = synth.getChannels();
            }
        }

        @Override
        // Invoked each time Receiver gets a MidiMessage
        public void send(MidiMessage message, long timeStamp) {
            // add the message to the queue
            midiMessageQueue.add(message);
            
            // update notes for graphics generation
            if (message instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) message;
                if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                    // https://www.songstuff.com/recording/article/midi_message_format/
                    currProg = shortMessage.getData1(); // program number
                }
                else if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                    Note noteOn = new Note(currProg, getChannel(shortMessage), 
                    getPitch(shortMessage), getVelocity(shortMessage));
                    notes.add(noteOn);
                }
                else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                    int thisChan = getChannel(shortMessage);
                    int thisPitch = getPitch(shortMessage);
                    for (int i = 0; i < notes.size(); i++) {
                        Note check = notes.get(i);
                        int checkProg = check.getProg();
                        int checkChan = check.getChan();
                        int checkPitch = check.getPitch();
                        if (checkProg == currProg && checkChan == thisChan 
                        && checkPitch == thisPitch) notes.remove(check);
                    }
                }
            }

            // print message
            if (verbose)
                print(message);

            // play this note for a keyboard controller
            if (playSynth)
                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                    if (shortMessage.getCommand() == ShortMessage.NOTE_ON) 
                        channels[getChannel(shortMessage)].noteOn(getPitch(shortMessage),
                        getVelocity(shortMessage));
                    else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                        channels[getChannel(shortMessage)].noteOff(getPitch(shortMessage),
                        getVelocity(shortMessage));
                    }
                }
        }
                                                                   
        // close the Receiver stream
        public void close() {
            synth.close();
            midiMessageQueue = null;
        }
    }

    /**
     * Private helper class that receives MidiMessages from MIDI file input, 
     * and adds each MIDI message received to a MidiMessage queue. As messages
     * are added to the MidiMessage queue, also updates notes ArrayList for
     * graphics generation. Optionally prints messages to terminal. Adapted from
     * MidiSource.java and updated with graphics generation data structures.
     */
    private class MidiFileReceiver implements Receiver {
        private boolean     verbose    = false; // default - do not print message
        public MidiFileReceiver(boolean verbose) {
            midiMessageQueue = new LinkedBlockingDeque<MidiMessage>();
            this.verbose   = verbose;
        }

        @Override
        // Invoked each time Receiver gets a MidiMessage
        public void send(MidiMessage message, long timeStamp) {

            // add the message to the queue
            midiMessageQueue.add(message);

            // update notes for graphics generation
            if (message instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) message;
                if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                    https://www.songstuff.com/recording/article/midi_message_format/
                    currProg = shortMessage.getData1(); // program number
                }
                else if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                    Note noteOn = new Note(currProg, getChannel(shortMessage),
                    getPitch(shortMessage), getVelocity(shortMessage));
                    notes.add(noteOn);
                }
                else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                    int thisChan = getChannel(shortMessage);
                    int thisPitch = getPitch(shortMessage);
                    for (int i = 0; i < notes.size(); i++) {
                        Note check = notes.get(i);
                        int checkProg = check.getProg();
                        int checkChan = check.getChan();
                        int checkPitch = check.getPitch();
                        if (checkProg == currProg && checkChan == thisChan
                        && checkPitch == thisPitch) notes.remove(check);
                    }
                }
            }
            // print message?
            if (verbose)
                print(message);
        }                                
                            
        // close the Receiver stream
        public void close() {
            midiMessageQueue = null;
        }
    }

    /**
     * Search for connected Midi Keyboard controller. If found, returns an
     * opened MidiDevice. Adapted from MidiSource.java.
     *
     * @param verbose          log information about the device to stdout
     */
    private static MidiDevice openMidiController(boolean verbose) {

        // set default StdDraw color
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        // get installed Midi devices 
        MidiDevice.Info deviceInfo[] = MidiSystem.getMidiDeviceInfo();
        MidiDevice device = null;
        for (int i = 0; i < deviceInfo.length; i++) {
            if (verbose) {
                System.out.print("DEVICE " + i + ": ");
                System.out.print(deviceInfo[i].getName()   + ", ");
                System.out.print(deviceInfo[i].getVendor() + ", ");
                System.out.print(deviceInfo[i].getDescription() + ", ");
            }
            try {
                device = MidiSystem.getMidiDevice(deviceInfo[i]);
                if (verbose)
                    System.out.print("Midi device available, ");
            } catch (MidiUnavailableException e) {
                if (verbose)
                    System.out.println("Midi unavailable, trying next...");
                continue;
            }

            // To detect if a MidiDevice represents a hardware MIDI port:
            // https://docs.oracle.com/javase/7/docs/api/javax/sound/midi/MidiDevice.html
            if ( ! (device instanceof Sequencer) && ! (device instanceof Synthesizer)) {
                if (!(device.isOpen())) {
                    try {
                        device.open();
                    } catch (MidiUnavailableException e) {
                        if (verbose)
                            System.out.println("Unable to open Midi device, trying next...");
                        continue;
                    }
                }

                // check for a valid Transmitter
                try {
                    Transmitter transmitter = device.getTransmitter();
                } catch (MidiUnavailableException e) {
                    if (verbose)
                        System.out.println("Failed to get transmitter, trying next...");
                    device.close();
                    continue;
                }
                if (verbose)
                    System.out.println("Valid MIDI controller connected.");
                break;
            }
            else {
                if (verbose)
                    System.out.println("Not a MIDI keyboard controller, trying next...");
                device = null;
            }
        }
        return device;
    }


    /* 
    *
    *
    *       CONSTRUCTORS
    *
    *
    */

    /**
     * Creates a CombineClasses object that listens to the first found connected MIDI
     * input device (MIDI Keyboard). Adapted from MidiSource.java and updated with 
     * graphics generation data structures. Sets StdDraw canvas size, and initializes
     * audiovisual database and notes ArrayList.
     *
     * @param verbose true turns on logging
     * @param connectToSynth use default Java sound synthesizer
     * @throws RuntimeException if no device was found or if writing to the log
     *                          file failed
     */
    public CombineClasses(boolean verbose, boolean connectToSynth) {
        // graphic is 16:9 aspect ratio, shapes are 1280:720 but buffered canvas is 1360:765
        StdDraw.setCanvasSize(1360, 765);        

        MidiDevice  keyboard = openMidiController(verbose);
        if (keyboard == null)
            throw new RuntimeException("Unable to connect to a MIDI keyboard controller.");

        try {
            Transmitter transmitter = keyboard.getTransmitter();
            transmitter.setReceiver(new MidiKeyboardControllerReceiver(verbose, connectToSynth));
            sourceType = MIDI_CONTROLLER;
        }
        catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }

        db = new CreateDB();
        notes = new ArrayList<Note>();
    }

    /**
     * Creates a CombineClasses object that produces MIDI messages from a 
     * time-stamped MIDI file, where each message is buffered and becomes 
     * available for consumption by the client once it is "played" from the
     * file. Adapted from MidiSource.java and updated with graphics generation
     * data structures. Sets StdDraw canvas size, and initializes audiovisual 
     * database and notes ArrayList.
     * 
     * @param filename          the name of the file to play from
     * @param verbose true turns on logging
     * @param connectToSynth    true if Sequencer should connect to Sequencer 
     *                          (use default Java sound synthesizer)
     * @throws RuntimeException if the file is not found or not a valid MIDI
     *                          file, if reading from the file failed, or if 
     *                          writing to the log file failed
     */
    public CombineClasses(String filename, boolean verbose, boolean connectToSynth) {
        // animation is 16:9 aspect ratio; shapes are 1280:720, buffered canvas is 1360:765
        StdDraw.setCanvasSize(1360, 765);
        
        playSynth  = connectToSynth;
        sourceType = MIDI_FILE;
        try {
            sequencer  = MidiSystem.getSequencer(connectToSynth);
        }
        catch  (MidiUnavailableException e) {
            e.printStackTrace();
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }

        // connect file to sequencer
        try {
            sequencer.setSequence(fileInputStream);
            sequencer.getTransmitter().setReceiver(new MidiFileReceiver(verbose));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filename);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid MIDI file: " + filename);
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI unavailable: " + filename);
        }
        
        try {
            // Add a listener for meta message events
            sequencer.addMetaEventListener(new MetaEventListener() {
                    public void meta(MetaMessage event) {
                        // close the Sequencer when done
                        if (event.getType() == MIDI_END_OF_TRACK) {
                            // Sequencer is done playing
                            close();
                        }
                    }
                });
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        db = new CreateDB();
        notes = new ArrayList<Note>();
    }


    /* 
    *
    *
    *       METHODS
    *
    *
    */

    /**
     * Static helper method. Extract the pitch from a short
     * MIDI message, where commmand == NOTE_ON or NOTE_OFF
     *
     * @param message ShortMessage object
     * @return key code number
     */
    public static int getPitch(ShortMessage message) {
        return message.getData1();
    }
    
    /**
     * Static helper method. Extract the velocity from a short
     * MIDI message, where commmand == NOTE_ON or NOTE_OFF
     *
     * @param message ShortMessage object
     * @return key code number
     */
    public static int getVelocity(ShortMessage message) {
        return message.getData2();
    }

    /**
     * Static helper method. Extract the channel from a short
     * MIDI message.
     *
     * @param message ShortMessage object
     * @return channel number
     */
    public static int getChannel(ShortMessage message) {
        return message.getChannel();
    }

    /**
     * Starts the MIDI source so it can produce messages. Adapted from MidiSource.java.
     *
     */
    public void start () {
        if (sourceType == MIDI_CONTROLLER) {
        }

        else if (sourceType == MIDI_FILE) {
            sequencer.start();
        }
        else throw new RuntimeException("MidiSource: Illegal source type: " + sourceType);
    }

    /**
     * Either stop listening for input from the device or stop playback from
     * the MIDI file.
     */
    public void close() {
        if (sourceType == MIDI_CONTROLLER && device.isOpen()) {
            device.close();
        }
        else if (sourceType == MIDI_FILE) {
            sequencer.stop();
            sequencer.close();
        }
    }

    /**
     * Return whether this MIDI source is still active, adapted from MidiSource.java.
     *
     * @return if listening from device, true if and only if this instance is
     *         still listening; if using from file, true if and only if the
     *         playback is still active
     */
    public boolean isActive() {
        if (sourceType == MIDI_CONTROLLER) {
            return device.isOpen();
        }
        else if (sourceType == MIDI_FILE) {
            return sequencer.isRunning();
        }
        else {
            return false;
        }
    }

    /**
     * Return whether there are new MidiMessages available.
     *
     * @return true if and only if there are new messages available to consume
     */
    public boolean isEmpty() {
        return midiMessageQueue.isEmpty();
    }

    /**
     * Return the next available MIDI ShortMessage (in FIFO order) from MidiMessageQueue. 
     * Generates current state of graphics first, then removes messages from queue until
     * it encounters a ShortMessage. Uses a ControllerEventListener to detect control
     * change, which checks the type of message and generates updated graphics. Returns
     * found ShortMessage or null if queue is empty.
     *
     * @return The next available {@link MidiMessage}
     */
    private ShortMessage getMidiMessage() {
        // https://www.tabnine.com/code/java/methods/javax.sound.midi.Sequencer/addMetaEventListener
        int[] allControllersMask = new int[128];
        for (int i = 0; i < allControllersMask.length; i++) {
            allControllersMask[i] = i;
        }
        graphics();
        while (!this.isEmpty()) {
            MidiMessage message = midiMessageQueue.remove();
            if (message instanceof ShortMessage) {
                ShortMessage m = (ShortMessage) message;
                sequencer.addControllerEventListener(new ControllerEventListener() {
                    public void controlChange(ShortMessage m) {
                        if (m.getCommand() == ShortMessage.NOTE_ON || 
                        m.getCommand() == ShortMessage.NOTE_OFF || 
                        getVelocity(m) > -1) graphics();
                    }   
                }, allControllersMask);
                return m;
            }
        }
        return null; // if empty
    }

    /**
     * Graphics generator. First enables double buffering for faster shape drawing and clears
     * screen to default black background. Iterates through notes ArrayList and for each note,
     * extracts pitch, velocity, program number, and channel number. Uses these values to 
     * select from the database to get color as an RGB value, shape, and quadrant. Sets pen 
     * color to new Color object initialized from RGB values. Uses velocity to calculate size,
     * and pitch and quadrant to calculate coordinates. Creates corresponding Shape object 
     * from shape text value, with newly calcuated coordinates and dimensions, and calls draw
     * method of specific Shape. After all shapes have been drawn, calls StdDraw's show() 
     * method to display entire frame.
     */
    public void graphics() {
        // clear screen to black background
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);

        Connection connection = null; // to connect to database

        // iterate through notes ArrayList
        for (int i = 0; i < notes.size(); i++) {
            // all values to be assigned to database query results
            boolean perc = false; // percussion
            String color = "";
            String shape = "";
            String quadrant = "";
            int r = 0;
            int g = 0;
            int b = 0;

            // note values to query database
            Note note = notes.get(i);
            int prog = note.getProg();
            if (note.getChan() == 9) perc = true;
            else perc = false;
            int n = note.getPitch();

            // query database (programvisuals) for color, shape, quadrant
            String s = "select * from programvisuals where percussion = " + perc +
            " and program = " + prog;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:mappings.db");
                Statement statement = connection.createStatement();
                ResultSet rs1 = statement.executeQuery(s);
                while (rs1.next()) {
                    // result set - program number, percussion, color, shape, quadrant
                    int pr = rs1.getInt("program");
                    boolean pe = rs1.getBoolean("percussion");
                    color = rs1.getString("color");
                    shape = rs1.getString("shape");
                    quadrant = rs1.getString("quadrant");
                }
            }
            catch (SQLException e) {
                // if the error message is "out of memory",
                // it probably means no database file is found
                System.err.println(e.getMessage());
            }
            finally {
                try {
                    if (connection != null)
                        connection.close();
                }
                catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }

            // query database (colornotes) for r, g, b
            String s2 = "select * from colornotes where color = '" + color + "' and note = " + n;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:mappings.db");
                Statement statement2 = connection.createStatement();
                ResultSet rs2 = statement2.executeQuery(s2);
                while (rs2.next()) {
                    // result set - color, note, r, g, b
                    String co = rs2.getString("color");
                    int no = rs2.getInt("note");
                    r = rs2.getInt("r");
                    g = rs2.getInt("g");
                    b = rs2.getInt("b");
                }
            }
            catch (SQLException e) {
                // if the error message is "out of memory",
                // it probably means no database file is found
                System.err.println(e.getMessage());
            }
            finally {
                try {
                    if (connection != null)
                        connection.close();
                }
                catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }

            // new color object from RGB in database, set pen color to draw shape
            Color changed = new Color(r, g, b);
            StdDraw.setPenColor(changed);

            // calculate size
            double length = (note.getVel() / MAXNUM * 2048 / (1360.0 * 4)); 
            // divide by 4 to keep within buffer border, otherwise too big and goes off edges
            // velocity 0-127, 2048 Hz

            // calculate coordinates
            // if no quadrant, then X and Y are proportional to pitch across entire canvas
            double x = (double) n / MAXNUM; // default
            double y = (double) n / MAXNUM; // default

            // rescale coordinates by quadrant
            if (quadrant.equals("A")) {
                x = (160.0 / 1360.0) * x + 40.0 / 1360.0;
                y = (120.0 / 765.0) * y + 600.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("B")) {
                x = (160.0 / 1360.0) * x + 40.0 / 1360.0;
                y = (120.0 / 765.0) * y + 480.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("C")) {
                x = (320.0 / 1360.0) * x + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 240.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("D")) {
                x = (320.0 / 1360.0) * x + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 22.5 / 765.0;   
            } else if (quadrant.equals("E")) {
                x = (160.0 / 1360.0) * x + 160.0 / 1360.0 + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 480.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("F")) {
                x = (440.0 / 1360.0) * x + 320.0 / 1360.0 + 40.0 / 1360.0;
                y = (480.0 / 765.0) * y + 240.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("G")) {
                x = (960.0 / 1360.0) * x + 320.0 / 1360.0 + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 22.5 / 765.0;
            } else if (quadrant.equals("H")) {
                x = (240.0 / 1360.0) * x + 800.0 / 1360.0 + 40.0 / 1360.0;
                y = (480.0 / 765.0) * y + 240.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("I")) {
                x = (240.0 / 1360.0) * x + 1040.0 / 1360.0 + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 360.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("J")) {
                x = (120.0 / 1360.0) * x + 1040.0 / 1360.0 + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 240.0 / 765.0 + 22.5 / 765.0;
            } else if (quadrant.equals("K")) {
                x = (120.0 / 1360.0) * x + 1160.0 / 1360.0 + 40.0 / 1360.0;
                y = (240.0 / 765.0) * y + 240.0 / 765.0 + 22.5 / 765.0;
            }

            // create and draw corresponding Shape subclass object
            if (shape.equals("square")) {
                Square square = new Square(x, y, length);
                square.draw();
            }
            else if (shape.equals("horizontal rectangle")) {
                HorizontalRectangle hr = new HorizontalRectangle(x, y, length);
                hr.draw();
            }
            else if (shape.equals("right diagonal rectangle")) {
                RightDiagonalRectangle rdr = new RightDiagonalRectangle(x, y, length);
                rdr.draw();
            }
            else if (shape.equals("left diagonal rectangle")) {
                LeftDiagonalRectangle ldr = new LeftDiagonalRectangle(x, y, length);
                ldr.draw();
            }
            else if (shape.equals("circle")) {
                Circle c = new Circle(x, y, length);
                c.draw();
            }
            else if (shape.equals("squiggle")) {
                Squiggle sq = new Squiggle(x, y, length);
                sq.draw();
            }
            else if (shape.equals("sawtooth")) {
                Sawtooth st = new Sawtooth(x, y, length);
                st.draw();
            }
        }
        // show all drawn shapes in frame
        StdDraw.show();
    }


    /* 
    *
    *
    *       MAIN METHOD
    *
    *
    */

   /**
     * Tests this {@code CombineClasses} data type.
     *  To test a MIDI keyboard controller connected to a computer:
     *     java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses [-p]
     *  To test a MIDI file:
     *     java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses [-p] <midifile.mid>
     * where:
     *     -classpath ".:sqlite-jdbc-mappings.jar" -  connects to the mappings database
     * and the optional argument:
     *     -p -  indicates that the default JavaMIDI Synthesizer will 
     *           be used to play notes
     *       *** While this is an optional argument, it is recommended to run CombineClasses
     *       with -p to get the complete audiovisual experience, though the option to exclude
     *       -p opens use cases in which the user only wants to view the visualization. ***
     * and the argument:
     *     <midifile.mid> - name of MIDI file`
     * 
     * Thus, the full usage is:
     *     java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses [-p] [<midifile.mid>]
     * 
     * @param args the command-line arguments
     */
     public static void main(String args[]) {
        String USAGE = "java -classpath \".:sqlite-jdbc-mappings.jar\" CombineClasses [-p] [<midifile.mid>]";
        // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses [-p] [<midifile.mid>]
        String PLAY  = "-p";
        boolean VERBOSE = false;
        CombineClasses source = null;

        System.out.println("Welcome to EyeTunes!");

        StdDraw.setCanvasSize(); // canvas by default is 512 x 512 pixels

        // make this receiver listen for input from first MIDI input device found
        // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses
        if (args.length == 0) {
            source = new CombineClasses(VERBOSE, false);
        }
        else if (args.length == 1) {
            // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses -p
            if (args[0].equals(PLAY))                        
                source = new CombineClasses(VERBOSE, true);
            // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses <midifile.mid>
            else {
                source = new CombineClasses(args[0], VERBOSE, false);
                source.start();
                while (source.isActive()) {
                    MidiMessage message = source.getMidiMessage();
                }
            }
        }
        else if (args.length == 2) {
            // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses -p <midifile.mid>
            if (args[0].equals(PLAY)) {
                source = new CombineClasses(args[1], VERBOSE, true);
                source.start();
                while (source.isActive()) {
                    MidiMessage message = source.getMidiMessage();
                }
            }
            // java -classpath ".:sqlite-jdbc-mappings.jar" CombineClasses <midifile.mid> -p
            else if (args[1].equals(PLAY)) {
                source = new CombineClasses(args[0], VERBOSE, true);
                source.start();
                while (source.isActive()) {
                    MidiMessage message = source.getMidiMessage();
                }
            }
            else
                System.out.println(USAGE);
        }
        else
            System.out.println(USAGE);
    }
}