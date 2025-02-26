package persistence;
import org.json.JSONArray;
import org.json.JSONObject;

import model.Detective;
import model.Player;

import java.io.*;
import java.util.List;


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
        // stub
        return null;
    }

    /*
     * EFFECTS: reads list of player from file and returns it
     * throws IOException if an error occurs reading data from file
     */
    public List<Player> readPlayers() throws IOException {
        // stub
        return null;
    }
    
    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        return "";
    }
    
}
