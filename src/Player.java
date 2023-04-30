import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;

@SuppressWarnings("unused")
public class Player extends Shell {
	/*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
    times in the youtube player so the song doesn't cut off early */
	private int timer;  //this number will be replaced with the song lengths
	private int stopTime; //used for pause button
	private int resumeTime; //used for pausing and resuming
	private boolean paused = false; //used to stop timer when song is paused
	private boolean repeat = false;//used tell if song has been repeated
	private int i = 0; //used to track location in Arraylist
	private java.util.List<Song> songList = new ArrayList<Song>();// this is the arraylist for the song list-brian
	private Song song;
	//TODO: private User currentUser; This will be set by the login button on the User Management tab
	//TODO: private PlaylistCollections currentUserPlaylist; This may or may not be needed, tbd

	public Player(String artist, String album, String name, String url, int yearReleased, String genre, int duration) {
		this.song = new Song(artist, album, name, url, yearReleased, genre, duration);
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Player shell = new Player(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
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

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(0, 0, 576, 457);

		//Now playing tab controls:
		TabItem playerTab = new TabItem(tabFolder, SWT.NONE);
		playerTab.setText("Now Playing");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		playerTab.setControl(composite);
		GridLayout gl_composite = new GridLayout(5, false);
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);

		// create the tree widget
		Tree tree = new Tree(composite, SWT.BORDER);
		tree.setBounds(285, 20, 150, 200);

		Label lblArtist = new Label(composite, SWT.NONE);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setBounds(10, 10, 81, 25);
		lblArtist.setText("Artist:");

		Label lblartistplaying = new Label(composite, SWT.NONE);
		lblartistplaying.setBounds(10, 38, 332, 25);
		lblartistplaying.setText("When song plays artist name goes here");

		Label lblAlbum = new Label(composite, SWT.NONE);
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setBounds(10, 69, 81, 25);
		lblAlbum.setText("Album:");

		Label lblalbumplaying = new Label(composite, SWT.NONE);
		lblalbumplaying.setBounds(10, 100, 332, 25);
		lblalbumplaying.setText("When song plays album name goes here");

		Label lblSong = new Label(composite, SWT.NONE);
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setBounds(10, 131, 81, 25);
		lblSong.setText("Song:");

		Label lblsongplaying = new Label(composite, SWT.NONE);
		lblsongplaying.setBounds(10, 162, 332, 25);
		lblsongplaying.setText("When song plays song name goes here");

		//ProgressBar used to track location within song
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 5, 1));
		progressBar.setMinimum(0);
		
		//button used to restart the current song
		Button btnRestart = new Button(composite, SWT.NONE);
		btnRestart.setBounds(168, 279, 105, 35);
		btnRestart.setText("Restart");
		btnRestart.addListener(SWT.Selection, event -> {
			Control[] controls = Player.this.getChildren();
			for (Control control : controls) {
				if (control instanceof Browser) {
					control.dispose();
				}//end if
			}//end for loop
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(songList.get(i).getUrl());
			timer = songList.get(i).getDuration();

		});//end btnRestart event
		
		//button used to start playing the playlist
		Button btnPlay = new Button(composite, SWT.NONE);
		btnPlay.setText("Play");
		btnPlay.addListener(SWT.Selection, event -> {
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(songList.get(i).getUrl());
			timer = songList.get(i).getDuration();
			progressBar.setMaximum(songList.get(i).getDuration());
			startTimer(display, progressBar);
			UpdateLabels(lblsongplaying, lblalbumplaying, lblartistplaying);
		});//end btnPlay event
		
		//button used to repeat the current song once it's finished playing
		Button btnRepeat = new Button(composite, SWT.NONE);
		btnRepeat.setBounds(36, 279, 105, 35);
		btnRepeat.setText("Repeat");
		btnRepeat.addListener(SWT.Selection, event -> {
			//adds current song to the next index on the Arraylist
			if (repeat = false) {
				repeat = true;
				btnRepeat.setText("UnRepeat");
			}
			else {
				repeat = false; 
				btnRepeat.setText("Repeat");
			}
			songList.add(i+1, songList.get(i));
		});//end btnRepeat event

		/*Note from Nick C - I don't understand the purpose of this event if the person who wrote it could explain
    	why it's using btnRepeat that would be wonderful */
		btnRepeat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Get the selected item in the tree
				TreeItem selectedItem = tree.getSelection()[0];

				// Get the Song object associated with the selected item
				Song selectedSong = (Song) selectedItem.getData();

				// Update the currentSong URL if a song is selected
				if (selectedSong != null) {
					Control[] controls = Player.this.getChildren();
					if (selectedSong != null) {
						for (Control control : controls) {
							if (control instanceof Browser) {
								((Browser) control).setUrl(selectedSong.getUrl());
							}//end if
						}//end for loop
						timer = selectedSong.getDuration();
						startTimer(display,progressBar);
						System.out.println("Playing " + selectedSong.getUrl());
					}
				}
			}
		});


		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Get the selected item in the tree
				TreeItem selectedItem = (TreeItem) event.item;

				// Get the Song object associated with the selected item
				Song selectedSong = (Song) selectedItem.getData();
				songList.add(selectedSong);

				// Update the currentSong URL if a song is selected
				Control[] controls = Player.this.getChildren();
				if (selectedSong != null) {
					for (Control control : controls) {
						if (control instanceof Browser) {
							control.dispose();
						}//end if
					}//end for loop
					Browser browser = new Browser(Player.this, SWT.NONE);
					browser.setBounds(50, 50, 1, 1);
					browser.setUrl(songList.get(i).getUrl());
					timer = selectedSong.getDuration();
					startTimer(display,progressBar);
					lblartistplaying.setText(selectedSong.getArtist());
					lblalbumplaying.setText(selectedSong.getAlbum());
					lblsongplaying.setText(selectedSong.getName());

				}
			}
		});

		Button btnPause = new Button(composite, SWT.NONE);
		btnPause.setBounds(299, 279, 105, 35);
		btnPause.setText("Pause");
		btnPause.addListener(SWT.Selection, event -> {
			/* pauses the video */
			if(paused == true) {
				Browser browser = new Browser(this, SWT.NONE);
				browser.setBounds(50, 50, 200, 200);
				browser.setUrl(songList.get(i).getUrl() + "&start=" + resumeTime);
				btnPause.setText("Pause");
				paused = false;
			}//end if
			else {
				paused = true;
				stopTime = timer;
				resumeTime = songList.get(i).getDuration() - stopTime;
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for loop
				btnPause.setText("Resume");
				startTimer(display,progressBar);}//end else
		});


		Button btnSkip = new Button(composite, SWT.NONE);
		btnSkip.setBounds(429, 279, 105, 35);
		btnSkip.setText("Skip");
		btnSkip.addListener(SWT.Selection, event -> {
			/* skips the current song */
			timer = 0;
			if (i == songList.size()-1) {
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for
			}//end if
		});

		// load the songs from the JSON file
		songList = Songs.loadSongsFromJson("songsList.json");
		lblartistplaying.setText(songList.get(i).getArtist());
		lblalbumplaying.setText(songList.get(i).getAlbum());
		lblsongplaying.setText(songList.get(i).getAlbum());

		// create the hashmap to store songs by genre
		HashMap<String, ArrayList<Song>> songsByGenre = new HashMap<>();

		for (Song song : songList) {

			String genre = song.getGenre();
			if (!songsByGenre.containsKey(genre)) {
				songsByGenre.put(genre, new ArrayList<Song>());
			}
			songsByGenre.get(genre).add(song);
		}

		// populate the tree with genres as top-level items
		for (String genre : songsByGenre.keySet()) {
			TreeItem genreItem = new TreeItem(tree, SWT.NONE);
			genreItem.setText(genre);

			// add the songs for this genre as children of the genre item
			for (Song song : songsByGenre.get(genre)) {
				TreeItem songItem = new TreeItem(genreItem, SWT.NONE);
				songItem.setText(song.getName());
				songItem.setData(song);
			}
		}
		createContents(); 
		PlaylistCollections playlistCollections = new PlaylistCollections();
		//add listener to play the selected playlist from the list


		//User Management Controls:
		TabItem userManagementTab = new TabItem(tabFolder, SWT.NONE);
		userManagementTab.setText("User Management");

		Composite userTabComposite = new Composite(tabFolder, SWT.NONE);
		userManagementTab.setControl(userTabComposite);

		Label currentUserLabel = new Label(userTabComposite, SWT.NONE);
		currentUserLabel.setText("Current User: ");//Regularly Updated Label Text
		currentUserLabel.setBounds(50, 10, 150, 25);

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

		Text usernameField = new Text(userTabComposite, SWT.BORDER);
		usernameField.setText("");
		usernameField.setBounds(145, 40, 100, 20);

		Text firstNameField = new Text(userTabComposite, SWT.BORDER);
		firstNameField.setText("");
		firstNameField.setBounds(145, 70, 100, 20);

		Text lastNameField = new Text(userTabComposite, SWT.BORDER);
		lastNameField.setText("");
		lastNameField.setBounds(145, 100, 100, 20);

		Text passwordField = new Text(userTabComposite, SWT.BORDER);
		passwordField.setText("");
		passwordField.setBounds(145, 130, 100, 20);

		Button loginButton = new Button(userTabComposite, SWT.NONE);
		loginButton.setText("Login");
		loginButton.setBounds(50, 170, 80, 25);

		Button createUserButton = new Button(userTabComposite, SWT.NONE);
		createUserButton.setText("Create User");
		createUserButton.setBounds(160, 170, 85, 25);	
		createUserButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String fileName = usernameField.getText() + "Playlists.json";
				createNewUserFile(fileName);
				//TODO: add constructor for new user
			}
		});

		//Playlist Builder Tab Controls:
		TabItem playlistBuilderTab = new TabItem(tabFolder, SWT.NONE);
		playlistBuilderTab.setText("Create Playlists");

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		playlistBuilderTab.setControl(composite_2);

		List allSongs = new List(composite_2, SWT.BORDER | SWT.V_SCROLL);
		allSongs.setBounds(10, 25, 150, 268);//spacing TBD
		for (Song song : songList) {
			allSongs.add(song.getName());
		}

		List songsToAdd = new List(composite_2, SWT.BORDER);
		songsToAdd.setBounds(410, 25, 150, 268);//spacing TBD

		Button rightArrow = new Button(composite_2, SWT.NONE);
		rightArrow.setText(">");
		rightArrow.setBounds(280, 95, 35, 35);

		Button leftArrow = new Button(composite_2, SWT.NONE);
		leftArrow.setText("<");
		leftArrow.setBounds(280, 140, 35, 35);

		Label playlistNameLabel = new Label(composite_2, SWT.NONE);
		playlistNameLabel.setText("Playlist Name: ");
		playlistNameLabel.setBounds(195, 10, 80, 25);

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
					songsToAdd.remove(item);
				}
			}
		});

		Text playlistNameField = new Text(composite_2, SWT.BORDER);
		playlistNameField.setText("");
		playlistNameField.setBounds(280, 10, 100, 20);

		Button createPlaylistButton = new Button(composite_2, SWT.NONE);
		createPlaylistButton.setText("Create Playlist");
		createPlaylistButton.setBounds(240, 279, 105, 35);

		//Playlist tab controls:
		TabItem playlistTab = new TabItem(tabFolder, SWT.NONE);
		playlistTab.setText("Playlists");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		playlistTab.setControl(composite_1);

		List playlistList = new List(composite_1, SWT.BORDER);
		playlistList.setBounds(220, 70, 150, 150);

		Label playlistListLabel = new Label(composite_1, SWT.NONE);
		playlistListLabel.setText("Playlists");
		playlistListLabel.setBounds(270, 25, 150, 30);
		
		//TODO make this button work
		Button selectButton = new Button(composite_1, SWT.NONE);
		selectButton.setText("Play");
		selectButton.setBounds(300, 279, 105, 35);
		
		Button deleteButton = new Button(composite_1, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setBounds(180, 279, 105, 35);
		
		/**
		 * This button iterates over the current user's collection of playlists and rewrites it to exclude the playlist the user selected to delete
		 * it then repopulates the playlistList with the new user playlists
		 */
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				String currentSelection = playlistList.getSelection()[0];
				//TODO: get rid of this first line below, replace it with an iteration over the currentUserPlaylist
				//TODO: probably add a line for file output filename creation user.getUsername + "playlist.json" or something
				java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson("UserPlaylistTest.json");
				JSONObject newUserPlaylistsJson = new JSONObject();
				JSONArray playlistsJsonArray = new JSONArray();
				for (Playlist playlist : playlists) {
					if (playlist.getName().equalsIgnoreCase(currentSelection)) {
						continue;
					} else {
						JSONObject currentPlaylistJson = new JSONObject();
						currentPlaylistJson.put("playlist name", playlist.getName());
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
					//sampleObject.put("playlists", *need to make the playlist first*);
					//playlistList.add(playlist.getName());THIS IS OLD
				}				
				newUserPlaylistsJson.put("playlists", playlistsJsonArray);
				try {
					Files.write(Paths.get("UserPlaylistTest.json"), newUserPlaylistsJson.toJSONString().getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				playlistList.removeAll();
				java.util.List<Playlist> playlistsUpdatedAfterDeletion = PlaylistCollections.loadPlaylistsFromJson("UserPlaylistTest.json");
				for (Playlist playlist : playlistsUpdatedAfterDeletion) {
					playlistList.add(playlist.getName());
				}
			}
		});
		
		//Buttons:
		//creates playlist and adds it to the list on playlist tab
		createPlaylistButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String playlistName = playlistNameField.getText();
				String[] songs = songsToAdd.getItems();
				Playlist newPlaylist = new Playlist(playlistName);
				boolean playlistExists = false;

				if (playlistName.isEmpty()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Invalid playlist name");
					messageBox.setMessage("Playlist name cannot be empty. Please enter a valid name.");
					messageBox.open();
				} else {
					// Check if playlist name already exists
					for (int i = 0; i < playlistList.getItemCount(); i++) {
						if (playlistList.getItem(i).equals(playlistName)) {
							playlistExists = true;
							break;
						}
					}

					if (!playlistExists) {
						// add the new playlist to the playlist list on tab 2
						playlistList.add(playlistName); 
						// add songs to the new playlist
						for (String songName : songs) {
							for (Song song : songList) {
								if (song.getName().equals(songName)) {
									newPlaylist.addSong(song);
									break;
								}
							}
						}
						// clear the songsToAdd list
						songsToAdd.removeAll();
						playlistNameField.setText("");

						// Add the new playlist to the playlistCollections
						playlistCollections.addPlaylist(newPlaylist);

						// Debug output
						System.out.println("Playlist '" + newPlaylist.getName() + "' added with songs:");
						for (Song song : newPlaylist.getSongs()) {
							System.out.println(song.getName());
						}
					} else {
						MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Playlist already exists");
						messageBox.setMessage("A playlist with the name '" + playlistName + "' already exists. Please choose a different name.");
						messageBox.open();
					}
				}
			}
		});


		//User Management Tab Login Button Listener:
		//TODO: replace the listbox with a tree with the playlist names as headers on the Playlist tab (noted here because its
		//contents will respond to actions taken on the user management tab)
		//TODO: update the below per block comment
		loginButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentUserLabel.setText("Current User: " + usernameField.getText());
				//TODO: set currentUserPlaylist to the PlaylistCollections that is a field for the User object
				reloadPlaylists();
			}

			public void reloadPlaylists() {
				playlistList.removeAll();
				/**
				 * THIS WILL HAVE TO CHANGE TO PULL A NEW FILENAME FOR EACH NEW USER -- BUT THE TEST WORKS!
				 */
				java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson("UserPlaylistTest.json");
				for (Playlist playlist : playlists) {
					playlistList.add(playlist.getName());
				}
			}
		});

	}

	protected void createNewUserFile(String fileName) {
		File newUserFile = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(newUserFile, false);
			fileWriter.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
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
	public void startTimer(Display display, ProgressBar progress) {
		if(paused == false) {
			display.timerExec(1000, new Runnable() {
				public void run() {
					if (timer > 0) {
						timer--;
						display.asyncExec(new Runnable() {
							public void run() {
								progress.setSelection(songList.get(i).getDuration() - timer);
							}
						});
						display.timerExec(1000, this);
					} else {
						// Stop the timer
						display.timerExec(-1, this);
						if (repeat = false) {
							i++;}//end if
						// Remove the Browser widget so we can load the second one
						Control[] controls = Player.this.getChildren();
						for (Control control : controls) {
							if (control instanceof Browser) {
								control.dispose();
							}//end if
						}//end for
						if (i == songList.size() - 1) { //if statment to check if current song is the last in the list
							return;
						}//end if
						//creating the new browser
						Browser browser = new Browser(Player.this, SWT.NONE);
						browser.setBounds(50, 50, 1, 1);
						browser.setUrl(songList.get(i).getUrl());
						// Set the timer variable to 36 for testing purposes will be changed later
						timer = songList.get(i).getDuration();
						startTimer(display, progress);
					}//end else
				}
			});
		}//end if
	}//end StartTimer method

	public void UpdateLabels(Label song, Label album, Label artist) {
		song.setText(songList.get(i).getName());
		album.setText(songList.get(i).getAlbum());
		artist.setText(songList.get(i).getArtist());
	}//end updatelables method


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
