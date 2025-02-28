package persistence;
import org.json.JSONArray;
import org.json.JSONObject;

import model.Detective;
import model.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import model.Card;


// Represents a reader that reads detective and players from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    /*
     * EFFECTS: reads detective from file and returns it
     * throws IOException if an error occurs reading data from file
     */
    public Detective readDetective() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseDetective(jsonObject);
    }

    /*
     * EFFECTS: reads list of player from file and returns it
     * throws IOException if an error occurs reading data from file
     */
    public List<Player> readPlayers() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parsePlayers(jsonObject);
    }
    
    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses detective from JSON object and returns it
    private Detective parseDetective(JSONObject jsonObject) {
        String name = jsonObject.getString("name");

        JSONArray jsonArray = jsonObject.getJSONArray("myhandcards");
        // Convert JSONArray to List<String>
        List<String> handCards = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            handCards.add(jsonArray.getString(i));  
        }

        Detective d = new Detective(name, handCards);

        JSONArray jsonSuspects = jsonObject.getJSONArray("mysuspects");
        List<String> suspects = new ArrayList<>();
        for (int i = 0; i < jsonSuspects.length(); i++) {
            suspects.add(jsonSuspects.getString(i));  
        }
        d.setSuspects(suspects);

        JSONArray jsonWeapons = jsonObject.getJSONArray("myweapons");
        List<String> weapons = new ArrayList<>();
        for (int i = 0; i < jsonWeapons.length(); i++) {
            weapons.add(jsonWeapons.getString(i));  
        }
        d.setWeapons(weapons);

        JSONArray jsonRooms = jsonObject.getJSONArray("myrooms");
        List<String> rooms = new ArrayList<>();
        for (int i = 0; i < jsonRooms.length(); i++) {
            rooms.add(jsonRooms.getString(i));  
        }
        d.setRooms(rooms);

        return d;
    }

    // EFFECTS: parses players from JSON object and returns it
    private List<Player> parsePlayers(JSONObject jsonObject) {
        List<Player> players = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject playerJson = jsonArray.getJSONObject(i);
            Player player = parsePlayer(playerJson);
            players.add(player);
        }
        return players;
    }

    private Player parsePlayer(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Player player = new Player(name);

        JSONArray jsonHandcards = jsonObject.getJSONArray("handcards");
        for (int i = 0; i < jsonHandcards.length(); i++) {
            player.addHandCard(jsonHandcards.getString(i));  
        }

        JSONArray jsonNocards = jsonObject.getJSONArray("nocards");
        for (int i = 0; i < jsonNocards.length(); i++) {
            player.addNocard(jsonNocards.getString(i));  
        }

        JSONArray jsonUncheckedset = jsonObject.getJSONArray("uncheckedcards");
        for (int i = 0; i < jsonUncheckedset.length(); i++) {
            JSONArray jsonUncheckedcards = jsonUncheckedset.getJSONArray(i);
            List<String> uncheckedCards = new ArrayList<>();
            for (int k = 0; k < jsonUncheckedcards.length(); k++) {
                uncheckedCards.add(jsonUncheckedcards.getString(k));
            }
            player.addUncheckedCards(uncheckedCards);
        }

        return player;
    }

}
