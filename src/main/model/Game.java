package model;

import java.util.*;

public class Game {
    private List<Suspect> suspects;
    private List<Weapon> weapons;
    private List<Room> rooms;
    

    public Game(){
        //stub
    }

    /* 
     * MODIFIES: this
     * EFFECTS: creates new ArrayLists for suspects, weapons, rooms 
     *          and adds every Card in the corresponding lists
     *          Suspect.names, Weapon.names, and Room.names
     */
    public void reset(){

    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the suspect in suspects with the given name
     *          returns true if progress is made in removing a suspect, else false
     * Throws InvalidCardName if name is not in suspectNames
     */
    public boolean removeSuspect(String name){
        // stub
        return false;
    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the weapon in weapons with the given name
     *          returns true if progress is made in removing a Weapon, else false
     * Throws InvalidCardName if name is not in weaponNames
     */
    public boolean removeWeapon(String name){
        // stub
        return false;
    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the room in rooms with the given name
     *          returns true if progress is made in removing a Room, else false
     * Throws NameNotFound if name is not in roomNames
     */
    public boolean removeRoom(String name){
        // stub
        return false;
    }

    /*
     * REQUIRE: suspects.size() >= 1
     * EFFECTS: returns true if there is only one suspect left in suspects
     */
    public boolean suspectIsFound(){
        // stub
        return false;
    }

    /*
     * REQUIRE: weapons.size() >= 1
     * EFFECTS: returns true if there is only one weapon left in weapons
     */
    public boolean weaponIsFound(){
        // stub
        return false;
    }

    /*
     * REQUIRE: rooms.size() >= 1
     * EFFECTS: returns true if there is only one room left in rooms
     */
    public boolean roomIsFound(){
        // stub
        return false;
    }

}
