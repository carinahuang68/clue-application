package model;

// Represents a card with a name
public class Card {

    private String name;
    public static String[] names = 
    {"Mustard", "Plum", "Green", "Peacock", "Scarlett", "White",
     "Knife", "Candlestick", "Revolver", "Rope", "Lead Pipe", "Wrench",
     "Hall", "Lounge", "Dining Room", "Kitchen", "Ball Room", "Conservatory", "Billiard Room", "Library", "Study"};

     /*
      * REQUIRE: name must be in Card.names
      */
    public Card(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    // EFFECTS: return true if name is in Card.names, else false
    public static boolean contains(String name){
        for (String n: names){
            if (n.equals(name)){
                return true;
            }
        }
        return false;
    }

}
