package model;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import persistence.JsonReader;

public class TestClueGame {
    ClueGame testGame;
    ClueGame testNewGame;

    @BeforeEach
    public void runBefore() {
        String detectiveName = "d";
        String[] playerNames = { "a", "b", "c" };
        String[] myHandCards = { "hall", "Plum", "study", "knife" };
        testNewGame = new ClueGame(detectiveName, playerNames, myHandCards);

        JsonReader jsonReader = new JsonReader("data/clueDemo.json");
        try {
            Detective detective = jsonReader.readDetective();
            List<Player> players = jsonReader.readPlayers();
            testGame = new ClueGame(detective, players);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testConstructor1() {
        assertEquals(4, testNewGame.getNumPlayers());
        assertEquals(3, testNewGame.getPlayers().size());
        assertEquals("a", testNewGame.getPlayer("a").getName());
        assertEquals("d", testNewGame.getDetective().getName());
    }

    @Test
    public void testConstructor2() {
        assertEquals(4, testGame.getNumPlayers());
        assertEquals(3, testGame.getPlayers().size());
        assertEquals("K", testGame.getPlayer("K").getName());
        assertEquals("Carina", testGame.getDetective().getName());
        assertNull(testGame.getPlayer("a"));
    }

    @Test
    public void testAddNoCardsToPlayerInvalid() {
        testGame.addNoCardsToPlayer("Carina", "mustard", "rope", "hall");
        assertEquals(4, testGame.getPlayer("K").getNoCards().size());
        assertEquals(8, testGame.getPlayer("I").getNoCards().size());
        assertEquals(6, testGame.getPlayer("J").getNoCards().size());
    }

    @Test
    public void testAddNoCardsToPlayerValid() {
        testGame.addNoCardsToPlayer("J", "Mustard", "Rope", "Hall");
        assertEquals(8, testGame.getPlayer("J").getNoCards().size());
        assertTrue(testGame.getPlayer("J").getNoCards().contains("Mustard"));
        assertTrue(testGame.getPlayer("J").getNoCards().contains("Rope"));
    }

    @Test
    public void testAddHandCardsToPlayerValid() {
        assertTrue(testGame.getPlayer("K").handCardNames().contains("Green"));
        testGame.addHandCardToPlayer("K", "Green");
        assertEquals(2, testGame.getPlayer("K").getHandCards().size());
    }

    @Test
    public void testAddHandCardToPlayerInvalid() {
        testGame.addHandCardToPlayer("Carina", "Green");
        assertEquals(2, testGame.getPlayer("K").getHandCards().size());
    }

    @Test
    public void testEliminatePlayer() {
        testGame.eliminatePlayer("K");
        assertEquals(1, testGame.getEliminatedPlayers().size());
        assertEquals(3, testGame.getNumPlayersRemaining());
        testGame.eliminatePlayer("K");
        assertEquals(1, testGame.getEliminatedPlayers().size());
        assertEquals(3, testGame.getNumPlayersRemaining());
        testGame.eliminatePlayer("Carina");
        assertEquals(2, testGame.getEliminatedPlayers().size());
        assertEquals(2, testGame.getNumPlayersRemaining());
        testGame.eliminatePlayer("I");
        assertEquals(3, testGame.getEliminatedPlayers().size());
        assertEquals(1, testGame.getNumPlayersRemaining());
        testGame.eliminatePlayer("J");
        assertEquals(4, testGame.getEliminatedPlayers().size());
        assertEquals(0, testGame.getNumPlayersRemaining());
    }

    @Test
    public void testGetRemainingPlayers() {
        List<String> allPlayers = new ArrayList<>();
        allPlayers.add("Carina");
        allPlayers.add("I");
        allPlayers.add("J");
        allPlayers.add("K");
        assertEquals(4, testGame.getRemainingPlayers(allPlayers).size());
        testGame.eliminatePlayer("I");
        assertEquals(3, testGame.getRemainingPlayers(allPlayers).size());
        assertFalse(testGame.getRemainingPlayers(allPlayers).contains("I"));
        testGame.eliminatePlayer("K");
        testGame.eliminatePlayer("J");
        assertEquals("Carina", testGame.getRemainingPlayers(allPlayers).get(0));
    }

}
