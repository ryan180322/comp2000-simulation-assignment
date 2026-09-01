import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;

@SuppressWarnings("unused")
public class Main extends JPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Forest Fire Simulation");
        
        // testing, might constantly change between them
        frame.setSize(800, 600);
        //frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}