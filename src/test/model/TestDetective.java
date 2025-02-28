package model;

import java.util.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        d = new Detective("Carina", mycards);
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
        d.eliminateCard("Knife");
        assertEquals(4, d.getWeapons().size());
        assertNull(d.getWeapon("Knife"));

        d.eliminateCard("Scarlett");
        assertEquals(3, d.getSuspects().size());
        assertNull(d.getSuspect("Scarlett"));

        d.eliminateCard("Billiard Room");
        assertEquals(7, d.getRooms().size());
        assertNull(d.getRoom("Billiard Room"));
    }

    // Tests for removeSuspect()
    @Test
    public void testTrueRemoveSuspect() {
        Suspect plum = d.getSuspect("Plum");
        assertEquals(plum, d.removeSuspect("Plum"));
        assertEquals(3, d.getSuspects().size());
        assertEquals("Peacock", d.getSuspects().get(0).getName());

        Suspect scarlett = d.getSuspect("Scarlett");
        assertEquals(scarlett, d.removeSuspect("Scarlett"));
        assertEquals(2, d.getSuspects().size());
        assertEquals("White", d.getSuspects().get(1).getName());
    }

    @Test
    public void testFalseRemoveSuspect() {
        assertNull(d.removeSuspect("Green"));
        assertEquals(4, d.getSuspects().size());
    }

    // Tests for removeWeapon()
    @Test
    public void testTrueRemoveWeapon() {
        Weapon knife = d.getWeapon("Knife");
        assertEquals(knife, d.removeWeapon("Knife"));
        assertEquals(4, d.getWeapons().size());
        assertNull(d.getWeapon("Knife"));

        Weapon wrench = d.getWeapon("Wrench");
        assertEquals(wrench, d.removeWeapon("Wrench"));
        assertEquals(3, d.getWeapons().size());
        assertNull(d.getWeapon("Knife"));
    }

    @Test
    public void testFalseRemoveWeapon() {
        assertNull(d.removeWeapon("Revolver"));
        assertEquals(5, d.getWeapons().size());
        assertNull(d.getWeapon("Revolver"));
    }

    // Tests for removeRoom()
    @Test
    public void testTrueRemoveRoom() {
        Room dining = d.getRoom("Dining Room");
        assertEquals(dining, d.removeRoom("Dining Room"));
        assertEquals(7, d.getRooms().size());
        assertNull(d.getWeapon("Dining Room"));

        Room library = d.getRoom("Library");
        assertEquals(library, d.removeRoom("Library"));
        assertEquals(6, d.getRooms().size());
        assertNull(d.getRoom("Library"));
    }

    @Test
    public void testFalseRemoveRoom() {
        assertNull(d.removeRoom("Ball Room"));
        assertEquals(8, d.getRooms().size());
    }

    @Test
    public void testFoundSuspectTrue() {
        d.removeSuspect("Mustard");
        d.removeSuspect("Plum");
        d.removeSuspect("Green");
        d.removeSuspect("Peacock");
        d.removeSuspect("White");
        assertTrue(d.foundSuspect());
    }

    @Test
    public void testFoundSuspectFalse() {
        d.removeSuspect("Mustard");
        d.removeSuspect("Green");
        d.removeSuspect("Peacock");
        d.removeSuspect("White");
        assertFalse(d.foundSuspect());
    }

    @Test
    public void testFoundWeaponTrue() {
        d.removeWeapon("Knife");
        d.removeWeapon("Candlestick");
        d.removeWeapon("Revolver");
        d.removeWeapon("Rope");
        d.removeWeapon("Lead Pipe");
        assertTrue(d.foundWeapon());
    }

    @Test
    public void testFoundWeaponFalse() {
        d.removeWeapon("Knife");
        d.removeWeapon("Revolver");
        d.removeWeapon("Rope");
        assertFalse(d.foundWeapon());
    }

    @Test
    public void testFoundRoomTrue() {
        d.removeRoom("Hall");
        d.removeRoom("Lounge");
        d.removeRoom("Kitchen");
        d.removeRoom("Ball Room");
        d.removeRoom("Conservatory");
        d.removeRoom("Billiard Room");
        d.removeRoom("Library");
        d.removeRoom("Study");
        assertTrue(d.foundRoom());
    }

    @Test
    public void testFoundRoomFalse() {
        d.removeRoom("Lounge");
        d.removeRoom("Kitchen");
        d.removeRoom("Ball Room");
        d.removeRoom("Conservatory");
        d.removeRoom("Billiard Room");
        d.removeRoom("Library");
        d.removeRoom("Study");
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
