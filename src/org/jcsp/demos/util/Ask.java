    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.demos.util;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import org.jcsp.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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

        final One2OneChannel frameEvent = Channel.createOne2One(new
                OverWritingBuffer(1));
        final One2OneChannel frameConfig = Channel.createOne2One(new
                OverWritingBuffer(1));
        final One2OneChannel okEvent = Channel.createOne2One(new
                OverWritingBuffer(1));
        final One2OneChannel okConfig = Channel.createOne2One(new
                OverWritingBuffer(1));
        final One2OneChannel cancelEvent = Channel.createOne2One(new
                OverWritingBuffer(1));
        final One2OneChannel cancelConfig = Channel.createOne2One(new
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
                        if (e.getID() != e.WINDOW_CLOSING)break;
                    case 1:
                        System.exit(0);
                    }
                }
            }
        }
        });
        boolean valid = false;
        do {
            frame.show();
            par.run();
            frame.hide();
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

}
