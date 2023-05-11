import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.wb.swt.SWTResourceManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import java.util.*;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
/**
 * This class contains the user interface, within that there are four tabs: a music player tab, a create playlist tab, a user management tab, and 
 * a view playlist tab
 * @author CISC213.N81
 *
 */
@SuppressWarnings("unused")
public class Player extends Shell {
	/*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
    times in the youtube player so the song doesn't cut off early */
	private int timer;  //this number will be replaced with the song lengths
	private int stopTime; //used for pause button
	private int resumeTime; //used for pausing and resuming
	private boolean paused = false; //used to stop timer when song is paused
	private boolean repeat = false;//used tell if song has been repeated
	private boolean isPlaying = false;//used to tell if the Queue is empty
	private int i = 0; //used to track location in Arraylist
	private java.util.List<Song> songList = new ArrayList<Song>();// this is the arraylist for the song list-brian
	public static java.util.List<User> userList = new ArrayList<User>();//this is the list of users
	private Song song; //song object
	public static User currentUser; //used to track which user is logged in
	public static PlaylistCollections currentUserPlaylist; //used to track which playlist has been selected
	public static Tree playlistList; //treemap of playlists
	public static loginWindow loginwindow; //object for the login window
	public LinkedList<Song> playQueue = new LinkedList<>(); 


	public Player(String artist, String album, String name, String url, int yearReleased, String genre, int duration) {
		this.song = new Song(artist, album, name, url, yearReleased, genre, duration);
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		//before launching the login window, read into memory a list of users and their credentials so the login window has something to verify against
		userListIO.loadUsersFromJson();
		//before launching the player, launch the login window
		loginwindow = new loginWindow();
		try {
			loginWindow dialog = new loginWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Create the shell.
	 * @param display
	 * @throws IOException 
	 * @wbp.parser.constructor
	 */
	@SuppressWarnings("static-access")
	public Player(Display display) throws IOException {
		super(display, SWT.SHELL_TRIM);
		setMaximumSize(new Point(600, 500));
		setMinimumSize(new Point(600, 500));
		setSize(709, 474);
		
		//Create the tabs
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(2, 0, 576, 457);
		
		//Create the composites to hold items within each tab
		Composite userTabComposite = new Composite(tabFolder, SWT.NONE);
		Composite composite = new Composite(tabFolder, SWT.NONE);
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		
		//load the playlistList with initial user data from login
		playlistList = new Tree(composite_1, SWT.BORDER);
		//put all the users playlists into the users playlist collection
		initialPlaylistLoad();

		//adds the icon to the top corner of the window
		Image play = new Image(display, "play.png");
		this.setImage(play);
		
		
		//User Management Controls:
		TabItem userManagementTab = new TabItem(tabFolder, SWT.NONE);
		userManagementTab.setText("User Management");
		userManagementTab.setControl(userTabComposite);
		
		Label currentUserLabel = new Label(userTabComposite, SWT.NONE);
		currentUserLabel.setBounds(50, 10, 80, 25);

		Label usernameLabel = new Label(userTabComposite, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameLabel.setBounds(50, 40, 80, 25);

		Label firstNameLabel = new Label(userTabComposite, SWT.NONE);
		firstNameLabel.setText("First Name: ");
		firstNameLabel.setBounds(50, 70, 80, 25);

		Label lastNameLabel = new Label(userTabComposite, SWT.NONE);
		lastNameLabel.setText("Last Name: ");
		lastNameLabel.setBounds(50, 100, 80, 25);

		Label passwordLabel = new Label(userTabComposite, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordLabel.setBounds(50, 130, 80, 25);
		
		Label emailLabel = new Label(userTabComposite, SWT.NONE);
		emailLabel.setText("Email: ");
		emailLabel.setBounds(50, 160, 80, 25);

		Text usernameField = new Text(userTabComposite, SWT.BORDER);
		usernameField.setText("");
		usernameField.setBounds(145, 40, 100, 20);

		Text firstNameField = new Text(userTabComposite, SWT.BORDER);
		firstNameField.setText("");
		firstNameField.setBounds(145, 70, 100, 20);

		Text lastNameField = new Text(userTabComposite, SWT.BORDER);
		lastNameField.setText("");
		lastNameField.setBounds(145, 100, 100, 20);

		Text passwordField = new Text(userTabComposite, SWT.BORDER | SWT.PASSWORD);
		passwordField.setText("");
		passwordField.setBounds(145, 130, 100, 20);
		
		Text emailField = new Text(userTabComposite, SWT.BORDER);
		emailField.setText("");
		emailField.setBounds(145, 160, 100, 20);

		/**
		 * The create new user button first verifies that all required fields are filled out, 
		 * then verifies that the username is available, 
		 * and if so it creates a new user.
		 * 
		 * If those checks are failed then a popup comes up with instructions for how to proceed.
		 * 
		 * A user file is also created for the new user here
		 */
		Button createUserButton = new Button(userTabComposite, SWT.NONE);
		createUserButton.setText("Create User");
		createUserButton.setBounds(50, 190, 85, 25);	
		createUserButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (usernameField.getText().isBlank() || firstNameField.getText().isBlank() || 
						lastNameField.getText().isBlank() || passwordField.getText().isBlank() || emailField.getText().isBlank()) {
					JOptionPane.showMessageDialog(null, "Please fill out all fields to continue");
				}
				String fileName = usernameField.getText() + "Playlists.json";
				createNewUserFile(fileName);
				//test whether username is available (other fields do not need to be unique)
				for (User user : userList) {
					if (usernameField.getText().equals(user.getUsername())) {
						JOptionPane.showMessageDialog(null, "This username is not available");
					}
				}
				User newUser = new User(firstNameField.getText(), lastNameField.getText(), 
						emailField.getText(), passwordField.getText(), usernameField.getText(), 0);
				currentUser = newUser;
				userList.add(newUser);
				userListIO.updateUserJsonFile();
			}
		});//end create new user button listener
		
		/**
		 * Sign out button: This removes the current user , reloads playlists, and re-prompts for sign in. 
		 * It does so by closing the program and re-calling main
		 */
		Button signoutButton = new Button(userTabComposite, SWT.NONE);
		signoutButton.setText("Sign Out");
		signoutButton.setBounds(135, 190, 85, 25);
		signoutButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dispose();
				Player.main(null);
			}
		});

		//Now playing tab controls:
		TabItem playerTab = new TabItem(tabFolder, SWT.NONE);
		playerTab.setText("Now Playing");		
		playerTab.setControl(composite);
		composite.setLayout(null);
		
		//label that acts as a header for the artist
		Label lblArtist = new Label(composite, SWT.NONE);
		lblArtist.setBounds(10, 10, 81, 25);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setText("Artist:");
		//label that shows the current artist playing
		Label lblartistplaying = new Label(composite, SWT.NONE);
		lblartistplaying.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lblartistplaying.setBounds(10, 38, 192, 25);
		lblartistplaying.setText(" ");
		//label that acts as a header for the album
		Label lblAlbum = new Label(composite, SWT.NONE);
		lblAlbum.setBounds(10, 69, 81, 25);
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setText("Album:");
		//label that shows the current album playing
		Label lblalbumplaying = new Label(composite, SWT.NONE);
		lblalbumplaying.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lblalbumplaying.setBounds(10, 100, 192, 25);
		lblalbumplaying.setText(" ");
		//label that acts as a header for the song
		Label lblSong = new Label(composite, SWT.NONE);
		lblSong.setBounds(10, 131, 81, 25);
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setText("Song:");
		//label that shows the current song playing
		Label lblsongplaying = new Label(composite, SWT.NONE);
		lblsongplaying.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lblsongplaying.setBounds(10, 162, 192, 25);
		lblsongplaying.setText(" ");
		//label that acts as a header for the genre
		Label lblGenre = new Label(composite, SWT.NONE);
		lblGenre.setText("Genre:");
		lblGenre.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblGenre.setBounds(10, 204, 81, 25);
		//label that shows the current genre playing
		Label lblgenreplaying = new Label(composite, SWT.NONE);
		lblgenreplaying.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lblgenreplaying.setText(" ");
		lblgenreplaying.setBounds(10, 235, 192, 25);
		//list of the queue
		List lstQueue = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		lstQueue.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lstQueue.setBounds(208, 38, 109, 201);

		// create the tree widget
		Tree tree = new Tree(composite, SWT.BORDER);
		tree.setBounds(332, 38, 181, 201);

	
		//scale used to track location within song
		Scale scale = new Scale(composite, SWT.NONE);
		scale.setBounds(35, 286, 478, 54);
		scale.setVisible(false);


		//button used to restart the current song
		Button btnRestart = new Button(composite, SWT.NONE);
		btnRestart.setBounds(174, 347, 66, 40);
		btnRestart.setText("Restart");
		//listener event for btnRestart that restarts the song by removing the browser then generating a new one
		btnRestart.addListener(SWT.Selection, event -> {
			Control[] controls = Player.this.getChildren();
			for (Control control : controls) {
				if (control instanceof Browser) {
					control.dispose();
				}//end if
			}//end for loop to dispose of browser
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(playQueue.peek().getUrl());
			timer = playQueue.peek().getDuration();
		});


		//button used to repeat the current song once it's finished playing
		Button btnRepeat = new Button(composite, SWT.NONE);
		btnRepeat.setBounds(102, 347, 66, 40);
		btnRepeat.setText("Repeat");
		//listener event for btnRepeat that adds the current song to the next index in the ArrayList
		btnRepeat.addListener(SWT.Selection, event -> {
			//adds current song to the next index on the ArrayList
			if (repeat = false) {
				repeat = true;
				btnRepeat.setText("UnRepeat");
			}//end if
			else {
				repeat = false; 
				btnRepeat.setText("Repeat");
			}//end else
			playQueue.push(playQueue.peek());
			updateQueueList(lstQueue);
		});//end btnRepeat listener event

		/**
		 * This listens to the tree and when a song is pressed, it will change the index(i) to the location of 
		 * the song in the songList or the json file to play that song
		 */
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Get the selected item in the tree
				TreeItem selectedItem = (TreeItem) event.item;
				// Get the Song object associated with the selected item
				Song selectedSong = (Song) selectedItem.getData();
				//looks for the index of the song you click and sets the current index as its number
				for(int j = 0; j < songList.size(); j++) {
					if(songList.get(j).equals(selectedSong)) {
						i = j;
					}//end if
				}//end for loop to look for song index
			}
		});//end of listener event for the tree
		//button used to pause the song that is currently playing
		Button btnPause = new Button(composite, SWT.NONE);
		btnPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnPause.setBounds(246, 347, 66, 40);
		btnPause.setText("Ⅱ");
		//listener event for btnPause that pauses or resumes a song depending on the paused boolean value
		btnPause.addListener(SWT.Selection, event -> {
			//generates a new browser if the song is already paused, resumes the song at the paused location
			if(paused == true) {
				Browser browser = new Browser(this, SWT.NONE);
				browser.setBounds(50, 50, 200, 200);
				browser.setUrl(playQueue.peek().getUrl() + "&start=" + resumeTime);
				timer = playQueue.peek().getDuration() - resumeTime;
				scale.setSelection(resumeTime);
				btnPause.setText("Ⅱ");
				paused = false;
			}//end if to resume song
			else { //takes the pause time and then disposes of the broswer
				paused = true;
				stopTime = timer;
				resumeTime = playQueue.peek().getDuration() - stopTime - 2; //subtracting an additional 2 seconds to account for loading times
				//remove the current browser
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for loop to dispose browser objects
				btnPause.setText("►");
				startTimer(display, scale, lstQueue, lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);//end else  for pausing the song
			}});//end selection listener event for btnPause

		//button used to skip the current song that's playing
		Button btnSkip = new Button(composite, SWT.NONE);
		btnSkip.setBounds(318, 347, 66, 40);
		btnSkip.setText("Skip");
		//button to like the currently playling song
		Button btnLike = new Button(composite, SWT.NONE);
		btnLike.setBounds(390, 347, 66, 40);
		btnLike.setText("Like");
		//button that adds selected song in treemap to queue
		Button btnAddToQueue = new Button(composite, SWT.NONE);
		btnAddToQueue.setBounds(390, 245, 124, 35);
		btnAddToQueue.setText("Add to Queue");
		//button to clear queue 
		Button btnClearQ = new Button(composite, SWT.NONE);
		btnClearQ.setBounds(273, 245, 111, 35);
		btnClearQ.setText("Clear Queue");
		
		Label lblUpNext = new Label(composite, SWT.NONE);
		lblUpNext.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblUpNext.setBounds(208, 10, 66, 25);
		lblUpNext.setText("Up Next:");
		
		Label lblAllSongs = new Label(composite, SWT.NONE);
		lblAllSongs.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAllSongs.setBounds(332, 10, 81, 25);
		lblAllSongs.setText("All Songs:"); 
		
		//button to move songs up the Queue
		Button btnQueueUp = new Button(composite, SWT.NONE);
		btnQueueUp.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		//listener event to move a song up the Queue
		btnQueueUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if statement to check if no song is selected
				if (lstQueue.getSelectionIndex()==-1) {
					return;
				}//end if
				//if statement to prevent the event from replacing the song currently playing with the one selected
				if(lstQueue.getSelectionIndex()!=0) {
				Song tempSong; //used to store song being moved
				int tempInt; //used to save selected song's location so song is still selected after move
				tempSong = playQueue.get(lstQueue.getSelectionIndex() + 1);
				tempInt = lstQueue.getSelectionIndex();
				playQueue.remove(lstQueue.getSelectionIndex()+1);
				playQueue.add(lstQueue.getSelectionIndex(), tempSong);
				updateQueueList(lstQueue);
				lstQueue.setSelection(tempInt - 1);
				}//end if
			}
		});//end listener event to move a song up the Queue
		btnQueueUp.setBounds(284, 12, 22, 25);
		btnQueueUp.setText("▲");
		//button to move songs down the Queue
		Button btnQueueDown = new Button(composite, SWT.NONE);
		//listener event to move a selected song down the Queue
		btnQueueDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if statement to check if no song is selected
				if (lstQueue.getSelectionIndex()==-1) {
					return;
				}//end if
				//if statement to prevent the event from moving a song that's already at the bottom of the Queue
				if(lstQueue.getSelectionIndex()+1!=playQueue.size()-1) {
				Song tempSong; //used to store song being moved
				int tempInt; //used to save selected song's location so song is still selected after move
				tempSong = playQueue.get(lstQueue.getSelectionIndex() + 1);
				tempInt = lstQueue.getSelectionIndex();
				playQueue.remove(lstQueue.getSelectionIndex()+1);
				playQueue.add(lstQueue.getSelectionIndex()+2, tempSong);
				updateQueueList(lstQueue);
				lstQueue.setSelection(tempInt + 1);
				}//end if
			}
		});//end event to move selected song down the Queue
		btnQueueDown.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		btnQueueDown.setBounds(306, 12, 22, 25);
		btnQueueDown.setText("▼");
		//button to remove a song from the Queue, only works on songs not currently playing
		Button btnRemove = new Button(composite, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if statement to check if no song is selected or Queuelist is empty
				if (lstQueue.getSelectionIndex()==-1 || playQueue.size() < 2) {
					return;
				}//end if
				playQueue.remove(lstQueue.getSelectionIndex()+1);
				updateQueueList(lstQueue);
			}
		});//end event to remove song from Queue
		btnRemove.setBounds(208, 245, 57, 35);
		btnRemove.setText("✖");
		
		//event that plays the current song at the point selected in the slider
		scale.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				//remove the current browser
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for loop to dispose browser objects
				Browser browser = new Browser(Player.this, SWT.NONE);
				browser.setBounds(50, 50, 200, 200);
				browser.setUrl(playQueue.peek().getUrl() + "&start=" + (scale.getSelection()-2));
				timer = playQueue.peek().getDuration() - scale.getSelection() + 2;
				scale.setSelection(scale.getSelection()-2);//subtracting 2 seconds to account for load time
				}//end if
		});
		//event for adding a song to the queue
		btnAddToQueue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				playQueue.add(songList.get(i));
				updateQueueList(lstQueue);
				//Starts playing automatically if the Queue is empty
				if(isPlaying==false) {
					UpdateLabels(lblsongplaying, lblalbumplaying,lblartistplaying,lblgenreplaying);
					//for loop to dispose of browser in case something is already playing
					Control[] controls = Player.this.getChildren();
					for (Control control : controls) {
						if (control instanceof Browser) {
							control.dispose();
						}//end if
					}//end for loop to dispose of browser
					Browser browser = new Browser(Player.this, SWT.NONE);
					browser.setBounds(50, 50, 1, 1);
					browser.setUrl(playQueue.peek().getUrl());
					timer = playQueue.peek().getDuration();
					scale.setMaximum(playQueue.peek().getDuration());
					scale.setVisible(true);
					startTimer(display, scale, lstQueue, lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);
					isPlaying= true;
				}//end if
			}
		});//end event to add single song to event
		//listener event for clearing a queue
		btnClearQ.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timer = 0;
				playQueue.clear();
				updateQueueList(lstQueue);
				lblsongplaying.setText("");
				lblalbumplaying.setText("");
				lblartistplaying.setText("");
				lblgenreplaying.setText("");
				scale.setSelection(0);
			}//end if
		});//end event to clear queue
		//listener event for btnSkip that sets the timer to 0 so the next song immediately loads in the timer method
		btnSkip.addListener(SWT.Selection, event -> {
			timer = 0;
			paused = false;
			btnPause.setText("Ⅱ");
			//if statement to check if Queue is empty
			if (playQueue.isEmpty()) {
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for loop to dispose of browser
			}//end if
		});//end listener event for btnSkip
		//listener for btnLike to add the current song to the liked songs playlist when clicked
		btnLike.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//As long as the current user is not guest, write the song to the users json file of playlists
				if(!currentUser.getUsername().equals("guest")) {
					currentUser.getUsersPlaylist().getPlaylistByName("Liked Songs").addSong(songList.get(i));
					currentUser.getUsersPlaylist().updateJsonFile(currentUser.getPlaylistFileName());
					
					//add the song to the playlist tree
					for (TreeItem treeItem : playlistList.getItems()) {
						if (treeItem.getText().equals("Liked Songs")) {
							TreeItem songItem = new TreeItem(treeItem, SWT.NONE);
							songItem.setText(songList.get(i).getName());
							songItem.setData(songList.get(i));
						}
					}
				//if the user is guest, display an error
				}else {
					JOptionPane.showMessageDialog(null, "To like songs and save playlists, "
							+ "please log in or create an account on the user management tab");
				}
				
			}
		});

		// load the songs from the JSON file
		songList = Songs.loadSongsFromJson("songsList.json");


		// create the hashmap to store songs by genre
		HashMap<String, ArrayList<Song>> songsByGenre = new HashMap<>();
		for (Song song : songList) {
			String genre = song.getGenre();
			if (!songsByGenre.containsKey(genre)) {
				songsByGenre.put(genre, new ArrayList<Song>());
			}//end if
			songsByGenre.get(genre).add(song);
		}//end for loop that adds genres to the hashmap

		// populate the tree with genres as top-level items
		for (String genre : songsByGenre.keySet()) {
			TreeItem genreItem = new TreeItem(tree, SWT.NONE);
			genreItem.setText(genre);
			// nested for loop add the songs for this genre as children of the genre item
			for (Song song : songsByGenre.get(genre)) {
				TreeItem songItem = new TreeItem(genreItem, SWT.NONE);
				songItem.setText(song.getName());
				songItem.setData(song);
			}//end nested for loop that adds songs to their respective genre
		}//end outer for loop that loops through the genres
		
		createContents(); 
		PlaylistCollections playlistCollections = new PlaylistCollections();

		//Playlist Builder Tab Controls:
		TabItem playlistBuilderTab = new TabItem(tabFolder, SWT.NONE);
		playlistBuilderTab.setText("Create Playlists");
		playlistBuilderTab.setControl(composite_2);

		List allSongs = new List(composite_2, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		allSongs.setBounds(10, 45, 200, 268);

		Text songSearchField = new Text(composite_2, SWT.BORDER);
		songSearchField.setText("");
		songSearchField.setBounds(65, 10, 110, 30);

		Label songSearchLabel = new Label(composite_2, SWT.NONE);
		songSearchLabel.setText("Seach: ");
		songSearchLabel.setBounds(10, 10, 110, 26);
		//for loop to add songs to list box
		for (Song song : songList) {	
			allSongs.add(song.getName());	    
		}//end for loop

		List songsToAdd = new List(composite_2, SWT.BORDER);
		songsToAdd.setBounds(360, 45, 200, 268);

		Button searchSong = new Button(composite_2, SWT.NONE);
		searchSong.setText(">");
		searchSong.setBounds(179, 9, 32, 32);

		Button rightArrow = new Button(composite_2, SWT.NONE);
		rightArrow.setText(">");
		rightArrow.setBounds(267, 125, 35, 35);

		Button leftArrow = new Button(composite_2, SWT.NONE);
		leftArrow.setText("<");
		leftArrow.setBounds(267, 170, 35, 35);

		Label playlistNameLabel = new Label(composite_2, SWT.NONE);
		playlistNameLabel.setText("Playlist Name: ");
		playlistNameLabel.setBounds(360, 10, 110, 26);
		//event that searches for a specific song in the create playlist tab
		searchSong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allSongs.removeAll();
				//for loop that searches for the song
				for (Song song : songList) {
					//if search field isn't empty
					if (!songSearchField.getText().toLowerCase().equals("")) {
						if (song.getName().toLowerCase().contains(songSearchField.getText().toLowerCase()) || 
								song.getArtist().toLowerCase().contains(songSearchField.getText().toLowerCase()) || 
								song.getGenre().toLowerCase().contains(songSearchField.getText().toLowerCase())) {
							allSongs.add(song.getName());
						}//end nested if
					}//end if
					else {
						allSongs.add(song.getName());
					}//end else
				}//end for loop
			}
		});//end event to search for specific song

		rightArrow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedItems = allSongs.getSelection();
				for (String item : selectedItems) {
					songsToAdd.add(item);
				}
			}
		});

		leftArrow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedItems = songsToAdd.getSelection();
				for (String item : selectedItems) {
					allSongs.add(item);
					songsToAdd.remove(item);
				}
			}
		});

		Text playlistNameField = new Text(composite_2, SWT.BORDER);
		playlistNameField.setText("");
		playlistNameField.setBounds(475, 10, 85, 30);
		//add listener to move song selected on left off of the list

		Button createPlaylistButton = new Button(composite_2, SWT.NONE);
		createPlaylistButton.setText("Create Playlist");
		createPlaylistButton.setBounds(225, 279, 120, 35);

		//Playlist tab controls:
		TabItem playlistTab = new TabItem(tabFolder, SWT.NONE);
		playlistTab.setText("Playlists");

		playlistTab.setControl(composite_1);

		playlistList.setBounds(182, 70, 223, 186);

		Label playlistListLabel = new Label(composite_1, SWT.NONE);
		playlistListLabel.setText("Playlists");
		playlistListLabel.setBounds(270, 25, 63, 30);

		Button playButton = new Button(composite_1, SWT.NONE);
		playButton.setText("Play Playlist");
		playButton.setBounds(243, 279, 105, 35);

		Button deleteButton = new Button(composite_1, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setBounds(123, 279, 105, 35);

		Button shuffleButton = new Button(composite_1, SWT.NONE);
		shuffleButton.setText("Shuffle");
		shuffleButton.setBounds(363, 279, 105, 35);

		/**
		 * This button iterates over the current user's collection of playlists and rewrites it to exclude the playlist the user selected to delete
		 * it then re-populates the playlistList with the new user playlists
		 */
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] currentSelection = playlistList.getSelection();
				if (currentSelection.length > 0) {
					TreeItem selectedPlaylist = currentSelection[0];
					String playlistName = selectedPlaylist.getText();
					java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson(currentUser.getPlaylistFileName());
					JSONObject newUserPlaylistsJson = new JSONObject();
					JSONArray playlistsJsonArray = new JSONArray();
					for (Playlist playlist : playlists) {
						if (playlist.getName().equalsIgnoreCase(playlistName)) {
							currentUser.getUsersPlaylist().removePlaylist(playlist, currentUser.getPlaylistFileName());
						} else {
							currentUser.getUsersPlaylist().updateJsonFile(currentUser.getPlaylistFileName());
						}
					}
					selectedPlaylist.dispose();
				}
			}
		});//end delete playlist button listener

		//creates play list and adds it to the list on playlist tab
		createPlaylistButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String playlistName = playlistNameField.getText();
				String[] songs = songsToAdd.getItems();
				Playlist newPlaylist = new Playlist(playlistName);
				boolean playlistExists = false;
				//if statement to make sure playlist isn't empty
				if (playlistName.isEmpty()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Invalid playlist name");
					messageBox.setMessage("Playlist name cannot be empty. Please enter a valid name.");
					messageBox.open();
				} else {
					// Check if playlist name already exists
					for (TreeItem item : playlistList.getItems()) {
						if (item.getText().equals(playlistName)) {
							playlistExists = true;
							break;
						}//end if
					}//end for loop to check if name is already used

					if (!playlistExists) {
						// add the new playlist to the playlist list on playlist tab
						TreeItem newPlaylistItem = new TreeItem(playlistList, SWT.NONE);
						newPlaylistItem.setText(playlistName);

						// add songs to the new playlist
						for (String songName : songs) {
							for (Song song : songList) {
								if (song.getName().equals(songName)) {
									newPlaylist.addSong(song);
									TreeItem songItem = new TreeItem(newPlaylistItem, SWT.NONE);
									songItem.setText(songName);
									songItem.setData(song);
									break;
								}///end if
							}//end nested for loop
						}//end outer for loop to add songs to new playlist

						// clear the songsToAdd list
						songsToAdd.removeAll();
						playlistNameField.setText("");

						// Add the new playlist to the playlistCollections for the current user
						//unless the current user is a guest account
						if (!currentUser.getUsername().equals("guest")) {
							currentUser.getUsersPlaylist().addPlaylist(newPlaylist, currentUser.getPlaylistFileName());
						}
					} else {
						MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Playlist already exists");
						messageBox.setMessage("A playlist with the name '" + playlistName + "' already exists. Please choose a different name.");
						messageBox.open();
					}//end nested else
				}//end outer else
			}
		});//end create playlist event


		//listener event for playButton that plays the playlist from the playlist tab
		playButton.addListener(SWT.Selection, event -> {
			TreeItem[] selectedItems = playlistList.getSelection();
			if (selectedItems.length > 0) {
				// Only play songs if a playlist is selected
				TreeItem playlistItem = selectedItems[0];
				for (TreeItem songItem : playlistItem.getItems()) {
					Song song = (Song) songItem.getData();
					playQueue.add(song);
					updateQueueList(lstQueue);
				}//end if
				//Starts playing automatically if the Queue is empty
				if(isPlaying==false) {
					UpdateLabels(lblsongplaying, lblalbumplaying,lblartistplaying,lblgenreplaying);
					//for loop to dispose of browser in case something is already playing
					Control[] controls = Player.this.getChildren();
					for (Control control : controls) {
						if (control instanceof Browser) {
							control.dispose();
						}//end if
					}//end for loop to dispose of browser
					Browser browser = new Browser(Player.this, SWT.NONE);
					browser.setBounds(50, 50, 1, 1);
					browser.setUrl(playQueue.peek().getUrl());
					timer = playQueue.peek().getDuration();
					scale.setMaximum(playQueue.peek().getDuration());
					scale.setVisible(true);
					startTimer(display, scale, lstQueue, lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);
					isPlaying= true;
				}//end if
			}
		});//end event to play playlist

		shuffleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Get the selected playlist
				TreeItem[] selection = playlistList.getSelection();
				if (selection.length == 0) {
					// No playlist has been selected, do nothing
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("No play list Selected.");
					messageBox.setMessage("Please select a play list to shuffle");
					messageBox.open();
					return;
				}//end if
				TreeItem selectedPlaylist = selection[0];

				// Get the child items (songs) of the selected playlist
				TreeItem[] songs = selectedPlaylist.getItems();

				// Create a list to hold the song objects
				ArrayList<Song> songList = new ArrayList<Song>();

				// Add each song object to the list
				for (TreeItem song : songs) {
					songList.add((Song) song.getData());
				}
				
				//clear the Queue
				playQueue.clear();

				// Shuffle the list
				Collections.shuffle(songList);
				
				//add each song to the queue
				playQueue.addAll(songList);
				updateQueueList(lstQueue);
				//Starts playing automatically if the Queue is empty
				if(isPlaying==false) {
					UpdateLabels(lblsongplaying, lblalbumplaying,lblartistplaying,lblgenreplaying);
					//for loop to dispose of browser in case something is already playing
					Control[] controls = Player.this.getChildren();
					for (Control control : controls) {
						if (control instanceof Browser) {
							control.dispose();
						}//end if
					}//end for loop to dispose of browser
					Browser browser = new Browser(Player.this, SWT.NONE);
					browser.setBounds(50, 50, 1, 1);
					browser.setUrl(playQueue.peek().getUrl());
					timer = playQueue.peek().getDuration();
					scale.setMaximum(playQueue.peek().getDuration());
					scale.setVisible(true);
					startTimer(display, scale, lstQueue, lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);
					isPlaying= true;
				}//end if

				// Clear the existing child items from the selected playlist
				selectedPlaylist.removeAll();

				// Add the shuffled songs back to the playlist as child items
				for (Song song : songList) {
					TreeItem songItem = new TreeItem(selectedPlaylist, SWT.NONE);
					songItem.setText(song.getName());
					songItem.setData(song);
				}

				// Update the tree widget
				tree.update();
			}
		});
	}

	/**
	 * event that loads from a json file the already existing playlists associated with the current user
	 */
	private void initialPlaylistLoad() {
		if (!(currentUser.getUsername().equals("guest"))) {
			java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson(currentUser.getPlaylistFileName());
			for (Playlist playlist : playlists) {
				//testLine
				currentUser.getUsersPlaylist().addPlaylist(playlist, null);
				TreeItem newPlaylistItem = new TreeItem(playlistList, 0);
				newPlaylistItem.setText(playlist.getName());
				
				java.util.List<Song> songs = playlist.getSongs();
				for (Song song : songs) {
					TreeItem songItem = new TreeItem(newPlaylistItem, SWT.NONE);
					songItem.setText(song.getName());
					songItem.setData(song);		
				}//end nested for loop	
			}//end outer for loop
		}//end if		
	}//end initialPlaylistLoad method

	public static void reloadPlaylists() {
		if (playlistList != null) {
			playlistList.clearAll(true);
		}
		java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson(currentUser.getPlaylistFileName());
		for (Playlist playlist : playlists) {
			TreeItem newPlaylistItem = new TreeItem(playlistList, SWT.NONE);
			newPlaylistItem.setText(playlist.getName());

			java.util.List<Song> songs = playlist.getSongs();
			for (Song song : songs) {
				TreeItem songItem = new TreeItem(newPlaylistItem, SWT.NONE);
				songItem.setText(song.getName());
				songItem.setData(song);		
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void createNewUserFile(String fileName) {
		JSONObject newUserJsonFile = new JSONObject();
		JSONArray playlistsJsonArray = new JSONArray();
		JSONObject currentPlaylistJson = new JSONObject();
		JSONArray emptyArray = new JSONArray();
		currentPlaylistJson.put("playlist name", "Liked Songs");
		currentPlaylistJson.put("songs", emptyArray);
		playlistsJsonArray.add(currentPlaylistJson);
		newUserJsonFile.put("playlists", playlistsJsonArray);
		try {
			Files.write(Paths.get(fileName), newUserJsonFile.toJSONString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
		
	
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Music Player");
		setSize(450, 300);

	}
	/**
	 * This method is a timer that decrements the timer variable by 1 every second, the code to make the progress bar will
	 * be going in here
	 * 
	 * It also loads the next song when the timer hits zero, right now there are only two set songs but it'll pull 
	 * whatever's next in the queue. It does this by removing the browser object and creating a new one
	 * 
	 * will add more code later to stop timer when btnpause is pressed and save the current time, when the paused song is
	 * resumed we will remove the video, then reload it at the time saved in stopTime and then do timer = timer - stopTime 
	 * before we call the startTimer method
	 * @param display
	 */
	
	public void startTimer(Display display, Scale scale, List list, Label name, Label album, Label artist, Label genre) {
		if(paused == false) {
			display.timerExec(1000, new Runnable() {
				public void run() {
					/*recursive loop that decrements the timer variable by 1 every second until the variable is
					 * no longer larger than 0
					 */
					if (timer > 0) {
						timer--;
						display.asyncExec(new Runnable() {
							public void run() {
							//if statement to make sure the progress bar stops when the song is paused
								if (paused == false) {
								scale.setSelection(playQueue.peek().getDuration() - timer);}//end if
							}
						});//end recursive loop
						display.timerExec(1000, this);
					} else {
						// Stop the timer
						display.timerExec(-1, this);
						playQueue.poll();
						// Remove the Browser widget so we can load the second one
						Control[] controls = Player.this.getChildren();
						for (Control control : controls) {
							if (control instanceof Browser) {
								control.dispose();
							}//end if
						}//end for
						if (playQueue.isEmpty()) { //if statement to check if current song is the last in the list
							// Remove the Browser widget if there is no queue
							Control[] controls1 = Player.this.getChildren();
							for (Control control : controls) {
								if (control instanceof Browser) {
									control.dispose();
								}//end if
							}//end for
							name.setText("");
							album.setText("");
							artist.setText("");
							genre.setText("");
							scale.setSelection(0);
							scale.setVisible(false);
							isPlaying = false;
							return;
						}//end if to check if at end of queue
						//creating the new browser
						Browser browser = new Browser(Player.this, SWT.NONE);
						browser.setBounds(50, 50, 1, 1);
						browser.setUrl(playQueue.peek().getUrl());
						updateQueueList(list);
						UpdateLabels(name, album, artist, genre);
						timer = playQueue.peek().getDuration();
						scale.setMaximum(playQueue.peek().getDuration());
						startTimer(display, scale, list, name, album, artist, genre);
					}//end else
				}
			});
			}//end if
		}//end StartTimer method
	
	public void UpdateLabels(Label song, Label album, Label artist, Label genre) {
		song.setText(playQueue.peek().getName());
		album.setText(playQueue.peek().getAlbum());
		artist.setText(playQueue.peek().getArtist());
		genre.setText(playQueue.peek().getGenre());
	}//end updateLables method
	/**
	 * method that updates the GUI to show queue list
	 * @param list
	 */
	public void updateQueueList(List list) {
		list.removeAll();
		for(int i=1; i<playQueue.size(); i++) {
			list.add(playQueue.get(i).getName());
		}//end for loop
	}//end updateQueueList method

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public static void getUserFromLogin(User user) {
		currentUser = user;
		currentUserPlaylist = currentUser.getUsersPlaylist();
		loginwindow.dispose();
	}
}
