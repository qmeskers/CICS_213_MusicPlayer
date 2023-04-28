 /**
  *  class to create multiple play lists and store song objects in them
  */
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name; //playlist name
    private List<Song> songs; //list of songs within playlist

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
    }
    //Adds song to playlist
    public void addSong(Song song) {
        songs.add(song);
    }
    //removes song from playlist
    public void removeSong(Song song) {
        songs.remove(song);
    }
}