package model;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.Test;

public class TestCard {
    List<String> emptyList;
    Detective d;

    @Test
    public void testInit() {
        Card rope = new Weapon("Rope");
        Card hall = new Room("Hall");
        Card plum = new Suspect("Plum");
        emptyList = new ArrayList<>();
        d = new Detective("Me", emptyList);
        assertEquals("Rope", rope.getName());
        assertEquals("Hall", hall.getName());
        assertEquals("Plum", plum.getName());
    }

    @Test
    public void testContains() {
        // Card
        assertTrue(Card.contains("Lounge"));
        assertTrue(Card.contains("White"));
        assertTrue(Card.contains("Candlestick"));
        assertFalse(Card.contains("Red"));

        // Suspect
        assertTrue(Suspect.contains("Mustard"));
        assertFalse(Suspect.contains("Hall"));

        // Weapon
        assertTrue(Weapon.contains("Lead Pipe"));
        assertFalse(Weapon.contains("White"));

        // Room
        assertTrue(Room.contains("Ball Room"));
        assertFalse(Room.contains("Ball"));
        assertFalse(Room.contains("Rope"));

    }

    @Test
    public void testName() {
        assertEquals("Mustard, Plum, Green, Peacock, Scarlett, White", Suspect.names());
        assertEquals("Knife, Candlestick, Revolver, Rope, Lead pipe, Wrench", Weapon.names());
        assertEquals("Hall, Lounge, Dining room, Kitchen, Ball room, Conservatory, Billiard room, Library, Study",
                Room.names());
    }

    @Test
    public void testToString() {
        Card knife = new Weapon("Knife");
        assertEquals("Knife", knife.toString());
    }

}
