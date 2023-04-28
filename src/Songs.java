import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * This class loads the songs from the songlist.json file and loads them into one large ArrayList 
 * 
 * Contains an unused method to add a song to the songlist.json file
 * @author CISC213.N81
 *
 */
public class Songs extends Song {
	 public Songs(String artist, String album, String name, String url, int yearReleased, String genre, int duration) {
		super(artist, album, name, url, yearReleased, genre, duration);
		// TODO Auto-generated constructor stub
	}

	// Method that loads songs from a JSON file and returns a list of Song objects
    public static List<Song> loadSongsFromJson(String filename) {
    	//empty list hold the objects
        List<Song> songList = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            Object jsonData = parser.parse(new FileReader(filename));
            
            // Check if the JSON data is an object basically check if it has the correct attributes
            if (jsonData instanceof JSONObject) {
              
                JSONObject json = (JSONObject) jsonData;
                JSONArray songArray = (JSONArray) json.get("songs");
                for (Object obj : songArray) {
                    JSONObject songJson = (JSONObject) obj;
                    Song song = createSongFromJson(songJson);
                    if (song != null) {
                        songList.add(song);
                    }
                }
            } else if (jsonData instanceof JSONArray) {
                // Handle array of objects
                JSONArray songArray = (JSONArray) jsonData;
                for (Object obj : songArray) {
                    JSONObject songJson = (JSONObject) obj;
                    Song song = createSongFromJson(songJson);
                    if (song != null) {
                        songList.add(song);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON file: " + e.getMessage());
        }
     // Return the list of Song objects

        return songList;
    }
    //Method that adds a song object to the json file. currently unused
    private static Song createSongFromJson(JSONObject songJson) {
        try {
            String artist = (String) songJson.get("artist");
            String album = (String) songJson.get("album");
            String name = (String) songJson.get("name");
            Long yearReleasedLong = (Long) songJson.get("yearReleased");
            String genre = (String) songJson.get("genre");
            Number durationNumber = (Number) songJson.get("duration");
            int duration = durationNumber != null ? durationNumber.intValue() : 0;
            int yearReleased = yearReleasedLong != null ? yearReleasedLong.intValue() : 0;
            String url = (String) songJson.get("url");
            return new Song(artist, album, name, url, yearReleased, genre, duration);
        } catch (Exception e) {
            System.err.println("Error creating song from JSON: " + e.getMessage());
            return null;
        }
    }
}
    

