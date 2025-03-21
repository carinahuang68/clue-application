package model;

import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import persistence.JsonReader;

public class TestClueGame {
    @Test
    public void testConstructor1() {
        String detectiveName = "d";
        String[] playerNames = {"a", "b", "c"};
        String[] myHandCards = {"hall", "Plum", "study", "knife"};
        ClueGame testNewGame = new ClueGame(detectiveName, playerNames, myHandCards);
        assertEquals(4, testNewGame.getNumPlayers());
        assertEquals(3, testNewGame.getPlayers().size());
        assertEquals("a", testNewGame.getPlayer("a").getName());
        assertEquals("d", testNewGame.getDetective().getName());
    }

    @Test
    public void testConstructor2() {
        JsonReader jsonReader = new JsonReader("data/clueDemo.json");
        try {
            Detective detective = jsonReader.readDetective();
            List<Player> players = jsonReader.readPlayers();
            ClueGame testGame = new ClueGame(detective, players);
            assertEquals(4, testGame.getNumPlayers());
            assertEquals(3, testGame.getPlayers().size());
            assertEquals("K", testGame.getPlayer("K").getName());
            assertEquals("Carina", testGame.getDetective().getName());
            assertNull(testGame.getPlayer("a"));
        } catch (IOException e) {
            fail();
        }
        
    }
}
