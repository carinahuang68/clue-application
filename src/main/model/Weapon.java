package model;

public class Weapon extends Card{

    public static String[] names = {"Knife", "Candlestick", "Revolver", "Rope", "Lead Pipe", "Wrench"};

    public Weapon(String name){
        super(name);
    }

    // EFFECTS: return true if name is in Weapon.names, else false
    public static boolean contains(String name){
        for (String n: names){
            if (n.equals(name)){
                return true;
            }
        }
        return false;
    }

}
