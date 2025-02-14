package model;
import java.util.*;


// Represents a card with a name
public class Card {

    private String name;
    public static String[] names = 
    {"Mustard", "Plum", "Green", "Peacock", "Scarlett", "White",
     "Knife", "Candlestick", "Revolver", "Rope", "Lead Pipe", "Wrench",
     "Hall", "Lounge", "Dining Room", "Kitchen", "Ball Room", "Conservatory", "Billiard Room", "Library", "Study"};

    public Card(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    // EFFECTS: return true if name is in Card.names, else false
    public boolean contains(String name){
        // stub
        return false;
    }

}
