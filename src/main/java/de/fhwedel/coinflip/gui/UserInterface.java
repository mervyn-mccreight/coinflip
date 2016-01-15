/*
 * Created by JFormDesigner on Fri Jan 15 21:49:53 CET 2016
 */

package de.fhwedel.coinflip.gui;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class UserInterface extends JFrame {
  public UserInterface() {
    initComponents();
  }

  private void playButtonOnClick(ActionEvent e) {
    appendLogTextTo(textPane1, "Bla");
  }

  private void appendLogTextTo(JTextPane textPane, String textToAdd) {
    StyledDocument styledDocument = textPane.getStyledDocument();
    try {
      styledDocument.insertString(styledDocument.getLength(),
          (styledDocument.getLength() == 0 ? "" : System.lineSeparator()) + textToAdd,
          textPane.getCharacterAttributes());
    } catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
    // Generated using JFormDesigner Evaluation license - Mervyn McCreight
    DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
    label1 = compFactory.createLabel("Server IP:");
    ipTextField = new JTextField();
    label2 = compFactory.createLabel("Server Port:");
    portTextField = new JTextField();
    playButton = new JButton();
    textPane1 = new JTextPane();
    CellConstraints cc = new CellConstraints();

    // ======== this ========
    setTitle("SRA CoinFlip Client");
    setResizable(false);
    Container contentPane = getContentPane();
    contentPane.setLayout(new FormLayout("2*(default, $lcgap), 62dlu, 3*($lcgap, default)",
        "7*(default, $lgap), 132dlu, $lgap, default"));

    // ---- label1 ----
    label1.setText("Server IP:");
    label1.setLabelFor(ipTextField);
    contentPane.add(label1, cc.xy(3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));

    // ---- ipTextField ----
    ipTextField.setToolTipText("Enter Server-IP here ...");
    contentPane.add(ipTextField, cc.xy(5, 3));

    // ---- label2 ----
    label2.setLabelFor(portTextField);
    contentPane.add(label2, cc.xy(3, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));

    // ---- portTextField ----
    portTextField.setToolTipText("Enter Server-Port here...");
    contentPane.add(portTextField, cc.xy(5, 5));

    // ---- playButton ----
    playButton.setText("Play");
    playButton.addActionListener(this::playButtonOnClick);
    contentPane.add(playButton, cc.xy(5, 9));

    // ---- textPane1 ----
    textPane1.setEditable(false);
    textPane1.setFocusable(false);
    textPane1.setFocusCycleRoot(false);
    textPane1.setRequestFocusEnabled(false);
    contentPane.add(textPane1, cc.xywh(3, 15, 7, 1, CellConstraints.FILL, CellConstraints.FILL));
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
  // Generated using JFormDesigner Evaluation license - Mervyn McCreight
  private JLabel label1;
  private JTextField ipTextField;
  private JLabel label2;
  private JTextField portTextField;
  private JButton playButton;
  private JTextPane textPane1;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}
