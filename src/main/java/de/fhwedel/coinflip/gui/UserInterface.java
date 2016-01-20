/*
 * Created by JFormDesigner on Fri Jan 15 21:49:53 CET 2016
 */

package de.fhwedel.coinflip.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.fhwedel.coinflip.CoinFlipClient;
import gr.planetz.impl.HttpPingingService;

public class UserInterface extends JFrame {
  private static final String BROKER_URI = "https://52.35.76.130:8443/broker/1.0/players";
  private final DefaultTableModel tableModel;
  private Optional<HttpPingingService> pingingService = Optional.empty();
  private Map<String, String> players = Maps.newHashMap();

  public UserInterface() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
    initComponents();
    tableModel = new DefaultTableModel(new Object[] {"Playername", "Address"}, 0);
    playerMap = new JTable(tableModel);
    playerMap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    playerMap.setShowVerticalLines(false);
    playerMap.setShowHorizontalLines(false);
    playerMap.getSelectionModel().addListSelectionListener(e -> {
      ListSelectionModel fuckyou = (ListSelectionModel) e.getSource();
      if (fuckyou.isSelectionEmpty()) {
        return;
      }

      Object valueAt = playerMap.getValueAt(playerMap.getSelectedRow(), 1);
      String url = (String) valueAt;
      ipTextField.setText(url.split(":")[0]);
      portTextField.setText(url.split(":")[1]);
    });
    scrollPane1.setViewportView(playerMap);
    startBroker();
  }

  private void startBroker() {
    try {
      pingingService = Optional
          .of(new HttpPingingService(BROKER_URI, "", "", "ssl-data/memc_keystore.jks", "secret"));
    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException
        | KeyManagementException e) {
      throw new RuntimeException(e);
    }
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

  private void refreshButtonClick(ActionEvent e) {
    playerMap.clearSelection();
    pingingService.ifPresent(p -> {
      try {
        Map<String, String> newPlayerMap = p.getPlayersDirectlyOverHttpGetRequest();

        players.clear();
        newPlayerMap.forEach((k, v) -> players.put(k, v));

        // update tablemodel
        if (tableModel.getRowCount() > 0) {
          for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
            tableModel.removeRow(i);
          }
        }

        players.forEach((k, v) -> tableModel.addRow(Lists.newArrayList(k, v).toArray()));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    });
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
    separator1 = compFactory.createSeparator("Available Players", SwingConstants.CENTER);
    scrollPane1 = new JScrollPane();
    playerMap = new JTable();
    refreshButton = new JButton();
    CellConstraints cc = new CellConstraints();

    // ======== this ========
    setTitle("SRA CoinFlip Client");
    setResizable(false);
    Container contentPane = getContentPane();
    contentPane.setLayout(
        new FormLayout("default, $lcgap, 40dlu, $lcgap, 62dlu, $lcgap, 41dlu, $lcgap, default",
            "8*(default, $lgap), $lgap, 10dlu, default, $lgap, 64dlu, 2*($lgap, default)"));

    // ---- label1 ----
    label1.setText("Server IP:");
    label1.setLabelFor(ipTextField);
    contentPane.add(label1, cc.xy(3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));

    // ---- ipTextField ----
    ipTextField.setToolTipText("Enter Server-IP here ...");
    contentPane.add(ipTextField, cc.xywh(5, 3, 3, 1));

    // ---- label2 ----
    label2.setLabelFor(portTextField);
    contentPane.add(label2, cc.xy(3, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));

    // ---- portTextField ----
    portTextField.setToolTipText("Enter Server-Port here...");
    contentPane.add(portTextField, cc.xywh(5, 5, 3, 1));

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
    contentPane.add(protocolProgessBar, cc.xywh(3, 13, 5, 1));

    // ---- progressLabel ----
    progressLabel.setText("Waiting...");
    progressLabel.setFont(UIManager.getFont("Label.font"));
    progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
    contentPane.add(progressLabel, cc.xywh(3, 15, 6, 1));
    contentPane.add(separator1, cc.xywh(1, 18, 9, 1));

    // ======== scrollPane1 ========
    {

      // ---- playerMap ----
      playerMap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      playerMap.setShowVerticalLines(false);
      playerMap.setShowHorizontalLines(false);
      scrollPane1.setViewportView(playerMap);
    }
    contentPane.add(scrollPane1, cc.xywh(3, 21, 5, 1));

    // ---- refreshButton ----
    refreshButton.setText("Refresh");
    refreshButton.addActionListener(this::refreshButtonClick);
    contentPane.add(refreshButton, cc.xy(5, 23));
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
  private JComponent separator1;
  private JScrollPane scrollPane1;
  private JTable playerMap;
  private JButton refreshButton;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}
