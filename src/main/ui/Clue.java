package ui;

import static org.junit.Assert.fail;

import java.util.*;

import exception.EmptyUncheckedSet;
import exception.InvalidCardName;
import exception.InvalidPlayerName;
import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;
import model.Detective;
import model.Card;

// represents the game Clue, containing all information needed
public class Clue {

    private List<Player> players;
    private Detective d;
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
    private String currentPlayer;
    private boolean viewedCurrentPlayersCard;
    Scanner ui;

    /*
     * REQUIRES: 3 <= numPlayers <= 6
     * MODIFIES: this
     * EFFECTS: sets up the game by making new Players and Detectives
     * through user based console
     */
    public Clue(int numPlayers) {
        this.numPlayers = numPlayers;
        numHandCardsPerPlayer = (Card.names.length - 3) / numPlayers;
        numCardsInRooms = (Card.names.length - 3) % numPlayers;
        ui = new Scanner(System.in);
        enterPlayerNames();
        printInstructions();
        printCardNames();
        manageMyHandCards();
        orderPlayers();
        runGame();
    }

    public Player getPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

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

    public void manageMyHandCards() {
        List<String> myHandCards = new ArrayList<>();
        for (int i = 1; i <= numHandCardsPerPlayer; i++) {
            System.out.print("Enter your hand card #" + i + ": ");
            currentCard = ui.nextLine();
            checkValidCardName(currentCard);
            myHandCards.add(currentCard);
            System.out.println();
        }
        try {
            d = new Detective(myName, myHandCards);
            for (String handCard : myHandCards) {
                addNoCardsToOtherPlayers(null, handCard);
            }
            printDetectiveNotes();
        } catch (InvalidCardName e) {
            System.out.println("You have at least one invalid card name!");
            System.out.println("Please make sure to spell names exactly from the card names reference!");
            System.out.println();
            manageMyHandCards();
        } catch (EmptyUncheckedSet e) {
            System.out.println("Error: Empty unchecked set");
        }
    }

    public void orderPlayers() {
        orderedPlayers = new ArrayList<>();
        System.out.println("Time to determine the order of turns!");
        System.out.print("Enter the player who goes first: ");
        currentPlayer = ui.nextLine();
        addPlayerToOrderedPlayer(currentPlayer);

        int i = 2;
        while (i <= numPlayers) {
            System.out.print("Enter next player: ");
            addPlayerToOrderedPlayer(currentPlayer);
            i++;
        }
        System.out.println("All set! Ready to start the game!");
        System.out.println();
    }

    public void addPlayerToOrderedPlayer(String player) {
        while ((getPlayer(player) == null && !myName.equals(player)) | orderedPlayers.contains(player)) {
            System.out.println("Invalid player name!");
            System.out.print("Please enter again: ");
            currentPlayer = ui.nextLine();
            System.out.println();
        }
        orderedPlayers.add(currentPlayer);
        System.out.println();
    }

    public void runGame() {
        while (!d.foundSuspect() | !d.foundWeapon() | !d.foundRoom()) {
            for (String p : orderedPlayers) {
                if (p.equals(myName)) {
                    detectivesTurn();
                } else {
                    playersTurn(p);
                }
            }
        }
        foundSecretMurder();
    }

    public void detectivesTurn() {
        System.out.println("Your turn! Please roll the dice.");
        System.out.println("Entered a room? (yes/no) ");
        currentAnswer = ui.nextLine();
        checkAnswer();
        if (currentAnswer.equals("yes")) {
            System.out.println("Check card in room? (yes/no) ");
            currentAnswer = ui.nextLine();
            checkAnswer();
            if (currentAnswer.equals("yes")) {
                checkCardInRoom();
            }
            detectiveAskQuestion();
            printDetectiveNotes();
        }
        System.out.println();
    }

    public void playersTurn(String p) {
        System.out.println(p + "'s turn:");
        System.out.println("Did they enter a room? (yes/no) ");
        String answer = ui.nextLine();
        checkAnswer();
        if (answer.equals("yes")) {
            playerAskQuestion(p);
            printDetectiveNotes();
        }
        System.out.println();
    }

    public void foundSecretMurder() {
        System.out.println("Found the secret murder!");
        System.out.println("Suspect: " + d.getSuspects().get(0).getName());
        System.out.println("Weapon: " + d.getWeapons().get(0).getName());
        System.out.println("Room: " + d.getRooms().get(0).getName());
    }

    public void checkCardInRoom() {
        System.out.print("Enter card's name: ");
        String name = ui.nextLine();
        System.out.println();
        try {
            d.eliminateCard(name);
            printMyNotes();
        } catch (InvalidCardName e) {
            System.out.println("Invalid card name! Try again.");
            System.out.println();
            checkCardInRoom();
        }
    }

    public void detectiveAskQuestion() {
        askClueQuestion();
        while (currentAnswer.equals("no")) {
            updateForPlayersNo(currentSuspect, currentWeapon, currentRoom, currentAskedPlayer);
        }

        viewedCurrentPlayersCard = false;

        while (!viewedCurrentPlayersCard) {
            try {
                viewCard(currentAskedPlayer);
                viewedCurrentPlayersCard = true;
            } catch (InvalidPlayerName e) {
                System.out.println("Invalid player name!");
                System.out.print("Enter player name again: ");
                currentAskedPlayer = ui.nextLine();
            } catch (InvalidCardName e) {
                System.out.println("Invalid card name!");
            } catch (EmptyUncheckedSet e) {
                error();
            }
        }
    }

    public void askClueQuestion() {
        System.out.println("Time to ask a CLUE question!");
        System.out.print("Enter a suspect: ");
        currentSuspect = ui.nextLine();
        checkValidSuspectName(currentSuspect);

        System.out.print("Enter a weapon: ");
        currentWeapon = ui.nextLine();
        checkValidWeaponName(currentWeapon);

        System.out.print("Enter the room you are in: ");
        currentRoom = ui.nextLine();
        checkValidRoomName(currentRoom);

        System.out.print("Enter the first player to ask: ");
        currentAskedPlayer = ui.nextLine();
        checkValidPlayer(currentAskedPlayer);

        System.out.print("Their answer (yes/no): ");
        currentAnswer = ui.nextLine();
        checkAnswer();
        System.out.println();
    }

    public void checkValidPlayer(String name) {
        while (getPlayer(name) == null) {
            System.out.println("Invalid player name!");
            printPlayers();
            System.out.print("Enter player's name from the above list: ");
            currentAskedPlayer = ui.nextLine();
        }
    }

    public void checkValidSuspectName(String name) {
        while (!Suspect.contains(name)) {
            System.out.println("Invalid suspect name!");
            printAllSuspects();
            System.out.print("Enter suspect name from the above list: ");
            currentSuspect = ui.nextLine();
        }
    }

    public void checkValidWeaponName(String name) {
        while (!Weapon.contains(name)) {
            System.out.println("Invalid weapon name!");
            printAllWeapons();
            System.out.print("Enter weapon name from the above list: ");
            currentWeapon = ui.nextLine();
        }
    }

    public void checkValidRoomName(String name) {
        while (!Room.contains(name)) {
            System.out.println("Invalid room name!");
            printAllRooms();
            System.out.print("Enter room name from the above list: ");
            currentRoom = ui.nextLine();
        }
    }

    public void checkValidCardName(String name) {
        while (!Card.contains(name)) {
            System.out.println("Invalid card name!");
            printCardNames();
            System.out.print("Enter card name from the reference above: ");
            currentCard = ui.nextLine();
        }
    }

    public void checkAnswer() {
        while (!currentAnswer.equals("yes") && !currentAnswer.equals("no")) {
            System.out.println("Invalid answer!");
            System.out.print("Please enter 'yes' or 'no': ");
            currentAnswer = ui.nextLine();
        }
    }

    public void viewCard(String name) throws InvalidPlayerName, InvalidCardName, EmptyUncheckedSet {
        Player player = getPlayer(name);
        if (player != null) {
            System.out.print("Enter " + name + "'s card you just viewed: ");
            currentCard = ui.nextLine();
            checkValidCardName(currentCard);
            player.addHandCard(currentCard, d);
            printPlayerNote(player);
            addNoCardsToOtherPlayers(player, currentCard);
        } else {
            throw new InvalidPlayerName();
        }
    }

    public void updateForPlayersNo(String suspect, String weapon, String room, String p) {
        try {
            Player player = getPlayer(p);
            while (player == null) {
                System.out.println("Invalid player name!");
                System.out.print("Enter asked player's name again: ");
                currentAskedPlayer = ui.nextLine();
                player = getPlayer(currentAskedPlayer);
            }
            player.addNoCard(suspect);
            player.addNoCard(weapon);
            player.addNoCard(room);
            player.checkUncheckedCards(d);
            printPlayerNote(player);
        } catch (InvalidCardName e) {
            System.out.println("You have at least one invalid card name! Try again.");
            System.out.println();
            detectiveAskQuestion();
        } catch (EmptyUncheckedSet e) {
            System.out.println("Error: Empty unchecked set");
            error();
        }
        System.out.print("Enter the next player to ask: ");
        currentAskedPlayer = ui.nextLine();
        System.out.print("Their answer (yes/no): ");
        currentAnswer = ui.nextLine();
        checkAnswer();
        System.out.println();
    }

    public void addNoCardsToOtherPlayers(Player player, String card) throws InvalidCardName, EmptyUncheckedSet {
        for (Player p : players) {
            if (!p.equals(player)) {
                p.addNoCard(card);
                p.checkUncheckedCards(d);
            }
        }
    }

    public void playerAskQuestion(String askingPlayer) {
        System.out.println("What did the player ask?");
        System.out.print("Enter the suspect: ");
        currentSuspect = ui.nextLine();
        checkValidSuspectName(currentSuspect);

        System.out.print("Enter the weapon: ");
        currentWeapon = ui.nextLine();
        checkValidWeaponName(currentWeapon);

        System.out.print("Enter the room they are in: ");
        currentRoom = ui.nextLine();
        checkValidRoomName(currentRoom);

        System.out.print("Enter the first player they asked: ");
        currentAskedPlayer = ui.nextLine();
        checkPlayersNotEqual(currentAskedPlayer, askingPlayer);

        if (currentAskedPlayer.equals(myName)) {
            System.out.print("Your answer (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
        } else {
            checkValidPlayer(currentAskedPlayer);
            System.out.print("Their answer (yes/no): ");
            currentAnswer = ui.nextLine();
            checkAnswer();
        }

        while (currentAnswer.equals("no")) {
            updateForPlayersNo(currentSuspect, currentWeapon, currentRoom, currentAskedPlayer);
        }

        // answer.equals("yes")
        Player p = getPlayer(currentAskedPlayer);
        // p.addUncheckedCards(currentSuspect, currentWeapon, currentRoom);

        boolean checkedCard = false;
        while (!checkedCard) {
            try {
                if (p != null) {
                    p.addUncheckedCards(currentSuspect, currentWeapon, currentRoom);
                    checkedCard = true;
                } else if (currentAskedPlayer.equals(myName)) {
                    System.out.println("Reveal one of your hand cards to " + askingPlayer);
                    System.out.println();
                    checkedCard = true;
                } else {
                    System.out.println("Invalid player name!");
                    System.out.print("Enter name again: ");
                    String name = ui.nextLine();
                    p = getPlayer(name);
                }
            } catch (InvalidCardName e) {
                System.out.println("You have at least one invalid card name! Try again.");
                System.out.println();
                playerAskQuestion(askingPlayer);
            }
        }
    }

    public void checkPlayersNotEqual(String askedPlayer, String askingPlayer) {
        if (askedPlayer.equals(askingPlayer)) {
            System.out.println("You entered the asking player's name!");
            System.out.print("Enter asked player: ");
            currentAskedPlayer = ui.nextLine();
        }
    }

    public void printCardNames() {
        System.out.println("Card names reference: ");
        printAllSuspects();
        printAllWeapons();
        printAllRooms();
        System.out.println();
    }

    public void printAllSuspects() {
        System.out.println("Suspects: " + Suspect.names());
    }

    public void printAllWeapons() {
        System.out.println("Weapon: " + Weapon.names());
    }

    public void printAllRooms() {
        System.out.println("Room: " + Room.names());
    }

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

    public void printDetectiveNotes() {
        System.out.println("Detective Notes:");
        System.out.println();
        printMyNotes();
        for (Player p : players) {
            printPlayerNote(p);
        }
    }

    public void printPlayers() {
        System.out.println("Players: " + players);
    }

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

    public void printMyNotes() {
        String myHandCards = d.getHandcards().toString();
        String suspects = d.getSuspects().toString();
        String weapons = d.getWeapons().toString();
        String rooms = d.getRooms().toString();
        System.out.println("Your hand cards: " + myHandCards);
        System.out.println("Potential suspects: " + suspects);
        System.out.println("Potential weapons: " + weapons);
        System.out.println("Potential rooms: " + rooms);
        System.out.println();
    }

    public void error() {
        System.out.println("Unfortunately, there was an error!");
        System.out.println("From now on, you're on your own. Good luck!");
        printDetectiveNotes();
    }

}
