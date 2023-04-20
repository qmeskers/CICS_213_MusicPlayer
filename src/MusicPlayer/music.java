package MusicPlayer;

import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 */

/**
 * @author fisherj
 *
 */
public class music {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		//tests for song json loader
		 String filename = "songlist.json";
		 List<Song> songs = Song.loadSongsFromJson(filename);

	        // Print out some values from the song list
	        for (Song song : songs) {
	            System.out.println("Artist: " + song.getArtist());
	            System.out.println("Album: " + song.getAlbum());
	            System.out.println("Name: " + song.getName());
	            System.out.println("Year Released: " + song.getYearReleased());
	            System.out.println("URL: " + song.getUrl());
	            System.out.println("genre " + song.getGenre());
	            System.out.println("duration " + song.getDuration());
	            System.out.println();
	        }
	    }
	}
