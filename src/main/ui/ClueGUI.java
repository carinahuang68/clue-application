package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;
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

// @SuppressWarnings("methodlength")

public class ClueGUI extends JPanel {
    private String filePath;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JFrame frame = new JFrame("Clue Application");
    private static final int frameWidth = 800;
    private static final int frameHeight = 600;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel gamePanel;
    private JPanel playerTurnPanel;
    private JPanel notesPanel;
    private JPanel controlPanel;

    private ClueGame game;
    private int numPlayers;
    private String[] playerNames;
    private String myName;
    private String[] myHandCards;
    private int numHandCardsPerPlayer;
    private List<String> orderedPlayers;
    private String currentInput;

    public ClueGUI() {
        openClueWindow();
        setUp();
    }

    // opens a backgroud window
    public void openClueWindow() {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // display JFrame to center of screen
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
    }

    
     // EFFECTS: Either calls load() or setUpNewGame() to load game,
     //          then call orderPlayersANDStartGame()
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
            orderPlayersANDStartGame();
        });

        newGameButton.addActionListener(e -> {
            dialog.dispose();
            setUpNewGame();
            orderPlayersANDStartGame();
        });

        dialog.setVisible(true);
    }

    // EFFECTS: obtains initial game info through user's inputs
    //          then create new clue game
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

    
     // EFFECTS: lets user select number of players
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

    // EFFECTS: lets user input all player's name
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

    // EFFECTS: calculates number of hand cards per player and
    //          show instuctions to set up Clue game
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

    // EFFECTS: lets user input their hand cards
    public void myHandCardsInputDialog() {
        boolean validInput = false;
        while (!validInput) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use vertical layout for stacking components

            // Create JTextArea for the card names reference
            JTextArea cardNamesTextArea = new JTextArea(5, 35); // 5 rows, 30 columns
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

    /*
     * REQUIRES: loaded file must have initialized players and detective
     * MODIFIES: this
     * EFFECTS: loads detective and players from file
     */
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

    /*
     * EFFECTS: Let user input player order
     *          After order is set, calls setUpPanels(), then calls runGame()
     */
    public void orderPlayersANDStartGame() {
        orderedPlayers = new ArrayList<>();
        JOptionPane.showMessageDialog(null, "Time to input player order!");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Your name: " + myName);
        JLabel playerNames = new JLabel("Players names: " + game.getPlayers());
        JLabel message = new JLabel("Enter first player: ");
        panel.add(name);
        panel.add(playerNames);
        panel.add(new JLabel(" "));
        panel.add(message);

        currentInput = JOptionPane.showInputDialog(null, panel);
        System.out.println("Num players = " + game.getNumPlayers());
        addPlayerToOrderedPlayer();
        panel.remove(message);
        panel.add(new JLabel("Enter next player: "));
        for (int i = 2; i <= game.getNumPlayers(); i++) {
            currentInput = JOptionPane.showInputDialog(null, panel);
            addPlayerToOrderedPlayer();
        }
        System.out.println("Ordered players: " + orderedPlayers);
        setUpPanels();
        runGame();
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


    // EFFECTS: sets up main game panel
    public void setUpPanels() {
        // TODO: make clue GUI
        setUpMainFrame();
        setUpPlayerTurnPanel();
    }

    public void setUpMainFrame() {
        // South Panel (Contains two rows)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new EmptyBorder(5, 15, 10, 15));

        // Bottom Row (Quit, Label, Next)
        JPanel bottomRow = new JPanel(new BorderLayout());
        JButton quitButton = new JButton("Quit");
        JLabel statusLabel = new JLabel("Next player: ", JLabel.CENTER);
        JButton nextButton = new JButton("Next");

        bottomRow.add(quitButton, BorderLayout.WEST);
        bottomRow.add(statusLabel, BorderLayout.CENTER);
        bottomRow.add(nextButton, BorderLayout.EAST);

        // Add both rows to the south panel
        //southPanel.add(topRow, BorderLayout.NORTH);
        southPanel.add(bottomRow, BorderLayout.SOUTH);

        // Load and resize image
       ImageIcon originalIcon = new ImageIcon("image/detective.jpeg"); // Load image
       Image originalImage = originalIcon.getImage();
       Image resizedImage = originalImage.getScaledInstance(220, 300, Image.SCALE_SMOOTH); // Resize to 200x300
       ImageIcon resizedIcon = new ImageIcon(resizedImage);

       JLabel imageLabel = new JLabel(resizedIcon); // Change to your image path
       JPanel imagePanel = new JPanel();
       imagePanel.add(imageLabel);

       // Add detective note in the center
       JPanel notesJPanel = new JPanel(new BorderLayout());
       JTextArea notes = new JTextArea("Detective Notes...");
       notes.setLineWrap(true);
       notes.setWrapStyleWord(true);
       JScrollPane detectiveNotes = new JScrollPane(notes);
       notesJPanel.add(detectiveNotes, BorderLayout.CENTER);

       JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(new JButton("Remove Card"));
        buttons.add(new JButton("Add Card"));
        buttons.add(new JButton("Eliminate Player"));
        buttons.add(new JButton("Save"));
       notesJPanel.add(buttons, BorderLayout.SOUTH);

        // Add south panel to frame
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(imagePanel, BorderLayout.WEST);
        frame.add(notesJPanel, BorderLayout.CENTER);

        frame.revalidate();  // Refresh layout
        frame.repaint();     // Redraw the frame
    }

    public void setUpPlayerTurnPanel() {
        playerTurnPanel = new JPanel();
        playerTurnPanel.add(new JLabel("Player's Turn: Roll the dice!"));
    }

    // EFFECTS: runs the game through looping each players' turn
    public void runGame() {
        // stub
    }

    public void switchToGamePanel() {
        cardLayout.show(mainPanel, "Game");
    }

    public void switchToPlayerTurnPanel() {
        cardLayout.show(mainPanel, "Player Turn");
    }

    public void switchToNotesPanel() {
        cardLayout.show(mainPanel, "Notes");
    }

    public void switchToControlPanel() {
        cardLayout.show(mainPanel, "Control");
    }
    
    // EFFECTS: saves the detective and list of players to a new file or destinated file
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
