package model;

import java.util.*;

import exception.InvalidCardName;

// Specification template:
    /* 
     * REQUIRES:
     * MODIFIES:
     * EFFECTS:
     */

public class Player {

    private String name;
    private List<Card> handCards;
    private List<Card> noCards;
    private List<Card> uncheckedCards;


    public Player(String name){
        this.name = name;
        handCards = new ArrayList<>();
        noCards = new ArrayList<>();
        uncheckedCards = new ArrayList<>();
    }

    /* 
     * MODIFIES: this
     * EFFECTS: adds the card with name to handCards if card in in Card.names
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void addHandCard(String name) throws InvalidCardName{

    }

    /* 
     * MODIFIES: this
     * EFFECTS: adds the card with name to noCards if card in in Card.names
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void addNoCard(String name) throws InvalidCardName{
        
    }

    /* 
     * MODIFIES: this
     * EFFECTS: adds the card with name to uncheckedCards if card in in Card.names
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void addUncheckedCard(String name) throws InvalidCardName{
        
    }

    /* 
     * MODIFIES: this
     * EFFECTS: removes the card with name to uncheckedCards if card in in Card.names
     * Throws InvalidCardName if name is not found in Card.names
     */
    public void removeUncheckedCard(String name) throws InvalidCardName{
        
    }

    public String getName(){
        return name;
    }

    public List<Card> getHandCards(){
        return handCards;
    }

    public List<Card> getNoCards(){
        return noCards;
    }

    public List<Card> getUncheckedCards(){
        return uncheckedCards;
    }


}
