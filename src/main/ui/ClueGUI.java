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
import model.EventLog;
import model.Event;

// @SuppressWarnings("methodlength")
// represents a clue graphical user interface, containing all information required to load the game interface
public class ClueGUI {
    private String filePath;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JFrame frame = new JFrame("Clue Application");
    private static final int frameWidth = 900;
    private static final int frameHeight = 600;
    JScrollPane detectiveNotes;
    JTextArea notes;
    JButton loadButton;
    JButton newGameButton;
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
    private int numUncheckedCardsInRoom;
    private String currentSuspect;
    private String currentWeapon;
    private String currentRoom;
    private boolean enteredQuestion = false;

    // EFFECTS: runs the CLUE application - GUI
    public ClueGUI() {
        openClueWindow();
        setUp();
    }

    // EFFECTS: opens a backgroud window
    public void openClueWindow() {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // display JFrame to center of screen
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Either calls load() or setUpNewGame() to load game, and then calls
    // orderPlayersANDStartGame()
    public void setUp() {
        JDialog dialog = getWelcomeOptionDialog();

        loadButton.addActionListener(e -> {
            dialog.dispose();
            load();
            orderPlayersAndStartGame();
        });

        newGameButton.addActionListener(e -> {
            dialog.dispose();
            setUpNewGame();
            orderPlayersAndStartGame();
        });

        dialog.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: returns the first JDialog that displays the option to load game or
    // start new game
    private JDialog getWelcomeOptionDialog() {
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
        loadButton = new JButton("Load Game");
        newGameButton = new JButton("New Game");

        buttonPanel.add(loadButton);
        buttonPanel.add(newGameButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        return dialog;
    }

    // MODIFIES: this
    // EFFECTS: obtains initial game info through user's inputs and then creates a
    // new clue game
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

    // MODIFIES: this
    // EFFECTS: shows a dialog that lets user select number of players
    public void numPlayerSelectionDialog() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Select number of players: ");

        Integer[] playerOptions = { 3, 4, 5, 6 };
        JComboBox<Integer> playerDropdown = new JComboBox<>(playerOptions);

        panel.add(label);
        panel.add(playerDropdown);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JOptionPane.showConfirmDialog(null, panel, "Number of Players", JOptionPane.DEFAULT_OPTION);

        numPlayers = (Integer) playerDropdown.getSelectedItem();

        System.out.println("Selected players: " + numPlayers);
        System.out.println();
    }

    // MODIFIES: this
    // EFFECTS: shows a pop up window that lets user input all player's name and
    // stores the player names
    @SuppressWarnings("methodlength")
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
    }

    // EFFECTS: Show instructions based on the number of players
    public void showInstructionsDialog() {
        numHandCardsPerPlayer = (Card.NAMES.length - 3) / numPlayers;
        numUncheckedCardsInRoom = (Card.NAMES.length - 3) % numPlayers;
        String instructions = "Set up instructions:\n"
                + "1. Randomly pick one secret suspect card, one secret weapon card, and one secret room card.\n"
                + "2. Place the 3 secret cards in the confidential folder, without looking at the cards!\n"
                + "3. Shuffle the remaining cards.\n"
                + "4. Distribute " + numHandCardsPerPlayer + " cards to each player, including you.\n";
        if (numUncheckedCardsInRoom != 0) {
            instructions += "5. Hide each of the " + numUncheckedCardsInRoom + " remaining cards in a room.\n";
        }
        JOptionPane.showMessageDialog(null, instructions);
    }

    // MODIFIES: this
    // EFFECTS: shows a dialog that lets user input hand card names and stores them
    // to myHandCards
    @SuppressWarnings("methodlength")
    public void myHandCardsInputDialog() {
        boolean validInput = false;
        while (!validInput) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

                if (myHandCards[i].isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Card names cannot be empty! \n Please re-enter all cards.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    validInput = false; // Restart input
                    break;
                }

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
        String cardNames = "Card names reference: \n"
                + "Suspects: " + Suspect.names() + "\n"
                + "Weapons: " + Weapon.names() + "\n"
                + "Rooms: " + Room.names();
        return cardNames;
    }

    /*
     * MODIFIES: this
     * EFFECTS: Let user input player order
     * After order is set, calls setUpMainFrame() and then calls runGame()
     */
    public void orderPlayersAndStartGame() {
        JPanel panel = inputFirstPlayerAndGetNextPlayerDialog();
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
     * EFFECTS: shows the first dialog for inputing a player's name and return the
     * next panel to input next player
     */
    private JPanel inputFirstPlayerAndGetNextPlayerDialog() {
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
        return panel;
    }

    /*
     * MODIFIES: this
     * EFFECTS: if player name is valid, sets currentPlayer as player and adds
     * currentPlayer to orderPlayers
     * if player name is invalid, lets user enter name again until currentPlayer is
     * valid
     */
    public void addPlayerToOrderedPlayer() {
        while ((game.getPlayer(currentInput) == null && !myName.equals(currentInput))
                | orderedPlayers.contains(currentInput)) {
            currentInput = invalidName("player");
        }
        orderedPlayers.add(currentInput);
        System.out.println();
    }

    /*
     * REQUIRES: game is ready for setting up the frame
     * MODIFIES: this
     * EFFECTS: sets up the main frame by adding the bottom, left, and main/center
     * panel to the frame
     */
    public void setUpFrame() {
        JPanel bottomPanel = getBottomPanel();
        JPanel leftPanel = getLeftPanel();
        JPanel mainPanel = getMainPanel();
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    /*
     * MODIFIES: this
     * EFFECTS: constructs and returns the main panel by adding a notes JScrollPane
     * and buttons bellow it
     */
    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        notes = new JTextArea("Detective Notes...");
        notes.setLineWrap(false);
        notes.setWrapStyleWord(true);
        detectiveNotes = new JScrollPane(notes);
        mainPanel.add(detectiveNotes, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        removeCardButton = new JButton("Remove a Potential Card");
        addCardButton = new JButton("Add a Potential Card");
        eliminatePlayerButton = new JButton("Eliminate Player");
        saveButton = new JButton("Save");
        buttons.add(removeCardButton);
        buttons.add(addCardButton);
        buttons.add(eliminatePlayerButton);
        buttons.add(saveButton);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        return mainPanel;
    }

    /*
     * EFFECTS: constructs and returns the left panel by adding the image and status
     * panel
     */
    private JPanel getLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(250, 400));
        JLabel imageLabel = getImageLabel();
        JPanel statusPanel = getStatusPanel();
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical gap
        leftPanel.add(statusPanel);
        return leftPanel;
    }

    /*
     * MODIFIES: this
     * EFFECTS: constructs and returns the status panel by initializaing and adding
     * status labels
     */
    private JPanel getStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(6, 1));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // initialize labels
        fileLabel = new JLabel("Destination: ");
        numPlayerLabel = new JLabel("# of players left: ");
        numUncheckedCardsInRoom = (Card.NAMES.length - 3) % game.getNumPlayers();
        numCardsInRoomLabel = new JLabel("# of unchecked cards in room: ");
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

    // EFFECTS: constructs and returns the detctive image label
    private JLabel getImageLabel() {
        ImageIcon originalIcon = new ImageIcon("image/detective.jpeg"); // Load image
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(250, 350, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return imageLabel;
    }

    /*
     * MODIFIES: this
     * EFFECTS: constructs and returns the bottom panel by adding the quit button,
     * next player label, and next button
     */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        JPanel bottomRow = new JPanel(new BorderLayout());
        quitButton = new JButton("Quit");
        nextPlayerLabel = new JLabel("Next player: ", JLabel.CENTER);
        nextButton = new JButton("Next turn");
        nextButton.setBackground(Color.CYAN);
        nextButton.setOpaque(true);
        bottomRow.add(quitButton, BorderLayout.WEST);
        bottomRow.add(nextPlayerLabel, BorderLayout.CENTER);
        bottomRow.add(nextButton, BorderLayout.EAST);
        bottomPanel.add(bottomRow, BorderLayout.SOUTH);
        return bottomPanel;
    }

    // EFFECTS: starts game by initialize current player index and call
    // updateFrame() and activateGUI()
    public void runGame() {
        currentPlayerIndex = -1;
        updateFrame();
        activateGUI();
    }

    // MODIFIES: this
    // EFFECTS: Activates the Clue GUI by adding action listiners to every button
    public void activateGUI() {
        removeCardButton.addActionListener(e -> removePotentialCard());
        addCardButton.addActionListener(e -> addPotentialCard());
        eliminatePlayerButton.addActionListener(e -> eliminatePlayer());
        saveButton.addActionListener(e -> save());
        quitButton.addActionListener(e -> quit());
        nextButton.addActionListener(e -> nextTurn());
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates the frame by re-setting text for the notes text area abd
     * status labels
     */
    public void updateFrame() {
        notes.setText(getDetectiveNotes()); // updates detective note
        fileLabel.setText("Destination: " + filePath);
        numPlayerLabel.setText("# of players left: " + game.getNumPlayersRemaining());
        numCardsInRoomLabel.setText("# of unchecked cards in room: " + numUncheckedCardsInRoom);
        int numEliminatedCards = game.getDetective().getNumCardsEliminated();
        int total = Card.NAMES.length - 3;
        progressLabel.setText("Progress: " + numEliminatedCards + "/" + total);
        nextPlayerLabel.setText("Next player: " + getNextPlayer());
        frame.revalidate();
        frame.repaint();
    }

    // EFFECTS: calculates and returns the next player
    public String getNextPlayer() {
        // Find the next non-eliminated player
        int tempIndex = currentPlayerIndex;
        do {
            tempIndex = (tempIndex + 1) % orderedPlayers.size();
        } while (game.getEliminatedPlayers().contains(orderedPlayers.get(tempIndex)));

        return orderedPlayers.get(tempIndex);
    }

    // EFFECTS: shows an input dialog to let user input a potential card to remove
    public void removePotentialCard() {
        String currentInput = JOptionPane.showInputDialog("Enter the potential card you want to remove: ",
                JOptionPane.OK_CANCEL_OPTION);
        while (!Card.contains(currentInput)) {
            currentInput = invalidName("card");
        }
        game.removePotentialCard(currentInput);
        updateFrame();
    }

    // EFFECTS: shows an input dialog to let user input a potential card to add
    public void addPotentialCard() {
        String currentInput = JOptionPane.showInputDialog("Enter the potential card you want to add: ",
                JOptionPane.OK_CANCEL_OPTION);
        while (!Card.contains(currentInput)) {
            currentInput = invalidName("card");
            System.out.println("Current Input: " + currentInput);
        }
        game.addPotentialCard(currentInput);
        updateFrame();
    }

    // EFFECTS: shows an input dialog to let user input a player to eliminate
    public void eliminatePlayer() {
        String currentInput = JOptionPane.showInputDialog("Enter the player to eliminate: ",
                JOptionPane.OK_CANCEL_OPTION);
        while (!orderedPlayers.contains(currentInput)) {
            currentInput = invalidName("player");
        }
        game.eliminatePlayer(currentInput);
        updateFrame();
    }

    // EFFECTS: shows a comfirm dialog to allow user to choose the option to save or
    // not save and closes the application
    public void quit() {
        String message = "Do you want to save changes before quitting?";
        int choice = JOptionPane.showConfirmDialog(frame, message, "Comfirm Quit", JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("User saved and quit.");
            save();
            printLog();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.out.println("User quit without saving.");
            printLog();
            System.exit(0);
        } else if (choice == JOptionPane.CANCEL_OPTION) {
            System.out.println("User canceled quitting.");
        }
    }

    private void printLog() {
        EventLog log = EventLog.getInstance();
        for (Event next : log) {
            System.out.println(next.toString());
            System.out.println();
        }
    }

    /*
     * REQUIRES: user is ready to input information for the next player's turn
     * MODIFIES: this
     * EFFECTS: main method that runs the next player's turn
     */
    public void nextTurn() {
        Detective detective = game.getDetective();

        // Check if game is over
        if (detective.foundSuspect() && detective.foundWeapon() && detective.foundRoom()) {
            foundSecretMurder();
            return;
        }

        if (game.getNumPlayersRemaining() == 1) {
            endGameMessage();
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
     * REQUIRES: only one player is left
     * EFFECTS: shows a message dialog that informs user who wins the game and
     * quit()
     */
    private void endGameMessage() {
        String remainingPlayer = game.getRemainingPlayers(orderedPlayers).get(0);
        String message = "All players except " + remainingPlayer + " are eliminated. \n";
        if (remainingPlayer.equals(myName)) {
            message += "Congratulations! You Win!";
        } else {
            message += remainingPlayer + " wins!";
        }
        JOptionPane.showMessageDialog(frame, message, "End Game Message", JOptionPane.DEFAULT_OPTION);
        quit();
    }

    /*
     * REQUIRES: it's your (user's) turn
     * MODIFIES: this
     * EFFECTS: runs the detective's (user's) turn
     */
    public void detectivesTurn() {
        JOptionPane.showMessageDialog(frame, "Your turn! \n Please roll the dice.", "Message",
                JOptionPane.INFORMATION_MESSAGE);
        int enteredRoom = JOptionPane.showConfirmDialog(frame, "Entered a room?", "Confirm",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (enteredRoom == JOptionPane.YES_OPTION) {
            System.out.println("Detective entered a room");
            int chechCardInRoom = JOptionPane.showConfirmDialog(frame, "Check card in room?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (chechCardInRoom == JOptionPane.YES_OPTION) {
                checkCardInRoom();
                System.out.println("Detective checked card in room");
            }
            detectiveAskQuestion();
        } else if (enteredRoom == JOptionPane.CANCEL_OPTION) {
            currentPlayerIndex = (currentPlayerIndex - 1 + orderedPlayers.size()) % orderedPlayers.size();
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
            currentInput = invalidName("card");
        }
        numUncheckedCardsInRoom--;
        game.removePotentialCard(currentInput);
    }

    /*
     * REQUIRES: detective has entered a room
     * MODIFIES: this
     * EFFECTS: updates no cards to players who answered "no" to you
     * view one of the player's hand card who answered "yes"
     */
    public void detectiveAskQuestion() {
        inputQuestion("Your Question");
        if (enteredQuestion) {
            inputPlayersAnswerToDetective();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: shows a pop up window to let user input suspect, weapon, and room
     * from a player's question
     */
    @SuppressWarnings("methodlength")
    private void inputQuestion(String title) {
        JScrollPane cardNamesPanel = getCardNamesScrollPane();
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField suspectField = new JTextField();
        JTextField weaponField = new JTextField();
        JTextField roomField = new JTextField();
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

        int option = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            currentSuspect = suspectField.getText();
            currentWeapon = weaponField.getText();
            currentRoom = roomField.getText();

            // Validate inputs
            while (currentSuspect.trim().isEmpty() | !Suspect.contains(currentSuspect)) {
                currentSuspect = invalidName("suspect");
            }
            while (currentWeapon.trim().isEmpty() | !Weapon.contains(currentWeapon)) {
                currentWeapon = invalidName("weapon");
            }
            while (currentRoom.trim().isEmpty() | !Room.contains(currentRoom)) {
                currentRoom = invalidName("room");
            }
            enteredQuestion = true;
        } else if (option == JOptionPane.CANCEL_OPTION) {
            currentPlayerIndex = (currentPlayerIndex - 1 + orderedPlayers.size()) % orderedPlayers.size();
        }

    }

    /*
     * REQUIRES: it is the detective's turn
     * MODIFIES: this
     * EFFECTS: let's user input asked player and their answers until a player
     * answer's 'yes' OR there are no more players to ask
     */
    public void inputPlayersAnswerToDetective() {
        List<String> askedPlayers = new ArrayList<>();
        String message = getCurrentCards() + "\n" + "Enter first player to ask: ";
        currentInput = JOptionPane.showInputDialog(frame, message);
        while (game.getPlayer(currentInput) == null) {
            currentInput = invalidName("player");
        }
        String currentAskedPlayer = currentInput;
        String question = "Does " + currentAskedPlayer + " have one of the following cards? \n" + getCurrentCards();
        int answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
        askedPlayers.add(currentAskedPlayer);
        while (answer == JOptionPane.NO_OPTION && askedPlayers.size() < game.getPlayers().size()) {
            game.addNoCardsToPlayer(currentAskedPlayer, currentSuspect, currentWeapon, currentRoom);
            message = getCurrentCards() + "\n" + "Enter the next player to ask: ";
            currentInput = JOptionPane.showInputDialog(frame, message);
            while (game.getPlayer(currentInput) == null | askedPlayers.contains(currentInput)) {
                currentInput = invalidName("player");
            }
            currentAskedPlayer = currentInput;
            question = "Does " + currentAskedPlayer + " have one of the following cards? \n" + getCurrentCards();
            answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
            askedPlayers.add(currentAskedPlayer);
        }
        yesToDetective(currentAskedPlayer, answer);
    }

    // EFFECTS: if answer is yes, view card
    private void yesToDetective(String currentAskedPlayer, int answer) {
        if (answer == JOptionPane.YES_OPTION) {
            viewCard(currentAskedPlayer);
        }
    }

    /*
     * REQUIRES: a player answered no to the detective
     * MODIFIES: this
     * EFFECTS: shows an input dialog that lets user input the player's card they
     * just viewed
     */
    public void viewCard(String player) {
        String message = getCurrentCards() + "\n" + "Enter " + player + "'s card you just viewed: ";
        currentInput = JOptionPane.showInputDialog(frame, message);
        while (!currentInput.equals(currentSuspect) && !currentInput.equals(currentWeapon)
                && !currentInput.equals(currentRoom)) {
            currentInput = invalidName("card");
        }
        game.addHandCardToPlayer(player, currentInput);
    }

    /*
     * REQUIRES: it is another player's turn (not the detective)
     * MODIFIES: this
     * EFFECTS: shows a comfirm dialog that asks user to select whether the player
     * entered a room and calls playerAskQUestion if user selected 'yes'
     */
    public void playersTurn() {
        String title = currentPlayer + "'s turn:";
        String question = "Did they enter a room?";
        int answer = JOptionPane.showConfirmDialog(frame, question, title, JOptionPane.YES_NO_CANCEL_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            playerAskQuestion();
        } else if (answer == JOptionPane.CANCEL_OPTION) {
            currentPlayerIndex = (currentPlayerIndex - 1 + orderedPlayers.size()) % orderedPlayers.size();
        }
    }

    /*
     * REQUIRES: a player has entered a room
     * MODIFIES: this
     * EFFECTS: let's user input information for the current player's question and
     * other players' answers
     */
    public void playerAskQuestion() {
        inputQuestion(currentPlayer + "'s Question");
        if (enteredQuestion) {
            inputPlayersAnswer();
        }
    }

    /*
     * REQUIRES: the current player has asked a question
     * MODIFIES: this
     * EFFECTS: shows input dialogs to let user input player(s) answer (yes or no)
     * to the current player's question until a player answered no or there are no
     * more players to be asked
     */
    @SuppressWarnings("methodlength")
    public void inputPlayersAnswer() {
        List<String> remainingPlayers = new ArrayList<>();
        for (String player : orderedPlayers) {
            remainingPlayers.add(player);
        }
        remainingPlayers.remove(currentPlayer);
        String message = getCurrentCards() + "\n" + "Enter the first player " + currentPlayer + " asked: ";
        currentInput = JOptionPane.showInputDialog(frame, message);
        while (!remainingPlayers.contains(currentInput)) {
            currentInput = invalidName("player");
        }
        String currentAskedPlayer = currentInput;
        int answer;
        String question = "";
        if (currentAskedPlayer.equals(myName)) {
            question = "Do you have one of the following cards? \n" + getCurrentCards();
        } else {
            question = "Does " + currentAskedPlayer + " have one of following cards? \n" + getCurrentCards();
        }
        answer = JOptionPane.showConfirmDialog(frame, question, "Answer", JOptionPane.YES_NO_OPTION);
        remainingPlayers.remove(currentAskedPlayer);

        while (answer == JOptionPane.NO_OPTION && remainingPlayers.size() > 0) {
            game.addNoCardsToPlayer(currentAskedPlayer, currentSuspect, currentWeapon, currentRoom);
            message = getCurrentCards() + "\n" + "Enter the next player " + currentPlayer + " asked: ";
            currentInput = JOptionPane.showInputDialog(frame, message);
            while (!remainingPlayers.contains(currentInput)) {
                currentInput = invalidName("player");
            }
            currentAskedPlayer = currentInput;
            if (currentAskedPlayer.equals(myName)) {
                question = "Do you have one of the following cards? \n" + getCurrentCards();
                answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
            } else {
                question = "Does " + currentAskedPlayer + " have one of following cards? \n" + getCurrentCards();
                answer = JOptionPane.showConfirmDialog(frame, question, "Player's answer", JOptionPane.YES_NO_OPTION);
            }
            remainingPlayers.remove(currentAskedPlayer);
        }

        if (currentAskedPlayer != myName) {
            if (answer == JOptionPane.YES_OPTION && currentAskedPlayer != myName) {
                game.addUncheckedCardsToPlayer(currentAskedPlayer, currentSuspect, currentWeapon, currentRoom);
            } else if (answer == JOptionPane.NO_OPTION) {
                game.addNoCardsToPlayer(currentAskedPlayer, currentSuspect, currentWeapon, currentRoom);
            }
        }

    }

    /*
     * REQUIRES: currentInput is invalid
     * EFFECTS: shows an invalid input message and lets user re-enter a name of type
     * type ands return the new input
     */
    private String invalidName(String type) {
        JOptionPane.showMessageDialog(null,
                "Invalid " + type + " name!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        currentInput = JOptionPane.showInputDialog("Please enter again: ");
        return currentInput;
    }

    /*
     * EFFECTS: returns all detective note as a formatted string to help the
     * detective find
     * the murderer
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

    // EFFECTS: returns the asked suspect, weapon, and room to remind the user
    public String getCurrentCards() {
        return "Current cards: " + currentSuspect + ", " + currentWeapon + ", " + currentRoom;
    }

    /*
     * EFFECTS: returns p's hand cards, no cards, and unchecked cards as a formatted
     * string.
     */
    public String getPlayerNote(Player p) {
        return p.getName() + "'s hand cards: " + p.getHandCards()
                + "\n"
                + p.getName() + "'s no cards: " + p.getNoCards()
                + "\n"
                + p.getName() + "'s unchecked cards: " + p.getUncheckedCards()
                + "\n";
    }

    /*
     * EFFECTS: returns detective's hand cards and potential murder cards as a
     * formatted string.
     */
    public String getMyNotes() {
        Detective detective = game.getDetective();
        return "---------------------------------------------------------------"
                + "------------------------------------------------------------------\n"
                + "Your hand cards: " + detective.getHandcards() + "\n"
                + "Potential suspects: " + detective.getSuspects() + "\n"
                + "Potential weapons: " + detective.getWeapons() + "\n"
                + "Potential rooms: " + detective.getRooms() + "\n"
                + "------------------------------------------------------"
                + "---------------------------------------------------------------------------\n";
    }

    /*
     * REQUIRES: the there are only one suspect, one weapon and one room left in
     * potential cards
     * EFFECST: shows a message dialog to let detective know that they found the
     * secret murder
     */
    public void foundSecretMurder() {
        String message = "Congratulations! You found the secret murder! \n";
        Detective d = game.getDetective();
        String suspect = d.getSuspects().get(0).getName();
        String weapon = d.getWeapons().get(0).getName();
        String room = d.getRooms().get(0).getName();
        message += "Suspect: " + suspect + " \n";
        message += "Weapon: " + weapon + "\n";
        message += "Room: " + room + "\n";
        JOptionPane.showMessageDialog(frame, message);
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
                List<Player> players = jsonReader.readPlayers();
                game = new ClueGame(detective, players);
                myName = game.getDetective().getName();
                System.out.println("Saved players to game");
            } catch (IOException e) {
                System.out.println("Unable to read from file: " + filePath);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: saves the detective and list of players to a new file or destinated
    // file and calls updateFrame
    public void save() {
        if (filePath == null) {
            enterNewFile();
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

    /*
     * REQUIRES: filePath is null
     * MODFIES: this
     * EFFECTS: shows a dialog to let user input new file name and save it to
     * JsonWriter and filePath
     */
    public void enterNewFile() {
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
        filePath = "data/" + fileNameField.getText().trim() + ".json";
        jsonWriter = new JsonWriter(filePath);
        saveStatusLabel.setVisible(true);
    }

}