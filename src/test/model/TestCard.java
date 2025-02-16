package model;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestCard {

    @Test
    public void testInit() {
        Card rope = new Weapon("Rope");
        Card hall = new Room("Hall");
        Card plum = new Suspect("Plum");
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

}
