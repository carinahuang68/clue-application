package model;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.InvalidCardName;


public class TestPlayer {

    Player p;

    @BeforeEach
    public void runBefore(){
        p = new Player("John");
    }

    @Test
    public void testInit(){
        assertEquals("John", p.getName());
        assertEquals(0, p.getHandCards().size());
        assertEquals(0, p.getNoCards().size());
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testAddHandCard(){
        try {
            p.addHandCard("Knife");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getHandCards().size());
        assertEquals("Knife", p.getHandCards().get(0).getName());
    }

    @Test
    public void testAddInvalidHandCard(){
        try {
            p.addHandCard("knife");
            fail();
        } catch (InvalidCardName e) {

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
        assertEquals("Green", p.getHandCards().get(0).getName());
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
    public void testAddUncheckedCard(){
        try {
            p.addUncheckedCard("Kitchen");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(1, p.getUncheckedCards().size());
        assertEquals("Kitchen", p.getHandCards().get(0).getName());
    }

    @Test
    public void testAddInvalidUncheckedCard(){
        try {
            p.addNoCard("Scarlet");
            fail();
        } catch (InvalidCardName e) {

        }
        assertEquals(0, p.getUncheckedCards().size());
    }

    @Test
    public void testRemoveUncheckedCard() {
        try {
            p.addUncheckedCard("Rope");
            p.addUncheckedCard("White");
            p.addUncheckedCard("Hall");
            p.removeUncheckedCard("Hall");
        } catch (InvalidCardName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, p.getUncheckedCards().size());
        assertEquals("Rope", p.getUncheckedCards().get(0).getName());
        assertEquals("White", p.getUncheckedCards().get(1).getName());

    }

}
