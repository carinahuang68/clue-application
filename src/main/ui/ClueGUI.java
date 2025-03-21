package ui;

import org.w3c.dom.events.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;
import persistence.JsonReader;
import persistence.JsonWriter;
import model.Detective;
import model.Card;
import model.ClueGame;

public class ClueGUI extends JPanel {
    private String filePath;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JFrame frame = new JFrame("Clue Application");
    private ClueGame game;
    private static final int frameWidth = 350;
    private static final int frameHeight = 200;
    private int numPlayers;
    private String[] playerNames;
    private String myName;
    private String[] myHandCards;
    private int numHandCardsPerPlayer;
    private List<String> orderedPlayers;
    private String currentInput;

    public ClueGUI() {
        setUp();
    }

    // @SuppressWarnings("methodlength")
    public void setUp() {
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
            load();
            orderPlayers();
            openClueWindow();
        });

        newGameButton.addActionListener(e -> {
            dialog.dispose();
            setUpNewGame();
            orderPlayers();
            openClueWindow();
        });
        dialog.setVisible(true);
    }

    public void setUpNewGame() {
        numPlayerSelectionDialog();
        playerNamesInputDialog();
        showInstructionsDialog();
        myHandCardsInputDialog();
        game = new ClueGame(myName, playerNames, myHandCards);
        String d = game.getDetective().getName();
        int num = game.getNumPlayers() - 1;
        System.out.println("Created new game with detective " + d + " and " + num + " other players.");
    }

    public void numPlayerSelectionDialog() {
        // Create panel for dropdown
        JPanel panel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Select number of players: ");

        // Create JComboBox with player options (3-6)
        Integer[] playerOptions = { 3, 4, 5, 6 };
        JComboBox<Integer> playerDropdown = new JComboBox<>(playerOptions);

        panel.add(label);
        panel.add(playerDropdown);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JOptionPane.showConfirmDialog(null, panel, "Number of Players", JOptionPane.DEFAULT_OPTION);

        numPlayers = (Integer) playerDropdown.getSelectedItem();

        // Example of using selected value after dialog closes
        System.out.println("Selected players: " + numPlayers);
        System.out.println();
    }

    public void playerNamesInputDialog() {
        JPanel panel = new JPanel(new GridLayout(numPlayers + 1, 1, 5, 5));
        panel.add(new JLabel("Enter your name: "));
        JTextField myNameField = new JTextField();
        panel.add(myNameField);
        JTextField[] nameFields = new JTextField[numPlayers - 1];
        for (int n = 1; n < numPlayers; n++) {
            panel.add(new JLabel("Enter player " + n + "'s name: "));
            int i = n - 1;
            nameFields[i] = new JTextField();
            panel.add(nameFields[i]);
        }

        JOptionPane.showConfirmDialog(null, panel, "Enter Player Names", JOptionPane.DEFAULT_OPTION);

        myName = myNameField.getText().trim();
        if (myName.isEmpty()) {
            myName = "d"; // default
        }
        System.out.println("My name: " + myName);
        int numOpponents = numPlayers - 1;
        playerNames = new String[numOpponents];
        System.out.print("Players: ");
        for (int i = 0; i < numOpponents; i++) {
            playerNames[i] = nameFields[i].getText().trim();
            if (playerNames[i].isEmpty()) {
                int n = i + 1;
                playerNames[i] = "" + n; // Default name if empty
            }
            System.out.print(playerNames[i] + " ");
        }
        System.out.println();
    }

    public void showInstructionsDialog() {
        numHandCardsPerPlayer = (Card.NAMES.length - 3) / numPlayers;
        int numCardsInRooms = (Card.NAMES.length - 3) % numPlayers;
        String instructions = "Set up instructions:\n"
                + "1. Randomly pick one secret suspect card, one secret weapon card, and one secret room card.\n"
                + "2. Place the 3 secret cards in the confidential folder, without looking at the cards!\n"
                + "3. Shuffle the remaining cards.\n"
                + "4. Distribute " + numHandCardsPerPlayer + " cards to each player, including you.\n";
        if (numCardsInRooms != 0) {
            instructions += "5. Hide each of the " + numCardsInRooms + " remaining cards in a room.\n";
        }
        JOptionPane.showMessageDialog(null, instructions);
    }

    public void myHandCardsInputDialog() {
        boolean validInput = false;
        while (!validInput) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use vertical layout for stacking components

            // Create JTextArea for the card names reference
            JTextArea cardNamesTextArea = new JTextArea(5, 42); // 5 rows, 30 columns
            cardNamesTextArea.setText(getCardNames());
            cardNamesTextArea.setWrapStyleWord(true); // Enable word wrapping
            cardNamesTextArea.setLineWrap(true); // Enable line wrapping
            cardNamesTextArea.setEditable(false); // Make it non-editable
            JScrollPane scrollPane = new JScrollPane(cardNamesTextArea);
            panel.add(scrollPane);

            JTextField[] handCardFields = new JTextField[numHandCardsPerPlayer];
            panel.add(new JLabel(""));
            for (int n = 1; n <= numHandCardsPerPlayer; n++) {
                panel.add(new JLabel("Enter your hand card #" + n + ": "));
                handCardFields[n - 1] = new JTextField(10);
                panel.add(handCardFields[n - 1]);
            }

            JOptionPane.showConfirmDialog(null, panel, "Enter Your Hand Cards", JOptionPane.DEFAULT_OPTION);

            myHandCards = new String[numHandCardsPerPlayer];
            validInput = true;
            for (int i = 0; i < numHandCardsPerPlayer; i++) {
                myHandCards[i] = handCardFields[i].getText().trim();

                // checks if input is empty
                if (myHandCards[i].isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Card names cannot be empty! \n Please re-enter all cards.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    validInput = false; // Restart input
                    break;
                }

                // checks if input is not a valid card name
                if (!Card.contains(myHandCards[i])) {
                    JOptionPane.showMessageDialog(null,
                            "You have one invalid card name! \n Please re-enter all cards.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    validInput = false; // Restart input
                    break;
                }
            }
        }

        System.out.print("My hand cards: ");
        for (String card : myHandCards) {
            System.out.print(card + " ");
        }
        System.out.println();

    }

    private String getCardNames() {
        String cardNames = "Card names reference: \n" +
                "Suspects: " + Suspect.names() + "\n" +
                "Weapons: " + Weapon.names() + "\n" +
                "Rooms: " + Room.names();
        return cardNames;
    }

    private void load() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a JSON File to Load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File("data")); // Opens the "data" folder inside the project
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files (*.json)", "json"));

        int result = fileChooser.showOpenDialog(null); // Show dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = "data/" + selectedFile.getName();
            jsonWriter = new JsonWriter(filePath);
            jsonReader = new JsonReader(filePath);
            System.out.println("Selected file: " + filePath);
            try {
                Detective detective = jsonReader.readDetective();
                System.out.println("Loaded " + detective.getName() + " from " + filePath);
                List<Player> players = jsonReader.readPlayers();
                System.out.println("Loaded " + players + " from " + filePath);
                game = new ClueGame(detective, players);
                myName = game.getDetective().getName();
                System.out.println("Saved players to game");
            } catch (IOException e) {
                System.out.println("Unable to read from file: " + filePath);
            }
        } else {
            System.out.println("File selection canceled.");
        }
    }

    public void orderPlayers() {
        orderedPlayers = new ArrayList<>();
        JOptionPane.showMessageDialog(null, "Time to input player order!");
        currentInput = JOptionPane.showInputDialog("Enter first player");
        System.out.println("Num players = " + game.getNumPlayers());
        addPlayerToOrderedPlayer();
        for (int i = 2; i <= game.getNumPlayers(); i++) {
            currentInput = JOptionPane.showInputDialog("Enter next player");
            addPlayerToOrderedPlayer();
        }
        System.out.println("Ordered players: " + orderedPlayers);
        System.out.println("All set! Ready to start the game!");
        System.out.println();
    }

    /*
     * MODIFIES: this
     * EFFECTS:
     * if player name is valid, sets currentPlayer as player and adds currentPlayer
     * to orderPlayers
     * if player name is invalid, lets user enter name again until currentPlayer is
     * valid
     */
    public void addPlayerToOrderedPlayer() {
        while ((game.getPlayer(currentInput) == null && !myName.equals(currentInput))
                | orderedPlayers.contains(currentInput)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid player name!",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    currentInput = JOptionPane.showInputDialog("Please enter again: ");
        }
        orderedPlayers.add(currentInput);
        System.out.println();
    }

    public void openClueWindow() {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // display JFrame to center of screen
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }

    public void save() {
        if (filePath == null) {
            JTextField fileNameField = new JTextField();
            JPanel panel = new JPanel(new GridLayout(2, 1));
            panel.add(new JLabel("Enter new file name (without .json):"));
            panel.add(fileNameField);

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "Save New Game", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION || fileNameField.getText().trim().isEmpty()) {
                System.out.println("Save canceled.");
                return; // Exit if user cancels or enters nothing
            }

            // Create the full file path inside "data" folder
            filePath = "data/" + fileNameField.getText().trim() + ".json";
        }

        try {
            jsonWriter.open();
            jsonWriter.write(game.getDetective(), game.getPlayers());
            jsonWriter.close();
            System.out.println("Saved detective and players to " + filePath);
            System.out.println();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + filePath);
        }
    }

}

// @Override
// public void actionPerformed(ActionEvent e) {
// // Auto-generated method stub
// throw new UnsupportedOperationException("Unimplemented method
// 'actionPerformed'");
// }
