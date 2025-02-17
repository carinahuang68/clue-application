package model;

// represents a suspect card with name
public class Suspect extends Card {

    public static String[] names = { "Mustard", "Plum", "Green", "Peacock", "Scarlett", "White" };

    public Suspect(String name) {
        super(name);
    }

    // EFFECTS: return true if name is in Suspect.names, else false
    public static boolean contains(String name) {
        for (String n : names) {
            if (n.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String names() {
        return "Mustard, Plum, Green, Peacock, Scarlett, White";
    }

}
