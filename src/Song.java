import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Song {
	//private fields to hold attributes
    private String artist;
    private String album;
    private String name;
    private int yearReleased;
    private String url;
    private String genre;
    private int duration;
    

    //constructor
    public Song(String artist, String album, String name, String url, int yearReleased, String genre, int duration) {
        this.artist = artist;
        this.album = album;
        this.name = name;
        this.url = url;
        this.yearReleased = yearReleased;
        this.genre = genre;
        this.duration = duration;
        
    }

    public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	//getters and setters
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getYearReleased() {
        return yearReleased;
    }

    public void setYearReleased(int yearReleased) {
        this.yearReleased = yearReleased;
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
       
