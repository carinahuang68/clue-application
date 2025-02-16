package model;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.EmptyUncheckedSet;
import exception.InvalidCardName;


public class TestPlayer {

    Player p;
    Clue c;

    @BeforeEach
    public void runBefore(){
        p = new Player("John");
        c = new Clue();
    }

    @Test
    public void testInit(){
        assertEquals("John", p.getName());
        assertEquals(0, p.getHandCards().size());
        assertEquals(0, p.getNoCards().size());
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testAddMultipleHandCards(){
        // add weapon hand card
        try {
            assertTrue(p.addHandCard("Knife", c));
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getHandCards().size());
        assertEquals("Knife", p.getHandCards().get(0).getName());
        assertEquals(5, c.getWeapons().size());
        assertEquals("Candlestick", c.getWeapons().get(0).getName());

        // add suspect hand card
        try {
            assertTrue(p.addHandCard("Green", c));
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, p.getHandCards().size());
        assertEquals("Green", p.getHandCards().get(1).getName());
        assertEquals(5, c.getSuspects().size());
        assertEquals("Peacock", c.getSuspects().get(2).getName());

        // add room hand card
        try {
            assertTrue(p.addHandCard("Kitchen", c));
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(3, p.getHandCards().size());
        assertEquals("Kitchen", p.getHandCards().get(2).getName());
        assertEquals(8, c.getRooms().size());
        assertEquals("Ball Room", c.getRooms().get(3).getName());
    }

    @Test
    public void addOneHandCardTwice(){
        try {
            assertTrue(p.addHandCard("Wrench", c));
            assertFalse(p.addHandCard("Wrench", c));
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getHandCards().size());
        assertEquals(5, c.getWeapons().size());

    }

    @Test
    public void testAddInvalidHandCard(){
        try {
            p.addHandCard("??", c);
            fail();
        } catch (InvalidCardName e) {
            // expected
        }
        assertEquals(0, p.getHandCards().size());
    }

    @Test
    public void testAddNoCard(){
        try {
            p.addNoCard("Green");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getNoCards().size());
        assertEquals("Green", p.getNoCards().get(0));
    }

    @Test
    public void testAddOneNoCardTwice(){
        try {
            p.addNoCard("Green");
            p.addNoCard("Green");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getNoCards().size());
        assertEquals("Green", p.getNoCards().get(0));
    }

    @Test
    public void testAddInvalidNoCard(){
        try {
            p.addNoCard("Billiard");
            fail();
        } catch (InvalidCardName e) {

        }
        assertEquals(0, p.getNoCards().size());
    }

    @Test
    public void testAddUncheckedCards(){
        // add all
        try {
            p.addUncheckedCards("Mustard", "Wrench", "Kitchen");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getUncheckedCards().size());
        assertEquals(3, p.getUncheckedCards().get(0).size());
        assertEquals("Mustard", p.getUncheckedCards().get(0).get(0));
        assertEquals("Wrench", p.getUncheckedCards().get(0).get(1));
        assertEquals("Kitchen", p.getUncheckedCards().get(0).get(2));

        // add some
        try {
            p.addNoCard("Rope");
            p.addNoCard("Kitchen");
            p.addUncheckedCards("Mustard", "Rope", "Kitchen");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(1, p.getUncheckedCards().get(1).size());
        assertEquals("Mustard", p.getUncheckedCards().get(1).get(0));
    }

    @Test
    public void testAddInvalidUncheckedCard(){
        try {
            p.addUncheckedCards("Scarlett", "Knife", "Hal");
            fail();
        } catch (InvalidCardName e) {

        }
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testRemoveUncheckedCard() {
        try {
            p.addUncheckedCards("White", "Rope", "Hall");
            p.addUncheckedCards("White", "Candlestick", "Hall");
            p.removeUncheckedCard("Hall");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(2, p.getUncheckedCards().get(0).size());
        assertEquals(2, p.getUncheckedCards().get(1).size());
        assertFalse(p.getUncheckedCards().get(0).contains("Hall"));
        assertFalse(p.getUncheckedCards().get(1).contains("Hall"));

        try {
            p.removeUncheckedCard("Rope");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals(1, p.getUncheckedCards().get(0).size());
        assertEquals(2, p.getUncheckedCards().get(1).size());
        assertFalse(p.getUncheckedCards().get(0).contains("Rope"));
    }

    @Test
    public void testRemoveInvalidUncheckedCard() {
        try {
            p.addUncheckedCards("Plum", "Rope", "Hall");
            p.removeUncheckedCard("Hall ");
            fail();
        } catch (InvalidCardName e) {
            // expected
        }
        assertEquals(1, p.getUncheckedCards().size());
        assertEquals(3, p.getUncheckedCards().get(0).size());
    }

    @Test
    public void testCheckUncheckedCards(){
        try {
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
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        } catch (EmptyUncheckedSet e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCheckUncheckedCardsHaveEmptySet(){
        try {
            p.addUncheckedCards("Peacock", "Revolver", "Lounge");
            p.addUncheckedCards("White", "Revolver", "Lounge");
            p.removeUncheckedCard("Revolver");
            p.removeUncheckedCard("Lounge");
            p.removeUncheckedCard("Peacock");
            p.checkUncheckedCards(c);
            fail();
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        } catch (EmptyUncheckedSet e) {
            // expected
        }
    }

}
