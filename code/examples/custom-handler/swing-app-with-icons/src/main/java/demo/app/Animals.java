package demo.app;

import demo.app.icons.MeowIcon;
import demo.app.icons.WoofIcon;

import javax.swing.*;
import java.awt.*;

public class Animals {

    public static void main(String...args) {
        create().setVisible(true);
    }

    static JFrame create() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container panel = frame.getContentPane();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Click a button:"));
        panel.add(button(MeowIcon.load(), "Cat", "Meow!"));
        panel.add(button(WoofIcon.load(), "Dog", "Woof!"));
        frame.pack();
        return frame;
    }

    private static JButton button(ImageIcon icon, String text, String msg) {
        JButton button = new JButton(text, icon);
        button.addActionListener(e -> JOptionPane.showMessageDialog(button, msg));
        return button;
    }
}
