/**
 *  Class to keep the list of playlist objects in them
 *  The file i/o methods for collections of playlists used by both system and user 
 *  are contained in this class
 *  @auhor CISC213.N81
 */
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PlaylistCollections {
    private List<Playlist> playlists;
    JSONArray collectionAsJson;

    public PlaylistCollections() {
        this.playlists = new ArrayList<>();
        this.collectionAsJson = new JSONArray();
    }

    public void addPlaylist(Playlist playlist, String fileName) {
        playlists.add(playlist);
        updateJsonFile(fileName);
    }

    @SuppressWarnings("unchecked")
	public void updateJsonFile(String fileName) {
    	JSONObject newUserPlaylistsJson = new JSONObject();
    	JSONArray playlistsJsonArray = new JSONArray();
    	for (Playlist playlist : playlists) {
    		JSONObject currentPlaylistJson = new JSONObject();
    		currentPlaylistJson.put("playlist name",  playlist.getName());
    		JSONArray songJsonArray = new JSONArray();
    		for (Song song : playlist.getSongs()) {
    			JSONObject currentSongJson = new JSONObject();
				currentSongJson.put("artist", song.getArtist());
				currentSongJson.put("album",  song.getAlbum());
				currentSongJson.put("name", song.getName());
				currentSongJson.put("yearReleased", song.getYearReleased());
				currentSongJson.put("url", song.getUrl());
				currentSongJson.put("genre", song.getGenre());
				currentSongJson.put("duration", song.getDuration());
				songJsonArray.add(currentSongJson);
    		}
    		currentPlaylistJson.put("songs", songJsonArray);
    		playlistsJsonArray.add(currentPlaylistJson);
    	}
    	newUserPlaylistsJson.put("playlists",  playlistsJsonArray);
    	try {
			Files.write(Paths.get(Player.currentUser.getPlaylistFileName()), newUserPlaylistsJson.toJSONString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	public void removePlaylist(Playlist playlist, String fileName) {
        playlists.remove(playlist);
        updateJsonFile(fileName);
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public Playlist getPlaylistByName(String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
    
    public static List<Playlist> loadPlaylistsFromJson(String filename){
    	List<Playlist> playlistList = new ArrayList<>();
    	
    	try {
    		JSONParser parser = new JSONParser();
    		Object jsonData = parser.parse(new FileReader(filename));
    		
    		if (jsonData instanceof JSONObject) {
    			
    			JSONObject json = (JSONObject) jsonData;
    			JSONArray playlistArray = (JSONArray) json.get("playlists");
    			for (Object obj : playlistArray) {
                    JSONObject playlistJson = (JSONObject) obj;
                    Playlist playlist = createPlaylistFromJson(playlistJson);
                    if (playlist != null) {
                        playlistList.add(playlist);
                    }
                }
    		} else if (jsonData instanceof JSONArray) {
    			JSONArray playlistArray = (JSONArray) jsonData;
    			for (Object obj : playlistArray) {
                    JSONObject playlistJson = (JSONObject) obj;
                    Playlist playlist = createPlaylistFromJson(playlistJson);
                    if (playlist != null) {
                        playlistList.add(playlist);
                    }
                }
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return playlistList;
    }

	private static Playlist createPlaylistFromJson(JSONObject playlistJson) {
		try {
			String name = (String) playlistJson.get("playlist name");
			Playlist current = new Playlist(name);
			JSONArray songArray = (JSONArray) playlistJson.get("songs");
			for (Object obj : songArray) {
				JSONObject songJson = (JSONObject) obj;
				Song song = (Song) getSongFromPlaylistJson(songJson);
				if (song != null) {
					current.addSong(song);
				}
			}			
            return current;
        } catch (Exception e) {
            System.err.println("Error creating song from JSON: " + e.getMessage());
            return null;
        }
	}

	private static Object getSongFromPlaylistJson(JSONObject songJson) {
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