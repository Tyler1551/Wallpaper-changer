package main.java;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

// TODO: 21/06/2021
/*
* Allow for UI to access file explorer and change the wallpaper_path variable
*
* Find a way to display updated weather conditions (with temperature) and potentially clock
*
* Allow for user to choose which wallpapers are displayed at certain times of day and under what weather conditions
*
*/

public class UI extends Frame {
    Button buttons[];

    public UI(){
        buttons = new Button[2];
        ArrayList<Button> buttons_array = new ArrayList<Button>();
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Customise wallpapers");
        labels.add("Current weather and location");

        for(int i = 0; i < buttons.length; i++){
            buttons[i] = new Button(labels.get(i));
            buttons_array.add(buttons[i]);
            this.add(buttons[i]);
        }

        Button customise_paper = buttons_array.get(0);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setSize(400,400);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }


}
