package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;
import persistence.JsonReader;
import persistence.JsonWriter;
import model.Detective;
import model.Card;

// represents the console version of UI for the clue game
public class ClueApp {

    private static final String JSON_STORE = "./data/clueGame.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private List<Player> players;
    private Detective detective;
    private int numHandCardsPerPlayer;
    private int numCardsInRooms;
    private int numPlayers;
    private String myName;
    private List<String> orderedPlayers;
    private String currentCard;
    private String currentAnswer;
    private String currentSuspect;
    private String currentWeapon;
    private String currentRoom;
    private String currentAskedPlayer;
    private String currentName;
    Scanner ui;

    public static void main(String[] args) throws Exception {
        try (Scanner ui = new Scanner(System.in)) {
            System.out.println();
            System.out.println("Welcome to Clue!");
            System.out.println();
            System.out.print("Enter number of players: ");
            int numPlayers = ui.nextInt();
            while (numPlayers < 3 | numPlayers > 6) {
                System.out.println("There must be 3 to 6 players!");
                System.out.print("Enter number of players: ");
                numPlayers = ui.nextInt();
            }
            System.out.println();
            new ClueApp(numPlayers);
        }
    }

    /*
     * REQUIRES: 3 <= numPlayers <= 6
     * MODIFIES: this
     * EFFECTS: sets up the game by making new Players and Detectives
     * through user based console
     */
    public ClueApp(int numPlayers) {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        String answer = getLoadOption(numPlayers);

        if (answer.equals("s")) {
            enterPlayerNames();
            printInstructions();
            printCardNames();
            manageMyHandCards();
        } else {
            load();
            myName = detective.getName();
            printDetectiveNotes();
        }

        orderPlayers();
        runGame();
    }

    // EFFECTS: tells user to select an option and get user's option
    private String getLoadOption(int numPlayers) {
        this.numPlayers = numPlayers;
        numHandCardsPerPlayer = (Card.NAMES.length - 3) / numPlayers;
        numCardsInRooms = (Card.NAMES.length - 3) % numPlayers;
        ui = new Scanner(System.in);
        System.out.println("Select from below");
        System.out.println("   l: load data from file");
        System.out.println("   s: start new game");
        System.out.print(">>> ");
        String answer = ui.nextLine();
        System.out.println();
        while (!answer.equals("l") & !answer.equals("s")) {
            System.out.println("Invalid answer! Please enter 'l' or 's");
            System.out.print(">>> ");
            answer = ui.nextLine();
            System.out.println();
        }
        return answer;
    }

    /*
     * EFFECTS: returns the player if name is found in players,
     * else returns null
     */
    public Player getPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /*
     * MODIFIES: this
     * EFFECTS: Let user enter name for each player,
     * initializes players by adding each player with name to players
     */
    public void enterPlayerNames() {
        System.out.print("Enter your name: ");
        myName = ui.nextLine();

        players = new ArrayList<>();
        int i = 1;
        String name = "";
        while (i < numPlayers) {
            System.out.print("Enter player " + i + "'s name: ");
            name = ui.nextLine();
            players.add(new Player(name));
            i++;
        }
        System.out.println();
    }

    /*
     * MODIFIES: this
     * EFFECTS: lets user enter their hand cards,
     * adds the hand cards to myHandCard, and
     * initialized the user as a Detective with myName and myHandCard
     */
    public void manageMyHandCards() {
        List<String> myHandCards = new ArrayList<>();
        for (int i = 1; i <= numHandCardsPerPlayer; i++) {
            System.out.print("Enter your hand card #" + i + ": ");
            currentName = ui.nextLine();
            checkValidCardName(currentName);
            myHandCards.add(currentCard);
            System.out.println();
        }
        detective = new Detective(myName, myHandCards);
        for (String handCard : myHandCards) {
            addNoCardsToOtherPlayers(null, handCard);
        }
        printDetectiveNotes();
    }

    /*
     * MODIFIES: this
     * EFFECTS: lets user enter the players name by order of turns
     * and store the names to orderedPlayers
     */
    public void orderPlayers() {
        orderedPlayers = new ArrayList<>();
        System.out.println("Time to determine the order of turns!");
        System.out.print("Enter the player who goes first: ");
        currentName = ui.nextLine();
        addPlayerToOrderedPlayer(currentName);

        int i = 2;
        while (i <= numPlayers) {
            System.out.print("Enter next player: ");
            currentName = ui.nextLine();
            addPlayerToOrderedPlayer(currentName);
            i++;
        }
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
    public void addPlayerToOrderedPlayer(String player) {
        String currentPlayer = player;
        while ((getPlayer(currentPlayer) == null && !myName.equals(currentPlayer))
                | orderedPlayers.contains(currentPlayer)) {
            System.out.println("Invalid player name!");
            System.out.print("Please enter again: ");
            currentPlayer = ui.nextLine();
            System.out.println();
        }
        orderedPlayers.add(currentPlayer);
        System.out.println();
    }

    /*
     * MODIFIES: this
     * EFFECTS: runs the game through looping players' turn
     */
    public void runGame() {
        while (!detective.foundSuspect() | !detective.foundWeapon() | !detective.foundRoom()) {
            for (String p : orderedPlayers) {
                if (p.equals(myName)) {
                    enterOption();
                    detectivesTurn();
                } else {
                    enterOption();
                    playersTurn(p);
                }
            }
        }
        foundSecretMurder();
    }

    public void enterOption() {
        String answer = getContinueOption();

        if (answer.equals("a")) {
            save();
        } else if (answer.equals("c")) {
            System.out.println();
        } else if (answer.equals("s")) {
            save();
            System.out.println("Hope to see you again. Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Hope you enjoyed the game. Goodbye!");
            System.exit(0);
        }
    }

    private String getContinueOption() {
        System.out.println("Select option:");
        System.out.println("   a: save notes and continue");
        System.out.println("   c: continue");
        System.out.println("   s: save notes and quit");
        System.out.println("   q: quit");
        System.out.print(">>> ");
        String answer = ui.nextLine();
        System.out.println();
        while (!answer.equals("a") & !answer.equals("c") & !answer.equals("s") & !answer.equals("q")) {
            System.out.println("Invalid answer! Please enter 'c', 's', or 'q'.");
            System.out.print(">>> ");
            answer = ui.nextLine();
            System.out.println();
        }
        return answer;
    }

    /*
     * REQUIRES: it's your (user's) turn
     * MODIFIES: this
     * EFFECTS: runs the detective's (user's) turn
     */
    public void detectivesTurn() {
        System.out.println("Your turn! Please roll the dice.");
        System.out.println();
        System.out.print("Entered a room? (yes/no) ");
        System.out.println();
        currentAnswer = ui.nextLine();
        checkAnswer();
        if (currentAnswer.equals("yes")) {
            System.out.print("Check card in room? (yes/no) ");
            System.out.println();
            currentAnswer = ui.nextLine();
            checkAnswer();
            if (currentAnswer.equals("yes")) {
                checkCardInRoom();
                System.out.println();
            }
            detectiveAskQuestion();
            printDetectiveNotes();
        }
        System.out.println();
    }

    /*
     * REQUIRES: it's another player's turn
     * MODIFIES: this
     * EFFECTS: runs another player's turn
     */
    public void playersTurn(String p) {
        System.out.println(p + "'s turn:");
        System.out.println("Did they enter a room? (yes/no) ");
        currentAnswer = ui.nextLine();
        checkAnswer();
        if (currentAnswer.equals("yes")) {
            playerAskQuestion(p);
            printDetectiveNotes();
        }
        System.out.println();
    }

    /*
     * REQUIRES: the secret muder is found
     * MODIFIES: this
     * EFFECTS: prints the secret murder
     */
    public void foundSecretMurder() {
        System.out.println("Found the secret murder!");
        System.out.println("Suspect: " + detective.getSuspects().get(0).getName());
        System.out.println("Weapon: " + detective.getWeapons().get(0).getName());
        System.out.println("Room: " + detective.getRooms().get(0).getName());
    }

    /*
     * REQUIRES: detective has entered a room and there is a hidden card inside
     * MODIFIES: this
     * EFFECTS: lets user enter the hidden card's name and eliminates the card from
     * the detective's potential murder cards
     */
    public void checkCardInRoom() {
        System.out.print("Enter card's name: ");
        System.out.println();
        String name = ui.nextLine();
        System.out.println();
        detective.eliminateCard(name);
        printMyNotes();
    }

    /*
     * REQUIRES: detective has entered a room
     * MODIFIES: this
     * EFFECTS: updates no cards to players who answered "no" to you
     * view one of the player's hand card who answered "yes"
     */
    public void detectiveAskQuestion() {
        askClueQuestion();
        while (currentAnswer.equals("no")) {
            updateForPlayersNo(currentSuspect, currentWeapon, currentRoom, currentAskedPlayer, "");
        }
        viewCard(currentAskedPlayer);
    }

    /*
     * REQUIRES: detective is ready to ask a CLUE question
     * MODIFIES: this
     * EFFECTS: lets user input their investigating suspect, weapon, the room they
     * are in, the first player they want to ask and answer from that player, while
     * ensuring that all names entered are valid
     */
    public void askClueQuestion() {
        System.out.println("Time to ask a CLUE question!");
        System.out.print("Enter a suspect: ");
        currentName = ui.nextLine();
        checkValidSuspectName(currentName);

        System.out.print("Enter a weapon: ");
        currentName = ui.nextLine();
        checkValidWeaponName(currentName);

        System.out.print("Enter the room you are in: ");
        currentName = ui.nextLine();
        checkValidRoomName(currentName);

        System.out.print("Enter the first player to ask: ");
        currentName = ui.nextLine();
        checkValidPlayer(currentName);

        System.out.print(currentAskedPlayer + "'s answer (yes/no): ");
        currentAnswer = ui.nextLine();
        checkAnswer();
        System.out.println();
    }

    /*
     * REQUIRES: detective has viewed the player's card
     * MODIFIES: this
     * EFFECTS: lets user enter the viewed card from player name
     * and updates the player's hand card and other player's no cards
     */
    public void viewCard(String name) {
        Player player = getPlayer(name);
        if (player != null) {
            System.out.print("Enter " + name + "'s card you just viewed: ");
            currentName = ui.nextLine();
            checkValidCardName(currentName);
            player.addHandCard(currentCard, detective);
            printPlayerNote(player);
            addNoCardsToOtherPlayers(player, currentCard);
        }
    }

    /*
     * REQUIRES: player p answered no to another player or detective's question
     * MODIFIES: this
     * EFFECTS: adds the three given card names to Player p's no card
     */
    public void updateForPlayersNo(String suspect, String weapon, String room, String p, String askingPlayer) {
        Player player = getPlayer(p);
        if (player != null) {
            player.addNoCard(suspect);
            player.addNoCard(weapon);
            player.addNoCard(room);
            player.checkUncheckedCards(detective);
            printPlayerNote(player);
        }
        enterNextPlayer(askingPlayer);
    }

    private void enterNextPlayer(String askingPlayer) {
        System.out.print("Enter the next player " + askingPlayer + " asked: ");
        currentAskedPlayer = ui.nextLine();
        if (currentAskedPlayer.equals(myName)) {
            System.out.print("Do you have the card? (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
            if (currentAnswer.equals("yes")) {
                System.out.println("Show the card to " + askingPlayer);
                currentAskedPlayer = myName;
                System.out.println();
            }
        } else {
            checkValidPlayer(currentAskedPlayer);
            System.out.print("Their answer (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
        }
        System.out.println();
    }

    /*
     * REQUIRES: the given card is in one player's handcard or in room
     * EFFECTS: adds the given card to each player's no card except the given player
     */
    public void addNoCardsToOtherPlayers(Player player, String card) {
        for (Player p : players) {
            if (!p.equals(player)) {
                p.addNoCard(card);
                p.checkUncheckedCards(detective);
            }
        }
    }

    /*
     * MODIFIES: this
     * REQUIRES: askingPlayer is the player asking the question
     * EFFECTS: updates information based on user's input on askingPlayer's question
     * and other player's answer
     */
    public void playerAskQuestion(String askingPlayer) {
        recordPlayersQuestion(askingPlayer);
        if (currentAskedPlayer.equals(myName)) {
            System.out.print("Do you have the card? (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
            if (currentAnswer.equals("yes")) {
                System.out.println("Show the card to " + askingPlayer);
            }
        } else {
            checkValidPlayer(currentAskedPlayer);
            System.out.print("Their answer (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
        }

        while (currentAnswer.equals("no")) {
            updateForPlayersNo(currentSuspect, currentWeapon, currentRoom, currentAskedPlayer, askingPlayer);
        }

        // answer.equals("yes")
        if (!currentAskedPlayer.equals(myName)) {
            Player p = getPlayer(currentAskedPlayer);
            p.addUncheckedCards(currentSuspect, currentWeapon, currentRoom);
        }
    }

    /*
     * REQUIRES: askingPlayer has asked a question
     * MODIFIES: this
     * EFFECTS: record player's question based on user's input
     */
    public void recordPlayersQuestion(String askingPlayer) {
        System.out.println("What did the player ask?");
        System.out.print("Enter the suspect: ");
        currentName = ui.nextLine();
        checkValidSuspectName(currentName);

        System.out.print("Enter the weapon: ");
        currentName = ui.nextLine();
        checkValidWeaponName(currentName);

        System.out.print("Enter the room they are in: ");
        currentName = ui.nextLine();
        checkValidRoomName(currentName);

        System.out.print("Enter the first player they asked: ");
        currentAskedPlayer = ui.nextLine();
        checkPlayersNotEqual(currentAskedPlayer, askingPlayer);
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentAskedPlayer until user inputs a valid player name
     * (Your name is not valid because you are the Detective)
     */
    public void checkValidPlayer(String name) {
        currentAskedPlayer = name;
        while (getPlayer(currentAskedPlayer) == null) {
            System.out.println();
            System.out.println("Invalid player name!");
            System.out.println();
            printPlayers();
            System.out.print("Enter player's name from the above list: ");
            currentAskedPlayer = ui.nextLine();
            System.out.println();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentSuspect until user inputs a valid suspect's name
     */
    public void checkValidSuspectName(String name) {
        currentSuspect = name;
        while (!Suspect.contains(currentSuspect)) {
            System.out.println();
            System.out.println("Invalid suspect name!");
            System.out.println();
            printAllSuspects();
            System.out.print("Enter suspect name from the above list: ");
            currentSuspect = ui.nextLine();
            System.out.println();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentWeapon until user inputs a valid weapon name
     */
    public void checkValidWeaponName(String name) {
        currentWeapon = name;
        while (!Weapon.contains(currentWeapon)) {
            System.out.println();
            System.out.println("Invalid weapon name!");
            System.out.println();
            printAllWeapons();
            System.out.print("Enter weapon name from the above list: ");
            currentWeapon = ui.nextLine();
            System.out.println();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentRoom until user inputs a valid room name
     */
    public void checkValidRoomName(String name) {
        currentRoom = name;
        while (!Room.contains(currentRoom)) {
            System.out.println();
            System.out.println("Invalid room name!");
            System.out.println();
            printAllRooms();
            System.out.print("Enter room name from the above list: ");
            currentRoom = ui.nextLine();
            System.out.println();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentCard until user inputs a valid card name
     */
    public void checkValidCardName(String name) {
        currentCard = name;
        while (!Card.contains(currentCard)) {
            System.out.println();
            System.out.println("Invalid card name!");
            System.out.println();
            printCardNames();
            System.out.print("Enter card name from the reference above: ");
            currentCard = ui.nextLine();
            System.out.println();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentAnswer until user inputs either 'yes' or 'no'
     */
    public void checkAnswer() {
        while (!currentAnswer.equals("yes") && !currentAnswer.equals("no")) {
            System.out.println("Invalid answer!");
            System.out.print("Please enter 'yes' or 'no': ");
            currentAnswer = ui.nextLine();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: updates currentAskedPlayer until user inputs a different player name
     * as askingPlayer
     */
    public void checkPlayersNotEqual(String askedPlayer, String askingPlayer) {
        currentAskedPlayer = askedPlayer;
        if (currentAskedPlayer.equals(askingPlayer)) {
            System.out.println("You entered the asking player's name!");
            System.out.print("Enter asked player: ");
            currentAskedPlayer = ui.nextLine();
        }
    }

    /*
     * EFFECTS: prints the card name reference
     */
    public void printCardNames() {
        System.out.println("Card names reference: ");
        printAllSuspects();
        printAllWeapons();
        printAllRooms();
        System.out.println();
    }

    /*
     * EFFECTS: prints all suspect names
     */
    public void printAllSuspects() {
        System.out.println("Suspects: " + Suspect.names());
    }

    /*
     * EFFECTS: prints all weapon names
     */
    public void printAllWeapons() {
        System.out.println("Weapon: " + Weapon.names());
    }

    /*
     * EFFECTS: prints all room names
     */
    public void printAllRooms() {
        System.out.println("Room: " + Room.names());
    }

    /*
     * EFFECTS: prints instructions for game set up
     */
    public void printInstructions() {
        System.out.println("Set up instructions:");
        System.out
                .println("1. Randomly pick one secret suspect card, one secret weapon card, and one secret room card.");
        System.out.println("2. Place the 3 secret cards in the confidential folder, without looking at the cards!");
        System.out.println("3. Shuffle the remaining cards.");
        System.out.println("4. Distribute " + numHandCardsPerPlayer + " cards to each player, including you.");
        if (numCardsInRooms != 0) {
            System.out.println("5. Hide each of the " + numCardsInRooms + " remaining cards in a room.");
        }
        System.out.println();
    }

    /*
     * EFFECTS: prints all clues to help detective find the murder
     */
    public void printDetectiveNotes() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Detective Notes:");
        System.out.println();
        printMyNotes();
        for (Player p : players) {
            printPlayerNote(p);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    /*
     * EFFECTS: prints all player names
     */
    public void printPlayers() {
        System.out.println("Players: " + players);
    }

    /*
     * EFFECTS: prints p's hand cards, no cards, and unchecked card
     */
    public void printPlayerNote(Player p) {
        String name = p.getName();
        String handCards = p.getHandCards().toString();
        String noCards = p.getNoCards().toString();
        String unCheckedCards = p.getUncheckedCards().toString();
        System.out.println(name + "'s hand cards: " + handCards);
        System.out.println(name + "'s no cards: " + noCards);
        System.out.println(name + "'s unchecked cards: " + unCheckedCards);
        System.out.println();
    }

    /*
     * EFFECTS: prints detectives's hand cards and potential murder cards
     */
    public void printMyNotes() {
        String myHandCards = detective.getHandcards().toString();
        String suspects = detective.getSuspects().toString();
        String weapons = detective.getWeapons().toString();
        String rooms = detective.getRooms().toString();
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Your hand cards: " + myHandCards);
        System.out.println("Potential suspects: " + suspects);
        System.out.println("Potential weapons: " + weapons);
        System.out.println("Potential rooms: " + rooms);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }

    // EFFECTS: saves the detective and list of players to file
    private void save() {
        try {
            jsonWriter.open();
            jsonWriter.write(detective, players);
            jsonWriter.close();
            System.out.println("Saved detective and players to " + JSON_STORE);
            System.out.println();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    /*
     * REQUIRES: loaded file must have initialized players and detective
     * MODIFIES: this
     * EFFECTS: loads detective and players from file
     */
    private void load() {
        try {
            detective = jsonReader.readDetective();
            System.out.println("Loaded " + detective.getName() + " from " + JSON_STORE);
            players = jsonReader.readPlayers();
            System.out.println("Loaded " + players + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}
