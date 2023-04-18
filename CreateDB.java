/* *****************************************************************************
 *  Compilation:  javac CreateDB.java
 *  Execution:    java -classpath ".:sqlite-jdbc-mappings.jar" CreateDB
 *
 *  A CreateDB object creates a SQLite database, mappings.db, containing the two
 *  tables, programvisuals and colornotes. programvisuals contains the fields
 *  (program integer, percussion boolean, color text, shape text, quadrant text).
 *  colornotes contains the fields (color text, note integer, r integer, g 
 *  integer, b integer).
 * 
 *  By Morgan Teman
 *
 **************************************************************************** */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// USAGE: java -classpath ".:sqlite-jdbc-mappings.jar" CreateDB
public class CreateDB {

    public CreateDB() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:mappings.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            // new tables
            statement.executeUpdate("drop table if exists programvisuals");
            statement.executeUpdate("create table programvisuals (program integer, percussion boolean, color text, shape text, quadrant text);");
            statement.executeUpdate("drop table if exists colornotes");
            statement.executeUpdate("create table colornotes (color text, note integer, r integer, g integer, b integer);");

            // programvisuals - prog, percussion, color, shape, quadrant
            // prog 0-5 (piano), perc false, red, square, C
            boolean percussion = false;
            for (int i = 0; i < 6; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'square', 'C');");
            }
            // prog 6 (harpsichord), perc false, orange, square, C
            statement.executeUpdate("insert into programvisuals values(6, " + percussion + ", 'orange', 'square', 'C');");
            // prog 7 (clavinet), perc false, yellow, square, C
            statement.executeUpdate("insert into programvisuals values(7, " + percussion + ", 'yellow', 'square', 'C');");
            // prog 8-15 (chromatic percussion), perc false, yellow, circle, E
            for (int i = 8; i < 16; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'circle', 'E');");
            }
            // prog 16-23 (organ), perc false, yellow, horizontal rectangle, C
            for (int i = 16; i < 24; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'horizontal rectangle', 'C');");
            }
            // prog 24-25 (acoustic guitar), perc false, green, horizontal rectangle, H
            for (int i = 24; i < 26; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'green', 'horizontal rectangle', 'H');");
            }
            // prog 26-30 (electric guitar), perc false, yellow, horizontal rectangle, F
            for (int i = 26; i < 31; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'horizontal rectangle', 'F');");
            }
            // prog 31 (guitar harmonic), perc false, green, horizontal rectangle, H
            statement.executeUpdate("insert into programvisuals values(31, " + percussion + ", 'green', 'horizontal rectangle', 'H');");
            // prog 32 (acoustic bass), perc false, green, squiggle, G
            statement.executeUpdate("insert into programvisuals values(32, " + percussion + ", 'green', 'squiggle', 'G');");
            // prog 33-34 (electric bass), perc false, red, squiggle, G
            for (int i = 33; i < 35; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'squiggle', 'G');");
            }            
            // prog 35 (fretless bass), perc false, green, squiggle, G
            statement.executeUpdate("insert into programvisuals values(34, " + percussion + ", 'green', 'squiggle', 'G');");
            // prog 36-37 (slap bass), perc false, orange, squiggle, G
            for (int i = 36; i < 38; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'orange', 'squiggle', 'G');");
            }
            // prog 38-39 (synth bass), perc false, blue, squiggle, G
            for (int i = 38; i < 40; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'blue', 'squiggle', 'G');");
            }
            // prog 40-41 (violin/viola), perc false, pink, left diagonal rectangle, A
            for (int i = 40; i < 42; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'pink', 'left diagonal rectangle', 'A');");
            }
            // prog 42 (cello), perc false, blue, left diagonal rectangle, H
            statement.executeUpdate("insert into programvisuals values(42, " + percussion + ", 'blue', 'left diagonal rectangle', 'H');");
            // prog 43 (contrabass), perc false, green, left diagonal rectangle, G
            statement.executeUpdate("insert into programvisuals values(43, " + percussion + ", 'green', 'left diagonal rectangle', 'G');");
            // prog 44-46 (strings), perc false, yellow, left diagonal rectangle, H
            for (int i = 44; i < 47; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'left diagonal rectangle', 'H');");
            }
            // prog 47 (timpani), perc false, green, left diagonal rectangle, H
            statement.executeUpdate("insert into programvisuals values(47, " + percussion + ", 'green', 'left diagonal rectangle', 'H');");
            // prog 48-49 (string ensemble), perc false, yellow, left diagonal rectangle, H
            for (int i = 48; i < 50; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'left diagonal rectangle', 'H');");
            }
            // prog 50-51 (synth strings), perc false, pink, horizontal rectangle, H
            for (int i = 50; i < 52; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'pink', 'horizontal rectangle', 'H');");
            }
            // prog 52-54 (voice ensemble), perc false, purple, horizontal rectangle, B
            for (int i = 52; i < 56; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'purple', 'horizontal rectangle', 'B');");
            }
            // prog 55 (orchestra hit), perc false, white, circle, N
            statement.executeUpdate("insert into programvisuals values(55, " + percussion + ", 'white', 'circle', 'N');");
            // prog 56 (trumpet), perc false, yellow, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(56, " + percussion + ", 'yellow', 'right diagonal rectangle', 'J');");
            // prog 57 (trombone), perc false, blue, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(57, " + percussion + ", 'blue', 'right diagonal rectangle', 'J');");
            // prog 58 (tuba), perc false, green, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(58, " + percussion + ", 'green', 'right diagonal rectangle', 'J');");
            // prog 59 (muted trumpet), perc false, yellow, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(59, " + percussion + ", 'yellow', 'right diagonal rectangle', 'J');");
            // prog 60 (french horn), perc false, blue, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(60, " + percussion + ", 'blue', 'right diagonal rectangle', 'J');");
            // prog 61 (brass section), perc false, orange, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(61, " + percussion + ", 'orange', 'right diagonal rectangle', 'J');");
            // prog 62-63 (synth brass), perc false, teal, horizontal rectangle, J
            for (int i = 62; i < 64; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'teal', 'horizontal rectangle', 'J');");
            }
            // prog 64-67 (saxophone), perc false, blue, right diagonal rectangle, J
            for (int i = 64; i < 68; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'blue', 'right diagonal rectangle', 'J');");
            }
            // prog 68 (oboe), perc false, green, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(68, " + percussion + ", 'green', 'right diagonal rectangle', 'J');");
            // prog 69-70 (horn/bassoon), perc false, blue, right diagonal rectangle, J
            for (int i = 69; i < 71; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'blue', 'right diagonal rectangle', 'J');");
            }
            // prog 71 (clarinet), perc false, purple, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(71, " + percussion + ", 'purple', 'right diagonal rectangle', 'J');");
            // prog 72-79 (pipes), perc false, pink, right diagonal rectangle, j
            for (int i = 72; i < 80; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'pink', 'right diagonal rectangle', 'J');");
            }
            // prog 80-84 (warm synths), perc false, pink, horizontal rectangle, D
            for (int i = 80; i < 85; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'pink', 'horizontal rectangle', 'D');");
            }
            // prog 85 (voice synth), perc false, purple, horizontal rectangle, B
            statement.executeUpdate("insert into programvisuals values(85, " + percussion + ", 'purple', 'horizontal rectangle', 'B');");
            // prog 86 (fifths synth), perc false, pink, horizontal rectangle, D
            statement.executeUpdate("insert into programvisuals values(86, " + percussion + ", 'pink', 'horizontal rectangle', 'D');");
            // prog 87-88 (brass synths), perc false, teal, horizontal rectangle, I
            for (int i = 87; i < 89; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'teal', 'horizontal rectangle', 'I');");
            }
            // prog 89 (warm synth), perc false, pink, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(89, " + percussion + ", 'pink', 'horizontal rectangle', 'I');");
            // prog 90 (polysynth), perc false, purple, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(90, " + percussion + ", 'purple', 'horizontal rectangle', 'I');");
            // prog 91 (choir), perc false, purple, horizontal rectangle, B
            statement.executeUpdate("insert into programvisuals values(91, " + percussion + ", 'purple', 'horizontal rectangle', 'B');");
            // prog 92 (bowed synth), perc false, pink, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(92, " + percussion + ", 'pink', 'horizontal rectangle', 'I');");
            // prog 93 (metallic synth), perc false, teal, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(93, " + percussion + ", 'teal', 'horizontal rectangle', 'I');");
            // prog 94 (halo synth), perc false, pink, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(94, " + percussion + ", 'pink', 'horizontal rectangle', 'I');");
            // prog 95-100 (shh sound fx), perc false, white, horizontal rectangle, K
            for (int i = 95; i < 101; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'white', 'horizontal rectangle', 'K');");
            }
            // prog 101-102 (voice sound fx), perc false, purple, horizontal rectangle, B
            for (int i = 101; i < 103; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'purple', 'horizontal rectangle', 'B');");
            }
            // prog 103 (sci-fi sound fx), perc false, teal, horizontal rectangle, I
            statement.executeUpdate("insert into programvisuals values(103, " + percussion + ", 'teal', 'horizontal rectangle', 'I');");
            // prog 104-108 (ethnic strings), perc false, green,left diagonal rectangle, H
            for (int i = 104; i < 109; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'green', 'left diagonal rectangle', 'H');");
            }
            // prog 109 (bagpipe), perc false, pink, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(109, " + percussion + ", 'pink', 'right diagonal rectangle', 'J');");
            // prog 110-111 (ethnic strings), perc false, green,left diagonal rectangle, H
            for (int i = 110; i < 112; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'green', 'left diagonal rectangle', 'H');");
            }
            // prog 112-113 (bells), perc false, yellow, circle, E
            for (int i = 112; i < 114; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'circle', 'E');");
            }
            // prog 114-117 (drums), perc false, red, circle, E
            for (int i = 114; i < 118; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'circle', 'E');");
            }
            // prog 118 (synth drum), perc false, purple, horizontal rectangle, E
            statement.executeUpdate("insert into programvisuals values(118, " + percussion + ", 'purple', 'horizontal rectangle', 'E');");
            // prog 119 (cymbal), perc false, orange, circle, E
            statement.executeUpdate("insert into programvisuals values(119, " + percussion + ", 'orange', 'circle', 'E');");
            // prog 120 (guitar fret noise), perc false, green, horizontal rectangle, F
            statement.executeUpdate("insert into programvisuals values(120, " + percussion + ", 'green', 'horizontal rectangle', 'F');");
            // prog 121-122 (shh sound fx), perc false, white, square, I
            for (int i = 121; i < 123; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'white', 'square', 'I');");
            }
            // prog 123 (bird tweet), perc false, pink, right diagonal rectangle, J
            statement.executeUpdate("insert into programvisuals values(123, " + percussion + ", 'pink', 'right diagonal rectangle', 'J');");
            // prog 124 (telephone ring), perc false, yellow, circle, J
            statement.executeUpdate("insert into programvisuals values(120, " + percussion + ", 'yellow', 'circle', 'J');");
            // prog 125-127 (loud percussion sound fx), perc false, white, circle, D
            for (int i = 125; i < 128; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'white', 'circle', 'D');");
            }

            // percussion (channel 9)
            percussion = true;
            // prog 33 (metronome click), perc true, red, circle, E
            statement.executeUpdate("insert into programvisuals values(33, " + percussion + ", 'red', 'circle', 'E');");
            // prog 34 (metronome bell), perc true, yellow, circle, J
            statement.executeUpdate("insert into programvisuals values(34, " + percussion + ", 'yellow', 'circle', 'J');");
            // prog 35-37 (drums), perc true, red, circle, E
            for (int i = 35; i < 38; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'circle', 'E');");
            }
            // prog 38 (acoustic snare), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(38, " + percussion + ", 'white', 'square', 'K');");
            // prog 39 (hand clap), perc true, white, circle, E
            statement.executeUpdate("insert into programvisuals values(39, " + percussion + ", 'white', 'circle', 'E');");
            // prog 40 (electric snare), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(40, " + percussion + ", 'white', 'square', 'K');");
            // prog 41 (low floor tom), perc true, red, circle, E
            statement.executeUpdate("insert into programvisuals values(41, " + percussion + ", 'red', 'circle', 'E');");
            // prog 42 (closed hi-hat), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(42, " + percussion + ", 'white', 'square', 'K');");
            // prog 43 (high floor tom), perc true, red, circle, E
            statement.executeUpdate("insert into programvisuals values(43, " + percussion + ", 'red', 'circle', 'E');");
            // prog 44 (pedal hi-hat), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(44, " + percussion + ", 'white', 'square', 'K');");
            // prog 45 (low tom), perc true, red, circle, E
            statement.executeUpdate("insert into programvisuals values(45, " + percussion + ", 'red', 'circle' ,'E');");
            // prog 46 (open hi-hat), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(46, " + percussion + ", 'white', 'square', 'K');");
            // prog 47-48 (low- & hi-mid tom), perc true, red, circle, E
            for (int i = 47; i < 49; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'circle', 'E');");
            }
            // prog 49 (crash cymbal 1), perc true, orange, circle, K
            statement.executeUpdate("insert into programvisuals values(49, " + percussion + ", 'orange', 'circle', 'K');");
            // prog 50 (high tom), perc true, red, circle, E
            statement.executeUpdate("insert into programvisuals values(50, " + percussion + ", 'red', 'circle', 'E');");
            // prog 51-52 (ride cymbal 1 & chinese cymbal), perc true, orange, circle, K
            for (int i = 51; i < 53; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'orange', 'circle', 'K');");
            }
            // prog 53 (ride bell), perc true, white, square, J
            statement.executeUpdate("insert into programvisuals values(53, " + percussion + ", 'white', 'square', 'J');");
            // prog 54 (tambourine), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(54, " + percussion + ", 'white', 'square', 'K');");
            // prog 55 (splash cymbal), perc true, orange, circle, K
            statement.executeUpdate("insert into programvisuals values(55, " + percussion + ", 'orange', 'circle', 'K');");
            // prog 56 (cowbell), perc true, orange, circle, J
            statement.executeUpdate("insert into programvisuals values(56, " + percussion + ", 'yellow', 'circle', 'J');");
            // prog 57 (crash cymbal 2), perc true, orange, circle, K
            statement.executeUpdate("insert into programvisuals values(57, " + percussion + ", 'orange', 'circle', 'K');");
            // prog 58 (vibraslap), perc true, red, sawtooth, E
            statement.executeUpdate("insert into programvisuals values(58, " + percussion + ", 'red', 'sawtooth', 'E');");
            // prog 59 (ride cymbal), perc true, white, square, K
            statement.executeUpdate("insert into programvisuals values(59, " + percussion + ", 'white', 'square', 'K');");
            // prog 60-64 (bongos & congas), perc true, orange, circle, E
            for (int i = 60; i < 65; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'orange', 'circle', 'E');");
            }
            // prog 65-66 (timbales), perc true, red, circle, E
            for (int i = 65; i < 67; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'circle', 'E');");
            }
            // prog 67-68 (agogos), perc true, yellow, circle, E
            for (int i = 67; i < 69; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'yellow', 'circle', 'E');");
            }
            // prog 69-70 (cabasa & maracas), perc true, white, square, K
            for (int i = 69; i < 71; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'white', 'square', 'K');");
            }
            // prog 71-72 (whistles), perc true, pink, right diagonal rectangle, J
            for (int i = 71; i < 73; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'pink', 'right diagonal rectangle', 'J');");
            }
            // prog 73-74 (guiros), perc true, red, sawtooth, E
            for (int i = 73; i < 75; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'sawtooth', 'E');");
            }
            // prog 75-77 (claves & wood blocks), perc true, red, circle, E
            for (int i = 75; i < 78; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'red', 'circle', 'E');");
            }
            // prog 78-79 (cuicas), perc true, blue, right diagonal rectangle, E
            for (int i = 78; i < 80; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'blue', 'right diagonal rectangle', 'E');");
            }
            // prog 80-81 (triangles), perc true, green, circle, J
            for (int i = 80; i < 82; i++) {
                statement.executeUpdate("insert into programvisuals values(" + i + ", " + percussion + ", 'green', 'circle', 'J');");
            }

            // colornotes - color, note, r, g, b
            // red
            for (int note = 0; note < 128; note++) {
                String color = "'red'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = (4 * note) + 3;
                    g = 0;
                    b = 0;
                } 
                else {
                    r = 255;
                    g = 4 * (note - 63) - 1;
                    b = 4 * (note - 63) - 1;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // orange
            for (int note = 0; note < 128; note++) {
                String color = "'orange'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = (4 * note) + 3;
                    g = 2 * note;
                    b = 0;
                }
                else {
                    r = 255;
                    g = 2 * (note - 64) + 128;
                    b = 4 * (note - 63) - 1;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // yellow
            for (int note = 0; note < 128; note++) {
                String color = "'yellow'"; 
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = 4 * note;
                    g = 4 * note;
                    b = 0;
                }
                else {
                    r = 255;
                    g = 255;
                    b = 4 * (note - 63) - 1;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // green
            for (int note = 0; note < 128; note++) {
                String color = "'green'"; 
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = 0;
                    g = (4 * note) + 3;
                    b = 0;
                }
                else {
                    r = 4 * (note - 63) - 1;
                    g = 255;
                    b = 4 * (note - 63) - 1;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // teal
            for (int note = 0; note < 128; note++) {
                String color = "'teal'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = 0;
                    g = (4 * note) + 3;
                    b = (4 * note) + 3;
                } 
                else {
                    r = 4 * (note - 63) - 1;
                    g = 255;
                    b = 255;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // blue
            for (int note = 0; note < 128; note++) {
                String color = "'blue'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = 0;
                    g = 0;
                    b = (4 * note) + 3;
                } 
                else {
                    r = 2 * (note - 63) - 1;
                    g = 4 * (note - 63) - 1;
                    b = 255;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // purple
            for (int note = 0; note < 128; note++) {
                String color = "'purple'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = 2 * note;
                    g = 0;
                    b = (4 * note) + 3;
                } 
                else {
                    r = 2 * (note - 64) + 128;
                    g = 4 * (note - 63) - 1;
                    b = 255;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // pink
            for (int note = 0; note < 128; note++) {
                String color = "'pink'";
                int r;
                int g; 
                int b;
                if (note < 64) {
                    r = (4 * note) + 3;
                    g = 0;
                    b = (2 * note) + 3;
                } 
                else {
                    r = 255;
                    g = 4 * (note - 63) - 1;
                    b = 2 * (note - 64) + 128;
                }
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
            // white
            for (int note = 0; note < 128; note++) {
                String color = "'white'";
                int r = 2 * note;
                int g = 2 * note; 
                int b = 2 * note;
                statement.executeUpdate("insert into colornotes values(" + color + ", " + note + ", " + r + ", " + g + ", " + b + ");");
            }
        }
        // exception handling
        catch (SQLException e) {
            // "out of memory" error message means no db file found
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (connection != null)
                    connection.close();
            }
            catch (SQLException e) {
                // connection close failed
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {

    }
}
