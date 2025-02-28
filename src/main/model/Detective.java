package model;

import java.util.*;

import org.json.JSONObject;

import persistence.Writable;

/*
 * represents you, the Detective, who is a player using
 * application with handCards and lists of potential murder 
 */
public class Detective implements Writable {
    private List<Suspect> suspects;
    private List<Weapon> weapons;
    private List<Room> rooms;
    private List<String> handCards;
    private String name;

    public Detective(String name, List<String> myCards) {
        this.name = name;
        handCards = myCards;
        reset();
    }

    /*
     * MODIFIES: this
     * EFFECTS: creates new ArrayLists for suspects, weapons, rooms
     * and adds every Card in the corresponding lists
     * Suspect.names, Weapon.names, and Room.names
     */
    public void reset() {
        suspects = new ArrayList<>();
        for (String s : Suspect.NAMES) {
            suspects.add(new Suspect(s));
        }

        weapons = new ArrayList<>();
        for (String s : Weapon.NAMES) {
            weapons.add(new Weapon(s));
        }

        rooms = new ArrayList<>();
        for (String s : Room.NAMES) {
            rooms.add(new Room(s));
        }

        for (String name : handCards) {
            eliminateCard(name);
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: removes the card from the detective's corresposding list of
     * potential murders
     */
    public void eliminateCard(String name) {
        if (Suspect.contains(name)) {
            removeSuspect(name);
        } else if (Weapon.contains(name)) {
            removeWeapon(name);
        } else {
            removeRoom(name);
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: set suspects according to the given list of suspect names
     */
    public void setSuspects(List<String> suspects) {
        this.suspects = new ArrayList<>();
        for (String s : suspects) {
            this.suspects.add(new Suspect(s));
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: set weapons according to the given list of weapon names
     */
    public void setWeapons(List<String> weapons) {
        this.weapons = new ArrayList<>();
        for (String s : weapons) {
            this.weapons.add(new Weapon(s));
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: set rooms according to the given room list of room names
     */
    public void setRooms(List<String> rooms) {
        this.rooms = new ArrayList<>();
        for (String s : rooms) {
            this.rooms.add(new Room(s));
        }
    }

    /*
     * REQUIRES: name must be in Suspect.names
     * MODIFIES: this
     * EFFECTS: removes the suspect in suspects with the given name and
     * returns the Suspect if it is found in suspects
     * returns null if suspect has already been removed
     */
    public Suspect removeSuspect(String name) {
        for (int i = 0; i < suspects.size(); i++) {
            if (suspects.get(i).getName().equals(name)) {
                return suspects.remove(i);
            }
        }
        return null;
    }

    /*
     * REQUIRES: name must be in Weapon.names
     * MODIFIES: this
     * EFFECTS: removes the weapon in weapons with the given name
     * returns true if progress is made in removing a Weapon, else false
     */
    public Weapon removeWeapon(String name) {
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).getName().equals(name)) {
                return weapons.remove(i);
            }
        }
        return null;
    }

    /*
     * REQUIRES: name must be in Room.names
     * MODIFIES: this
     * EFFECTS: removes the room in rooms with the given name
     * returns true if progress is made in removing a Room, else false
     */
    public Room removeRoom(String name) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(name)) {
                return rooms.remove(i);
            }
        }
        return null;
    }

    /*
     * REQUIRE: suspects.size() >= 1
     * EFFECTS: returns true if there is only one suspect left in suspects
     */
    public boolean foundSuspect() {
        return suspects.size() == 1;
    }

    /*
     * REQUIRE: weapons.size() >= 1
     * EFFECTS: returns true if there is only one weapon left in weapons
     */
    public boolean foundWeapon() {
        return weapons.size() == 1;
    }

    /*
     * REQUIRE: rooms.size() >= 1
     * EFFECTS: returns true if there is only one room left in rooms
     */
    public boolean foundRoom() {
        return rooms.size() == 1;
    }

    /*
     * REQUIRE: Suspect.contains(name)
     * EFFECTS: returns the suspect with the given name
     */
    public Suspect getSuspect(String name) {
        for (Suspect s : suspects) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    /*
     * REQUIRE: Weapon.contains(name)
     * EFFECTS: returns the weapon with the given name
     */
    public Weapon getWeapon(String name) {
        for (Weapon w : weapons) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        return null;
    }

    /*
     * REQUIRE: Room.contains(name)
     * EFFECTS: returns the room with the given name
     */
    public Room getRoom(String name) {
        for (Room r : rooms) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public List<Suspect> getSuspects() {
        return suspects;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<String> getHandcards() {
        return handCards;
    }

    public String getName() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("myhandcards", handCards);
        json.put("mysuspects", cardsToString(suspects));
        json.put("myweapons", cardsToString(weapons));
        json.put("myrooms", cardsToString(rooms));

        return json;
    }

    private <T extends Card> List<String> cardsToString(List<T> cards) {
        List<String> names = new ArrayList<>();
        for (T card : cards) {
            names.add(card.getName());
        }
        return names;
    }
    
}
