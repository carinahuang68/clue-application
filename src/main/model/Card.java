package model;

// Represents a card with a name
public class Card {

    private String name;
    public static final String[] NAMES = { "Mustard", "Plum", "Green", "Peacock", "Scarlett", "White",
            "Knife", "Candlestick", "Revolver", "Rope", "Lead pipe", "Wrench",
            "Hall", "Lounge", "Dining room", "Kitchen", "Ball room", "Conservatory", "Billiard room", "Library",
            "Study" };

    /*
     * REQUIRE: name must be in Card.names
     */
    public Card(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // EFFECTS: return true if name is in Card.names, else false
    public static boolean contains(String name) {
        for (String n : NAMES) {
            if (n.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return name;
    }

}
