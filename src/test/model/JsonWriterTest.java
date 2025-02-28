package model;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

// import org.junit.Test;

import persistence.JsonReader;
import persistence.JsonWriter;

public class JsonWriterTest {

    @Test
    public void testWriterInvalidFile() {
        try {
            Detective d = new Detective("Me", null);
            JsonWriter writer = new JsonWriter("./data/my\\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    public void testWriterInit() {
        try {
            List<String> myHandcards = new ArrayList<>();
            myHandcards.add("Mustard");
            myHandcards.add("Plum");
            myHandcards.add("Knife");
            Detective d = new Detective("Me", myHandcards);
            List<Player> players = new ArrayList<>();
            players.add(new Player("P1"));
            players.add(new Player("P2"));
            players.add(new Player("P3"));
            for (Player p : players) {
                for (String card : myHandcards) {
                    p.addNoCard(card);
                }
            }

            JsonWriter writer = new JsonWriter("./data/testWriterInit.json");
            writer.open();
            writer.write(d, players);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterInit.json");
            d = reader.readDetective();
            assertEquals("Me", d.getName());
            assertEquals(3, d.getHandcards().size());
            assertEquals(4, d.getSuspects().size());
            assertEquals(5, d.getWeapons().size());
            assertEquals(9, d.getRooms().size());

            players = reader.readPlayers();
            assertEquals(3, players.size());
            assertEquals("P1", players.get(0).getName());
            assertEquals("P2", players.get(1).getName());
            assertEquals("P3", players.get(2).getName());
            assertEquals(0, players.get(0).getHandCards().size());
            assertEquals(3, players.get(0).getNoCards().size());
            assertEquals("Plum", players.get(0).getNoCards().get(1));
            assertEquals(0, players.get(0).getUncheckedCards().size());

        } catch (Exception e) {
            fail("Exception should not have been thrown");
        }
    }

    // @Test
    // public void testWriterInitDetective() {
    // try {
    // List<String> myHandcards = new ArrayList<>();
    // myHandcards.add("Mustard");
    // myHandcards.add("Plum");
    // myHandcards.add("Knife");
    // Detective d = new Detective("Me", myHandcards);
    // JsonWriter writer = new JsonWriter("./data/testWriterInitDetective.json");
    // writer.open();
    // writer.write(d);
    // writer.close();

    // JsonReader reader = new JsonReader("./data/testWriterInitDetective.json");
    // d = reader.readDetective();
    // assertEquals("Me", d.getName());
    // assertEquals(3, d.getHandcards().size());
    // assertEquals(4, d.getSuspects().size());
    // assertEquals(5, d.getWeapons().size());
    // assertEquals(9, d.getRooms().size());
    // } catch (Exception e) {
    // fail("Exception should not have been thrown");
    // }
    // }

    // @Test
    // public void testWriterInitPlayers() {
    // try {
    // List<Player> players = new ArrayList<>();
    // players.add(new Player("P1"));
    // players.add(new Player("P2"));
    // players.add(new Player("P3"));
    // List<String> myHandCards = new ArrayList<>();
    // myHandCards.add("Mustard");
    // myHandCards.add("Plum");
    // myHandCards.add("Knife");
    // for (Player p : players) {
    // for (String card : myHandCards) {
    // p.addNoCard(card);
    // }
    // }

    // JsonWriter writer = new JsonWriter("./data/testWriterInitPlayers.json");
    // writer.open();
    // writer.write(players);
    // writer.close();

    // JsonReader reader = new JsonReader("./data/testWriterInitPlayers.json");
    // players = reader.readPlayers();
    // assertEquals(3, players.size());
    // assertEquals("P1", players.get(0).getName());
    // assertEquals("P2", players.get(1).getName());
    // assertEquals("P3", players.get(2).getName());
    // assertEquals(0, players.get(0).getHandCards().size());
    // assertEquals(3, players.get(0).getNoCards().size());
    // assertEquals("Plum", players.get(0).getNoCards().get(1));
    // assertEquals(0, players.get(0).getUncheckedCards().size());

    // } catch (Exception e) {
    // fail("Exception should not have been thrown");
    // }
    // }

    // @Test
    // public void testWriterGeneralDetectiveAndPlayers() {
    // try {
    // ClueApp clue = new ClueApp(3);
    // List<Player> players = new ArrayList<>();
    // players.add(new Player("P1"));
    // players.add(new Player("P2"));
    // players.add(new Player("P3"));
    // players.get(0).addUncheckedCards("Scarlett", "Lead Pipe", "Hall");

    // JsonWriter writer = new JsonWriter("./data/testWriterInitPlayers.json");
    // writer.open();
    // writer.write(players);
    // writer.close();

    // } catch (Exception e) {
    //
    // }
    // }
}
