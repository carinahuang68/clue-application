package model;

import java.util.*;

import org.json.JSONObject;

import persistence.Writable;

// represents anoth player with name
public class Player implements Writable {

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
        EventLog.getInstance().logEvent(new Event("Added " + name + " to " + this.name + "'s hand cards."));
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
     * REQUIRES: Card.contains(name)
     * MODIFIES: this
     * EFFECTS: add new card with name directly to handcards
     */
    public void addHandCard(String name) {
        if (!containsEqualIgnoreCase(handCardNames(), name)) {
            if (Suspect.contains(name)) {
                Suspect s = new Suspect(name);
                handCards.add(s);
            } else if (Weapon.contains(name)) {
                Weapon w = new Weapon(name);
                handCards.add(w);
            } else {
                Room r = new Room(name);
                handCards.add(r);
            }
            EventLog.getInstance().logEvent(new Event("Added " + name + " to " + this.name + "'s hand cards."));
        }
    }

    /*
     * REQUIRES: name must be in Card.names
     * MODIFIES: this
     * EFFECTS: adds card name to noCards and removes it from uncheckedCards
     */
    public void addNoCard(String name) {
        if (!containsEqualIgnoreCase(noCards, name)) {
            noCards.add(name);
            EventLog.getInstance().logEvent(new Event("Added " + name + " to " + this.name + "'s no cards."));
            removeUncheckedCard(name);
        }
    }

    /*
     * REQUIRES: Card.contains(name)
     * MODIFIES: this
     * EFFECTS: add name directly to noCards
     */
    public void addNocard(String name) {
        noCards.add(name);
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
            if (containsEqualIgnoreCase(noCards, currentCard)) {
                newUncheckedCards.remove(currentCard);
            } else {
                i++;
            }
        }
        uncheckedCards.add(newUncheckedCards);
        
        EventLog.getInstance()
                .logEvent(new Event("Added " + newUncheckedCards + " to " + this.name + "'s unchecked sets"));
    }

    /*
     * MODIFIES: this
     * EFFECTS: adds the given unchecked set to uncheckedCards
     */
    public void addUncheckedCards(List<String> uncheckedSet) {
        uncheckedCards.add(uncheckedSet);
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
                if (s.equalsIgnoreCase(name)) {
                    uncheckedSet.remove(s);
                    EventLog.getInstance()
                            .logEvent(new Event("Removed " + name + " from " + this.name + "'s unchecked cards."));
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
                EventLog.getInstance().logEvent(new Event("Removed a set of unchecked cards from " + this.name));
                hasProgress = true;
            } else {
                i++;
            }
        }
        return hasProgress;
    }

    // EFFECTS: returns true if name is found in cards, ignoring cases
    private boolean containsEqualIgnoreCase(List<String> cards, String name) {
        for (String card : cards) {
            if (card.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("handcards", handCardNames());
        json.put("nocards", noCards);
        json.put("uncheckedcards", uncheckedCards);
        return json;
    }

    public List<String> handCardNames() {
        List<String> names = new ArrayList<>();
        for (Card c : handCards) {
            names.add(c.getName());
        }
        return names;
    }

}
