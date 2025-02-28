package model;

// represents a room card with name
public class Room extends Card {

    public static final String[] NAMES = { "Hall", "Lounge", "Dining Room", "Kitchen", "Ball Room", "Conservatory",
            "Billiard Room", "Library", "Study" };

    public Room(String name) {
        super(name);
    }

    // /*
    // * REQUIRES: name must be a card's name
    // * EFFECT: removes the card with the given name from the suspects list in game
    // * and adds the card to each player's noCards
    // */
    // public void checkCardInRoom(String name){

    // }

    // EFFECTS: return true if name is in Room.names, else false
    public static boolean contains(String name) {
        for (String n : NAMES) {
            if (n.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static String names() {
        return "Hall, Lounge, Dining Room, Kitchen, Ball Room, Conservatory, Billiard Room, Library, Study";
    }

}
