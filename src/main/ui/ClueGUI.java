package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
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

public class ClueGUI {
    private String filePath;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JFrame frame = new JFrame("Clue Application");
    private static final int frameWidth = 1000;
    private static final int frameHeight = 600;
    JScrollPane detectiveNotes;
    JTextArea notes;
    JButton saveButton;
    JButton quitButton;
    JButton nextButton;
    JButton refreshButton;
    JButton addCardButton;
    JButton removeCardButton;
    JButton eliminatePlayerButton;
    JLabel nextPlayerLabel;
    JLabel progressLabel;
    JLabel saveStatusLabel;
    JLabel fileLabel;
    JLabel numPlayerLabel;
    JLabel numCardsInRoomLabel;

    private ClueGame game;
    private int numPlayers;
    private String[] playerNames;
    private String myName;
    private String[] myHandCards;
    private int numHandCardsPerPlayer;
    private List<String> orderedPlayers;
    private int currentPlayerIndex;
    private String currentPlayer;
    private String currentInput;
    private int numCardsInRooms;

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
    // then call orderPlayersANDStartGame()
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
    // then create new clue game
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
    // show instuctions to set up Clue game
    public void showInstructionsDialog() {
        numHandCardsPerPlayer = (Card.NAMES.length - 3) / numPlayers;
        numCardsInRooms = (Card.NAMES.length - 3) % numPlayers;
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

            JScrollPane scrollPane = getCardNamesScrollPane();
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

    private JScrollPane getCardNamesScrollPane() {
        // Create JTextArea for the card names reference
        JTextArea cardNamesTextArea = new JTextArea(5, 35); // 5 rows, 30 columns
        cardNamesTextArea.setText(getCardNames());
        cardNamesTextArea.setWrapStyleWord(true); // Enable word wrapping
        cardNamesTextArea.setLineWrap(true); // Enable line wrapping
        cardNamesTextArea.setEditable(false); // Make it non-editable
        JScrollPane scrollPane = new JScrollPane(cardNamesTextArea);
        return scrollPane;
    }

    private String getCardNames() {
        String cardNames = "Card names reference: \n" +
                "Suspects: " + Suspect.names() + "\n" +
                "Weapons: " + Weapon.names() + "\n" +
                "Rooms: " + Room.names();
        return cardNames;
    }

    /*
     * EFFECTS: Let user input player order
     * After order is set, calls setUpMainFrame(), then calls runGame()
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
        setUpFrame();
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
            invalidName("player");
        }
        orderedPlayers.add(currentInput);
        System.out.println();
    }

    public void setUpFrame() {
        JPanel bottomPanel = getBottomPanel();
        JPanel leftPanel = getLeftPanel();
        JPanel mainPanel = getMainPanel();
        // Add panels to frame
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        notes = new JTextArea("Detective Notes...");
        notes.setLineWrap(false);
        notes.setWrapStyleWord(true);
        detectiveNotes = new JScrollPane(notes);
        mainPanel.add(detectiveNotes, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        removeCardButton = new JButton("Remove Card");
        addCardButton = new JButton("Add Card");
        eliminatePlayerButton = new JButton("Eliminate Player");
        saveButton = new JButton("Save");
        buttons.add(removeCardButton);
        buttons.add(addCardButton);
        buttons.add(eliminatePlayerButton);
        buttons.add(saveButton);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel getLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(250, 400));
        JLabel imageLabel = getImageLabel();
        JPanel statusPanel = getStatusPanel();
        // Add image label
        leftPanel.add(imageLabel);
        // Add spacing between image and status panel
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical gap
        // Add status panel
        leftPanel.add(statusPanel);
        return leftPanel;
    }

    private JPanel getStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(6, 1));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // initialize labels
        fileLabel = new JLabel("Destination: ");
        numPlayerLabel = new JLabel("# of players left: ");
        numCardsInRooms = (Card.NAMES.length - 3) % game.getNumPlayers();
        numCardsInRoomLabel = new JLabel("# of cards in room: " + numCardsInRooms);
        numHandCardsPerPlayer = (Card.NAMES.length - 3) / game.getNumPlayers();
        JLabel numHandCardsPerPlayerLabel = new JLabel("# of hand cards per player: " + numHandCardsPerPlayer);
        progressLabel = new JLabel("Progress: ");
        saveStatusLabel = new JLabel("Saved");
        saveStatusLabel.setVisible(false);
        // add labels
        statusPanel.add(fileLabel);
        statusPanel.add(numPlayerLabel);
        statusPanel.add(numHandCardsPerPlayerLabel);
        statusPanel.add(numCardsInRoomLabel);
        statusPanel.add(progressLabel);
        statusPanel.add(saveStatusLabel);
        return statusPanel;
    }

    private JLabel getImageLabel() {
        // image label
        ImageIcon originalIcon = new ImageIcon("image/detective.jpeg"); // Load image
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(250, 350, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return imageLabel;
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        JPanel bottomRow = new JPanel(new BorderLayout());
        quitButton = new JButton("Quit");
        nextPlayerLabel = new JLabel("Next player: ", JLabel.CENTER);
        nextButton = new JButton("Next");
        bottomRow.add(quitButton, BorderLayout.WEST);
        bottomRow.add(nextPlayerLabel, BorderLayout.CENTER);
        bottomRow.add(nextButton, BorderLayout.EAST);
        bottomPanel.add(bottomRow, BorderLayout.SOUTH);
        return bottomPanel;
    }

    // EFFECTS: runs the game
    public void runGame() {
        currentPlayerIndex = -1; // initialize currentPlayerIndex
        updateFrame();
        activateGUI();
    }

    /*
     * Activates Clue GUI where actions are performed when user clicks button
     */
    public void activateGUI() {
        removeCardButton.addActionListener(e -> removePotentialCard());
        addCardButton.addActionListener(e -> addPotentialCard());
        eliminatePlayerButton.addActionListener(e -> eliminatePlayer());
        saveButton.addActionListener(e -> save());
        quitButton.addActionListener(e -> quit());
        nextButton.addActionListener(e -> nextTurn());
    }

    public void updateFrame() {
        notes.setText(getDetectiveNotes()); // updates detective note
        fileLabel.setText("Destination: " + filePath);
        numPlayerLabel.setText("# of players left: " + game.getNumPlayersRemaining());
        // calculate progress
        int numEliminatedCards = game.getDetective().getNumCardsEliminated();
        int total = Card.NAMES.length - 3;
        progressLabel.setText("Progress: " + numEliminatedCards + "/" + total);
        nextPlayerLabel.setText("Next player: " + getNextPlayer());
        frame.revalidate();
        frame.repaint();
    }

    public String getNextPlayer() {
        // Find the next non-eliminated player
        int tempIndex = currentPlayerIndex;
        do {
            tempIndex = (tempIndex + 1) % orderedPlayers.size();
        } while (game.getEliminatedPlayers().contains(orderedPlayers.get(tempIndex)));

        return orderedPlayers.get(tempIndex);
    }

    public void removePotentialCard() {
        // stub
    }

    public void addPotentialCard() {
        // stub
    }

    public void eliminatePlayer() {
        // stub
    }

    public void quit() {
        String message = "Do you want to save changed before quitting?";
        int choice = JOptionPane.showConfirmDialog(frame, message, "Comfirm Quit", JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("User saved and quit.");
            save();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.out.println("User quit without saving.");
            System.exit(0);
        } else if (choice == JOptionPane.CANCEL_OPTION) {
            System.out.println("User canceled quitting.");
        }
    }

    public void nextTurn() {
        Detective detective = game.getDetective();

        // Check if game is over
        if (detective.foundSuspect() && detective.foundWeapon() && detective.foundRoom()) {
            foundSecretMurder();
            return;
        }

        // Update to the next player
        currentPlayer = getNextPlayer();
        currentPlayerIndex = orderedPlayers.indexOf(currentPlayer);

        if (currentPlayer.equals(myName)) {
            detectivesTurn();
        } else {
            playersTurn();
        }

        updateFrame();
    }

     /*
     * REQUIRES: it's your (user's) turn
     * MODIFIES: this
     * EFFECTS: runs the detective's (user's) turn
     */
    public void detectivesTurn() {
        JOptionPane.showMessageDialog(frame, "Your turn! \n Please roll the dice.", "Message", JOptionPane.INFORMATION_MESSAGE);
        int enteredRoom = JOptionPane.showConfirmDialog(frame, "Entered a room?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (enteredRoom == JOptionPane.YES_OPTION) {
            System.out.println("Detective entered a room");
            int chechCardInRoom = JOptionPane.showConfirmDialog(frame, "Check card in room?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (chechCardInRoom == JOptionPane.YES_OPTION) {
                checkCardInRoom();
                System.out.println("Detective checked card in room");
            }
            detectiveAskQuestion();
        }
    }

    /*
     * REQUIRES: detective has entered a room and there is a hidden card inside
     * MODIFIES: this
     * EFFECTS: lets user enter the hidden card's name and eliminates the card from
     * the detective's potential murder cards
     */
    public void checkCardInRoom() {
        currentInput = JOptionPane.showInputDialog(frame, "Enter card's name:");
        while (!Card.contains(currentInput) | currentInput.isEmpty()) {
            invalidName("card");
        }
        game.getDetective().eliminateCard(currentInput);
    }

    /*
     * REQUIRES: detective has entered a room
     * MODIFIES: this
     * EFFECTS: updates no cards to players who answered "no" to you
     * view one of the player's hand card who answered "yes"
     */
    public void detectiveAskQuestion() {
        JScrollPane cardNamesPanel = getCardNamesScrollPane();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField suspectField = new JTextField();
        JTextField weaponField = new JTextField();
        JTextField roomField = new JTextField();
        // Add labels and input fields to the panel
        inputPanel.add(new JLabel("Suspect:"));
        inputPanel.add(suspectField);
        inputPanel.add(new JLabel("Weapon:"));
        inputPanel.add(weaponField);
        inputPanel.add(new JLabel("Room:"));
        inputPanel.add(roomField);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(cardNamesPanel);
        panel.add(inputPanel);

        // Show the custom input dialog
        int option = JOptionPane.showConfirmDialog(null, inputPanel, "Clue Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String suspect = suspectField.getText();
            String weapon = weaponField.getText();
            String room = roomField.getText();

            // Validate inputs
            while (!suspect.trim().isEmpty() | !Suspect.contains(suspect)) {
                invalidName("suspect");
                suspect = currentInput;
            }
            while (!weapon.trim().isEmpty() | !Weapon.contains(weapon)) {
                invalidName("weapon");
                weapon = currentInput;
            }
            while (!room.trim().isEmpty() | !Room.contains(room)) {
                invalidName("room");
                room = currentInput;
            }
            inputPlayersAnswer(suspect, weapon, room);
        }
    }

    public void inputPlayersAnswer(String suspect, String weapon, String room) {
        List<String> askedPlayers = new ArrayList<>();
        currentInput = JOptionPane.showInputDialog(frame, "Enter first player to ask: ");
        while(game.getPlayer(currentInput) == null) {
            invalidName("player");
        }
        String currentAskedPlayer = currentInput;
        String question = "Does " + currentAskedPlayer + " have one of the cards?";
        int answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
        askedPlayers.add(currentAskedPlayer);

        while (answer == JOptionPane.NO_OPTION && askedPlayers.size() < game.getPlayers().size()) {
            game.addNoCardsToPlayer(currentAskedPlayer, suspect, weapon, room);
            currentInput = JOptionPane.showInputDialog(frame, "Enter the next player to ask: ");
            while(game.getPlayer(currentInput) == null | askedPlayers.contains(currentInput)) {
                invalidName("player");
            }
            currentAskedPlayer = currentInput;
            answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
            askedPlayers.add(currentAskedPlayer);
        }

        if (answer == JOptionPane.YES_OPTION) {
            viewCard(currentAskedPlayer);
        }
    }

    public void viewCard(String player) {
        String message = "Enter " + player + "'s card you just viewed: ";
        currentInput = JOptionPane.showInputDialog(frame, message);
        while (!Card.contains(currentInput)) {
            invalidName("card");
        }
        game.addHandCardToPlayer(player, currentInput);
    }

    public void playersTurn() {
        String title = currentPlayer + "'s turn:";
        String question = "Did they enter a room?";
        int answer = JOptionPane.showConfirmDialog(frame, question, title, JOptionPane.YES_NO_CANCEL_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            playerAskQuestion();
        }
    }

    public void playerAskQuestion() {
        
    }

    /*
     * EFFECTS: Inform user that they entered an invalid name of type
     *          and let user re enter the type name again
     */
    private void invalidName(String type) {
        JOptionPane.showMessageDialog(null,
                "Invalid " + type + " name!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        currentInput = JOptionPane.showInputDialog("Please enter again: ");
    }

    /*
     * EFFECTS: returns all clues as a formatted string to help the detective find
     * the murderer.
     */
    public String getDetectiveNotes() {
        StringBuilder sb = new StringBuilder();

        sb.append("Detective Notes...\n\n");

        sb.append(getMyNotes()).append("\n");

        List<Player> players = game.getPlayers();
        for (Player p : players) {
            sb.append(getPlayerNote(p)).append("\n");
        }

        return sb.toString();
    }

    /*
     * EFFECTS: returns p's hand cards, no cards, and unchecked cards as a formatted
     * string.
     */
    public String getPlayerNote(Player p) {
        return p.getName() + "'s hand cards: " + p.getHandCards() + "\n" +
                p.getName() + "'s no cards: " + p.getNoCards() + "\n" +
                p.getName() + "'s unchecked cards: " + p.getUncheckedCards() + "\n";
    }

    /*
     * EFFECTS: returns detective's hand cards and potential murder cards as a
     * formatted string.
     */
    public String getMyNotes() {
        Detective detective = game.getDetective();
        return "---------------------------------------------------------------------------------------------------------------------------------\n" +
                "Your hand cards: " + detective.getHandcards() + "\n" +
                "Potential suspects: " + detective.getSuspects() + "\n" +
                "Potential weapons: " + detective.getWeapons() + "\n" +
                "Potential rooms: " + detective.getRooms() + "\n" +
                "---------------------------------------------------------------------------------------------------------------------------------\n";
    }

    public void foundSecretMurder() {

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
            filePath = "data/" + selectedFile.getName();
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

    // EFFECTS: saves the detective and list of players to a new file or destinated
    // file
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
            jsonWriter = new JsonWriter(filePath);
            saveStatusLabel.setVisible(true);
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
        saveStatusLabel.setVisible(true);
        Timer timer = new Timer(2000, e -> saveStatusLabel.setVisible(false));
        timer.setRepeats(false); // Ensure it only runs once
        timer.start(); // Start the timer
        updateFrame();
    }

}