/**
 *  Class to keep the list of playlist objects in them
 *  @auhor CISC213.N81
 */
 
import java.util.ArrayList;
import java.util.List;

public class PlaylistCollections {
    private List<Playlist> playlists; //The list of playlists

    public PlaylistCollections() {
        this.playlists = new ArrayList<>();
    }
    //method to add a playlist
    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }
    //method to remove a playlist
    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }
    //method that searches for a playlist by name
    public Playlist getPlaylistByName(String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
}