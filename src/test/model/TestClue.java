package model;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.InvalidRoomName;
import exception.InvalidSuspectName;
import exception.InvalidWeaponName;

public class TestClue {
    Clue c;

    @BeforeEach
    public void runBefore(){
        c = new Clue();
    }

    @Test
    public void testInitOrReset(){
        assertEquals(6, c.getSuspects().size());
        assertEquals("Mustard", c.getSuspects().get(0).getName());
        assertEquals("White", c.getSuspects().get(5).getName());
        assertEquals(6, c.getWeapons().size());
        assertEquals("Knife", c.getWeapons().get(0).getName());
        assertEquals("Lead Pipe", c.getWeapons().get(4).getName());
        assertEquals(9, c.getRooms().size());
        assertEquals("Hall", c.getWeapons().get(0).getName());
        assertEquals("Kitchen", c.getWeapons().get(3).getName());
    }

    // Tests for removeSuspect()
    @Test
    public void testTrueRemoveSuspect(){
        try {
            assertTrue(c.removeSuspect("Plum"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(5, c.getSuspects().size());
        assertEquals("Green", c.getSuspects().get(1).getName());

        try {
            assertTrue(c.removeSuspect("Mustard"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(4, c.getSuspects().size());
        assertEquals("Green", c.getSuspects().get(0).getName());
    }

    @Test
    public void testFalseRemoveSuspect(){
        try {
            assertTrue(c.removeSuspect("White"));
            assertFalse(c.removeSuspect("White"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(5, c.getSuspects().size());
        assertEquals("Scarlett", c.getSuspects().get(4).getName());
    }

    @Test
    public void testInvalidRemoveSuspect(){
        try {
            c.removeSuspect("Hall");
            fail();
        } catch (InvalidSuspectName e) {
            // expected
        }
        assertEquals(6, c.getSuspects().size());
    }


    // Tests for removeWeapon()
    @Test
    public void testTrueRemoveWeapon(){
        try {
            assertTrue(c.removeWeapon("Knife"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(5, c.getWeapons().size());
        assertEquals("Candlestick", c.getWeapons().get(0).getName());

        try {
            assertTrue(c.removeWeapon("Wrench"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(4, c.getWeapons().size());
    }

    @Test
    public void testFalseRemoveWeapon(){
        try {
            assertTrue(c.removeWeapon("Rope"));
            assertFalse(c.removeWeapon("Rope"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(5, c.getWeapons().size());
        assertEquals("Lead Pipe", c.getWeapons().get(3).getName());
    }

    @Test
    public void testInvalidRemoveWeapon(){
        try {
            c.removeWeapon("rope");
            fail();
        } catch (InvalidWeaponName e) {
            // expected
        }
        assertEquals(6, c.getSuspects().size());
    }

    // Tests for removeRoom()
    @Test
    public void testTrueRemoveRoom(){
        try {
            assertTrue(c.removeRoom("Dining Room"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(8, c.getRooms().size());
        assertEquals("Kitchen", c.getRooms().get(2).getName());

        try {
            assertTrue(c.removeRoom("Library"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(7, c.getRooms().size());
        assertEquals("Study", c.getRooms().get(6).getName());
    }

    @Test
    public void testFalseRemoveRoom(){
        try {
            assertTrue(c.removeRoom("Conservatory"));
            assertFalse(c.removeRoom("Conservatory"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(8, c.getRooms().size());
        assertEquals("Billiard Room", c.getRooms().get(5).getName());
    }

    @Test
    public void testInvalidRemoveRoom(){
        try {
            c.removeRoom("Dining");
            fail();
        } catch (InvalidRoomName e) {
            // expected
        }
        assertEquals(9, c.getRooms().size());
    }

    @Test
    public void testFoundSuspectTrue(){
        try {
            c.removeSuspect("Mustard");
            c.removeSuspect("Plum");
            c.removeSuspect("Green");
            c.removeSuspect("Peacock");
            c.removeSuspect("White");
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertTrue(c.foundSuspect());
    }

    @Test
    public void testFoundSuspectFalse(){
        try {
            c.removeSuspect("Mustard");
            c.removeSuspect("Green");
            c.removeSuspect("Peacock");
            c.removeSuspect("White");
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertFalse(c.foundSuspect());
    }

    @Test
    public void testFoundWeaponTrue(){
        try {
            c.removeWeapon("Knife");
            c.removeWeapon("Candlestick");
            c.removeWeapon("Revolver");
            c.removeWeapon("Rope");
            c.removeWeapon("Lead Pipe");
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertTrue(c.foundWeapon());
    }

    @Test
    public void testFoundWeaponFalse(){
        try {
            c.removeWeapon("Knife");
            c.removeWeapon("Revolver");
            c.removeWeapon("Rope");
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertFalse(c.foundWeapon());
    }

    @Test
    public void testFoundRoomTrue(){
        try {
            c.removeRoom("Hall");
            c.removeRoom("Lounge");
            c.removeRoom("Kitchen");
            c.removeRoom("Ball Room");
            c.removeRoom("Conservatory");
            c.removeRoom("Billiard Room");
            c.removeRoom("Library");
            c.removeRoom("Study");
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertTrue(c.foundRoom());
    }

    @Test
    public void testFoundRoomFalse(){
        try {
            c.removeRoom("Lounge");
            c.removeRoom("Kitchen");
            c.removeRoom("Ball Room");
            c.removeRoom("Conservatory");
            c.removeRoom("Billiard Room");
            c.removeRoom("Library");
            c.removeRoom("Study");
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertFalse(c.foundRoom());
    }

}
