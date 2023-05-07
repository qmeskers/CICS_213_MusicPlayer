/**
 * This is the class that contains elements of the user object, User object will be used in conjunction with
 * playlists that get assigned to each user
 * @author CISC213.N81
 *
 */
public class User {
	String firstname; //User's first name
	String lastname; //User's last name
	String email; //User's email
	String password; //User's password
	String username;
	int userID;
	String playlistFileName; //fileName for user's playlist
	PlaylistCollections usersPlaylist; //user's collection of playlists
	/**
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param password
	 * @param userID
	 */
	public User(String firstname, String lastname, String email, String password, String username, int userID) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.username = username;
		this.userID = userID;
		this.playlistFileName = username + "Playlists.json";
		this.usersPlaylist = new PlaylistCollections();
		Player.userList.add(this);
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @return the name of this user's playlist file
	 */
	public String getPlaylistFileName() {
		return playlistFileName;
	}
	/**
	 * @return the users playlist collection
	 */
	public PlaylistCollections getUsersPlaylist() {
		return usersPlaylist;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @param playlistFileName the file name to set
	 */
	public void setPlaylistFileName(String playlistFileName) {
		this.playlistFileName = playlistFileName;
	}
	/**
	 * @param usersPlaylist the PlaylistCollection for this user
	 */
	public void setUsersPlaylist(PlaylistCollections usersPlaylist) {
		this.usersPlaylist = usersPlaylist;
	}
}