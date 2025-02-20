package model;

import java.util.*;

// represents another player with name
public class Player {

    private String name;
    private List<Card> handCards;
    private List<String> noCards;
    private List<List<String>> uncheckedCards;

    public Player(String name) {
        this.name = name;
        handCards = new ArrayList<>();
        noCards = new ArrayList<>();
        uncheckedCards = new ArrayList<>();
    }

    /*
     * REQUIRES: name must be in Card.names
     * MODIFIES: this
     * EFFECTS: if card in in Card.names,
     * removes the card with name from Clue c and
     * adds it to handCards
     * returns true if a Card has been added to handCards, else false
     */
    public boolean addHandCard(String name, Detective c) {
        if (Suspect.contains(name)) {
            Suspect s = c.removeSuspect(name);
            if (s != null) {
                handCards.add(s);
                return true;
            }
            return false;
        } else if (Weapon.contains(name)) {
            Weapon w = c.removeWeapon(name);
            if (w != null) {
                handCards.add(w);
                return true;
            }
            return false;
        } else {
            Room r = c.removeRoom(name);
            if (r != null) {
                handCards.add(r);
                return true;
            }
            return false;
        }
    }

    /*
     * REQUIRES: name must be in Card.names
     * MODIFIES: this
     * EFFECTS: adds card name to noCards and removes it from uncheckedCards
     */
    public void addNoCard(String name) {
        if (!noCards.contains(name)) {
            noCards.add(name);
            removeUncheckedCard(name);
        }
    }

    /*
     * REQUIRE: one parameter is suspect, one is weapon, and one is room
     * MODIFIES: this
     * EFFECTS: adds a list of the given Strings that is not in noCards to
     * uncheckedCards
     */
    public void addUncheckedCards(String suspect, String weapon, String room) {
        List<String> newUncheckedCards = new ArrayList<>();
        newUncheckedCards.add(suspect);
        newUncheckedCards.add(weapon);
        newUncheckedCards.add(room);
        int i = 0;
        while (i < newUncheckedCards.size()) {
            String currentCard = newUncheckedCards.get(i);
            if (noCards.contains(currentCard)) {
                newUncheckedCards.remove(currentCard);
            } else {
                i++;
            }
        }
        uncheckedCards.add(newUncheckedCards);
    }

    /*
     * REQUIRES: name must be in Card.names
     * MODIFIES: this
     * EFFECTS: removes the cards with name from uncheckedCards if card is found in
     * uncheckedCards; does nothing if card is not found
     */
    public void removeUncheckedCard(String name) {
        for (List<String> uncheckedSet : uncheckedCards) {
            for (String s : uncheckedSet) {
                if (s.equals(name)) {
                    uncheckedSet.remove(s);
                    break;
                }
            }
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: if a list in uncheckedCard contains only one name,
     * add the Card with name to handCards and removes the list in uncheckedCard
     * returns true if progress has been made
     */
    public boolean checkUncheckedCards(Detective c) {
        boolean hasProgress = false;
        int i = 0;
        while (i < uncheckedCards.size()) {
            List<String> uncheckedSet = uncheckedCards.get(i);
            if (uncheckedSet.size() == 1) {
                addHandCard(uncheckedSet.get(0), c);
                uncheckedCards.remove(i);
                hasProgress = true;
            } else {
                i++;
            }
        }
        return hasProgress;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public List<String> getNoCards() {
        return noCards;
    }

    public List<List<String>> getUncheckedCards() {
        return uncheckedCards;
    }

    public String toString() {
        return name;
    }

}
