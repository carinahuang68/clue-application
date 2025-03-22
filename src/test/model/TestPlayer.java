package model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPlayer {

    Player p;
    Detective c;

    @BeforeEach
    public void runBefore() {
        p = new Player("John");
        List<String> emptyList = new ArrayList<>();
        c = new Detective("myName", emptyList);
    }

    @Test
    public void testInit() {
        assertEquals("John", p.getName());
        assertEquals(0, p.getHandCards().size());
        assertEquals(0, p.getNoCards().size());
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testAddMultipleHandCards() {
        // add weapon hand card
        assertTrue(p.addHandCard("Knife", c));
        assertEquals(1, p.getHandCards().size());
        assertEquals("Knife", p.getHandCards().get(0).getName());
        assertEquals(5, c.getWeapons().size());
        assertEquals("Candlestick", c.getWeapons().get(0).getName());

        // add suspect hand card
        assertTrue(p.addHandCard("Green", c));
        assertEquals(2, p.getHandCards().size());
        assertEquals("Green", p.getHandCards().get(1).getName());
        assertEquals(5, c.getSuspects().size());
        assertEquals("Peacock", c.getSuspects().get(2).getName());
        assertFalse(p.addHandCard("Green", c));

        // add room hand card
        assertTrue(p.addHandCard("Kitchen", c));
        assertEquals(3, p.getHandCards().size());
        assertEquals("Kitchen", p.getHandCards().get(2).getName());
        assertEquals(8, c.getRooms().size());
        assertEquals("Ball room", c.getRooms().get(3).getName());
        assertFalse(p.addHandCard("Kitchen", c));
    }

    @Test
    public void addOneHandCardTwice() {
        assertTrue(p.addHandCard("Wrench", c));
        assertFalse(p.addHandCard("Wrench", c));
        assertEquals(1, p.getHandCards().size());
        assertEquals(5, c.getWeapons().size());

    }

    @Test
    public void testAddNoCard() {
        p.addNoCard("Green");
        assertEquals(1, p.getNoCards().size());
        assertEquals("Green", p.getNoCards().get(0));
    }

    @Test
    public void testAddOneNoCardTwice() {
        p.addNoCard("Green");
        p.addNoCard("Green");
        assertEquals(1, p.getNoCards().size());
        assertEquals("Green", p.getNoCards().get(0));
    }

    @Test
    public void testAddUncheckedCards() {
        // add all
        p.addUncheckedCards("Mustard", "Wrench", "Kitchen");
        assertEquals(1, p.getUncheckedCards().size());
        assertEquals(3, p.getUncheckedCards().get(0).size());
        assertEquals("Mustard", p.getUncheckedCards().get(0).get(0));
        assertEquals("Wrench", p.getUncheckedCards().get(0).get(1));
        assertEquals("Kitchen", p.getUncheckedCards().get(0).get(2));

        // add some
        p.addNoCard("Rope");
        p.addNoCard("Kitchen");
        p.addUncheckedCards("Mustard", "Rope", "Kitchen");
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(1, p.getUncheckedCards().get(1).size());
        assertEquals("Mustard", p.getUncheckedCards().get(1).get(0));
    }

    @Test
    public void testRemoveUncheckedCard() {
        p.addUncheckedCards("White", "Rope", "Hall");
        p.addUncheckedCards("White", "Candlestick", "Hall");
        p.removeUncheckedCard("Hall");
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(2, p.getUncheckedCards().get(0).size());
        assertEquals(2, p.getUncheckedCards().get(1).size());
        assertFalse(p.getUncheckedCards().get(0).contains("Hall"));
        assertFalse(p.getUncheckedCards().get(1).contains("Hall"));

        p.removeUncheckedCard("Rope");
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(1, p.getUncheckedCards().get(0).size());
        assertEquals(2, p.getUncheckedCards().get(1).size());
        assertFalse(p.getUncheckedCards().get(0).contains("Rope"));
    }

    @Test
    public void testCheckUncheckedCards() {
        assertFalse(p.checkUncheckedCards(c));
        p.addUncheckedCards("Peacock", "Revolver", "Lounge");
        p.addUncheckedCards("White", "Revolver", "Lounge");
        assertFalse(p.checkUncheckedCards(c));

        p.removeUncheckedCard("Peacock");
        assertFalse(p.checkUncheckedCards(c));
        assertEquals(2, p.getUncheckedCards().size());

        Weapon revolver = c.getWeapon("Revolver");
        p.removeUncheckedCard("Lounge");
        assertTrue(p.checkUncheckedCards(c));
        assertEquals(1, p.getUncheckedCards().size());
        assertEquals(1, p.getHandCards().size());
        assertTrue(p.getHandCards().contains(revolver));

        p.removeUncheckedCard("White");
        assertTrue(p.checkUncheckedCards(c));
        assertEquals(1, p.getHandCards().size());
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testCheckUncheckedCardsHaveEmptySet() {
        p.addUncheckedCards("Peacock", "Revolver", "Lounge");
        p.addUncheckedCards("White", "Revolver", "Lounge");
        p.removeUncheckedCard("Revolver");
        p.removeUncheckedCard("Lounge");
        p.removeUncheckedCard("Peacock");
        p.checkUncheckedCards(c);
    }

    @Test
    public void testToString() {
        Player p = new Player("Me");
        assertEquals("Me", p.toString());
    }

    @Test
    public void testHandCardNames() {
        Player p = new Player("P");
        p.addHandCard("Plum");
        p.addHandCard("Hall");
        List<String> names = new ArrayList<>();
        names.add("Plum");
        names.add("Hall");
        assertEquals(names, p.handCardNames());
    }

}
