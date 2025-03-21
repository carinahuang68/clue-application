package model;

// represents a weapon card with name
public class Weapon extends Card {

    public static final String[] NAMES = { "Knife", "Candlestick", "Revolver", "Rope", "Lead pipe", "Wrench" };

    public Weapon(String name) {
        super(name);
    }

    // EFFECTS: return true if name is in Weapon.names, else false
    public static boolean contains(String name) {
        for (String n : NAMES) {
            if (n.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static String names() {
        return "Knife, Candlestick, Revolver, Rope, Lead pipe, Wrench";
    }

}
