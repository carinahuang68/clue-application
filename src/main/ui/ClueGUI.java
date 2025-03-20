package ui;

import org.w3c.dom.events.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;
import persistence.JsonReader;
import persistence.JsonWriter;
import model.Detective;
import model.Card;

public class ClueGUI extends JPanel implements ActionListener {
    JFrame frame = new JFrame("Clue Application");
    ClueApp c;
    private static final int frameWidth = 350;
    private static final int frameHeight = 200;

    public ClueGUI() {
        startPopUpWindow();
        openClueWindow();
    }

    public void openClueWindow() {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // display JFrame to center of screen
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }

    // @SuppressWarnings("methodlength")
    public void startPopUpWindow() {
        JDialog dialog = new JDialog((Frame) null, "Welcome to Clue!", true);
        dialog.setSize(300, 180);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null); // Center on screen

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(2, 1));
        messagePanel.add(new JLabel("Please select an option:", SwingConstants.CENTER));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        JButton loadButton = new JButton("Load Game");
        JButton newGameButton = new JButton("New Game");

        buttonPanel.add(loadButton);
        buttonPanel.add(newGameButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> {
            dialog.dispose();
            loadGame();
        });

        newGameButton.addActionListener(e -> {
            dialog.dispose();  
            setUpGame();
        });

        dialog.setVisible(true);
    }

    public void setUpGame() {
        numPlayerSelectionDialog();
        playerNamesInputDialog();
    }

    public void numPlayerSelectionDialog() {
        // // Create dialog
        // JDialog dialog = new JDialog((Frame) null, "Select Number of Players", true);
        // dialog.setSize(300, 150);
        // dialog.setLayout(new BorderLayout());
        // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // dialog.setLocationRelativeTo(null); // Center on screen

        // // Create panel for dropdown
        // JPanel panel = new JPanel(new FlowLayout());
        // JLabel label = new JLabel("Select number of players: ");

        // // Create JComboBox with player options (3-6)
        // Integer[] playerOptions = {3, 4, 5, 6};
        // JComboBox<Integer> playerDropdown = new JComboBox<>(playerOptions);

        // panel.add(label);
        // panel.add(playerDropdown);
        // panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // // Create submit button
        // JButton submitButton = new JButton("Next");
        // submitButton.addActionListener(e -> {
        //     numPlayers = (Integer) playerDropdown.getSelectedItem(); // Get selected value
        //     JOptionPane.showMessageDialog(dialog, "You selected " + numPlayers + " players.");
        //     dialog.dispose(); // Close dialog
        // });

        // // Add components to dialog
        // dialog.add(panel, BorderLayout.CENTER);
        // dialog.add(submitButton, BorderLayout.SOUTH);
        // dialog.setVisible(true);

        // // Example of using selected value after dialog closes
        // System.out.println("Selected players: " + numPlayers);
    }

    public void playerNamesInputDialog() {
        // JTextField[] nameFields = new JTextField[numPlayers];
        // JPanel panel = new JPanel(new GridLayout(numPlayers + 1, 1, 5, 5));

        // panel.add(new JLabel("Enter player names:"));
        // for (int i = 0; i < numPlayers; i++) {
        //     nameFields[i] = new JTextField();
        //     panel.add(new JLabel("Player " + (i + 1) + ":"));
        //     panel.add(nameFields[i]);
        // }

        // int result = JOptionPane.showConfirmDialog(null, panel, 
        //     "Enter Player Names", JOptionPane.OK_CANCEL_OPTION);
        
        // if (result == JOptionPane.OK_OPTION) {
        //     String[] playerNames = new String[numPlayers];
        //     for (int i = 0; i < numPlayers; i++) {
        //         playerNames[i] = nameFields[i].getText().trim();
        //         if (playerNames[i].isEmpty()) {
        //             playerNames[i] = "Player " + (i + 1); // Default name if empty
        //         }
        //     }
        //     return playerNames;
        // }
        // return null; // User canceled
    }

    public void loadGame() {
        // stub
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

}
