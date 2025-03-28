package model;

import java.util.ArrayList;
import java.util.List;

public class ClueGame {
    private int numPlayers;
    private List<Player> players;
    private Detective detective;
    private List<String> eliminatedPlayers;

    /*
     * EFFECTS: Constructs a new clue game with new players and new detective
     */
    public ClueGame(String detectiveName, String[] playerNames, String[] detectiveHandCards) {
        numPlayers = playerNames.length + 1;
        players = new ArrayList<>();
        for (String name : playerNames) {
            Player player = new Player(name);
            players.add(player);
        }
        List<String> myHandCards = new ArrayList<>();
        for (String handCard : detectiveHandCards) {
            myHandCards.add(handCard);
        }
        detective = new Detective(detectiveName, myHandCards);
        eliminatedPlayers = new ArrayList<>();
    }

    /*
     * EFFECTS: Constructs a new clue game from pre-existing detective and players
     */
    public ClueGame(Detective detective, List<Player> players) {
        numPlayers = players.size() + 1;
        this.detective = detective;
        this.players = players;
        eliminatedPlayers = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

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
     * REQUIRES: name must be a player's name and all other parameters are valid
     * EFFECTS: adds all cards to player's no card
     */
    public void addNoCardsToPlayer(String name, String suspect, String weapon, String room) {
        Player player = getPlayer(name);
        if (player != null) {
            player.addNoCard(suspect);
            player.addNoCard(weapon);
            player.addNoCard(room);
            player.checkUncheckedCards(detective);
        }
    }

    /*
     * REQUIRES: name must be a player's name and card must be a card name
     * MODIFIES: this
     * EFFECTS: adds hand card to player and add the card to other player's no cards
     */
    public void addHandCardToPlayer(String name, String card) {
        Player player = getPlayer(name);
        if (player != null) {
            player.addHandCard(card);
            detective.eliminateCard(card);
            for (Player p : players) {
                if (!p.equals(player)) {
                    p.addNoCard(card);
                    p.checkUncheckedCards(detective);
                }
            }
        }
    }

    /*
     * REQUIRES: all parameters must be a valid name of player or card name
     * MODIFIES: this
     * EFFECTS: adds three unchecked cards to player name
     */
    public void addUncheckedCardsToPlayer(String name, String suspect, String weapon, String room) {
        Player player = getPlayer(name);
        player.addUncheckedCards(suspect, weapon, room);
    }

    /*
     * REQUIRES: card must be a valid card name
     * MODIFES: this
     * EFFECTS: removes card from detective's corresponding potential card list
     */
    public void removePotentialCard(String card) {
        EventLog.getInstance().logEvent(new Event("Removing a potential card manually..."));
        detective.eliminateCard(card);
    } 

    /*
     * REQUIRES: card must be a valid card name
     * MODIFES: this
     * EFFECTS: adds card to detective's corresponding potential card list
     */
    public void addPotentialCard(String card) {
        EventLog.getInstance().logEvent(new Event("Adding a potential card manually..."));
        detective.addCard(card);
    }

    /*
     * REQUIRES: name is a player's name
     * 
     */
    public void eliminatePlayer(String name) {
        if (!eliminatedPlayers.contains(name)) {
            eliminatedPlayers.add(name);
        }
    }

    public List<String> getRemainingPlayers(List<String> allPlayers) {
        List<String> remainingPlayers = new ArrayList<>();
        for (String player : allPlayers) {
            remainingPlayers.add(player);
        }
        for (String eliminatedPlayer : eliminatedPlayers) {
            remainingPlayers.remove(eliminatedPlayer);
        }
        return remainingPlayers;
    }

    public int getNumPlayersRemaining() {
        return numPlayers - eliminatedPlayers.size();
    }

    public List<String> getEliminatedPlayers() {
        return eliminatedPlayers;
    }

    public Detective getDetective() {
        return detective;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

}
