package model;

import java.util.*;

import exception.EmptyUncheckedSet;
import exception.InvalidCardName;

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
     * MODIFIES: this
     * EFFECTS: if card in in Card.names,
     * removes the card with name from Clue c and
     * adds it to handCards
     * returns true if a Card has been added to handCards, else false
     * Throws InvalidCardName if name is not found in Card.names
     */
    public boolean addHandCard(String name, Detective c) throws InvalidCardName {
        if (Card.contains(name)) {
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
        } else {
            throw new InvalidCardName();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: adds card name to noCards and removes it from uncheckedCards
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void addNoCard(String name) throws InvalidCardName {
        if (Card.contains(name)) {
            if (!noCards.contains(name)) {
                noCards.add(name);
                removeUncheckedCard(name);
            }
        } else {
            throw new InvalidCardName();
        }
    }

    /*
     * REQUIRE: one parameter is suspect, one is weapon, and one is room (no
     * duplicates)
     * MODIFIES: this
     * EFFECTS: adds a list of the given Strings that is not in noCards to
     * uncheckedCards
     * Throws InvalidCardName if at least one name is not found in Card.names
     */
    public void addUncheckedCards(String suspect, String weapon, String room) throws InvalidCardName {
        List<String> newUncheckedCards = new ArrayList<>();
        newUncheckedCards.add(suspect);
        newUncheckedCards.add(weapon);
        newUncheckedCards.add(room);
        int i = 0;
        while (i < newUncheckedCards.size()) {
            String currentCard = newUncheckedCards.get(i);
            if (Card.contains(currentCard)) {
                if (noCards.contains(currentCard)) {
                    newUncheckedCards.remove(currentCard);
                } else {
                    i++;
                }
            } else {
                throw new InvalidCardName();
            }
        }
        uncheckedCards.add(newUncheckedCards);
    }

    /*
     * MODIFIES: this
     * EFFECTS: removes the cards with name from uncheckedCards if card is found in
     * uncheckedCards; does nothing if card is not found
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void removeUncheckedCard(String name) throws InvalidCardName {
        if (Card.contains(name)) {
            for (List<String> uncheckedSet : uncheckedCards) {
                for (String s : uncheckedSet) {
                    if (s.equals(name)) {
                        uncheckedSet.remove(s);
                        break;
                    }
                }
            }
        } else {
            throw new InvalidCardName();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: if a list in uncheckedCard contains only one name,
     * add the Card with name to handCards and removes the list in uncheckedCard
     * returns true if progress has been made
     */
    public boolean checkUncheckedCards(Detective c) throws EmptyUncheckedSet, InvalidCardName {
        boolean hasProgress = false;
        int i = 0;
        while (i < uncheckedCards.size()) {
            List<String> uncheckedSet = uncheckedCards.get(i);
            if (uncheckedSet.size() == 0) {
                throw new EmptyUncheckedSet();
            } else if (uncheckedSet.size() == 1) {
                try {
                    addHandCard(uncheckedSet.get(0), c);
                } catch (InvalidCardName e) {
                    throw new InvalidCardName();
                }
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
