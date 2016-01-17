/*
 * Created by JFormDesigner on Fri Jan 15 21:49:53 CET 2016
 */

package de.fhwedel.coinflip.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.fhwedel.coinflip.CoinFlipClient;

public class UserInterface extends JFrame {
  public UserInterface() {
    initComponents();
  }

  private void playButtonOnClick(ActionEvent e) {
    playButton.setEnabled(false);
    progressLabel.setText("Status");
    protocolProgessBar.setValue(0);

    new Thread(() -> {
      CoinFlipClient coinFlipClient = new CoinFlipClient(Optional.ofNullable(progressLabel),
          Optional.ofNullable(protocolProgessBar));
      try {
        coinFlipClient.connect(InetAddress.getByName(ipTextField.getText()),
            Integer.valueOf(portTextField.getText())).play();
      } catch (UnknownHostException e1) {
        progressLabel.setText("Invalid Server hostname/ip.");
      }
      playButton.setEnabled(true);
    }).start();

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
    protocolProgessBar = new JProgressBar();
    progressLabel = new JLabel();
    CellConstraints cc = new CellConstraints();

    // ======== this ========
    setTitle("SRA CoinFlip Client");
    setResizable(false);
    Container contentPane = getContentPane();
    contentPane.setLayout(new FormLayout("2*(default, $lcgap), 62dlu, 3*($lcgap, default)",
        "8*(default, $lgap), 132dlu, $lgap, default"));

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
    playButton.setFocusable(false);
    playButton.setFocusPainted(false);
    playButton.addActionListener(this::playButtonOnClick);
    contentPane.add(playButton, cc.xy(5, 9));

    // ---- protocolProgessBar ----
    protocolProgessBar.setMaximum(9);
    protocolProgessBar.setStringPainted(true);
    protocolProgessBar.setFocusable(false);
    contentPane.add(protocolProgessBar, cc.xywh(3, 13, 7, 1));

    // ---- progressLabel ----
    progressLabel.setText("Status");
    progressLabel.setFont(new Font("Droid Sans Mono", Font.PLAIN, 10));
    progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
    contentPane.add(progressLabel, cc.xywh(3, 15, 7, 1));
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
  private JProgressBar protocolProgessBar;
  private JLabel progressLabel;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}
