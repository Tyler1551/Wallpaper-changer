package main.java;

import java.awt.*;
import java.awt.event.*;

public class ActionListeners implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Frame frame = new Frame();

        frame.setSize(600, 600);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        frame.setVisible(true);
    }
}
