package model;

import model.*;
import persistence.JsonReader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            List<Player> players = new ArrayList<>();
            players = reader.readPlayers();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyPlayers() {
        JsonReader reader = new JsonReader("./data/testReaderEmpty.json");
        try {
            List<Player> players = reader.readPlayers();

            assertEquals(3, players.size());
            assertEquals("Elsa", players.get(0).getName());
            assertEquals("Steve", players.get(1).getName());
            assertEquals("Kelvin", players.get(2).getName());
            assertEquals(0, players.get(0).getHandCards().size());
            assertEquals(0, players.get(0).getNoCards().size());
            assertEquals(0, players.get(0).getUncheckedCards().size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }


    @Test
    void testReaderEmptyDetective() {
        JsonReader reader = new JsonReader("./data/testReaderEmpty.json");
        try {
            Detective d = reader.readDetective();
            assertEquals("Car", d.getName());
            assertEquals(0, d.getHandcards().size());
            assertEquals(0, d.getSuspects().size());
            assertEquals(0, d.getWeapons().size());
            assertEquals(0, d.getRooms().size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneral() {
        JsonReader reader = new JsonReader("./data/testReaderGeneral.json");
        try {
            List<Player> players = reader.readPlayers();
            assertEquals("Elsa", players.get(0).getName());
            assertEquals(2, players.get(0).getHandCards().size());
            assertEquals(4, players.get(0).getNoCards().size());
            assertEquals(2, players.get(0).getUncheckedCards().size());
            assertEquals(2, players.get(0).getUncheckedCards().get(0).size());
            assertEquals(3, players.get(0).getUncheckedCards().get(1).size());

            assertEquals("Steve", players.get(1).getName());
            assertEquals(1, players.get(1).getHandCards().size());
            assertEquals("Kitchen", players.get(1).getHandCards().get(0).getName());
            assertEquals(5, players.get(1).getNoCards().size());
            assertEquals(0, players.get(1).getUncheckedCards().size());

            assertEquals("Kelvin", players.get(2).getName());
            assertEquals(0, players.get(2).getHandCards().size());
            assertEquals(6, players.get(2).getNoCards().size());
            assertEquals(1, players.get(2).getUncheckedCards().size());
            assertEquals(3, players.get(2).getUncheckedCards().get(0).size());
            

            Detective d = reader.readDetective();
            assertEquals("Car", d.getName());
            assertEquals(3, d.getHandcards().size());
            assertEquals(5, d.getSuspects().size());
            assertEquals(4, d.getWeapons().size());
            assertEquals(5, d.getRooms().size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
