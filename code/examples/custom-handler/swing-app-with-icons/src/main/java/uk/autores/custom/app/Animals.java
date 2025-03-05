// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.custom.app;

import uk.autores.custom.app.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Animals {

    public static void main(String...args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> create().setVisible(true));
    }

    public static JFrame create() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container panel = frame.getContentPane();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Click a button:"));
        panel.add(button(Icons.meow(), "Cat", "Meow!"));
        panel.add(button(Icons.woof(), "Dog", "Woof!"));
        frame.pack();
        return frame;
    }

    private static JButton button(ImageIcon icon, String text, String msg) {
        JButton button = new JButton(text, icon);
        button.addActionListener(e -> JOptionPane.showMessageDialog(button, msg));
        return button;
    }
}
