/**
 * This class contains elements of the song object, song objects will be loaded into playlists
 * @author CISC213.N81
 *
 */
public class Song {
	//private fields to hold attributes
    private String artist; //artist name
    private String album; //album name
    private String name; //song name
    private int yearReleased; //release year
    private String url; //YouTube embed link. All URL variables are structured like "www.youtube.com/embed/ |song id| ?autoplay=1"
    private String genre; //song genre
    private int duration; //song duration
    

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
    

}
       
