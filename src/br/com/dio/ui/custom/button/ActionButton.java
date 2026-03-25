package br.com.dio.ui.custom.button;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class ActionButton extends JButton {

    public ActionButton(final String text, final ActionListener actionListener) {
        this.setText(text);
        this.addActionListener(actionListener);
    }
}
