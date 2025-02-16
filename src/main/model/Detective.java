package model;

import java.util.*;

import exception.InvalidCardName;
import exception.InvalidRoomName;
import exception.InvalidSuspectName;
import exception.InvalidWeaponName;

// represents 
public class Detective {
    private List<Suspect> suspects;
    private List<Weapon> weapons;
    private List<Room> rooms;
    private List<String> handCards;
    private String name;

    public Detective(String name, List<String> myCards) throws InvalidCardName {
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
    public void reset() throws InvalidCardName {
        suspects = new ArrayList<>();
        for (String s : Suspect.names) {
            suspects.add(new Suspect(s));
        }

        weapons = new ArrayList<>();
        for (String s : Weapon.names) {
            weapons.add(new Weapon(s));
        }

        rooms = new ArrayList<>();
        for (String s : Room.names) {
            rooms.add(new Room(s));
        }

        for (String name: handCards){
            eliminateCard(name);
        }
    }

    /*
     * MODIFIES: this
     * EFFECT: 
     */
    public void eliminateCard(String name) throws InvalidCardName {
        if (Card.contains(name)){
            if (Suspect.contains(name)){
                removeSuspect(name);
            } else if (Weapon.contains(name)) {
                removeWeapon(name);
            } else {
                removeRoom(name);
            }
        } else {
            throw new InvalidCardName();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: removes the suspect in suspects with the given name and
     * returns the Suspect if it is found in suspects
     * returns null if suspect has already been removed
     * Throws InvalidSuspectName if name is not in Suspect.names
     */
    public Suspect removeSuspect(String name) throws InvalidSuspectName {
        if (!Suspect.contains(name)) {
            throw new InvalidSuspectName();
        }

        for (int i = 0; i < suspects.size(); i++) {
            if (suspects.get(i).getName().equals(name)) {
                return suspects.remove(i);
            }
        }
        return null;
    }

    /*
     * MODIFIES: this
     * EFFECTS: removes the weapon in weapons with the given name
     * returns true if progress is made in removing a Weapon, else false
     * Throws InvalidWeaponName if name is not in weapons
     */
    public Weapon removeWeapon(String name) throws InvalidWeaponName {
        if (!Weapon.contains(name)) {
            throw new InvalidWeaponName();
        }

        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).getName().equals(name)) {
                return weapons.remove(i);
            }
        }
        return null;
    }

    /*
     * MODIFIES: this
     * EFFECTS: removes the room in rooms with the given name
     * returns true if progress is made in removing a Room, else false
     * Throws NameNotFound if name is not in rooms
     */
    public Room removeRoom(String name) throws InvalidRoomName {
        if (!Room.contains(name)) {
            throw new InvalidRoomName();
        }
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
    public Suspect getSuspect(String name){
        for (Suspect s: suspects){
            if (s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }

    /*
     * REQUIRE: Weapon.contains(name)
     * EFFECTS: returns the weapon with the given name
     */
    public Weapon getWeapon(String name){
        for (Weapon w: weapons){
            if (w.getName().equals(name)){
                return w;
            }
        }
        return null;
    }

    /*
     * REQUIRE: Room.contains(name)
     * EFFECTS: returns the room with the given name
     */
    public Room getRoom(String name){
        for (Room r: rooms){
            if (r.getName().equals(name)){
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

    public List<String> getHandcards(){
        return handCards;
    }

    public String getName(){
        return name;
    }
}
