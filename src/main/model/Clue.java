package model;

import java.util.*;

import exception.InvalidRoomName;
import exception.InvalidSuspectName;
import exception.InvalidWeaponName;

public class Clue {
    private List<Suspect> suspects;
    private List<Weapon> weapons;
    private List<Room> rooms;
    

    public Clue(){
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
     *          returns true if progress is made in removing a suspect
     *          returns false if suspect has already been removed
     * Throws InvalidSuspectName if name is not in Suspect.names
     */
    public boolean removeSuspect(String name) throws InvalidSuspectName {
        // stub
        return false;
    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the weapon in weapons with the given name
     *          returns true if progress is made in removing a Weapon, else false
     * Throws InvalidCardName if name is not in weapons
     */
    public boolean removeWeapon(String name) throws InvalidWeaponName {
        // stub
        return false;
    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the room in rooms with the given name
     *          returns true if progress is made in removing a Room, else false
     * Throws NameNotFound if name is not in rooms
     */
    public boolean removeRoom(String name) throws InvalidRoomName {
        // stub
        return false;
    }

    /*
     * REQUIRE: suspects.size() >= 1
     * EFFECTS: returns true if there is only one suspect left in suspects
     */
    public boolean foundSuspect(){
        // stub
        return false;
    }

    /*
     * REQUIRE: weapons.size() >= 1
     * EFFECTS: returns true if there is only one weapon left in weapons
     */
    public boolean foundWeapon(){
        // stub
        return false;
    }

    /*
     * REQUIRE: rooms.size() >= 1
     * EFFECTS: returns true if there is only one room left in rooms
     */
    public boolean foundRoom(){
        // stub
        return false;
    }

    public List<Suspect> getSuspects(){
        return suspects;
    }

    public List<Weapon> getWeapons(){
        return weapons;
    }

    public List<Room> getRooms(){
        return rooms;
    }

}
