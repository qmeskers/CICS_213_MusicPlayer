/*
 * Class to keep the list of playlist objects in them
 */
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PlaylistCollections {
    private List<Playlist> playlists;

    public PlaylistCollections() {
        this.playlists = new ArrayList<>();
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
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