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

    public void eliminatePlayer(String name) {
        eliminatedPlayers.add(name);
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
