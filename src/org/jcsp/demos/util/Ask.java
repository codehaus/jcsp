    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.demos.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jcsp.awt.ActiveButton;
import org.jcsp.awt.ActiveFrame;
import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.Parallel;
import org.jcsp.util.OverWritingBuffer;

/**
 * @author Quickstone Technologies Limited
 */
public class Ask
{
    private static Container buttonContainer = null;

    private static class Prompt {
        public final String label;
        public final boolean isNumber;
        public final int min;
        public final int max;
        public final int def;
        public int value;
        public String valueS;
        public TextField control;
        public Prompt(final String label, final int min, final int max,
                      final int def) {
            this.label = label;
            this.min = min;
            this.max = max;
            this.def = def;
            this.value = def;
            buttonContainer.add(new Label(label));
            buttonContainer.add(control = new TextField("" + def));
            this.isNumber = true;
        }

        public Prompt(final String label) {
            this.label = label;
            this.min = 0;
            this.max = 0;
            this.def = 0;
            this.valueS = "";
            buttonContainer.add(new Label(label));
            buttonContainer.add(control = new TextField(""));
            this.isNumber = false;
        }
    }


    private static Hashtable prompts = null;
    private static String title = "";
    private static String descr = "";

    public static void app(final String prompt, final String description) {
        int l = prompt.length();
        System.out.println("\nJCSP Demonstration - " + prompt);
        for (int i = -21; i < l; i++) {
            System.out.print("=");
        }
        System.out.println("\n" + description + "\n");
        title = "JCSP Demonstration - " + prompt;
        descr = description;
    }

    public static void addPrompt(final String prompt, final int min,
                                 final int max, final int def) {
        if (prompts == null) {
            buttonContainer = new Panel();
            buttonContainer.setLayout(new GridLayout(0, 2));
            prompts = new Hashtable();
        }
        prompts.put(prompt, new Prompt(prompt, min, max, def));
    }

    public static void addPrompt(final String prompt) {
        if (prompts == null) {
            buttonContainer = new Panel();
            buttonContainer.setLayout(new GridLayout(0, 2));
            prompts = new Hashtable();
        }
        prompts.put(prompt, new Prompt(prompt));
    }

    public static int readInt(final String prompt) {
        Prompt p = (Prompt) prompts.get(prompt);
        if (p == null)throw new Error("oops: " + prompt);
        return p.value;
    }

    public static String readStr(final String prompt) {
        Prompt p = (Prompt) prompts.get(prompt);
        if (p == null)throw new Error("oops: " + prompt);
        return p.control.getText().trim();
    }

    public static void show() {

        final One2OneChannel frameEvent = Channel.one2one(new
                OverWritingBuffer(1));
        final One2OneChannel frameConfig = Channel.one2one(new
                OverWritingBuffer(1));
        final One2OneChannel okEvent = Channel.one2one(new
                OverWritingBuffer(1));
        final One2OneChannel okConfig = Channel.one2one(new
                OverWritingBuffer(1));
        final One2OneChannel cancelEvent = Channel.one2one(new
                OverWritingBuffer(1));
        final One2OneChannel cancelConfig = Channel.one2one(new
                OverWritingBuffer(1));

        final ActiveFrame frame = new ActiveFrame(frameConfig.in(),
                                                  frameEvent.out(), title);
        final ActiveButton ok = new ActiveButton(okConfig.in(), okEvent.out(),
                                                 "Ok");
        final ActiveButton cancel = new ActiveButton(cancelConfig.in(),
                cancelEvent.out(), "Cancel");

        frame.setLayout(new BorderLayout());

        { // Put the title line in
            Label l = new Label(title, Label.CENTER);
            l.setFont(new Font("default", Font.BOLD, 16));
            frame.add(l, BorderLayout.NORTH);
        }

        { // Put the OK and CANCEL buttons in
            Container ctr = new Panel();
            ctr.add(ok);
            ctr.add(cancel);
            frame.add(ctr, BorderLayout.SOUTH);
        }

        {
            Container ctr = new Panel();
            ctr.setLayout(new BorderLayout());
            { // Put the description in
                TextArea ta = new TextArea(descr, 0, 0,
                                           TextArea.SCROLLBARS_NONE);
                Color c = ta.getBackground();
                ta.setEditable(false);
                ta.setBackground(c);
                ctr.add(ta, BorderLayout.NORTH);
            }
            if (prompts != null) {
                Container ctr2 = new Panel();
                ctr2.add(buttonContainer);
                // Put the main control grid in
                ctr.add(ctr2, BorderLayout.CENTER);
            }
            frame.add(ctr, BorderLayout.CENTER);
        }

        frame.pack();
        Parallel par = new Parallel(new CSProcess[] {
                                    frame,
                                    ok,
                                    cancel,
                                    new CSProcess() {
            public void run() {
                Alternative alt = new Alternative(new Guard[] {okEvent.in(),
                                                  cancelEvent.in(),
                                                  frameEvent.in()});
                boolean loop = true;
                while (loop) {
                    switch (alt.priSelect()) {
                    case 0:
                        okEvent.in().read();
                        okConfig.out().write(null);
                        cancelConfig.out().write(null);
                        frameConfig.out().write(null);
                        loop = false;
                        break;
                    case 2:
                        WindowEvent e = (WindowEvent) frameEvent.in().read();
                        if (e.getID() != WindowEvent.WINDOW_CLOSING)break;
                    case 1:
                        System.exit(0);
                    }
                }
            }
        }
        });
        boolean valid = false;
        do {
            frame.setVisible(true);
            par.run();
            frame.setVisible(false);
            valid = true;
            if (prompts != null) {
                for (Enumeration e = prompts.elements(); e.hasMoreElements(); ) {
                    Prompt p = (Prompt) e.nextElement();
                    if (p.isNumber) {
                        String t = p.control.getText();
                        if (t == null) {
                            valid = false;
                            p.control.setText("" + p.value);
                            continue;
                        }
                        t = t.trim();
                        if (t.equals("")) {
                            valid = false;
                            p.control.setText("" + p.value);
                            continue;
                        }
                        try {
                            int v = Integer.parseInt(t);
                            if ((v < p.min) || (v > p.max)) {
                                valid = false;
                                p.control.setText("" + p.value);
                                continue;
                            } else {
                                p.value = v;
                            }
                        } catch (NumberFormatException ex) {
                            valid = false;
                            p.control.setText("" + p.value);
                            continue;
                        }
                    }
                }
            }
        } while (!valid);
        par.releaseAllThreads();
    }

    public static void blank() {
        if (prompts != null) {
            prompts = null;
            buttonContainer = null;
        }
        System.out.println();
    }

//The above Ask class from Quickstone has been merged with the below Ask class from PHW:
    
    /**
     * This <TT>Ask</TT> class contains a set of static methods for safe interactive input
     * of individual primitive types.  They will not return until an acceptable
     * answer has been entered.
     * The implementation techniques draw on those in the `Java Gently' textbook
     * by Judy Bishop.
     *
     * @author P.H. Welch
     *
     */
    
    private static InputStreamReader isr = new InputStreamReader (System.in);
    private static BufferedReader in = new BufferedReader (isr);

    /**
     * Don't allow any instances!
     */
    private Ask () {
    }

    /**
     * <TT>Ask.Int</TT> issues the prompt and returns an int between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid int has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static int Int (String prompt, int min, int max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            int answer = Integer.valueOf (item.trim ()).intValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *integer* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Long</TT> issues the prompt and returns an long between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid long has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static long Long (String prompt, long min, long max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            long answer = Long.valueOf (item.trim ()).longValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *long* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Byte</TT> issues the prompt and returns a byte between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid byte has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static byte Byte (String prompt, byte min, byte max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            byte answer = Byte.valueOf (item.trim ()).byteValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *byte* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Short</TT> issues the prompt and returns a short between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid short has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static short Short (String prompt, short min, short max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            short answer = Short.valueOf (item.trim ()).shortValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *short* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Float</TT> issues the prompt and returns a float between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid float has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static float Float (String prompt, float min, float max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            float answer = Float.valueOf (item.trim ()).floatValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *float* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Double</TT> issues the prompt and returns a double between min and max inclusive.
     * Keyboard errors in typing the reply are all trapped.  The method will
     * not return until a valid double has been entered in the indicated range.
     *
     * @param prompt the string used to prompt for input.
     * @param min user input must have a value >= min.
     * @param max user input must have a value <= max.
     *
     * @return user input complying with the rules.
     */
    public static double Double (String prompt, double min, double max) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            double answer = Double.valueOf (item.trim ()).doubleValue ();
            if ((min <= answer) && (answer <= max)) return answer;
            System.out.println (" *** Please answer between " + min +
                                " and " + max);
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (NumberFormatException e2) {
          System.out.println (" *** Please type in an *double* ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Boolean</TT> issues the prompt and returns a boolean depending on the first
     * non-white-space character typed.  'y' or 'Y' cause true to be returned,
     * while 'n' or 'N' cause false to be returned.  Anything else is rejected.
     *
     * @param prompt the string used to prompt for input.
     *
     * @return whether the user typed <I>yes</I> or <I>no</I> (according to the above rules).
     */
    public static boolean Boolean (String prompt) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            char ch = item.charAt (0);
            if ((ch == 'y') || (ch == 'Y')) return true;
            if ((ch == 'n') || (ch == 'N')) return false;
            System.out.println (" *** Please answer yes or no ...");
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.Char</TT> issues the prompt and returns a char depending on the first
     * non-white-space character typed.  This character must be one in
     * the valid character array.  Anything else is rejected.
     *
     * @param prompt the string used to prompt for input.
     * @param valid the array of characters defining valid user responses.
     *
     * @return user input complying with the rules.
     */
    public static char Char (String prompt, char[] valid) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          StringTokenizer tokens = new StringTokenizer (line);
          String item = tokens.nextToken();
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only type in one item ...");
          } else {
            char ch = item.charAt (0);
            for (int i = 0; i < valid.length; i++) {
              if (ch == valid[i]) {
                System.out.print ("");  // JIT bug work-around ?!!
                return ch;
              }
            }
            System.out.print (" *** Please type one character from \"");
            for (int i = 0; i < valid.length; i++) {
              System.out.print (valid[i]);
            }
            System.out.println ("\" ...");
          }
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    // Void issues the prompt and returns when <return> is pressed.

    /**
     * <TT>Ask.Void</TT> issues the prompt and returns when <return> is pressed.
     *
     * @param prompt the string used to prompt for a <return>.
     */
    public static void Void (String prompt) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) return;
          StringTokenizer tokens = new StringTokenizer (line);
          if (oneTokenReply && tokens.hasMoreTokens ()) {
            System.out.println (" *** Please only press <return> ...");
          } else {
            return;
          }
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * <TT>Ask.string</TT> issues the prompt and returns the String that is typed.
     *
     * @param prompt the string used to prompt for input.
     *
     * @return whatever the user inputs.
     */
    public static String string (String prompt) {
      while (true) {
        System.out.print (prompt);
        System.out.flush ();
        try {
          String line = in.readLine ();
          if (line == null) throw new NoSuchElementException ();
          return line;
        } catch (NoSuchElementException e) {
          System.out.println ("\n *** Please type something in ...");
        } catch (IOException e) {
          System.out.println (" *** " + e);
          System.out.println (" *** abandoning program !!!\n");
          System.exit (1);
        }
      }
    }

    /**
     * If <TT>oneTokenReply</TT>, extra tokens in replies cause the input to be rejected.
     */
    private static boolean oneTokenReply = true;

    /**
     * <TT>Ask.setOneTokenReply (true)</TT> specifies that more than one token in
     * the user reply will be rejected, even if the first token is valid.
     * This is the default condition for this class.
     * <P>
     * <TT>Ask.setOneTokenReply (false)</TT> specifies that multiple tokens are
     * allowed, but that only the first will be processed.
     * <P>
     * The <TT>false</TT> allows user input to be documented with comments (in the
     * second and following tokens) explaining the meaning of the first token, which
     * contains the actual data.  This is useful when preparing user input as a file,
     * from which the <I>standard input</I> stream will later be redirected, so that
     * each line of input can be documented.
     *
     * @param b if true, the response must consist of a single token
     * - otherwise, multiple tokens are allowed but only the first will be processed.
     */
    public static void setOneTokenReply (boolean b) {
      oneTokenReply = b;
    }
    /**
     * <TT>Ask.getOneTokenReply</TT> returns whether multiple response tokens
     * will be rejected.
     *
     * @return whether multiple response tokens will be rejected.
     */
    public static boolean getOneTokenReply () {
      return oneTokenReply;
    }

    
    
}
