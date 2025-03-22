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

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Player player = new Player("Rope");
        Card card = new Card("Rope");
        Card cardNull2 = new Card(null);
        Card cardNull1 = new Card(null);
        assertFalse(cardNull1.equals(card));
        assertTrue(cardNull1.equals(cardNull2));
        assertTrue(card.equals(card));
        assertFalse(card.equals(null));
        assertFalse(card.equals(player));
    }

    @Test
    public void testHashCode() {
        Card card1 = new Card("Knife"); // case sensitive
        Card card2 = new Card("Knife");
        assertEquals(card1.hashCode(), card2.hashCode());

        Card card3 = new Card("knife"); // case insensitive
        assertEquals(card1.hashCode(), card3.hashCode());

        Card card4 = new Card(null);
        Card card5 = new Card(null);

        assertEquals(card4.hashCode(), card5.hashCode());
    }

}
