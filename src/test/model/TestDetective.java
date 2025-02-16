package model;

import java.util.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.InvalidCardName;
import exception.InvalidRoomName;
import exception.InvalidSuspectName;
import exception.InvalidWeaponName;

public class TestDetective {
    Detective d;
    List<String> mycards;

    @BeforeEach
    public void runBefore() {
        mycards = new ArrayList<>();
        mycards.add("Mustard");
        mycards.add("Green");
        mycards.add("Revolver");
        mycards.add("Ball Room");
        try {
            d = new Detective("Carina", mycards);
        } catch (InvalidCardName e) {
            fail("Invalid card name!");
        }
    }

    @Test
    public void testInit() {
        // test init
        assertEquals("Carina", d.getName());
        assertEquals(mycards, d.getHandcards());

        // test reset() + eliminateCards()
        assertEquals(4, d.getSuspects().size());
        assertNull(d.getSuspect("Mustard"));
        assertNull(d.getSuspect("Green"));

        assertEquals(5, d.getWeapons().size());
        assertNull(d.getWeapon("Revolver"));

        assertEquals(8, d.getRooms().size());
        assertNull(d.getRoom("Ball Room"));
    }

    @Test
    public void testEliminateCard() {
        try {
            d.eliminateCard("Knife");
            assertEquals(4, d.getWeapons().size());
            assertNull(d.getWeapon("Knife"));

            d.eliminateCard("Scarlett");
            assertEquals(3, d.getSuspects().size());
            assertNull(d.getSuspect("Scarlett"));

            d.eliminateCard("Billiard Room");
            assertEquals(7, d.getRooms().size());
            assertNull(d.getRoom("Billiard Room"));

        } catch (InvalidCardName e) {
            fail();
        }
    }

    @Test
    public void testEliminateInvalidCard() {
        try {
            d.eliminateCard("rope");
            fail();
        } catch (InvalidCardName e) {
            // expected
        }
    }

    // Tests for removeSuspect()
    @Test
    public void testTrueRemoveSuspect() {
        Suspect plum = d.getSuspect("Plum");
        try {
            assertEquals(plum, d.removeSuspect("Plum"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(3, d.getSuspects().size());
        assertEquals("Peacock", d.getSuspects().get(0).getName());

        Suspect scarlett = d.getSuspect("Scarlett");
        try {
            assertEquals(scarlett, d.removeSuspect("Scarlett"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(2, d.getSuspects().size());
        assertEquals("White", d.getSuspects().get(1).getName());
    }

    @Test
    public void testFalseRemoveSuspect() {
        try {
            assertNull(d.removeSuspect("Green"));
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertEquals(4, d.getSuspects().size());
    }

    @Test
    public void testInvalidRemoveSuspect() {
        try {
            d.removeSuspect("Hall");
            fail("not expected to reach this line");
        } catch (InvalidSuspectName e) {
            // expected
        }
        assertEquals(4, d.getSuspects().size());
    }

    // Tests for removeWeapon()
    @Test
    public void testTrueRemoveWeapon() {
        Weapon knife = d.getWeapon("Knife");
        try {
            assertEquals(knife, d.removeWeapon("Knife"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(4, d.getWeapons().size());
        assertNull(d.getWeapon("Knife"));

        Weapon wrench = d.getWeapon("Wrench");
        try {
            assertEquals(wrench, d.removeWeapon("Wrench"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(3, d.getWeapons().size());
        assertNull(d.getWeapon("Knife"));
    }

    @Test
    public void testFalseRemoveWeapon() {
        try {
            assertNull(d.removeWeapon("Revolver"));
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertEquals(5, d.getWeapons().size());
        assertNull(d.getWeapon("Revolver"));
    }

    @Test
    public void testInvalidRemoveWeapon() {
        try {
            d.removeWeapon("rope");
            fail();
        } catch (InvalidWeaponName e) {
            // expected
        }
        assertEquals(5, d.getWeapons().size());
    }

    // Tests for removeRoom()
    @Test
    public void testTrueRemoveRoom() {
        Room dining = d.getRoom("Dining Room");
        try {
            assertEquals(dining, d.removeRoom("Dining Room"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(7, d.getRooms().size());
        assertNull(d.getWeapon("Dining Room"));

        Room library = d.getRoom("Library");
        try {
            assertEquals(library, d.removeRoom("Library"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(6, d.getRooms().size());
        assertNull(d.getRoom("Library"));
    }

    @Test
    public void testFalseRemoveRoom() {
        try {
            assertNull(d.removeRoom("Ball Room"));
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertEquals(8, d.getRooms().size());
    }

    @Test
    public void testInvalidRemoveRoom() {
        try {
            d.removeRoom("Dining");
            fail();
        } catch (InvalidRoomName e) {
            // expected
        }
        assertEquals(8, d.getRooms().size());
    }

    @Test
    public void testFoundSuspectTrue() {
        try {
            d.removeSuspect("Mustard");
            d.removeSuspect("Plum");
            d.removeSuspect("Green");
            d.removeSuspect("Peacock");
            d.removeSuspect("White");
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertTrue(d.foundSuspect());
    }

    @Test
    public void testFoundSuspectFalse() {
        try {
            d.removeSuspect("Mustard");
            d.removeSuspect("Green");
            d.removeSuspect("Peacock");
            d.removeSuspect("White");
        } catch (InvalidSuspectName e) {
            fail("Unexpected exception");
        }
        assertFalse(d.foundSuspect());
    }

    @Test
    public void testFoundWeaponTrue() {
        try {
            d.removeWeapon("Knife");
            d.removeWeapon("Candlestick");
            d.removeWeapon("Revolver");
            d.removeWeapon("Rope");
            d.removeWeapon("Lead Pipe");
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertTrue(d.foundWeapon());
    }

    @Test
    public void testFoundWeaponFalse() {
        try {
            d.removeWeapon("Knife");
            d.removeWeapon("Revolver");
            d.removeWeapon("Rope");
        } catch (InvalidWeaponName e) {
            fail("Unexpected exception");
        }
        assertFalse(d.foundWeapon());
    }

    @Test
    public void testFoundRoomTrue() {
        try {
            d.removeRoom("Hall");
            d.removeRoom("Lounge");
            d.removeRoom("Kitchen");
            d.removeRoom("Ball Room");
            d.removeRoom("Conservatory");
            d.removeRoom("Billiard Room");
            d.removeRoom("Library");
            d.removeRoom("Study");
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertTrue(d.foundRoom());
    }

    @Test
    public void testFoundRoomFalse() {
        try {
            d.removeRoom("Lounge");
            d.removeRoom("Kitchen");
            d.removeRoom("Ball Room");
            d.removeRoom("Conservatory");
            d.removeRoom("Billiard Room");
            d.removeRoom("Library");
            d.removeRoom("Study");
        } catch (InvalidRoomName e) {
            fail("Unexpected exception");
        }
        assertFalse(d.foundRoom());
    }

    @Test
    public void testGetSuspect() {
        assertEquals(d.getSuspects().get(2), d.getSuspect("Scarlett"));
    }

    @Test
    public void testGetWeapon() {
        assertEquals(d.getWeapons().get(4), d.getWeapon("Wrench"));
    }

    @Test
    public void testGetRoom() {
        assertEquals(d.getRooms().get(7), d.getRoom("Study"));
    }

}
